package ttps.spring.infra;

public class CredencialesErroneasException extends RuntimeException {
        public CredencialesErroneasException() {
            super("Credenciales incorrectas");
        }
        public CredencialesErroneasException(String mensaje) {
            super(mensaje);
        }
}
