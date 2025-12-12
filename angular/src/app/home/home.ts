import { Component, OnInit, OnDestroy, inject, ChangeDetectorRef, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { RouterModule, Router, RouterLink } from '@angular/router';
import { MascotaService } from '../services/mascota.service';
import { AuthService } from '../services/auth.service';
import { Mascota } from '../models/mascota.model';
import { LoginResponse } from '../models/usuario.model';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-home',
  imports: [CommonModule, RouterModule, RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home implements OnInit, OnDestroy {
  private mascotaService = inject(MascotaService);
  private authService = inject(AuthService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);
  private platformId = inject(PLATFORM_ID);
  private isBrowser: boolean;
  private subscription?: Subscription;

  public mascotas: Mascota[] = [];
  public isLoading = true;
  public error: string | null = null;
  public fotoActualPorMascota: Map<number, number> = new Map();
  public currentUser: LoginResponse | null = null;

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

  obtenerImagenMascota(mascota: Mascota): string {
    if (mascota.fotos) {
      try {
        const fotosArray = JSON.parse(mascota.fotos);
        if (fotosArray && fotosArray.length > 0) {
          const fotoUrl = fotosArray[0];
          return `http://localhost:8080${fotoUrl}`;
        }
      } catch (e) {
        console.error('Error parseando fotos:', e);
      }
    }
    return 'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?w=400';
  }

  obtenerTodasLasFotos(mascota: Mascota): string[] {
    if (mascota.fotos) {
      try {
        const fotosArray = JSON.parse(mascota.fotos);
        if (fotosArray && fotosArray.length > 0) {
          return fotosArray.map((url: string) => `http://localhost:8080${url}`);
        }
      } catch (e) {
        console.error('Error parseando fotos:', e);
      }
    }
    return [];
  }

  tieneMasDe1Foto(mascota: Mascota): boolean {
    return this.obtenerTodasLasFotos(mascota).length > 1;
  }

  getCantidadFotos(mascota: Mascota): number {
    return this.obtenerTodasLasFotos(mascota).length;
  }

  getFotoActual(mascotaId: number): number {
    return this.fotoActualPorMascota.get(mascotaId) || 0;
  }

  cambiarFoto(mascota: Mascota, direccion: 'next' | 'prev', event: Event): void {
    event.stopPropagation();
    event.preventDefault();

    const fotos = this.obtenerTodasLasFotos(mascota);
    if (fotos.length <= 1) return;

    const fotoActual = this.fotoActualPorMascota.get(mascota.id) || 0;
    let nuevaFoto: number;

    if (direccion === 'next') {
      nuevaFoto = (fotoActual + 1) % fotos.length;
    } else {
      nuevaFoto = fotoActual === 0 ? fotos.length - 1 : fotoActual - 1;
    }

    this.fotoActualPorMascota.set(mascota.id, nuevaFoto);
  }

  obtenerNombreZona(coordenadas: string): string {
    if (!coordenadas) return 'Ubicación desconocida';
    return coordenadas;
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

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  irALogin(): void {
    this.router.navigate(['/login']);
  }

  irAPerfil(): void {
    this.router.navigate(['/perfil']);
  }

  cerrarSesion(): void {
    this.authService.logout();
  }

  async abrirMapaUbicacion(mascota: Mascota): Promise<void> {
    if (!mascota.coordenadas) {
      return;
    }

    this.mascotaSeleccionada = mascota;
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
}
