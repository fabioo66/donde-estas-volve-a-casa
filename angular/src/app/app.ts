import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterOutlet } from '@angular/router';
import { AuthService } from './services/auth.service';
import { LoginResponse } from './models/usuario.model';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, CommonModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  protected readonly title = signal('donde-estas-volve-a-casa');
  public currentUser: LoginResponse | null = null;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });
  }

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
