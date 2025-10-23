package ttps.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.LinkedList;
import java.util.List;


public class Administrador extends Usuario {

    public Administrador() {
        super();
    }

    public Administrador(String nombre, String apellido, String email, String contrasenia, String telefono, String barrio, String ciudad) {
        super(nombre, apellido, email, contrasenia, telefono, barrio, ciudad);
    }

}

