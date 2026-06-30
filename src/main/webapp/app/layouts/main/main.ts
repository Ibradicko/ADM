import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';

import { LangChangeEvent, TranslateService } from '@ngx-translate/core';
import dayjs from 'dayjs/esm';
import { filter } from 'rxjs';

import { AppPageTitleStrategy } from 'app/app-page-title-strategy';
import { AccountService } from 'app/core/auth/account.service';

@Component({
  selector: 'jhi-main',
  templateUrl: './main.html',
  providers: [AppPageTitleStrategy],
  imports: [RouterOutlet],
})
export default class Main implements OnInit {
  private readonly router = inject(Router);
  private readonly appPageTitleStrategy = inject(AppPageTitleStrategy);
  private readonly accountService = inject(AccountService);
  private readonly translateService = inject(TranslateService);

  readonly account = inject(AccountService).account;
  readonly currentUrl = signal(this.router.url);
  readonly isStandaloneAuthPage = computed(() => {
    const url = this.currentUrl();
    // /account/password et /account/settings sont des pages d'utilisateurs déjà connectés
    // (accessibles depuis le menu du profil) : elles doivent garder le bandeau applicatif,
    // sinon l'utilisateur perd tout moyen de naviguer ou de se déconnecter.
    return (
      url.startsWith('/login') ||
      url.startsWith('/account/activate') ||
      url.startsWith('/account/register') ||
      url.startsWith('/account/reset')
    );
  });
  readonly showApplicationShell = computed(() => !this.isStandaloneAuthPage() && this.account() !== null);

  ngOnInit(): void {
    this.accountService.identity().subscribe();

    this.router.events.pipe(filter(event => event instanceof NavigationEnd)).subscribe(event => {
      this.currentUrl.set((event as NavigationEnd).urlAfterRedirects);
    });

    this.translateService.onLangChange.subscribe((langChangeEvent: LangChangeEvent) => {
      this.appPageTitleStrategy.updateTitle(this.router.routerState.snapshot);
      dayjs.locale(langChangeEvent.lang);
      document.documentElement.setAttribute('lang', langChangeEvent.lang);
    });
  }
}
