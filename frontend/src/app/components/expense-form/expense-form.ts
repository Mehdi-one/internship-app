import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';

import {
  ExpenseCategory,
  ExpenseDocumentType,
  ExpenseFormSubmission,
  ExpenseRequest,
  ExpenseType,
  ProjectLot,
} from '../../models/business.models';

interface ExpenseTypeOption {
  value: ExpenseType;
  label: string;
}

@Component({
  selector: 'app-expense-form',
  imports: [FormsModule],
  templateUrl: './expense-form.html',
})
export class ExpenseFormComponent implements OnChanges {
  @Input() projectLots: ProjectLot[] = [];
  @Input() initialExpense: ExpenseRequest | null = null;
  @Input() submitLabel = 'Add expense';
  @Input() projectLabel = '';
  @Input() projectStartDate?: string;
  @Input() projectEndDate?: string;

  @Output() expenseCreated = new EventEmitter<ExpenseFormSubmission>();

  documentType: ExpenseDocumentType = 'INVOICE';
  selectedFile?: File;

  expense: ExpenseRequest = {
    category: 'MATERIALS',
    expenseType: 'MATERIAL_PURCHASE',
    amountHT: 0,
    tvaRate: 20,
  };

  ngOnChanges(changes: SimpleChanges) {
    if (changes['initialExpense']) {
      this.expense = this.initialExpense
        ? { ...this.initialExpense }
        : this.emptyExpense();

      if (this.expense.category === 'EXTERNAL_RENTAL') {
        this.expense.category = 'EQUIPMENT';
        this.expense.expenseType = 'EQUIPMENT_EXTERNAL_RENTAL';
      }

      this.ensureExpenseTypeMatchesCategory();
    }
  }

  readonly typeOptionsByCategory: Record<ExpenseCategory, ExpenseTypeOption[]> = {
    MATERIALS: [
      { value: 'MATERIAL_PURCHASE', label: 'Achat de matériaux' },
      { value: 'MATERIAL_TRANSPORT', label: 'Transport de matériaux' },
    ],
    EQUIPMENT: [
      { value: 'EQUIPMENT_FUEL', label: 'Carburant' },
      { value: 'EQUIPMENT_MAINTENANCE', label: 'Maintenance / réparation' },
      { value: 'EQUIPMENT_TRANSPORT', label: 'Transport de matériel' },
      { value: 'EQUIPMENT_EXTERNAL_RENTAL', label: 'Location externe de matériel' },
    ],
    EMPLOYEES: [
      { value: 'EMPLOYEE_DAILY_EXPENSES', label: 'Charges de journée' },
      { value: 'EMPLOYEE_SALARY_ADVANCE', label: 'Avance sur salaire' },
    ],
    COMPANY_STAFF: [
      { value: 'COMPANY_STAFF_DAILY_EXPENSES', label: 'Charges de journée' },
    ],
    SUBCONTRACTING: [
      { value: 'SUBCONTRACTING_SERVICE', label: 'Prestation de sous-traitance' },
    ],
    SITE_FEES: [
      { value: 'SITE_EXPENSE', label: 'Frais de chantier' },
    ],
    GENERAL_FEES: [
      { value: 'GENERAL_EXPENSE', label: 'Frais généraux' },
    ],
    OTHER: [
      { value: 'OTHER_EXPENSE', label: 'Autre dépense' },
    ],
    EXTERNAL_RENTAL: [
      { value: 'EQUIPMENT_EXTERNAL_RENTAL', label: 'Location externe de matériel' },
    ],
  };

  submit(form: NgForm) {
    if (form.invalid) {
      return;
    }

    if (this.isExpenseDateOutsideProject) {
      return;
    }

    const request: ExpenseRequest = {
      ...this.expense,
      projectLotId: this.expense.projectLotId ? Number(this.expense.projectLotId) : undefined,
      amountHT: Number(this.expense.amountHT),
      tvaRate: this.expense.tvaRate === undefined ? undefined : Number(this.expense.tvaRate),
    };

    this.expenseCreated.emit({
      request,
      documentType: this.selectedFile ? this.documentType : undefined,
      file: this.selectedFile,
    });
    this.expense = this.emptyExpense();
    this.documentType = 'INVOICE';
    this.selectedFile = undefined;
    form.resetForm(this.expense);
  }

  get activeLots() {
    return this.projectLots.filter((lot) => !lot.archived);
  }

  get availableExpenseTypes(): ExpenseTypeOption[] {
    return this.typeOptionsByCategory[this.expense.category ?? 'MATERIALS'];
  }

  onCategoryChange() {
    this.expense.expenseType = this.availableExpenseTypes[0]?.value;
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    this.selectedFile = input.files?.[0];
  }

  get isExpenseDateOutsideProject() {
    if (!this.expense.expenseDate) {
      return false;
    }

    if (this.projectStartDate && this.expense.expenseDate < this.projectStartDate) {
      return true;
    }

    if (this.projectEndDate && this.expense.expenseDate > this.projectEndDate) {
      return true;
    }

    return false;
  }

  private emptyExpense(): ExpenseRequest {
    return {
      category: 'MATERIALS',
      expenseType: 'MATERIAL_PURCHASE',
      amountHT: 0,
      tvaRate: 20,
    };
  }

  private ensureExpenseTypeMatchesCategory() {
    const allowedTypes = this.availableExpenseTypes.map((option) => option.value);
    if (!this.expense.expenseType || !allowedTypes.includes(this.expense.expenseType)) {
      this.expense.expenseType = this.availableExpenseTypes[0]?.value;
    }
  }
}
