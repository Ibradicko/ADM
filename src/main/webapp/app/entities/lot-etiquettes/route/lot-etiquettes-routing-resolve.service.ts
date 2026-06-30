import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { ILotEtiquettes } from '../lot-etiquettes.model';
import { LotEtiquettesService } from '../service/lot-etiquettes.service';

const lotEtiquettesResolve = (route: ActivatedRouteSnapshot): Observable<null | ILotEtiquettes> => {
  const id = route.params.id;
  if (id) {
    const router = inject(Router);
    const service = inject(LotEtiquettesService);
    return service.find(id).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 404) {
          router.navigate(['404']);
        } else {
          router.navigate(['error']);
        }
        return EMPTY;
      }),
    );
  }

  return of(null);
};

export default lotEtiquettesResolve;
