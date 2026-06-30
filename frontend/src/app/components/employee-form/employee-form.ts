import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';

import { EmployeeRequest } from '../../models/employee.model';

@Component({
  selector: 'app-employee-form',
  imports: [FormsModule],
  templateUrl: './employee-form.html',
})
export class EmployeeFormComponent implements OnChanges {
  @Input() initialEmployee: EmployeeRequest | null = null;
  @Input() submitLabel = 'Enregistrer';
  @Input() editMode = false;
  @Output() employeeSubmitted = new EventEmitter<EmployeeRequest>();

  employee: EmployeeRequest = this.emptyEmployee();
  private initialHourlyCost = 0;

  ngOnChanges(changes: SimpleChanges) {
    if (changes['initialEmployee']) {
      this.employee = this.initialEmployee ? { ...this.initialEmployee } : this.emptyEmployee();
      this.initialHourlyCost = Number(this.employee.hourlyCost);
    }
  }

  submit(form: NgForm) {
    if (form.invalid) {
      return;
    }

    this.employeeSubmitted.emit({
      ...this.employee,
      matricule: this.employee.matricule.trim(),
      fullName: this.employee.fullName.trim(),
      qualification: this.employee.qualification.trim(),
      hourlyCost: Number(this.employee.hourlyCost),
    });
  }

  get hourlyCostChanged() {
    return this.editMode && Number(this.employee.hourlyCost) !== this.initialHourlyCost;
  }

  private emptyEmployee(): EmployeeRequest {
    return {
      matricule: '',
      fullName: '',
      qualification: '',
      contractType: 'CDI',
      hourlyCost: 0,
      status: 'ACTIVE',
    };
  }
}
