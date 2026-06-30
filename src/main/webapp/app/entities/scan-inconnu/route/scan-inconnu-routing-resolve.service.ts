import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { IScanInconnu } from '../scan-inconnu.model';
import { ScanInconnuService } from '../service/scan-inconnu.service';

const scanInconnuResolve = (route: ActivatedRouteSnapshot): Observable<null | IScanInconnu> => {
  const id = route.params.id;
  if (id) {
    const router = inject(Router);
    const service = inject(ScanInconnuService);
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

export default scanInconnuResolve;
