import { Component, OnInit, AfterViewInit, OnDestroy, inject, ChangeDetectorRef, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MascotaService } from '../services/mascota.service';
import { AvistamientoService } from '../services/avistamiento.service';
import { AuthService } from '../services/auth.service';
import { Mascota } from '../models/mascota.model';

@Component({
  selector: 'app-reportar-avistamiento',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reportar-avistamiento.html',
  styleUrl: './reportar-avistamiento.css',
})
export class ReportarAvistamientoComponent implements OnInit, AfterViewInit, OnDestroy {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private mascotaService = inject(MascotaService);
  private avistamientoService = inject(AvistamientoService);
  private authService = inject(AuthService);
  private cdr = inject(ChangeDetectorRef);
  private platformId = inject(PLATFORM_ID);

  public mascota: Mascota | null = null;
  public isLoading = true;
  public error: string | null = null;

  public avistamientoForm = {
    ubicacion: '',
    descripcion: '',
    fotosBase64: [] as string[]
  };

  public enviandoAvistamiento = false;
  public errorAvistamiento: string | null = null;
  public successMessage: string | null = null;

  // Mapa
  private map: any = null;
  private marker: any = null;
  private L: any = null;
  public mostrarMapa = false;

  ngOnInit(): void {
    console.log('🔴 ReportarAvistamiento ngOnInit');
    const mascotaId = this.route.snapshot.paramMap.get('id');
    console.log('📋 Mascota ID:', mascotaId);
    if (mascotaId) {
      this.cargarMascota(parseInt(mascotaId));
    } else {
      this.error = 'No se especificó una mascota';
      this.isLoading = false;
    }
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

  cargarMascota(id: number): void {
    console.log('🟡 Cargando mascota:', id);
    this.isLoading = true;
    this.mascotaService.obtenerMascota(id).subscribe({
      next: (mascota) => {
        console.log('✅ Mascota recibida:', mascota);
        this.mascota = mascota;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('❌ Error al cargar mascota:', err);
        this.error = 'No se pudo cargar la información de la mascota';
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
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
    if (this.avistamientoForm.ubicacion) {
      const coords = this.avistamientoForm.ubicacion.split(',');
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
      this.avistamientoForm.ubicacion = `${lat}, ${lng}`;
    });
  }

  agregarMarcador(lat: number, lng: number): void {
    if (!this.L) return;

    // Remover marcador anterior si existe
    if (this.marker) {
      this.marker.remove();
    }

    // Crear nuevo marcador
    this.marker = this.L.marker([lat, lng], {
      icon: this.L.icon({
        iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-red.png',
        shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
        iconSize: [25, 41],
        iconAnchor: [12, 41],
        popupAnchor: [1, -34],
        shadowSize: [41, 41]
      })
    }).addTo(this.map);

    this.marker.bindPopup('Ubicación del avistamiento').openPopup();
  }

  onFileSelected(event: any): void {
    const files = event.target.files;
    if (files && files.length > 0) {
      for (let i = 0; i < files.length; i++) {
        const file = files[i];
        const reader = new FileReader();
        reader.onload = (e: any) => {
          this.avistamientoForm.fotosBase64.push(e.target.result);
          // Forzar detección de cambios para actualizar la vista inmediatamente
          this.cdr.detectChanges();
        };
        reader.readAsDataURL(file);
      }
    }
  }

  eliminarFoto(index: number): void {
    this.avistamientoForm.fotosBase64.splice(index, 1);
  }

  enviarAvistamiento(): void {
    if (!this.mascota) return;

    // Validar campos requeridos
    if (!this.avistamientoForm.ubicacion.trim()) {
      this.errorAvistamiento = 'La ubicación es requerida';
      return;
    }

    if (!this.avistamientoForm.descripcion.trim()) {
      this.errorAvistamiento = 'La descripción es requerida';
      return;
    }

    this.enviandoAvistamiento = true;
    this.errorAvistamiento = null;
    this.successMessage = null;

    // Obtener el ID del usuario autenticado
    const currentUser = this.authService.getCurrentUser();
    if (!currentUser) {
      this.errorAvistamiento = 'No se pudo obtener la información del usuario';
      this.enviandoAvistamiento = false;
      return;
    }

    const avistamientoData = {
      mascotaId: this.mascota.id,
      usuarioId: currentUser.id,
      ubicacion: this.avistamientoForm.ubicacion,
      descripcion: this.avistamientoForm.descripcion,
      fotosBase64: this.avistamientoForm.fotosBase64
    };

    this.avistamientoService.crearAvistamiento(avistamientoData).subscribe({
      next: (response: any) => {
        this.successMessage = '¡Avistamiento reportado exitosamente!';
        this.enviandoAvistamiento = false;
        
        // Redirigir después de 2 segundos
        setTimeout(() => {
          this.router.navigate(['/']);
        }, 2000);
      },
      error: (err: any) => {
        console.error('Error al crear avistamiento:', err);
        this.errorAvistamiento = 'Error al reportar el avistamiento. Por favor, intenta nuevamente.';
        this.enviandoAvistamiento = false;
      }
    });
  }

  volver(): void {
    this.router.navigate(['/']);
  }

  obtenerImagenMascota(): string {
    if (!this.mascota || !this.mascota.fotos) {
      return 'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?w=400';
    }

    try {
      const fotosArray = JSON.parse(this.mascota.fotos);
      if (fotosArray && fotosArray.length > 0) {
        return `http://localhost:8080${fotosArray[0]}`;
      }
    } catch (e) {
      console.error('Error parseando fotos:', e);
    }
    
    return 'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?w=400';
  }
}
