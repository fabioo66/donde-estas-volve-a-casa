import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
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
      map(mascotas => mascotas.map(m => ({
        ...m,
        tamanio: m.tamaño || m.tamanio // Mapear tamaño del backend a tamano del frontend
      })))
    );
  }

  obtenerMascota(id: number): Observable<Mascota> {
    return this.http.get<any>(`${this.apiUrl}/${id}`).pipe(
      map(m => ({
        ...m,
        tamanio: m.tamaño || m.tamanio
      }))
    );
  }

  obtenerMascotasUsuario(usuarioId: number): Observable<Mascota[]> {
    return this.http.get<any[]>(`${this.apiUrl}/usuario/${usuarioId}`).pipe(
      map(mascotas => mascotas.map(m => ({
        ...m,
        tamanio: m.tamaño || m.tamanio
      })))
    );
  }

  crearMascota(usuarioId: number, mascota: MascotaRequest): Observable<Mascota> {
    // Mapear tamano (frontend) a tamaño (backend)
    const mascotaBackend = {
      ...mascota,
      tamaño: mascota.tamanio
    };
    // Remover la propiedad tamano para evitar conflictos
    delete (mascotaBackend as any).tamanio;

    return this.http.post<any>(`${this.apiUrl}/usuario/${usuarioId}`, mascotaBackend).pipe(
      map(m => ({
        ...m,
        tamanio: m.tamaño || m.tamanio
      }))
    );
  }

  actualizarMascota(id: number, mascota: MascotaRequest): Observable<Mascota> {
    // Mapear tamano (frontend) a tamaño (backend)
    const mascotaBackend = {
      ...mascota,
      tamaño: mascota.tamanio
    };
    // Remover la propiedad tamano para evitar conflictos
    delete (mascotaBackend as any).tamanio;

    return this.http.put<any>(`${this.apiUrl}/${id}`, mascotaBackend).pipe(
      map(m => ({
        ...m,
        tamanio: m.tamaño || m.tamanio
      }))
    );
  }

  eliminarMascota(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
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

