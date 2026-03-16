package ttps.spring.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ttps.spring.models.Avistamiento;

import java.time.LocalDate;

@Schema(description = "Respuesta con datos completos de un avistamiento")
public class AvistamientoResponse {

    @Schema(description = "ID del avistamiento", example = "1")
    private int id;

    @Schema(description = "Coordenadas del avistamiento", example = "-31.4201,-64.1888")
    private String coordenada;

    @Schema(description = "Descripción del avistamiento")
    private String descripcion;

    @Schema(description = "Fecha del avistamiento")
    private LocalDate fecha;

    @Schema(description = "URLs de las fotos en formato JSON")
    private String fotos;

    @Schema(description = "Estado activo del avistamiento")
    private boolean activo;

    @Schema(description = "Información de la mascota avistada")
    private MascotaInfo mascota;

    @Schema(description = "Información del usuario que reportó")
    private UsuarioInfo usuario;

    public AvistamientoResponse() {}

    public AvistamientoResponse(Avistamiento avistamiento) {
        this.id = avistamiento.getId();
        this.coordenada = avistamiento.getCoordenada();
        this.descripcion = avistamiento.getDescripcion();
        this.fecha = avistamiento.getFecha();
        this.fotos = avistamiento.getFotos();
        this.activo = avistamiento.isActivo();
        
        if (avistamiento.getMascota() != null) {
            this.mascota = new MascotaInfo(
                avistamiento.getMascota().getId(),
                avistamiento.getMascota().getNombre(),
                avistamiento.getMascota().getTipo(),
                avistamiento.getMascota().getRaza(),
                avistamiento.getMascota().getColor(),
                avistamiento.getMascota().getTamanio() != null ? avistamiento.getMascota().getTamanio().name() : null,
                avistamiento.getMascota().getFotos()
            );
        }
        
        if (avistamiento.getUsuario() != null) {
            this.usuario = new UsuarioInfo(
                (int) avistamiento.getUsuario().getId(),
                avistamiento.getUsuario().getNombre(),
                avistamiento.getUsuario().getEmail()
            );
        }
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCoordenada() {
        return coordenada;
    }

    public void setCoordenada(String coordenada) {
        this.coordenada = coordenada;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getFotos() {
        return fotos;
    }

    public void setFotos(String fotos) {
        this.fotos = fotos;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public MascotaInfo getMascota() {
        return mascota;
    }

    public void setMascota(MascotaInfo mascota) {
        this.mascota = mascota;
    }

    public UsuarioInfo getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioInfo usuario) {
        this.usuario = usuario;
    }

    // Clases internas para información simplificada
    @Schema(description = "Información básica de la mascota")
    public static class MascotaInfo {
        @Schema(description = "ID de la mascota")
        private int id;
        
        @Schema(description = "Nombre de la mascota")
        private String nombre;
        
        @Schema(description = "Tipo de mascota")
        private String tipo;
        
        @Schema(description = "Raza de la mascota")
        private String raza;
        
        @Schema(description = "Color de la mascota")
        private String color;
        
        @Schema(description = "Tamaño de la mascota")
        private String tamanio;

        @Schema(description = "Fotos de la mascota en formato JSON")
        private String fotos;

        public MascotaInfo() {}

        public MascotaInfo(int id, String nombre, String tipo, String raza, String color, String tamanio, String fotos) {
            this.id = id;
            this.nombre = nombre;
            this.tipo = tipo;
            this.raza = raza;
            this.color = color;
            this.tamanio = tamanio;
            this.fotos = fotos;
        }

        // Getters y Setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        public String getRaza() {
            return raza;
        }

        public void setRaza(String raza) {
            this.raza = raza;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getTamanio() {
            return tamanio;
        }

        public void setTamanio(String tamanio) {
            this.tamanio = tamanio;
        }

        public String getFotos() {
            return fotos;
        }

        public void setFotos(String fotos) {
            this.fotos = fotos;
        }
    }

    @Schema(description = "Información básica del usuario")
    public static class UsuarioInfo {
        @Schema(description = "ID del usuario")
        private int id;
        
        @Schema(description = "Nombre del usuario")
        private String nombre;
        
        @Schema(description = "Email del usuario")
        private String email;

        public UsuarioInfo() {}

        public UsuarioInfo(int id, String nombre, String email) {
            this.id = id;
            this.nombre = nombre;
            this.email = email;
        }

        // Getters y Setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
