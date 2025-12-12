import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, throwError, timeout } from 'rxjs';
import { Avistamiento } from '../models/avistamiento.model';

@Injectable({
  providedIn: 'root'
})
export class AvistamientoService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/avistamientos';

  obtenerTodosLosAvistamientos(): Observable<Avistamiento[]> {
    return this.http.get<Avistamiento[]>(this.apiUrl).pipe(
      timeout(10000), // 10 segundos timeout
      catchError(error => {
        console.error('Error en servicio de avistamientos:', error);
        return throwError(() => error);
      })
    );
  }

  obtenerAvistamiento(id: number): Observable<Avistamiento> {
    return this.http.get<Avistamiento>(`${this.apiUrl}/${id}`);
  }

  obtenerAvistamientosPorMascota(mascotaId: number): Observable<Avistamiento[]> {
    return this.http.get<Avistamiento[]>(`${this.apiUrl}/mascota/${mascotaId}`);
  }
}

