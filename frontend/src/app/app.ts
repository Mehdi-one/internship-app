import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { OidcSecurityService } from 'angular-auth-oidc-client';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  private readonly oidcSecurityService = inject(OidcSecurityService);
  private readonly http = inject(HttpClient);

  isAuthenticated = false;
  userName = '';
  publicApiResponse = '';
  privateApiResponse = '';

  constructor() {
    this.oidcSecurityService.isAuthenticated$.subscribe(({ isAuthenticated }) => {
      this.isAuthenticated = isAuthenticated;
    });

    this.oidcSecurityService.userData$.subscribe(({ userData }) => {
      this.userName = userData?.preferred_username ?? '';
    });
  }

  login() {
    this.oidcSecurityService.authorize();
  }

  logout() {
    this.oidcSecurityService.logoffLocal();
    this.isAuthenticated = false;
    this.userName = '';
    this.publicApiResponse = '';
    this.privateApiResponse = '';
  }

  callPublicApi() {
    this.http.get<{ message: string }>('http://localhost:8081/api/public').subscribe({
      next: (response) => {
        this.publicApiResponse = response.message;
      },
      error: () => {
        this.publicApiResponse = 'Public API failed. Check if Spring Boot is running on port 8081.';
      },
    });
  }

  callPrivateApi() {
    if (!this.isAuthenticated) {
      this.privateApiResponse = 'Login first, then try the private API.';
      return;
    }

    this.oidcSecurityService.getAccessToken().subscribe((token) => {
      if (!token) {
        this.privateApiResponse = 'No token found. Login again.';
        return;
      }

      const headers = new HttpHeaders({
        Authorization: `Bearer ${token}`,
      });

      this.http
        .get<{ message: string; user: string }>('http://localhost:8081/api/private', { headers })
        .subscribe({
          next: (response) => {
            this.privateApiResponse = `${response.message} - user: ${response.user}`;
          },
          error: () => {
            this.privateApiResponse = 'Private API failed. Check backend, Keycloak, or login token.';
          },
        });
    });
  }
}
