package ttps.spring.infra;

public class UsuarioYaRegistradoException extends RuntimeException {
        public UsuarioYaRegistradoException() {
            super("El nombre de usuario ya esta en uso");
        }
        public UsuarioYaRegistradoException(String message) {
            super(message);
        }
}
