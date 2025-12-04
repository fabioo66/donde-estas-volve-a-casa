export interface Mascota {
  id: number;
  nombre: string;
  tamanio: string;
  color: string;
  fecha: string;
  descripcion: string;
  estado: string;
  coordenadas: string;
  tipo: string;
  raza: string;
  activo: boolean;
  fotos?: string; // JSON string con array de URLs: "['/uploads/foto1.jpg', '/uploads/foto2.jpg']"
}

export enum Estado {
  PERDIDO = 'PERDIDO',
  ENCONTRADO = 'ENCONTRADO',
  RECUPERADO = 'RECUPERADO'
}

export enum Tamanio {
  PEQUENIO = 'PEQUENIO',
  MEDIANO = 'MEDIANO',
  GRANDE = 'GRANDE'
}

