package ttps.spring.models;


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
