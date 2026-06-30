# admSupervisionVentes

## Test Scanner USB Codes-Barres

Un scanner USB standard doit etre configure en mode clavier HID avec `Entree` comme suffixe.

1. Tester le scanner dans un editeur de texte : un scan doit saisir un seul code puis un retour a la ligne.
2. En caisse, selectionner la boutique, scanner un code connu deux fois et verifier que la quantite passe a `2`.
3. Scanner un code inconnu : ADM doit enregistrer le scan et proposer l'affectation uniquement aux profils autorises.
4. Tester les workflows stock avec le champ `codeBarres` :
   - `POST /api/reception-produits/{id}/scan`
   - `POST /api/inventaire-stocks/{id}/scan`
   - `POST /api/transfert-stocks/{id}/scan`
5. Verifier qu'un second code principal actif pour le meme produit est refuse.
6. Modifier un code existant et verifier l'historique de recodification.
7. Depuis l'action `Etiquettes`, generer un lot et controler la quantite et le statut d'impression.

Si des caracteres parasites apparaissent, aligner la disposition clavier du scanner sur celle du poste et desactiver les prefixes/suffixes autres que `Entree`.

Cette application a été générée avec JHipster 9.0.0. La documentation et l'aide sont disponibles à l'adresse [https://www.jhipster.tech/documentation-archive/v9.0.0](https://www.jhipster.tech/documentation-archive/v9.0.0).

## Structure Du Projet

Node est requis pour la génération et recommandé pour le développement. Le fichier `package.json` est toujours généré afin d'améliorer l'expérience de développement avec Prettier, les hooks de commit, les scripts, etc.

À la racine du projet, JHipster génère les fichiers de configuration d'outils connus comme Git, Prettier, ESLint et Husky. Leur documentation est disponible sur le Web.

La structure de `/src/*` suit la structure Java standard.

- `.yo-rc.json` - fichier de configuration Yeoman.
  La configuration JHipster est stockée dans ce fichier sous la clé `generator-jhipster`. Des clés `generator-jhipster-*` peuvent être présentes pour configurer des blueprints spécifiques.
- `.yo-resolve` (facultatif) - résolveur de conflits Yeoman.
  Il permet d'appliquer une action précise lorsqu'un conflit est détecté, sans afficher de question pour les fichiers correspondant à un motif. Chaque ligne respecte le format `[motif] [action]`, où le motif est un motif [Minimatch](https://github.com/isaacs/minimatch#minimatch) et l'action vaut `skip` (valeur par défaut si elle est omise) ou `force`. Les lignes commençant par `#` sont des commentaires et sont ignorées.
- `.jhipster/*.json` - fichiers de configuration des entités JHipster.

- `npmw` - wrapper permettant d'utiliser la version de npm installée localement.
  Par défaut, JHipster installe Node et npm localement à l'aide de l'outil de build. Ce wrapper vérifie que npm est installé localement et l'utilise afin d'éviter les différences liées aux versions. En utilisant `./npmw` à la place de la commande `npm` habituelle, il est possible de configurer un environnement sans installation globale de Node pour développer ou tester l'application.
- `/src/main/docker` - configurations Docker de l'application et des services dont elle dépend.

## Développement

Le système de build installe automatiquement les versions recommandées de Node et npm.

Un wrapper est fourni pour lancer npm.
Cette commande est nécessaire uniquement lorsque les dépendances du fichier [package.json](package.json) changent.

```bash
./npmw install
```

Le projet utilise les scripts npm et [Angular CLI](https://angular.dev/tools/cli), avec esbuild comme système de build.

Exécutez les commandes suivantes dans deux terminaux distincts. Le navigateur s'actualisera automatiquement lorsque les fichiers seront modifiés.

```bash
./npmw run backend:start
./npmw run start
```

Npm sert également à gérer les dépendances CSS et JavaScript de l'application. Pour mettre à niveau une dépendance, indiquez une version plus récente dans [package.json](package.json). Les commandes `./npmw update` et `./npmw install` permettent aussi de gérer les dépendances.
Ajoutez l'option `help` à une commande pour afficher son mode d'emploi, par exemple `./npmw help update`.

La commande `./npmw run` affiche tous les scripts disponibles dans le projet.

## Demarrage Local Rapide

Pour un demarrage de travail sous Windows avec la configuration Docker/PostgreSQL actuellement utilisee dans le projet, lancez les commandes depuis la racine du projet:

```powershell
cd C:\Users\ibrah\Music\DEV2026\ADM
```

Demarrer Docker Desktop:

```powershell
Start-Process "$Env:ProgramFiles\Docker\Docker\Docker Desktop.exe"
```

Attendez que Docker Desktop soit totalement demarre, puis lancez PostgreSQL:

```powershell
cmd /c npm.cmd run docker:db:up
```

Lancer pgAdmin:

```powershell
node scripts\docker-cli.mjs run --name adm-pgadmin --network admsupervisionventes_default -p 127.0.0.1:5050:80 -e PGADMIN_DEFAULT_EMAIL=admin@admin.com -e PGADMIN_DEFAULT_PASSWORD=admin -d dpage/pgadmin4
```

Si le conteneur pgAdmin existe deja, relancez-le avec:

```powershell
node scripts\docker-cli.mjs start adm-pgadmin
```

Connexion pgAdmin:

- URL: `http://localhost:5050`
- Email: `admin@admin.com`
- Mot de passe: `admin`

Ajouter le serveur PostgreSQL dans pgAdmin avec ces parametres:

- Host: `postgresql`
- Port: `5432`
- Database: `admSupervisionVentes`
- Username: `admSupervisionVentes`
- Password: laisser vide, ou mettre n'importe quelle valeur si pgAdmin refuse le champ vide

Lancer le backend dans un terminal:

```powershell
cd C:\Users\ibrah\Music\DEV2026\ADM
cmd /c npm.cmd run backend:dev
```

Si Docker Desktop ne demarre pas mais PostgreSQL 17 est installe localement, utiliser une instance PostgreSQL dediee au projet sur `5433`:

```powershell
if (-not (Test-Path target\pgdata\PG_VERSION)) {
  New-Item -ItemType Directory -Force target\pgdata | Out-Null
  & 'C:\Program Files\PostgreSQL\17\bin\initdb.exe' -D target\pgdata -U admSupervisionVentes --auth=trust --encoding=UTF8 --locale=C
}

& 'C:\Program Files\PostgreSQL\17\bin\pg_ctl.exe' -D target\pgdata -l target\pg-local.log -o "-p 5433 -h 127.0.0.1" start
& 'C:\Program Files\PostgreSQL\17\bin\createdb.exe' -h 127.0.0.1 -p 5433 -U admSupervisionVentes admSupervisionVentes
```

Lancer le frontend Angular dans un deuxieme terminal:

```powershell
cd C:\Users\ibrah\Music\DEV2026\ADM
cmd /c npm.cmd run start
```

Les scripts Docker du projet utilisent `scripts/docker-cli.mjs`. Par defaut, ce wrapper garde la configuration Docker normale afin de conserver le contexte Docker Desktop `desktop-linux`.

Si vous devez contourner une erreur Windows du type `C:\Users\<utilisateur>\.docker\config.json: Access is denied`, vous pouvez forcer une configuration Docker locale dans `.docker-codex`:

```powershell
$env:ADM_DOCKER_ISOLATED_CONFIG='1'
cmd /c npm.cmd run docker:db:up
```

Si Docker affiche `failed to connect to the docker API at npipe:////./pipe/docker_engine` ou `npipe:////./pipe/dockerDesktopLinuxEngine`, demarrez Docker Desktop puis attendez que l'icone indique que Docker tourne. Si l'erreur persiste, ouvrez PowerShell en administrateur et lancez:

```powershell
sc.exe start com.docker.service
wsl --shutdown
```

Relancez ensuite Docker Desktop, puis `cmd /c npm.cmd run docker:db:up`.

Acces utiles:

- Frontend Angular: `http://localhost:4200`
- Backend Spring Boot: `http://localhost:8080`
- PostgreSQL Docker: `localhost:5433`
- pgAdmin: `http://localhost:5050`

## Identifiants De Test Existants

Les comptes de connexion garantis par les seeds Liquibase du projet sont:

- `admin` / `admin`
  - autorites: `ROLE_ADMIN`, `ROLE_USER`
  - usage recommande: recette complete, parametrage, verification des modules metier
- `user` / `user`
  - autorite: `ROLE_USER`
  - usage recommande: verification d'un acces restreint de base
- `manager-adm` / `user`
  - profil metier: `MANAGER_ADM`
  - usage recommande: supervision globale, redevances, reporting, audit, utilisateurs et parametrage fonctionnel
- `manager-boutique` / `user`
  - profil metier: `MANAGER_BOUTIQUE`
  - usage recommande: pilotage boutique, caisse, stock, rapports et gestion des utilisateurs de sa boutique
- `vendeur` / `user`
  - profil metier: `VENDEUR`
  - usage recommande: poste caisse et operations de vente
- `agent-stock` / `user`
  - profil metier: `AGENT_STOCK`
  - usage recommande: produits, codes-barres, receptions, inventaires et mouvements de stock

Ces comptes proviennent de:

- [src/main/resources/config/liquibase/data/user.csv](src/main/resources/config/liquibase/data/user.csv)
- [src/main/resources/config/liquibase/data/user_authority.csv](src/main/resources/config/liquibase/data/user_authority.csv)
- [src/main/resources/config/liquibase/changelog/20260602100000_seed_demo_role_accounts.xml](src/main/resources/config/liquibase/changelog/20260602100000_seed_demo_role_accounts.xml)

Point important:

- les comptes metier de demo sont affectes a la boutique `ADM-DEMO` creee par Liquibase;
- pour tester d'autres boutiques, connectez-vous avec `admin / admin` ou `manager-adm / user`, puis creez les affectations depuis l'interface ou l'API d'administration.

### Prise En Charge PWA

JHipster prend en charge les PWA (Progressive Web Apps), mais cette fonctionnalité est désactivée par défaut. Le service worker est l'un des principaux composants d'une PWA.

Le code d'initialisation du service worker est désactivé par défaut. Pour l'activer, décommentez le code suivant dans `src/main/webapp/app/app.config.ts` :

```typescript
ServiceWorkerModule.register('ngsw-worker.js', { enabled: false }),
```

### Gestion Des Dépendances

Par exemple, pour ajouter la bibliothèque [Leaflet](https://leafletjs.com/) comme dépendance d'exécution de l'application, lancez :

```bash
./npmw install --save --save-exact leaflet
```

Pour utiliser en développement les définitions de types TypeScript du dépôt [DefinitelyTyped](https://definitelytyped.org/), lancez :

```bash
./npmw install --save-dev --save-exact @types/leaflet
```

Importez ensuite les fichiers JavaScript et CSS indiqués dans les instructions d'installation de la bibliothèque afin que [esbuild][] les prenne en compte.
Modifiez le fichier [src/main/webapp/app/app.config.ts](src/main/webapp/app/app.config.ts) :

```typescript
import 'leaflet/dist/leaflet.js';
```

Modifiez le fichier [src/main/webapp/content/scss/vendor.scss](src/main/webapp/content/scss/vendor.scss) :

```typescript
@import 'leaflet/dist/leaflet.css';
```

Remarque : quelques étapes supplémentaires sont nécessaires pour Leaflet, mais elles ne sont pas détaillées ici.

Pour plus d'informations sur le développement avec JHipster, consultez [Utiliser JHipster en développement](https://www.jhipster.tech/development/).

### Utilisation D'Angular CLI

Vous pouvez également utiliser [Angular CLI](https://angular.dev/tools/cli) pour générer du code client personnalisé.

Par exemple, la commande suivante :

```bash
ng generate component my-component
```

génère plusieurs fichiers :

```bash
create src/main/webapp/app/my-component/my-component.html
create src/main/webapp/app/my-component/my-component.ts
update src/main/webapp/app/app.config.ts
```

## Build Pour La Production

### Packaging En JAR

Pour construire le JAR final et optimiser l'application admSupervisionVentes pour la production, lancez :

```bash
./mvnw -Pprod clean verify
```

Cette commande concatène et minifie les fichiers CSS et JavaScript du client. Elle modifie également `index.html` pour qu'il référence les nouveaux fichiers.
Pour vérifier que tout fonctionne, lancez :

```bash
java -jar target/*.jar
```

Ouvrez ensuite [http://localhost:8080](http://localhost:8080) dans votre navigateur.

Consultez [Utiliser JHipster en production][] pour plus de détails.

### Packaging En WAR

Pour produire un fichier WAR destiné à être déployé sur un serveur d'applications, lancez :

```bash
./mvnw -Pprod,war clean verify
```

### Centre De Contrôle JHipster

JHipster Control Center facilite la gestion et la supervision des applications. Pour démarrer un serveur local accessible sur `http://localhost:7419`, lancez :

```bash
docker compose -f src/main/docker/jhipster-control-center.yml up
```

## Tests

### Tests Spring Boot

Pour exécuter les tests de l'application, lancez :

```bash
./mvnw verify
```

### Tests Du Client

Les tests unitaires utilisent Vitest. Ils se trouvent à proximité des composants concernés et s'exécutent avec :

```bash
./npmw test
```

## API Backend

### Processus De Stock

Les endpoints CRUD générés sont disponibles pour les entités. Les processus métier suivants sont également exposés :

- `POST /api/reception-produits/{receptionId}/scan`
- `POST /api/reception-produits/{receptionId}/validate`
- `POST /api/inventaire-stocks/{inventaireId}/start`
- `POST /api/inventaire-stocks/{inventaireId}/scan`
- `POST /api/inventaire-stocks/{inventaireId}/close`
- `POST /api/transfert-stocks/{transfertId}/validate`
- `POST /api/mouvement-stocks/{mouvementId}/reverse`

Règles supplémentaires appliquées côté serveur :

- une seule ligne de stock par couple `produit/depot` ;
- rejet des codes-barres actifs ambigus dans le périmètre d'une même boutique ;
- annulation d'un mouvement de stock validé uniquement si le stock actuel correspond toujours à l'état d'origine du mouvement.

### Tableau De Bord Et Reporting

- `GET /api/dashboard/overview?from&to&boutiqueId&locataireId`
- `GET /api/dashboard/sales-by-day?from&to&boutiqueId&locataireId&statutVente&minMontantNet`
- `GET /api/dashboard/stock-alerts?boutiqueId&depotId&produitId`
- `POST /api/reporting/exports/generate`
- `GET /api/reporting/exports/{rapportExportId}/preview`
- `GET /api/reporting/exports/{rapportExportId}/download`

Le reporting génère désormais des fichiers physiques dans le répertoire défini par `application.reporting.storage-path` :

- rapports `stock_*` pour les alertes de stock ;
- rapports `sales_*` pour la chronologie des ventes ;
- rapports `redevance_*` / `royalty_*` pour les synthèses de redevances ;
- rapports `scan_*` pour le suivi des scans inconnus.

### Autorisations Métier Et Audit

Le backend applique désormais aux principaux modules métier le même modèle d'autorisation que celui utilisé pour la gestion des utilisateurs :

- `SALES_READ` / `SALES_MANAGE` sur `ventes`, `paiement-ventes` et `ticket-caisses` ;
- `STOCK_READ` / `STOCK_MANAGE` sur les opérations CRUD et les processus de stock ;
- `REPORTING_READ` / `REPORTING_EXPORT` sur les endpoints du tableau de bord et des exports ;
- `ROYALTY_READ` / `ROYALTY_MANAGE` sur `calcul-redevances` et `paiement-redevances`.

Les utilisateurs non administrateurs sont limités au périmètre des boutiques défini par leurs `AffectationUtilisateur` actives :

- les endpoints de liste limitent automatiquement les critères fondés sur `boutiqueId` aux boutiques accessibles ;
- `GET /{id}`, `POST`, `PUT`, `PATCH` et `DELETE` vérifient de nouveau l'accès à la boutique dans la couche service ;
- les actions sensibles sont enregistrées dans `JournalAudit`, notamment les mutations CRUD des modules de vente, stock, reporting et redevances, ainsi que les actions `preview` et `download` des exports.

### Vérification

La suite de tests d'intégration ciblée des processus backend personnalisés s'exécute avec :

```bash
mvn --% -Dskip.npm=true -Dskip.installnodenpm=true -Dit.test=StockWorkflowResourceIT,DashboardReportingResourceIT failsafe:integration-test failsafe:verify
```

## Autres

### Qualité Du Code Avec Sonar

Sonar est utilisé pour analyser la qualité du code. Pour démarrer un serveur Sonar local accessible sur `http://localhost:9001`, lancez :

```bash
docker compose -f src/main/docker/sonar.yml up -d
```

Remarque : la redirection forcée vers l'authentification de l'interface a été désactivée dans [src/main/docker/sonar.yml](src/main/docker/sonar.yml) afin de simplifier l'évaluation de SonarQube. Réactivez-la pour une utilisation réelle.

Vous pouvez lancer une analyse Sonar avec [sonar-scanner](https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner) ou avec le plugin Maven.

Lancez ensuite l'analyse Sonar :

```bash
./mvnw -Pprod clean verify sonar:sonar -Dsonar.login=admin -Dsonar.password=admin
```

Si vous devez relancer la phase Sonar, précisez au minimum la phase `initialize`, car les propriétés Sonar sont chargées depuis le fichier `sonar-project.properties`.

```bash
./mvnw initialize sonar:sonar -Dsonar.login=admin -Dsonar.password=admin
```

Au lieu de transmettre `sonar.password` et `sonar.login` dans la ligne de commande, vous pouvez configurer ces paramètres dans [sonar-project.properties](sonar-project.properties) :

```bash
sonar.login=admin
sonar.password=admin
```

Pour plus d'informations, consultez la [page consacrée à la qualité du code][].

### Prise En Charge De Docker Compose

JHipster génère plusieurs fichiers de configuration Docker Compose dans le dossier [src/main/docker/](src/main/docker/) afin de lancer les services tiers requis.

Par exemple, pour démarrer les services requis dans des conteneurs Docker, lancez :

```bash
npm run services:up
```

Pour arrêter et supprimer les conteneurs, lancez :

```bash
npm run docker:db:down
```

L'[intégration Docker Compose de Spring](https://docs.spring.io/spring-boot/reference/features/dev-services.html) est activée par défaut. Elle peut être désactivée dans `application.yml` :

```yaml
spring:
  ...
  docker:
    compose:
      enabled: false
```

Vous pouvez également conteneuriser entièrement l'application et tous les services dont elle dépend.
Commencez par construire une image Docker de l'application :

```bash
npm run java:docker
```

Pour un système équipé d'un processeur ARM64, par exemple une puce Apple Silicon (M\*), construisez une image ARM64 avec :

```bash
npm run java:docker:arm64
```

Lancez ensuite :

```bash
npm run app:up
```

Pour plus d'informations, consultez [Docker et Docker Compose](https://www.jhipster.tech/documentation-archive/v9.0.0/docker-compose/). Cette page présente également le sous-générateur Docker Compose (`jhipster docker-compose`), capable de générer les configurations Docker d'une ou plusieurs applications JHipster.

## Intégration Continue (Facultatif)

Pour configurer l'intégration continue du projet, lancez le sous-générateur CI/CD (`jhipster ci-cd`). Il permet de créer les fichiers de configuration de plusieurs systèmes d'intégration continue. Consultez la page [Configurer l'intégration continue](https://www.jhipster.tech/documentation-archive/v9.0.0/setting-up-ci/) pour plus d'informations.

## Références

- [Page d'accueil et documentation récente de JHipster](https://www.jhipster.tech/)
- [Archive JHipster 9.0.0](https://www.jhipster.tech/documentation-archive/v9.0.0)
- [Utiliser JHipster en développement](https://www.jhipster.tech/documentation-archive/v9.0.0/development/)
- [Utiliser Docker et Docker Compose](https://www.jhipster.tech/documentation-archive/v9.0.0/docker-compose)
- [Utiliser JHipster en production](https://www.jhipster.tech/documentation-archive/v9.0.0/production/)
- [Exécuter les tests](https://www.jhipster.tech/documentation-archive/v9.0.0/running-tests/)
- [Qualité du code](https://www.jhipster.tech/documentation-archive/v9.0.0/code-quality/)
- [Configurer l'intégration continue](https://www.jhipster.tech/documentation-archive/v9.0.0/setting-up-ci/)
- [Node.js](https://nodejs.org/)
- [NPM](https://www.npmjs.com/)
- [BrowserSync](https://www.browsersync.io/)
- [Jest](https://jestjs.io)
- [Leaflet](https://leafletjs.com/)
- [DefinitelyTyped](https://definitelytyped.org/)
- [Angular CLI](https://angular.dev/tools/cli)
