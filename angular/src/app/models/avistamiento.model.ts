export interface Avistamiento {
  id: number;
  coordenada: string;
  descripcion: string;
  fecha: string;
  fotos?: string; // JSON string con array de URLs
  activo: boolean;
  mascota: {
    id: number;
    nombre: string;
    tipo: string;
    raza: string;
    color: string;
    tamanio: string;
    fotos?: string; // JSON string con array de URLs de las fotos de la mascota
  };
  usuario: {
    id: number;
    nombre: string;
    email: string;
  };
}
