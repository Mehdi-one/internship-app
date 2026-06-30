import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';

import { Equipment, Project, ProjectSummary } from '../../models/business.models';
import { Employee } from '../../models/employee.model';
import { LabelFrPipe } from '../../pipes/label-fr.pipe';
import { ApiService } from '../../services/api.service';
import { EmployeeService } from '../../services/employee.service';

@Component({
  selector: 'app-dashboard',
  imports: [RouterLink, LabelFrPipe],
  templateUrl: './dashboard.html',
})
export class DashboardComponent implements OnInit {
  private readonly apiService = inject(ApiService);
  private readonly employeeService = inject(EmployeeService);
  private readonly changeDetectorRef = inject(ChangeDetectorRef);

  message = '';
  totalProjects = 0;
  totalBudget = 0;
  totalExpenses = 0;
  activeEmployees = 0;
  availableEquipment = 0;
  recentProjects: Project[] = [];
  recentEmployees: Employee[] = [];
  recentEquipment: Equipment[] = [];

  ngOnInit() {
    this.loadDashboard();
  }

  loadDashboard() {
    this.message = 'Chargement du tableau de bord...';

    forkJoin({
      projects: this.apiService.getProjects(),
      employees: this.employeeService.getEmployees(),
      equipment: this.apiService.getEquipment(),
    })
      .pipe(
        switchMap(({ projects, employees, equipment }) => {
          this.totalProjects = projects.length;
          this.totalBudget = projects.reduce((total, project) => total + Number(project.estimatedDryCost || 0), 0);
          this.activeEmployees = employees.filter((employee) => employee.status === 'ACTIVE').length;
          this.availableEquipment = equipment.filter((item) => item.status === 'AVAILABLE').length;
          this.recentProjects = projects.slice(0, 5);
          this.recentEmployees = employees.slice(0, 3);
          this.recentEquipment = equipment.slice(0, 3);

          if (projects.length === 0) {
            return of([] as ProjectSummary[]);
          }

          return forkJoin(
            projects.map((project) =>
              this.apiService.getProjectSummary(project.id).pipe(catchError(() => of(null))),
            ),
          );
        }),
      )
      .subscribe({
        next: (summaries) => {
          this.totalExpenses = summaries
            .filter((summary): summary is ProjectSummary => summary !== null)
            .reduce((total, summary) => total + Number(summary.totalExpenses || 0), 0);
          this.message = '';
          this.changeDetectorRef.detectChanges();
        },
        error: (error) => {
          console.error('Dashboard loading failed', error);
          this.message = `Impossible de charger le tableau de bord: ${error?.message || error?.statusText || 'erreur inconnue'}`;
          this.changeDetectorRef.detectChanges();
        },
      });
  }

  formatMoney(value: number) {
    return new Intl.NumberFormat('fr-MA', {
      maximumFractionDigits: 0,
    }).format(value);
  }

}
