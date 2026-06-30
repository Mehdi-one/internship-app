import { Component, Input } from '@angular/core';

import { FinancialSummary } from '../../models/business.models';

@Component({
  selector: 'app-project-summary',
  imports: [],
  templateUrl: './project-summary.html',
})
export class ProjectSummaryComponent {
  @Input() summary: FinancialSummary | null = null;

  formatMoney(value: number | null) {
    return new Intl.NumberFormat('fr-MA', { maximumFractionDigits: 2 }).format(Number(value || 0));
  }

  get budgetConsumptionProgress() {
    return Math.min(Math.max(Number(this.summary?.consommationBudget || 0), 0), 100);
  }

  get budgetProgressClass() {
    if (this.summary?.budgetCritique) {
      return 'danger';
    }
    if (this.summary?.budgetAlert) {
      return 'warning';
    }
    return 'success';
  }
}
