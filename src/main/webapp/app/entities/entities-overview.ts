import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

interface EntityLink {
  label: string;
  route: string;
}

interface EntityGroup {
  title: string;
  entities: EntityLink[];
}

@Component({
  selector: 'jhi-entities-overview',
  imports: [RouterLink],
  template: `
    <section class="container-fluid py-4">
      <div class="d-flex align-items-center justify-content-between flex-wrap gap-2 mb-4">
        <div>
          <h1 class="h3 mb-1">Entites JHipster</h1>
          <p class="text-muted mb-0">Acces direct aux ecrans CRUD generes pour les entites du backend.</p>
        </div>
        <a class="btn btn-outline-primary btn-sm" routerLink="/admin/docs">API Docs</a>
      </div>

      @for (group of entityGroups; track group.title) {
        <section class="mb-4">
          <h2 class="h5 mb-2">{{ group.title }}</h2>
          <div class="table-responsive">
            <table class="table table-striped table-hover align-middle">
              <thead>
                <tr>
                  <th scope="col">Entite</th>
                  <th scope="col">Route</th>
                  <th scope="col" class="text-end">Action</th>
                </tr>
              </thead>
              <tbody>
                @for (entity of group.entities; track entity.route) {
                  <tr>
                    <td>{{ entity.label }}</td>
                    <td>
                      <code>{{ entity.route }}</code>
                    </td>
                    <td class="text-end">
                      <a class="btn btn-primary btn-sm" [routerLink]="entity.route">Ouvrir</a>
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        </section>
      }
    </section>
  `,
})
export class EntitiesOverview {
  readonly entityGroups: EntityGroup[] = [
    {
      title: 'Administration et securite',
      entities: [
        { label: 'Authorities', route: '/authority' },
        { label: 'User management', route: '/admin/user-management' },
        { label: 'Profils metier', route: '/profil-metier' },
        { label: 'Permissions metier', route: '/permission-metier' },
        { label: 'Affectations utilisateur', route: '/affectation-utilisateur' },
      ],
    },
    {
      title: 'Boutiques et locataires',
      entities: [
        { label: 'Boutiques', route: '/boutique' },
        { label: 'Locataires', route: '/locataire' },
        { label: 'Exploitations boutique', route: '/exploitation-boutique' },
      ],
    },
    {
      title: 'Catalogue et identification',
      entities: [
        { label: 'Groupes article', route: '/groupe-article' },
        { label: 'Familles article', route: '/famille-article' },
        { label: 'Sous-familles article', route: '/sous-famille-article' },
        { label: 'Unites mesure', route: '/unite-mesure' },
        { label: 'Produits', route: '/produit' },
        { label: 'Tarifs produit', route: '/tarif-produit' },
        { label: 'Codes-barres produit', route: '/code-barres-produit' },
        { label: 'Historique codes-barres', route: '/historique-code-barres' },
        { label: 'Parametres code-barres', route: '/parametre-code-barres' },
        { label: 'Lots etiquettes', route: '/lot-etiquettes' },
        { label: 'Etiquettes produit', route: '/etiquette-produit' },
        { label: 'Scans inconnus', route: '/scan-inconnu' },
      ],
    },
    {
      title: 'Ventes et caisse',
      entities: [
        { label: 'Modes paiement', route: '/mode-paiement-ref' },
        { label: 'Ventes', route: '/vente' },
        { label: 'Lignes vente', route: '/ligne-vente' },
        { label: 'Paiements vente', route: '/paiement-vente' },
        { label: 'Operations correctives vente', route: '/operation-corrective-vente' },
        { label: 'Tickets caisse', route: '/ticket-caisse' },
      ],
    },
    {
      title: 'Stocks',
      entities: [
        { label: 'Depots stock', route: '/depot-stock' },
        { label: 'Stocks produit', route: '/stock-produit' },
        { label: 'Mouvements stock', route: '/mouvement-stock' },
        { label: 'Lignes mouvement stock', route: '/ligne-mouvement-stock' },
        { label: 'Receptions produit', route: '/reception-produit' },
        { label: 'Lignes reception produit', route: '/ligne-reception-produit' },
        { label: 'Inventaires stock', route: '/inventaire-stock' },
        { label: 'Lignes inventaire stock', route: '/ligne-inventaire-stock' },
        { label: 'Transferts stock', route: '/transfert-stock' },
        { label: 'Lignes transfert stock', route: '/ligne-transfert-stock' },
      ],
    },
    {
      title: 'Redevances, audit et rapports',
      entities: [
        { label: 'Regles redevance', route: '/regle-redevance' },
        { label: 'Calculs redevance', route: '/calcul-redevance' },
        { label: 'Lignes calcul redevance', route: '/ligne-calcul-redevance' },
        { label: 'Paiements redevance', route: '/paiement-redevance' },
        { label: 'Regularisations redevance', route: '/regularisation-redevance' },
        { label: 'Journal audit', route: '/journal-audit' },
        { label: 'Rapports export', route: '/rapport-export' },
        { label: 'Parametres globaux', route: '/parametre-global' },
      ],
    },
  ];
}

export default EntitiesOverview;
