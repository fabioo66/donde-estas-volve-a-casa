export interface JwtPayload {
  userId: number;
  email: string;
  tipo: string;
  exp: number;
  iat: number;
  sub: string;
}

export class JwtHelper {
  /**
   * Decodifica un token JWT sin validar la firma
   */
  static decodeToken(token: string): JwtPayload | null {
    try {
      const parts = token.split('.');
      if (parts.length !== 3) {
        return null;
      }

      const payload = parts[1];
      const decoded = atob(payload);
      return JSON.parse(decoded) as JwtPayload;
    } catch (error) {
      console.error('Error decodificando JWT:', error);
      return null;
    }
  }

  /**
   * Verifica si un token está expirado
   */
  static isTokenExpired(token: string): boolean {
    const payload = this.decodeToken(token);
    if (!payload || !payload.exp) {
      return true;
    }

    // exp está en segundos, Date.now() está en milisegundos
    const currentTime = Math.floor(Date.now() / 1000);
    return payload.exp < currentTime;
  }

  /**
   * Obtiene el tiempo restante del token en milisegundos
   */
  static getTokenRemainingTime(token: string): number {
    const payload = this.decodeToken(token);
    if (!payload || !payload.exp) {
      return 0;
    }

    const currentTime = Math.floor(Date.now() / 1000);
    const remainingSeconds = payload.exp - currentTime;
    return Math.max(0, remainingSeconds * 1000);
  }

  /**
   * Verifica si el token expirará en los próximos minutos especificados
   */
  static willExpireSoon(token: string, minutesBeforeExpiry: number = 5): boolean {
    const remainingTime = this.getTokenRemainingTime(token);
    return remainingTime <= (minutesBeforeExpiry * 60 * 1000);
  }
}
