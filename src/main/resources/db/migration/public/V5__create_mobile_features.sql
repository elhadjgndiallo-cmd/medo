CREATE TABLE IF NOT EXISTS public.clients_mobile (
    id                     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nom                    VARCHAR(100) NOT NULL,
    prenom                 VARCHAR(100) NOT NULL,
    email                  VARCHAR(255) NOT NULL,
    mot_de_passe           TEXT         NOT NULL,
    telephone              VARCHAR(30),
    avatar_initiales       VARCHAR(5),
    localisation_activee   BOOLEAN NOT NULL DEFAULT TRUE,
    notifications_activees BOOLEAN NOT NULL DEFAULT TRUE,
    actif                  BOOLEAN NOT NULL DEFAULT TRUE,
    created_at             TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at             TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_client_mobile_email UNIQUE (email)
);
CREATE INDEX IF NOT EXISTS idx_client_mobile_email ON public.clients_mobile(email);

CREATE TABLE IF NOT EXISTS public.favoris_mobile (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    client_id           UUID NOT NULL REFERENCES public.clients_mobile(id) ON DELETE CASCADE,
    pharmacie_tenant_id UUID NOT NULL REFERENCES public.tenants(id) ON DELETE CASCADE,
    pharmacie_nom       VARCHAR(200) NOT NULL,
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_favori_client_pharmacie UNIQUE (client_id, pharmacie_tenant_id)
);
CREATE INDEX IF NOT EXISTS idx_favori_client ON public.favoris_mobile(client_id);

CREATE TABLE IF NOT EXISTS public.historique_recherches (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    client_id        UUID NOT NULL REFERENCES public.clients_mobile(id) ON DELETE CASCADE,
    terme_produit    VARCHAR(200) NOT NULL,
    categorie        VARCHAR(100),
    nombre_resultats INTEGER NOT NULL DEFAULT 0,
    latitude_client  DOUBLE PRECISION,
    longitude_client DOUBLE PRECISION,
    created_at       TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_historique_client ON public.historique_recherches(client_id);
CREATE INDEX IF NOT EXISTS idx_historique_date   ON public.historique_recherches(created_at DESC);

CREATE TRIGGER trg_clients_mobile_updated_at
    BEFORE UPDATE ON public.clients_mobile
    FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();
