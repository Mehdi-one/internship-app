export type ProjectStatus = 'PROSPECT' | 'IN_PROGRESS' | 'SUSPENDED' | 'DELIVERED' | 'CLOSED';

export type ProjectType = 'WORKS' | 'SUPPLIES' | 'SERVICES';

export type ExpenseCategory =
  | 'MATERIALS'
  | 'SUBCONTRACTING'
  | 'EXTERNAL_RENTAL'
  | 'SITE_FEES'
  | 'GENERAL_FEES'
  | 'OTHER';

export type ExpenseStatus = 'COMMITTED' | 'INVOICED' | 'PAID' | 'CANCELLED';

export interface Project {
  id: number;
  reference: string;
  title: string;
  clientName?: string;
  projectType: ProjectType;
  amountHT: number;
  tvaRate: number;
  estimatedBudget: number;
  startDate?: string;
  endDate?: string;
  executionDelayDays?: number;
  responsibleName?: string;
  archived: boolean;
  status: ProjectStatus;
  createdAt: string;
  updatedAt: string;
}

export interface ProjectRequest {
  reference: string;
  title: string;
  clientName: string;
  projectType: ProjectType;
  amountHT: number;
  tvaRate: number;
  estimatedBudget: number;
  startDate?: string;
  endDate?: string;
  executionDelayDays?: number;
  responsibleName: string;
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
  projectLotId?: number;
  projectLotDesignation?: string;
  category: ExpenseCategory;
  description?: string;
  amountHT: number;
  tvaRate?: number;
  supplierName?: string;
  invoiceNumber?: string;
  expenseDate?: string;
  status: ExpenseStatus;
  createdAt: string;
  updatedAt: string;
}

export interface ExpenseRequest {
  projectLotId?: number;
  category?: ExpenseCategory;
  description?: string;
  amountHT: number;
  tvaRate?: number;
  supplierName?: string;
  invoiceNumber?: string;
  expenseDate?: string;
  status?: ExpenseStatus;
}

export interface ProjectSummary {
  projectId: number;
  reference: string;
  title: string;
  amountHT: number;
  estimatedBudget: number;
  totalExpenses: number;
  margin: number;
  marginRate: number;
  budgetConsumptionRate: number;
  status: ProjectStatus;
}

export type EmployeeStatus = 'ACTIVE' | 'INACTIVE';

export type ContractType = 'CDI' | 'CDD' | 'INTERIM' | 'FREELANCE' | 'OTHER';

export interface Employee {
  id: number;
  registrationNumber: string;
  fullName: string;
  qualification: string;
  contractType: ContractType;
  hourlyCost: number;
  status: EmployeeStatus;
  createdAt: string;
  updatedAt: string;
}

export interface EmployeeRequest {
  registrationNumber: string;
  fullName: string;
  qualification: string;
  contractType: ContractType;
  hourlyCost: number;
  status?: EmployeeStatus;
}

export interface EmployeeFilters {
  search?: string;
  status?: EmployeeStatus | '';
}

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
  status: EquipmentStatus;
  createdAt: string;
  updatedAt: string;
}

export interface EquipmentRequest {
  reference: string;
  type: EquipmentType;
  brandModel: string;
  acquisitionCost?: number;
  usageCostType: UsageCostType;
  usageCost: number;
  fuelConsumption?: number;
  maintenanceCost?: number;
  insuranceCost?: number;
  status?: EquipmentStatus;
}

export interface EquipmentFilters {
  search?: string;
  status?: EquipmentStatus | '';
}
