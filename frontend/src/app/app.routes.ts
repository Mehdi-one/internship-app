import { Routes } from '@angular/router';

import { DashboardComponent } from './pages/dashboard/dashboard';
import { EmployeeCreateComponent } from './pages/employee-create/employee-create';
import { EmployeeDetailComponent } from './pages/employee-detail/employee-detail';
import { EmployeeEditComponent } from './pages/employee-edit/employee-edit';
import { EmployeeListComponent } from './pages/employee-list/employee-list';
import { EquipmentCreateComponent } from './pages/equipment-create/equipment-create';
import { EquipmentEditComponent } from './pages/equipment-edit/equipment-edit';
import { EquipmentListComponent } from './pages/equipment-list/equipment-list';
import { ExpenseListComponent } from './pages/expense-list/expense-list';
import { ProjectCreateComponent } from './pages/project-create/project-create';
import { ProjectDetailsComponent } from './pages/project-details/project-details';
import { ProjectEditComponent } from './pages/project-edit/project-edit';
import { ProjectListComponent } from './pages/project-list/project-list';
import { roleGuard } from './guards/role.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'projects', pathMatch: 'full' },
  { path: 'dashboard', component: DashboardComponent, canActivate: [roleGuard], data: { roles: ['DIRIGEANT', 'ADMIN'] } },
  { path: 'employees', component: EmployeeListComponent, canActivate: [roleGuard], data: { roles: ['DIRIGEANT', 'GESTIONNAIRE', 'ADMIN'] } },
  { path: 'employees/new', component: EmployeeCreateComponent, canActivate: [roleGuard], data: { roles: ['GESTIONNAIRE', 'ADMIN'] } },
  { path: 'employees/:id/edit', component: EmployeeEditComponent, canActivate: [roleGuard], data: { roles: ['GESTIONNAIRE', 'ADMIN'] } },
  { path: 'employees/:id', component: EmployeeDetailComponent, canActivate: [roleGuard], data: { roles: ['DIRIGEANT', 'GESTIONNAIRE', 'ADMIN'] } },
  { path: 'equipment', component: EquipmentListComponent, canActivate: [roleGuard], data: { roles: ['DIRIGEANT', 'GESTIONNAIRE', 'ADMIN'] } },
  { path: 'equipment/new', component: EquipmentCreateComponent, canActivate: [roleGuard], data: { roles: ['GESTIONNAIRE', 'ADMIN'] } },
  { path: 'equipment/:id/edit', component: EquipmentEditComponent, canActivate: [roleGuard], data: { roles: ['GESTIONNAIRE', 'ADMIN'] } },
  { path: 'expenses', component: ExpenseListComponent },
  { path: 'projects', component: ProjectListComponent },
  { path: 'projects/new', component: ProjectCreateComponent, canActivate: [roleGuard], data: { roles: ['DIRIGEANT', 'CONDUCTEUR', 'ADMIN'] } },
  { path: 'projects/:id/edit', component: ProjectEditComponent, canActivate: [roleGuard], data: { roles: ['DIRIGEANT', 'CONDUCTEUR', 'ADMIN'] } },
  { path: 'projects/:id', component: ProjectDetailsComponent },
];
