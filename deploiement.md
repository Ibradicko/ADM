# Deploiement ADM Supervision Ventes

Ce guide decrit les etapes pour deployer l'application ADM en ligne avec Docker, PostgreSQL et un reverse proxy HTTPS.

## 1. Architecture recommandee

- Application : monolithe JHipster Spring Boot + Angular, port interne `8080`
- Base de donnees : PostgreSQL
- Migrations : Liquibase au demarrage de l'application
- Image applicative : construite avec Maven/Jib
- Exposition web : Nginx ou Traefik en reverse proxy HTTPS vers `app:8080`
- Stockage persistant :
  - volume PostgreSQL
  - repertoire des exports/reporting si utilise en production

## 2. Prerequis serveur

Serveur Linux recommande :

- Ubuntu 22.04+ ou Debian 12+
- 2 vCPU minimum
- 4 Go RAM minimum
- 20 Go disque minimum
- Nom de domaine pointant vers le serveur
- Ports ouverts : `80`, `443`

Installer Docker et Docker Compose :

```bash
sudo apt update
sudo apt install -y ca-certificates curl gnupg
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
sudo chmod a+r /etc/apt/keyrings/docker.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
sudo usermod -aG docker $USER
```

Se reconnecter ensuite au serveur pour appliquer le groupe `docker`.

## 3. Verification locale avant de deployer

Depuis la racine du projet :

```bash
npm ci
npx tsc -p tsconfig.app.json --noEmit
npm run webapp:build:dev
npm run java:jar:prod
```

Optionnel mais recommande :

```bash
npm run lint
npm test
npm run backend:unit:test
```

## 4. Construire l'image Docker

Depuis la racine du projet :

```bash
npm run java:docker:prod
```

L'image locale produite est :

```text
admsupervisionventes:latest
```

Pour une architecture ARM64 :

```bash
npm run java:docker:prod -- -Djib-maven-plugin.architecture=arm64
```

Pour publier vers un registry, tagger puis pousser :

```bash
docker tag admsupervisionventes:latest registry.example.com/adm/admsupervisionventes:1.0.0
docker push registry.example.com/adm/admsupervisionventes:1.0.0
```

## 5. Variables d'environnement obligatoires

Ne pas garder les valeurs de developpement en production.

Variables principales :

```env
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:postgresql://postgresql:5432/admSupervisionVentes
SPRING_DATASOURCE_USERNAME=admSupervisionVentes
SPRING_DATASOURCE_PASSWORD=changer-ce-mot-de-passe
SPRING_LIQUIBASE_URL=jdbc:postgresql://postgresql:5432/admSupervisionVentes
JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET=generer-une-cle-base64-forte
JHIPSTER_MAIL_BASE_URL=https://votre-domaine.com
SPRING_MAIL_HOST=smtp.example.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=utilisateur-smtp
SPRING_MAIL_PASSWORD=mot-de-passe-smtp
SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH=true
SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE=true
APPLICATION_REPORTING_STORAGE_PATH=/data/generated-reports
```

Generer une cle JWT forte :

```bash
openssl rand -base64 64
```

## 6. Fichier `.env` production

Sur le serveur :

```bash
mkdir -p /opt/adm
cd /opt/adm
nano .env
```

Exemple :

```env
POSTGRES_DB=admSupervisionVentes
POSTGRES_USER=admSupervisionVentes
POSTGRES_PASSWORD=changer-ce-mot-de-passe

APP_IMAGE=registry.example.com/adm/admsupervisionventes:1.0.0
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:postgresql://postgresql:5432/admSupervisionVentes
SPRING_DATASOURCE_USERNAME=admSupervisionVentes
SPRING_DATASOURCE_PASSWORD=changer-ce-mot-de-passe
SPRING_LIQUIBASE_URL=jdbc:postgresql://postgresql:5432/admSupervisionVentes
JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET=remplacer-par-la-cle-openssl
JHIPSTER_MAIL_BASE_URL=https://votre-domaine.com
APPLICATION_REPORTING_STORAGE_PATH=/data/generated-reports
```

Proteger le fichier :

```bash
chmod 600 .env
```

## 7. Docker Compose production

Creer `/opt/adm/docker-compose.yml` :

```yaml
name: adm

services:
  postgresql:
    image: postgres:18.3
    restart: unless-stopped
    environment:
      POSTGRES_DB: ${POSTGRES_DB}
      POSTGRES_USER: ${POSTGRES_USER}
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U $${POSTGRES_USER} -d $${POSTGRES_DB}"]
      interval: 10s
      timeout: 5s
      retries: 10

  app:
    image: ${APP_IMAGE}
    restart: unless-stopped
    depends_on:
      postgresql:
        condition: service_healthy
    environment:
      _JAVA_OPTIONS: "-Xms512m -Xmx1024m"
      SPRING_PROFILES_ACTIVE: ${SPRING_PROFILES_ACTIVE}
      SPRING_DATASOURCE_URL: ${SPRING_DATASOURCE_URL}
      SPRING_DATASOURCE_USERNAME: ${SPRING_DATASOURCE_USERNAME}
      SPRING_DATASOURCE_PASSWORD: ${SPRING_DATASOURCE_PASSWORD}
      SPRING_LIQUIBASE_URL: ${SPRING_LIQUIBASE_URL}
      JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET: ${JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET}
      JHIPSTER_MAIL_BASE_URL: ${JHIPSTER_MAIL_BASE_URL}
      APPLICATION_REPORTING_STORAGE_PATH: ${APPLICATION_REPORTING_STORAGE_PATH}
    volumes:
      - reports_data:/data/generated-reports
    expose:
      - "8080"
    ports:
      - "127.0.0.1:8080:8080"
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/management/health"]
      interval: 10s
      timeout: 5s
      retries: 40

volumes:
  postgres_data:
  reports_data:
```

Demarrer :

```bash
docker compose pull
docker compose up -d
docker compose ps
docker compose logs -f app
```

Verifier :

```bash
curl http://127.0.0.1:8080/management/health
```

La reponse doit contenir `UP`.

## 8. Reverse proxy HTTPS avec Nginx

Installer Nginx et Certbot :

```bash
sudo apt install -y nginx certbot python3-certbot-nginx
```

Creer `/etc/nginx/sites-available/adm` :

```nginx
server {
    listen 80;
    server_name votre-domaine.com;

    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

Activer :

```bash
sudo ln -s /etc/nginx/sites-available/adm /etc/nginx/sites-enabled/adm
sudo nginx -t
sudo systemctl reload nginx
```

Activer HTTPS :

```bash
sudo certbot --nginx -d votre-domaine.com
```

Verifier le renouvellement :

```bash
sudo certbot renew --dry-run
```

## 9. Premiere mise en production

Ordre conseille :

```bash
cd /opt/adm
docker compose pull
docker compose up -d postgresql
docker compose logs -f postgresql
docker compose up -d app
docker compose logs -f app
```

Liquibase cree et met a jour le schema automatiquement au demarrage de l'application avec le profil `prod`.

Verifier les endpoints :

```bash
curl http://127.0.0.1:8080/management/health
curl https://votre-domaine.com/management/health
```

Puis ouvrir :

```text
https://votre-domaine.com
```

## 10. Mise a jour applicative

Sur le poste de build :

```bash
npm ci
npx tsc -p tsconfig.app.json --noEmit
npm run webapp:build:dev
npm run java:docker:prod
docker tag admsupervisionventes:latest registry.example.com/adm/admsupervisionventes:1.0.1
docker push registry.example.com/adm/admsupervisionventes:1.0.1
```

Sur le serveur :

```bash
cd /opt/adm
nano .env
```

Changer :

```env
APP_IMAGE=registry.example.com/adm/admsupervisionventes:1.0.1
```

Puis :

```bash
docker compose pull app
docker compose up -d app
docker compose logs -f app
```

## 11. Rollback

Revenir a l'image precedente dans `.env` :

```env
APP_IMAGE=registry.example.com/adm/admsupervisionventes:1.0.0
```

Puis :

```bash
docker compose pull app
docker compose up -d app
```

Attention : si une migration Liquibase destructive a deja ete appliquee, le rollback applicatif seul peut ne pas suffire. Faire une sauvegarde de base avant chaque mise a jour importante.

## 12. Sauvegarde PostgreSQL

Sauvegarde manuelle :

```bash
cd /opt/adm
mkdir -p backups
docker compose exec -T postgresql pg_dump -U "$POSTGRES_USER" "$POSTGRES_DB" > "backups/adm-$(date +%Y%m%d-%H%M%S).sql"
```

Restauration :

```bash
cd /opt/adm
docker compose down
docker volume rm adm_postgres_data
docker compose up -d postgresql
cat backups/adm-YYYYMMDD-HHMMSS.sql | docker compose exec -T postgresql psql -U "$POSTGRES_USER" "$POSTGRES_DB"
docker compose up -d app
```

## 13. Logs et diagnostic

Etat des services :

```bash
docker compose ps
```

Logs application :

```bash
docker compose logs -f app
```

Logs base :

```bash
docker compose logs -f postgresql
```

Healthcheck :

```bash
curl http://127.0.0.1:8080/management/health
```

Verifier les migrations Liquibase :

```bash
docker compose exec postgresql psql -U "$POSTGRES_USER" "$POSTGRES_DB" -c "select id, author, dateexecuted from databasechangelog order by dateexecuted desc limit 20;"
```

## 14. Securite minimale

- Ne jamais utiliser `POSTGRES_HOST_AUTH_METHOD=trust` en production.
- Definir un vrai `POSTGRES_PASSWORD`.
- Definir `JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET`.
- Forcer HTTPS via Nginx/Certbot.
- Garder PostgreSQL non expose publiquement.
- Garder l'application exposee localement sur `127.0.0.1:8080` si Nginx est sur le meme serveur.
- Proteger `.env` avec `chmod 600`.
- Sauvegarder la base avant chaque mise a jour.
- Verifier les comptes admin apres la premiere connexion et changer les mots de passe temporaires.

## 15. Commandes utiles du projet

Developpement local :

```bash
npm run docker:db:up
npm run backend:dev
npm start
```

Build frontend :

```bash
npm run webapp:build:dev
npm run webapp:build:prod
```

Build JAR :

```bash
npm run java:jar:prod
```

Build image Docker :

```bash
npm run java:docker:prod
```

Compose local fourni par le projet :

```bash
npm run app:up
```

## 16. Checklist avant ouverture publique

- Domaine pointe vers le serveur.
- Certificat HTTPS actif.
- `.env` production rempli et protege.
- PostgreSQL persistant via volume.
- `SPRING_PROFILES_ACTIVE=prod`.
- `JHIPSTER_MAIL_BASE_URL=https://votre-domaine.com`.
- Secret JWT genere avec `openssl rand -base64 64`.
- Healthcheck `UP`.
- Connexion admin testee.
- Creation manager ADM testee.
- Creation locataire/boutique testee.
- Sauvegarde initiale creee.
