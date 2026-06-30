import { Component, EventEmitter, Output } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';

import { FuelLogRequest } from '../../models/fuel-log.model';

@Component({
  selector: 'app-fuel-log-form',
  imports: [FormsModule],
  templateUrl: './fuel-log-form.html',
})
export class FuelLogFormComponent {
  @Output() fuelLogSubmitted = new EventEmitter<FuelLogRequest>();

  fuelLog = this.emptyFuelLog();

  submit(form: NgForm) {
    if (form.invalid) {
      return;
    }

    this.fuelLogSubmitted.emit({
      ...this.fuelLog,
      liters: Number(this.fuelLog.liters),
      costPerLiter: Number(this.fuelLog.costPerLiter),
      mileageOrHours: this.fuelLog.mileageOrHours
        ? Number(this.fuelLog.mileageOrHours)
        : undefined,
      notes: this.fuelLog.notes?.trim() || undefined,
    });
    this.fuelLog = this.emptyFuelLog();
    form.resetForm(this.fuelLog);
  }

  get calculatedTotal() {
    return Number((Number(this.fuelLog.liters || 0) * Number(this.fuelLog.costPerLiter || 0)).toFixed(2));
  }

  private emptyFuelLog(): FuelLogRequest {
    return {
      date: new Date().toISOString().slice(0, 10),
      liters: 0,
      costPerLiter: 0,
      mileageOrHours: undefined,
      notes: '',
    };
  }
}
