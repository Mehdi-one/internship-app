import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import {
  Expense,
  ExpenseCategory,
  ExpenseDocument,
  ExpenseFilters,
  ExpenseStatus,
} from '../../models/business.models';
import { LabelFrPipe } from '../../pipes/label-fr.pipe';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-expense-list',
  imports: [FormsModule, RouterLink, LabelFrPipe],
  templateUrl: './expense-list.html',
})
export class ExpenseListComponent implements OnInit {
  private readonly apiService = inject(ApiService);
  private readonly changeDetectorRef = inject(ChangeDetectorRef);

  expenses: Expense[] = [];
  message = '';
  filters: ExpenseFilters = { search: '', category: '', status: '' };

  readonly categories: ExpenseCategory[] = [
    'MATERIALS',
    'EQUIPMENT',
    'EMPLOYEES',
    'COMPANY_STAFF',
    'SUBCONTRACTING',
    'SITE_FEES',
    'GENERAL_FEES',
    'OTHER',
  ];
  readonly statuses: ExpenseStatus[] = ['COMMITTED', 'INVOICED', 'PAID', 'CANCELLED'];

  ngOnInit() {
    this.loadExpenses();
  }

  loadExpenses() {
    this.message = 'Chargement des depenses...';
    this.apiService.getExpenses(this.filters).subscribe({
      next: (expenses) => {
        this.expenses = expenses;
        this.message = expenses.length === 0 ? 'Aucune depense trouvee.' : '';
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Expense loading failed', error);
        this.message = `Impossible de charger les depenses: ${error?.message || error?.statusText || 'erreur inconnue'}`;
        this.changeDetectorRef.detectChanges();
      },
    });
  }

  formatMoney(value: number) {
    return new Intl.NumberFormat('fr-MA', { maximumFractionDigits: 2 }).format(Number(value || 0));
  }

  downloadExpenseDocument(document: ExpenseDocument) {
    this.apiService.downloadExpenseDocument(document.id).subscribe({
      next: (blob) => {
        const url = URL.createObjectURL(blob);
        const link = window.document.createElement('a');
        link.href = url;
        link.download = document.originalFileName;
        link.click();
        URL.revokeObjectURL(url);
      },
      error: (error) => {
        console.error('Expense document download failed', error);
        this.message = 'Impossible de telecharger le justificatif.';
        this.changeDetectorRef.detectChanges();
      },
    });
  }
}
