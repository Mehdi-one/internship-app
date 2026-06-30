import { Component, inject } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { filter } from 'rxjs';
import { AuthorizationService } from './services/authorization.service';

@Component({
  selector: 'app-root',
  imports: [RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  private readonly oidcSecurityService = inject(OidcSecurityService);
  private readonly router = inject(Router);
  readonly authorization = inject(AuthorizationService);

  isAuthenticated = false;
  userName = '';
  pageTitle = 'Tableau de bord';

  constructor() {
    this.oidcSecurityService.isAuthenticated$.subscribe(({ isAuthenticated }) => {
      this.isAuthenticated = isAuthenticated;
    });

    this.oidcSecurityService.userData$.subscribe(({ userData }) => {
      this.userName = userData?.preferred_username ?? '';
      if (userData && !this.authorization.canViewDashboard() && this.router.url.startsWith('/dashboard')) {
        this.router.navigate(['/projects']);
      }
    });

    this.router.events.pipe(filter((event) => event instanceof NavigationEnd)).subscribe((event) => {
      this.pageTitle = this.resolvePageTitle(event.urlAfterRedirects);
    });
  }

  login() {
    this.oidcSecurityService.authorize();
  }

  logout() {
    this.oidcSecurityService.logoff().subscribe({
      next: () => this.clearLocalSession(),
      error: () => {
        this.oidcSecurityService.logoffLocal();
        this.clearLocalSession();
      },
    });
  }

  private clearLocalSession() {
    this.authorization.clear();
    this.isAuthenticated = false;
    this.userName = '';
  }

  private resolvePageTitle(url: string) {
    if (url.startsWith('/projects/new')) return 'Nouveau marche';
    if (url.startsWith('/expenses')) return 'Depenses';
    if (url.startsWith('/projects')) return 'Marches';
    if (url.startsWith('/employees/new')) return 'Nouveau salarie';
    if (url.startsWith('/employees')) return 'Salaries';
    if (url.startsWith('/equipment/new')) return 'Nouveau materiel';
    if (url.startsWith('/equipment')) return 'Materiel';
    return 'Tableau de bord';
  }
}
