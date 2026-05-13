import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Mascota } from '../../models/mascota.model';

export type CardViewType = 'home' | 'mis-publicaciones';

@Component({
  selector: 'app-mascota-card',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './mascota-card.component.html',
  styleUrls: ['./mascota-card.component.css']
})
export class MascotaCardComponent implements OnInit {
  @Input() mascota!: Mascota;
  @Input() viewType: CardViewType = 'home';
  @Input() fotoActual: number = 0;
  @Input() puedeEditarEliminar: boolean = false;
  @Input() isLoggedIn: boolean = false;

  @Output() cambiarFotoEvent = new EventEmitter<{ mascota: Mascota; direccion: 'next' | 'prev' }>();
  @Output() abrirModalEvent = new EventEmitter<Mascota>();
  @Output() editarEvent = new EventEmitter<number>();
  @Output() eliminarEvent = new EventEmitter<Mascota>();
  @Output() reportarAvistamientoEvent = new EventEmitter<number>();
  @Output() verUbicacionEvent = new EventEmitter<Mascota>();
  @Output() irALoginEvent = new EventEmitter<void>();

  fotosArray: string[] = [];

  ngOnInit(): void {
    this.fotosArray = this.obtenerTodasLasFotos();
  }

  obtenerTodasLasFotos(): string[] {
    if (this.mascota.fotos) {
      try {
        const fotosArray = JSON.parse(this.mascota.fotos);
        if (Array.isArray(fotosArray) && fotosArray.length > 0) {
          return fotosArray.map(foto => `http://localhost:8080${foto}`);
        }
      } catch (e) {
        console.error('Error al parsear fotos:', e);
      }
    }
    return [];
  }

  tieneMasDe1Foto(): boolean {
    return this.fotosArray.length > 1;
  }

  getCantidadFotos(): number {
    return this.fotosArray.length;
  }

  cambiarFoto(direccion: 'next' | 'prev', event: Event): void {
    event.stopPropagation();
    event.preventDefault();
    this.cambiarFotoEvent.emit({ mascota: this.mascota, direccion });
  }

  abrirModal(): void {
    this.abrirModalEvent.emit(this.mascota);
  }

  editar(): void {
    this.editarEvent.emit(this.mascota.id);
  }

  eliminar(): void {
    this.eliminarEvent.emit(this.mascota);
  }

  reportarAvistamiento(): void {
    this.reportarAvistamientoEvent.emit(this.mascota.id);
  }

  verUbicacion(): void {
    this.verUbicacionEvent.emit(this.mascota);
  }

  irALogin(): void {
    this.irALoginEvent.emit();
  }

  mapearEstado(estado: string): string {
    if (!estado) return 'Desconocido';

    const estadoMapeado: { [key: string]: string } = {
      'PERDIDO_PROPIO': 'PERDIDO',
      'PERDIDO_AJENO': 'PERDIDO',
      'ADOPTADO': 'ADOPTADO',
      'RECUPERADO': 'RECUPERADO'
    };

    return estadoMapeado[estado.toUpperCase()] || estado;
  }

  isHome(): boolean {
    return this.viewType === 'home';
  }

  isMisPublicaciones(): boolean {
    return this.viewType === 'mis-publicaciones';
  }
}



