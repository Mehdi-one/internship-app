import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { firstValueFrom, from, switchMap, timeout } from 'rxjs';

import { FuelLog, FuelLogRequest } from '../models/fuel-log.model';

@Injectable({ providedIn: 'root' })
export class FuelLogService {
  private readonly http = inject(HttpClient);
  private readonly oidcSecurityService = inject(OidcSecurityService);
  private readonly apiUrl = 'http://localhost:8081/api/equipment';

  getFuelLogs(equipmentId: number) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) => this.http.get<FuelLog[]>(`${this.apiUrl}/${equipmentId}/fuel-logs`, { headers })),
      timeout({ first: 5000 }),
    );
  }

  addFuelLog(equipmentId: number, request: FuelLogRequest) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) => this.http.post<FuelLog>(`${this.apiUrl}/${equipmentId}/fuel-logs`, request, { headers })),
      timeout({ first: 5000 }),
    );
  }

  private async getAuthHeaders(): Promise<HttpHeaders> {
    const token = await firstValueFrom(this.oidcSecurityService.getAccessToken().pipe(timeout({ first: 5000 })));
    if (!token) {
      throw new Error('No access token found. Please logout and login again.');
    }
    return new HttpHeaders({ Authorization: `Bearer ${token}` });
  }
}
