import { Component, inject } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { filter } from 'rxjs';

@Component({
  selector: 'app-root',
  imports: [RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  private readonly oidcSecurityService = inject(OidcSecurityService);
  private readonly router = inject(Router);

  isAuthenticated = false;
  userName = '';
  pageTitle = 'Tableau de bord';

  constructor() {
    this.oidcSecurityService.isAuthenticated$.subscribe(({ isAuthenticated }) => {
      this.isAuthenticated = isAuthenticated;
    });

    this.oidcSecurityService.userData$.subscribe(({ userData }) => {
      this.userName = userData?.preferred_username ?? '';
    });

    this.router.events.pipe(filter((event) => event instanceof NavigationEnd)).subscribe((event) => {
      this.pageTitle = this.resolvePageTitle(event.urlAfterRedirects);
    });
  }

  login() {
    this.oidcSecurityService.authorize();
  }

  logout() {
    this.oidcSecurityService.logoffLocal();
    this.isAuthenticated = false;
    this.userName = '';
  }

  private resolvePageTitle(url: string) {
    if (url.startsWith('/projects/new')) return 'Nouveau marche';
    if (url.startsWith('/projects')) return 'Marches';
    if (url.startsWith('/employees/new')) return 'Nouveau salarie';
    if (url.startsWith('/employees')) return 'Salaries';
    if (url.startsWith('/equipment/new')) return 'Nouveau materiel';
    if (url.startsWith('/equipment')) return 'Materiel';
    return 'Tableau de bord';
  }
}
