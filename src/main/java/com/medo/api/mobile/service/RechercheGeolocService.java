package com.medo.api.mobile.services;

import com.medo.api.common.repository.TenantRepository;
import com.medo.api.common.entity.Tenant;
import com.medo.api.mobile.repository.FavoriRepository;
import com.medo.api.mobile.repository.HistoriqueRechercheRepository;
import com.medo.api.mobile.dto.MobileDtos.ResultatRechercheResponse;
import com.medo.api.mobile.entity.ClientMobile;
import com.medo.api.mobile.entity.HistoriqueRecherche;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class RechercheGeolocService {

    private static final Logger log = LoggerFactory.getLogger(RechercheGeolocService.class);
    private static final double RAYON_DEFAULT_KM = 5.0;

    @Autowired private TenantRepository              tenantRepository;
    @Autowired private FavoriRepository              favoriRepository;
    @Autowired private HistoriqueRechercheRepository historiqueRepository;
    @Autowired private JdbcTemplate                  jdbcTemplate;

    @Cacheable(value = "geo-search",
               key = "#terme + '::' + #latitude + '::' + #longitude + '::' + #rayonKm")
    @Transactional(readOnly = true)
    public List<ResultatRechercheResponse> rechercherMedicament(
            String terme, Double latitude, Double longitude,
            Double rayonKm, UUID clientId) {

        double rayon = (rayonKm != null) ? rayonKm : RAYON_DEFAULT_KM;
        log.info("Recherche '{}' ({},{}) rayon={}km", terme, latitude, longitude, rayon);

        List<Tenant> tenants = tenantRepository.findAllByStatut(Tenant.StatutTenant.ACTIF);
        List<ResultatRechercheResponse> resultats = new ArrayList<>();

        for (Tenant tenant : tenants) {
            try {
                ResultatRechercheResponse r =
                    interrogerTenant(tenant, terme, latitude, longitude, rayon, clientId);
                if (r != null) resultats.add(r);
            } catch (Exception e) {
                log.warn("Erreur tenant {} : {}", tenant.getSchemaName(), e.getMessage());
            }
        }

        resultats.sort(Comparator.comparingDouble(ResultatRechercheResponse::getDistanceKm));
        log.info("Résultats '{}' : {}", terme, resultats.size());
        return resultats;
    }

    @Transactional
    public void enregistrerHistorique(ClientMobile client, String terme,
                                       String categorie, int nbResultats,
                                       Double lat, Double lng) {
        HistoriqueRecherche h = new HistoriqueRecherche();
        h.setClient(client); h.setTermeProduit(terme);
        h.setCategorie(categorie); h.setNombreResultats(nbResultats);
        h.setLatitudeClient(lat); h.setLongitudeClient(lng);
        historiqueRepository.save(h);
    }

    private ResultatRechercheResponse interrogerTenant(
            Tenant tenant, String terme, double lat, double lng,
            double rayonKm, UUID clientId) {

        String sc   = tenant.getSchemaName();
        String like = "%" + terme + "%";

        String sql = String.format("""
            SELECT ph.nom, ph.adresse, ph.ville, ph.telephone, ph.horaires,
                   ph.latitude, ph.longitude,
                   CASE WHEN EXISTS (
                       SELECT 1 FROM %s.disponibilite_produits dp
                       JOIN %s.variantes_produit vp ON vp.id = dp.variante_id
                       JOIN %s.produits p ON p.id = vp.produit_id
                       WHERE (LOWER(p.nom) LIKE LOWER(?) OR LOWER(p.dci) LIKE LOWER(?))
                       AND dp.disponible = TRUE
                   ) THEN TRUE ELSE FALSE END AS disponible,
                   (6371 * ACOS(
                       COS(RADIANS(?)) * COS(RADIANS(ph.latitude)) *
                       COS(RADIANS(ph.longitude) - RADIANS(?)) +
                       SIN(RADIANS(?)) * SIN(RADIANS(ph.latitude))
                   )) AS distance_km
            FROM %s.pharmacie ph
            WHERE ph.latitude IS NOT NULL AND ph.longitude IS NOT NULL
            HAVING distance_km <= ?
            LIMIT 1
            """, sc, sc, sc, sc);

        try {
            return jdbcTemplate.queryForObject(sql,
                (rs, i) -> {
                    boolean estFavori = clientId != null &&
                        favoriRepository.existsByClientIdAndPharmacieTenantId(
                            clientId, tenant.getId());
                    ResultatRechercheResponse r = new ResultatRechercheResponse();
                    r.setPharmacieTenanId(tenant.getId());
                    r.setPharmacieNom(rs.getString("nom"));
                    r.setPharmacieAdresse(rs.getString("adresse"));
                    r.setPharmacieVille(rs.getString("ville"));
                    r.setPharmacieTeléphone(rs.getString("telephone"));
                    r.setHoraires(rs.getString("horaires"));
                    r.setDistanceKm(rs.getDouble("distance_km"));
                    r.setDisponible(rs.getBoolean("disponible"));
                    r.setEstFavori(estFavori);
                    r.setEstOuverte(true);
                    return r;
                },
                like, like, lat, lng, lat, rayonKm);
        } catch (Exception e) {
            return null;
        }
    }

    public static double calculerDistance(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                 + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                 * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return 6371.0 * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
