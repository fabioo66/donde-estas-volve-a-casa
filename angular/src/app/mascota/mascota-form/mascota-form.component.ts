import { Component, OnDestroy, AfterViewInit, Inject, PLATFORM_ID, ChangeDetectorRef } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MascotaService, MascotaRequest } from '../../services/mascota.service';
import { AuthService } from '../../services/auth.service';
import { Estado, Tamanio } from '../../models/mascota.model';

@Component({
  selector: 'app-mascota-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './mascota-form.component.html',
  styleUrls: ['./mascota-form.component.css']
})
export class MascotaFormComponent implements AfterViewInit, OnDestroy {
  // Propiedades del formulario usando template-driven forms
  mascota: MascotaRequest = {
    nombre: '',
    tamanio: '', // Sin ñ para compatibilidad con TypeScript
    color: '',
    fecha: new Date().toISOString().split('T')[0],
    descripcion: '',
    estado: Estado.PERDIDO_PROPIO, // Cambiado a estado correcto
    tipo: '',
    raza: '',
    coordenadas: ''
  };

  archivosSeleccionados: File[] = [];
  previsualizaciones: string[] = [];
  loading = false;
  Estados = Estado;
  Tamanios = Tamanio;

  // Propiedades para mensajes de éxito y error
  successMessage: string | null = null;
  errorMessage: string | null = null;

  // Propiedades para el mapa
  mostrarMapa = false;
  map: any;
  marker: any;
  L: any;

  constructor(
    private mascotaService: MascotaService,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  onFileSelect(event: any): void {
    const files = Array.from(event.target.files) as File[];

    // Validar tipos de archivo
    const tiposPermitidos = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif'];
    const archivosValidos = files.filter(file => {
      if (!tiposPermitidos.includes(file.type)) {
        this.errorMessage = `El archivo ${file.name} no es una imagen válida. Solo se permiten JPG, PNG y GIF.`;
        this.autoHideMessage('error');
        return false;
      }
      if (file.size > 10 * 1024 * 1024) { // 10MB
        this.errorMessage = `El archivo ${file.name} es demasiado grande. El tamaño máximo es 10MB.`;
        this.autoHideMessage('error');
        return false;
      }
      return true;
    });

    this.archivosSeleccionados = archivosValidos;

    // Generar previsualizaciones
    this.previsualizaciones = [];
    archivosValidos.forEach(file => {
      const reader = new FileReader();
      reader.onload = (e) => {
        this.previsualizaciones.push(e.target?.result as string);
        // Forzar detección de cambios para actualizar la vista inmediatamente
        this.cdr.detectChanges();
      };
      reader.readAsDataURL(file);
    });
  }

  removerImagen(index: number): void {
    this.archivosSeleccionados.splice(index, 1);
    this.previsualizaciones.splice(index, 1);
  }

  async onSubmit(form: any): Promise<void> {
    // Limpiar mensajes previos
    this.successMessage = null;
    this.errorMessage = null;

    // Verificar manualmente los campos obligatorios
    const camposObligatorios = ['nombre', 'fecha', 'tamanio', 'color', 'tipo', 'descripcion', 'estado'];
    const tieneAgunCampoVacio = camposObligatorios.some(campo => {
      const valor = this.mascota[campo as keyof MascotaRequest];
      return !valor || (typeof valor === 'string' && valor.trim() === '');
    });

    // Si hay campos vacíos, mostrar error
    if (tieneAgunCampoVacio) {
      // Marcar todos los campos como touched para mostrar errores
      Object.keys(form.controls).forEach(key => {
        form.controls[key].markAsTouched();
      });

      this.errorMessage = 'Por favor completá todos los campos obligatorios marcados con *.';
      this.autoHideMessage('error');
      return;
    }

    // Validar también el formulario de Angular
    if (form.invalid) {
      Object.keys(form.controls).forEach(key => {
        form.controls[key].markAsTouched();
      });
      this.errorMessage = 'Por favor verificá que todos los campos obligatorios estén correctamente completados.';
      this.autoHideMessage('error');
      return;
    }

    this.loading = true;

    try {
      // Obtener el usuario autenticado actual
      const currentUser = this.authService.getCurrentUser();
      if (!currentUser) {
        this.errorMessage = 'Debe estar autenticado para publicar una mascota.';
        this.autoHideMessage('error');
        this.router.navigate(['/login']);
        return;
      }

      // Convertir archivos a base64
      const fotosBase64: string[] = [];
      for (const archivo of this.archivosSeleccionados) {
        const base64 = await this.mascotaService.convertirArchivoABase64(archivo);
        fotosBase64.push(base64);
      }

      const mascotaData: MascotaRequest = {
        ...this.mascota,
        fotosBase64: fotosBase64
      };

      // Usar el ID del usuario autenticado
      const usuarioId = currentUser.id;

      await this.mascotaService.crearMascota(usuarioId, mascotaData).toPromise();

      this.successMessage = '¡Mascota reportada exitosamente! Gracias por ayudar a reunir familias. 🐾';
      this.resetForm(form);
      this.autoHideMessage('success');

      // Redirigir después de un breve delay para que el usuario vea el mensaje
      setTimeout(() => {
        this.router.navigate(['/mis-publicaciones']);
      }, 3000);
    } catch (error) {
      console.error('Error al crear mascota:', error);
      this.errorMessage = 'Ocurrió un error al reportar la mascota. Por favor, intentá nuevamente.';
      this.autoHideMessage('error');
    } finally {
      this.loading = false;
    }
  }

  esCampoInvalido(campo: any): boolean {
    return !!(campo?.invalid && campo?.touched);
  }

  getMensajeError(campo: any): string {
    if (campo?.errors?.['required']) {
      return 'Este campo es requerido';
    }
    return '';
  }

  // Método para verificar si el formulario está válido
  isFormValid(): boolean {
    const camposObligatorios = ['nombre', 'fecha', 'tamanio', 'color', 'tipo', 'descripcion', 'estado'];

    return camposObligatorios.every(campo => {
      const valor = this.mascota[campo as keyof MascotaRequest];
      return valor && (typeof valor !== 'string' || valor.trim() !== '');
    });
  }

  // Método para ocultar automáticamente los mensajes
  private autoHideMessage(type: 'success' | 'error'): void {
    setTimeout(() => {
      if (type === 'success') this.successMessage = null;
      else this.errorMessage = null;
    }, 5000);
  }

  // Método para cerrar mensajes manualmente
  closeMessages(): void {
    this.successMessage = null;
    this.errorMessage = null;
  }

  // Método para resetear el formulario
  private resetForm(form: any): void {
    this.previsualizaciones = [];
    this.archivosSeleccionados = [];
    this.mascota = {
      nombre: '',
      tamanio: '',
      color: '',
      fecha: new Date().toISOString().split('T')[0],
      descripcion: '',
      estado: Estado.PERDIDO_PROPIO,
      tipo: '',
      raza: '',
      coordenadas: ''
    };
    form.resetForm();
  }

  ngAfterViewInit(): void {
    // Inicializar el mapa cuando se muestre
    if (this.mostrarMapa) {
      setTimeout(() => this.inicializarMapa(), 100);
    }
  }

  ngOnDestroy(): void {
    // Limpiar el mapa al destruir el componente
    if (this.map) {
      this.map.remove();
    }
  }

  async toggleMapa(): Promise<void> {
    this.mostrarMapa = !this.mostrarMapa;
    if (this.mostrarMapa && !this.map && isPlatformBrowser(this.platformId)) {
      // Importar Leaflet dinámicamente solo en el navegador
      if (!this.L) {
        this.L = await import('leaflet');
      }
      setTimeout(() => this.inicializarMapa(), 100);
    }
  }

  inicializarMapa(): void {
    if (!isPlatformBrowser(this.platformId) || !this.L) return;

    // Coordenadas de La Plata por defecto
    const latDefault = -34.9215;
    const lngDefault = -57.9545;

    // Crear el mapa centrado en La Plata
    this.map = this.L.map('map').setView([latDefault, lngDefault], 13);

    // Agregar capa de OpenStreetMap
    this.L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors',
      maxZoom: 19
    }).addTo(this.map);

    // Forzar el recalculo del tamaño del mapa después de un pequeño delay
    setTimeout(() => {
      if (this.map) {
        this.map.invalidateSize();
      }
    }, 200);

    // Si ya hay coordenadas en el form, colocar el marcador ahí
    if (this.mascota.coordenadas) {
      const coords = this.mascota.coordenadas.split(',');
      if (coords.length === 2) {
        const lat = parseFloat(coords[0].trim());
        const lng = parseFloat(coords[1].trim());
        if (!isNaN(lat) && !isNaN(lng)) {
          this.agregarMarcador(lat, lng);
          this.map.setView([lat, lng], 13);
        }
      }
    }

    // Agregar evento de click en el mapa
    this.map.on('click', (e: any) => {
      const lat = e.latlng.lat;
      const lng = e.latlng.lng;
      this.agregarMarcador(lat, lng);
      this.mascota.coordenadas = `${lat}, ${lng}`;
    });
  }

  agregarMarcador(lat: number, lng: number): void {
    if (!this.L) return;

    // Remover marcador anterior si existe
    if (this.marker) {
      this.marker.remove();
    }

    // Crear nuevo marcador
    this.marker = this.L.marker([lat, lng]).addTo(this.map);
  }
}
