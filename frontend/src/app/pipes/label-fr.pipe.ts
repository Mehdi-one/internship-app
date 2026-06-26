import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'labelFr',
  standalone: true,
})
export class LabelFrPipe implements PipeTransform {
  transform(value: string | number | null | undefined): string {
    if (value === null || value === undefined || value === '') {
      return '-';
    }

    const labels: Record<string, string> = {
      PROSPECT: 'Prospect',
      IN_PROGRESS: 'En cours',
      SUSPENDED: 'Suspendu',
      DELIVERED: 'Livre',
      CLOSED: 'Cloture',
      ACTIVE: 'Actif',
      INACTIVE: 'Inactif',
      ARCHIVED: 'Archive',
      WORKS: 'Travaux',
      SUPPLIES: 'Fournitures',
      SERVICES: 'Services',
      MATERIALS: 'Materiaux',
      SUBCONTRACTING: 'Sous-traitance',
      EXTERNAL_RENTAL: 'Location externe',
      SITE_FEES: 'Frais de chantier',
      GENERAL_FEES: 'Frais generaux',
      OTHER: 'Autre',
      COMMITTED: 'Engagee',
      INVOICED: 'Facturee',
      PAID: 'Payee',
      CANCELLED: 'Annulee',
      CDI: 'CDI',
      CDD: 'CDD',
      INTERIM: 'Interim',
      FREELANCE: 'Freelance',
      TRUCK: 'Camion',
      EARTHMOVING_MACHINE: 'Engin de chantier',
      LIGHT_VEHICLE: 'Vehicule leger',
      TOOL: 'Outil',
      AVAILABLE: 'Disponible',
      ASSIGNED: 'Affecte',
      MAINTENANCE: 'Maintenance',
      REFORMED: 'Reforme',
      HOURLY: 'Horaire',
      DAILY: 'Journalier',
    };

    const key = String(value);
    return labels[key] ?? key;
  }
}
