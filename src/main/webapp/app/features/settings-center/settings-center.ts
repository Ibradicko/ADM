import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { firstValueFrom } from 'rxjs';

import { UiPermissionService } from 'app/core/services/ui-permission.service';
import { IParametreGlobal, NewParametreGlobal } from 'app/entities/parametre-global/parametre-global.model';
import { ParametreGlobalService } from 'app/entities/parametre-global/service/parametre-global.service';
import { TranslateDirective } from 'app/shared/language';

interface MessageParametres {
  type: 'success' | 'danger' | 'info';
  key: string;
}

interface ParametreCatalogue {
  code: string;
  labelKey: string;
  descriptionKey: string;
  valeurDefaut: string;
  type: 'text' | 'number' | 'boolean';
}

const PARAMETRES_GLOBAUX_CONTROLES: ParametreCatalogue[] = [
  {
    code: 'DEVISE_DEFAUT',
    labelKey: 'settingsCenter.parameters.defaultCurrency.label',
    descriptionKey: 'settingsCenter.parameters.defaultCurrency.description',
    valeurDefaut: 'XOF',
    type: 'text',
  },
  {
    code: 'TAUX_REDEVANCE_DEFAUT',
    labelKey: 'settingsCenter.parameters.defaultRoyaltyRate.label',
    descriptionKey: 'settingsCenter.parameters.defaultRoyaltyRate.description',
    valeurDefaut: '10',
    type: 'number',
  },
  {
    code: 'STOCK_ALERTE_DEFAUT',
    labelKey: 'settingsCenter.parameters.defaultStockAlert.label',
    descriptionKey: 'settingsCenter.parameters.defaultStockAlert.description',
    valeurDefaut: '5',
    type: 'number',
  },
  {
    code: 'TICKET_PREFIXE',
    labelKey: 'settingsCenter.parameters.ticketPrefix.label',
    descriptionKey: 'settingsCenter.parameters.ticketPrefix.description',
    valeurDefaut: 'ADM',
    type: 'text',
  },
];

@Component({
  selector: 'jhi-settings-center',
  templateUrl: './settings-center.html',
  styleUrl: './settings-center.scss',
  imports: [FormsModule, RouterLink, TranslateDirective, TranslateModule],
})
export default class SettingsCenterComponent implements OnInit {
  readonly permissionsUi = inject(UiPermissionService);

  readonly parametresGlobaux = signal<IParametreGlobal[]>([]);
  readonly valeursGlobales = signal<Record<string, string>>({});
  readonly actifsGlobaux = signal<Record<string, boolean>>({});
  readonly chargement = signal(false);
  readonly enregistrement = signal(false);
  readonly message = signal<MessageParametres | null>(null);
  readonly recherche = signal('');

  readonly parametresGlobauxControles = PARAMETRES_GLOBAUX_CONTROLES;
  readonly parametresActifs = computed(() => this.parametresGlobaux().filter(parametre => parametre.actif).length);
  readonly parametresInactifs = computed(() => this.parametresGlobaux().filter(parametre => parametre.actif === false).length);
  readonly parametresControlesActifs = computed(
    () => PARAMETRES_GLOBAUX_CONTROLES.filter(definition => this.actifGlobal(definition.code)).length,
  );
  readonly parametresNonControles = computed(() => {
    const codesControles = new Set(PARAMETRES_GLOBAUX_CONTROLES.map(definition => definition.code));
    return this.parametresGlobaux().filter(parametre => parametre.code && !codesControles.has(parametre.code));
  });
  readonly parametresGlobauxFiltres = computed(() => {
    const texte = this.recherche().trim().toLowerCase();
    if (!texte) {
      return this.parametresGlobaux();
    }
    return this.parametresGlobaux().filter(parametre =>
      [parametre.code, parametre.valeur, parametre.description].filter(Boolean).join(' ').toLowerCase().includes(texte),
    );
  });

  private readonly parametreGlobalService = inject(ParametreGlobalService);

  ngOnInit(): void {
    void this.recharger();
  }

  async recharger(): Promise<void> {
    this.chargement.set(true);
    this.message.set(null);
    try {
      const response = await firstValueFrom(this.parametreGlobalService.query({ size: 200, sort: ['code,asc'] }));
      this.parametresGlobaux.set(response.body ?? []);
      this.synchroniserValeursParametres();
    } catch {
      this.message.set({ type: 'danger', key: 'settingsCenter.messages.loadFailed' });
    } finally {
      this.chargement.set(false);
    }
  }

  async enregistrerParametresGlobaux(): Promise<void> {
    if (!this.permissionsUi.peutGererParametres()) {
      return;
    }

    this.enregistrement.set(true);
    this.message.set(null);
    try {
      await Promise.all(
        PARAMETRES_GLOBAUX_CONTROLES.map(definition => {
          const existant = this.parametresGlobaux().find(parametre => parametre.code === definition.code);
          const payload = {
            code: definition.code,
            valeur: this.valeursGlobales()[definition.code] ?? definition.valeurDefaut,
            actif: this.actifsGlobaux()[definition.code] ?? true,
            description: definition.descriptionKey,
          };

          if (existant) {
            return firstValueFrom(this.parametreGlobalService.partialUpdate({ id: existant.id, ...payload }));
          }

          return firstValueFrom(this.parametreGlobalService.create({ id: null, ...payload } satisfies NewParametreGlobal));
        }),
      );
      this.message.set({ type: 'success', key: 'settingsCenter.messages.globalSaved' });
      await this.recharger();
    } catch {
      this.message.set({ type: 'danger', key: 'settingsCenter.messages.globalSaveFailed' });
    } finally {
      this.enregistrement.set(false);
    }
  }

  async basculerActifGlobal(parametre: IParametreGlobal): Promise<void> {
    if (!this.permissionsUi.peutGererParametres()) {
      return;
    }

    this.enregistrement.set(true);
    this.message.set(null);
    try {
      await firstValueFrom(this.parametreGlobalService.partialUpdate({ id: parametre.id, actif: !parametre.actif }));
      this.message.set({ type: 'success', key: 'settingsCenter.messages.statusUpdated' });
      await this.recharger();
    } catch {
      this.message.set({ type: 'danger', key: 'settingsCenter.messages.statusUpdateFailed' });
    } finally {
      this.enregistrement.set(false);
    }
  }

  definitionParametre(code: string | null | undefined): ParametreCatalogue | undefined {
    return PARAMETRES_GLOBAUX_CONTROLES.find(definition => definition.code === code);
  }

  valeurGlobale(code: string): string {
    return this.valeursGlobales()[code] ?? '';
  }

  definirValeurGlobale(code: string, valeur: string): void {
    this.valeursGlobales.update(valeurs => ({ ...valeurs, [code]: valeur }));
  }

  actifGlobal(code: string): boolean {
    return this.actifsGlobaux()[code] ?? true;
  }

  definirActifGlobal(code: string, actif: boolean): void {
    this.actifsGlobaux.update(actifs => ({ ...actifs, [code]: actif }));
  }

  reinitialiserDefinition(definition: ParametreCatalogue): void {
    this.definirValeurGlobale(definition.code, definition.valeurDefaut);
    this.definirActifGlobal(definition.code, true);
  }

  private synchroniserValeursParametres(): void {
    const prochainsGlobaux: Record<string, string> = {};
    const prochainsActifsGlobaux: Record<string, boolean> = {};
    for (const definition of PARAMETRES_GLOBAUX_CONTROLES) {
      const existant = this.parametresGlobaux().find(parametre => parametre.code === definition.code);
      prochainsGlobaux[definition.code] = existant?.valeur ?? definition.valeurDefaut;
      prochainsActifsGlobaux[definition.code] = existant?.actif ?? true;
    }
    this.valeursGlobales.set(prochainsGlobaux);
    this.actifsGlobaux.set(prochainsActifsGlobaux);
  }
}
