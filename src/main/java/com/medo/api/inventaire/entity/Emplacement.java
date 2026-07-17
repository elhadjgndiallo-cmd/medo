package com.medo.api.inventaire.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "emplacements",
    uniqueConstraints = @UniqueConstraint(name = "uk_empl_code", columnNames = "code"))
public class Emplacement {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private TypeEmplacement type = TypeEmplacement.RAYON;

    public Emplacement() {}

    public UUID getId()                    { return id; }
    public void setId(UUID v)              { this.id = v; }
    public String getNom()                 { return nom; }
    public void setNom(String v)           { this.nom = v; }
    public String getCode()                { return code; }
    public void setCode(String v)          { this.code = v; }
    public TypeEmplacement getType()       { return type; }
    public void setType(TypeEmplacement v) { this.type = v; }

    public String getIconeBadge() {
        if (type == null) return "shelf";
        return switch (type) {
            case FRIGO   -> "snowflake";
            case RESERVE -> "box";
            case COFFRE  -> "lock";
            default      -> "shelf";
        };
    }

    public enum TypeEmplacement { RAYON, RESERVE, FRIGO, COFFRE }
}
