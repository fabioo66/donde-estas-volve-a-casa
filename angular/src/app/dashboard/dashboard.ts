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
  public mascotasPerdidas: Mascota[] = []; // Cambio: mascotas perdidas en lugar de publicaciones
  public fotoActualPorMascota: Map<number, number> = new Map(); // Para el carrusel de fotos

  public stats: DashboardStats = {
    mascotasPerdidas: 0,
    recuperadas: 0,
    adoptadas: 0,
    seguimientosPendientes: 0
  };

  constructor() {
    this.isBrowser = isPlatformBrowser(this.platformId);
  }

  ngOnInit(): void {
    const userSub = this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      if (!user) {
        this.router.navigate(['/login']);
      }
    });
    this.subscriptions.push(userSub);

    // Cargar mascotas perdidas
    this.cargarMascotasPerdidas();
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
        this.stats = {
          mascotasPerdidas: 0,
          recuperadas: 0,
          adoptadas: 0,
          seguimientosPendientes: 0
        };
        this.cdr.detectChanges();
      }
    });
  }

  public navegarA(ruta: string): void {
    this.router.navigate([ruta]);
  }

  public logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  public irAPublicarMascota(): void {
    this.router.navigate(['/mascota/nuevo']);
  }

  public verTodasLasPublicaciones(): void {
    this.router.navigate(['/mis-publicaciones']);
  }

  public reportarAvistamiento(id: number): void {
    this.router.navigate(['/reportar-avistamiento', id]);
  }

  public editarMascota(id: number): void {
    this.router.navigate(['/mascota', id, 'editar']);
  }

  public getEstadoTexto(estado: string): string {
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

  public obtenerFotoPrincipal(mascota: Mascota): string {
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
    return '/assets/images/mascota-default.svg';
  }

  obtenerFotos(mascota: Mascota): string[] {
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

  anteriorFoto(mascota: Mascota): void {
    const fotos = this.obtenerFotos(mascota);
    if (fotos.length > 1) {
      const actual = this.fotoActualPorMascota.get(mascota.id) || 0;
      const nueva = actual === 0 ? fotos.length - 1 : actual - 1;
      this.fotoActualPorMascota.set(mascota.id, nueva);
    }
  }

  siguienteFoto(mascota: Mascota): void {
    const fotos = this.obtenerFotos(mascota);
    if (fotos.length > 1) {
      const actual = this.fotoActualPorMascota.get(mascota.id) || 0;
      const nueva = actual === fotos.length - 1 ? 0 : actual + 1;
      this.fotoActualPorMascota.set(mascota.id, nueva);
    }
  }

  public formatearFecha(fecha: string): string {
    if (!fecha) return '';
    return new Date(fecha).toLocaleDateString('es-ES');
  }
}
