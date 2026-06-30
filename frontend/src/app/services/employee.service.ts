import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { firstValueFrom, from, switchMap, timeout } from 'rxjs';

import {
  Employee,
  EmployeeCostHistory,
  EmployeeDetail,
  EmployeeFilters,
  EmployeeRequest,
} from '../models/employee.model';

@Injectable({ providedIn: 'root' })
export class EmployeeService {
  private readonly http = inject(HttpClient);
  private readonly oidcSecurityService = inject(OidcSecurityService);
  private readonly apiUrl = 'http://localhost:8081/api/employees';

  getEmployees(filters: EmployeeFilters = {}) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) => {
        let params = new HttpParams();
        if (filters.search) {
          params = params.set('search', filters.search);
        }
        if (filters.status) {
          params = params.set('status', filters.status);
        }
        return this.http.get<Employee[]>(this.apiUrl, { headers, params });
      }),
      timeout({ first: 5000 }),
    );
  }

  getEmployee(id: number) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) => this.http.get<EmployeeDetail>(`${this.apiUrl}/${id}`, { headers })),
      timeout({ first: 5000 }),
    );
  }

  getCostHistory(id: number) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) => this.http.get<EmployeeCostHistory[]>(`${this.apiUrl}/${id}/cost-history`, { headers })),
      timeout({ first: 5000 }),
    );
  }

  createEmployee(request: EmployeeRequest) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) => this.http.post<Employee>(this.apiUrl, request, { headers })),
      timeout({ first: 5000 }),
    );
  }

  updateEmployee(id: number, request: EmployeeRequest) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) => this.http.put<Employee>(`${this.apiUrl}/${id}`, request, { headers })),
      timeout({ first: 5000 }),
    );
  }

  deactivateEmployee(id: number) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) => this.http.patch<Employee>(`${this.apiUrl}/${id}/deactivate`, {}, { headers })),
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
