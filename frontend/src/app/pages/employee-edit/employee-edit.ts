import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { EmployeeFormComponent } from '../../components/employee-form/employee-form';
import { EmployeeRequest } from '../../models/employee.model';
import { EmployeeService } from '../../services/employee.service';

@Component({
  selector: 'app-employee-edit',
  imports: [RouterLink, EmployeeFormComponent],
  templateUrl: './employee-edit.html',
})
export class EmployeeEditComponent implements OnInit {
  private readonly employeeService = inject(EmployeeService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly changeDetectorRef = inject(ChangeDetectorRef);

  employeeId = 0;
  employee: EmployeeRequest | null = null;
  message = '';

  ngOnInit() {
    this.employeeId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadEmployee();
  }

  loadEmployee() {
    this.message = 'Chargement du salarié...';
    this.employeeService.getEmployee(this.employeeId).subscribe({
      next: (employee) => {
        this.employee = {
          matricule: employee.matricule,
          fullName: employee.fullName,
          qualification: employee.qualification,
          contractType: employee.contractType,
          hourlyCost: employee.hourlyCost,
          status: employee.status,
        };
        this.message = '';
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Employee loading failed', error);
        this.message = `Impossible de charger le salarié : ${error?.error?.message || 'erreur inconnue'}`;
        this.changeDetectorRef.detectChanges();
      },
    });
  }

  updateEmployee(request: EmployeeRequest) {
    this.message = 'Enregistrement du salarié...';
    this.employeeService.updateEmployee(this.employeeId, request).subscribe({
      next: () => this.router.navigate(['/employees', this.employeeId]),
      error: (error) => {
        console.error('Employee update failed', error);
        this.message = `Impossible d'enregistrer le salarié : ${error?.error?.message || 'erreur inconnue'}`;
        this.changeDetectorRef.detectChanges();
      },
    });
  }
}
