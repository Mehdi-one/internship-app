import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { firstValueFrom, from, switchMap, timeout } from 'rxjs';

import {
  Employee,
  EmployeeFilters,
  EmployeeRequest,
  Equipment,
  EquipmentFilters,
  EquipmentRequest,
  Expense,
  ExpenseRequest,
  ProjectFilters,
  ProjectLot,
  ProjectLotRequest,
  Project,
  ProjectRequest,
  ProjectSummary,
} from '../models/business.models';

@Injectable({
  providedIn: 'root',
})
export class ApiService {
  private readonly http = inject(HttpClient);
  private readonly oidcSecurityService = inject(OidcSecurityService);
  private readonly apiUrl = 'http://localhost:8081/api';

  getEquipment(filters: EquipmentFilters = {}) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) => {
        let params = new HttpParams();

        if (filters.search) {
          params = params.set('search', filters.search);
        }

        if (filters.status) {
          params = params.set('status', filters.status);
        }

        return this.http.get<Equipment[]>(`${this.apiUrl}/equipment`, { headers, params });
      }),
      timeout({ first: 5000 }),
    );
  }

  getEquipmentById(id: number) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) => this.http.get<Equipment>(`${this.apiUrl}/equipment/${id}`, { headers })),
      timeout({ first: 5000 }),
    );
  }

  createEquipment(request: EquipmentRequest) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) => this.http.post<Equipment>(`${this.apiUrl}/equipment`, request, { headers })),
      timeout({ first: 5000 }),
    );
  }

  updateEquipment(id: number, request: EquipmentRequest) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) => this.http.put<Equipment>(`${this.apiUrl}/equipment/${id}`, request, { headers })),
      timeout({ first: 5000 }),
    );
  }

  reformEquipment(id: number) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) => this.http.patch<Equipment>(`${this.apiUrl}/equipment/${id}/reform`, {}, { headers })),
      timeout({ first: 5000 }),
    );
  }

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

        return this.http.get<Employee[]>(`${this.apiUrl}/employees`, { headers, params });
      }),
      timeout({ first: 5000 }),
    );
  }

  getEmployee(id: number) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) => this.http.get<Employee>(`${this.apiUrl}/employees/${id}`, { headers })),
      timeout({ first: 5000 }),
    );
  }

  createEmployee(request: EmployeeRequest) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) => this.http.post<Employee>(`${this.apiUrl}/employees`, request, { headers })),
      timeout({ first: 5000 }),
    );
  }

  updateEmployee(id: number, request: EmployeeRequest) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) => this.http.put<Employee>(`${this.apiUrl}/employees/${id}`, request, { headers })),
      timeout({ first: 5000 }),
    );
  }

  deactivateEmployee(id: number) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) => this.http.patch<Employee>(`${this.apiUrl}/employees/${id}/deactivate`, {}, { headers })),
      timeout({ first: 5000 }),
    );
  }

  getProjects(filters: ProjectFilters = {}) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) => {
        let params = new HttpParams();

        if (filters.search) {
          params = params.set('search', filters.search);
        }

        if (filters.status) {
          params = params.set('status', filters.status);
        }

        return this.http.get<Project[]>(`${this.apiUrl}/projects`, { headers, params });
      }),
      timeout({ first: 5000 }),
    );
  }

  getProject(id: number) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) => this.http.get<Project>(`${this.apiUrl}/projects/${id}`, { headers })),
      timeout({ first: 5000 }),
    );
  }

  createProject(request: ProjectRequest) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) => this.http.post<Project>(`${this.apiUrl}/projects`, request, { headers })),
      timeout({ first: 5000 }),
    );
  }

  updateProject(id: number, request: ProjectRequest) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) => this.http.put<Project>(`${this.apiUrl}/projects/${id}`, request, { headers })),
      timeout({ first: 5000 }),
    );
  }

  closeProject(id: number) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) => this.http.patch<Project>(`${this.apiUrl}/projects/${id}/close`, {}, { headers })),
      timeout({ first: 5000 }),
    );
  }

  archiveProject(id: number) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) => this.http.patch<Project>(`${this.apiUrl}/projects/${id}/archive`, {}, { headers })),
      timeout({ first: 5000 }),
    );
  }

  getProjectLots(projectId: number) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) => this.http.get<ProjectLot[]>(`${this.apiUrl}/projects/${projectId}/lots`, { headers })),
      timeout({ first: 5000 }),
    );
  }

  createProjectLot(projectId: number, request: ProjectLotRequest) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) => this.http.post<ProjectLot>(`${this.apiUrl}/projects/${projectId}/lots`, request, { headers })),
      timeout({ first: 5000 }),
    );
  }

  archiveProjectLot(id: number) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) => this.http.patch<ProjectLot>(`${this.apiUrl}/project-lots/${id}/archive`, {}, { headers })),
      timeout({ first: 5000 }),
    );
  }

  getProjectSummary(id: number) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) => this.http.get<ProjectSummary>(`${this.apiUrl}/projects/${id}/summary`, { headers })),
      timeout({ first: 5000 }),
    );
  }

  getProjectExpenses(projectId: number) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) => this.http.get<Expense[]>(`${this.apiUrl}/projects/${projectId}/expenses`, { headers })),
      timeout({ first: 5000 }),
    );
  }

  createExpense(projectId: number, request: ExpenseRequest) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) =>
        this.http.post<Expense>(`${this.apiUrl}/projects/${projectId}/expenses`, request, { headers }),
      ),
      timeout({ first: 5000 }),
    );
  }

  updateExpense(id: number, request: ExpenseRequest) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) => this.http.put<Expense>(`${this.apiUrl}/expenses/${id}`, request, { headers })),
      timeout({ first: 5000 }),
    );
  }

  cancelExpense(id: number) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) => this.http.patch<Expense>(`${this.apiUrl}/expenses/${id}/cancel`, {}, { headers })),
      timeout({ first: 5000 }),
    );
  }

  private async getAuthHeaders(): Promise<HttpHeaders> {
    const token = await firstValueFrom(this.oidcSecurityService.getAccessToken().pipe(timeout({ first: 5000 })));

    if (!token) {
      throw new Error('No access token found. Please logout and login again.');
    }

    return new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });
  }
}
