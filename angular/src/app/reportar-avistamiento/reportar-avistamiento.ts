import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MascotaService } from '../services/mascota.service';
import { AvistamientoService } from '../services/avistamiento.service';
import { Mascota } from '../models/mascota.model';

@Component({
  selector: 'app-reportar-avistamiento',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reportar-avistamiento.html',
  styleUrl: './reportar-avistamiento.css',
})
export class ReportarAvistamientoComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private mascotaService = inject(MascotaService);
  private avistamientoService = inject(AvistamientoService);
  private cdr = inject(ChangeDetectorRef);

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

  onFileSelected(event: any): void {
    const files = event.target.files;
    if (files && files.length > 0) {
      for (let i = 0; i < files.length; i++) {
        const file = files[i];
        const reader = new FileReader();
        reader.onload = (e: any) => {
          this.avistamientoForm.fotosBase64.push(e.target.result);
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

    const avistamientoData = {
      mascotaId: this.mascota.id,
      usuarioId: 6, // ID hardcodeado temporalmente
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
