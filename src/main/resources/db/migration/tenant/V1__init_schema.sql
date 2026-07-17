-- Initialisation du schéma tenant
-- Ce script est exécuté pour chaque nouveau tenant

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Enable PostGIS for geolocation (if available)
-- CREATE EXTENSION IF NOT EXISTS postgis;

COMMENT ON SCHEMA CURRENT_SCHEMA() IS 'Schéma d''une pharmacie (tenant)';
