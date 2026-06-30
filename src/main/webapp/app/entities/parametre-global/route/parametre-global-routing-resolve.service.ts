import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { IParametreGlobal } from '../parametre-global.model';
import { ParametreGlobalService } from '../service/parametre-global.service';

const parametreGlobalResolve = (route: ActivatedRouteSnapshot): Observable<null | IParametreGlobal> => {
  const id = route.params.id;
  if (id) {
    const router = inject(Router);
    const service = inject(ParametreGlobalService);
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

export default parametreGlobalResolve;
