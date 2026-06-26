import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';

import { ExpenseRequest, ProjectLot } from '../../models/business.models';

@Component({
  selector: 'app-expense-form',
  imports: [FormsModule],
  templateUrl: './expense-form.html',
})
export class ExpenseFormComponent implements OnChanges {
  @Input() projectLots: ProjectLot[] = [];
  @Input() initialExpense: ExpenseRequest | null = null;
  @Input() submitLabel = 'Add expense';

  @Output() expenseCreated = new EventEmitter<ExpenseRequest>();

  expense: ExpenseRequest = {
    category: 'MATERIALS',
    amountHT: 0,
    tvaRate: 20,
    status: 'COMMITTED',
  };

  ngOnChanges(changes: SimpleChanges) {
    if (changes['initialExpense']) {
      this.expense = this.initialExpense
        ? { ...this.initialExpense }
        : this.emptyExpense();
    }
  }

  submit(form: NgForm) {
    if (form.invalid) {
      return;
    }

    const request: ExpenseRequest = {
      ...this.expense,
      projectLotId: this.expense.projectLotId ? Number(this.expense.projectLotId) : undefined,
      amountHT: Number(this.expense.amountHT),
      tvaRate: this.expense.tvaRate === undefined ? undefined : Number(this.expense.tvaRate),
    };

    this.expenseCreated.emit(request);
    this.expense = this.emptyExpense();
    form.resetForm(this.expense);
  }

  get activeLots() {
    return this.projectLots.filter((lot) => !lot.archived);
  }

  private emptyExpense(): ExpenseRequest {
    return {
      category: 'MATERIALS',
      amountHT: 0,
      tvaRate: 20,
      status: 'COMMITTED',
    };
  }
}
