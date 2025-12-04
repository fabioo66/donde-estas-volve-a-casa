import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
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
      map(mascotas => mascotas.map(m => ({
        ...m,
        tamanio: m.tamaño || m.tamanio // Mapear tamaño a tamanio
      })))
    );
  }

  obtenerMascota(id: number): Observable<Mascota> {
    return this.http.get<Mascota>(`${this.apiUrl}/${id}`);
  }

  obtenerMascotasUsuario(usuarioId: number): Observable<Mascota[]> {
    return this.http.get<Mascota[]>(`${this.apiUrl}/usuario/${usuarioId}`);
  }
}

