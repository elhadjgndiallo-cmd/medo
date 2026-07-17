package com.medo.api.auth.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "permissions",
    uniqueConstraints = @UniqueConstraint(name = "uk_perm", columnNames = {"module","action"}))
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "module", nullable = false, length = 30)
    private ModuleApp module;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 20)
    private ActionType action;

    @Column(name = "libelle", nullable = false, length = 200)
    private String libelle;

    public Permission() {}

    public Permission(UUID id, ModuleApp module, ActionType action, String libelle) {
        this.id = id; this.module = module;
        this.action = action; this.libelle = libelle;
    }

    public UUID getId()         { return id; }
    public void setId(UUID v)   { this.id = v; }
    public ModuleApp getModule()      { return module; }
    public void setModule(ModuleApp v){ this.module = v; }
    public ActionType getAction()     { return action; }
    public void setAction(ActionType v){ this.action = v; }
    public String getLibelle()        { return libelle; }
    public void setLibelle(String v)  { this.libelle = v; }

    public String getCode() { return module.name() + "." + action.name(); }

    public enum ModuleApp { POS, INVENTAIRE, ACHATS, RAPPORTS, ADMINISTRATION, CLIENTS, COMPTABILITE }
    public enum ActionType { LIRE, ECRIRE, MODIFIER, SUPPRIMER }
}
