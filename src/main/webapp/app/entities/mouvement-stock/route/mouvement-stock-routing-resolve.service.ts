import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { IMouvementStock } from '../mouvement-stock.model';
import { MouvementStockService } from '../service/mouvement-stock.service';

const mouvementStockResolve = (route: ActivatedRouteSnapshot): Observable<null | IMouvementStock> => {
  const id = route.params.id;
  if (id) {
    const router = inject(Router);
    const service = inject(MouvementStockService);
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

export default mouvementStockResolve;
