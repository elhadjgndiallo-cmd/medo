package com.medo.api.auth.controllers;

import com.medo.api.auth.dto.AuthDtos.*;
import com.medo.api.auth.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentification")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Connexion utilisateur pharmacie")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renouvellement token")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest req) {
        return ResponseEntity.ok(authService.refresh(req));
    }

    @PostMapping("/logout")
    @Operation(summary = "Déconnexion")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest req) {
        authService.logout(req);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/mot-de-passe/reset")
    @Operation(summary = "Réinitialisation mot de passe")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest req) {
        authService.requestPasswordReset(req);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/inscription")
    @Operation(summary = "Demande d'inscription pharmacie")
    public ResponseEntity<InscriptionResponse> inscrire(@Valid @RequestBody InscriptionRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.demanderInscription(req));
    }

    @PostMapping("/mobile/register")
    @Operation(summary = "Inscription client mobile")
    public ResponseEntity<TokenResponse> mobileRegister(@Valid @RequestBody MobileRegisterRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.mobileRegister(req));
    }

    @PostMapping("/mobile/login")
    @Operation(summary = "Connexion client mobile")
    public ResponseEntity<TokenResponse> mobileLogin(@Valid @RequestBody MobileLoginRequest req) {
        return ResponseEntity.ok(authService.mobileLogin(req));
    }
}
