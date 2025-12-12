import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, throwError, timeout } from 'rxjs';
import { map } from 'rxjs/operators';
import { Mascota } from '../models/mascota.model';

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
        tamanio: m.tamaño || m.tamanio // Mapear tamaño a tamanio
      }))),
      catchError(error => {
        console.error('Error en servicio de mascotas:', error);
        return throwError(() => error);
      })
    );
  }

  obtenerMascota(id: number): Observable<Mascota> {
    return this.http.get<any>(`${this.apiUrl}/${id}`).pipe(
      timeout(10000),
      map(mascota => ({
        ...mascota,
        tamanio: mascota.tamaño || mascota.tamanio
      })),
      catchError(error => {
        console.error('Error en servicio de mascotas:', error);
        return throwError(() => error);
      })
    );
  }

  obtenerMascotasUsuario(usuarioId: number): Observable<Mascota[]> {
    return this.http.get<Mascota[]>(`${this.apiUrl}/usuario/${usuarioId}`).pipe(
      timeout(10000),
      catchError(error => {
        console.error('Error en servicio de mascotas:', error);
        return throwError(() => error);
      })
    );
  }
}

