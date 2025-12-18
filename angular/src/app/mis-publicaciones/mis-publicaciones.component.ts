import { Component, OnInit, inject, PLATFORM_ID, ChangeDetectorRef } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { MascotaService } from '../services/mascota.service';
import { Mascota } from '../models/mascota.model';
import { LoginResponse } from '../models/usuario.model';

@Component({
  selector: 'app-mis-publicaciones',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './mis-publicaciones.component.html',
  styleUrls: ['./mis-publicaciones.component.css']
})
export class MisPublicacionesComponent implements OnInit {
  misPublicaciones: Mascota[] = [];
  isLoading = true;
  error: string | null = null;
  currentUser: LoginResponse | null = null;
  mascotaSeleccionada: Mascota | null = null;
  mostrarModal = false;
  private platformId = inject(PLATFORM_ID);
  private isBrowser: boolean;

  constructor(
    private authService: AuthService,
    private mascotaService: MascotaService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {
    this.isBrowser = isPlatformBrowser(this.platformId);
  }

  ngOnInit(): void {
    console.log('🔧 Iniciando MisPublicacionesComponent...');

    // Exponer el componente en window para debugging
    if (this.isBrowser) {
      (window as any).misPublicacionesComponent = this;
    }

    this.currentUser = this.authService.getCurrentUser();
    console.log('👤 Usuario desde servicio:', this.currentUser);

    // Si no hay usuario en el servicio, verificar localStorage directamente (solo en browser)
    if (!this.currentUser && this.isBrowser) {
      console.log('🔍 Verificando localStorage...');
      const storedUser = localStorage.getItem('currentUser');
      if (storedUser) {
        try {
          this.currentUser = JSON.parse(storedUser);
          console.log('👤 Usuario desde localStorage:', this.currentUser);
        } catch (error) {
          console.error('❌ Error al parsear usuario de localStorage:', error);
        }
      }
    }

    if (this.currentUser && this.currentUser.id) {
      console.log('✅ Usuario encontrado, cargando publicaciones...');
      this.cargarMisPublicaciones();
    } else {
      console.log('❌ No hay usuario válido, redirigiendo al login...');
      this.router.navigate(['/login']);
    }
  }

  cargarMisPublicaciones(): void {
    if (!this.currentUser || !this.currentUser.id) {
      console.log('❌ No hay usuario o ID para cargar publicaciones');
      this.error = 'Usuario no válido';
      this.isLoading = false;
      return;
    }

    console.log('📊 Iniciando carga de publicaciones para usuario ID:', this.currentUser.id);
    console.log('🔗 URL que se va a consultar:', `http://localhost:8080/mascotas/usuario/${this.currentUser.id}`);

    this.isLoading = true;
    this.error = null;

    this.mascotaService.obtenerMascotasUsuario(this.currentUser.id).subscribe({
      next: (mascotas) => {
        console.log('✅ Publicaciones recibidas:', mascotas);
        console.log('📊 Número de publicaciones:', mascotas ? mascotas.length : 0);
        this.misPublicaciones = mascotas || [];
        this.isLoading = false;
        console.log('🔄 Estado actualizado: isLoading =', this.isLoading, ', misPublicaciones =', this.misPublicaciones);

        // Forzar detección de cambios
        this.cdr.detectChanges();
        console.log('🔄 Detectando cambios forzadamente...');
      },
      error: (error) => {
        console.error('❌ Error al cargar publicaciones:', error);
        console.error('❌ Error status:', error.status);
        console.error('❌ Error message:', error.message);
        console.error('❌ Error completo:', JSON.stringify(error, null, 2));

        let mensajeError = 'Error al cargar las publicaciones';
        if (error.status === 0) {
          mensajeError = 'No se puede conectar con el servidor. Verifica que el backend esté funcionando.';
        } else if (error.status === 404) {
          mensajeError = 'No se encontraron publicaciones para este usuario.';
        } else if (error.status === 401) {
          mensajeError = 'No tienes autorización. Inicia sesión nuevamente.';
        } else if (error.status === 500) {
          mensajeError = 'Error interno del servidor.';
        }

        this.error = mensajeError;
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  editarMascota(id: number): void {
    this.router.navigate(['/mascota', id, 'editar']);
  }

  eliminarMascota(id: number): void {
    if (confirm('¿Estás seguro de que quieres eliminar esta publicación?')) {

      this.mascotaService.eliminarMascota(id).subscribe({
        next: (mascotaActualizada) => {
          console.log('✅ Mascota eliminada (borrado lógico):', mascotaActualizada);

          // Actualizar la mascota en la lista local con los nuevos datos
          const index = this.misPublicaciones.findIndex(m => m.id === id);
          if (index !== -1) {
            this.misPublicaciones[index] = mascotaActualizada;
            console.log('📝 Mascota actualizada en la lista local');
          }

          // Actualizar también en el modal si está abierto
          if (this.mascotaSeleccionada && this.mascotaSeleccionada.id === id) {
            this.mascotaSeleccionada = mascotaActualizada;
            console.log('📝 Mascota actualizada en el modal');
          }

          // Forzar detección de cambios
          this.cdr.detectChanges();

          // Mostrar mensaje de éxito
          alert('Publicación eliminada exitosamente. Ahora aparecerá como eliminada.');

          console.log('🔄 Lista actualizada. Estado activo de la mascota:', mascotaActualizada.activo);
        },
        error: (error) => {
          console.error('❌ Error al eliminar mascota:', error);
          console.error('❌ Error status:', error.status);
          console.error('❌ Error message:', error.message);

          let mensajeError = 'Error al eliminar la publicación';
          if (error.status === 404) {
            mensajeError = 'La publicación no fue encontrada';
          } else if (error.status === 403 || error.status === 401) {
            mensajeError = 'No tienes permisos para eliminar esta publicación';
          } else if (error.status === 500) {
            mensajeError = 'Error interno del servidor al eliminar la publicación';
          }

          alert(mensajeError);
        }
      });
    }
  }

  abrirModal(mascota: Mascota): void {
    this.mascotaSeleccionada = mascota;
    this.mostrarModal = true;
  }

  cerrarModal(): void {
    this.mostrarModal = false;
    this.mascotaSeleccionada = null;
  }

  reportarAvistamiento(id: number): void {
    this.router.navigate(['/reportar-avistamiento', id]);
  }

  volverAlDashboard(): void {
    this.router.navigate(['/home']);
  }

  crearNuevaPublicacion(): void {
    this.router.navigate(['/mascota/nuevo']);
  }

  obtenerFotoPrincipal(mascota: Mascota): string {
    try {
      if (mascota.fotos) {
        const fotosArray = JSON.parse(mascota.fotos);
        if (Array.isArray(fotosArray) && fotosArray.length > 0) {
          return `http://localhost:8080${fotosArray[0]}`;
        }
      }
    } catch (error) {
      console.error('Error al parsear fotos:', error);
    }
    return '/assets/images/mascota-default.svg';
  }

  obtenerTodasLasFotos(mascota: Mascota): string[] {
    try {
      if (mascota.fotos) {
        const fotosArray = JSON.parse(mascota.fotos);
        if (Array.isArray(fotosArray)) {
          return fotosArray.map(foto => `http://localhost:8080${foto}`);
        }
      }
    } catch (error) {
      console.error('Error al parsear fotos:', error);
    }
    return [];
  }

  getEstadoTexto(estado: string): string {
    switch (estado) {
      case 'PERDIDO_PROPIO':
        return 'Perdido';
      case 'PERDIDO_AJENO':
        return 'Encontrado';
      case 'RECUPERADO':
        return 'Recuperado';
      case 'ADOPTADO':
        return 'Adoptado';
      default:
        return estado;
    }
  }

  formatearFecha(fecha: string): string {
    if (!fecha) return '';
    return new Date(fecha).toLocaleDateString('es-ES', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    });
  }

  reintentar(): void {
    this.cargarMisPublicaciones();
  }
}
