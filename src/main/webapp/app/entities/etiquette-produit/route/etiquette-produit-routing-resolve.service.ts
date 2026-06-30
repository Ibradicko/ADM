import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { IEtiquetteProduit } from '../etiquette-produit.model';
import { EtiquetteProduitService } from '../service/etiquette-produit.service';

const etiquetteProduitResolve = (route: ActivatedRouteSnapshot): Observable<null | IEtiquetteProduit> => {
  const id = route.params.id;
  if (id) {
    const router = inject(Router);
    const service = inject(EtiquetteProduitService);
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

export default etiquetteProduitResolve;
