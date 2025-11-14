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

    public UsuarioRegistrado(String nombre, String apellido, String email, String contrasenia,
                             String telefono, String barrio, String ciudad) {
        super(nombre, apellido, email, contrasenia, telefono, barrio, ciudad);
    }
}
