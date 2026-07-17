package com.medo.api.auth.services;

import com.medo.api.auth.dao.UtilisateurRepository;
import com.medo.api.auth.dto.AuthDtos.*;
import com.medo.api.auth.entity.Role;
import com.medo.api.auth.entity.Utilisateur;
import com.medo.api.auth.entity.Utilisateur.TypeUser;
import com.medo.api.common.dao.DemandeInscriptionRepository;
import com.medo.api.common.entity.DemandeInscription;
import com.medo.api.common.entity.Tenant;
import com.medo.api.exception.GlobalExceptionHandler.*;
import com.medo.api.security.JwtTokenProvider;
import com.medo.api.tenant.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final String BLACKLIST_PREFIX = "blacklist::";
    private static final long   ACCESS_EXPIRY_SEC = 1800L;

    @Autowired private UtilisateurRepository utilisateurRepository;
    @Autowired private DemandeInscriptionRepository demandeRepository;
    @Autowired private JwtTokenProvider jwtTokenProvider;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private RedisTemplate<String, String> stringRedisTemplate;

    // ── Login ──
    @Transactional
    public TokenResponse login(LoginRequest req) {
        Utilisateur user = utilisateurRepository.findByEmailWithRoles(req.getEmail())
            .orElseThrow(() -> new AuthenticationException("Email ou mot de passe incorrect"));

        if (!Boolean.TRUE.equals(user.getActif()))
            throw new AuthenticationException("Compte désactivé");

        if (!passwordEncoder.matches(req.getMotDePasse(), user.getMotDePasse()))
            throw new AuthenticationException("Email ou mot de passe incorrect");

        utilisateurRepository.updateDernierConnexion(user.getId(), LocalDateTime.now());

        String tenantId = TenantContext.getCurrentTenant();
        List<String> roles = extractRoles(user);

        String at = jwtTokenProvider.generateAccessToken(
            user.getId().toString(), tenantId, user.getTypeUtilisateur().name(), roles);
        String rt = jwtTokenProvider.generateRefreshToken(user.getId().toString(), tenantId);

        log.info("Login OK : {} (tenant:{})", req.getEmail(), tenantId);
        return new TokenResponse(at, rt, ACCESS_EXPIRY_SEC,
            user.getTypeUtilisateur().name(), tenantId, buildUserInfo(user));
    }

    // ── Refresh ──
    @Transactional
    public TokenResponse refresh(RefreshTokenRequest req) {
        if (!jwtTokenProvider.validateToken(req.getRefreshToken()))
            throw new AuthenticationException("Refresh token invalide ou expiré");

        String jti = jwtTokenProvider.getJtiFromToken(req.getRefreshToken());
        if (isBlacklisted(jti))
            throw new AuthenticationException("Refresh token révoqué. Reconnectez-vous.");

        String userId   = jwtTokenProvider.getUserIdFromToken(req.getRefreshToken());
        String tenantId = jwtTokenProvider.getTenantIdFromToken(req.getRefreshToken());

        Utilisateur user = utilisateurRepository.findById(UUID.fromString(userId))
            .orElseThrow(() -> new AuthenticationException("Utilisateur introuvable"));

        if (!Boolean.TRUE.equals(user.getActif()))
            throw new AuthenticationException("Compte désactivé");

        blacklist(jti, jwtTokenProvider.getExpirationFromToken(req.getRefreshToken()));

        List<String> roles = extractRoles(user);
        String at = jwtTokenProvider.generateAccessToken(
            userId, tenantId, user.getTypeUtilisateur().name(), roles);
        String rt = jwtTokenProvider.generateRefreshToken(userId, tenantId);

        return new TokenResponse(at, rt, ACCESS_EXPIRY_SEC,
            user.getTypeUtilisateur().name(), tenantId, buildUserInfo(user));
    }

    // ── Logout ──
    public void logout(LogoutRequest req) {
        if (jwtTokenProvider.validateToken(req.getRefreshToken())) {
            String jti = jwtTokenProvider.getJtiFromToken(req.getRefreshToken());
            blacklist(jti, jwtTokenProvider.getExpirationFromToken(req.getRefreshToken()));
        }
    }

    public void requestPasswordReset(ResetPasswordRequest req) {
        log.info("Reset mdp demandé : {}", req.getEmail());
    }

    // ── Inscription pharmacie ──
    @Transactional
    public InscriptionResponse demanderInscription(InscriptionRequest req) {
        if (demandeRepository.existsByEmailContact(req.getEmailContact()))
            throw new DuplicateResourceException("Email déjà utilisé : " + req.getEmailContact());
        if (demandeRepository.existsBySousDomaineSouhaite(req.getSousDomaineSouhaite()))
            throw new DuplicateResourceException("Sous-domaine déjà pris : " + req.getSousDomaineSouhaite());

        Tenant.PlanAbonnement plan = "PRO".equalsIgnoreCase(req.getPlanDemande())
            ? Tenant.PlanAbonnement.PRO : Tenant.PlanAbonnement.GRATUIT;

        DemandeInscription demande = new DemandeInscription();
        demande.setNomPharmacie(req.getNomPharmacie());
        demande.setEmailContact(req.getEmailContact());
        demande.setMotDePasseHash(passwordEncoder.encode(req.getMotDePasse()));
        demande.setSousDomaineSouhaite(req.getSousDomaineSouhaite().toLowerCase().trim());
        demande.setAdresse(req.getAdresse());
        demande.setTelephone(req.getTelephone());
        demande.setPlanDemande(plan);

        DemandeInscription saved = demandeRepository.save(demande);
        log.info("Demande inscription : {} ({})", req.getNomPharmacie(), saved.getId());
        return new InscriptionResponse(saved.getId(),
            "Demande reçue. Vous serez notifié après validation.", "EN_ATTENTE");
    }

    // ── Mobile register ──
    @Transactional
    public TokenResponse mobileRegister(MobileRegisterRequest req) {
        if (utilisateurRepository.existsByEmail(req.getEmail()))
            throw new DuplicateResourceException("Email déjà utilisé");

        Utilisateur u = new Utilisateur();
        u.setNom(req.getNom()); u.setPrenom(req.getPrenom());
        u.setEmail(req.getEmail());
        u.setMotDePasse(passwordEncoder.encode(req.getMotDePasse()));
        u.setTelephone(req.getTelephone());
        u.setTypeUtilisateur(TypeUser.CLIENT_MOBILE);
        u.setActif(true);

        Utilisateur saved = utilisateurRepository.save(u);
        String at = jwtTokenProvider.generateAccessToken(
            saved.getId().toString(), "public", TypeUser.CLIENT_MOBILE.name(), List.of());
        String rt = jwtTokenProvider.generateRefreshToken(saved.getId().toString(), "public");
        return new TokenResponse(at, rt, ACCESS_EXPIRY_SEC,
            TypeUser.CLIENT_MOBILE.name(), "public", buildUserInfo(saved));
    }

    // ── Mobile login ──
    @Transactional
    public TokenResponse mobileLogin(MobileLoginRequest req) {
        Utilisateur u = utilisateurRepository.findByEmailAndActifTrue(req.getEmail())
            .filter(x -> TypeUser.CLIENT_MOBILE.equals(x.getTypeUtilisateur()))
            .orElseThrow(() -> new AuthenticationException("Email ou mot de passe incorrect"));

        if (!passwordEncoder.matches(req.getMotDePasse(), u.getMotDePasse()))
            throw new AuthenticationException("Email ou mot de passe incorrect");

        utilisateurRepository.updateDernierConnexion(u.getId(), LocalDateTime.now());
        String at = jwtTokenProvider.generateAccessToken(
            u.getId().toString(), "public", TypeUser.CLIENT_MOBILE.name(), List.of());
        String rt = jwtTokenProvider.generateRefreshToken(u.getId().toString(), "public");
        return new TokenResponse(at, rt, ACCESS_EXPIRY_SEC,
            TypeUser.CLIENT_MOBILE.name(), "public", buildUserInfo(u));
    }

    // ── Helpers ──
    private void blacklist(String jti, Date expiry) {
        long ttl = expiry.getTime() - System.currentTimeMillis();
        if (ttl > 0)
            stringRedisTemplate.opsForValue()
                .set(BLACKLIST_PREFIX + jti, "revoked", ttl, TimeUnit.MILLISECONDS);
    }

    private boolean isBlacklisted(String jti) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(BLACKLIST_PREFIX + jti));
    }

    private List<String> extractRoles(Utilisateur u) {
        return u.getRoles().stream().map(Role::getNom).collect(Collectors.toList());
    }

    private UserInfo buildUserInfo(Utilisateur u) {
        return new UserInfo(u.getId(), u.getNom(), u.getPrenom(),
            u.getEmail(), u.getTypeUtilisateur().name(), extractRoles(u));
    }
}
