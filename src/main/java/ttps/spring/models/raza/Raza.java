package ttps.spring.models.raza;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ttps.spring.models.tipo_mascota.Tipo_mascota;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "raza")
public class Raza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String nombre;

    // Nombre normalizado para búsquedas y unicidad (UPPER, sin diacríticos)
    @Setter
    private String nombreNormalizado;

    @ManyToOne
    private Tipo_mascota tipo_mascota;

    public Raza(@NotNull String nombre, String nombreNormalizado, Tipo_mascota tipo_mascota) {
        this.nombre = nombre;
        this.nombreNormalizado = nombreNormalizado;
        this.tipo_mascota = tipo_mascota;
    }

    // helpers para integración con servicios que usan tipo id directamente
    public void setTipoMascotaId(Long tipoId) {
        if (this.tipo_mascota == null) this.tipo_mascota = new Tipo_mascota();
        this.tipo_mascota.setId(tipoId);
    }

    public Long getTipoMascotaId() {
        return this.tipo_mascota != null ? this.tipo_mascota.getId() : null;
    }
}
