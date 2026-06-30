import { Component, OnInit, computed, inject, input, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { UiPermissionService } from 'app/core/services/ui-permission.service';
import { IAffectationUtilisateur } from 'app/entities/affectation-utilisateur/affectation-utilisateur.model';
import { AffectationUtilisateurService } from 'app/entities/affectation-utilisateur/service/affectation-utilisateur.service';
import { IExploitationBoutique } from 'app/entities/exploitation-boutique/exploitation-boutique.model';
import { ExploitationBoutiqueService } from 'app/entities/exploitation-boutique/service/exploitation-boutique.service';
import { firstValueFrom } from 'rxjs';
import { IBoutique } from '../boutique.model';

@Component({
  selector: 'jhi-boutique-detail',
  templateUrl: './boutique-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, TranslateModule, RouterLink],
})
export class BoutiqueDetail implements OnInit {
  readonly boutique = input<IBoutique | null>(null);
  readonly permissionsUi = inject(UiPermissionService);
  readonly exploitation = signal<IExploitationBoutique | null>(null);
  readonly affectations = signal<IAffectationUtilisateur[]>([]);
  readonly manager = computed(
    () => this.affectations().find(affectation => affectation.profil?.code === 'MANAGER_BOUTIQUE' && affectation.actif) ?? null,
  );

  private readonly exploitationBoutiqueService = inject(ExploitationBoutiqueService);
  private readonly affectationUtilisateurService = inject(AffectationUtilisateurService);

  ngOnInit(): void {
    void this.chargerContexte();
  }

  nomManager(): string {
    const user = this.manager()?.user;
    const nomComplet = [user?.firstName, user?.lastName].filter(Boolean).join(' ').trim();
    return nomComplet || user?.login || '--';
  }

  private async chargerContexte(): Promise<void> {
    const boutiqueId = this.boutique()?.id;
    if (!boutiqueId) {
      return;
    }

    const [exploitationsResponse, affectationsResponse] = await Promise.all([
      firstValueFrom(this.exploitationBoutiqueService.query({ 'boutiqueId.equals': boutiqueId, 'statut.equals': 'ACTIF', size: 20 })),
      firstValueFrom(this.affectationUtilisateurService.query({ 'boutiqueId.equals': boutiqueId, 'actif.equals': true, size: 50 })),
    ]);

    this.exploitation.set(exploitationsResponse.body?.[0] ?? null);
    this.affectations.set(affectationsResponse.body ?? []);
  }

  previousState(): void {
    globalThis.history.back();
  }
}
