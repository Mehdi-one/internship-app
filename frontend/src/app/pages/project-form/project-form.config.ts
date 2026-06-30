export const CONTRACTING_AUTHORITIES = [
  'Commune',
  'Ministere',
  'ONEE',
  'OCP',
  'Agence',
] as const;

export function calculatePlannedEndDate(notificationOrderDate?: string, executionDelayDays?: number) {
  if (!notificationOrderDate || !executionDelayDays || executionDelayDays < 1) {
    return '';
  }

  const date = new Date(`${notificationOrderDate}T00:00:00`);
  date.setDate(date.getDate() + executionDelayDays - 1);
  return [
    date.getFullYear(),
    String(date.getMonth() + 1).padStart(2, '0'),
    String(date.getDate()).padStart(2, '0'),
  ].join('-');
}
