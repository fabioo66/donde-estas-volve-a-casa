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

  // Propiedades para mensajes de éxito y error
  successMessage: string | null = null;
  errorMessage: string | null = null;

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
        console.log('🔍 Tamaño recibido (crudo):', mascota.tamanio, 'Tipo:', typeof mascota.tamanio);
        console.log('🔍 Mascota completa:', JSON.stringify(mascota, null, 2));

        // Asignar campos uno por uno para evitar problemas de binding
        this.mascota.id = mascota.id;
        this.mascota.nombre = mascota.nombre || '';
        this.mascota.color = mascota.color || '';
        this.mascota.fecha = mascota.fecha || '';
        this.mascota.descripcion = mascota.descripcion || '';
        this.mascota.estado = mascota.estado || '';
        this.mascota.tipo = mascota.tipo || '';
        this.mascota.raza = mascota.raza || '';
        this.mascota.coordenadas = mascota.coordenadas || '';
        this.mascota.fotos = mascota.fotos || '';
        this.mascota.activo = mascota.activo !== undefined ? mascota.activo : true;

        // Manejar el tamaño de forma especial - buscar tanto en 'tamanio' como en 'tamaño'
        const tamanioBackend = mascota.tamanio || (mascota as any)['tamaño'] || '';
        console.log('🔍 Tamaño encontrado:', tamanioBackend, 'desde campo:', mascota.tamanio ? 'tamanio' : 'tamaño');

        this.mascota.tamanio = tamanioBackend;

        if (tamanioBackend) {
          console.log('🔍 Tamaño antes de validar:', tamanioBackend);
          console.log('🔍 Tamaño asignado al modelo:', this.mascota.tamanio);

          // Solo normalizar si es necesario, pero usar el valor original si ya es válido
          const valoresValidos = ['PEQUENIO', 'MEDIANO', 'GRANDE'];
          if (valoresValidos.includes(tamanioBackend)) {
            console.log('🔧 Tamaño ya es válido, usando directamente:', tamanioBackend);
            this.mascota.tamanio = tamanioBackend;
          } else {
            const tamanioNormalizado = this.normalizarTamanio(tamanioBackend);
            this.mascota.tamanio = tamanioNormalizado;
            console.log('🔄 Tamaño normalizado:', this.mascota.tamanio);
          }
        } else {
          console.log('⚠️ No hay tamaño para normalizar - valor encontrado:', tamanioBackend);
          this.mascota.tamanio = '';
        }

        this.cargarFotosExistentes();
        this.loading = false;

        // Forzar detección de cambios para resolver problemas de hidratación
        this.cdr.detectChanges();

        // Debug final: verificar el estado después de la detección de cambios
        console.log('🏁 Estado final del tamaño después de detectChanges:', this.mascota.tamanio);

        // Forzar otra actualización después de un tick para asegurar el binding
        setTimeout(() => {
          console.log('⏰ Estado del tamaño después de setTimeout:', this.mascota.tamanio);
          this.cdr.detectChanges();
        }, 0);
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
        this.errorMessage = `${file.name} no es una imagen válida. Solo se permiten JPG, PNG y GIF.`;
        this.autoHideMessage('error');
        return;
      }

      // Validar tamaño (10MB)
      if (file.size > 10 * 1024 * 1024) {
        this.errorMessage = `${file.name} es demasiado grande. El tamaño máximo es 10MB.`;
        this.autoHideMessage('error');
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

  // Método para ocultar automáticamente los mensajes
  private autoHideMessage(type: 'success' | 'error'): void {
    setTimeout(() => {
      if (type === 'success') this.successMessage = null;
      else this.errorMessage = null;
    }, 5000);
  }

  // Método para cerrar mensajes manualmente
  closeMessages(): void {
    this.successMessage = null;
    this.errorMessage = null;
  }

  // Método para verificar si el formulario está válido
  isFormValid(): boolean {
    const camposObligatorios = ['nombre', 'fecha', 'tamanio', 'color', 'tipo', 'estado'];

    return camposObligatorios.every(campo => {
      const valor = this.mascota[campo as keyof Mascota];
      return valor && (typeof valor !== 'string' || valor.trim() !== '');
    });
  }

  // Método para normalizar el tamaño para que coincida con las opciones del select
  private normalizarTamanio(tamanio: string): string {
    console.log('🔧 Normalizando tamaño:', tamanio, 'Tipo:', typeof tamanio);

    if (!tamanio) {
      console.log('🔧 Tamaño vacío, retornando vacío');
      return '';
    }

    // Convertir a string si viene como número u otro tipo
    const tamanioStr = String(tamanio).trim();
    const tamanioUpper = tamanioStr.toUpperCase();

    console.log('🔧 Tamaño en mayúsculas:', tamanioUpper);

    // Mapear variaciones comunes
    if (tamanioUpper.includes('PEQUE') || tamanioUpper === 'SMALL' || tamanioUpper === 'S' || tamanioUpper === 'PEQUENIO') {
      console.log('🔧 Mapeando a PEQUENIO');
      return 'PEQUENIO';
    } else if (tamanioUpper.includes('MEDIAN') || tamanioUpper === 'MEDIUM' || tamanioUpper === 'M' || tamanioUpper === 'MEDIANO') {
      console.log('🔧 Mapeando a MEDIANO');
      return 'MEDIANO';
    } else if (tamanioUpper.includes('GRAND') || tamanioUpper === 'LARGE' || tamanioUpper === 'L' || tamanioUpper === 'GRANDE') {
      console.log('🔧 Mapeando a GRANDE');
      return 'GRANDE';
    }

    // Si ya está en el formato correcto, devolverlo
    if (['PEQUENIO', 'MEDIANO', 'GRANDE'].includes(tamanioUpper)) {
      console.log('🔧 Ya está en formato correcto:', tamanioUpper);
      return tamanioUpper;
    }

    // Si no coincide con nada, devolver vacío para forzar selección
    console.warn('⚠️ Tamaño no reconocido:', tamanio, 'Retornando vacío');
    return '';
  }

  onSubmit(): void {
    // Limpiar mensajes previos
    this.successMessage = null;
    this.errorMessage = null;

    // Verificar manualmente los campos obligatorios
    const camposObligatorios = ['nombre', 'fecha', 'tamanio', 'color', 'tipo', 'estado'];
    const tieneAlgunCampoVacio = camposObligatorios.some(campo => {
      const valor = this.mascota[campo as keyof Mascota];
      return !valor || (typeof valor === 'string' && valor.trim() === '');
    });

    // Si hay campos vacíos, mostrar error
    if (tieneAlgunCampoVacio) {
      this.errorMessage = 'Por favor completá todos los campos obligatorios marcados con *.';
      this.autoHideMessage('error');
      return;
    }

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
        this.successMessage = '¡Información actualizada exitosamente! 🐾';
        this.autoHideMessage('success');

        // Redirigir después de un breve delay
        setTimeout(() => {
          this.router.navigate(['/mis-publicaciones']);
        }, 2000);
        this.loadingSubmit = false;
      },
      error: (error) => {
        console.error('❌ Error al actualizar mascota:', error);
        this.errorMessage = 'Ocurrió un error al actualizar la información. Por favor, intentá nuevamente.';
        this.autoHideMessage('error');
        this.loadingSubmit = false;
      }
    });
  }

  cancelar(): void {
    console.log('🔙 Cancelando edición, volviendo al home...');
    this.router.navigate(['/home']);
  }
}

