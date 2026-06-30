import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { ProjectRequest } from '../../models/business.models';
import { ApiService } from '../../services/api.service';
import { calculatePlannedEndDate, CONTRACTING_AUTHORITIES } from '../project-form/project-form.config';

@Component({
  selector: 'app-project-edit',
  imports: [FormsModule, RouterLink],
  templateUrl: './project-edit.html',
})
export class ProjectEditComponent implements OnInit {
  private readonly apiService = inject(ApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly changeDetectorRef = inject(ChangeDetectorRef);

  projectId = 0;
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

  ngOnInit() {
    this.projectId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadProject();
  }

  loadProject() {
    this.message = 'Chargement du marche...';

    this.apiService.getProject(this.projectId).subscribe({
      next: (project) => {
        this.project = {
          reference: project.reference,
          title: project.title,
          contractingAuthority: project.contractingAuthority,
          projectType: project.projectType || 'WORKS',
          awardedAmountHT: project.awardedAmountHT,
          tvaRate: project.tvaRate ?? 20,
          estimatedDryCost: project.estimatedDryCost,
          notificationOrderDate: project.notificationOrderDate,
          executionDelayDays: project.executionDelayDays,
          responsibleUserReference: project.responsibleUserReference,
          status: project.status,
        };
        this.message = '';
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Project loading failed', error);
        this.message = `Impossible de charger le marche: ${error?.message || error?.statusText || 'erreur inconnue'}`;
        this.changeDetectorRef.detectChanges();
      },
    });
  }

  submit(form: NgForm) {
    if (form.invalid) {
      this.message = 'Veuillez remplir les champs obligatoires.';
      return;
    }

    this.message = 'Enregistrement du marche...';
    this.apiService.updateProject(this.projectId, this.project).subscribe({
      next: (project) => {
        this.message = 'Marche enregistre.';
        this.changeDetectorRef.detectChanges();
        this.router.navigate(['/projects', project.id]);
      },
      error: (error) => {
        console.error('Project update failed', error);
        this.message = `Impossible denregistrer le marche: ${error?.message || error?.statusText || 'erreur inconnue'}`;
        this.changeDetectorRef.detectChanges();
      },
    });
  }

  get plannedEndDate() {
    return calculatePlannedEndDate(this.project.notificationOrderDate, this.project.executionDelayDays);
  }
}
