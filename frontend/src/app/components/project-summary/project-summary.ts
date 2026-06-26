import { Component, Input } from '@angular/core';

import { ProjectSummary } from '../../models/business.models';

@Component({
  selector: 'app-project-summary',
  imports: [],
  templateUrl: './project-summary.html',
})
export class ProjectSummaryComponent {
  @Input() summary: ProjectSummary | null = null;
}
