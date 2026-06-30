import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { IRegularisationRedevance } from '../regularisation-redevance.model';
import { RegularisationRedevanceService } from '../service/regularisation-redevance.service';

const regularisationRedevanceResolve = (route: ActivatedRouteSnapshot): Observable<null | IRegularisationRedevance> => {
  const id = route.params.id;
  if (id) {
    const router = inject(Router);
    const service = inject(RegularisationRedevanceService);
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

export default regularisationRedevanceResolve;
