import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, catchError } from 'rxjs';

export interface DashboardStats {
  mascotasPerdidas: number;
  recuperadas: number;
  adoptadas: number;
  seguimientosPendientes: number;
}

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private apiUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  obtenerEstadisticas(): Observable<DashboardStats> {
    console.log('🔄 Obteniendo estadísticas del dashboard desde API...');

    return this.http.get<DashboardStats>(`${this.apiUrl}/dashboard/estadisticas`).pipe(
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
