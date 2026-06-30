import { Component, inject, signal } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, ReactiveFormsModule, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { AccountService } from 'app/core/auth/account.service';
import { TranslateDirective } from 'app/shared/language';
import { finalize } from 'rxjs/operators';

import PasswordStrengthBar from './password-strength-bar/password-strength-bar';
import { PasswordService } from './password.service';

@Component({
  selector: 'jhi-password',
  imports: [TranslateDirective, TranslateModule, ReactiveFormsModule, PasswordStrengthBar],
  templateUrl: './password.html',
})
export default class Password {
  readonly doNotMatch = signal(false);
  readonly error = signal(false);
  readonly success = signal(false);
  readonly isSaving = signal(false);

  readonly account = inject(AccountService).account;

  passwordForm = new FormGroup(
    {
      currentPassword: new FormControl('', { nonNullable: true, validators: Validators.required }),
      newPassword: new FormControl('', {
        nonNullable: true,
        validators: [Validators.required, Validators.minLength(8), Validators.maxLength(100)],
      }),
      confirmPassword: new FormControl('', {
        nonNullable: true,
        validators: [Validators.required, Validators.minLength(8), Validators.maxLength(100)],
      }),
    },
    { validators: this.passwordsMatchValidator() },
  );

  private readonly accountService = inject(AccountService);
  private readonly passwordService = inject(PasswordService);
  private readonly router = inject(Router);

  changePassword(): void {
    this.error.set(false);
    this.success.set(false);
    this.doNotMatch.set(false);

    const { newPassword, confirmPassword, currentPassword } = this.passwordForm.getRawValue();
    if (this.passwordForm.invalid) {
      this.passwordForm.markAllAsTouched();
      this.doNotMatch.set(newPassword !== confirmPassword);
      return;
    }

    if (newPassword !== confirmPassword) {
      this.doNotMatch.set(true);
      return;
    }

    this.isSaving.set(true);
    this.passwordService
      .save(newPassword, currentPassword)
      .pipe(finalize(() => this.isSaving.set(false)))
      .subscribe({
        next: () => {
          this.success.set(true);
          // Recharger le compte pour effacer mustChangePassword du cache
          this.accountService.identity(true).subscribe(account => {
            if (account && !account.mustChangePassword) {
              this.router.navigate(['/dashboard']);
            }
          });
        },
        error: () => this.error.set(true),
      });
  }

  clearMessages(): void {
    this.error.set(false);
    this.success.set(false);
    this.doNotMatch.set(false);
  }

  private passwordsMatchValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const newPassword = control.get('newPassword')?.value;
      const confirmPassword = control.get('confirmPassword')?.value;

      if (!newPassword || !confirmPassword || newPassword === confirmPassword) {
        return null;
      }

      return { passwordMismatch: true };
    };
  }
}
