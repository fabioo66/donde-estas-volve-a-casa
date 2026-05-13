import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, map, of, tap } from 'rxjs';
import { UbicacionResponse } from '../models/usuario.model';

@Injectable({
  providedIn: 'root'
})
export class GeolocalizacionService {
  private apiUrl = 'https://apis.datos.gob.ar/georef/api/ubicacion';
  private cacheUbicaciones = new Map<string, string>();

  constructor(private http: HttpClient) {}

  obtenerUbicacion(lat: number, lon: number): Observable<UbicacionResponse> {
    return this.http.get<UbicacionResponse>(`${this.apiUrl}?lat=${lat}&lon=${lon}`);
  }

  obtenerUbicacionDesdeCoordenadas(coordenadas: string): Observable<string> {
    const coords = this.parsearCoordenadas(coordenadas);
    if (!coords) {
      return of('Ubicación no disponible');
    }

    const cacheKey = `${coords.lat},${coords.lon}`;
    const cached = this.cacheUbicaciones.get(cacheKey);
    if (cached) {
      return of(cached);
    }

    return this.obtenerUbicacion(coords.lat, coords.lon).pipe(
      map(response => this.formatearUbicacion(response)),
      tap(ubicacion => this.cacheUbicaciones.set(cacheKey, ubicacion)),
      catchError(() => of('Ubicación no disponible'))
    );
  }

  private parsearCoordenadas(coordenadas: string): { lat: number; lon: number } | null {
    if (!coordenadas) return null;

    const partes = coordenadas.split(',');
    if (partes.length !== 2) return null;

    const lat = parseFloat(partes[0].trim());
    const lon = parseFloat(partes[1].trim());

    if (Number.isNaN(lat) || Number.isNaN(lon)) return null;

    return { lat, lon };
  }

  private formatearUbicacion(response: UbicacionResponse): string {
    const partes = [
      response?.ubicacion?.municipio?.nombre,
      response?.ubicacion?.provincia?.nombre,
      response?.ubicacion?.departamento?.nombre
    ].filter((parte): parte is string => !!parte);

    return partes.length > 0 ? partes.join(', ') : 'Ubicación no disponible';
  }

  obtenerPosicionActual(): Promise<GeolocationPosition> {
    return new Promise((resolve, reject) => {
      if (!navigator.geolocation) {
        reject(new Error('Geolocalización no soportada por el navegador'));
      } else {
        navigator.geolocation.getCurrentPosition(resolve, reject);
      }
    });
  }
}

