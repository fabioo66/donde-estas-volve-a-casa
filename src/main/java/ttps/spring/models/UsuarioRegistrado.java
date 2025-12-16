package ttps.spring.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("REGISTRADO")
public class UsuarioRegistrado extends Usuario{

    // Constructor vacío
    public UsuarioRegistrado() {
        super();
    }

    public UsuarioRegistrado(String nombreUsuario, String nombre, String apellido, String email, String contrasenia,
                             String telefono, String genero, Integer edad, String provincia, String municipio, String departamento) {
        super(nombreUsuario, nombre, apellido, email, contrasenia, telefono, genero, edad, provincia, municipio, departamento);
    }
}
