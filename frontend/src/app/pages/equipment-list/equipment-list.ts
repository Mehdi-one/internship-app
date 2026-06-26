import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { Equipment, EquipmentFilters, EquipmentStatus } from '../../models/business.models';
import { LabelFrPipe } from '../../pipes/label-fr.pipe';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-equipment-list',
  imports: [FormsModule, RouterLink, LabelFrPipe],
  templateUrl: './equipment-list.html',
})
export class EquipmentListComponent implements OnInit {
  private readonly apiService = inject(ApiService);
  private readonly changeDetectorRef = inject(ChangeDetectorRef);

  equipment: Equipment[] = [];
  message = '';
  filters: EquipmentFilters = {
    search: '',
    status: '',
  };
  statuses: EquipmentStatus[] = ['AVAILABLE', 'ASSIGNED', 'MAINTENANCE', 'REFORMED'];

  ngOnInit() {
    this.loadEquipment();
  }

  loadEquipment() {
    this.message = 'Chargement du materiel...';

    this.apiService.getEquipment(this.filters).subscribe({
      next: (equipment) => {
        this.equipment = equipment;
        this.message = equipment.length === 0 ? 'Aucun materiel pour le moment.' : '';
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Equipment loading failed', error);
        this.message = `Impossible de charger le materiel: ${error?.message || error?.statusText || 'erreur inconnue'}`;
        this.changeDetectorRef.detectChanges();
      },
    });
  }

  reformEquipment(id: number) {
    this.message = 'Reforme du materiel...';

    this.apiService.reformEquipment(id).subscribe({
      next: () => {
        this.message = 'Materiel reforme.';
        this.loadEquipment();
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Equipment reform failed', error);
        this.message = `Impossible de reformer le materiel: ${error?.message || error?.statusText || 'erreur inconnue'}`;
        this.changeDetectorRef.detectChanges();
      },
    });
  }
}
