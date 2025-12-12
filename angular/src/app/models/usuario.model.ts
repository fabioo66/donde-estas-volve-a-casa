export interface Usuario {
  id?: number;
  nombreUsuario: string;
  nombre: string;
  apellido: string;
  email: string;
  password?: string;
  telefono: string;
  genero: string;
  edad: number;
  provincia: string;
  municipio: string;
  departamento: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  id: number;
  nombre: string;
  apellido: string;
  email: string;
  tipo: string;
  token: string; // JWT token
}

export interface UbicacionResponse {
  ubicacion: {
    departamento: {
      id: string;
      nombre: string;
    };
    lat: number;
    lon: number;
    municipio: {
      id: string;
      nombre: string;
    };
    provincia: {
      id: string;
      nombre: string;
    };
  };
}
