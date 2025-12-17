# PROJET – CYBERSHIELD 360
**Document de Référence Technique & Fonctionnelle**

---

## 1. Vision du Projet
**CyberShield 360** est une plateforme unifiée de cybersécurité combinant deux piliers complémentaires :
1.  **Audit & Gouvernance (L'IA Consultante) :** Un système d'audit automatisé qui scanne l'infrastructure, identifie les failles, et les traduit en exigences normatives (ISO 27001, IEC 62443, RGPD) via une Intelligence Artificielle.
2.  **Protection Active (Le Framework Polymorphe) :** Une surcouche de communication sécurisée (Java) déployable sur les systèmes critiques (IoT, Drones, Robots) pour obfusquer les flux et neutraliser les menaces détectées par l'audit.

---

## 2. Architecture Globale
Le système repose sur une **Architecture Hybride** pour maximiser la performance et la réactivité :

* **SOA (Service Oriented Architecture) - Synchrone :**
    * Utilisé pour l'authentification, la gestion des utilisateurs et l'administration.
    * Garantit des réponses immédiates pour l'interface utilisateur.
* **EDA (Event-Driven Architecture) - Asynchrone :**
    * Utilisé pour les scans réseaux longs, l'analyse IA (RAG), et la réaction aux menaces en temps réel.
    * Repose sur un bus d'événements (Redis) pour découpler les services.

---

## 3. Arborescence Technique (Structure des Dossiers)

```markdown
/cyber-shield-root
│
├── /backend-protection (Java Spring Boot)
│   ├── /src/main/java/com/cybershield
│   │   ├── /auth          # Service : Authentification (JWT/OAuth2)
│   │   ├── /core          # Service : Gestion des Clients & Configuration
│   │   ├── /polymorph     # Moteur : Chiffrement & Obfuscation de flux
│   │   └── /events        # EDA : Producteur/Consommateur Redis
│   ├── pom.xml
│   └── Dockerfile
│
├── /backend-audit (Python Django)
│   ├── /config            # Configuration du projet Django
│   ├── /api               # Service : API REST pour le Dashboard
│   ├── /scanners          # Moteur : Scripts Nmap, OpenVAS, Analyseurs de logs
│   ├── /intelligence      # Moteur IA : RAG, Base Vectorielle (Normes ISO)
│   ├── /tasks             # EDA : Workers Celery pour tâches de fond
│   ├── requirements.txt
│   └── Dockerfile
│
├── /frontend-dashboard (Angular)
│   ├── /src/app
│   │   ├── /core          # Services HTTP & Auth
│   │   ├── /modules
│   │   │   ├── /dashboard # Vue : Scores, Graphiques
│   │   │   ├── /audit     # Vue : Rapports et Recommandations
│   │   │   ├── /settings  # Vue : Configuration flotte
│   │   └── /shared        # Composants UI réutilisables
│   ├── package.json
│   └── Dockerfile
│
├── /infrastructure
│   ├── /database          # Scripts SQL d'initialisation (PostgreSQL)
│   ├── /broker            # Configuration Redis
│   └── docker-compose.yml # Orchestrateur global
```

## 4. Matrice des Responsabilités

### A. Le Gardien (Backend Protection - Java)
* **Rôle :** Sécurité opérationnelle "Dure".
* **Responsabilités :**
    * Authentification centralisée (OAuth2 Resource Server).
    * Gestion des clés cryptographiques et rotation.
    * Orchestration du **Framework Polymorphe** (bruit, leurres, kill-switch).

### B. L'Auditeur (Backend Audit & IA - Python)
* **Rôle :** Intelligence, Analyse et Conformité.
* **Les 5 Piliers d'Audit :**
    1.  **Réseau :** Scan de ports, protocoles non sécurisés.
    2.  **Matériel :** Inventaire IoT, versions firmware obsolètes.
    3.  **Architecture :** Segmentation réseau, isolation.
    4.  **Bonnes Pratiques :** Mots de passe, 2FA.
    5.  **RGPD :** Détection de données sensibles exposées.
* **Technologie IA :** RAG (Retrieval-Augmented Generation) pour mapper les failles techniques aux textes de loi.

### C. Le Dashboard (Frontend - Angular)
* **Rôle :** Restitution et Pilotage décisionnel.
* **Fonctionnalités :**
    * Visualisation des scores de sécurité (0-100).
    * Cartographie des risques (Heatmap).
    * **Action Center :** Bouton pour déployer la protection Java sur les failles critiques.

---

## 5. Flux de Données (Hybride)

| Type de Flux | Source | Destination | Protocole | Cas d'usage |
| :--- | :--- | :--- | :--- | :--- |
| **Authentification** | Angular | Spring Boot | REST (HTTPS) | Connexion utilisateur (Login). |
| **Commande Scan** | Angular | Django | REST (HTTPS) | L'utilisateur demande un audit. |
| **Exécution Scan** | Django | Worker Scan | Interne/Queue | Lancement des scripts Nmap (Tâche longue). |
| **Résultat Scan** | Worker | IA Engine | Redis (Event) | Fin du scan, envoi des données brutes à l'IA. |
| **Notification** | Spring Boot | Angular | WebSocket | Mise à jour du dashboard (ex: "Audit terminé"). |
| **Alerte Menace** | Agent IoT | Spring Boot | TCP Sécurisé | Détection d'une tentative d'intrusion. |

---

## 6. Stack Infrastructure (Docker)

Le système sera déployé via conteneurs Docker orchestrés par `docker-compose`.

| Service | Image Base | Port Interne | Rôle |
| :--- | :--- | :--- | :--- |
| **PostgreSQL** | `postgres:15-alpine` | 5432 | Base de données relationnelle unique. Stocke Utilisateurs, Logs d'audit, Configs. |
| **Redis** | `redis:7-alpine` | 6379 | Message Broker (Bus d'événements) et Cache rapide pour Celery. |
| **Backend Java** | `openjdk:17-slim` | 8080 | API Spring Boot (Protection). |
| **Backend Python** | `python:3.11-slim` | 8000 | API Django + Workers IA (Audit). |
| **Frontend** | `nginx:alpine` | 80 | Serveur Web pour l'application Angular compilée. |

---

## 7. Modèle de Données (Conceptuel)

Les entités principales à gérer dans la base de données :

* **`AuditSession`** : Un scan complet à un instant T (Date, Client, Score Global).
* **`Asset`** : Un équipement découvert sur le réseau (PC, Robot, Serveur, Caméra).
* **`Vulnerability`** : Une faille technique identifiée sur un Asset (CVE, Port ouvert).
* **`ComplianceRule`** : Une règle normative de référence (ex: "ISO 27001 - A.12.3").
* **`Recommendation`** : L'action corrective proposée par l'IA (ex: "Activer chiffrement polymorphe").