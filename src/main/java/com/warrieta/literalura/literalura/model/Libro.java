package com.warrieta.literalura.literalura.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@Entity
@Table(name = "libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    @ManyToOne
    private Autor autor;

    @Enumerated(EnumType.STRING)
    private Idioma idioma;
    private Double numeroDeDescargas;

    public Libro(DatosLibro datosLibro) {
        this.titulo = datosLibro.titulo();
        this.idioma = Idioma.fromString(datosLibro.idiomas().stream()
                .limit(1)
                .collect(Collectors.joining()));
        this.numeroDeDescargas = datosLibro.numeroDeDescargas();
    }
    public Libro() {
    }
    @Override
    public String toString() {
        return titulo;
    }
}