-- ═══════════════════════════════════════════════════════════
-- V1 — Initialisation schéma tenant (une pharmacie)
-- ═══════════════════════════════════════════════════════════

-- RBAC
CREATE TABLE IF NOT EXISTS permissions (
    id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    module  VARCHAR(30) NOT NULL,
    action  VARCHAR(20) NOT NULL,
    libelle VARCHAR(200) NOT NULL,
    CONSTRAINT uk_perm UNIQUE (module, action),
    CONSTRAINT chk_module CHECK (module IN ('POS','INVENTAIRE','ACHATS','RAPPORTS','ADMINISTRATION','CLIENTS','COMPTABILITE')),
    CONSTRAINT chk_action CHECK (action IN ('LIRE','ECRIRE','MODIFIER','SUPPRIMER'))
);

CREATE TABLE IF NOT EXISTS roles (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nom         VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    est_systeme BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_role_nom UNIQUE (nom)
);

CREATE TABLE IF NOT EXISTS roles_permissions (
    role_id       UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    permission_id UUID NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE IF NOT EXISTS utilisateurs (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nom               VARCHAR(100) NOT NULL,
    prenom            VARCHAR(100) NOT NULL,
    email             VARCHAR(255) NOT NULL,
    mot_de_passe      TEXT NOT NULL,
    telephone         VARCHAR(30),
    type_utilisateur  VARCHAR(30) NOT NULL CHECK (type_utilisateur IN ('SUPER_ADMIN','ADMIN_PHARMACIE','EMPLOYE','CLIENT_MOBILE')),
    actif             BOOLEAN NOT NULL DEFAULT TRUE,
    avatar_initiales  VARCHAR(5),
    dernier_connexion TIMESTAMP,
    created_at        TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_utilisateur_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS utilisateurs_roles (
    utilisateur_id UUID NOT NULL REFERENCES utilisateurs(id) ON DELETE CASCADE,
    role_id        UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (utilisateur_id, role_id)
);

CREATE INDEX IF NOT EXISTS idx_util_email ON utilisateurs(email);

-- Pharmacie
CREATE TABLE IF NOT EXISTS pharmacie (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nom              VARCHAR(150) NOT NULL,
    adresse          VARCHAR(500) NOT NULL,
    ville            VARCHAR(100),
    latitude         DOUBLE PRECISION,
    longitude        DOUBLE PRECISION,
    telephone        VARCHAR(30),
    horaires         TEXT,
    logo_url         VARCHAR(500),
    couleur_primaire VARCHAR(20) DEFAULT '#1B3A6B',
    created_at       TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Inventaire
CREATE TABLE IF NOT EXISTS emplacements (
    id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nom  VARCHAR(100) NOT NULL,
    code VARCHAR(50)  NOT NULL,
    type VARCHAR(20)  NOT NULL DEFAULT 'RAYON' CHECK (type IN ('RAYON','RESERVE','FRIGO','COFFRE')),
    CONSTRAINT uk_empl_code UNIQUE (code)
);

CREATE TABLE IF NOT EXISTS produits (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nom         VARCHAR(200) NOT NULL,
    dci         VARCHAR(200),
    categorie   VARCHAR(100),
    code_barres VARCHAR(100),
    icone_type  VARCHAR(50) DEFAULT 'pill',
    actif       BOOLEAN NOT NULL DEFAULT TRUE,
    prix_vente  DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    prix_achat  DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_prod_nom   ON produits(nom);
CREATE INDEX IF NOT EXISTS idx_prod_actif ON produits(actif);

CREATE TABLE IF NOT EXISTS variantes_produit (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    produit_id  UUID NOT NULL REFERENCES produits(id) ON DELETE CASCADE,
    dosage      VARCHAR(100),
    forme       VARCHAR(100),
    unite       VARCHAR(50),
    code_barres VARCHAR(100),
    prix_vente  DECIMAL(15,2),
    actif       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_var_prod ON variantes_produit(produit_id);

CREATE TABLE IF NOT EXISTS lots (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    variante_id      UUID NOT NULL REFERENCES variantes_produit(id) ON DELETE CASCADE,
    emplacement_id   UUID REFERENCES emplacements(id) ON DELETE SET NULL,
    numero_lot       VARCHAR(100) NOT NULL,
    date_fabrication DATE,
    date_peremption  DATE NOT NULL,
    quantite         INTEGER NOT NULL DEFAULT 0 CHECK (quantite >= 0),
    created_at       TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_lot_var  ON lots(variante_id);
CREATE INDEX IF NOT EXISTS idx_lot_perp ON lots(date_peremption);

CREATE TABLE IF NOT EXISTS stocks (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    variante_id     UUID NOT NULL REFERENCES variantes_produit(id) ON DELETE CASCADE,
    quantite_totale INTEGER NOT NULL DEFAULT 0 CHECK (quantite_totale >= 0),
    seuil_min       INTEGER NOT NULL DEFAULT 0,
    seuil_max       INTEGER,
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_stock_variante UNIQUE (variante_id)
);

CREATE TABLE IF NOT EXISTS mouvements_stock (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    lot_id       UUID NOT NULL REFERENCES lots(id) ON DELETE CASCADE,
    type         VARCHAR(20) NOT NULL CHECK (type IN ('ENTREE','SORTIE','AJUSTEMENT','REBUT')),
    quantite     INTEGER NOT NULL,
    motif        VARCHAR(200),
    reference_id UUID,
    created_by   UUID,
    created_at   TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Fournisseurs
CREATE TABLE IF NOT EXISTS fournisseurs (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nom        VARCHAR(200) NOT NULL,
    contact    VARCHAR(200),
    email      VARCHAR(255),
    adresse    TEXT,
    actif      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- POS
CREATE TABLE IF NOT EXISTS caisses (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nom        VARCHAR(100) NOT NULL,
    reference  VARCHAR(100),
    actif      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS sessions_caisse (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    caisse_id       UUID NOT NULL REFERENCES caisses(id) ON DELETE CASCADE,
    utilisateur_id  UUID NOT NULL REFERENCES utilisateurs(id),
    date_ouverture  TIMESTAMP NOT NULL DEFAULT NOW(),
    date_fermeture  TIMESTAMP,
    fond_caisse     DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    montant_cloture DECIMAL(15,2),
    statut          VARCHAR(20) NOT NULL DEFAULT 'OUVERTE' CHECK (statut IN ('OUVERTE','FERMEE')),
    notes           TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_session_statut ON sessions_caisse(statut);

CREATE TABLE IF NOT EXISTS clients (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nom        VARCHAR(200) NOT NULL,
    telephone  VARCHAR(30),
    email      VARCHAR(255),
    notes      TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS ventes (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id      UUID NOT NULL REFERENCES sessions_caisse(id),
    client_id       UUID REFERENCES clients(id) ON DELETE SET NULL,
    utilisateur_id  UUID NOT NULL REFERENCES utilisateurs(id),
    date_vente      TIMESTAMP NOT NULL DEFAULT NOW(),
    numero_ticket   VARCHAR(50) NOT NULL,
    montant_total   DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    montant_remise  DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    mode_paiement   VARCHAR(30) NOT NULL DEFAULT 'ESPECES'
                        CHECK (mode_paiement IN ('ESPECES','MOBILE_MONEY','CARTE','CREDIT')),
    statut          VARCHAR(20) NOT NULL DEFAULT 'VALIDEE'
                        CHECK (statut IN ('VALIDEE','ANNULEE','EN_ATTENTE')),
    notes           TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_vente_ticket UNIQUE (numero_ticket)
);
CREATE INDEX IF NOT EXISTS idx_vente_date   ON ventes(date_vente DESC);
CREATE INDEX IF NOT EXISTS idx_vente_statut ON ventes(statut);

CREATE TABLE IF NOT EXISTS lignes_vente (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    vente_id      UUID NOT NULL REFERENCES ventes(id) ON DELETE CASCADE,
    variante_id   UUID NOT NULL REFERENCES variantes_produit(id),
    lot_id        UUID REFERENCES lots(id) ON DELETE SET NULL,
    quantite      INTEGER NOT NULL CHECK (quantite > 0),
    prix_unitaire DECIMAL(15,2) NOT NULL,
    sous_total    DECIMAL(15,2) NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_lv_vente ON lignes_vente(vente_id);

-- Achats
CREATE TABLE IF NOT EXISTS bons_commande (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    reference             VARCHAR(50) NOT NULL,
    fournisseur_id        UUID NOT NULL REFERENCES fournisseurs(id),
    date_commande         DATE NOT NULL DEFAULT CURRENT_DATE,
    date_livraison_prevue DATE,
    statut                VARCHAR(30) NOT NULL DEFAULT 'CONFIRME'
                              CHECK (statut IN ('BROUILLON','CONFIRME','PARTIELLEMENT_RECU','RECU','ANNULE')),
    montant_total         DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    notes                 TEXT,
    created_by            UUID REFERENCES utilisateurs(id),
    created_at            TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_bc_ref UNIQUE (reference)
);
CREATE INDEX IF NOT EXISTS idx_bc_statut ON bons_commande(statut);

CREATE TABLE IF NOT EXISTS lignes_commande (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    bc_id           UUID NOT NULL REFERENCES bons_commande(id) ON DELETE CASCADE,
    variante_id     UUID NOT NULL REFERENCES variantes_produit(id),
    quantite_cmd    INTEGER NOT NULL CHECK (quantite_cmd > 0),
    quantite_recue  INTEGER NOT NULL DEFAULT 0,
    prix_unitaire   DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    sous_total      DECIMAL(15,2) NOT NULL DEFAULT 0.00
);
CREATE INDEX IF NOT EXISTS idx_lc_bc ON lignes_commande(bc_id);

-- Disponibilité produits (pour l'app mobile)
CREATE TABLE IF NOT EXISTS disponibilite_produits (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    variante_id UUID NOT NULL REFERENCES variantes_produit(id) ON DELETE CASCADE,
    disponible  BOOLEAN NOT NULL DEFAULT FALSE,
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_dispo_var UNIQUE (variante_id)
);

-- Triggers updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$ BEGIN NEW.updated_at = NOW(); RETURN NEW; END; $$ LANGUAGE plpgsql;

DO $$
DECLARE t TEXT;
BEGIN
    FOREACH t IN ARRAY ARRAY[
        'utilisateurs','produits','pharmacie','fournisseurs',
        'clients','bons_commande','disponibilite_produits'
    ] LOOP
        EXECUTE format('CREATE TRIGGER trg_%s_upd BEFORE UPDATE ON %s FOR EACH ROW EXECUTE FUNCTION update_updated_at_column()', t, t);
    END LOOP;
END; $$;

-- Seed permissions
INSERT INTO permissions (module, action, libelle) VALUES
    ('POS','LIRE','Consulter les ventes'),('POS','ECRIRE','Créer des ventes'),
    ('POS','MODIFIER','Modifier une vente'),('POS','SUPPRIMER','Annuler une vente'),
    ('INVENTAIRE','LIRE','Consulter le stock'),('INVENTAIRE','ECRIRE','Ajouter produits/lots'),
    ('INVENTAIRE','MODIFIER','Modifier le stock'),('INVENTAIRE','SUPPRIMER','Supprimer produits'),
    ('ACHATS','LIRE','Voir les commandes'),('ACHATS','ECRIRE','Créer des commandes'),
    ('ACHATS','MODIFIER','Réceptionner commandes'),('ACHATS','SUPPRIMER','Annuler commandes'),
    ('RAPPORTS','LIRE','Voir les rapports'),
    ('ADMINISTRATION','LIRE','Voir la configuration'),
    ('ADMINISTRATION','ECRIRE','Gérer utilisateurs'),
    ('ADMINISTRATION','MODIFIER','Modifier paramètres'),
    ('CLIENTS','LIRE','Voir les clients'),
    ('CLIENTS','ECRIRE','Ajouter clients'),
    ('CLIENTS','MODIFIER','Modifier clients')
ON CONFLICT (module, action) DO NOTHING;

-- Rôles système
INSERT INTO roles (nom, description, est_systeme) VALUES
    ('ADMIN_PHARMACIE','Administrateur — accès complet', TRUE),
    ('EMPLOYE_BASE','Employé — lecture POS et Inventaire', TRUE)
ON CONFLICT (nom) DO NOTHING;
