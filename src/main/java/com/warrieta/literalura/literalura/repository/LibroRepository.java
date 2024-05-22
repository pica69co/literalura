package com.warrieta.literalura.literalura.repository;

import com.warrieta.literalura.literalura.model.Idioma;
import com.warrieta.literalura.literalura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {
    Optional<Libro> findByTitulo(String titulo);

    @Query("SELECT l FROM Libro l WHERE l.idioma = :idiomaSeleccionado")
    List<Libro> listarLibrosPorIdioma(Idioma idiomaSeleccionado);

    @Query("SELECT l FROM Libro l ORDER BY l.numeroDeDescargas DESC LIMIT 10")
    List<Libro> top10LibrosBD();
}
