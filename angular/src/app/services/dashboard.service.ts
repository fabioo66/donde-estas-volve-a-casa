import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, forkJoin, map, catchError } from 'rxjs';

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
    console.log('🔄 Obteniendo estadísticas del dashboard...');

    // Por ahora usar datos básicos para evitar problemas de loading
    // TODO: Implementar llamadas reales cuando se solucione el problema de autenticación
    const statsBasicas: DashboardStats = {
      mascotasPerdidas: 2,
      recuperadas: 0,
      adoptadas: 0,
      seguimientosPendientes: 0
    };

    console.log('✅ Estadísticas retornadas:', statsBasicas);
    return of(statsBasicas);
  }

  obtenerResumenUsuario(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/dashboard/resumen`);
  }

  obtenerActividadReciente(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/dashboard/actividad`);
  }
}
