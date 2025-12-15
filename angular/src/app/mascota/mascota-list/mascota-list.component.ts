import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MascotaService } from '../../services/mascota.service';
import { Mascota, Estado, Tamanio } from '../../models/mascota.model';

@Component({
  selector: 'app-mascota-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './mascota-list.component.html',
  styleUrls: ['./mascota-list.component.css']
})
export class MascotaListComponent implements OnInit {
  mascotas: Mascota[] = [];
  mascotasFiltradas: Mascota[] = [];
  loading = true;

  // Filtros
  filtroNombre = '';
  filtroTamanio = '';
  filtroColor = '';
  filtroTipo = '';
  filtroEstado = 'PERDIDO_PROPIO'; // Por defecto mostrar mascotas perdidas propias

  // Opciones para filtros
  Estados = Estado;
  Tamanios = Tamanio;
  tiposAnimales = ['PERRO', 'GATO', 'OTRO'];

  // Paginación
  paginaActual = 1;
  itemsPorPagina = 9;
  totalPaginas = 1;

  constructor(
    private mascotaService: MascotaService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cargarMascotas();
  }

  cargarMascotas(): void {
    this.loading = true;
    this.mascotaService.obtenerMascotasPerdidas().subscribe({
      next: (mascotas) => {
        this.mascotas = mascotas;
        this.aplicarFiltros();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error al cargar mascotas:', error);
        this.loading = false;
      }
    });
  }

  aplicarFiltros(): void {
    let mascotasTemp = [...this.mascotas];

    // Filtro por nombre
    if (this.filtroNombre.trim()) {
      mascotasTemp = mascotasTemp.filter(mascota =>
        mascota.nombre.toLowerCase().includes(this.filtroNombre.toLowerCase())
      );
    }

    // Filtro por tamaño
    if (this.filtroTamanio) {
      mascotasTemp = mascotasTemp.filter(mascota =>
        mascota.tamanio === this.filtroTamanio
      );
    }

    // Filtro por color
    if (this.filtroColor.trim()) {
      mascotasTemp = mascotasTemp.filter(mascota =>
        mascota.color.toLowerCase().includes(this.filtroColor.toLowerCase())
      );
    }

    // Filtro por tipo
    if (this.filtroTipo) {
      mascotasTemp = mascotasTemp.filter(mascota =>
        mascota.tipo === this.filtroTipo
      );
    }

    // Filtro por estado
    if (this.filtroEstado) {
      mascotasTemp = mascotasTemp.filter(mascota =>
        mascota.estado === this.filtroEstado
      );
    }

    this.mascotasFiltradas = mascotasTemp;
    this.calcularPaginacion();
    this.paginaActual = 1; // Reset a la primera página al aplicar filtros
  }

  calcularPaginacion(): void {
    this.totalPaginas = Math.ceil(this.mascotasFiltradas.length / this.itemsPorPagina);
  }

  get mascotasPaginadas(): Mascota[] {
    const inicio = (this.paginaActual - 1) * this.itemsPorPagina;
    const fin = inicio + this.itemsPorPagina;
    return this.mascotasFiltradas.slice(inicio, fin);
  }

  cambiarPagina(nuevaPagina: number): void {
    if (nuevaPagina >= 1 && nuevaPagina <= this.totalPaginas) {
      this.paginaActual = nuevaPagina;
    }
  }

  get paginasArray(): number[] {
    return Array.from({ length: this.totalPaginas }, (_, i) => i + 1);
  }

  limpiarFiltros(): void {
    this.filtroNombre = '';
    this.filtroTamanio = '';
    this.filtroColor = '';
    this.filtroTipo = '';
    this.filtroEstado = 'PERDIDO_PROPIO';
    this.aplicarFiltros();
  }

  verDetalle(mascota: Mascota): void {
    this.router.navigate(['/mascota', mascota.id]);
  }

  editarMascota(mascota: Mascota): void {
    this.router.navigate(['/mascota', mascota.id, 'editar']);
  }

  obtenerPrimeraFoto(mascota: Mascota): string {
    if (mascota.fotos) {
      try {
        const fotos = JSON.parse(mascota.fotos);
        if (fotos && fotos.length > 0) {
          return 'http://localhost:8080' + fotos[0];
        }
      } catch (error) {
        console.error('Error al parsear fotos:', error);
      }
    }
    // Imagen por defecto
    return '/assets/images/mascota-default.svg';
  }

  formatearFecha(fecha: string): string {
    return new Date(fecha).toLocaleDateString('es-AR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    });
  }

  getEstadoColor(estado: string): string {
    switch (estado) {
      case 'PERDIDO':
        return 'bg-red-100 text-red-800 dark:bg-red-900/20 dark:text-red-400';
      case 'ADOPTADO':
        return 'bg-blue-100 text-blue-800 dark:bg-blue-900/20 dark:text-blue-400';
      case 'RECUPERADO':
        return 'bg-green-100 text-green-800 dark:bg-green-900/20 dark:text-green-400';
      default:
        return 'bg-gray-100 text-gray-800 dark:bg-gray-900/20 dark:text-gray-400';
    }
  }

  getTamanioTexto(tamanio: string): string {
    switch (tamanio) {
      case 'PEQUENIO':
        return 'Pequeño';
      case 'MEDIANO':
        return 'Mediano';
      case 'GRANDE':
        return 'Grande';
      default:
        return tamanio;
    }
  }

  reportarMascota(): void {
    this.router.navigate(['/mascota/nuevo']);
  }

  getAbsValue(value: number): number {
    return Math.abs(value);
  }
}
