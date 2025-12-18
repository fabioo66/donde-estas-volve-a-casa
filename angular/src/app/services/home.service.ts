import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, catchError } from 'rxjs';

export interface HomeStats {
  mascotasPerdidas: number;
  recuperadas: number;
  adoptadas: number;
  seguimientosPendientes: number;
}

@Injectable({
  providedIn: 'root'
})
export class HomeService {
  private apiUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  obtenerEstadisticas(): Observable<HomeStats> {
    console.log('🔄 Obteniendo estadísticas del home desde API...');

    return this.http.get<HomeStats>(`${this.apiUrl}/home/estadisticas`).pipe(
      map(response => {
        console.log('✅ Estadísticas recibidas de la API:', response);
        return response;
      }),
      catchError(error => {
        console.error('❌ Error al obtener estadísticas de la API:', error);
        throw error;
      })
    );
  }

}
