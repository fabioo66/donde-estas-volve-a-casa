package ttps.spring.models.raza;

import jakarta.persistence.*;
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
    private int id;

    @Setter
    private String nombre;

    @ManyToOne
    private Tipo_mascota tipo_mascota;
}
