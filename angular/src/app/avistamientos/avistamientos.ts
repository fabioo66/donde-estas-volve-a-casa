import { Component, OnInit, OnDestroy, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
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
  private subscription?: Subscription;

  public avistamientos: Avistamiento[] = [];
  public isLoading = true;
  public error: string | null = null;
  public fotoActualPorAvistamiento: Map<number, number> = new Map();

  constructor() {}

  ngOnInit(): void {
    this.cargarAvistamientos();
  }

  ngOnDestroy(): void {
    // Cancelar la suscripción si existe
    if (this.subscription) {
      this.subscription.unsubscribe();
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
    return 'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?w=400';
  }

  obtenerTodasLasFotos(avistamiento: Avistamiento): string[] {
    if (avistamiento.fotos) {
      try {
        const fotosArray = JSON.parse(avistamiento.fotos);
        if (fotosArray && fotosArray.length > 0) {
          return fotosArray.map((url: string) => `http://localhost:8080${url}`);
        }
      } catch (e) {
        console.error('Error parseando fotos:', e);
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
}

