import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, throwError, timeout } from 'rxjs';
import { map } from 'rxjs/operators';
import { Mascota } from '../models/mascota.model';

export interface MascotaRequest {
  nombre: string;
  tamanio: string; // Sin ñ para evitar errores de TypeScript
  color: string;
  fecha: string;
  descripcion: string;
  estado: string;
  coordenadas?: string;
  tipo: string;
  raza: string;
  fotosBase64?: string[];
}

@Injectable({
  providedIn: 'root'
})
export class MascotaService {
  private apiUrl = 'http://localhost:8080/mascotas';

  constructor(private http: HttpClient) { }

  obtenerMascotasPerdidas(): Observable<Mascota[]> {
    return this.http.get<any[]>(`${this.apiUrl}/perdidas`).pipe(
      timeout(10000), // 10 segundos timeout
      map(mascotas => mascotas.map(m => ({
        ...m,
        tamanio: m.tamanio || m.tamano // El backend devuelve "tamanio" sin ñ
      }))),
      catchError(error => {
        console.error('Error en servicio de mascotas:', error);
        return throwError(() => error);
      })
    );
  }

  obtenerMascota(id: number): Observable<Mascota> {
    console.log(`🔍 Intentando obtener mascota con ID: ${id}`);
    return this.http.get<any>(`${this.apiUrl}/${id}`).pipe(
      timeout(5000), // Reducido a 5 segundos
      map(mascota => {
        console.log('✅ Mascota obtenida del backend:', mascota);
        return {
          ...mascota,
          tamanio: mascota.tamanio || mascota.tamano
        };
      }),
      catchError(error => {
        console.error('❌ Error detallado en obtenerMascota:', error);
        if (error.name === 'TimeoutError') {
          console.error('❌ Timeout: El backend no responde');
          return throwError(() => ({
            message: 'El servidor no responde. Verifica que esté ejecutándose.',
            status: 'TIMEOUT',
            originalError: error
          }));
        }
        return throwError(() => error);
      })
    );
  }

  obtenerMascotasUsuario(usuarioId: number): Observable<Mascota[]> {
    return this.http.get<any[]>(`${this.apiUrl}/usuario/${usuarioId}`).pipe(
      timeout(10000),
      map(mascotas => mascotas.map(m => ({
        ...m,
        tamanio: m.tamanio || m.tamano
      }))),
      catchError(error => {
        console.error('Error en servicio de mascotas:', error);
        return throwError(() => error);
      })
    );
  }

  crearMascota(usuarioId: number, mascota: MascotaRequest): Observable<Mascota> {
    return this.http.post<any>(`${this.apiUrl}/usuario/${usuarioId}`, mascota).pipe(
      map(m => ({
        ...m,
        tamanio: m.tamanio || m.tamano
      }))
    );
  }

  actualizarMascota(id: number, mascota: MascotaRequest): Observable<Mascota> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, mascota).pipe(
      map(m => ({
        ...m,
        tamanio: m.tamanio || m.tamano
      }))
    );
  }

  eliminarMascota(id: number): Observable<Mascota> {
    return this.http.delete<any>(`${this.apiUrl}/${id}`).pipe(
      timeout(10000),
      map(mascota => ({
        ...mascota,
        tamanio: mascota.tamanio || mascota.tamano
      })),
      catchError(error => {
        console.error('Error al eliminar mascota:', error);
        return throwError(() => error);
      })
    );
  }

  // Utility para convertir archivo a base64
  convertirArchivoABase64(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = () => {
        const base64 = reader.result as string;
        // Remover el prefijo data:image/...;base64,
        const base64Data = base64.split(',')[1];
        resolve(base64Data);
      };
      reader.onerror = reject;
      reader.readAsDataURL(file);
    });
  }
}

