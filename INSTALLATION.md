# Guide d'Installation - MEDO API

## 📋 Prérequis

### Logiciels Requis
- **Java 17** ou supérieur
- **Maven 3.8+**
- **PostgreSQL 14+**
- **Redis 6+**
- **Git**

### Vérification des installations
```bash
java -version
mvn -version
psql --version
redis-cli --version
```

## 🚀 Installation Pas à Pas

### 1. Cloner le Projet
```bash
git clone <repository-url>
cd medo-api
```

### 2. Configuration PostgreSQL

#### 2.1 Créer la base de données
```sql
-- Se connecter à PostgreSQL
psql -U postgres

-- Créer l'utilisateur
CREATE USER medo WITH PASSWORD 'medo_secret';

-- Créer la base
CREATE DATABASE medo OWNER medo;

-- Se connecter à la base
\c medo

-- Activer l'extension UUID
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Donner les permissions
GRANT ALL PRIVILEGES ON DATABASE medo TO medo;
GRANT ALL ON SCHEMA public TO medo;
```

#### 2.2 Configuration pour Multi-Tenancy
```sql
-- L'utilisateur doit pouvoir créer des schémas
ALTER USER medo CREATEDB;
```

### 3. Configuration Redis

#### Windows
```bash
# Télécharger Redis depuis https://github.com/microsoftarchive/redis/releases
# Ou utiliser WSL/Docker

# Démarrer Redis
redis-server
```

#### Linux/Mac
```bash
# Installer Redis
sudo apt-get install redis-server  # Ubuntu/Debian
brew install redis                 # macOS

# Démarrer Redis
redis-server

# Vérifier
redis-cli ping
# Réponse attendue: PONG
```

### 4. Configuration de l'Application

#### 4.1 Variables d'Environnement

**Windows (PowerShell)**
```powershell
$env:DB_HOST="localhost"
$env:DB_PORT="5432"
$env:DB_NAME="medo"
$env:DB_USER="medo"
$env:DB_PASS="medo_secret"
$env:REDIS_HOST="localhost"
$env:REDIS_PORT="6379"
$env:JWT_SECRET="medo_jwt_secret_key_must_be_at_least_32_chars_long_for_production"
```

**Linux/Mac (Bash)**
```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=medo
export DB_USER=medo
export DB_PASS=medo_secret
export REDIS_HOST=localhost
export REDIS_PORT=6379
export JWT_SECRET=medo_jwt_secret_key_must_be_at_least_32_chars_long_for_production
```

#### 4.2 Fichier .env (Alternative)
Créer un fichier `.env` à la racine :
```properties
DB_HOST=localhost
DB_PORT=5432
DB_NAME=medo
DB_USER=medo
DB_PASS=medo_secret
REDIS_HOST=localhost
REDIS_PORT=6379
JWT_SECRET=medo_jwt_secret_key_must_be_at_least_32_chars_long_for_production
```

### 5. Build et Compilation

#### 5.1 Télécharger les dépendances
```bash
mvn clean install -DskipTests
```

#### 5.2 Exécuter les tests
```bash
mvn test
```

### 6. Lancer l'Application

#### Mode Développement
```bash
mvn spring-boot:run
```

#### Mode Production
```bash
# Build
mvn clean package -DskipTests

# Exécuter le JAR
java -jar target/medo-api-1.0.0.jar
```

### 7. Vérification

#### 7.1 Santé de l'application
```bash
curl http://localhost:8080/actuator/health
```

Réponse attendue :
```json
{
  "status": "UP"
}
```

#### 7.2 Documentation API
Ouvrir dans le navigateur :
```
http://localhost:8080/swagger-ui
```

## 🐳 Installation avec Docker

### Prérequis
- Docker Desktop installé

### Démarrage Rapide
```bash
# Construire et démarrer tous les services
docker-compose up -d

# Vérifier les logs
docker-compose logs -f medo-api

# Arrêter les services
docker-compose down

# Arrêter et supprimer les volumes
docker-compose down -v
```

### Services Docker
- **PostgreSQL** : `localhost:5432`
- **Redis** : `localhost:6379`
- **API** : `localhost:8080`

## 🔧 Configuration Avancée

### Profils Spring

#### Développement (application.yml)
```yaml
spring:
  profiles:
    active: dev
  jpa:
    show-sql: true
logging:
  level:
    com.medo: DEBUG
```

#### Production (application-prod.yml)
```yaml
spring:
  profiles:
    active: prod
  jpa:
    show-sql: false
logging:
  level:
    com.medo: INFO
```

### Lancer avec un profil
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

## 🗃️ Migrations de Base de Données

### Flyway - Migrations Automatiques

Les migrations s'exécutent automatiquement au démarrage :

**Schéma PUBLIC** (`src/main/resources/db/migration/public/`)
- `V1__create_tenants.sql`
- `V2__create_demandes_inscription.sql`
- `V3__create_clients_mobile.sql`

**Schémas TENANT** (`src/main/resources/db/migration/tenant/`)
- `V1__init_schema.sql`
- `V2__create_utilisateurs_roles.sql`
- `V3__create_produits.sql`
- etc.

### Vérifier l'état des migrations
```sql
SELECT * FROM flyway_schema_history_public;
```

## 🧪 Premiers Tests

### 1. Créer une Demande d'Inscription
```bash
curl -X POST http://localhost:8080/api/v1/auth/inscription \
  -H "Content-Type: application/json" \
  -d '{
    "nomPharmacie": "Pharmacie Test",
    "emailContact": "test@pharmacie.com",
    "motDePasse": "password123",
    "sousDomaineSouhaite": "test-pharma",
    "adresse": "123 Rue Test",
    "telephone": "+224123456789",
    "planDemande": "GRATUIT"
  }'
```

### 2. Inscription Client Mobile
```bash
curl -X POST http://localhost:8080/api/v1/auth/mobile/register \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Doe",
    "prenom": "John",
    "email": "john@example.com",
    "motDePasse": "password123",
    "telephone": "+224987654321"
  }'
```

## 🐛 Dépannage

### Problème : Port 8080 déjà utilisé
```yaml
# Changer le port dans application.yml
server:
  port: 8081
```

### Problème : Connexion PostgreSQL refusée
```bash
# Vérifier que PostgreSQL est démarré
sudo service postgresql status  # Linux
brew services list              # macOS

# Vérifier les connexions
psql -U medo -d medo -h localhost
```

### Problème : Redis inaccessible
```bash
# Démarrer Redis
redis-server

# Tester la connexion
redis-cli ping
```

### Problème : Flyway migration failed
```bash
# Réinitialiser Flyway
mvn flyway:clean flyway:migrate

# Ou en SQL
DROP TABLE flyway_schema_history_public;
```

## 📞 Support

Pour toute question ou problème :
- Créer une issue sur le repository
- Contacter l'équipe de développement

---

✅ **Installation terminée !** L'API est maintenant prête à l'emploi.
