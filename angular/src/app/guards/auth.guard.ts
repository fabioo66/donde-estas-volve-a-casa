import { inject, PLATFORM_ID } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { isPlatformBrowser } from '@angular/common';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const platformId = inject(PLATFORM_ID);
  const isBrowser = isPlatformBrowser(platformId);

  console.log('AuthGuard ejecutándose para:', state.url);

  // Verificación directa tanto del servicio como del localStorage
  let isAuthenticated = false;
  let userInfo = null;

  // Primero verificar el servicio
  const currentUser = authService.getCurrentUser();
  if (currentUser && currentUser.token) {
    isAuthenticated = true;
    userInfo = currentUser;
    console.log('Usuario encontrado en servicio:', userInfo);
  }

  // Si no está en el servicio, verificar localStorage directamente
  if (!isAuthenticated && isBrowser) {
    const storedUser = localStorage.getItem('currentUser');
    if (storedUser) {
      try {
        const parsedUser = JSON.parse(storedUser);
        if (parsedUser && parsedUser.token) {
          isAuthenticated = true;
          userInfo = parsedUser;
          console.log('Usuario encontrado en localStorage:', userInfo);
        }
      } catch (error) {
        console.error('Error parsing localStorage user:', error);
      }
    }
  }

  if (isAuthenticated) {
    console.log('Acceso permitido a:', state.url);
    return true;
  }

  // Redirigir a página de acceso denegado
  router.navigate(['/acceso-denegado']);
  return false;
};
