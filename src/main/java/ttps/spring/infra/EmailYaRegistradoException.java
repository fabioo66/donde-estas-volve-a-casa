package ttps.spring.infra;

public class EmailYaRegistradoException extends RuntimeException {

    public EmailYaRegistradoException() {
        super("El email ya está registrado");
    }

    public EmailYaRegistradoException(String mensaje) {
        super(mensaje);
    }
}
