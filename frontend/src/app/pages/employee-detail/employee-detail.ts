import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { EmployeeDetail } from '../../models/employee.model';
import { LabelFrPipe } from '../../pipes/label-fr.pipe';
import { AuthorizationService } from '../../services/authorization.service';
import { EmployeeService } from '../../services/employee.service';

@Component({
  selector: 'app-employee-detail',
  imports: [RouterLink, LabelFrPipe],
  templateUrl: './employee-detail.html',
})
export class EmployeeDetailComponent implements OnInit {
  private readonly employeeService = inject(EmployeeService);
  private readonly route = inject(ActivatedRoute);
  private readonly changeDetectorRef = inject(ChangeDetectorRef);
  readonly authorization = inject(AuthorizationService);

  employeeId = 0;
  employee: EmployeeDetail | null = null;
  message = '';

  ngOnInit() {
    this.employeeId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadEmployee();
  }

  loadEmployee() {
    this.message = 'Chargement du salarié...';
    this.employeeService.getEmployee(this.employeeId).subscribe({
      next: (employee) => {
        this.employee = employee;
        this.message = '';
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Employee detail loading failed', error);
        this.message = `Impossible de charger le salarié : ${error?.error?.message || 'erreur inconnue'}`;
        this.changeDetectorRef.detectChanges();
      },
    });
  }

  deactivateEmployee() {
    this.message = 'Désactivation du salarié...';
    this.employeeService.deactivateEmployee(this.employeeId).subscribe({
      next: () => this.loadEmployee(),
      error: (error) => {
        console.error('Employee deactivation failed', error);
        this.message = `Impossible de désactiver le salarié : ${error?.error?.message || 'erreur inconnue'}`;
        this.changeDetectorRef.detectChanges();
      },
    });
  }
}
