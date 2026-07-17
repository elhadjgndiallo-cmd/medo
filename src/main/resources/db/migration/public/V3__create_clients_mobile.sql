-- Table des clients mobiles (schéma public car cross-tenant)
CREATE TABLE IF NOT EXISTS clients_mobile (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL,
    telephone VARCHAR(30),
    avatar_initiales VARCHAR(5),
    localisation_activee BOOLEAN NOT NULL DEFAULT true,
    notifications_activees BOOLEAN NOT NULL DEFAULT true,
    actif BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table des favoris
CREATE TABLE IF NOT EXISTS favoris_mobile (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    client_id UUID NOT NULL REFERENCES clients_mobile(id) ON DELETE CASCADE,
    pharmacie_tenant_id UUID NOT NULL REFERENCES tenants(id),
    pharmacie_nom VARCHAR(200) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(client_id, pharmacie_tenant_id)
);

-- Table de l'historique des recherches
CREATE TABLE IF NOT EXISTS historique_recherches (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    client_id UUID NOT NULL REFERENCES clients_mobile(id) ON DELETE CASCADE,
    terme_produit VARCHAR(200) NOT NULL,
    categorie VARCHAR(100),
    nombre_resultats INT NOT NULL DEFAULT 0,
    latitude_client DECIMAL(10, 8),
    longitude_client DECIMAL(11, 8),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_clients_mobile_email ON clients_mobile(email);
CREATE INDEX idx_favoris_client ON favoris_mobile(client_id);
CREATE INDEX idx_historique_client ON historique_recherches(client_id);
CREATE INDEX idx_historique_date ON historique_recherches(created_at DESC);

COMMENT ON TABLE clients_mobile IS 'Utilisateurs de l''application mobile';
COMMENT ON TABLE favoris_mobile IS 'Pharmacies favorites des clients';
COMMENT ON TABLE historique_recherches IS 'Historique des recherches des clients';
