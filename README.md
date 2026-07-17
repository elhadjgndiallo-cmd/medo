# MEDO API - Système de Gestion de Pharmacie Multi-Tenant

API REST Spring Boot pour la gestion de pharmacies avec architecture multi-tenant et application mobile.

## 🏗️ Architecture

### Multi-Tenancy
- Architecture **SCHEMA-based** : chaque pharmacie a son propre schéma PostgreSQL
- Schéma `public` : contient les données partagées (tenants, abonnements, demandes inscription)
- Schémas tenant (`pharma_xxx`) : contient les données métier de chaque pharmacie

### Modules Fonctionnels

```
src/main/java/com/medo/api/
├── MedoApiApplication.java          # Point d'entrée
│
├── config/                          # Configuration Spring
│   ├── SecurityConfig.java          # Spring Security + JWT
│   ├── MultiTenantConfig.java       # Configuration Hibernate multi-tenant
│   ├── RedisConfig.java             # Cache Redis
│   ├── SwaggerConfig.java           # Documentation OpenAPI
│   ├── CorsConfig.java              # Configuration CORS
│   └── FlywayConfig.java            # Migrations DB
│
├── tenant/                          # Gestion multi-tenancy
│   ├── TenantContext.java           # ThreadLocal du tenant courant
│   ├── TenantFilter.java            # Intercepte header X-Tenant
│   ├── TenantIdentifierResolver.java
│   └── TenantProvisioningService.java
│
├── security/                        # Sécurité & JWT
│   ├── JwtTokenProvider.java
│   ├── JwtAuthenticationFilter.java
│   └── MedoUserPrincipal.java
│
├── common/                          # Schéma PUBLIC
│   ├── entity/
│   │   ├── Tenant.java
│   │   ├── Abonnement.java
│   │   └── DemandeInscription.java
│   ├── repository/
│   ├── service/
│   └── controller/
│
├── auth/                            # Module Authentification
│   ├── entity/
│   │   ├── Utilisateur.java
│   │   ├── Role.java
│   │   └── Permission.java
│   ├── repository/
│   ├── service/AuthService.java
│   ├── controller/AuthController.java
│   └── dto/AuthDtos.java
│
├── inventaire/                      # Module Inventaire
│   ├── entity/
│   │   ├── Produit.java
│   │   ├── VarianteProduit.java
│   │   ├── Lot.java
│   │   ├── Stock.java
│   │   ├── Emplacement.java
│   │   └── MouvementStock.java
│   ├── repository/
│   ├── service/
│   ├── controller/
│   └── dto/
│
├── pos/                             # Module Point de Vente
│   ├── entity/
│   │   ├── Caisse.java
│   │   ├── SessionCaisse.java
│   │   ├── Vente.java
│   │   ├── LigneVente.java
│   │   └── Client.java
│   ├── repository/
│   ├── service/
│   ├── controller/
│   └── dto/
│
├── achats/                          # Module Achats
│   ├── entity/
│   │   ├── Fournisseur.java
│   │   ├── BonCommande.java
│   │   └── LigneCommande.java
│   ├── repository/
│   ├── service/AchatService.java
│   ├── controller/AchatController.java
│   └── dto/AchatsDtos.java
│
├── mobile/                          # Module Mobile/Public
│   ├── entity/
│   │   ├── ClientMobile.java
│   │   ├── Favori.java
│   │   ├── HistoriqueRecherche.java
│   │   └── DisponibiliteProduit.java
│   ├── repository/
│   ├── service/
│   ├── controller/
│   └── dto/
│
├── pharmacie/                       # Module Pharmacie
│   ├── entity/Pharmacie.java
│   ├── service/PharmacieService.java
│   └── controller/PharmacieController.java
│
├── rapports/                        # Module Rapports
│   ├── service/StatistiqueService.java
│   └── controller/RapportController.java
│
└── exception/                       # Gestion erreurs
    └── GlobalExceptionHandler.java
```

## 🗄️ Base de Données

### Migrations Flyway

**Schéma PUBLIC** (`db/migration/public/`)
```
V1__create_tenants.sql
V2__create_abonnements.sql
V3__create_demandes_inscription.sql
V4__create_clients_mobile.sql
```

**Schémas TENANT** (`db/migration/tenant/`)
```
V1__init_schema.sql
V2__create_produits.sql
V3__create_stock_lots.sql
V4__create_pos.sql
V5__create_achats.sql
V6__create_utilisateurs_roles.sql
```

## 🚀 Démarrage

### Prérequis
- Java 17+
- PostgreSQL 14+
- Redis 6+
- Maven 3.8+

### Configuration

**1. Variables d'environnement**
```bash
DB_HOST=localhost
DB_PORT=5432
DB_NAME=medo
DB_USER=medo
DB_PASS=medo_secret
REDIS_HOST=localhost
REDIS_PORT=6379
JWT_SECRET=votre_secret_jwt_au_moins_32_caracteres
```

**2. Base de données**
```sql
CREATE DATABASE medo;
CREATE USER medo WITH PASSWORD 'medo_secret';
GRANT ALL PRIVILEGES ON DATABASE medo TO medo;
```

**3. Lancer l'application**
```bash
mvn clean install
mvn spring-boot:run
```

L'API sera disponible sur `http://localhost:8080`

## 📚 Documentation API

Une fois l'application démarrée :
- **Swagger UI** : http://localhost:8080/swagger-ui
- **OpenAPI JSON** : http://localhost:8080/api-docs

## 🔐 Authentification

### JWT Token Flow

1. **Login Pharmacie**
```http
POST /api/v1/auth/login
Content-Type: application/json
X-Tenant: pharma_abc

{
  "email": "admin@pharmacie.com",
  "motDePasse": "password"
}
```

2. **Login Client Mobile**
```http
POST /api/v1/auth/mobile/login
Content-Type: application/json

{
  "email": "client@email.com",
  "motDePasse": "password"
}
```

3. **Utiliser le token**
```http
GET /api/v1/inventaire/produits
Authorization: Bearer <access_token>
X-Tenant: pharma_abc
```

## 🏪 Endpoints Principaux

### Authentification (`/api/v1/auth`)
- `POST /login` - Connexion pharmacie
- `POST /mobile/login` - Connexion client mobile
- `POST /refresh` - Renouveler token
- `POST /inscription` - Demande inscription pharmacie

### Inventaire (`/api/v1/inventaire`)
- `GET /produits` - Liste produits
- `POST /produits` - Créer produit
- `GET /stocks/alertes` - Alertes stock bas
- `POST /lots` - Créer lot

### Point de Vente (`/api/v1/pos`)
- `GET /caisses` - Liste caisses
- `POST /sessions/ouvrir` - Ouvrir session
- `POST /ventes` - Enregistrer vente
- `POST /sessions/fermer` - Fermer session

### Achats (`/api/v1/achats`)
- `GET /fournisseurs` - Liste fournisseurs
- `POST /bons-commande` - Créer commande
- `POST /bons-commande/{id}/recevoir` - Réceptionner

### Mobile Public (`/api/v1/public`)
- `GET /recherche` - Recherche médicaments (sans auth)
- `GET /pharmacies/proximite` - Pharmacies proches

### Mobile Client (`/api/v1/clients`)
- `GET /me` - Profil client
- `GET /me/favoris` - Favoris
- `GET /me/historique` - Historique recherches

## 🔧 Technologies

- **Spring Boot 3.2.1** - Framework
- **Spring Security** - Authentification/Autorisation
- **Spring Data JPA** - ORM
- **Hibernate Multi-Tenancy** - Multi-tenant SCHEMA
- **PostgreSQL** - Base de données
- **Flyway** - Migrations DB
- **Redis** - Cache & sessions
- **JWT (jjwt 0.12.3)** - Tokens
- **SpringDoc OpenAPI** - Documentation
- **Maven** - Build

## 📝 Notes Importantes

### Multi-Tenancy
- Le header `X-Tenant` est **obligatoire** pour toutes les requêtes tenant-specific
- Les clients mobiles utilisent le tenant `public`
- Chaque tenant a son propre schéma isolé

### Sécurité
- Les mots de passe sont hashés avec BCrypt
- Les tokens JWT expirent après 30 minutes
- Les refresh tokens expirent après 7 jours
- Les tokens révoqués sont blacklistés dans Redis

### Performance
- Cache Redis activé sur les requêtes fréquentes
- Pagination par défaut (20 éléments)
- Connexions DB poolées (Hikari)

## 📄 License

Propriétaire - MEDO © 2024
