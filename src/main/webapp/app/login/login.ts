import { AfterViewInit, Component, ElementRef, OnInit, inject, signal, viewChild } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';

import { AccountService } from 'app/core/auth/account.service';
import { StateStorageService } from 'app/core/auth/state-storage.service';
import { LoginService } from 'app/login/login.service';
import { TranslateDirective } from 'app/shared/language';

@Component({
  selector: 'jhi-login',
  imports: [ReactiveFormsModule, RouterLink, FontAwesomeModule, TranslateDirective, TranslateModule],
  templateUrl: './login.html',
})
export default class Login implements OnInit, AfterViewInit {
  username = viewChild.required<ElementRef>('username');

  readonly authenticationError = signal(false);

  loginForm = new FormGroup({
    username: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    password: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    rememberMe: new FormControl(false, { nonNullable: true, validators: [Validators.required] }),
  });

  private readonly accountService = inject(AccountService);
  private readonly loginService = inject(LoginService);
  private readonly router = inject(Router);
  private readonly stateStorageService = inject(StateStorageService);

  ngOnInit(): void {
    // if already authenticated then navigate to dashboard page
    this.accountService.identity().subscribe(account => {
      if (account?.mustChangePassword) {
        this.router.navigate(['/account/password']);
        return;
      }
      if (account) {
        this.router.navigate(['/dashboard']);
      }
    });
  }

  ngAfterViewInit(): void {
    this.username().nativeElement.focus();
  }

  login(): void {
    this.stateStorageService.clearUrl();
    this.loginService.login(this.loginForm.getRawValue()).subscribe({
      next: account => {
        this.authenticationError.set(false);
        if (account?.mustChangePassword) {
          this.router.navigate(['/account/password']);
          return;
        }
        if (account) {
          this.router.navigate(['/dashboard']);
        }
      },
      error: () => this.authenticationError.set(true),
    });
  }
}
