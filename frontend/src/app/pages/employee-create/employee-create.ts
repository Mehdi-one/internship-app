import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { EmployeeRequest } from '../../models/business.models';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-employee-create',
  imports: [FormsModule, RouterLink],
  templateUrl: './employee-create.html',
})
export class EmployeeCreateComponent {
  private readonly apiService = inject(ApiService);
  private readonly router = inject(Router);
  private readonly changeDetectorRef = inject(ChangeDetectorRef);

  message = '';
  employee: EmployeeRequest = {
    registrationNumber: '',
    fullName: '',
    qualification: '',
    contractType: 'CDI',
    hourlyCost: 0,
    status: 'ACTIVE',
  };

  submit(form: NgForm) {
    if (form.invalid) {
      this.message = 'Veuillez remplir les champs obligatoires.';
      return;
    }

    this.message = 'Creation du salarie...';

    this.apiService.createEmployee({
      ...this.employee,
      hourlyCost: Number(this.employee.hourlyCost),
    }).subscribe({
      next: () => {
        this.message = 'Salarie cree.';
        this.changeDetectorRef.detectChanges();
        this.router.navigate(['/employees']);
      },
      error: (error) => {
        console.error('Employee creation failed', error);
        this.message = `Impossible de creer le salarie: ${error?.message || error?.statusText || 'verifiez les champs obligatoires et le matricule unique'}`;
        this.changeDetectorRef.detectChanges();
      },
    });
  }
}
