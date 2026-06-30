import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { of, switchMap } from 'rxjs';

import {
  EmployeeAssignment,
  EmployeeAssignmentRequest,
  Equipment,
  EquipmentAssignment,
  EquipmentAssignmentRequest,
  Expense,
  ExpenseDocument,
  ExpenseDocumentType,
  ExpenseFormSubmission,
  ExpenseRequest,
  FinancialSummary,
  Project,
  ProjectLot,
  ProjectLotRequest,
} from '../../models/business.models';
import { Employee } from '../../models/employee.model';
import { ApiService } from '../../services/api.service';
import { AuthorizationService } from '../../services/authorization.service';
import { EmployeeService } from '../../services/employee.service';
import { ExpenseFormComponent } from '../../components/expense-form/expense-form';
import { ProjectSummaryComponent } from '../../components/project-summary/project-summary';
import { LabelFrPipe } from '../../pipes/label-fr.pipe';

type ProjectWorkspaceTab = 'employees' | 'equipment' | 'lots' | 'expenses';

@Component({
  selector: 'app-project-details',
  imports: [FormsModule, RouterLink, ExpenseFormComponent, ProjectSummaryComponent, LabelFrPipe],
  templateUrl: './project-details.html',
})
export class ProjectDetailsComponent implements OnInit {
  private readonly apiService = inject(ApiService);
  private readonly employeeService = inject(EmployeeService);
  private readonly route = inject(ActivatedRoute);
  private readonly changeDetectorRef = inject(ChangeDetectorRef);
  readonly authorization = inject(AuthorizationService);

  projectId = 0;
  project: Project | null = null;
  summary: FinancialSummary | null = null;
  lots: ProjectLot[] = [];
  expenses: Expense[] = [];
  employees: Employee[] = [];
  equipment: Equipment[] = [];
  employeeAssignments: EmployeeAssignment[] = [];
  equipmentAssignments: EquipmentAssignment[] = [];
  activeProjectTab: ProjectWorkspaceTab = 'employees';
  editingEmployeeAssignmentId: number | null = null;
  editingEquipmentAssignmentId: number | null = null;
  editingExpenseId: number | null = null;
  editingExpense: ExpenseRequest | null = null;
  documentExpenseId?: number;
  documentType: ExpenseDocumentType = 'INVOICE';
  documentFile?: File;
  documentUploadAttempted = false;
  message = '';
  isLoading = false;
  lot: ProjectLotRequest = {
    designation: '',
    quantity: 0,
    unitPrice: 0,
    plannedAmount: 0,
  };
  employeeAssignment: EmployeeAssignmentRequest = {
    employeeId: undefined,
    assignmentDate: '',
    hours: 0,
  };
  equipmentAssignment: EquipmentAssignmentRequest = {
    equipmentId: undefined,
    assignmentDate: '',
    usageQuantity: 0,
    fuelCost: 0,
    maintenanceCost: 0,
    transportCost: 0,
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
    this.employeeAssignments = [];
    this.equipmentAssignments = [];

    this.apiService.getProject(this.projectId).subscribe({
      next: (project) => {
        this.project = project;
        this.isLoading = false;
        this.message = '';
        this.changeDetectorRef.detectChanges();
        this.loadSummary();
        this.loadLots();
        this.loadExpenses();
        this.loadEmployees();
        this.loadEquipment();
        this.loadEmployeeAssignments();
        this.loadEquipmentAssignments();
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
    this.apiService.getFinancialSummary(this.projectId).subscribe({
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

  loadEmployees() {
    this.employeeService.getEmployees({ status: 'ACTIVE' }).subscribe({
      next: (employees) => {
        this.employees = employees;
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Employees loading failed', error);
        this.message = 'Le marche est charge, mais les salaries ne sont pas disponibles.';
        this.changeDetectorRef.detectChanges();
      },
    });
  }

  loadEquipment() {
    this.apiService.getEquipment().subscribe({
      next: (equipment) => {
        this.equipment = equipment;
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Equipment loading failed', error);
        this.message = 'Le marche est charge, mais le materiel nest pas disponible.';
        this.changeDetectorRef.detectChanges();
      },
    });
  }

  loadEmployeeAssignments() {
    this.apiService.getEmployeeAssignments(this.projectId).subscribe({
      next: (assignments) => {
        this.employeeAssignments = assignments;
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Employee assignments loading failed', error);
        this.message = 'Le marche est charge, mais le pointage des salaries nest pas disponible.';
        this.changeDetectorRef.detectChanges();
      },
    });
  }

  loadEquipmentAssignments() {
    this.apiService.getEquipmentAssignments(this.projectId).subscribe({
      next: (assignments) => {
        this.equipmentAssignments = assignments;
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Equipment assignments loading failed', error);
        this.message = 'Le marche est charge, mais le pointage du materiel nest pas disponible.';
        this.changeDetectorRef.detectChanges();
      },
    });
  }

  addExpense(submission: ExpenseFormSubmission) {
    this.message = 'Ajout de la depense...';

    this.apiService.createExpense(this.projectId, submission.request).pipe(
      switchMap((expense) =>
        submission.file && submission.documentType
          ? this.apiService.uploadExpenseDocument(expense.id, submission.documentType, submission.file)
          : of(expense),
      ),
    ).subscribe({
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
      expenseType: expense.expenseType,
      description: expense.description,
      amountHT: expense.amountHT,
      tvaRate: expense.tvaRate ?? 20,
      supplierName: expense.supplierName,
      invoiceNumber: expense.invoiceNumber,
      expenseDate: expense.expenseDate,
    };
    this.message = '';
    this.changeDetectorRef.detectChanges();
  }

  cancelEditExpense() {
    this.editingExpenseId = null;
    this.editingExpense = null;
    this.changeDetectorRef.detectChanges();
  }

  updateExpense(submission: ExpenseFormSubmission) {
    if (!this.editingExpenseId) {
      return;
    }

    this.message = 'Enregistrement de la depense...';

    this.apiService.updateExpense(this.editingExpenseId, submission.request).pipe(
      switchMap((expense) =>
        submission.file && submission.documentType
          ? this.apiService.uploadExpenseDocument(expense.id, submission.documentType, submission.file)
          : of(expense),
      ),
    ).subscribe({
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

  invoiceExpense(id: number) {
    this.updateExpenseStatus(
      'Passage de la depense au statut facture...',
      'Depense marquee comme facturee.',
      'Impossible de marquer la depense comme facturee.',
      () => this.apiService.invoiceExpense(id),
    );
  }

  payExpense(id: number) {
    this.updateExpenseStatus(
      'Passage de la depense au statut paye...',
      'Depense marquee comme payee.',
      'Impossible de marquer la depense comme payee.',
      () => this.apiService.payExpense(id),
    );
  }

  onDocumentFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    this.documentFile = input.files?.[0];
    if (this.documentFile) {
      this.documentUploadAttempted = false;
    }
  }

  startDocumentUpload(expenseId: number) {
    this.documentExpenseId = expenseId;
    this.documentType = 'INVOICE';
    this.documentFile = undefined;
    this.documentUploadAttempted = false;
    this.message = '';
  }

  cancelDocumentUpload() {
    this.documentExpenseId = undefined;
    this.documentType = 'INVOICE';
    this.documentFile = undefined;
    this.documentUploadAttempted = false;
  }

  get selectedDocumentExpense(): Expense | undefined {
    return this.expenses.find((item) => item.id === this.documentExpenseId);
  }

  get canUploadDocumentToSelectedExpense(): boolean {
    const expense = this.selectedDocumentExpense;
    return !!expense && expense.status !== 'CANCELLED' && expense.documents.length === 0;
  }

  get selectedDocumentExpenseLabel(): string {
    const expense = this.selectedDocumentExpense;
    if (!expense) {
      return '';
    }

    const reference = expense.invoiceNumber ? `Piece ${expense.invoiceNumber}` : `Depense du ${expense.expenseDate}`;
    return expense.description ? `${reference} - ${expense.description}` : reference;
  }

  uploadExpenseDocument() {
    this.documentUploadAttempted = true;
    if (!this.documentExpenseId || !this.documentFile) {
      this.message = 'Selectionnez une piece jointe.';
      this.changeDetectorRef.detectChanges();
      return;
    }

    this.message = 'Ajout du justificatif...';
    this.apiService.uploadExpenseDocument(this.documentExpenseId, this.documentType, this.documentFile).subscribe({
      next: () => {
        this.message = 'Justificatif ajoute.';
        this.cancelDocumentUpload();
        this.loadExpenses();
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Expense document upload failed', error);
        this.message = `Impossible dajouter le justificatif: ${error?.error?.message || error?.message || 'format ou taille invalide'}`;
        this.changeDetectorRef.detectChanges();
      },
    });
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

  private updateExpenseStatus(
    pendingMessage: string,
    successMessage: string,
    errorMessage: string,
    request: () => ReturnType<ApiService['payExpense']>,
  ) {
    this.message = pendingMessage;
    this.changeDetectorRef.detectChanges();

    request().subscribe({
      next: () => {
        this.message = successMessage;
        this.loadSummary();
        this.loadExpenses();
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Expense status update failed', error);
        this.message = errorMessage;
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
    const request: ProjectLotRequest = {
      ...this.lot,
      plannedAmount: this.calculatedLotPlannedAmount,
    };

    this.apiService.createProjectLot(this.projectId, request).subscribe({
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

  get calculatedLotPlannedAmount() {
    const quantity = Number(this.lot.quantity) || 0;
    const unitPrice = Number(this.lot.unitPrice) || 0;
    return Number((quantity * unitPrice).toFixed(2));
  }

  get selectedEquipmentUsageType() {
    return this.equipment.find((item) => item.id === this.equipmentAssignment.equipmentId)?.usageCostType ?? 'HOURLY';
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

  saveEmployeeAssignment(form: NgForm) {
    if (form.invalid) {
      this.message = 'Veuillez remplir les champs obligatoires du pointage salarie.';
      return;
    }

    if (this.isPointageDateOutsideProject(this.employeeAssignment.assignmentDate)) {
      this.message = 'La date de pointage doit etre comprise entre la date debut et la date fin du marche.';
      return;
    }

    const isEditing = this.editingEmployeeAssignmentId !== null;
    this.message = isEditing ? 'Modification du pointage salarie...' : 'Ajout du pointage salarie...';

    const request$ = isEditing
      ? this.apiService.updateEmployeeAssignment(this.editingEmployeeAssignmentId!, this.employeeAssignment)
      : this.apiService.createEmployeeAssignment(this.projectId, this.employeeAssignment);

    request$.subscribe({
      next: () => {
        this.message = isEditing ? 'Pointage salarie modifie.' : 'Pointage salarie ajoute.';
        this.cancelEmployeeAssignmentEdit(form);
        this.loadEmployeeAssignments();
        this.loadSummary();
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Employee assignment save failed', error);
        this.message = `Impossible denregistrer le pointage salarie: ${error?.message || error?.statusText || 'erreur inconnue'}`;
        this.changeDetectorRef.detectChanges();
      },
    });
  }

  startEmployeeAssignmentEdit(assignment: EmployeeAssignment) {
    this.editingEmployeeAssignmentId = assignment.id;
    this.employeeAssignment = {
      employeeId: assignment.employeeId,
      assignmentDate: assignment.assignmentDate,
      hours: assignment.hours,
    };
    this.message = '';
  }

  cancelEmployeeAssignmentEdit(form: NgForm) {
    this.editingEmployeeAssignmentId = null;
    this.employeeAssignment = { employeeId: undefined, assignmentDate: '', hours: 0 };
    form.resetForm(this.employeeAssignment);
  }

  validateEmployeeAssignment(id: number) {
    this.message = 'Validation du pointage salarie...';

    this.apiService.validateEmployeeAssignment(id).subscribe({
      next: () => {
        this.message = 'Pointage salarie valide.';
        this.loadEmployeeAssignments();
        this.loadSummary();
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Employee assignment validation failed', error);
        this.message = `Impossible de valider le pointage salarie: ${error?.message || error?.statusText || 'erreur inconnue'}`;
        this.changeDetectorRef.detectChanges();
      },
    });
  }

  cancelEmployeeAssignment(id: number) {
    this.message = 'Annulation du pointage salarie...';

    this.apiService.cancelEmployeeAssignment(id).subscribe({
      next: () => {
        this.message = 'Pointage salarie annule.';
        this.loadEmployeeAssignments();
        this.loadSummary();
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Employee assignment cancellation failed', error);
        this.message = `Impossible dannuler le pointage salarie: ${error?.message || error?.statusText || 'erreur inconnue'}`;
        this.changeDetectorRef.detectChanges();
      },
    });
  }

  saveEquipmentAssignment(form: NgForm) {
    if (form.invalid) {
      this.message = 'Veuillez remplir les champs obligatoires du pointage materiel.';
      return;
    }

    if (this.isPointageDateOutsideProject(this.equipmentAssignment.assignmentDate)) {
      this.message = 'La date de pointage doit etre comprise entre la date debut et la date fin du marche.';
      return;
    }

    const isEditing = this.editingEquipmentAssignmentId !== null;
    this.message = isEditing ? 'Modification du pointage materiel...' : 'Ajout du pointage materiel...';

    const request$ = isEditing
      ? this.apiService.updateEquipmentAssignment(this.editingEquipmentAssignmentId!, this.equipmentAssignment)
      : this.apiService.createEquipmentAssignment(this.projectId, this.equipmentAssignment);

    request$.subscribe({
      next: () => {
        this.message = isEditing ? 'Pointage materiel modifie.' : 'Pointage materiel ajoute.';
        this.cancelEquipmentAssignmentEdit(form);
        this.loadEquipmentAssignments();
        this.loadSummary();
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Equipment assignment save failed', error);
        this.message = `Impossible denregistrer le pointage materiel: ${error?.message || error?.statusText || 'erreur inconnue'}`;
        this.changeDetectorRef.detectChanges();
      },
    });
  }

  startEquipmentAssignmentEdit(assignment: EquipmentAssignment) {
    this.editingEquipmentAssignmentId = assignment.id;
    this.equipmentAssignment = {
      equipmentId: assignment.equipmentId,
      assignmentDate: assignment.assignmentDate,
      usageQuantity: assignment.usageQuantity,
      fuelCost: assignment.fuelCost,
      maintenanceCost: assignment.maintenanceCost,
      transportCost: assignment.transportCost,
    };
    this.message = '';
  }

  cancelEquipmentAssignmentEdit(form: NgForm) {
    this.editingEquipmentAssignmentId = null;
    this.equipmentAssignment = {
      equipmentId: undefined,
      assignmentDate: '',
      usageQuantity: 0,
      fuelCost: 0,
      maintenanceCost: 0,
      transportCost: 0,
    };
    form.resetForm(this.equipmentAssignment);
  }

  validateEquipmentAssignment(id: number) {
    this.message = 'Validation du pointage materiel...';

    this.apiService.validateEquipmentAssignment(id).subscribe({
      next: () => {
        this.message = 'Pointage materiel valide.';
        this.loadEquipmentAssignments();
        this.loadSummary();
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Equipment assignment validation failed', error);
        this.message = `Impossible de valider le pointage materiel: ${error?.message || error?.statusText || 'erreur inconnue'}`;
        this.changeDetectorRef.detectChanges();
      },
    });
  }

  cancelEquipmentAssignment(id: number) {
    this.message = 'Annulation du pointage materiel...';

    this.apiService.cancelEquipmentAssignment(id).subscribe({
      next: () => {
        this.message = 'Pointage materiel annule.';
        this.loadEquipmentAssignments();
        this.loadSummary();
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Equipment assignment cancellation failed', error);
        this.message = `Impossible dannuler le pointage materiel: ${error?.message || error?.statusText || 'erreur inconnue'}`;
        this.changeDetectorRef.detectChanges();
      },
    });
  }

  isPointageDateOutsideProject(date?: string) {
    if (!date || !this.project) {
      return false;
    }

    if (this.project.notificationOrderDate && date < this.project.notificationOrderDate) {
      return true;
    }

    if (this.project.plannedEndDate && date > this.project.plannedEndDate) {
      return true;
    }

    return false;
  }
}
