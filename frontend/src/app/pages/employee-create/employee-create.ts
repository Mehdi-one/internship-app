import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { EmployeeFormComponent } from '../../components/employee-form/employee-form';
import { EmployeeRequest } from '../../models/employee.model';
import { EmployeeService } from '../../services/employee.service';

@Component({
  selector: 'app-employee-create',
  imports: [RouterLink, EmployeeFormComponent],
  templateUrl: './employee-create.html',
})
export class EmployeeCreateComponent {
  private readonly employeeService = inject(EmployeeService);
  private readonly router = inject(Router);
  private readonly changeDetectorRef = inject(ChangeDetectorRef);

  message = '';

  createEmployee(request: EmployeeRequest) {
    this.message = 'Création du salarié...';
    this.employeeService.createEmployee(request).subscribe({
      next: (employee) => {
        this.router.navigate(['/employees', employee.id]);
      },
      error: (error) => {
        console.error('Employee creation failed', error);
        this.message = `Impossible de créer le salarié : ${error?.error?.message || 'vérifiez les champs et le matricule unique'}`;
        this.changeDetectorRef.detectChanges();
      },
    });
  }
}
