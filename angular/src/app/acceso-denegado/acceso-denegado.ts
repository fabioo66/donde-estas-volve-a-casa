import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-acceso-denegado',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './acceso-denegado.html',
  styleUrl: './acceso-denegado.css'
})
export class AccesoDenegadoComponent {
  constructor(private router: Router) {}

  irALogin(): void {
    this.router.navigate(['/login']);
  }

  irARegistro(): void {
    this.router.navigate(['/registro']);
  }

  irAHome(): void {
    this.router.navigate(['/home']);
  }
}
