import { Injectable, PLATFORM_ID, inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, timer } from 'rxjs';
import { tap } from 'rxjs/operators';
import { Usuario, LoginRequest, LoginResponse } from '../models/usuario.model';
import { JwtHelper } from '../utils/jwt-helper';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/usuarios';
  private currentUserSubject = new BehaviorSubject<LoginResponse | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();
  private platformId = inject(PLATFORM_ID);
  private isBrowser: boolean;
  private tokenCheckInterval: any;

  constructor(private http: HttpClient) {
    this.isBrowser = isPlatformBrowser(this.platformId);

    // Cargar usuario desde localStorage solo si estamos en el navegador
    if (this.isBrowser) {
      this.loadUserFromStorage();
      this.startTokenExpirationCheck();
    }
  }

  /**
   * Carga el usuario desde localStorage y valida su token
   */
  private loadUserFromStorage(): void {
    const storedUser = localStorage.getItem('currentUser');
    if (storedUser) {
      try {
        const parsedUser = JSON.parse(storedUser);
        if (this.isValidStoredUser(parsedUser)) {
          this.currentUserSubject.next(parsedUser);
          console.log('✅ Usuario cargado desde localStorage con token válido');
        } else {
          console.log('❌ Token expirado encontrado en localStorage, limpiando...');
          this.clearExpiredSession();
        }
      } catch (error) {
        console.error('❌ Error al parsear usuario de localStorage:', error);
        this.clearExpiredSession();
      }
    }
  }

  /**
   * Valida si un usuario almacenado tiene un token válido
   */
  private isValidStoredUser(user: any): boolean {
    if (!user || !user.token) {
      return false;
    }

    return !JwtHelper.isTokenExpired(user.token);
  }

  /**
   * Inicia el chequeo periódico de expiración del token
   */
  private startTokenExpirationCheck(): void {
    // Verificar cada minuto si el token está por expirar
    this.tokenCheckInterval = timer(0, 60000).subscribe(() => {
      this.checkTokenExpiration();
    });
  }

  /**
   * Verifica si el token actual está expirado o por expirar
   */
  private checkTokenExpiration(): void {
    const currentUser = this.currentUserSubject.value;
    if (!currentUser || !currentUser.token) {
      return;
    }

    if (JwtHelper.isTokenExpired(currentUser.token)) {
      console.log('🔒 Token expirado detectado, cerrando sesión...');
      this.clearExpiredSession();
      return;
    }

    // Opcional: Advertir cuando el token expirará pronto (5 minutos)
    if (JwtHelper.willExpireSoon(currentUser.token, 5)) {
      console.log('⚠️ Token expirará en menos de 5 minutos');
      // Aquí podrías mostrar una notificación al usuario
    }
  }

  /**
   * Limpia la sesión expirada
   */
  private clearExpiredSession(): void {
    if (this.isBrowser) {
      localStorage.removeItem('currentUser');
    }
    this.currentUserSubject.next(null);
  }

  registro(usuario: Usuario): Observable<any> {
    return this.http.post(`${this.apiUrl}/registro`, usuario);
  }

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => {
        if (response && response.token) {
          // Verificar que el token recibido no esté expirado
          if (!JwtHelper.isTokenExpired(response.token)) {
            if (this.isBrowser) {
              localStorage.setItem('currentUser', JSON.stringify(response));
            }
            this.currentUserSubject.next(response);
            console.log('✅ Login exitoso, token válido almacenado');
          } else {
            console.error('❌ Token expirado recibido del servidor');
            throw new Error('Token expirado recibido del servidor');
          }
        }
      })
    );
  }

  logout(): void {
    if (this.isBrowser) {
      localStorage.removeItem('currentUser');
    }
    this.currentUserSubject.next(null);

    // Limpiar el intervalo de verificación
    if (this.tokenCheckInterval) {
      this.tokenCheckInterval.unsubscribe();
    }
  }

  obtenerUsuario(id: number): Observable<Usuario> {
    return this.http.get<Usuario>(`${this.apiUrl}/${id}`);
  }

  actualizarPerfil(id: number, usuario: Usuario): Observable<Usuario> {
    return this.http.put<Usuario>(`${this.apiUrl}/${id}`, usuario);
  }

  getCurrentUser(): LoginResponse | null {
    return this.currentUserSubject.value;
  }

  getToken(): string | null {
    const currentUser = this.getCurrentUser();
    if (currentUser && currentUser.token && !JwtHelper.isTokenExpired(currentUser.token)) {
      return currentUser.token;
    }

    // Si el token está expirado, limpiar sesión
    if (currentUser && currentUser.token && JwtHelper.isTokenExpired(currentUser.token)) {
      this.clearExpiredSession();
    }

    return null;
  }

  /**
   * Método unificado y robusto para verificar autenticación
   */
  isLoggedIn(): boolean {
    // 1. Verificar el usuario en memoria
    const currentUser = this.currentUserSubject.value;
    if (currentUser && currentUser.token && !JwtHelper.isTokenExpired(currentUser.token)) {
      return true;
    }

    // 2. Si no hay usuario en memoria o el token está expirado, verificar localStorage
    if (this.isBrowser) {
      const storedUser = localStorage.getItem('currentUser');
      if (storedUser) {
        try {
          const parsedUser = JSON.parse(storedUser);
          if (parsedUser && parsedUser.token) {
            // Verificar expiración
            if (!JwtHelper.isTokenExpired(parsedUser.token)) {
              // Token válido, sincronizar con memoria
              this.currentUserSubject.next(parsedUser);
              return true;
            } else {
              // Token expirado, limpiar
              console.log('🔒 Token expirado encontrado, limpiando...');
              this.clearExpiredSession();
            }
          }
        } catch (error) {
          console.error('❌ Error al parsear usuario almacenado:', error);
          this.clearExpiredSession();
        }
      }
    }

    return false;
  }

  /**
   * Verifica si el usuario está autenticado con token válido (método simplificado para guards)
   */
  isAuthenticated(): boolean {
    return this.isLoggedIn();
  }

  /**
   * Obtiene información del token actual
   */
  getTokenInfo(): any {
    const token = this.getToken();
    if (!token) return null;

    return {
      payload: JwtHelper.decodeToken(token),
      remainingTime: JwtHelper.getTokenRemainingTime(token),
      willExpireSoon: JwtHelper.willExpireSoon(token)
    };
  }

  ngOnDestroy(): void {
    if (this.tokenCheckInterval) {
      this.tokenCheckInterval.unsubscribe();
    }
  }
}
