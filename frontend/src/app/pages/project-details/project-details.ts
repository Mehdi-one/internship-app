import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { Expense, ExpenseRequest, Project, ProjectLot, ProjectLotRequest, ProjectSummary } from '../../models/business.models';
import { ApiService } from '../../services/api.service';
import { ExpenseFormComponent } from '../../components/expense-form/expense-form';
import { ProjectSummaryComponent } from '../../components/project-summary/project-summary';
import { LabelFrPipe } from '../../pipes/label-fr.pipe';

@Component({
  selector: 'app-project-details',
  imports: [FormsModule, RouterLink, ExpenseFormComponent, ProjectSummaryComponent, LabelFrPipe],
  templateUrl: './project-details.html',
})
export class ProjectDetailsComponent implements OnInit {
  private readonly apiService = inject(ApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly changeDetectorRef = inject(ChangeDetectorRef);

  projectId = 0;
  project: Project | null = null;
  summary: ProjectSummary | null = null;
  lots: ProjectLot[] = [];
  expenses: Expense[] = [];
  editingExpenseId: number | null = null;
  editingExpense: ExpenseRequest | null = null;
  message = '';
  isLoading = false;
  lot: ProjectLotRequest = {
    designation: '',
    quantity: 0,
    unitPrice: 0,
    plannedAmount: 0,
  };

  ngOnInit() {
    this.route.paramMap.subscribe((params) => {
      this.projectId = Number(params.get('id'));

      if (!this.projectId) {
        this.message = "Identifiant du marche invalide dans l'URL.";
        this.changeDetectorRef.detectChanges();
        return;
      }

      this.loadProject();
    });
  }

  loadProject() {
    this.isLoading = true;
    this.message = 'Chargement du marche...';
    this.project = null;
    this.summary = null;
    this.lots = [];
    this.expenses = [];

    this.apiService.getProject(this.projectId).subscribe({
      next: (project) => {
        this.project = project;
        this.isLoading = false;
        this.message = '';
        this.changeDetectorRef.detectChanges();
        this.loadSummary();
        this.loadLots();
        this.loadExpenses();
      },
      error: (error) => {
        console.error('Project details loading failed', error);
        this.isLoading = false;
        this.message = `Impossible de charger le marche ${this.projectId}: ${error?.message || error?.statusText || 'erreur inconnue'}`;
        this.changeDetectorRef.detectChanges();
      },
    });
  }

  loadLots() {
    this.apiService.getProjectLots(this.projectId).subscribe({
      next: (lots) => {
        this.lots = lots;
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Project lots loading failed', error);
        this.message = 'Le marche est charge, mais les lots/postes ne sont pas disponibles.';
        this.changeDetectorRef.detectChanges();
      },
    });
  }

  loadSummary() {
    this.apiService.getProjectSummary(this.projectId).subscribe({
      next: (summary) => {
        this.summary = summary;
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Project summary loading failed', error);
        this.message = "Le marche est charge, mais la synthese financiere n'est pas disponible.";
        this.changeDetectorRef.detectChanges();
      },
    });
  }

  loadExpenses() {
    this.apiService.getProjectExpenses(this.projectId).subscribe({
      next: (expenses) => {
        this.expenses = expenses;
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Project expenses loading failed', error);
        this.message = 'Le marche est charge, mais les depenses ne sont pas disponibles.';
        this.changeDetectorRef.detectChanges();
      },
    });
  }

  addExpense(request: ExpenseRequest) {
    this.message = 'Ajout de la depense...';

    this.apiService.createExpense(this.projectId, request).subscribe({
      next: () => {
        this.message = 'Depense ajoutee.';
        this.loadSummary();
        this.loadExpenses();
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Expense creation failed', error);
        this.message = 'Impossible dajouter la depense.';
        this.changeDetectorRef.detectChanges();
      },
    });
  }

  startEditExpense(expense: Expense) {
    this.editingExpenseId = expense.id;
    this.editingExpense = {
      projectLotId: expense.projectLotId,
      category: expense.category,
      description: expense.description,
      amountHT: expense.amountHT,
      tvaRate: expense.tvaRate ?? 20,
      supplierName: expense.supplierName,
      invoiceNumber: expense.invoiceNumber,
      expenseDate: expense.expenseDate,
      status: expense.status,
    };
    this.message = '';
    this.changeDetectorRef.detectChanges();
  }

  cancelEditExpense() {
    this.editingExpenseId = null;
    this.editingExpense = null;
    this.changeDetectorRef.detectChanges();
  }

  updateExpense(request: ExpenseRequest) {
    if (!this.editingExpenseId) {
      return;
    }

    this.message = 'Enregistrement de la depense...';

    this.apiService.updateExpense(this.editingExpenseId, request).subscribe({
      next: () => {
        this.message = 'Depense enregistree.';
        this.cancelEditExpense();
        this.loadSummary();
        this.loadExpenses();
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Expense update failed', error);
        this.message = `Impossible denregistrer la depense: ${error?.message || error?.statusText || 'erreur inconnue'}`;
        this.changeDetectorRef.detectChanges();
      },
    });
  }

  cancelExpense(id: number) {
    this.message = 'Annulation de la depense...';
    this.changeDetectorRef.detectChanges();

    this.apiService.cancelExpense(id).subscribe({
      next: () => {
        this.message = 'Depense annulee.';
        this.loadSummary();
        this.loadExpenses();
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Expense cancellation failed', error);
        this.message = `Impossible dannuler la depense: ${error?.message || error?.statusText || 'erreur inconnue'}`;
        this.changeDetectorRef.detectChanges();
      },
    });
  }

  closeProject() {
    this.message = 'Cloture du marche...';
    this.changeDetectorRef.detectChanges();

    this.apiService.closeProject(this.projectId).subscribe({
      next: (project) => {
        this.project = project;
        this.message = 'Marche cloture.';
        this.loadSummary();
        this.loadExpenses();
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Project closing failed', error);
        this.message = `Impossible de cloturer le marche: ${error?.message || error?.statusText || 'erreur inconnue'}`;
        this.changeDetectorRef.detectChanges();
      },
    });
  }

  archiveProject() {
    this.message = 'Archivage du marche...';
    this.changeDetectorRef.detectChanges();

    this.apiService.archiveProject(this.projectId).subscribe({
      next: (project) => {
        this.project = project;
        this.message = 'Marche archive.';
        this.loadSummary();
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Project archive failed', error);
        this.message = `Impossible darchiver le marche: ${error?.message || error?.statusText || 'erreur inconnue'}`;
        this.changeDetectorRef.detectChanges();
      },
    });
  }

  addLot(form: NgForm) {
    if (form.invalid) {
      this.message = 'Veuillez remplir les champs obligatoires du lot/poste.';
      return;
    }

    this.message = 'Ajout du lot/poste...';

    this.apiService.createProjectLot(this.projectId, this.lot).subscribe({
      next: () => {
        this.message = 'Lot/poste ajoute.';
        this.lot = {
          designation: '',
          quantity: 0,
          unitPrice: 0,
          plannedAmount: 0,
        };
        form.resetForm(this.lot);
        this.loadLots();
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Lot creation failed', error);
        this.message = `Impossible dajouter le lot/poste: ${error?.message || error?.statusText || 'erreur inconnue'}`;
        this.changeDetectorRef.detectChanges();
      },
    });
  }

  archiveLot(id: number) {
    this.message = 'Archivage du lot/poste...';

    this.apiService.archiveProjectLot(id).subscribe({
      next: () => {
        this.message = 'Lot/poste archive.';
        this.loadLots();
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Lot archive failed', error);
        this.message = `Impossible darchiver le lot/poste: ${error?.message || error?.statusText || 'erreur inconnue'}`;
        this.changeDetectorRef.detectChanges();
      },
    });
  }
}
