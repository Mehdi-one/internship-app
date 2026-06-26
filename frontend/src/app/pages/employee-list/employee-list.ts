import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { Employee, EmployeeFilters, EmployeeStatus } from '../../models/business.models';
import { LabelFrPipe } from '../../pipes/label-fr.pipe';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-employee-list',
  imports: [FormsModule, RouterLink, LabelFrPipe],
  templateUrl: './employee-list.html',
})
export class EmployeeListComponent implements OnInit {
  private readonly apiService = inject(ApiService);
  private readonly changeDetectorRef = inject(ChangeDetectorRef);

  employees: Employee[] = [];
  message = '';
  filters: EmployeeFilters = {
    search: '',
    status: '',
  };
  statuses: EmployeeStatus[] = ['ACTIVE', 'INACTIVE'];

  ngOnInit() {
    this.loadEmployees();
  }

  loadEmployees() {
    this.message = 'Chargement des salaries...';

    this.apiService.getEmployees(this.filters).subscribe({
      next: (employees) => {
        this.employees = employees;
        this.message = employees.length === 0 ? 'Aucun salarie pour le moment.' : '';
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Employee loading failed', error);
        this.message = `Impossible de charger les salaries: ${error?.message || error?.statusText || 'erreur inconnue'}`;
        this.changeDetectorRef.detectChanges();
      },
    });
  }

  deactivateEmployee(id: number) {
    this.message = 'Desactivation du salarie...';

    this.apiService.deactivateEmployee(id).subscribe({
      next: () => {
        this.message = 'Salarie desactive.';
        this.loadEmployees();
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Employee deactivation failed', error);
        this.message = `Impossible de desactiver le salarie: ${error?.message || error?.statusText || 'erreur inconnue'}`;
        this.changeDetectorRef.detectChanges();
      },
    });
  }
}
