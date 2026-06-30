export interface FuelLog {
  id: number;
  equipmentId: number;
  date: string;
  liters: number;
  costPerLiter: number;
  totalCost: number;
  mileageOrHours?: number;
  notes?: string;
  createdAt: string;
}

export interface FuelLogRequest {
  date: string;
  liters: number;
  costPerLiter: number;
  mileageOrHours?: number;
  notes?: string;
}
