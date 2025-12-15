import { Component, OnInit, AfterViewInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MascotaService } from '../../services/mascota.service';
import { Mascota } from '../../models/mascota.model';

@Component({
  selector: 'app-mascota-edit',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './mascota-edit.component.html',
  styleUrls: ['./mascota-edit.component.css']
})
export class MascotaEditComponent implements OnInit, AfterViewInit {
  mascotaId: number = 0;
  mascota: Mascota = {
    id: 0,
    nombre: '',
    tamanio: '',
    color: '',
    fecha: '',
    descripcion: '',
    estado: '',
    tipo: '',
    raza: '',
    coordenadas: '',
    fotos: '',
    activo: true
  };

  archivosSeleccionados: File[] = [];
  fotosPrevisualizacion: string[] = [];
  loading = true;
  loadingSubmit = false;
  error: string | null = null;
  isViewInitialized = false;

  constructor(
    private mascotaService: MascotaService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.mascotaId = +params['id'];
      console.log('🔍 ID de mascota a cargar:', this.mascotaId);
      if (this.mascotaId && this.mascotaId > 0) {
        this.cargarMascota();
      } else {
        console.error('❌ ID de mascota inválido:', this.mascotaId);
        this.error = 'ID de mascota inválido';
        this.loading = false;
      }
    });
  }

  trackByIndex(index: number): number {
    return index;
  }

  ngAfterViewInit(): void {
    this.isViewInitialized = true;
    this.cdr.detectChanges();
  }

  cargarMascota(): void {
    this.loading = true;
    this.error = null;
    console.log(`🔄 Iniciando carga de mascota con ID: ${this.mascotaId}`);
    console.log(`🌐 URL que se llamará: http://localhost:8080/mascotas/${this.mascotaId}`);

    this.mascotaService.obtenerMascota(this.mascotaId).subscribe({
      next: (mascota) => {
        console.log('✅ Mascota cargada exitosamente:', mascota);
        this.mascota = { ...mascota };
        this.cargarFotosExistentes();
        this.loading = false;
        // Forzar detección de cambios para resolver problemas de hidratación
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('❌ Error detallado al cargar mascota:', error);
        console.error('❌ Status:', error.status);
        console.error('❌ Message:', error.message);

        this.error = `Error al cargar la información de la mascota: ${error.status || 'Desconocido'} - ${error.message || 'Error de conexión'}`;
        this.loading = false;

        // Forzar detección de cambios
        this.cdr.detectChanges();
      }
    });
  }

  cargarFotosExistentes(): void {
    if (this.mascota.fotos) {
      try {
        const fotos = JSON.parse(this.mascota.fotos);
        this.fotosPrevisualizacion = fotos.map((foto: string) => `http://localhost:8080${foto}`);
      } catch (error) {
        console.error('Error al parsear fotos:', error);
        this.fotosPrevisualizacion = [];
      }
    }
  }

  onFileSelect(event: any): void {
    const files = Array.from(event.target.files) as File[];
    this.archivosSeleccionados = [];

    files.forEach(file => {
      // Validar tipo
      if (!file.type.startsWith('image/')) {
        alert(`${file.name} no es una imagen válida`);
        return;
      }

      // Validar tamaño (10MB)
      if (file.size > 10 * 1024 * 1024) {
        alert(`${file.name} es demasiado grande. Máximo 10MB`);
        return;
      }

      this.archivosSeleccionados.push(file);

      // Crear preview
      const reader = new FileReader();
      reader.onload = (e) => {
        this.fotosPrevisualizacion.push(e.target?.result as string);
      };
      reader.readAsDataURL(file);
    });
  }

  eliminarFoto(index: number): void {
    this.fotosPrevisualizacion.splice(index, 1);
    if (index < this.archivosSeleccionados.length) {
      this.archivosSeleccionados.splice(index, 1);
    }
  }

  esCampoInvalido(campo: any): boolean {
    return campo?.invalid && campo?.touched;
  }

  onSubmit(): void {
    this.loadingSubmit = true;
    console.log('🔄 Enviando datos actualizados:', this.mascota);

    // Crear el objeto de datos de la mascota
    const mascotaData = {
      nombre: this.mascota.nombre,
      tamanio: this.mascota.tamanio,
      color: this.mascota.color,
      fecha: this.mascota.fecha,
      descripcion: this.mascota.descripcion || '',
      estado: this.mascota.estado,
      tipo: this.mascota.tipo || '',
      raza: this.mascota.raza || '',
      coordenadas: this.mascota.coordenadas || ''
    };

    this.mascotaService.actualizarMascota(this.mascotaId, mascotaData).subscribe({
      next: (response) => {
        console.log('✅ Mascota actualizada:', response);
        alert('Mascota actualizada exitosamente');
        this.router.navigate(['/mis-publicaciones']);
        this.loadingSubmit = false;
      },
      error: (error) => {
        console.error('❌ Error al actualizar mascota:', error);
        alert('Error al actualizar la mascota. Inténtalo nuevamente.');
        this.loadingSubmit = false;
      }
    });
  }

  cancelar(): void {
    console.log('🔙 Cancelando edición, volviendo al dashboard...');
    this.router.navigate(['/dashboard']);
  }
}

