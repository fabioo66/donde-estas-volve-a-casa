import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  console.log('🔐 AuthGuard verificando acceso a:', state.url);

  // Usar el método unificado de autenticación
  const isAuthenticated = authService.isAuthenticated();

  if (isAuthenticated) {
    console.log('✅ Acceso permitido a:', state.url);

    // Mostrar información del token en desarrollo
    const tokenInfo = authService.getTokenInfo();
    if (tokenInfo) {
      console.log('🎫 Info del token:', {
        usuario: tokenInfo.payload?.email,
        expiraEn: Math.round(tokenInfo.remainingTime / 1000 / 60) + ' minutos',
        expiraraPronto: tokenInfo.willExpireSoon
      });
    }

    return true;
  }

  console.log('❌ Acceso denegado a:', state.url, '- Usuario no autenticado o token expirado');

  // Redirigir a página de acceso denegado
  router.navigate(['/acceso-denegado'], {
    queryParams: { returnUrl: state.url }
  });

  return false;
};
