import { inject, Injectable, signal } from '@angular/core';
import { Router, UrlTree } from '@angular/router';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { combineLatest, filter, map, Observable, take } from 'rxjs';

export type UserRole = 'DIRIGEANT' | 'CONDUCTEUR' | 'GESTIONNAIRE' | 'ADMIN';

@Injectable({ providedIn: 'root' })
export class AuthorizationService {
  private readonly oidcSecurityService = inject(OidcSecurityService);
  private readonly router = inject(Router);
  private readonly currentRoles = signal<UserRole[]>([]);

  constructor() {
    this.oidcSecurityService.getAccessToken().subscribe((accessToken) => {
      this.setRoles(this.extractRolesFromToken(accessToken));
    });
  }

  hasAnyRole(...roles: UserRole[]) {
    return roles.some((role) => this.currentRoles().includes(role));
  }

  canViewDashboard() {
    return this.hasAnyRole('DIRIGEANT', 'ADMIN');
  }

  canViewMargins() {
    return this.hasAnyRole('DIRIGEANT', 'ADMIN');
  }

  canManageProjects() {
    return this.hasAnyRole('DIRIGEANT', 'CONDUCTEUR', 'ADMIN');
  }

  canCloseOrArchiveProjects() {
    return this.hasAnyRole('DIRIGEANT', 'ADMIN');
  }

  canManageExpenses() {
    return this.hasAnyRole('CONDUCTEUR', 'GESTIONNAIRE', 'ADMIN');
  }

  canManagePointages() {
    return this.hasAnyRole('CONDUCTEUR', 'ADMIN');
  }

  canViewResources() {
    return this.hasAnyRole('DIRIGEANT', 'GESTIONNAIRE', 'ADMIN');
  }

  canManageResources() {
    return this.hasAnyRole('GESTIONNAIRE', 'ADMIN');
  }

  primaryRoleLabel() {
    if (this.currentRoles().includes('ADMIN')) return 'Administrateur';
    if (this.currentRoles().includes('DIRIGEANT')) return 'Dirigeant';
    if (this.currentRoles().includes('CONDUCTEUR')) return 'Conducteur';
    if (this.currentRoles().includes('GESTIONNAIRE')) return 'Gestionnaire';
    return 'Aucun role';
  }

  authorize(requiredRoles: UserRole[]): Observable<boolean | UrlTree> {
    return combineLatest([
      this.oidcSecurityService.isAuthenticated$,
      this.oidcSecurityService.getAccessToken(),
    ]).pipe(
      filter(([authentication, accessToken]) => !authentication.isAuthenticated || Boolean(accessToken)),
      take(1),
      map(([authentication, accessToken]) => {
        if (!authentication.isAuthenticated) {
          return true;
        }

        const roles = this.extractRolesFromToken(accessToken);
        this.setRoles(roles);
        return requiredRoles.some((role) => roles.includes(role))
          ? true
          : this.router.createUrlTree(['/projects']);
      }),
    );
  }

  clear() {
    this.currentRoles.set([]);
  }

  private extractRolesFromToken(accessToken: string): UserRole[] {
    if (!accessToken) {
      return [];
    }

    try {
      const encodedPayload = accessToken.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
      const paddedPayload = encodedPayload.padEnd(Math.ceil(encodedPayload.length / 4) * 4, '=');
      const payload = JSON.parse(atob(paddedPayload)) as { realm_access?: { roles?: string[] } };
      return this.filterApplicationRoles(payload.realm_access?.roles ?? []);
    } catch {
      return [];
    }
  }

  private filterApplicationRoles(realmRoles: string[]): UserRole[] {
    const applicationRoles: UserRole[] = ['DIRIGEANT', 'CONDUCTEUR', 'GESTIONNAIRE', 'ADMIN'];
    return realmRoles.filter((role): role is UserRole => applicationRoles.includes(role as UserRole));
  }

  private setRoles(roles: UserRole[]) {
    this.currentRoles.set(roles);
  }
}
