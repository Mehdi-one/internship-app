import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { firstValueFrom, from, switchMap, timeout } from 'rxjs';

import {
  EmployeeAssignment,
  EmployeeAssignmentRequest,
  Equipment,
  EquipmentAssignment,
  EquipmentAssignmentRequest,
  EquipmentFilters,
  EquipmentRequest,
  Expense,
  ExpenseDocument,
  ExpenseDocumentType,
  ExpenseFilters,
  ExpenseRequest,
  FinancialSummary,
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

  getFinancialSummary(id: number) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) => this.http.get<FinancialSummary>(`${this.apiUrl}/projects/${id}/financial-summary`, { headers })),
      timeout({ first: 5000 }),
    );
  }

  getProjectExpenses(projectId: number) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) => this.http.get<Expense[]>(`${this.apiUrl}/projects/${projectId}/expenses`, { headers })),
      timeout({ first: 5000 }),
    );
  }

  getExpenses(filters: ExpenseFilters = {}) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) => {
        let params = new HttpParams();

        if (filters.search) {
          params = params.set('search', filters.search);
        }
        if (filters.category) {
          params = params.set('category', filters.category);
        }
        if (filters.status) {
          params = params.set('status', filters.status);
        }

        return this.http.get<Expense[]>(`${this.apiUrl}/expenses`, { headers, params });
      }),
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

  invoiceExpense(id: number) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) => this.http.patch<Expense>(`${this.apiUrl}/expenses/${id}/invoice`, {}, { headers })),
      timeout({ first: 5000 }),
    );
  }

  payExpense(id: number) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) => this.http.patch<Expense>(`${this.apiUrl}/expenses/${id}/pay`, {}, { headers })),
      timeout({ first: 5000 }),
    );
  }

  cancelExpense(id: number) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) => this.http.patch<Expense>(`${this.apiUrl}/expenses/${id}/cancel`, {}, { headers })),
      timeout({ first: 5000 }),
    );
  }

  uploadExpenseDocument(id: number, documentType: ExpenseDocumentType, file: File) {
    const formData = new FormData();
    formData.append('documentType', documentType);
    formData.append('file', file);

    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) =>
        this.http.post<ExpenseDocument>(`${this.apiUrl}/expenses/${id}/documents`, formData, { headers }),
      ),
      timeout({ first: 10000 }),
    );
  }

  downloadExpenseDocument(id: number) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) =>
        this.http.get(`${this.apiUrl}/expense-documents/${id}/download`, { headers, responseType: 'blob' }),
      ),
      timeout({ first: 10000 }),
    );
  }

  getEmployeeAssignments(projectId: number) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) =>
        this.http.get<EmployeeAssignment[]>(`${this.apiUrl}/projects/${projectId}/employee-assignments`, { headers }),
      ),
      timeout({ first: 5000 }),
    );
  }

  createEmployeeAssignment(projectId: number, request: EmployeeAssignmentRequest) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) =>
        this.http.post<EmployeeAssignment>(`${this.apiUrl}/projects/${projectId}/employee-assignments`, request, {
          headers,
        }),
      ),
      timeout({ first: 5000 }),
    );
  }

  updateEmployeeAssignment(id: number, request: EmployeeAssignmentRequest) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) =>
        this.http.put<EmployeeAssignment>(`${this.apiUrl}/employee-assignments/${id}`, request, { headers }),
      ),
      timeout({ first: 5000 }),
    );
  }

  validateEmployeeAssignment(id: number) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) => this.http.patch<EmployeeAssignment>(`${this.apiUrl}/employee-assignments/${id}/validate`, {}, { headers })),
      timeout({ first: 5000 }),
    );
  }

  cancelEmployeeAssignment(id: number) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) => this.http.patch<EmployeeAssignment>(`${this.apiUrl}/employee-assignments/${id}/cancel`, {}, { headers })),
      timeout({ first: 5000 }),
    );
  }

  getEquipmentAssignments(projectId: number) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) =>
        this.http.get<EquipmentAssignment[]>(`${this.apiUrl}/projects/${projectId}/equipment-assignments`, { headers }),
      ),
      timeout({ first: 5000 }),
    );
  }

  createEquipmentAssignment(projectId: number, request: EquipmentAssignmentRequest) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) =>
        this.http.post<EquipmentAssignment>(`${this.apiUrl}/projects/${projectId}/equipment-assignments`, request, {
          headers,
        }),
      ),
      timeout({ first: 5000 }),
    );
  }

  updateEquipmentAssignment(id: number, request: EquipmentAssignmentRequest) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) =>
        this.http.put<EquipmentAssignment>(`${this.apiUrl}/equipment-assignments/${id}`, request, { headers }),
      ),
      timeout({ first: 5000 }),
    );
  }

  validateEquipmentAssignment(id: number) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) => this.http.patch<EquipmentAssignment>(`${this.apiUrl}/equipment-assignments/${id}/validate`, {}, { headers })),
      timeout({ first: 5000 }),
    );
  }

  cancelEquipmentAssignment(id: number) {
    return from(this.getAuthHeaders()).pipe(
      switchMap((headers) => this.http.patch<EquipmentAssignment>(`${this.apiUrl}/equipment-assignments/${id}/cancel`, {}, { headers })),
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
