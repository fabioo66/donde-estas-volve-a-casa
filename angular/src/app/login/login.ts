import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { LoginRequest } from '../models/usuario.model';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent {
  credentials: LoginRequest = {
    email: '',
    password: ''
  };
  errorMessage: string = '';
  loading: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  onSubmit(): void {
    // Limpiar mensajes previos
    this.errorMessage = '';
    this.cdr.detectChanges();

    // Validación de campos vacíos
    if (!this.credentials.email || !this.credentials.password) {
      this.errorMessage = 'Por favor complete todos los campos';
      this.cdr.detectChanges();
      return;
    }

    // Validación de formato de email
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(this.credentials.email)) {
      this.errorMessage = 'Por favor ingrese un correo electrónico válido';
      this.cdr.detectChanges();
      return;
    }

    // Validación de longitud de contraseña
    if (this.credentials.password.length < 6) {
      this.errorMessage = 'La contraseña debe tener al menos 6 caracteres';
      this.cdr.detectChanges();
      return;
    }

    this.loading = true;
    this.cdr.detectChanges();

    console.log('Intentando login con:', { email: this.credentials.email });

    this.authService.login(this.credentials).subscribe({
      next: (response) => {
        console.log('Login exitoso', response);
        this.loading = false;
        this.cdr.detectChanges();
        this.router.navigate(['/home']);
      },
      error: (error) => {
        console.error('Error completo en login:', error);
        console.error('Error status:', error.status);
        console.error('Error.error:', error.error);

        // PRIMERO: SIEMPRE desbloquear el formulario INMEDIATAMENTE
        this.loading = false;

        // SEGUNDO: Determinar el mensaje de error apropiado
        if (error.status === 401) {
          // Este es el caso de credenciales incorrectas
          this.errorMessage = '❌ Email o contraseña incorrectos. Por favor verifica tus credenciales e intenta nuevamente.';
        } else if (error.status === 0) {
          this.errorMessage = '⚠️ No se puede conectar con el servidor. Verifica que el backend esté ejecutándose.';
        } else if (error.status === 500) {
          this.errorMessage = '⚠️ Error en el servidor. Por favor intenta nuevamente más tarde.';
        } else if (typeof error.error === 'string' && error.error.length > 0) {
          this.errorMessage = '❌ ' + error.error;
        } else if (error.error?.message) {
          this.errorMessage = '❌ ' + error.error.message;
        } else {
          this.errorMessage = '⚠️ Error inesperado. Por favor intenta nuevamente.';
        }

        console.log('Mensaje de error establecido:', this.errorMessage);

        // FORZAR detección de cambios
        this.cdr.detectChanges();
      },
      complete: () => {
        // Fallback de seguridad adicional
        if (this.loading) {
          console.warn('Formulario todavía en loading en complete(), desbloqueando...');
          this.loading = false;
          this.cdr.detectChanges();
        }
      }
    });
  }
}
