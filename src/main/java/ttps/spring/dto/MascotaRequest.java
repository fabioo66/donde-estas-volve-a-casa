package ttps.spring.dto;

import java.time.LocalDate;
import java.util.List;

public class MascotaRequest {
    private String nombre;
    private String tamanio; // PEQUENIO, MEDIANO, GRANDE
    private String color;
    private LocalDate fecha;
    private String descripcion;
    private String estado; // PERDIDO_PROPIO, PERDIDO_AJENO, ADOPTADO, RECUPERADO
    private List<String> fotosBase64;
    private String coordenadas;
    private String tipo;
    private String raza;

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getTamanio() { return tamanio; }
    public void setTamanio(String tamanio) { this.tamanio = tamanio; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public List<String> getFotosBase64() { return fotosBase64; }
    public void setFotosBase64(List<String> fotosBase64) { this.fotosBase64 = fotosBase64; }
    public String getCoordenadas() { return coordenadas; }
    public void setCoordenadas(String coordenadas) { this.coordenadas = coordenadas; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getRaza() { return raza; }
    public void setRaza(String raza) { this.raza = raza; }
}
