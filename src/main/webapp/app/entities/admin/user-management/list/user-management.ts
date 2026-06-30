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
import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { ItemCount } from 'app/shared/pagination';
import { SortByDirective, SortDirective, SortService, SortState, sortStateSignal } from 'app/shared/sort';
import { UserManagementDeleteDialog } from '../delete/user-management-delete-dialog';
import { UserManagementService } from '../service/user-management.service';
import { IUserManagement } from '../user-management.model';

@Component({
  selector: 'jhi-user-mgmt',
  templateUrl: './user-management.html',
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

    return this.users().filter(user => {
      const matchesSearch =
        !searchTerm ||
        [user.login, user.email, user.firstName, user.lastName].filter(Boolean).some(value => value!.toLowerCase().includes(searchTerm));
      const matchesStatus = selectedStatus === 'ALL' || (selectedStatus === 'ACTIVE' ? user.activated === true : user.activated === false);
      const matchesAuthority = selectedAuthority === 'ALL' || (user.authorities ?? []).includes(selectedAuthority);

      return matchesSearch && matchesStatus && matchesAuthority;
    });
  });
  readonly activeCount = computed(() => this.users().filter(user => user.activated).length);
  readonly inactiveCount = computed(() => this.users().filter(user => !user.activated).length);
  readonly adminCount = computed(() => this.users().filter(user => (user.authorities ?? []).includes('ROLE_ADMIN')).length);
  readonly availableAuthorities = computed(() =>
    [...new Set(this.users().flatMap(user => user.authorities ?? []))].sort((left, right) => left.localeCompare(right)),
  );

  private readonly userService = inject(UserManagementService);
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
    this.userService.update({ ...userManagement, activated: isActivated }).subscribe(() => this.loadAll());
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
    this.userService
      .query({
        page: this.page() - 1,
        size: this.itemsPerPage(),
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
    this.totalItems.set(Number(headers.get(TOTAL_COUNT_RESPONSE_HEADER)));
    this.users.set(users ?? []);
  }
}
