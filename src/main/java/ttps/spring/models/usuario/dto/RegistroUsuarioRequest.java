package ttps.spring.models.usuario.dto;

public record RegistroUsuarioRequest(
        String nombreUsuario,
        String nombre,
        String apellido,
        String email,
        String password,
        String telefono,
        String genero,
        Integer edad,
        String provincia,
        String municipio,
        String departamento
) {}
