import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { EmployeeRequest } from '../../models/business.models';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-employee-edit',
  imports: [FormsModule, RouterLink],
  templateUrl: './employee-edit.html',
})
export class EmployeeEditComponent implements OnInit {
  private readonly apiService = inject(ApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly changeDetectorRef = inject(ChangeDetectorRef);

  employeeId = 0;
  message = '';
  employee: EmployeeRequest = {
    registrationNumber: '',
    fullName: '',
    qualification: '',
    contractType: 'CDI',
    hourlyCost: 0,
    status: 'ACTIVE',
  };

  ngOnInit() {
    this.employeeId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadEmployee();
  }

  loadEmployee() {
    this.message = 'Chargement du salarie...';

    this.apiService.getEmployee(this.employeeId).subscribe({
      next: (employee) => {
        this.employee = {
          registrationNumber: employee.registrationNumber,
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
        this.message = `Impossible de charger le salarie: ${error?.message || error?.statusText || 'erreur inconnue'}`;
        this.changeDetectorRef.detectChanges();
      },
    });
  }

  submit(form: NgForm) {
    if (form.invalid) {
      this.message = 'Veuillez remplir les champs obligatoires.';
      return;
    }

    this.message = 'Enregistrement du salarie...';

    this.apiService.updateEmployee(this.employeeId, {
      ...this.employee,
      hourlyCost: Number(this.employee.hourlyCost),
    }).subscribe({
      next: () => {
        this.message = 'Salarie enregistre.';
        this.changeDetectorRef.detectChanges();
        this.router.navigate(['/employees']);
      },
      error: (error) => {
        console.error('Employee update failed', error);
        this.message = `Impossible denregistrer le salarie: ${error?.message || error?.statusText || 'erreur inconnue'}`;
        this.changeDetectorRef.detectChanges();
      },
    });
  }
}
