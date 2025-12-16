package ttps.spring.dto;

public class LoginResponse {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String tipo; // "REGISTRADO" o "ADMINISTRADOR"
    private String token; // JWT token

    public LoginResponse(Long id, String nombre, String apellido, String email, String tipo, String token) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.tipo = tipo;
        this.token = token;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getToken() { return token; }
}
