import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { MascotaService, MascotaRequest } from '../../services/mascota.service';
import { Mascota, Estado, Tamanio } from '../../models/mascota.model';

@Component({
  selector: 'app-mascota-edit',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './mascota-edit.component.html',
  styleUrls: ['./mascota-edit.component.css']
})
export class MascotaEditComponent implements OnInit {
  // Objeto para el formulario template-driven
  mascotaForm: MascotaRequest = {
    nombre: '',
    tamanio: '',
    color: '',
    fecha: '',
    descripcion: '',
    estado: '',
    tipo: '',
    raza: '',
    coordenadas: ''
  };

  mascotaId: number = 0;
  mascota: Mascota | null = null;
  archivosSeleccionados: File[] = [];
  previsualizaciones: string[] = [];
  fotosActuales: string[] = [];
  loading = false;
  loadingMascota = true;
  Estados = Estado;
  Tamanios = Tamanio;

  constructor(
    private mascotaService: MascotaService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.mascotaId = +params['id'];
      this.cargarMascota();
    });
  }

  cargarMascota(): void {
    this.loadingMascota = true;
    this.mascotaService.obtenerMascota(this.mascotaId).subscribe({
      next: (mascota) => {
        this.mascota = mascota;
        this.llenarFormulario(mascota);
        this.cargarFotosActuales(mascota);
        this.loadingMascota = false;
      },
      error: (error) => {
        console.error('Error al cargar mascota:', error);
        alert('Error al cargar la información de la mascota');
        this.router.navigate(['/mascotas']);
        this.loadingMascota = false;
      }
    });
  }

  llenarFormulario(mascota: Mascota): void {
    this.mascotaForm = {
      nombre: mascota.nombre,
      tamanio: mascota.tamano,
      color: mascota.color,
      fecha: mascota.fecha,
      descripcion: mascota.descripcion || '',
      estado: mascota.estado,
      tipo: mascota.tipo,
      raza: mascota.raza || '',
      coordenadas: mascota.coordenadas || ''
    };
  }

  cargarFotosActuales(mascota: Mascota): void {
    if (mascota.fotos) {
      try {
        this.fotosActuales = JSON.parse(mascota.fotos);
      } catch (error) {
        console.error('Error al parsear fotos:', error);
        this.fotosActuales = [];
      }
    }
  }

  onFileSelect(event: any): void {
    const files = Array.from(event.target.files) as File[];

    // Validar tipos de archivo
    const tiposPermitidos = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif'];
    const archivosValidos = files.filter(file => {
      if (!tiposPermitidos.includes(file.type)) {
        alert(`El archivo ${file.name} no es una imagen válida. Solo se permiten JPG, PNG y GIF.`);
        return false;
      }
      if (file.size > 10 * 1024 * 1024) { // 10MB
        alert(`El archivo ${file.name} es demasiado grande. El tamaño máximo es 10MB.`);
        return false;
      }
      return true;
    });

    this.archivosSeleccionados = archivosValidos;

    // Generar previsualizaciones
    this.previsualizaciones = [];
    archivosValidos.forEach(file => {
      const reader = new FileReader();
      reader.onload = (e) => {
        this.previsualizaciones.push(e.target?.result as string);
      };
      reader.readAsDataURL(file);
    });
  }

  eliminarArchivo(index: number): void {
    this.archivosSeleccionados.splice(index, 1);
    this.previsualizaciones.splice(index, 1);
  }

  eliminarFotoActual(index: number): void {
    this.fotosActuales.splice(index, 1);
  }

  async onSubmit(form: any): Promise<void> {
    if (form.invalid) {
      // Marcar todos los campos como touched para mostrar errores
      Object.keys(form.controls).forEach(key => {
        form.controls[key].markAsTouched();
      });
      return;
    }

    this.loading = true;

    try {
      // Convertir archivos nuevos a base64 (solo si hay archivos seleccionados)
      const fotosBase64: string[] = [];
      if (this.archivosSeleccionados.length > 0) {
        for (const archivo of this.archivosSeleccionados) {
          const base64 = await this.mascotaService.convertirArchivoABase64(archivo);
          fotosBase64.push(base64);
        }
      }

      const mascotaData: MascotaRequest = {
        ...this.mascotaForm,
        // Solo enviar fotos si hay nuevas imágenes seleccionadas
        ...(fotosBase64.length > 0 && { fotosBase64: fotosBase64 })
      };

      await this.mascotaService.actualizarMascota(this.mascotaId, mascotaData).toPromise();

      alert('Mascota actualizada exitosamente');
      this.router.navigate(['/mascotas']);
    } catch (error) {
      console.error('Error al actualizar mascota:', error);
      alert('Error al actualizar la mascota. Inténtalo nuevamente.');
    } finally {
      this.loading = false;
    }
  }

  async onDelete(): Promise<void> {
    if (!confirm('¿Estás seguro que deseas eliminar esta mascota?')) {
      return;
    }

    this.loading = true;

    try {
      await this.mascotaService.eliminarMascota(this.mascotaId).toPromise();
      alert('Mascota eliminada exitosamente');
      this.router.navigate(['/mascotas']);
    } catch (error) {
      console.error('Error al eliminar mascota:', error);
      alert('Error al eliminar la mascota. Inténtalo nuevamente.');
    } finally {
      this.loading = false;
    }
  }

  onCancel(): void {
    this.router.navigate(['/mascotas']);
  }

  esCampoInvalido(campo: any): boolean {
    return !!(campo?.invalid && campo?.touched);
  }

  getMensajeError(campo: any): string {
    if (campo?.errors?.['required']) {
      return 'Este campo es requerido';
    }
    return '';
  }
}
