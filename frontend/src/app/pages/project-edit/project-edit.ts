import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { ProjectRequest } from '../../models/business.models';
import { ApiService } from '../../services/api.service';

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
  project: ProjectRequest = {
    reference: '',
    title: '',
    clientName: '',
    projectType: 'WORKS',
    amountHT: 0,
    tvaRate: 20,
    estimatedBudget: 0,
    responsibleName: '',
    status: 'IN_PROGRESS',
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
          clientName: project.clientName || '',
          projectType: project.projectType || 'WORKS',
          amountHT: project.amountHT,
          tvaRate: project.tvaRate ?? 20,
          estimatedBudget: project.estimatedBudget,
          startDate: project.startDate,
          endDate: project.endDate,
          executionDelayDays: project.executionDelayDays,
          responsibleName: project.responsibleName || '',
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
}
