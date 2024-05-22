package com.warrieta.literalura.literalura.service;
import com.warrieta.literalura.literalura.model.*;
import com.warrieta.literalura.literalura.repository.AutorRepository;
import com.warrieta.literalura.literalura.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Optional;

@Service
public class LibroService {
    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private AutorRepository autorRepository;
    public Libro guardarLibroConAutor(DatosLibro datosLibro) {
        // Guardar el autor y devolver la entidad persistida
        Autor autor = guardarAutor(datosLibro);

        // Crear el libro y asignar el autor
        Libro libro = new Libro(datosLibro);
        libro.setAutor(autor);

        // Guardar el libro y devolver la entidad persistida
        return libroRepository.save(libro);
    }


    public Autor guardarAutor(DatosLibro datosLibro) {
        // Asumimos que solo hay un autor
        DatosAutor datosAutor = datosLibro.autor().get(0);

        // Verificar si el autor ya existe en la base de datos
        Optional<Autor> autorExistente = verificarAutorExistenteEnBD(datosAutor.nombre());
        if (autorExistente.isPresent()) {
            // Si el autor ya está en la base de datos, simplemente lo obtenemos
            return autorExistente.get();
        } else {
            // Si el autor no está en la base de datos, lo creamos
            Autor autor = new Autor(datosAutor);
            return autorRepository.save(autor);
        }
    }

    public Optional<Libro> verificarLibroExistenteEnBD(String titulo){
        return libroRepository.findByTitulo(titulo);
    }

    public Optional<Autor> verificarAutorExistenteEnBD(String nombre){
        return autorRepository.findByNombre(nombre);
    }

    public List<Libro> listarLibrosRegistrados() {
        return libroRepository.findAll();
    }

    public List<Autor> listarAutoresRegistrados() {
        return autorRepository.findAll();
    }

    public List<Autor> listarAutoresPorAño(Integer añoIngresado) {
        return autorRepository.listarAutoresPorAño(añoIngresado);
    }

    public List<Libro> listarLibrosPorIdioma(Idioma idiomaSeleccionado) {
        return libroRepository.listarLibrosPorIdioma(idiomaSeleccionado);
    }

    public DoubleSummaryStatistics obtenerEstadisticasDeDescargas() {
        // Obtener todos los libros de la base de datos
        List<Libro> libros = libroRepository.findAll();
        if (libros.isEmpty()) {
            // No hay libros en la base de datos
            System.out.println("No hay registros de libros en la base de datos.");
            return new DoubleSummaryStatistics();
        }else {
            return libros.stream()
                    .filter(libro -> libro.getNumeroDeDescargas() > 0.0)
                    .mapToDouble(Libro::getNumeroDeDescargas)
                    .summaryStatistics();
        }

    }
    public List<Libro> top10LibrosBD() {
        return libroRepository.top10LibrosBD();
    }

    public Optional<Autor> buscarAutorPorNombre(String nombre){
        return autorRepository.buscarAutorPorNombre(nombre);
    }

    public List<Autor> buscarAutoresPorNacimiento(Integer fechaIngresada){
        return autorRepository.buscarAutoresPorNacimiento(fechaIngresada);
    }

    public List<Autor> buscarAutoresPorFallecimiento(Integer fechaIngresada){
        return autorRepository.buscarAutoresPorFallecimiento(fechaIngresada);
    }

    public List<Autor> listarAutoresFallecidos(Integer fechaIngresada){
        return autorRepository.listarAutoresFallecidos(fechaIngresada);
    }
}
