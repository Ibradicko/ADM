import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { ILigneMouvementStock } from '../ligne-mouvement-stock.model';
import { LigneMouvementStockService } from '../service/ligne-mouvement-stock.service';

const ligneMouvementStockResolve = (route: ActivatedRouteSnapshot): Observable<null | ILigneMouvementStock> => {
  const id = route.params.id;
  if (id) {
    const router = inject(Router);
    const service = inject(LigneMouvementStockService);
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

export default ligneMouvementStockResolve;
