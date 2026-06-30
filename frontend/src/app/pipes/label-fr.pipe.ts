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
      RECEIVED: 'Receptionne',
      CLOSED: 'Cloture',
      ACTIVE: 'Actif',
      INACTIVE: 'Inactif',
      ARCHIVED: 'Archive',
      WORKS: 'Travaux',
      SUPPLIES: 'Fournitures',
      SERVICES: 'Services',
      MATERIALS: 'Materiaux',
      EQUIPMENT: 'Materiel',
      EMPLOYEES: 'Salaries',
      COMPANY_STAFF: 'Staff de la societe',
      SUBCONTRACTING: 'Sous-traitance',
      EXTERNAL_RENTAL: 'Location externe',
      SITE_FEES: 'Frais de chantier',
      GENERAL_FEES: 'Frais generaux',
      OTHER: 'Autre',
      MATERIAL_PURCHASE: 'Achat de materiaux',
      MATERIAL_TRANSPORT: 'Transport de materiaux',
      EQUIPMENT_FUEL: 'Carburant',
      EQUIPMENT_MAINTENANCE: 'Maintenance / reparation',
      EQUIPMENT_TRANSPORT: 'Transport de materiel',
      EQUIPMENT_EXTERNAL_RENTAL: 'Location externe de materiel',
      EMPLOYEE_DAILY_EXPENSES: 'Charges de journee',
      EMPLOYEE_SALARY_ADVANCE: 'Avance sur salaire',
      COMPANY_STAFF_DAILY_EXPENSES: 'Charges de journee',
      SUBCONTRACTING_SERVICE: 'Prestation de sous-traitance',
      SITE_EXPENSE: 'Frais de chantier',
      GENERAL_EXPENSE: 'Frais generaux',
      OTHER_EXPENSE: 'Autre depense',
      COMMITTED: 'Engagee',
      INVOICED: 'Facturee',
      PAID: 'Payee',
      CANCELLED: 'Annulee',
      INVOICE: 'Facture',
      DELIVERY_NOTE: 'Bon de livraison',
      DRAFT: 'Saisi',
      VALIDATED: 'Valide',
      CDI: 'CDI',
      CDD: 'CDD',
      INTERIMAIRE: 'Intérimaire',
      SOUS_TRAITANT: 'Sous-traitant',
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
