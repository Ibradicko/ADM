import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';
import dayjs from 'dayjs/esm';
import { finalize, map, switchMap } from 'rxjs/operators';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IExploitationBoutique, NewExploitationBoutique } from 'app/entities/exploitation-boutique/exploitation-boutique.model';
import { ExploitationBoutiqueService } from 'app/entities/exploitation-boutique/service/exploitation-boutique.service';
import { ILocataire } from 'app/entities/locataire/locataire.model';
import { LocataireService } from 'app/entities/locataire/service/locataire.service';
import { IBoutique } from '../boutique.model';

@Component({
  selector: 'jhi-boutique-assign-dialog',
  templateUrl: './boutique-assign-dialog.html',
  imports: [FormsModule, FontAwesomeModule],
})
export class BoutiqueAssignDialog implements OnInit {
  boutique: IBoutique | null = null;
  activeExploitation: IExploitationBoutique | null = null;
  readonly isSaving = signal(false);
  readonly locataires = signal<ILocataire[]>([]);
  readonly recherche = signal('');
  readonly locataireSelectionne = signal<ILocataire | null>(null);
  readonly numeroContrat = signal('');
  readonly dateDebut = signal(dayjs().format(DATE_FORMAT));
  readonly dateFin = signal('');
  readonly tauxRedevanceDefaut = signal<number | null>(null);
  readonly commentaire = signal('');
  readonly messageErreur = signal<string | null>(null);

  readonly locatairesFiltres = computed(() => {
    const recherche = this.normaliser(this.recherche());
    if (!recherche) {
      return this.locataires();
    }
    return this.locataires().filter(locataire =>
      this.normaliser([locataire.code, locataire.nom, locataire.email, locataire.typeLocataire].filter(Boolean).join(' ')).includes(
        recherche,
      ),
    );
  });

  protected readonly activeModal = inject(NgbActiveModal);
  protected readonly locataireService = inject(LocataireService);
  protected readonly exploitationBoutiqueService = inject(ExploitationBoutiqueService);

  ngOnInit(): void {
    this.locataireService
      .query({ size: 500, sort: ['nom,asc'], 'statut.equals': 'ACTIF' })
      .pipe(map((res: HttpResponse<ILocataire[]>) => res.body ?? []))
      .subscribe(locataires => this.locataires.set(locataires));
  }

  cancel(): void {
    this.activeModal.dismiss();
  }

  choisirLocataire(locataire: ILocataire): void {
    this.locataireSelectionne.set(locataire);
    this.recherche.set('');
  }

  assigner(): void {
    this.messageErreur.set(null);
    const boutique = this.boutique;
    const locataire = this.locataireSelectionne();
    if (!boutique || !locataire) {
      this.messageErreur.set('Selectionnez un locataire actif avant de continuer.');
      return;
    }
    if (!locataire.email?.trim()) {
      this.messageErreur.set('Le locataire selectionne doit avoir un email pour recevoir son compte administrateur boutique.');
      return;
    }

    const payload: NewExploitationBoutique = {
      id: null,
      numeroContrat: this.numeroContrat().trim() || null,
      dateDebut: dayjs(this.dateDebut(), DATE_FORMAT),
      dateFin: this.dateFin().trim() ? dayjs(this.dateFin(), DATE_FORMAT) : null,
      tauxRedevanceDefaut: this.tauxRedevanceDefaut(),
      statut: 'ACTIF',
      commentaire: this.commentaire().trim() || null,
      boutique,
      locataire,
    };

    const exploitationACloturer = this.activeExploitation;

    this.isSaving.set(true);
    (exploitationACloturer
      ? this.exploitationBoutiqueService
          .partialUpdate({ id: exploitationACloturer.id, statut: 'INACTIF', dateFin: dayjs() })
          .pipe(switchMap(() => this.exploitationBoutiqueService.create(payload)))
      : this.exploitationBoutiqueService.create(payload)
    )
      .pipe(finalize(() => this.isSaving.set(false)))
      .subscribe({
        next: () => this.activeModal.close('assigned'),
        error: error => this.messageErreur.set(this.formatErreur(error)),
      });
  }

  formatLocataire(locataire: ILocataire): string {
    return [locataire.code, locataire.nom].filter(Boolean).join(' - ') || `Locataire ${locataire.id}`;
  }

  private formatErreur(error: unknown): string {
    const maybeError = error as { error?: { message?: string; detail?: string }; headers?: { get?: (name: string) => string | null } };
    const message = `${maybeError.error?.message ?? ''} ${maybeError.error?.detail ?? ''} ${maybeError.headers?.get?.('X-admSupervisionVentes-error') ?? ''}`;
    if (message.includes('activeExploitationExists')) {
      return 'Cette boutique a deja une exploitation active. Cloturez-la avant de reaffecter la boutique.';
    }
    if (message.includes('tenantEmailRequired')) {
      return 'Le locataire doit avoir un email valide pour recevoir son compte administrateur boutique.';
    }
    if (message.includes('tenantAdminProfileMissing')) {
      return 'Le profil MANAGER_BOUTIQUE est introuvable. Creez ce profil metier avant de continuer.';
    }
    return "Impossible d'affecter ce locataire pour le moment. Verifiez les donnees puis reessayez.";
  }

  private normaliser(value: string): string {
    return value
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .toLowerCase()
      .trim();
  }
}
