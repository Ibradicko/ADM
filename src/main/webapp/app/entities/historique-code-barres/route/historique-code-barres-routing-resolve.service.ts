import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { IHistoriqueCodeBarres } from '../historique-code-barres.model';
import { HistoriqueCodeBarresService } from '../service/historique-code-barres.service';

const historiqueCodeBarresResolve = (route: ActivatedRouteSnapshot): Observable<null | IHistoriqueCodeBarres> => {
  const id = route.params.id;
  if (id) {
    const router = inject(Router);
    const service = inject(HistoriqueCodeBarresService);
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

export default historiqueCodeBarresResolve;
