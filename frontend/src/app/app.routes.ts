import { Routes } from '@angular/router';

import { DashboardComponent } from './pages/dashboard/dashboard';
import { EmployeeCreateComponent } from './pages/employee-create/employee-create';
import { EmployeeEditComponent } from './pages/employee-edit/employee-edit';
import { EmployeeListComponent } from './pages/employee-list/employee-list';
import { EquipmentCreateComponent } from './pages/equipment-create/equipment-create';
import { EquipmentEditComponent } from './pages/equipment-edit/equipment-edit';
import { EquipmentListComponent } from './pages/equipment-list/equipment-list';
import { ProjectCreateComponent } from './pages/project-create/project-create';
import { ProjectDetailsComponent } from './pages/project-details/project-details';
import { ProjectEditComponent } from './pages/project-edit/project-edit';
import { ProjectListComponent } from './pages/project-list/project-list';

export const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'employees', component: EmployeeListComponent },
  { path: 'employees/new', component: EmployeeCreateComponent },
  { path: 'employees/:id/edit', component: EmployeeEditComponent },
  { path: 'equipment', component: EquipmentListComponent },
  { path: 'equipment/new', component: EquipmentCreateComponent },
  { path: 'equipment/:id/edit', component: EquipmentEditComponent },
  { path: 'projects', component: ProjectListComponent },
  { path: 'projects/new', component: ProjectCreateComponent },
  { path: 'projects/:id/edit', component: ProjectEditComponent },
  { path: 'projects/:id', component: ProjectDetailsComponent },
];
