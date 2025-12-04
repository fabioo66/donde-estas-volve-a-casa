import { Component, OnInit, OnDestroy, inject, PLATFORM_ID, AfterViewInit } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { MascotaService } from '../services/mascota.service';
import { Mascota } from '../models/mascota.model';

@Component({
  selector: 'app-home',
  imports: [CommonModule],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home implements OnInit, AfterViewInit, OnDestroy {
  private mascotaService = inject(MascotaService);
  private platformId = inject(PLATFORM_ID);
  private isBrowser: boolean;

  public mascotas: Mascota[] = [];
  public isLoading = true;
  public error: string | null = null;
  public fotoActualPorMascota: Map<number, number> = new Map();

  constructor() {
    this.isBrowser = isPlatformBrowser(this.platformId);
  }

  ngOnInit(): void {
    this.cargarMascotasPerdidas();
  }

  ngAfterViewInit(): void {
    // No hacer nada aquí, el carrusel se maneja con CSS y eventos de click
  }

  ngOnDestroy(): void {
    // Limpiar recursos si es necesario
  }

  cargarMascotasPerdidas(): void {
    this.isLoading = true;
    this.mascotaService.obtenerMascotasPerdidas().subscribe({
      next: (mascotas) => {
        this.mascotas = mascotas;
        // Inicializar el índice de foto actual para cada mascota
        mascotas.forEach(mascota => {
          this.fotoActualPorMascota.set(mascota.id, 0);
        });
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error al cargar mascotas perdidas:', err);
        this.error = 'No se pudieron cargar las mascotas perdidas';
        this.isLoading = false;
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
}

