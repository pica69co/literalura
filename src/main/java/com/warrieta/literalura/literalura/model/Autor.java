package com.warrieta.literalura.literalura.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "autores")
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private Integer fechaDeNacimiento;
    private Integer fechaDeFallecimiento;
    @OneToMany(mappedBy = "autor",cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Libro> libros = new ArrayList<>();

    public Autor(DatosAutor datosAutor) {
        this.nombre = datosAutor.nombre();
        this.fechaDeNacimiento = datosAutor.fechaDeNacimiento();
        this.fechaDeFallecimiento = datosAutor.fechaDeFallecimiento();
    }

    public Autor() {
    }
}