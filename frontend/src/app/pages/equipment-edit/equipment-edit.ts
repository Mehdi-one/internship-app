import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { EquipmentRequest } from '../../models/business.models';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-equipment-edit',
  imports: [FormsModule, RouterLink],
  templateUrl: './equipment-edit.html',
})
export class EquipmentEditComponent implements OnInit {
  private readonly apiService = inject(ApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly changeDetectorRef = inject(ChangeDetectorRef);

  equipmentId = 0;
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

  ngOnInit() {
    this.equipmentId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadEquipment();
  }

  loadEquipment() {
    this.message = 'Chargement du materiel...';

    this.apiService.getEquipmentById(this.equipmentId).subscribe({
      next: (equipment) => {
        this.equipment = {
          reference: equipment.reference,
          type: equipment.type,
          brandModel: equipment.brandModel,
          acquisitionCost: equipment.acquisitionCost || 0,
          usageCostType: equipment.usageCostType,
          usageCost: equipment.usageCost,
          fuelConsumption: equipment.fuelConsumption || 0,
          maintenanceCost: equipment.maintenanceCost || 0,
          insuranceCost: equipment.insuranceCost || 0,
          status: equipment.status,
        };
        this.message = '';
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Equipment loading failed', error);
        this.message = `Impossible de charger le materiel: ${error?.message || error?.statusText || 'erreur inconnue'}`;
        this.changeDetectorRef.detectChanges();
      },
    });
  }

  submit(form: NgForm) {
    if (form.invalid) {
      this.message = 'Veuillez remplir les champs obligatoires.';
      return;
    }

    this.message = 'Enregistrement du materiel...';

    this.apiService.updateEquipment(this.equipmentId, this.normalizeEquipment()).subscribe({
      next: () => {
        this.message = 'Materiel enregistre.';
        this.changeDetectorRef.detectChanges();
        this.router.navigate(['/equipment']);
      },
      error: (error) => {
        console.error('Equipment update failed', error);
        this.message = `Impossible denregistrer le materiel: ${error?.message || error?.statusText || 'erreur inconnue'}`;
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
