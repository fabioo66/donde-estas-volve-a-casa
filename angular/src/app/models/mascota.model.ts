export interface Mascota {
  id: number;
  nombre: string;
  tamanio: string; // Sin ñ para evitar errores de TypeScript
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
  PERDIDO_PROPIO = 'PERDIDO_PROPIO',
  PERDIDO_AJENO = 'PERDIDO_AJENO',
  ADOPTADO = 'ADOPTADO',
  RECUPERADO = 'RECUPERADO'
}

export enum Tamanio {
  PEQUENIO = 'PEQUENIO',
  MEDIANO = 'MEDIANO',
  GRANDE = 'GRANDE'
}

