import { Component, OnInit, PLATFORM_ID, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { GeolocalizacionService } from '../services/geolocalizacion.service';
import { Usuario } from '../models/usuario.model';

@Component({
  selector: 'app-perfil',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './perfil.html',
  styleUrls: ['./perfil.css']
})
export class PerfilComponent implements OnInit {
  private platformId = inject(PLATFORM_ID);
  private isBrowser: boolean;

  usuario: Usuario = {
    nombreUsuario: '',
    nombre: '',
    apellido: '',
    email: '',
    telefono: '',
    genero: '',
    edad: 18,
    provincia: '',
    municipio: '',
    departamento: ''
  };

  errorMessage: string = '';
  successMessage: string = '';
  loading: boolean = false;
  loadingUbicacion: boolean = false;
  passwordActual: string = '';
  passwordNueva: string = '';
  passwordConfirmar: string = '';

  constructor(
    private authService: AuthService,
    private geoService: GeolocalizacionService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {
    this.isBrowser = isPlatformBrowser(this.platformId);
  }

  ngOnInit(): void {
    const currentUser = this.authService.getCurrentUser();
    if (!currentUser) {
      this.router.navigate(['/login']);
      return;
    }

    this.cargarPerfil(currentUser.id);
  }

  cargarPerfil(id: number): void {
    this.authService.obtenerUsuario(id).subscribe({
      next: (usuario) => {
        this.usuario = { ...usuario };
        delete this.usuario.password;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al cargar perfil', error);
        this.errorMessage = 'Error al cargar los datos del perfil';
        this.cdr.detectChanges();
      }
    });
  }

  obtenerUbicacionActual(): void {
    if (!this.isBrowser) {
      this.errorMessage = 'La geolocalización solo está disponible en el navegador';
      this.cdr.detectChanges();
      return;
    }

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
          this.successMessage = 'Ubicación actualizada';
          this.cdr.detectChanges();
          setTimeout(() => {
            this.successMessage = '';
            this.cdr.detectChanges();
          }, 3000);
        }
      })
      .catch(error => {
        console.error('Error al obtener ubicación', error);
        this.errorMessage = 'No se pudo obtener la ubicación';
        this.cdr.detectChanges();
      })
      .finally(() => {
        this.loadingUbicacion = false;
        this.cdr.detectChanges();
      });
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

    if (!this.usuario.edad) {
      this.errorMessage = '❌ La edad es obligatoria';
      this.cdr.detectChanges();
      return false;
    }
    if (this.usuario.edad < 18) {
      this.errorMessage = '❌ Debe ser mayor de 18 años';
      this.cdr.detectChanges();
      return false;
    }
    if (this.usuario.edad > 120) {
      this.errorMessage = '❌ Por favor ingrese una edad válida';
      this.cdr.detectChanges();
      return false;
    }

    if (!this.usuario.provincia || this.usuario.provincia.trim().length === 0) {
      this.errorMessage = '❌ La provincia es obligatoria';
      this.cdr.detectChanges();
      return false;
    }
    if (!this.usuario.municipio || this.usuario.municipio.trim().length === 0) {
      this.errorMessage = '❌ El municipio es obligatorio';
      this.cdr.detectChanges();
      return false;
    }
    if (!this.usuario.departamento || this.usuario.departamento.trim().length === 0) {
      this.errorMessage = '❌ El departamento es obligatorio';
      this.cdr.detectChanges();
      return false;
    }

    // Validar contraseñas solo si se intenta cambiar
    const intentaCambiarPassword = this.passwordActual || this.passwordNueva || this.passwordConfirmar;

    if (intentaCambiarPassword) {
      if (!this.passwordActual || this.passwordActual.trim().length === 0) {
        this.errorMessage = '❌ Debe ingresar su contraseña actual para cambiarla';
        this.cdr.detectChanges();
        return false;
      }

      if (!this.passwordNueva || this.passwordNueva.length === 0) {
        this.errorMessage = '❌ Debe ingresar una nueva contraseña';
        this.cdr.detectChanges();
        return false;
      }

      if (this.passwordNueva.length < 6) {
        this.errorMessage = '❌ La nueva contraseña debe tener al menos 6 caracteres';
        this.cdr.detectChanges();
        return false;
      }

      if (!this.passwordConfirmar || this.passwordConfirmar.length === 0) {
        this.errorMessage = '❌ Debe confirmar la nueva contraseña';
        this.cdr.detectChanges();
        return false;
      }

      if (this.passwordNueva !== this.passwordConfirmar) {
        this.errorMessage = '❌ Las contraseñas no coinciden';
        this.cdr.detectChanges();
        return false;
      }

      if (this.passwordActual === this.passwordNueva) {
        this.errorMessage = '❌ La nueva contraseña debe ser diferente de la actual';
        this.cdr.detectChanges();
        return false;
      }
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

    const currentUser = this.authService.getCurrentUser();
    if (!currentUser) {
      this.errorMessage = '⚠️ Sesión expirada. Por favor inicia sesión nuevamente';
      this.loading = false;
      this.cdr.detectChanges();
      setTimeout(() => {
        this.router.navigate(['/login']);
      }, 2000);
      return;
    }

    const perfilActualizado: any = {
      nombreUsuario: this.usuario.nombreUsuario,
      nombre: this.usuario.nombre,
      apellido: this.usuario.apellido,
      email: this.usuario.email,
      telefono: this.usuario.telefono,
      genero: this.usuario.genero,
      edad: this.usuario.edad,
      provincia: this.usuario.provincia,
      municipio: this.usuario.municipio,
      departamento: this.usuario.departamento
    };

    // Solo incluir passwords si se está intentando cambiar
    if (this.passwordNueva && this.passwordActual) {
      perfilActualizado.passwordActual = this.passwordActual;
      perfilActualizado.passwordNueva = this.passwordNueva;
    }

    console.log('Actualizando perfil...');

    this.authService.actualizarPerfil(currentUser.id, perfilActualizado).subscribe({
      next: (response) => {
        console.log('Perfil actualizado', response);
        this.successMessage = '✅ Perfil actualizado exitosamente';

        this.passwordActual = '';
        this.passwordNueva = '';
        this.passwordConfirmar = '';

        this.loading = false;
        this.cdr.detectChanges();

        setTimeout(() => {
          this.successMessage = '';
          this.cdr.detectChanges();
        }, 5000);
      },
      error: (error) => {
        console.error('Error al actualizar perfil:', error);
        this.loading = false;

        if (error.status === 0) {
          this.errorMessage = '⚠️ No se puede conectar con el servidor. Verifica que el backend esté ejecutándose';
        } else if (error.status === 404) {
          this.errorMessage = '❌ Usuario no encontrado. Por favor inicia sesión nuevamente';
          setTimeout(() => {
            this.authService.logout();
            this.router.navigate(['/login']);
          }, 2000);
        } else if (error.status === 400) {
          this.errorMessage = '❌ Datos inválidos. Por favor verifica todos los campos';
        } else if (error.status === 401) {
          this.errorMessage = '❌ Contraseña actual incorrecta. Por favor verifica e intenta nuevamente';
        } else if (error.status === 409) {
          this.errorMessage = '❌ El email o nombre de usuario ya están en uso por otra cuenta';
        } else if (error.status === 500) {
          const errorMsg = typeof error.error === 'string' ? error.error : 'Error interno del servidor';
          this.errorMessage = '⚠️ Error en el servidor: ' + errorMsg;
        } else if (typeof error.error === 'string') {
          this.errorMessage = '❌ ' + error.error;
        } else if (error.error?.message) {
          this.errorMessage = '❌ ' + error.error.message;
        } else {
          this.errorMessage = '⚠️ Error al actualizar perfil. Por favor intenta nuevamente';
        }

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

  cerrarSesion(): void {
    this.authService.logout();
    this.router.navigate(['/home']);
  }
}

