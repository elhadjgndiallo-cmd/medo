package com.medo.api.pos.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "caisses")
public class Caisse {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "reference", length = 100)
    private String reference;

    @Column(name = "actif", nullable = false)
    private Boolean actif = true;

    @OneToMany(mappedBy = "caisse", fetch = FetchType.LAZY)
    private List<SessionCaisse> sessions = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Caisse() {}

    public UUID getId()                          { return id; }
    public void setId(UUID v)                    { this.id = v; }
    public String getNom()                       { return nom; }
    public void setNom(String v)                 { this.nom = v; }
    public String getReference()                 { return reference; }
    public void setReference(String v)           { this.reference = v; }
    public Boolean getActif()                    { return actif; }
    public void setActif(Boolean v)              { this.actif = v; }
    public List<SessionCaisse> getSessions()     { return sessions; }
    public void setSessions(List<SessionCaisse> v){ this.sessions = v; }
    public LocalDateTime getCreatedAt()          { return createdAt; }
    public void setCreatedAt(LocalDateTime v)    { this.createdAt = v; }

    public SessionCaisse getSessionActive() {
        return sessions.stream()
            .filter(s -> SessionCaisse.StatutSession.OUVERTE.equals(s.getStatut()))
            .findFirst().orElse(null);
    }

    public boolean isDisponible() {
        return Boolean.TRUE.equals(actif) && getSessionActive() == null;
    }
}
