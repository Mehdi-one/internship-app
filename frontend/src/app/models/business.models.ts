export type ProjectStatus = 'PROSPECT' | 'IN_PROGRESS' | 'SUSPENDED' | 'RECEIVED' | 'CLOSED';

export type ProjectType = 'WORKS' | 'SUPPLIES' | 'SERVICES';

export type ExpenseCategory =
  | 'MATERIALS'
  | 'EQUIPMENT'
  | 'EMPLOYEES'
  | 'COMPANY_STAFF'
  | 'SUBCONTRACTING'
  | 'EXTERNAL_RENTAL'
  | 'SITE_FEES'
  | 'GENERAL_FEES'
  | 'OTHER';

export type ExpenseType =
  | 'MATERIAL_PURCHASE'
  | 'MATERIAL_TRANSPORT'
  | 'EQUIPMENT_FUEL'
  | 'EQUIPMENT_MAINTENANCE'
  | 'EQUIPMENT_TRANSPORT'
  | 'EQUIPMENT_EXTERNAL_RENTAL'
  | 'EMPLOYEE_DAILY_EXPENSES'
  | 'EMPLOYEE_SALARY_ADVANCE'
  | 'COMPANY_STAFF_DAILY_EXPENSES'
  | 'SUBCONTRACTING_SERVICE'
  | 'SITE_EXPENSE'
  | 'GENERAL_EXPENSE'
  | 'OTHER_EXPENSE';

export type ExpenseStatus = 'COMMITTED' | 'INVOICED' | 'PAID' | 'CANCELLED';

export type ExpenseDocumentType = 'INVOICE' | 'DELIVERY_NOTE' | 'OTHER';

export interface ExpenseDocument {
  id: number;
  expenseId: number;
  documentType: ExpenseDocumentType;
  originalFileName: string;
  contentType: string;
  fileSize: number;
  createdAt: string;
}

export interface Project {
  id: number;
  reference: string;
  title: string;
  contractingAuthority: string;
  projectType: ProjectType;
  awardedAmountHT: number;
  tvaRate: number;
  estimatedDryCost: number;
  notificationOrderDate?: string;
  executionDelayDays?: number;
  plannedEndDate?: string;
  responsibleUserReference: string;
  archived: boolean;
  status: ProjectStatus;
  createdAt: string;
  updatedAt: string;
}

export interface ProjectRequest {
  reference: string;
  title: string;
  contractingAuthority: string;
  projectType: ProjectType;
  awardedAmountHT: number;
  tvaRate: number;
  estimatedDryCost: number;
  notificationOrderDate?: string;
  executionDelayDays?: number;
  responsibleUserReference: string;
  status?: ProjectStatus;
}

export interface ProjectFilters {
  search?: string;
  status?: ProjectStatus | '';
}

export interface ProjectLot {
  id: number;
  projectId: number;
  designation: string;
  quantity?: number;
  unitPrice?: number;
  plannedAmount: number;
  archived: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface ProjectLotRequest {
  designation: string;
  quantity?: number;
  unitPrice?: number;
  plannedAmount: number;
}

export interface Expense {
  id: number;
  projectId: number;
  projectReference: string;
  projectTitle: string;
  projectLotId?: number;
  projectLotDesignation?: string;
  category: ExpenseCategory;
  expenseType?: ExpenseType;
  description?: string;
  amountHT: number;
  tvaRate?: number;
  supplierName?: string;
  invoiceNumber?: string;
  expenseDate?: string;
  status: ExpenseStatus;
  documents: ExpenseDocument[];
  createdAt: string;
  updatedAt: string;
}

export interface ExpenseFilters {
  search?: string;
  category?: ExpenseCategory | '';
  status?: ExpenseStatus | '';
}

export interface ExpenseRequest {
  projectLotId?: number;
  category?: ExpenseCategory;
  expenseType?: ExpenseType;
  description?: string;
  amountHT: number;
  tvaRate?: number;
  supplierName?: string;
  invoiceNumber?: string;
  expenseDate?: string;
}

export interface ExpenseFormSubmission {
  request: ExpenseRequest;
  documentType?: ExpenseDocumentType;
  file?: File;
}

export interface ProjectSummary {
  projectId: number;
  reference: string;
  title: string;
  awardedAmountHT: number;
  estimatedDryCost: number;
  directExpenses: number;
  laborCost: number;
  equipmentCost: number;
  totalExpenses: number;
  forecastMargin: number;
  forecastMarginRate: number;
  provisionalGrossMargin: number;
  provisionalGrossMarginRate: number;
  remainingBudget: number;
  budgetConsumptionRate: number;
  status: ProjectStatus;
}

export interface FinancialSummary {
  projectId: number;
  budgetPrevisionnel: number;
  debourseTotal: number;
  debourseMainOeuvre: number;
  debourseParc: number;
  debourseDepenses: number;
  consommationBudget: number;
  alerteSeuil: number;
  budgetAlert: boolean;
  budgetCritique: boolean;
  montantHT: number | null;
  margeBrute: number | null;
  tauxMarge: number | null;
}

export type {
  ContractType,
  Employee,
  EmployeeCostHistory,
  EmployeeDetail,
  EmployeeFilters,
  EmployeeRequest,
  EmployeeStatus,
} from './employee.model';

export type EquipmentStatus = 'AVAILABLE' | 'ASSIGNED' | 'MAINTENANCE' | 'REFORMED';

export type EquipmentType = 'TRUCK' | 'EARTHMOVING_MACHINE' | 'LIGHT_VEHICLE' | 'TOOL' | 'OTHER';

export type UsageCostType = 'HOURLY' | 'DAILY';

export interface Equipment {
  id: number;
  reference: string;
  type: EquipmentType;
  brandModel: string;
  acquisitionCost?: number;
  usageCostType: UsageCostType;
  usageCost: number;
  fuelConsumption?: number;
  maintenanceCost?: number;
  insuranceCost?: number;
  nextMaintenanceDate?: string;
  insuranceExpiryDate?: string;
  maintenanceDueSoon: boolean;
  insuranceExpiringSoon: boolean;
  status: EquipmentStatus;
  createdAt: string;
  updatedAt: string;
}

export interface EquipmentRequest {
  reference: string;
  type: EquipmentType;
  brandModel: string;
  acquisitionCost?: number;
  usageCostType?: UsageCostType;
  usageCost?: number;
  fuelConsumption?: number;
  maintenanceCost?: number;
  insuranceCost?: number;
  nextMaintenanceDate?: string;
  insuranceExpiryDate?: string;
  status?: EquipmentStatus;
}

export interface EquipmentFilters {
  search?: string;
  status?: EquipmentStatus | '';
}

export type AssignmentStatus = 'DRAFT' | 'VALIDATED' | 'CANCELLED';

export interface EmployeeAssignment {
  id: number;
  projectId: number;
  employeeId: number;
  employeeName: string;
  assignmentDate?: string;
  hours: number;
  hourlyCostSnapshot: number;
  totalCost: number;
  status: AssignmentStatus;
  createdAt: string;
  updatedAt: string;
}

export interface EmployeeAssignmentRequest {
  employeeId?: number;
  assignmentDate?: string;
  hours: number;
}

export interface EquipmentAssignment {
  id: number;
  projectId: number;
  equipmentId: number;
  equipmentReference: string;
  equipmentName: string;
  assignmentDate?: string;
  usageQuantity: number;
  usageCostType: UsageCostType;
  usageCostSnapshot: number;
  fuelCost: number;
  maintenanceCost: number;
  transportCost: number;
  totalCost: number;
  status: AssignmentStatus;
  createdAt: string;
  updatedAt: string;
}

export interface EquipmentAssignmentRequest {
  equipmentId?: number;
  assignmentDate?: string;
  usageQuantity: number;
  fuelCost?: number;
  maintenanceCost?: number;
  transportCost?: number;
}
