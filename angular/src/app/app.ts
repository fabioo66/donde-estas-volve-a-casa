import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterOutlet, RouterLink, RouterLinkActive, NavigationEnd } from '@angular/router';
import { AuthService } from './services/auth.service';
import { LoginResponse } from './models/usuario.model';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  protected readonly title = signal('donde-estas-volve-a-casa');
  public mobileMenuOpen = false;
  public currentUser: LoginResponse | null = null;
  public showHeader = true; // Nueva propiedad para controlar la visibilidad del header

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });

    // Escuchar cambios de ruta para ocultar header en dashboard
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      this.showHeader = !event.url.startsWith('/dashboard');
    });
  }

  // Métodos del menú móvil
  toggleMobileMenu() {
    this.mobileMenuOpen = !this.mobileMenuOpen;
  }

  closeMobileMenu() {
    this.mobileMenuOpen = false;
  }

  // Métodos de autenticación
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
    this.router.navigate(['/home']);
  }

  irAHome(): void {
    this.router.navigate(['/home']);
  }
}
