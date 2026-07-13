import { DatePipe } from '@angular/common';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { NgbPagination } from '@ng-bootstrap/ng-bootstrap/pagination';
import { combineLatest } from 'rxjs';

import { SORT } from 'app/config/navigation.constants';
import { ITEMS_PER_PAGE, PAGE_HEADER, TOTAL_COUNT_RESPONSE_HEADER } from 'app/config/pagination.constants';
import { AccountService } from 'app/core/auth/account.service';
import { AffectationUtilisateurService } from 'app/entities/affectation-utilisateur/service/affectation-utilisateur.service';
import { ProfilMetierService } from 'app/entities/profil-metier/service/profil-metier.service';
import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { ItemCount } from 'app/shared/pagination';
import { SortByDirective, SortDirective, SortService, SortState, sortStateSignal } from 'app/shared/sort';
import { UserManagementDeleteDialog } from '../delete/user-management-delete-dialog';
import { UserManagementService } from '../service/user-management.service';
import { IUserManagement } from '../user-management.model';

const MANAGER_ADM_AUTHORITY = 'ROLE_MANAGER_ADM';
const MANAGER_ADM_PROFILE = 'MANAGER_ADM';

@Component({
  selector: 'jhi-user-mgmt',
  templateUrl: './user-management.html',
  styleUrl: './user-management.scss',
  imports: [
    RouterLink,
    FormsModule,
    FontAwesomeModule,
    AlertError,
    Alert,
    NgbPagination,
    SortDirective,
    SortByDirective,
    ItemCount,
    DatePipe,
  ],
})
export class UserManagement implements OnInit {
  readonly currentAccount = inject(AccountService).account;
  readonly users = signal<IUserManagement[]>([]);
  readonly managerAdmUserIds = signal<Set<number>>(new Set());
  readonly isLoading = signal(false);
  readonly totalItems = signal(0);
  readonly itemsPerPage = signal(ITEMS_PER_PAGE);
  readonly page = signal(1);
  sortState = sortStateSignal({});
  readonly searchTerm = signal('');
  readonly selectedStatus = signal<'ALL' | 'ACTIVE' | 'INACTIVE'>('ALL');
  readonly selectedAuthority = signal('ALL');
  readonly visibleUsers = computed(() => {
    const searchTerm = this.searchTerm().trim().toLowerCase();
    const selectedStatus = this.selectedStatus();
    const selectedAuthority = this.selectedAuthority();

    return this.managerAdmUsers().filter(user => {
      const matchesSearch =
        !searchTerm ||
        [user.login, user.email, user.firstName, user.lastName].filter(Boolean).some(value => value!.toLowerCase().includes(searchTerm));
      const matchesStatus = selectedStatus === 'ALL' || (selectedStatus === 'ACTIVE' ? user.activated === true : user.activated === false);
      const matchesAuthority = selectedAuthority === 'ALL' || (user.authorities ?? []).includes(selectedAuthority);

      return matchesSearch && matchesStatus && matchesAuthority;
    });
  });
  readonly managerAdmUsers = computed(() =>
    this.users().filter(user => (user.authorities ?? []).includes(MANAGER_ADM_AUTHORITY) || this.managerAdmUserIds().has(user.id ?? -1)),
  );
  readonly activeCount = computed(() => this.managerAdmUsers().filter(user => user.activated).length);
  readonly inactiveCount = computed(() => this.managerAdmUsers().filter(user => !user.activated).length);
  readonly managerAdmCount = computed(() => this.managerAdmUsers().length);
  readonly availableAuthorities = computed(() =>
    [...new Set(this.managerAdmUsers().flatMap(user => user.authorities ?? []))].sort((left, right) => left.localeCompare(right)),
  );

  private readonly userService = inject(UserManagementService);
  private readonly affectationUtilisateurService = inject(AffectationUtilisateurService);
  private readonly profilMetierService = inject(ProfilMetierService);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly sortService = inject(SortService);
  private readonly modalService = inject(NgbModal);

  ngOnInit(): void {
    this.handleNavigation();
  }

  fullName(userManagement: IUserManagement): string {
    const fullName = [userManagement.firstName, userManagement.lastName].filter(Boolean).join(' ').trim();
    return fullName || userManagement.login;
  }

  formatValue(value: string | null | undefined): string {
    return value?.trim() ? value : '--';
  }

  formatAuthority(authority: string): string {
    return (
      {
        ROLE_ADMIN: 'Administrateur',
        ROLE_MANAGER_ADM: 'Manager ADM',
        ROLE_USER: 'Utilisateur',
      }[authority] ?? authority.replace(/^ROLE_/, '').replace(/_/g, ' ')
    );
  }

  clearFilters(): void {
    this.searchTerm.set('');
    this.selectedStatus.set('ALL');
    this.selectedAuthority.set('ALL');
  }

  setActive(userManagement: IUserManagement, isActivated: boolean): void {
    const request = isActivated ? this.userService.activate(userManagement.login) : this.userService.delete(userManagement.login);
    request.subscribe(() => this.loadAll());
  }

  trackIdentity(item: IUserManagement): number {
    return item.id!;
  }

  navigateToPage(page: number): void {
    this.page.set(page);
    this.transition();
  }

  deleteUser(userManagement: IUserManagement): void {
    const modalRef = this.modalService.open(UserManagementDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.userManagement = userManagement;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }

  loadAll(): void {
    this.isLoading.set(true);
    this.profilMetierService.query({ size: 1000 }).subscribe({
      next: profilResponse => {
        const managerAdmProfil = (profilResponse.body ?? []).find(profil => profil.code === MANAGER_ADM_PROFILE);
        if (!managerAdmProfil) {
          this.managerAdmUserIds.set(new Set());
          this.loadUsers();
          return;
        }

        this.affectationUtilisateurService.query({ 'profilId.equals': managerAdmProfil.id, size: 1000, sort: ['id,asc'] }).subscribe({
          next: affectationResponse => {
            this.managerAdmUserIds.set(
              new Set((affectationResponse.body ?? []).map(affectation => affectation.user?.id).filter(Boolean) as number[]),
            );
            this.loadUsers();
          },
          error: () => {
            this.managerAdmUserIds.set(new Set());
            this.loadUsers();
          },
        });
      },
      error: () => this.loadUsers(),
    });
  }

  private loadUsers(): void {
    this.userService
      .query({
        page: 0,
        size: 1000,
        sort: this.sortService.buildSortParam(this.sortState(), 'id'),
      })
      .subscribe({
        next: (res: HttpResponse<IUserManagement[]>) => {
          this.isLoading.set(false);
          this.onSuccess(res.body, res.headers);
        },
        error: () => this.isLoading.set(false),
      });
  }

  transition(sortState?: SortState): void {
    this.router.navigate(['./'], {
      relativeTo: this.activatedRoute.parent,
      queryParams: {
        page: this.page(),
        sort: this.sortService.buildSortParam(sortState ?? this.sortState()),
      },
    });
  }

  private handleNavigation(): void {
    combineLatest([this.activatedRoute.data, this.activatedRoute.queryParamMap]).subscribe(([data, params]) => {
      const page = params.get(PAGE_HEADER);
      this.page.set(+(page ?? 1));
      this.sortState.set(this.sortService.parseSortParam(params.get(SORT) ?? data.defaultSort));
      this.loadAll();
    });
  }

  private onSuccess(users: IUserManagement[] | null, headers: HttpHeaders): void {
    this.users.set(users ?? []);
    this.totalItems.set(this.managerAdmUsers().length || Number(headers.get(TOTAL_COUNT_RESPONSE_HEADER)));
  }
}
