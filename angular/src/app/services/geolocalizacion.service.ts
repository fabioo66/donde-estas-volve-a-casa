import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UbicacionResponse } from '../models/usuario.model';

@Injectable({
  providedIn: 'root'
})
export class GeolocalizacionService {
  private apiUrl = 'https://apis.datos.gob.ar/georef/api/ubicacion';

  constructor(private http: HttpClient) {}

  obtenerUbicacion(lat: number, lon: number): Observable<UbicacionResponse> {
    return this.http.get<UbicacionResponse>(`${this.apiUrl}?lat=${lat}&lon=${lon}`);
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

