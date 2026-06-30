import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';

import { AuthorizationService, UserRole } from '../services/authorization.service';

export const roleGuard: CanActivateFn = (route) => {
  const requiredRoles = (route.data['roles'] ?? []) as UserRole[];
  return inject(AuthorizationService).authorize(requiredRoles);
};
