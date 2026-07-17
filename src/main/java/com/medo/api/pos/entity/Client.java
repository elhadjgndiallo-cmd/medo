package com.medo.api.pos.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "clients")
public class Client {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nom", nullable = false, length = 200)
    private String nom;

    @Column(name = "telephone", length = 30)
    private String telephone;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Client() {}

    public UUID getId()                    { return id; }
    public void setId(UUID v)              { this.id = v; }
    public String getNom()                 { return nom; }
    public void setNom(String v)           { this.nom = v; }
    public String getTelephone()           { return telephone; }
    public void setTelephone(String v)     { this.telephone = v; }
    public String getEmail()               { return email; }
    public void setEmail(String v)         { this.email = v; }
    public String getNotes()               { return notes; }
    public void setNotes(String v)         { this.notes = v; }
    public LocalDateTime getCreatedAt()    { return createdAt; }
    public void setCreatedAt(LocalDateTime v){ this.createdAt = v; }
    public LocalDateTime getUpdatedAt()    { return updatedAt; }
    public void setUpdatedAt(LocalDateTime v){ this.updatedAt = v; }
}
