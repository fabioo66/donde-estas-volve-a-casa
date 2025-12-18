import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-acceso-denegado',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './acceso-denegado.html',
  styleUrl: './acceso-denegado.css'
})
export class AccesoDenegadoComponent implements OnInit {
  private authService = inject(AuthService);
  private route = inject(ActivatedRoute);

  public isAuthenticated = false;
  public returnUrl: string | null = null;

  constructor(private router: Router) {}

  ngOnInit(): void {
    // Verificar estado de autenticación
    this.isAuthenticated = this.authService.isAuthenticated();

    // Obtener URL de retorno si existe
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || null;

    // Si el usuario está autenticado, redirigir
    if (this.isAuthenticated) {
      console.log('✅ Usuario autenticado detectado en página de acceso denegado, redirigiendo...');
      this.redirectAuthenticated();
    }

    // Suscribirse a cambios en el estado de autenticación
    this.authService.currentUser$.subscribe(user => {
      this.isAuthenticated = !!user;
      if (this.isAuthenticated && user) {
        console.log('✅ Usuario autenticado detectado, redirigiendo...');
        this.redirectAuthenticated();
      }
    });
  }

  private redirectAuthenticated(): void {
    if (this.returnUrl) {
      console.log('↩️ Redirigiendo a URL de retorno:', this.returnUrl);
      this.router.navigate([this.returnUrl]);
    } else {
      console.log('🏠 Redirigiendo al home');
      this.router.navigate(['/home']);
    }
  }

  irALogin(): void {
    // Pasar la URL de retorno al login
    const navigationExtras = this.returnUrl
      ? { queryParams: { returnUrl: this.returnUrl } }
      : {};

    this.router.navigate(['/login'], navigationExtras);
  }

  irARegistro(): void {
    // Pasar la URL de retorno al registro
    const navigationExtras = this.returnUrl
      ? { queryParams: { returnUrl: this.returnUrl } }
      : {};

    this.router.navigate(['/registro'], navigationExtras);
  }

  irAHome(): void {
    this.router.navigate(['/home']);
  }

  irADashboard(): void {
    if (this.isAuthenticated) {
      this.router.navigate(['/home']);
    } else {
      this.irALogin();
    }
  }
}
