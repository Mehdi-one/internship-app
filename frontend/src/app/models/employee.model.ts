export type EmployeeStatus = 'ACTIVE' | 'INACTIVE';

export type ContractType = 'CDI' | 'CDD' | 'INTERIMAIRE' | 'SOUS_TRAITANT';

export interface Employee {
  id: number;
  matricule: string;
  fullName: string;
  qualification: string;
  contractType: ContractType;
  hourlyCost: number;
  status: EmployeeStatus;
  createdAt: string;
  updatedAt: string;
}

export interface EmployeeCostHistory {
  id: number;
  employeeId: number;
  hourlyCost: number;
  effectiveDate: string;
  createdAt: string;
}

export interface EmployeeDetail extends Employee {
  costHistory: EmployeeCostHistory[];
}

export interface EmployeeRequest {
  matricule: string;
  fullName: string;
  qualification: string;
  contractType: ContractType;
  hourlyCost: number;
  status: EmployeeStatus;
}

export interface EmployeeFilters {
  search?: string;
  status?: EmployeeStatus | '';
}
