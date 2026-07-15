# Deploiement d'un projet JHipster - ADM Supervision Ventes

Ce fichier explique comment deployer cette application JHipster pas a pas. Il est ecrit pour quelqu'un qui n'a jamais fait le build.

Le projet ADM est une application JHipster monolithique :

- Backend : Spring Boot, Java 21, API REST sous `/api`
- Frontend : Angular, compile dans `target/classes/static`
- Base de donnees : PostgreSQL
- Migrations : Liquibase
- Build : Maven + npm
- Image Docker : Jib, sans Dockerfile applicatif
- Port applicatif : `8080`

## 1. Comprendre le fonctionnement JHipster

En developpement, JHipster lance souvent deux serveurs :

- Angular dev server : `http://localhost:4200`
- Spring Boot backend : `http://localhost:8080`

En production, il n'y a plus de serveur Angular separe. Angular est compile en fichiers statiques, puis ces fichiers sont inclus dans l'application Spring Boot. Le serveur Spring Boot sert donc a la fois :

- le frontend Angular
- les API REST
- les endpoints de supervision `/management`

Schema simple :

```text
Navigateur
   |
   | HTTPS
   v
Nginx / reverse proxy
   |
   | HTTP local :8080
   v
Application Spring Boot JHipster
   |
   | JDBC
   v
PostgreSQL
```

## 2. Profils JHipster importants

JHipster utilise des profils Spring.

### Profil `dev`

Utilise en local.

Caracteristiques :

- logs plus verbeux
- devtools actif
- frontend lance separement avec Angular
- base PostgreSQL locale via Docker sur `5433`

Commandes locales :

```powershell
npm run docker:db:up
npm run backend:dev
npm start
```

### Profil `prod`

Utilise pour le deploiement.

Caracteristiques :

- Angular compile en mode production
- Spring Boot sert le frontend compile
- logs moins verbeux
- Liquibase applique les migrations avec le contexte `prod`
- configuration sensible surchargee par variables d'environnement

Profil de production :

```env
SPRING_PROFILES_ACTIVE=prod
```

## 3. Fichiers importants pour le deploiement

| Fichier | Role |
|---|---|
| `pom.xml` | Build Maven, plugins JHipster, Jib, Java |
| `package.json` | Scripts npm, build Angular |
| `angular.json` | Configuration Angular et budgets de production |
| `src/main/resources/config/application.yml` | Configuration commune Spring/JHipster |
| `src/main/resources/config/application-prod.yml` | Configuration production par defaut |
| `src/main/resources/config/liquibase/master.xml` | Liste des migrations Liquibase |
| `src/main/docker/app.yml` | Compose JHipster genere, utile comme base |
| `src/main/docker/postgresql.yml` | PostgreSQL local/dev |
| `.env.production` | Variables de production pour ce projet |

Important : en production, on ne modifie pas directement `application-prod.yml` pour les secrets. On surcharge avec des variables d'environnement.

## 4. Preparer le poste de build

Sur Windows, le projet se build depuis PowerShell.

Verifier Java :

```powershell
java -version
```

Il faut Java 21.

Verifier Node :

```powershell
node -v
npm -v
```

Le projet attend Node `>=24.14.0`.

Installer les dependances :

```powershell
npm ci
```

Si `npm ci` echoue parce que `node_modules` existe deja ou parce que le lock a change, verifier d'abord l'erreur. Ne pas faire `npm audit fix --force` sans controle, car cela peut changer des versions majeures.

## 5. Verifications avant build

### Verification TypeScript

Cette commande verifie le code Angular sans produire de fichiers :

```powershell
npx tsc -p tsconfig.app.json --noEmit
```

Resultat attendu : aucune erreur.

### Build Angular developpement

```powershell
npm run webapp:build:dev
```

Ce build est plus tolerant et utile pour verifier rapidement le frontend.

### Build Angular production

```powershell
npm run webapp:build:prod
```

Ce build minifie, optimise et applique les budgets Angular.

Sortie attendue :

```text
Application bundle generation complete.
Output location: target/classes/static
```

Note : un warning sur `content/css/loading.css` peut apparaitre. Il n'est pas bloquant si le build termine avec succes.

## 6. Comprendre Maven dans JHipster

Maven orchestre le build backend et peut aussi lancer le build frontend.

Sur Linux/macOS, beaucoup de scripts utilisent :

```bash
./mvnw
```

Sur Windows PowerShell, il faut utiliser :

```powershell
.\mvnw.cmd
```

Important PowerShell : les options `-D...` doivent souvent etre entre guillemets.

Exemple correct :

```powershell
.\mvnw.cmd -ntp "-Dmaven.test.skip=true" -Pprod verify
```

## 7. Build JAR JHipster

Le JAR est l'application Spring Boot complete. En profil `prod`, il inclut le frontend Angular compile.

Commande Windows recommandee pour ce projet :

```powershell
.\mvnw.cmd -ntp "-Dmaven.test.skip=true" -Pprod verify
```

Pourquoi `-Dmaven.test.skip=true` ?

- `-DskipTests` saute l'execution des tests mais compile encore les tests.
- Dans ce projet, certains tests Java sont obsoletes et ne compilent pas encore.
- `-Dmaven.test.skip=true` saute la compilation et l'execution des tests.

Quand les tests auront ete corriges, il faudra preferer :

```powershell
.\mvnw.cmd -ntp -Pprod verify
```

Artefact attendu :

```text
target/adm-supervision-ventes-0.0.1-SNAPSHOT.jar
```

## 8. Build Docker JHipster avec Jib

JHipster utilise Jib pour construire une image Docker sans Dockerfile applicatif.

Jib :

- prend le JAR et les classes Java
- ajoute les ressources Angular compilees
- cree une image Docker optimisee en couches
- charge l'image dans Docker Desktop

Commande Windows validee pour ce projet :

```powershell
.\mvnw.cmd -ntp "-Dmaven.test.skip=true" -Pprod jib:dockerBuild
```

Image produite :

```text
admsupervisionventes:latest
```

Verifier l'image :

```powershell
docker images admsupervisionventes
```

Exemple de resultat :

```text
admsupervisionventes   latest   <image_id>   ...   580MB
```

Note : les scripts npm `java:docker:prod` utilisent `./mvnw`, donc ils peuvent echouer sous PowerShell Windows. Sous Windows, utiliser directement `.\mvnw.cmd`.

## 9. Variables d'environnement de production

Le fichier local du projet est :

```text
.env.production
```

Sur le serveur, Docker Compose attend plutot un fichier nomme :

```text
.env
```

Donc au deploiement, on copie le contenu de `.env.production` dans `/opt/adm/.env`.

Variables principales :

```env
POSTGRES_DB=admSupervisionVentes
POSTGRES_USER=admSupervisionVentes
POSTGRES_PASSWORD=mot-de-passe-postgres

APP_IMAGE=admsupervisionventes:latest

SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:postgresql://postgresql:5432/admSupervisionVentes
SPRING_DATASOURCE_USERNAME=admSupervisionVentes
SPRING_DATASOURCE_PASSWORD=mot-de-passe-postgres
SPRING_LIQUIBASE_URL=jdbc:postgresql://postgresql:5432/admSupervisionVentes

JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET=secret-jwt-base64
JHIPSTER_MAIL_BASE_URL=https://votre-domaine.com
APPLICATION_REPORTING_STORAGE_PATH=/data/generated-reports
```

### Generer le secret JWT

Sur Linux :

```bash
openssl rand -base64 64
```

Sur PowerShell :

```powershell
$bytes = New-Object byte[] 64
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
$rng.GetBytes($bytes)
$rng.Dispose()
[Convert]::ToBase64String($bytes)
```

Ce secret doit rester prive. Si on le change, les tokens JWT existants deviennent invalides.

## 10. Preparer le serveur

Serveur recommande :

- Ubuntu 22.04+ ou Debian 12+
- 2 vCPU minimum
- 4 Go RAM minimum
- 20 Go disque minimum
- ports ouverts : `80`, `443`
- domaine pointant vers l'IP du serveur

Installer Docker :

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

Se deconnecter/reconnecter apres `usermod`.

Verifier :

```bash
docker version
docker compose version
```

## 11. Methode de transfert de l'image

Il y a deux facons courantes.

### Option A - Registry Docker

Sur le poste local :

```powershell
docker tag admsupervisionventes:latest registry.example.com/adm/admsupervisionventes:1.0.0
docker push registry.example.com/adm/admsupervisionventes:1.0.0
```

Dans `.env` serveur :

```env
APP_IMAGE=registry.example.com/adm/admsupervisionventes:1.0.0
```

Sur le serveur :

```bash
docker compose pull
```

### Option B - Export direct sans registry

Sur le poste local :

```powershell
docker save admsupervisionventes:latest -o admsupervisionventes.tar
```

Copier le fichier vers le serveur :

```powershell
scp .\admsupervisionventes.tar user@votre-serveur:/opt/adm/
```

Sur le serveur :

```bash
cd /opt/adm
docker load -i admsupervisionventes.tar
docker images admsupervisionventes
```

Cette methode est simple pour apprendre et pour un premier deploiement.

## 12. Creer le dossier de production

Sur le serveur :

```bash
sudo mkdir -p /opt/adm
sudo chown -R $USER:$USER /opt/adm
cd /opt/adm
```

Creer le fichier `.env` :

```bash
nano .env
```

Coller les variables de `.env.production`, puis remplacer :

- domaine
- secret JWT
- SMTP si necessaire
- image Docker si elle vient d'un registry

Proteger le fichier :

```bash
chmod 600 .env
```

## 13. Docker Compose production

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
      SPRING_MAIL_HOST: ${SPRING_MAIL_HOST}
      SPRING_MAIL_PORT: ${SPRING_MAIL_PORT}
      SPRING_MAIL_USERNAME: ${SPRING_MAIL_USERNAME}
      SPRING_MAIL_PASSWORD: ${SPRING_MAIL_PASSWORD}
      SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH: ${SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH}
      SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE: ${SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE}
      APPLICATION_REPORTING_STORAGE_PATH: ${APPLICATION_REPORTING_STORAGE_PATH}
    volumes:
      - reports_data:/data/generated-reports
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

Pourquoi `127.0.0.1:8080:8080` ?

- L'application n'est pas exposee directement sur Internet.
- Seul Nginx, sur le meme serveur, peut y acceder.
- Le public passe par HTTPS sur `443`.

## 14. Premier demarrage

Sur le serveur :

```bash
cd /opt/adm
docker compose up -d postgresql
docker compose ps
docker compose logs -f postgresql
```

Quand PostgreSQL est `healthy`, lancer l'application :

```bash
docker compose up -d app
docker compose logs -f app
```

Au premier demarrage, JHipster lance Liquibase. Liquibase cree les tables et applique les migrations.

Verifier la sante :

```bash
curl http://127.0.0.1:8080/management/health
```

Resultat attendu :

```json
{"status":"UP"}
```

Selon la securite, la reponse peut etre plus courte ou masquer les details. Le point important est `UP`.

## 15. Installer Nginx et HTTPS

Installer :

```bash
sudo apt install -y nginx certbot python3-certbot-nginx
```

Creer la config :

```bash
sudo nano /etc/nginx/sites-available/adm
```

Contenu :

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

Demander le certificat HTTPS :

```bash
sudo certbot --nginx -d votre-domaine.com
```

Verifier le renouvellement automatique :

```bash
sudo certbot renew --dry-run
```

## 16. Verification fonctionnelle apres deploiement

Ouvrir :

```text
https://votre-domaine.com
```

Verifier :

- page de login accessible
- connexion admin
- dashboard
- creation boutique
- creation locataire
- creation manager ADM
- creation manager boutique/vendeur si utilisee
- changement de mot de passe a la premiere connexion
- endpoints health

Health local serveur :

```bash
curl http://127.0.0.1:8080/management/health
```

Logs :

```bash
docker compose logs -f app
```

## 17. Mise a jour de l'application

Sur le poste local :

```powershell
npx tsc -p tsconfig.app.json --noEmit
npm run webapp:build:prod
.\mvnw.cmd -ntp "-Dmaven.test.skip=true" -Pprod jib:dockerBuild
```

Si transfert direct :

```powershell
docker save admsupervisionventes:latest -o admsupervisionventes.tar
scp .\admsupervisionventes.tar user@votre-serveur:/opt/adm/
```

Sur le serveur :

```bash
cd /opt/adm
docker compose down app
docker load -i admsupervisionventes.tar
docker compose up -d app
docker compose logs -f app
```

Si registry :

```bash
cd /opt/adm
docker compose pull app
docker compose up -d app
docker compose logs -f app
```

## 18. Rollback

Garder toujours l'image precedente.

Si registry :

```env
APP_IMAGE=registry.example.com/adm/admsupervisionventes:ancienne-version
```

Puis :

```bash
docker compose pull app
docker compose up -d app
```

Attention : si une migration Liquibase a modifie la base de facon non reversible, revenir a l'image precedente peut ne pas suffire. Faire une sauvegarde PostgreSQL avant chaque mise a jour importante.

## 19. Sauvegarde PostgreSQL

Sauvegarde :

```bash
cd /opt/adm
mkdir -p backups
docker compose exec -T postgresql pg_dump -U "$POSTGRES_USER" "$POSTGRES_DB" > "backups/adm-$(date +%Y%m%d-%H%M%S).sql"
```

Restauration complete :

```bash
cd /opt/adm
docker compose down
docker volume rm adm_postgres_data
docker compose up -d postgresql
cat backups/adm-YYYYMMDD-HHMMSS.sql | docker compose exec -T postgresql psql -U "$POSTGRES_USER" "$POSTGRES_DB"
docker compose up -d app
```

## 20. Commandes de diagnostic

Voir les conteneurs :

```bash
docker compose ps
```

Voir les logs app :

```bash
docker compose logs -f app
```

Voir les logs PostgreSQL :

```bash
docker compose logs -f postgresql
```

Entrer dans PostgreSQL :

```bash
docker compose exec postgresql psql -U "$POSTGRES_USER" "$POSTGRES_DB"
```

Voir les migrations Liquibase :

```bash
docker compose exec postgresql psql -U "$POSTGRES_USER" "$POSTGRES_DB" -c "select id, author, dateexecuted from databasechangelog order by dateexecuted desc limit 20;"
```

Voir les images :

```bash
docker images
```

Nettoyer les anciennes images inutilisees :

```bash
docker image prune
```

## 21. Points de securite

Avant ouverture publique :

- Ne pas utiliser `POSTGRES_HOST_AUTH_METHOD=trust`.
- Utiliser un vrai mot de passe PostgreSQL.
- Generer un vrai secret JWT.
- Ne pas committer les vrais secrets dans Git.
- Mettre `JHIPSTER_MAIL_BASE_URL=https://votre-domaine.com`.
- Garder PostgreSQL non expose publiquement.
- Exposer l'application seulement via Nginx HTTPS.
- Proteger `/opt/adm/.env` avec `chmod 600`.
- Sauvegarder PostgreSQL avant chaque mise a jour.
- Tester la premiere connexion et le changement de mot de passe.

## 22. Checklist simple pour ce projet

Sur le poste local :

```powershell
npm ci
npx tsc -p tsconfig.app.json --noEmit
npm run webapp:build:prod
.\mvnw.cmd -ntp "-Dmaven.test.skip=true" -Pprod jib:dockerBuild
docker images admsupervisionventes
```

Sur le serveur :

```bash
mkdir -p /opt/adm
cd /opt/adm
nano .env
nano docker-compose.yml
docker compose up -d postgresql
docker compose up -d app
docker compose logs -f app
curl http://127.0.0.1:8080/management/health
```

Puis :

```bash
sudo apt install -y nginx certbot python3-certbot-nginx
sudo nano /etc/nginx/sites-available/adm
sudo nginx -t
sudo systemctl reload nginx
sudo certbot --nginx -d votre-domaine.com
```

## 23. Etat actuel connu

Dernieres validations effectuees dans ce projet :

- TypeScript : OK
- Build Angular dev : OK
- Build Angular prod : OK
- Compilation backend sans tests : OK
- Image Docker Jib : OK avec `.\mvnw.cmd -ntp "-Dmaven.test.skip=true" -Pprod jib:dockerBuild`

Point technique restant :

- Les tests Java ne compilent pas encore avec `-DskipTests`, car certains tests sont obsoletes. Pour produire l'artefact de deploiement, utiliser temporairement `-Dmaven.test.skip=true`.

## 24. Deploiement reel effectue sur le serveur ADM

Cette section reprend les etapes effectivement realisees pour deployer ADM sur le serveur existant.

### Informations serveur

| Element | Valeur |
|---|---|
| IP serveur | `187.77.178.216` |
| Utilisateur SSH | `craadmin` |
| Systeme | Ubuntu 24.04.4 LTS |
| Docker | Deja installe |
| Docker Compose | Deja installe |
| Dossier des applications | `/home/craadmin/applications` |
| Dossier ADM | `/home/craadmin/applications/adm-supervision-ventes` |
| Image Docker ADM | `admsupervisionventes:latest` |
| Port serveur choisi | `8094` |
| Port interne application | `8080` |
| URL publique temporaire | `http://187.77.178.216:8094` |

### 1. Connexion au serveur

Depuis PowerShell sur le poste local :

```powershell
ssh craadmin@187.77.178.216
```

Le mot de passe est demande ensuite :

```text
craadmin@187.77.178.216's password:
```

Verifier que l'on est bien connecte :

```bash
whoami
```

Resultat attendu :

```text
craadmin
```

### 2. Verifier Docker et les applications existantes

Sur le serveur :

```bash
docker --version
docker compose version
docker ps
```

Objectif :

- confirmer que Docker est disponible ;
- voir les applications deja lancees ;
- reperer les ports deja utilises.

Lors du deploiement ADM, les ports deja utilises incluaient notamment :

```text
8090, 8091, 8093, 8095, 8096, 8098
3000, 4201, 4400, 4500, 5678, 6333
```

Le port `8094` etait libre.

Verification :

```bash
sudo ss -tulpn | grep 8094
```

Si la commande ne retourne rien, le port est libre.

### 3. Reperer l'organisation serveur

Les applications existantes etaient dans :

```bash
/home/craadmin/applications
```

Commande utilisee :

```bash
find /home/craadmin -maxdepth 3 -name "docker-compose*.yml" -o -name "compose*.yml"
```

Exemples trouves :

```text
/home/craadmin/applications/craa_app/docker-compose-appcraa-prod.yml
/home/craadmin/applications/checking-app/docker-compose.yml
/home/craadmin/applications/apej_app/docker-compose.yml
```

ADM suit donc la meme organisation.

### 4. Creer le dossier ADM sur le serveur

Sur le serveur :

```bash
mkdir -p /home/craadmin/applications/adm-supervision-ventes
cd /home/craadmin/applications/adm-supervision-ventes
pwd
```

Resultat attendu :

```text
/home/craadmin/applications/adm-supervision-ventes
```

### 5. Exporter l'image Docker depuis le poste local

Sur le poste local Windows, dans le dossier du projet :

```powershell
cd C:\Users\ibrah\Music\DEV2026\ADM
```

Verifier que l'image existe :

```powershell
docker images admsupervisionventes
```

Image attendue :

```text
admsupervisionventes:latest
```

Exporter l'image en fichier `.tar` :

```powershell
docker save admsupervisionventes:latest -o adm-supervision-ventes.tar
```

Verifier que le fichier existe :

```powershell
dir adm-supervision-ventes.tar
```

Important : ce fichier est un artefact de transfert. Il ne doit pas etre committe dans Git.

### 6. Copier l'image vers le serveur

Depuis PowerShell local :

```powershell
scp adm-supervision-ventes.tar craadmin@187.77.178.216:/home/craadmin/applications/adm-supervision-ventes/
```

### 7. Charger l'image Docker sur le serveur

Sur le serveur :

```bash
cd /home/craadmin/applications/adm-supervision-ventes
docker load -i adm-supervision-ventes.tar
docker images | grep admsupervisionventes
```

Resultat obtenu :

```text
Loaded image: admsupervisionventes:latest
admsupervisionventes:latest
```

### 8. Creer le fichier Docker Compose serveur

Sur le serveur :

```bash
cd /home/craadmin/applications/adm-supervision-ventes
nano docker-compose.yml
```

Contenu utilise :

```yaml
services:
  adm-postgresql:
    image: postgres:17.4
    container_name: adm-postgresql
    environment:
      POSTGRES_DB: adm_supervision_ventes
      POSTGRES_USER: adm_user
      POSTGRES_PASSWORD: CHANGE_ME_POSTGRES_PASSWORD
    volumes:
      - adm_postgresql_data:/var/lib/postgresql/data
    networks:
      - adm-network
    restart: unless-stopped

  adm-app:
    image: admsupervisionventes:latest
    container_name: adm-supervision-ventes
    depends_on:
      - adm-postgresql
    environment:
      SPRING_PROFILES_ACTIVE: prod
      SPRING_DATASOURCE_URL: jdbc:postgresql://adm-postgresql:5432/adm_supervision_ventes
      SPRING_DATASOURCE_USERNAME: adm_user
      SPRING_DATASOURCE_PASSWORD: CHANGE_ME_POSTGRES_PASSWORD
      JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET: "SECRET_JWT_A_GENERER"
      SERVER_PORT: 8080
    ports:
      - "8094:8080"
    networks:
      - adm-network
    restart: unless-stopped

volumes:
  adm_postgresql_data:

networks:
  adm-network:
    driver: bridge
```

Note securite : remplacer `CHANGE_ME_POSTGRES_PASSWORD` par le vrai mot de passe uniquement sur le serveur. Dans une version plus propre, les secrets doivent etre sortis du `docker-compose.yml` et places dans un fichier `.env` serveur protege par `chmod 600`.

### 9. Generer le secret JWT JHipster

Le premier lancement a echoue avec cette erreur :

```text
Could not resolve placeholder 'jhipster.security.authentication.jwt.base64-secret'
```

Cause : en profil `prod`, JHipster exige un secret JWT.

Sur le serveur :

```bash
openssl rand -base64 64
```

Copier la valeur generee dans le compose :

```yaml
JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET: "VALEUR_GENEREE_PAR_OPENSSL"
```

Ce secret doit rester prive. Si on le change, les utilisateurs devront se reconnecter car les anciens tokens JWT ne seront plus valides.

### 10. Lancer ADM

Sur le serveur :

```bash
cd /home/craadmin/applications/adm-supervision-ventes
docker compose up -d
```

Verifier les conteneurs :

```bash
docker compose ps
```

Conteneurs attendus :

```text
adm-postgresql
adm-supervision-ventes
```

Voir les logs :

```bash
docker logs -f adm-supervision-ventes
```

Message attendu :

```text
Application 'admSupervisionVentes' is running! Access URLs:
Local:    http://localhost:8080/
External: http://172.x.x.x:8080/
Profile(s): [prod]
```

Attention : les URLs affichees par Spring Boot sont internes au conteneur. Depuis le navigateur, il faut utiliser le port expose par Docker.

### 11. Tester dans le navigateur

URL testee avec succes :

```text
http://187.77.178.216:8094
```

Si la page de login ADM s'affiche, le deploiement est fonctionnel.

### 12. Commandes utiles apres deploiement

Aller dans le dossier ADM :

```bash
cd /home/craadmin/applications/adm-supervision-ventes
```

Voir l'etat :

```bash
docker compose ps
```

Voir les logs :

```bash
docker logs -f adm-supervision-ventes
```

Arreter ADM :

```bash
docker compose down
```

Redemarrer ADM :

```bash
docker compose up -d
```

### 13. Voir les donnees PostgreSQL

Entrer dans PostgreSQL :

```bash
docker exec -it adm-postgresql psql -U adm_user -d adm_supervision_ventes
```

Lister les tables :

```sql
\dt
```

Voir les utilisateurs :

```sql
SELECT * FROM jhi_user;
```

Quitter PostgreSQL :

```sql
\q
```

### 14. Vider completement la base ADM

Attention : cette commande supprime toutes les donnees ADM.

```bash
cd /home/craadmin/applications/adm-supervision-ventes
docker compose down -v
docker compose up -d
docker logs -f adm-supervision-ventes
```

Explication :

- `docker compose down -v` supprime les conteneurs et le volume PostgreSQL ADM ;
- `docker compose up -d` recree PostgreSQL et l'application ;
- Liquibase recree automatiquement les tables au demarrage.

### 15. Points a ameliorer apres ce premier deploiement

- Mettre les secrets dans un fichier `.env` serveur au lieu de les laisser dans `docker-compose.yml`.
- Proteger ce fichier avec `chmod 600 .env`.
- Ajouter un reverse proxy Nginx avec HTTPS.
- Associer un nom de domaine.
- Mettre `JHIPSTER_MAIL_BASE_URL` avec l'URL finale publique.
- Configurer SMTP si les emails de reinitialisation doivent partir reellement.
- Prevoir une sauvegarde PostgreSQL avant les mises a jour.

### 16. Mettre a jour ADM apres une modification locale

Quand le code est modifie sur le poste local, il faut reconstruire une nouvelle image Docker, l'envoyer au serveur, puis redemarrer uniquement le service applicatif.

Important : une mise a jour normale ne doit pas supprimer le volume PostgreSQL. Ne pas utiliser `docker compose down -v`, sauf si l'objectif est de vider completement la base.

#### Etape A - Tester et reconstruire sur le poste local

Dans PowerShell :

```powershell
cd C:\Users\ibrah\Music\DEV2026\ADM
```

Verifier TypeScript :

```powershell
node node_modules\typescript\bin\tsc -p tsconfig.app.json --noEmit
```

Construire l'image Docker JHipster avec Jib :

```powershell
.\mvnw.cmd -ntp "-Dmaven.test.skip=true" -Pprod jib:dockerBuild
```

Verifier que l'image existe :

```powershell
docker images admsupervisionventes
```

Exporter l'image en archive Docker :

```powershell
docker save admsupervisionventes:latest -o adm-supervision-ventes.tar
```

Envoyer l'archive sur le serveur :

```powershell
scp adm-supervision-ventes.tar craadmin@187.77.178.216:/home/craadmin/applications/adm-supervision-ventes/
```

#### Etape B - Charger et relancer sur le serveur

Se connecter au serveur :

```powershell
ssh craadmin@187.77.178.216
```

Aller dans le dossier ADM :

```bash
cd /home/craadmin/applications/adm-supervision-ventes
```

Charger la nouvelle image :

```bash
docker load -i adm-supervision-ventes.tar
```

Redemarrer uniquement l'application :

```bash
docker compose up -d adm-app
```

Verifier l'etat :

```bash
docker compose ps
```

Verifier les logs :

```bash
docker logs --tail=80 adm-supervision-ventes
```

Message attendu :

```text
Started AdmSupervisionVentesApp
Application 'admSupervisionVentes' is running
```

Tester dans le navigateur :

```text
http://187.77.178.216:8094
```

#### Commandes a eviter pendant une mise a jour normale

Ne pas faire :

```bash
docker compose down -v
```

Pourquoi : l'option `-v` supprime les volumes Docker, donc le volume PostgreSQL `adm_postgresql_data`. Cela efface toutes les donnees ADM.

Pour une mise a jour applicative simple, utiliser :

```bash
docker compose up -d adm-app
```

La base reste intacte.
