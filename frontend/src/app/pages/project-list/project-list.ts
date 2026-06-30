import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { Project, ProjectFilters, ProjectStatus } from '../../models/business.models';
import { LabelFrPipe } from '../../pipes/label-fr.pipe';
import { ApiService } from '../../services/api.service';
import { AuthorizationService } from '../../services/authorization.service';

@Component({
  selector: 'app-project-list',
  imports: [FormsModule, RouterLink, LabelFrPipe],
  templateUrl: './project-list.html',
})
export class ProjectListComponent implements OnInit {
  private readonly apiService = inject(ApiService);
  private readonly changeDetectorRef = inject(ChangeDetectorRef);
  readonly authorization = inject(AuthorizationService);

  projects: Project[] = [];
  message = '';
  filters: ProjectFilters = {
    search: '',
    status: '',
  };
  statuses: ProjectStatus[] = ['PROSPECT', 'IN_PROGRESS', 'SUSPENDED', 'RECEIVED', 'CLOSED'];

  ngOnInit() {
    this.loadProjects();
  }

  loadProjects() {
    this.message = 'Chargement des marches...';

    this.apiService.getProjects(this.filters).subscribe({
      next: (projects) => {
        this.projects = projects;
        this.message = projects.length === 0 ? 'Aucun marche pour le moment.' : '';
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Project loading failed', error);
        this.message = `Impossible de charger les marches: ${error?.message || error?.statusText || 'erreur inconnue'}`;
        this.changeDetectorRef.detectChanges();
      },
    });
  }
}
