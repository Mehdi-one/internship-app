# Internship App

Ce projet est une premiere structure vide pour mon stage.

Le but pour le moment est de preparer une base avec :

- Angular pour le frontend
- Spring Boot pour le backend
- PostgreSQL pour la base de donnees
- Keycloak pour l'authentification

La logique generale est :

```text
Angular -> Spring Boot API -> PostgreSQL
             |
          Keycloak
```

## Structure du projet

```text
internship-app/
|-- frontend/
|-- backend/
|-- docker-compose.yml
`-- README.md
```

## Frontend

Le dossier `frontend` contient l'application Angular.

Pour lancer le frontend :

```powershell
cd C:\internship-app\frontend
npm install
ng serve
```

Adresse :

```text
http://localhost:4200
```

## Backend

Le dossier `backend` contient l'application Spring Boot.

Pour lancer le backend :

```powershell
cd C:\internship-app\backend
mvn spring-boot:run
```

Endpoint de test :

```text
http://localhost:8081/api/public
```

## Docker

Le fichier `docker-compose.yml` sert a lancer les services externes :

- PostgreSQL
- Keycloak

Commande :

```powershell
cd C:\internship-app
docker compose up -d
```

Pour arreter :

```powershell
docker compose down
```

## Ports utilises

```text
Angular:     4200
Spring Boot: 8081
Keycloak:    8080
PostgreSQL:  5432
```

## Keycloak

Keycloak sera utilise pour gerer la connexion des utilisateurs.

Compte admin local :

```text
username: admin
password: admin
```

Realm a creer :

```text
internship-app
```

Client a creer :

```text
angular-client
```


