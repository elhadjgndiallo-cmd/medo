CREATE TABLE IF NOT EXISTS public.abonnements (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id           UUID NOT NULL REFERENCES public.tenants(id) ON DELETE CASCADE,
    plan                VARCHAR(20) NOT NULL CHECK (plan IN ('GRATUIT','PRO')),
    date_debut          DATE NOT NULL DEFAULT CURRENT_DATE,
    date_fin            DATE,
    montant_mensuel     DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_abonnement_tenant UNIQUE (tenant_id)
);
CREATE TRIGGER trg_abonnements_updated_at
    BEFORE UPDATE ON public.abonnements
    FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();
