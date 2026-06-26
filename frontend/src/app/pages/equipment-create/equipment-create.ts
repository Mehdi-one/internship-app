import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { EquipmentRequest } from '../../models/business.models';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-equipment-create',
  imports: [FormsModule, RouterLink],
  templateUrl: './equipment-create.html',
})
export class EquipmentCreateComponent {
  private readonly apiService = inject(ApiService);
  private readonly router = inject(Router);
  private readonly changeDetectorRef = inject(ChangeDetectorRef);

  message = '';
  equipment: EquipmentRequest = {
    reference: '',
    type: 'TRUCK',
    brandModel: '',
    acquisitionCost: 0,
    usageCostType: 'HOURLY',
    usageCost: 0,
    fuelConsumption: 0,
    maintenanceCost: 0,
    insuranceCost: 0,
    status: 'AVAILABLE',
  };

  submit(form: NgForm) {
    if (form.invalid) {
      this.message = 'Veuillez remplir les champs obligatoires.';
      return;
    }

    this.message = 'Creation du materiel...';

    this.apiService.createEquipment(this.normalizeEquipment()).subscribe({
      next: () => {
        this.message = 'Materiel cree.';
        this.changeDetectorRef.detectChanges();
        this.router.navigate(['/equipment']);
      },
      error: (error) => {
        console.error('Equipment creation failed', error);
        this.message = `Impossible de creer le materiel: ${error?.message || error?.statusText || 'verifiez les champs obligatoires et la reference unique'}`;
        this.changeDetectorRef.detectChanges();
      },
    });
  }

  private normalizeEquipment(): EquipmentRequest {
    return {
      ...this.equipment,
      acquisitionCost: Number(this.equipment.acquisitionCost || 0),
      usageCost: Number(this.equipment.usageCost),
      fuelConsumption: Number(this.equipment.fuelConsumption || 0),
      maintenanceCost: Number(this.equipment.maintenanceCost || 0),
      insuranceCost: Number(this.equipment.insuranceCost || 0),
    };
  }
}
