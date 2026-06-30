import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { ProjectRequest } from '../../models/business.models';
import { ApiService } from '../../services/api.service';
import { calculatePlannedEndDate, CONTRACTING_AUTHORITIES } from '../project-form/project-form.config';

@Component({
  selector: 'app-project-create',
  imports: [FormsModule, RouterLink],
  templateUrl: './project-create.html',
})
export class ProjectCreateComponent {
  private readonly apiService = inject(ApiService);
  private readonly router = inject(Router);
  private readonly changeDetectorRef = inject(ChangeDetectorRef);

  message = '';
  readonly contractingAuthorities = CONTRACTING_AUTHORITIES;
  project: ProjectRequest = {
    reference: '',
    title: '',
    contractingAuthority: '',
    projectType: 'WORKS',
    awardedAmountHT: 0,
    tvaRate: 20,
    estimatedDryCost: 0,
    responsibleUserReference: '',
    status: 'PROSPECT',
  };

  submit(form: NgForm) {
    if (form.invalid) {
      this.message = 'Veuillez remplir les champs obligatoires.';
      return;
    }

    this.message = 'Creation du marche...';
    this.apiService.createProject(this.project).subscribe({
      next: (createdProject) => {
        this.message = 'Marche cree.';
        this.changeDetectorRef.detectChanges();
        this.router.navigate(['/projects', createdProject.id]);
      },
      error: (error) => {
        console.error('Project creation failed', error);
        this.message = `Impossible de creer le marche: ${error?.message || error?.statusText || 'verifiez les champs obligatoires et la reference unique'}`;
        this.changeDetectorRef.detectChanges();
      },
    });
  }

  get plannedEndDate() {
    return calculatePlannedEndDate(this.project.notificationOrderDate, this.project.executionDelayDays);
  }
}
