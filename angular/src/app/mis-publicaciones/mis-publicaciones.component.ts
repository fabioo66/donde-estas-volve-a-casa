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

  // Propiedades para mensajes de éxito y error
  successMessage: string | null = null;
  errorMessage: string | null = null;

  // Propiedades para modal de confirmación de eliminación
  mostrarModalEliminacion = false;
  mascotaAEliminar: Mascota | null = null;

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

    if (this.currentUser && this.currentUser.id) {
      console.log('✅ Usuario encontrado, cargando publicaciones...');
      this.cargarMisPublicaciones();
    } else {
      console.log('❌ No hay usuario válido, redirigiendo al login...');
      this.router.navigate(['/login']);
    }
  }

  private getCurrentUserOrNull(): LoginResponse | null {
    const currentUser = this.authService.getCurrentUser();
    return currentUser?.id ? currentUser : null;
  }

  private finalizarCarga(): void {
    this.isLoading = false;
    this.cdr.detectChanges();
  }

  private obtenerMensajeErrorHttp(error: any, fallback: string): string {
    if (error?.status === 0) {
      return 'No se puede conectar con el servidor. Verifica que el backend esté funcionando.';
    }

    if (error?.status === 401) {
      return 'No tienes autorización. Inicia sesión nuevamente.';
    }

    if (error?.status === 403) {
      return 'No tienes permisos para realizar esta acción.';
    }

    if (error?.status === 404) {
      return 'No se encontraron publicaciones para este usuario.';
    }

    if (error?.status === 500) {
      return 'Error interno del servidor.';
    }

    if (typeof error?.error === 'string' && error.error.trim()) {
      return error.error;
    }

    if (typeof error?.error?.message === 'string' && error.error.message.trim()) {
      return error.error.message;
    }

    if (typeof error?.message === 'string' && error.message.trim()) {
      return error.message;
    }

    return fallback;
  }

  cargarMisPublicaciones(): void {
    this.currentUser = this.getCurrentUserOrNull();

    if (!this.currentUser) {
      console.log('❌ No hay usuario o ID para cargar publicaciones');
      this.error = 'Usuario no válido';
      this.finalizarCarga();
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
        console.log('🔄 Estado actualizado: isLoading =', this.isLoading, ', misPublicaciones =', this.misPublicaciones);

        // Forzar detección de cambios
        this.finalizarCarga();
        console.log('🔄 Detectando cambios forzadamente...');
      },
      error: (error) => {
        console.error('❌ Error al cargar publicaciones:', error);
        console.error('❌ Error status:', error.status);
        console.error('❌ Error message:', error.message);
        console.error('❌ Error completo:', JSON.stringify(error, null, 2));

        this.error = this.obtenerMensajeErrorHttp(error, 'Error al cargar las publicaciones');
        this.finalizarCarga();
      }
    });
  }

  editarMascota(id: number): void {
    this.router.navigate(['/mascota', id, 'editar']);
  }

  eliminarMascota(mascota: Mascota): void {
    // Mostrar modal de confirmación personalizado
    this.mascotaAEliminar = mascota;
    this.mostrarModalEliminacion = true;
  }

  confirmarEliminacion(): void {
    if (!this.mascotaAEliminar) return;

    const id = this.mascotaAEliminar.id;
    this.cerrarModalEliminacion();

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

        // Mostrar mensaje de éxito bonito
        this.successMessage = '✅ Publicación eliminada exitosamente. La mascota ahora aparecerá como eliminada.';
        this.cdr.detectChanges();
        this.autoHideMessage('success');

        console.log('🔄 Lista actualizada. Estado activo de la mascota:', mascotaActualizada.activo);
      },
      error: (error) => {
        console.error('❌ Error al eliminar mascota:', error);
        console.error('❌ Error status:', error.status);
        console.error('❌ Error message:', error.message);

        if (error?.status === 404) {
          this.errorMessage = 'La publicación no fue encontrada';
        } else if (error?.status === 403 || error?.status === 401) {
          this.errorMessage = 'No tienes permisos para eliminar esta publicación';
        } else if (error?.status === 500) {
          this.errorMessage = 'Error interno del servidor al eliminar la publicación';
        } else {
          this.errorMessage = this.obtenerMensajeErrorHttp(error, 'Error al eliminar la publicación');
        }

        this.cdr.detectChanges();
        this.autoHideMessage('error');
      }
    });
  }

  cerrarModalEliminacion(): void {
    this.mostrarModalEliminacion = false;
    this.mascotaAEliminar = null;
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

  // Método para ocultar automáticamente los mensajes
  private autoHideMessage(type: 'success' | 'error'): void {
    setTimeout(() => {
      if (type === 'success') this.successMessage = null;
      else this.errorMessage = null;
      this.cdr.detectChanges();
    }, 5000);
  }

  // Método para cerrar mensajes manualmente
  closeMessages(): void {
    this.successMessage = null;
    this.errorMessage = null;
  }

  // Método para reintentar la carga (para el botón de error)
  reintentar(): void {
    this.cargarMisPublicaciones();
  }
}
