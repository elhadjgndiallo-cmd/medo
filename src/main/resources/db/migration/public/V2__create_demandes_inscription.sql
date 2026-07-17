CREATE TABLE IF NOT EXISTS public.demandes_inscription (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nom_pharmacie         VARCHAR(150) NOT NULL,
    email_contact         VARCHAR(255) NOT NULL,
    mot_de_passe_hash     TEXT         NOT NULL,
    sous_domaine_souhaite VARCHAR(100) NOT NULL,
    adresse               VARCHAR(500) NOT NULL,
    telephone             VARCHAR(30),
    plan_demande          VARCHAR(20)  NOT NULL DEFAULT 'GRATUIT' CHECK (plan_demande IN ('GRATUIT','PRO')),
    statut                VARCHAR(20)  NOT NULL DEFAULT 'EN_ATTENTE' CHECK (statut IN ('EN_ATTENTE','ACCEPTEE','REJETEE')),
    motif_rejet           VARCHAR(500),
    tenant_id             UUID         REFERENCES public.tenants(id) ON DELETE SET NULL,
    traite_par            UUID,
    traite_le             TIMESTAMP,
    created_at            TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_demande_email        UNIQUE (email_contact),
    CONSTRAINT uk_demande_sous_domaine UNIQUE (sous_domaine_souhaite)
);
CREATE INDEX IF NOT EXISTS idx_demande_statut ON public.demandes_inscription(statut);
CREATE TRIGGER trg_demandes_updated_at
    BEFORE UPDATE ON public.demandes_inscription
    FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();
