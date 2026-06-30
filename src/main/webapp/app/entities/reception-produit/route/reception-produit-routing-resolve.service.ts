import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { IReceptionProduit } from '../reception-produit.model';
import { ReceptionProduitService } from '../service/reception-produit.service';

const receptionProduitResolve = (route: ActivatedRouteSnapshot): Observable<null | IReceptionProduit> => {
  const id = route.params.id;
  if (id) {
    const router = inject(Router);
    const service = inject(ReceptionProduitService);
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

export default receptionProduitResolve;
