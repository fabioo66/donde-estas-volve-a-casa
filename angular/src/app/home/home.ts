import { Component, OnInit, OnDestroy, inject, ChangeDetectorRef, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { MascotaService } from '../services/mascota.service';
import { GeolocalizacionService } from '../services/geolocalizacion.service';
import { AuthService } from '../services/auth.service';
import { Mascota } from '../models/mascota.model';
import { LoginResponse } from '../models/usuario.model';
import { Subscription } from 'rxjs';
import { HomeService, HomeStats } from '../services/home.service';
import { MascotaCardComponent } from '../shared/mascota-card/mascota-card.component';


@Component({
  selector: 'app-home',
  imports: [CommonModule, RouterModule],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home implements OnInit, OnDestroy {
  private mascotaService = inject(MascotaService);
  private geolocalizacionService = inject(GeolocalizacionService);
  private authService = inject(AuthService);
  private homeService = inject(HomeService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);
  private platformId = inject(PLATFORM_ID);
  private isBrowser: boolean;
  private subscription?: Subscription;


  public mascotas: Mascota[] = [];
  public isLoading = true;
  public isLoadingStats = false;
  public error: string | null = null;
  public fotoActualPorMascota: Map<number, number> = new Map();
  public ubicacionPorMascota: Map<number, string> = new Map();
  public ubicacionCargando: Set<number> = new Set();
  public currentUser: LoginResponse | null = null;
  public stats: HomeStats | null = null;

  // Mapa modal
  public mostrarMapaModal = false;
  public mascotaSeleccionada: Mascota | null = null;
  private map: any = null;
  private L: any = null;

  constructor() {
    this.isBrowser = isPlatformBrowser(this.platformId);
  }

  ngOnInit(): void {
    console.log('🔴 Home ngOnInit llamado');
    this.cargarMascotasPerdidas();
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      // Solo cargar estadísticas si el usuario está logueado
      if (user) {
        this.cargarDatos();
      }
    });
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

  cargarMascotasPerdidas(): void {
    console.log('🟡 Iniciando carga de mascotas...');
    this.isLoading = true;
    this.error = null;
    this.mascotas = [];

    // Cancelar suscripción anterior si existe
    if (this.subscription) {
      this.subscription.unsubscribe();
    }

    this.subscription = this.mascotaService.obtenerMascotasPerdidas().subscribe({
      next: (mascotas) => {
        console.log('✅ Mascotas recibidas:', mascotas.length, mascotas);
        this.mascotas = mascotas;
        // Inicializar el índice de foto actual para cada mascota
        mascotas.forEach(mascota => {
          this.fotoActualPorMascota.set(mascota.id, 0);
          this.cargarUbicacionMascota(mascota);
        });
        this.isLoading = false;
        console.log('✅ isLoading:', this.isLoading);
        // Forzar detección de cambios
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('❌ Error al cargar mascotas perdidas:', err);
        this.error = 'No se pudieron cargar las mascotas perdidas';
        this.isLoading = false;
        // Forzar detección de cambios incluso en error
        this.cdr.detectChanges();
      }
    });
  }

  obtenerUbicacionTexto(mascota: Mascota | null): string {
    if (!mascota) return 'Ubicación no disponible';
    if (!mascota.coordenadas) return 'Ubicación no disponible';
    return this.ubicacionPorMascota.get(mascota.id)
      ?? (this.ubicacionCargando.has(mascota.id) ? 'Buscando ubicación...' : 'Ubicación no disponible');
  }

  getFotoActual(mascotaId: number): number {
    return this.fotoActualPorMascota.get(mascotaId) || 0;
  }

  private cargarUbicacionMascota(mascota: Mascota): void {
    if (!mascota.coordenadas || this.ubicacionPorMascota.has(mascota.id) || this.ubicacionCargando.has(mascota.id)) {
      return;
    }

    this.ubicacionCargando.add(mascota.id);
    this.geolocalizacionService.obtenerUbicacionDesdeCoordenadas(mascota.coordenadas).subscribe({
      next: (ubicacion) => {
        this.ubicacionPorMascota.set(mascota.id, ubicacion);
        this.ubicacionCargando.delete(mascota.id);
        this.cdr.detectChanges();
      },
      error: () => {
        this.ubicacionPorMascota.set(mascota.id, 'Ubicación no disponible');
        this.ubicacionCargando.delete(mascota.id);
        this.cdr.detectChanges();
      }
    });
  }

  // ...existing code...
  onCambiarFoto(event: { mascota: Mascota; direccion: 'next' | 'prev' }): void {
    const fotoActual = this.fotoActualPorMascota.get(event.mascota.id) || 0;
    let nuevaFoto: number;

    if (event.direccion === 'next') {
      nuevaFoto = (fotoActual + 1) % (event.mascota.fotos ? JSON.parse(event.mascota.fotos).length : 1);
    } else {
      const totalFotos = event.mascota.fotos ? JSON.parse(event.mascota.fotos).length : 1;
      nuevaFoto = fotoActual === 0 ? totalFotos - 1 : fotoActual - 1;
    }

    this.fotoActualPorMascota.set(event.mascota.id, nuevaFoto);
  }

  onVerUbicacion(mascota: Mascota): void {
    this.abrirMapaUbicacion(mascota);
  }


  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  irALogin(): void {
    this.router.navigate(['/login']);
  }


  async abrirMapaUbicacion(mascota: Mascota): Promise<void> {
    if (!mascota.coordenadas) {
      return;
    }

    this.mascotaSeleccionada = mascota;
    this.mostrarMapaModal = true;
    this.cargarUbicacionMascota(mascota);

    // Importar Leaflet dinámicamente solo en el navegador
    if (!this.L && this.isBrowser) {
      this.L = await import('leaflet');
    }

    // Esperar a que el DOM se actualice
    setTimeout(() => this.inicializarMapaModal(), 100);
  }

  cerrarMapaModal(): void {
    this.mostrarMapaModal = false;
    this.mascotaSeleccionada = null;
    if (this.map) {
      this.map.remove();
      this.map = null;
    }
  }

  inicializarMapaModal(): void {
    if (!this.isBrowser || !this.L || !this.mascotaSeleccionada) return;

    const coordenadas = this.mascotaSeleccionada.coordenadas;
    if (!coordenadas) return;

    // Parsear coordenadas (formato: "lat, lng")
    const coords = coordenadas.split(',');
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

    // Agregar marcador
    const marker = this.L.marker([lat, lng], {
      icon: this.L.icon({
        iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-blue.png',
        shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
        iconSize: [25, 41],
        iconAnchor: [12, 41],
        popupAnchor: [1, -34],
        shadowSize: [41, 41]
      })
    }).addTo(this.map);

    marker.bindPopup(`
      <div style="text-align: center;">
        <strong>${this.mascotaSeleccionada.nombre}</strong><br/>
        <span style="font-size: 12px; color: #666;">Última ubicación conocida</span>
      </div>
    `).openPopup();

    // Forzar recalculo del tamaño
    setTimeout(() => {
      if (this.map) {
        this.map.invalidateSize();
      }
    }, 100);
  }

  public cargarDatos(): void {
    console.log('🔄 Cargando datos del dashboard...');

    // Activar loading para estadísticas
    this.isLoadingStats = true;
    this.cdr.detectChanges();

    // Cargar estadísticas (simplificado)
    this.homeService.obtenerEstadisticas().subscribe({
      next: (stats) => {
        this.stats = stats;
        this.isLoadingStats = false;
        this.cdr.detectChanges();
        console.log('✅ Estadísticas cargadas:', stats);
      },
      error: (error) => {
        console.error('❌ Error al cargar estadísticas:', error);
        this.isLoadingStats = false;
        this.cdr.detectChanges();
      }
    });
  }
}
