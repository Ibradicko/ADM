import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { ICalculRedevance } from '../calcul-redevance.model';
import { CalculRedevanceService } from '../service/calcul-redevance.service';

const calculRedevanceResolve = (route: ActivatedRouteSnapshot): Observable<null | ICalculRedevance> => {
  const id = route.params.id;
  if (id) {
    const router = inject(Router);
    const service = inject(CalculRedevanceService);
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

export default calculRedevanceResolve;
