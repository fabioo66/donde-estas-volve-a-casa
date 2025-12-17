// typescript
import { Component, OnInit, AfterViewInit, OnDestroy, inject, PLATFORM_ID, ChangeDetectorRef } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { MascotaService } from '../services/mascota.service';
import { DashboardService, DashboardStats } from '../services/dashboard.service';
import { LoginResponse } from '../models/usuario.model';
import { Mascota } from '../models/mascota.model';
import { Subscription } from 'rxjs';


@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.css']
})
export class DashboardComponent implements OnInit, AfterViewInit, OnDestroy {
  private authService = inject(AuthService);
  private mascotaService = inject(MascotaService);
  private dashboardService = inject(DashboardService);
  private router = inject(Router);
  private platformId = inject(PLATFORM_ID);
  private cdr = inject(ChangeDetectorRef);
  private isBrowser: boolean;
  private subscriptions: Subscription[] = [];

  public currentUser: LoginResponse | null = null;
  public isLoading = false;
  public isLoadingStats = false;
  public error: string | null = null;
  public mascotasPerdidas: Mascota[] = [];
  public fotoActualPorMascota: Map<number, number> = new Map();
  public stats: DashboardStats | null = null;

  constructor() {
    this.isBrowser = isPlatformBrowser(this.platformId);
  }

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });

    // Cargar mascotas perdidas
    this.cargarMascotasPerdidas();
    // Cargar estadísticas también desde el inicio
    this.cargarDatos();
  }

  ngAfterViewInit(): void {
    // Cargar datos de forma más simple después de la vista
    setTimeout(() => {
      const storedUser = this.authService.getCurrentUser();
      if (storedUser) {
        this.currentUser = storedUser;
        this.cargarDatos();
      }
    }, 100); // Delay más largo para asegurar estabilidad
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  cargarMascotasPerdidas(): void {
    console.log('🟡 Cargando mascotas perdidas...');
    this.isLoading = true;
    this.error = null;
    this.mascotasPerdidas = [];

    const mascotasSub = this.mascotaService.obtenerMascotasPerdidas().subscribe({
      next: (mascotas) => {
        console.log('✅ Mascotas perdidas recibidas:', mascotas.length, mascotas);
        this.mascotasPerdidas = mascotas;
        // Inicializar el índice de foto actual para cada mascota
        mascotas.forEach(mascota => {
          this.fotoActualPorMascota.set(mascota.id, 0);
        });
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('❌ Error al cargar mascotas perdidas:', err);
        this.error = 'No se pudieron cargar las mascotas perdidas';
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
    this.subscriptions.push(mascotasSub);
  }

  public cargarDatos(): void {
    console.log('🔄 Cargando datos del dashboard...');

    // Activar loading para estadísticas
    this.isLoadingStats = true;
    this.cdr.detectChanges();

    // Cargar estadísticas (simplificado)
    this.dashboardService.obtenerEstadisticas().subscribe({
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


  obtenerImagenMascota(mascota: Mascota): string {
    if (mascota.fotos) {
      try {
        const fotosArray = JSON.parse(mascota.fotos);
        if (fotosArray && fotosArray.length > 0) {
          const fotoActual = this.fotoActualPorMascota.get(mascota.id) || 0;
          const fotoUrl = fotosArray[fotoActual];
          return `http://localhost:8080${fotoUrl}`;
        }
      } catch (error) {
        console.error('Error al parsear fotos:', error);
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
      } catch (error) {
        console.error('Error al parsear fotos:', error);
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
      'MEDIANO': 'Mediano',
      'GRANDE': 'Grande'
    };

    return tamanioMap[tamanio.toUpperCase()] || tamanio;
  }

  editarMascota(id: number): void {
    this.router.navigate(['/mascota', id, 'editar']);
  }

  eliminarMascota(id: number): void {
    // Aquí agregarías la lógica de confirmación y eliminación
    if (confirm('¿Estás seguro de que deseas eliminar esta publicación?')) {
      console.log('Eliminar mascota con ID:', id);
      // Implementar llamada al servicio para eliminar
    }
  }

  // Funciones trackBy para mejorar rendimiento
  trackByMascotaId(index: number, mascota: Mascota): number {
    return mascota.id;
  }

  trackByIndex(index: number): number {
    return index;
  }

}
