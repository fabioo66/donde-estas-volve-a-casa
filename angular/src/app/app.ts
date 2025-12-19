import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from './services/auth.service';
import { LoginResponse } from './models/usuario.model';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  protected readonly title = signal('donde-estas-volve-a-casa');
  public mobileMenuOpen = false;
  public userMenuOpen = false; // Nueva propiedad para el menú desplegable del usuario
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

    // El header ahora se muestra en todas las páginas
    this.showHeader = true;
  }

  // Métodos del menú móvil
  toggleMobileMenu() {
    this.mobileMenuOpen = !this.mobileMenuOpen;
  }

  closeMobileMenu() {
    this.mobileMenuOpen = false;
  }

  // Métodos del menú de usuario
  toggleUserMenu() {
    this.userMenuOpen = !this.userMenuOpen;
  }

  closeUserMenu() {
    this.userMenuOpen = false;
  }

  // Métodos de autenticación
  isLoggedIn(): boolean {
    return this.authService.isAuthenticated();
  }

  logout(): void {
    this.closeUserMenu();
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  irALogin(): void {
    this.closeMobileMenu();
    this.router.navigate(['/login']);
  }

  irAPerfil(): void {
    this.closeMobileMenu();
    this.router.navigate(['/perfil']);
  }

  irAHome(): void {
    this.closeMobileMenu();
    this.router.navigate(['/home']);
  }
}
