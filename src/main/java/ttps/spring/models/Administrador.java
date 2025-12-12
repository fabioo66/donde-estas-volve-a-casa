package ttps.spring.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMINISTRADOR")
public class Administrador extends Usuario {

    public Administrador() {
        super();
    }

    public Administrador(String nombre, String apellido, String email, String contrasenia, String telefono, String provincia, String municipio, String departamento) {
        super(nombre, apellido, email, contrasenia, telefono, provincia, municipio, departamento);
    }
}
