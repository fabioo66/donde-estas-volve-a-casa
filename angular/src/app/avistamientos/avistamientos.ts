import { Component, OnInit, OnDestroy, inject, ChangeDetectorRef, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { AvistamientoService } from '../services/avistamiento.service';
import { Avistamiento } from '../models/avistamiento.model';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-avistamientos',
  imports: [CommonModule],
  templateUrl: './avistamientos.html',
  styleUrl: './avistamientos.css',
})
export class AvistamientosComponent implements OnInit, OnDestroy {
  private avistamientoService = inject(AvistamientoService);
  private cdr = inject(ChangeDetectorRef);
  private platformId = inject(PLATFORM_ID);
  private subscription?: Subscription;
  private isBrowser: boolean;

  public avistamientos: Avistamiento[] = [];
  public isLoading = true;
  public error: string | null = null;
  public fotoActualPorAvistamiento: Map<number, number> = new Map();

  // Mapa modal
  public mostrarMapaModal = false;
  public avistamientoSeleccionado: Avistamiento | null = null;
  private map: any = null;
  private L: any = null;

  constructor() {
    this.isBrowser = isPlatformBrowser(this.platformId);
  }

  ngOnInit(): void {
    this.cargarAvistamientos();
  }

  ngOnDestroy(): void {
    // Cancelar la suscripción si existe
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
    // Limpiar el mapa si existe
    if (this.map) {
      this.map.remove();
    }
  }

  cargarAvistamientos(): void {
    this.isLoading = true;
    this.error = null;
    this.avistamientos = [];

    // Cancelar suscripción anterior si existe
    if (this.subscription) {
      this.subscription.unsubscribe();
    }

    this.subscription = this.avistamientoService.obtenerTodosLosAvistamientos().subscribe({
      next: (avistamientos) => {
        this.avistamientos = avistamientos;
        // Inicializar el índice de foto actual para cada avistamiento
        avistamientos.forEach(avistamiento => {
          this.fotoActualPorAvistamiento.set(avistamiento.id, 0);
        });
        this.isLoading = false;
        // Forzar detección de cambios
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al cargar avistamientos:', err);
        this.error = 'No se pudieron cargar los avistamientos';
        this.isLoading = false;
        // Forzar detección de cambios incluso en error
        this.cdr.detectChanges();
      }
    });
  }

  obtenerImagenAvistamiento(avistamiento: Avistamiento): string {
    // Intentar obtener la foto del avistamiento primero
    if (avistamiento.fotos) {
      try {
        const fotosArray = JSON.parse(avistamiento.fotos);
        if (fotosArray && fotosArray.length > 0) {
          const fotoUrl = fotosArray[0];
          return `http://localhost:8080${fotoUrl}`;
        }
      } catch (e) {
        console.error('Error parseando fotos del avistamiento', avistamiento.id, ':', e);
      }
    }

    // Si no hay fotos del avistamiento, usar imagen por defecto
    return '/assets/images/mascota-default.svg';
  }

  obtenerTodasLasFotos(avistamiento: Avistamiento): string[] {
    // Obtener las fotos del avistamiento
    if (avistamiento.fotos) {
      try {
        const fotosArray = JSON.parse(avistamiento.fotos);
        if (fotosArray && fotosArray.length > 0) {
          return fotosArray.map((url: string) => `http://localhost:8080${url}`);
        }
      } catch (e) {
        console.error('Error parseando fotos del avistamiento:', e);
      }
    }
    return [];
  }

  tieneMasDe1Foto(avistamiento: Avistamiento): boolean {
    return this.obtenerTodasLasFotos(avistamiento).length > 1;
  }

  getCantidadFotos(avistamiento: Avistamiento): number {
    return this.obtenerTodasLasFotos(avistamiento).length;
  }

  getFotoActual(avistamientoId: number): number {
    return this.fotoActualPorAvistamiento.get(avistamientoId) || 0;
  }

  cambiarFoto(avistamiento: Avistamiento, direccion: 'next' | 'prev', event: Event): void {
    event.stopPropagation();
    event.preventDefault();

    const fotos = this.obtenerTodasLasFotos(avistamiento);
    if (fotos.length <= 1) return;

    const fotoActual = this.fotoActualPorAvistamiento.get(avistamiento.id) || 0;
    let nuevaFoto: number;

    if (direccion === 'next') {
      nuevaFoto = (fotoActual + 1) % fotos.length;
    } else {
      nuevaFoto = fotoActual === 0 ? fotos.length - 1 : fotoActual - 1;
    }

    this.fotoActualPorAvistamiento.set(avistamiento.id, nuevaFoto);
  }

  obtenerFechaFormateada(fecha: string | Date): string {
    if (!fecha) return '';
    const fechaObj = new Date(fecha);
    const hoy = new Date();
    const diffTime = Math.abs(hoy.getTime() - fechaObj.getTime());
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

    if (diffDays === 0) return 'Hoy';
    if (diffDays === 1) return 'Ayer';
    if (diffDays < 7) return `Hace ${diffDays} días`;
    if (diffDays < 30) return `Hace ${Math.floor(diffDays / 7)} semanas`;
    return fechaObj.toLocaleDateString('es-AR', { day: 'numeric', month: 'short' });
  }

  formatearTamanio(tamanio: string): string {
    if (!tamanio) return '';

    const tamanioMap: { [key: string]: string } = {
      'PEQUENIO': 'Pequeño',
      'PEQUEÑO': 'Pequeño',
      'MEDIANO': 'Mediano',
      'GRANDE': 'Grande'
    };

    return tamanioMap[tamanio.toUpperCase()] || tamanio;
  }

  async abrirMapaUbicacion(avistamiento: Avistamiento): Promise<void> {
    if (!avistamiento.coordenada) {
      return;
    }

    this.avistamientoSeleccionado = avistamiento;
    this.mostrarMapaModal = true;

    // Importar Leaflet dinámicamente solo en el navegador
    if (!this.L && this.isBrowser) {
      this.L = await import('leaflet');
    }

    // Esperar a que el DOM se actualice
    setTimeout(() => this.inicializarMapaModal(), 100);
  }

  cerrarMapaModal(): void {
    this.mostrarMapaModal = false;
    this.avistamientoSeleccionado = null;
    if (this.map) {
      this.map.remove();
      this.map = null;
    }
  }

  inicializarMapaModal(): void {
    if (!this.isBrowser || !this.L || !this.avistamientoSeleccionado) return;

    const coordenada = this.avistamientoSeleccionado.coordenada;
    if (!coordenada) return;

    // Parsear coordenadas (formato: "lat, lng")
    const coords = coordenada.split(',');
    if (coords.length !== 2) return;

    const lat = parseFloat(coords[0].trim());
    const lng = parseFloat(coords[1].trim());

    if (isNaN(lat) || isNaN(lng)) return;

    // Crear el mapa
    this.map = this.L.map('mapa-modal').setView([lat, lng], 15);

    // Agregar capa de OpenStreetMap
    this.L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors',
      maxZoom: 19
    }).addTo(this.map);

    // Agregar marcador rojo para avistamiento
    const marker = this.L.marker([lat, lng], {
      icon: this.L.icon({
        iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-red.png',
        shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
        iconSize: [25, 41],
        iconAnchor: [12, 41],
        popupAnchor: [1, -34],
        shadowSize: [41, 41]
      })
    }).addTo(this.map);

    marker.bindPopup(`
      <div style="text-align: center;">
        <strong>${this.avistamientoSeleccionado.mascota?.nombre || 'Mascota'}</strong><br/>
        <span style="font-size: 12px; color: #666;">Avistamiento reportado aquí</span>
      </div>
    `).openPopup();

    // Forzar recalculo del tamaño
    setTimeout(() => {
      if (this.map) {
        this.map.invalidateSize();
      }
    }, 100);
  }
}
