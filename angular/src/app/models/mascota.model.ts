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
  fotos?: string[]; // Array de URLs base64 o URLs de imágenes
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

