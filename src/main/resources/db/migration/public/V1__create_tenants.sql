CREATE TABLE IF NOT EXISTS public.tenants (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nom                 VARCHAR(150) NOT NULL,
    sous_domaine        VARCHAR(100) NOT NULL,
    domain_personnalise VARCHAR(255),
    schema_name         VARCHAR(100) NOT NULL,
    statut              VARCHAR(20)  NOT NULL DEFAULT 'EN_ATTENTE'
                            CHECK (statut IN ('EN_ATTENTE','ACTIF','SUSPENDU')),
    plan                VARCHAR(20)  NOT NULL DEFAULT 'GRATUIT'
                            CHECK (plan IN ('GRATUIT','PRO')),
    logo_url            VARCHAR(500),
    couleur_primaire    VARCHAR(20)  DEFAULT '#1B3A6B',
    couleur_secondaire  VARCHAR(20)  DEFAULT '#059669',
    email_contact       VARCHAR(255) NOT NULL,
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_tenant_sous_domaine UNIQUE (sous_domaine),
    CONSTRAINT uk_tenant_schema_name  UNIQUE (schema_name)
);
CREATE INDEX IF NOT EXISTS idx_tenant_statut ON public.tenants(statut);
CREATE INDEX IF NOT EXISTS idx_tenant_plan   ON public.tenants(plan);

CREATE OR REPLACE FUNCTION public.update_updated_at_column()
RETURNS TRIGGER AS $$ BEGIN NEW.updated_at = NOW(); RETURN NEW; END; $$ LANGUAGE plpgsql;

CREATE TRIGGER trg_tenants_updated_at
    BEFORE UPDATE ON public.tenants
    FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();
