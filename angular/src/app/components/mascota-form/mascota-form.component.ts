import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MascotaService, MascotaRequest } from '../../services/mascota.service';
import { Estado, Tamanio } from '../../models/mascota.model';

@Component({
  selector: 'app-mascota-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './mascota-form.component.html',
  styleUrls: ['./mascota-form.component.css']
})
export class MascotaFormComponent {
  // Propiedades del formulario usando template-driven forms
  mascota: MascotaRequest = {
    nombre: '',
    tamanio: '', // Sin ñ para compatibilidad con TypeScript
    color: '',
    fecha: new Date().toISOString().split('T')[0],
    descripcion: '',
    estado: Estado.PERDIDO_PROPIO, // Cambiado a estado correcto
    tipo: '',
    raza: '',
    coordenadas: ''
  };

  archivosSeleccionados: File[] = [];
  previsualizaciones: string[] = [];
  loading = false;
  Estados = Estado;
  Tamanios = Tamanio;

  constructor(
    private mascotaService: MascotaService,
    private router: Router
  ) {}

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
      // Convertir archivos a base64
      const fotosBase64: string[] = [];
      for (const archivo of this.archivosSeleccionados) {
        const base64 = await this.mascotaService.convertirArchivoABase64(archivo);
        fotosBase64.push(base64);
      }

      const mascotaData: MascotaRequest = {
        ...this.mascota,
        fotosBase64: fotosBase64
      };

      // TODO: Obtener ID del usuario autenticado
      const usuarioId = 1; // Placeholder - esto debería venir del servicio de autenticación

      await this.mascotaService.crearMascota(usuarioId, mascotaData).toPromise();

      alert('Mascota reportada exitosamente');
      this.router.navigate(['/mascotas']);
    } catch (error) {
      console.error('Error al crear mascota:', error);
      alert('Error al reportar la mascota. Inténtalo nuevamente.');
    } finally {
      this.loading = false;
    }
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
