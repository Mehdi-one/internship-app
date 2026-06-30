import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { Employee, EmployeeFilters, EmployeeStatus } from '../../models/employee.model';
import { LabelFrPipe } from '../../pipes/label-fr.pipe';
import { AuthorizationService } from '../../services/authorization.service';
import { EmployeeService } from '../../services/employee.service';

@Component({
  selector: 'app-employee-list',
  imports: [FormsModule, RouterLink, LabelFrPipe],
  templateUrl: './employee-list.html',
})
export class EmployeeListComponent implements OnInit {
  private readonly employeeService = inject(EmployeeService);
  private readonly changeDetectorRef = inject(ChangeDetectorRef);
  readonly authorization = inject(AuthorizationService);

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

    this.employeeService.getEmployees(this.filters).subscribe({
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

    this.employeeService.deactivateEmployee(id).subscribe({
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
