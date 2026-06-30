import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { VenteService } from 'app/entities/vente/service/vente.service';
import { IVente } from 'app/entities/vente/vente.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { AlertErrorModel } from 'app/shared/alert/alert-error.model';
import { TranslateDirective } from 'app/shared/language';
import { TicketCaisseService } from '../service/ticket-caisse.service';
import { ITicketCaisse } from '../ticket-caisse.model';

import { TicketCaisseFormGroup, TicketCaisseFormService } from './ticket-caisse-form.service';

@Component({
  selector: 'jhi-ticket-caisse-update',
  templateUrl: './ticket-caisse-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class TicketCaisseUpdate implements OnInit {
  readonly isSaving = signal(false);
  ticketCaisse: ITicketCaisse | null = null;

  ventesSharedCollection = signal<IVente[]>([]);

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected ticketCaisseService = inject(TicketCaisseService);
  protected ticketCaisseFormService = inject(TicketCaisseFormService);
  protected venteService = inject(VenteService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TicketCaisseFormGroup = this.ticketCaisseFormService.createTicketCaisseFormGroup();

  compareVente = (o1: IVente | null, o2: IVente | null): boolean => this.venteService.compareVente(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ticketCaisse }) => {
      this.ticketCaisse = ticketCaisse;
      if (ticketCaisse) {
        this.updateForm(ticketCaisse);
      }

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(
          new EventWithContent<AlertErrorModel>('admSupervisionVentesApp.error', { ...err, key: `error.file.${err.key}` }),
        ),
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const ticketCaisse = this.ticketCaisseFormService.getTicketCaisse(this.editForm);
    if (ticketCaisse.id === null) {
      this.subscribeToSaveResponse(this.ticketCaisseService.create(ticketCaisse));
    } else {
      this.subscribeToSaveResponse(this.ticketCaisseService.update(ticketCaisse));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ITicketCaisse | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving.set(false);
  }

  protected updateForm(ticketCaisse: ITicketCaisse): void {
    this.ticketCaisse = ticketCaisse;
    this.ticketCaisseFormService.resetForm(this.editForm, ticketCaisse);

    this.ventesSharedCollection.update(ventes => this.venteService.addVenteToCollectionIfMissing<IVente>(ventes, ticketCaisse.vente));
  }

  protected loadRelationshipsOptions(): void {
    this.venteService
      .query()
      .pipe(map((res: HttpResponse<IVente[]>) => res.body ?? []))
      .pipe(map((ventes: IVente[]) => this.venteService.addVenteToCollectionIfMissing<IVente>(ventes, this.ticketCaisse?.vente)))
      .subscribe((ventes: IVente[]) => this.ventesSharedCollection.set(ventes));
  }
}
