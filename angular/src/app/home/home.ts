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
}
