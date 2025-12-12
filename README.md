# Système de Gestion de Colis et Transporteurs

## 📋 Description

API REST moderne pour la gestion de colis avec différents types (STANDARD, FRAGILE, FRIGO) et gestion des transporteurs avec spécialités. Le système implémente une authentification JWT stateless et suit les meilleures pratiques DevOps.

## 🚀 Technologies Utilisées

- **Backend**: Spring Boot 3.5.7
- **Base de données**: MongoDB (NoSQL avec schéma flexible)
- **Sécurité**: Spring Security + JWT (stateless)
- **Tests**: JUnit 5 + Mockito
- **Documentation**: Swagger/OpenAPI 3.0
- **Conteneurisation**: Docker + Docker Compose
- **CI/CD**: GitHub Actions
- **Build**: Maven
- **Outils**: Lombok, Spring DevTools

## 📁 Structure du Projet

```
src/
├── main/
│   ├── java/org/example/colis/
│   │   ├── config/          # Configurations (Swagger, DataInitializer)
│   │   ├── controller/      # REST Controllers
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── enums/           # Énumérations
│   │   ├── exception/       # Gestion des exceptions
│   │   ├── mapper/          # Mappers Entity <-> DTO
│   │   ├── model/           # Entités MongoDB
│   │   ├── repository/      # Repositories Spring Data
│   │   ├── security/        # JWT & Security Config
│   │   └── service/         # Logique métier
│   └── resources/
│       └── application.properties
└── test/
    └── java/org/example/colis/
        └── service/         # Tests unitaires
```

## 🗄️ Modèle de Données

### Collection `users`

```json
{
  "_id": "ObjectId",
  "login": "string",
  "password": "string (encrypted)",
  "role": "ADMIN | TRANSPORTEUR",
  "active": "boolean",
  // Si TRANSPORTEUR uniquement:
  "statut": "DISPONIBLE | EN_LIVRAISON",
  "specialite": "STANDARD | FRAGILE | FRIGO"
}
```

### Collection `colis`

```json
{
  "_id": "ObjectId",
  "type": "STANDARD | FRAGILE | FRIGO",
  "poids": "double",
  "adresseDestination": "string",
  "statut": "EN_ATTENTE | EN_TRANSIT | LIVRE | ANNULE",
  "transporteurId": "string",
  // Si FRAGILE uniquement:
  "instructionsManutention": "string",
  // Si FRIGO uniquement:
  "temperatureMin": "double",
  "temperatureMax": "double"
}
```

## 🔐 Authentification

L'API utilise JWT (JSON Web Token) pour l'authentification stateless.

### Structure du JWT

- **Issuer**: `colis-api`
- **Subject**: Login de l'utilisateur
- **Claim**: Rôle de l'utilisateur
- **Expiration**: 24 heures (86400000 ms)
- **Signature**: HMAC256

### Utilisation

1. **Login**: `POST /api/auth/login`
   ```json
   {
     "login": "admin",
     "password": "admin123"
   }
   ```

2. **Réponse**:
   ```json
   {
     "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     "login": "admin",
     "role": "ADMIN"
   }
   ```

3. **Utiliser le token**: Ajouter le header `Authorization: Bearer {token}` à chaque requête

## 📡 Endpoints API

### Authentication

- `POST /api/auth/login` - Authentification (public)

### Admin - Gestion des Transporteurs

- `GET /api/admin/users` - Liste tous les utilisateurs
- `GET /api/admin/transporteurs` - Liste les transporteurs (filtrable par spécialité)
- `POST /api/admin/transporteurs` - Créer un transporteur
- `PUT /api/admin/transporteurs/{id}` - Modifier un transporteur
- `DELETE /api/admin/transporteurs/{id}` - Supprimer un transporteur

### Admin - Gestion des Colis

- `GET /api/admin/colis` - Liste tous les colis (filtrable par type/statut)
- `GET /api/admin/colis/search?adresse=` - Rechercher par adresse
- `GET /api/admin/colis/{id}` - Obtenir un colis par ID
- `POST /api/admin/colis` - Créer un nouveau colis
- `POST /api/admin/colis/{id}/assign` - Assigner à un transporteur
- `PUT /api/admin/colis/{id}` - Modifier un colis
- `PATCH /api/admin/colis/{id}/statut` - Mettre à jour le statut
- `DELETE /api/admin/colis/{id}` - Supprimer un colis

### Transporteur - Gestion des Colis

- `GET /api/transporteur/colis` - Liste ses colis (filtrable)
- `GET /api/transporteur/colis/search?adresse=` - Rechercher ses colis
- `GET /api/transporteur/colis/{id}` - Obtenir un de ses colis
- `PATCH /api/transporteur/colis/{id}/statut` - Mettre à jour le statut

## 🚀 Démarrage Rapide

### Prérequis

- Java 17+
- Maven 3.6+
- Docker & Docker Compose (optionnel)

### Option 1: Avec Docker Compose (Recommandé)

```bash
# Démarrer l'application et MongoDB
docker-compose up -d

# L'API sera disponible sur http://localhost:8080
# MongoDB sur localhost:27017
```

### Option 2: Démarrage Local

```bash
# 1. Démarrer MongoDB
docker run -d -p 27017:27017 \
  -e MONGO_INITDB_ROOT_USERNAME=root \
  -e MONGO_INITDB_ROOT_PASSWORD=secret \
  mongo:latest

# 2. Compiler et lancer l'application
mvn clean install
mvn spring-boot:run
```

### Option 3: Build et Run avec Docker

```bash
# Build l'image
docker build -t colis-app .

# Run le container
docker run -p 8080:8080 \
  -e SPRING_DATA_MONGODB_HOST=host.docker.internal \
  colis-app
```

## 📚 Documentation API

Une fois l'application démarrée, accédez à:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## 👤 Utilisateurs par Défaut

L'application crée automatiquement des utilisateurs de test au démarrage:

### Admin
- **Login**: `admin`
- **Password**: `admin123`
- **Rôle**: ADMIN

### Transporteurs
- **Login**: `transporteur_standard` | **Password**: `trans123` | **Spécialité**: STANDARD
- **Login**: `transporteur_fragile` | **Password**: `trans123` | **Spécialité**: FRAGILE
- **Login**: `transporteur_frigo` | **Password**: `trans123` | **Spécialité**: FRIGO

## ✅ Tests

### Exécuter tous les tests

```bash
mvn test
```

### Tests avec couverture

```bash
mvn clean verify
```

### Tests inclus

- **AuthServiceTest**: Tests d'authentification
- **UserServiceTest**: Tests de gestion des utilisateurs
- **ColisServiceTest**: Tests de gestion des colis (création, assignation, validation)

## 🔒 Règles Métier

1. **Un colis ne peut être assigné qu'à un transporteur avec la spécialité correspondante**:
   - Colis STANDARD → Transporteur STANDARD
   - Colis FRAGILE → Transporteur FRAGILE
   - Colis FRIGO → Transporteur FRIGO

2. **Colis FRAGILE**: Doit avoir des `instructionsManutention`

3. **Colis FRIGO**: Doit avoir `temperatureMin` et `temperatureMax` (min < max)

4. **TRANSPORTEUR**: Doit avoir une `specialite` et un `statut`

5. **ADMIN**: Ne peut pas avoir de `specialite` ou `statut`

6. **Utilisateur désactivé**: Ne peut plus se connecter

7. **TRANSPORTEUR**: Ne peut voir et modifier que ses propres colis

8. **ADMIN**: Peut tout voir et tout modifier

## 🔄 CI/CD

Le projet utilise GitHub Actions pour:

1. **Build**: Compilation Maven
2. **Tests**: Exécution des tests unitaires
3. **Docker**: Build de l'image Docker
4. **Quality**: Vérification de la qualité du code

### Workflow déclenché sur:
- Push sur `main` ou `develop`
- Pull Request vers `main` ou `develop`

## 📝 Configuration

### Variables d'environnement

```properties
# MongoDB
SPRING_DATA_MONGODB_HOST=localhost
SPRING_DATA_MONGODB_PORT=27017
SPRING_DATA_MONGODB_DATABASE=colis_db
SPRING_DATA_MONGODB_USERNAME=root
SPRING_DATA_MONGODB_PASSWORD=secret

# JWT
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400000

# Server
SERVER_PORT=8080
```

## 🛠️ Développement

### Ajouter un nouveau endpoint

1. Créer le DTO dans `dto/`
2. Créer/modifier le service dans `service/`
3. Créer le controller dans `controller/`
4. Ajouter les tests dans `test/service/`

### Outils de développement

- **Lombok**: Réduit le boilerplate code
- **DevTools**: Hot reload pendant le développement
- **Actuator**: Monitoring et health checks

## 🐛 Gestion des Erreurs

L'API utilise `@ControllerAdvice` pour une gestion centralisée des erreurs:

- **ResourceNotFoundException** (404): Ressource non trouvée
- **BusinessException** (400): Erreur de logique métier
- **UnauthorizedException** (403): Accès non autorisé
- **MethodArgumentNotValidException** (400): Validation échouée

### Format de réponse d'erreur

```json
{
  "timestamp": "2024-11-17T16:30:00",
  "status": 400,
  "error": "Business Error",
  "message": "Specialite is required for TRANSPORTEUR",
  "path": "/api/admin/transporteurs"
}
```

## 📊 Fonctionnalités Java Modernes

- **Stream API**: Traitement des collections
- **Optional**: Gestion des valeurs nullables
- **Lambda expressions**: Code fonctionnel
- **Switch expressions**: Mapping type-specialite
- **Records**: (peut être utilisé pour les DTOs simples)

## 🤝 Contribution

1. Fork le projet
2. Créer une branche feature (`git checkout -b feature/AmazingFeature`)
3. Commit les changements (`git commit -m 'Add AmazingFeature'`)
4. Push vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrir une Pull Request

## 📄 License

Ce projet est sous licence MIT.

## 👥 Auteurs

Projet développé dans le cadre d'un système de gestion logistique moderne.

---

**Note**: Ce projet utilise les meilleures pratiques Spring Boot et DevOps pour un déploiement en production.
