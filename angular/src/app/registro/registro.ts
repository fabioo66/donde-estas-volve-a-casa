import { Component, ChangeDetectorRef, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { GeolocalizacionService } from '../services/geolocalizacion.service';
import { Usuario } from '../models/usuario.model';

@Component({
  selector: 'app-registro',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './registro.html',
  styleUrls: ['./registro.css']
})
export class RegistroComponent implements OnInit {
  usuario: Usuario = {
    nombreUsuario: '',
    nombre: '',
    apellido: '',
    email: '',
    password: '',
    telefono: '',
    genero: '',
    fechaNacimiento: undefined,
    provincia: '',
    municipio: '',
    departamento: ''
  };

  errorMessage: string = '';
  successMessage: string = '';
  loading: boolean = false;
  loadingUbicacion: boolean = false;

  private route = inject(ActivatedRoute);
  private returnUrl: string | null = null;
  public maxBirthDateFor18: string = '';

  constructor(
    private authService: AuthService,
    private geoService: GeolocalizacionService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || null;
    console.log('🔗 URL de retorno configurada:', this.returnUrl);
    this.maxBirthDateFor18 = this.computeMaxBirthDateFor18();
  }

  obtenerUbicacionActual(): void {
    this.loadingUbicacion = true;
    this.errorMessage = '';
    this.cdr.detectChanges();

    this.geoService.obtenerPosicionActual()
      .then(position => {
        const lat = position.coords.latitude;
        const lon = position.coords.longitude;

        return this.geoService.obtenerUbicacion(lat, lon).toPromise();
      })
      .then(response => {
        if (response && response.ubicacion) {
          this.usuario.provincia = response.ubicacion.provincia.nombre;
          this.usuario.municipio = response.ubicacion.municipio.nombre;
          this.usuario.departamento = response.ubicacion.departamento.nombre;
          this.successMessage = 'Ubicación obtenida exitosamente';
          this.cdr.detectChanges();
          setTimeout(() => {
            this.successMessage = '';
            this.cdr.detectChanges();
          }, 3000);
        }
      })
      .catch(error => {
        console.error('Error al obtener ubicación', error);
        this.errorMessage = 'No se pudo obtener la ubicación. Por favor ingresa los datos manualmente.';
        this.cdr.detectChanges();
      })
      .finally(() => {
        this.loadingUbicacion = false;
        this.cdr.detectChanges();
      });
  }

  private parseToDate(value: Date | string | undefined | null): Date | null {
    if (!value) return null;
    const d = value instanceof Date ? value : new Date(value);
    return isNaN(d.getTime()) ? null : d;
  }

  private calculateAge(birth: Date | string | undefined | null): number {
    const b = this.parseToDate(birth);
    if (!b) return -1;

    const today = new Date();
    let age = today.getFullYear() - b.getFullYear();
    const monthDiff = today.getMonth() - b.getMonth();

    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < b.getDate())) {
      age--;
    }

    return age;
  }

  private computeMaxBirthDateFor18(): string {
    const d = new Date();
    d.setFullYear(d.getFullYear() - 18);
    return d.toISOString().split('T')[0];
  }

  validarFormulario(): boolean {
    this.errorMessage = '';

    if (!this.usuario.nombreUsuario || this.usuario.nombreUsuario.trim().length === 0) {
      this.errorMessage = '❌ El nombre de usuario es obligatorio';
      this.cdr.detectChanges();
      return false;
    }

    if (this.usuario.nombreUsuario.length < 3) {
      this.errorMessage = '❌ El nombre de usuario debe tener al menos 3 caracteres';
      this.cdr.detectChanges();
      return false;
    }

    if (!this.usuario.nombre || this.usuario.nombre.trim().length === 0) {
      this.errorMessage = '❌ El nombre es obligatorio';
      this.cdr.detectChanges();
      return false;
    }

    if (!this.usuario.apellido || this.usuario.apellido.trim().length === 0) {
      this.errorMessage = '❌ El apellido es obligatorio';
      this.cdr.detectChanges();
      return false;
    }

    if (!this.usuario.email || this.usuario.email.trim().length === 0) {
      this.errorMessage = '❌ El correo electrónico es obligatorio';
      this.cdr.detectChanges();
      return false;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(this.usuario.email)) {
      this.errorMessage = '❌ Por favor ingrese un correo electrónico válido';
      this.cdr.detectChanges();
      return false;
    }

    if (!this.usuario.fechaNacimiento) {
      this.errorMessage = '❌ La fecha de nacimiento es obligatoria';
      this.cdr.detectChanges();
      return false;
    }

    const edad = this.calculateAge(this.usuario.fechaNacimiento);
    if (edad < 0) {
      this.errorMessage = '❌ Fecha de nacimiento inválida';
      this.cdr.detectChanges();
      return false;
    }

    if (edad < 18) {
      this.errorMessage = '❌ Debe ser mayor de 18 años para registrarse';
      this.cdr.detectChanges();
      return false;
    }

    if (!this.usuario.password || this.usuario.password.length === 0) {
      this.errorMessage = '❌ La contraseña es obligatoria';
      this.cdr.detectChanges();
      return false;
    }

    if (this.usuario.password.length < 6) {
      this.errorMessage = '❌ La contraseña debe tener al menos 6 caracteres';
      this.cdr.detectChanges();
      return false;
    }

    if (!this.usuario.telefono || this.usuario.telefono.trim().length === 0) {
      this.errorMessage = '❌ El número de teléfono es obligatorio';
      this.cdr.detectChanges();
      return false;
    }

    if (!this.usuario.genero || this.usuario.genero === '') {
      this.errorMessage = '❌ Por favor seleccione un género';
      this.cdr.detectChanges();
      return false;
    }

    if (!this.usuario.provincia || this.usuario.provincia.trim().length === 0) {
      this.errorMessage = '❌ La provincia es obligatoria. Use el botón "Ubicación Actual" o ingrese manualmente';
      this.cdr.detectChanges();
      return false;
    }

    if (!this.usuario.municipio || this.usuario.municipio.trim().length === 0) {
      this.errorMessage = '❌ El municipio es obligatorio. Use el botón "Ubicación Actual" o ingrese manualmente';
      this.cdr.detectChanges();
      return false;
    }

    if (!this.usuario.departamento || this.usuario.departamento.trim().length === 0) {
      this.errorMessage = '❌ El departamento es obligatorio. Use el botón "Ubicación Actual" o ingrese manualmente';
      this.cdr.detectChanges();
      return false;
    }

    return true;
  }

  onSubmit(): void {
    this.errorMessage = '';
    this.successMessage = '';
    this.cdr.detectChanges();

    if (!this.validarFormulario()) {
      return;
    }

    this.loading = true;
    this.cdr.detectChanges();

    console.log('Enviando registro:', this.usuario);

    this.authService.registro(this.usuario).subscribe({
      next: (response) => {
        console.log('Registro exitoso', response);
        this.successMessage = '✅ Usuario registrado exitosamente. Redirigiendo al login...';
        this.loading = false;
        this.cdr.detectChanges();
        setTimeout(() => {
          if (this.returnUrl) {
            this.router.navigate(['/login'], { queryParams: { returnUrl: this.returnUrl } });
          } else {
            this.router.navigate(['/login']);
          }
        }, 2000);
      },
      error: (error) => {
        console.error('Error completo en registro:', error);
        console.error('Error status:', error.status);
        console.error('Error.error:', error.error);

        this.loading = false;

        if (error.status === 0) {
          this.errorMessage = '⚠️ No se puede conectar con el servidor. Verifica que el backend esté ejecutándose en http://localhost:8080';
        } else if (error.status === 409) {
          this.errorMessage = '❌ El email o nombre de usuario ya están registrados. Por favor usa otros diferentes';
        } else if (error.status === 400) {
          this.errorMessage = '❌ Datos inválidos. Por favor verifica todos los campos';
        } else if (error.status === 500) {
          const errorMsg = typeof error.error === 'string' ? error.error : 'Error interno del servidor';
          this.errorMessage = '⚠️ Error en el servidor: ' + errorMsg;
        } else if (typeof error.error === 'string' && error.error.length > 0) {
          this.errorMessage = '❌ ' + error.error;
        } else if (error.error?.message) {
          this.errorMessage = '❌ ' + error.error.message;
        } else {
          this.errorMessage = '⚠️ Error al registrar usuario. Por favor intenta nuevamente.';
        }

        console.log('Mensaje de error establecido:', this.errorMessage);
        this.cdr.detectChanges();
      },
      complete: () => {
        if (this.loading) {
          console.warn('Formulario todavía en loading en complete(), desbloqueando...');
          this.loading = false;
          this.cdr.detectChanges();
        }
      }
    });
  }
}

