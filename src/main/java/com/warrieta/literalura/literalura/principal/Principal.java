package com.warrieta.literalura.literalura.principal;

import com.warrieta.literalura.literalura.model.*;
import com.warrieta.literalura.literalura.service.ConsumoAPI;
import com.warrieta.literalura.literalura.service.ConvierteDatos;
import com.warrieta.literalura.literalura.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class Principal {
    private static final String URL_BASE = "https://gutendex.com/books/";
    ConsumoAPI consumoAPI = new ConsumoAPI();
    ConvierteDatos conversor = new ConvierteDatos();
    Scanner teclado = new Scanner(System.in);

    @Autowired
    private LibroService service;


    public void muestraElMenu(){
        var opcion = -1;
        while(opcion!=0) {
            var menu = """
                -------------
                Elija La Opcion de Busqueda que Desea Realizar:
                1 - Buscar libro por titulo
                2 - Buscar libros registrados en BD
                3 - Buscar autores registrados en BD
                4 - Ver autores vivos en un año determinado
                5 - Buscar libros por idioma
                6 - Generar estadisticas
                7 - Top 10 libros
                8 - Buscar autor por nombre
                9 - Listar autores con otras consultas
                0 - Salir
                """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarLibroPorTitulo();
                    break;
                case 2:
                    listarLibrosRegistrados();
                    break;
                case 3:
                    listarAutoresRegistrados();
                    break;
                case 4:
                    listarAutoresPorAño();
                    break;
                case 5:
                    listarLibrosPorIdioma();
                    break;
                case 6:
                    generarEstadisticasDeLibrosBD();
                    break;
                case 7:
                    top10LibrosDB();
                    break;
                case 8:
                    buscarAutorPorNombre();
                    break;
                case 9:
                    listarAutoresPorOtrasConsultas();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación, Vuelva pronto!");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }
    }


    private Datos getDatosLibro() {
        System.out.println("Escribe el nombre del libro que deseas buscar");
        var tituloLibro = teclado.nextLine();
        var json = consumoAPI.obtenerDatos(URL_BASE+"?search="+tituloLibro.replace(" ","+"));
        Datos datos = conversor.obtenerDatos(json, Datos.class);
        return datos;
    }

    private void buscarLibroPorTitulo() {
        Datos datosEncontrados = getDatosLibro();
        Optional<DatosLibro> libroBuscado = datosEncontrados.resultados().stream()
                .findFirst();
        if(libroBuscado.isPresent()){
            System.out.println(
                    "\n----- LIBRO -----" +
                            "\nTitulo: " + libroBuscado.get().titulo() +
                            "\nAutor: " + libroBuscado.get().autor().stream()
                            .map(a -> a.nombre()).limit(1).collect(Collectors.joining())+
                            "\nIdioma: " + libroBuscado.get().idiomas() +
                            "\nNumero de descargas: " + libroBuscado.get().numeroDeDescargas() +
                            "\n-----------------\n"
            );
            try{
                DatosLibro libroEncontrado = libroBuscado.get();
                Optional<Libro> libroExistente = service.verificarLibroExistenteEnBD(libroEncontrado.titulo());


                if (libroExistente.isPresent()) {
                    System.out.println("El libro '" + libroEncontrado.titulo() + "' se encuentra en la base de datos.");
                }else{
                    Optional<Autor> autorExistente = service.verificarAutorExistenteEnBD(libroEncontrado.autor()
                            .stream()
                            .map(a -> a.nombre())
                            .collect(Collectors.joining()));
                    Autor autor= null;
                    if (autorExistente.isPresent()) {
                        System.out.println("El autor '" + autorExistente.get().getNombre() + "' se encuentra registrado en la base de datos.");
                }else {
                        autor = service.guardarAutor(libroEncontrado);
                    }
                    Libro libro = new Libro(libroEncontrado);
                    libro.setAutor(autor);
                    libro = service.guardarLibroConAutor(libroEncontrado);

                    System.out.println("Libro guardado: " + libro.getTitulo());
                }


            }catch (Exception e){
                System.out.println("Algo salio mal:" +e.getMessage());
            }

        }else {
            System.out.println("libro no encontrado, intenta con otro nombre");
        }

    }

    private void listarLibrosRegistrados() {
        List<Libro> librosRegistrados = service.listarLibrosRegistrados();
        librosRegistrados.forEach(l -> System.out.println(
                "----- LIBRO -----" +
                        "\nTitulo: " + l.getTitulo() +
                        "\nAutor: " + l.getAutor().getNombre() +
                        "\nIdioma: " + l.getIdioma().getIdiomaOmdb() +
                        "\nNumero de descargas: " + l.getNumeroDeDescargas() +
                        "\n-----------------\n"
        ));
    }

    private void listarAutoresRegistrados(){
        List<Autor> autoresRegistrados = service.listarAutoresRegistrados();
        if (autoresRegistrados.isEmpty()){
            System.out.println("Auno no hay autores registrados en la BD");
        }else {
            autoresRegistrados.forEach(a -> System.out.println(
                    "----- AUTORES -----" +
                            "\nNombre: " + a.getNombre() +
                            "\nFecha de nacimiento: " + a.getFechaDeNacimiento() +
                            "\nFecha de fallecimiento: " + a.getFechaDeFallecimiento() +
                            "\nLibros: " + a.getLibros() +
                            "\n-----------------\n"
            ));
        }
    }

    private void listarAutoresPorAño() {
        System.out.println("Ingrese el año vivo de autor(es) que desea buscar Ejemplo:1619");
        try {
            String entrada = teclado.nextLine();
            var añoIngresado = Integer.valueOf(entrada);
            List<Autor> autoresPorAño = service.listarAutoresPorAño(añoIngresado);
            if(!autoresPorAño.isEmpty()){
                autoresPorAño.forEach(a -> System.out.println(
                        "----- AUTOR -----" +
                                "\nNombre: " + a.getNombre() +
                                "\nFecha de nacimiento: " + a.getFechaDeNacimiento() +
                                "\nFecha de fallecimiento: " + a.getFechaDeFallecimiento() +
                                "\nLibros: " + a.getLibros() +
                                "\n-----------------\n"
                ));
            }else {
                System.out.println("No hay autores vivos en el año ingresado que se encuentren registrados en la BD");
            }
        }catch (NumberFormatException e){
            System.out.println("Introduce un año valido");
        }catch (InputMismatchException e){
            System.out.println("Favor de ingresar numeros de 4 cifras");
        }catch (Exception e){
            System.out.println("Ocurrio un error Inesperado"+e.getMessage() );
        }
    }

    private void listarLibrosPorIdioma(){
        var menuIdiomas = """
                Ingrese el idioma para buscar los libros:
                es - Español
                en - Inglés
                fr - Francés
                pt - Portugués
                """;
        System.out.println(menuIdiomas);
        var idiomaIngresado = teclado.nextLine();
        if(idiomaIngresado.equalsIgnoreCase("es") || idiomaIngresado.equalsIgnoreCase("en")
                || idiomaIngresado.equalsIgnoreCase("fr") || idiomaIngresado.equalsIgnoreCase("pt")){
            Idioma idiomaSeleccionado = Idioma.fromString(idiomaIngresado);
            List<Libro> librosPorIdioma = service.listarLibrosPorIdioma(idiomaSeleccionado);
            if(librosPorIdioma.isEmpty()){
                System.out.println("No hay libros registrados en ese idioma en la BD");
            }else {
                librosPorIdioma.forEach(l -> System.out.println(
                        "----- LIBRO -----" +
                                "\nTitulo: " + l.getTitulo() +
                                "\nAutor: " + l.getAutor().getNombre() +
                                "\nIdioma: " + l.getIdioma().getIdiomaOmdb() +
                                "\nNumero de descargas: " + l.getNumeroDeDescargas() +
                                "\n-----------------\n"
                ));
            }
        } else {
            System.out.println("Por favor selecciona un idioma valido");
        }
    }

    public void generarEstadisticasDeLibrosBD() {

        DoubleSummaryStatistics est = service.obtenerEstadisticasDeDescargas();
        System.out.println("Estadísticas de descargas de los libros de la BD");
        System.out.println("Cantidad Media de Descargas: " + est.getAverage());
        System.out.println("Cantidad Máxima de Descargas: " + est.getMax());
        System.out.println("Cantidad Mínima de Descargas: " + est.getMin());
        System.out.println("Cantidad de registros evaluados para cálculos estadísticos: " + est.getCount());
    }

    public void top10LibrosDB(){
        try {
            List<Libro> top10Libros = service.top10LibrosBD();
            System.out.println("Top 10 libros mas descargados");
            top10Libros.forEach(l -> System.out.println(
                    "----- LIBRO -----" +
                            "\nTitulo: " + l.getTitulo() +
                            "\nAutor: " + l.getAutor().getNombre() +
                            "\nIdioma: " + l.getIdioma().getIdiomaOmdb() +
                            "\nNumero de descargas: " + l.getNumeroDeDescargas() +
                            "\n-----------------\n"
            ));
        }catch (Exception e){
            System.out.println("Se produjo un error al obtener los datos de los 10 libros mas descargados");
        }
    }

    public void buscarAutorPorNombre(){
        System.out.println("Ingrese el nombre del autor que desea buscar en la BD");
        try {
            String nombreAutor = teclado.nextLine();
            Optional<Autor> autorBD = service.buscarAutorPorNombre(nombreAutor);
            if(autorBD.isPresent()){
                Autor autorEncontrado = autorBD.get();
                System.out.println(
                        "----- AUTOR ENCONTRADO -----" +
                                "\nNombre: " + autorEncontrado.getNombre() +
                                "\nFecha de nacimiento: " + autorEncontrado.getFechaDeNacimiento() +
                                "\nFecha de fallecimiento: " + autorEncontrado.getFechaDeFallecimiento() +
                                "\nLibros: " + autorEncontrado.getLibros() +
                                "\n-----------------\n"
                );
            }else {
                System.out.println("El autor no existe en la BD");
            }
        }catch (Exception e){
            System.out.println("Introduce un nombre valido");
        }
    }

    private void listarAutoresPorOtrasConsultas() {
        var otrasConsultas = """
                Ingresa la opcion por la cual desea listar los autores
                1.- Listar autores por Año de Nacimiento
                2.- Listar Autores por Año de Fallecimiento
                3.- Listar Autores ya Fallecidos
                """;
        System.out.println(otrasConsultas);
        try {
            String input = teclado.nextLine();
            var opcion = Integer.valueOf(input);
            switch (opcion){
                case 1:
                    listarAutoresPorNacimiento();
                    break;
                case 2:
                    listarAutoresPorFallecimiento();
                    break;
                case 3:
                    listarAutoresFallecidos();
                    break;
            }
        }catch (NumberFormatException e){
            System.out.println("Ingresa un numero de acuerdo a la opcion, no letras");
        }
    }

    private void listarAutoresFallecidos() {
        try{
            LocalDate fechaActual = LocalDate.now();
            Integer año = fechaActual.getYear();
            List<Autor> autoresYaFallecidos = service.listarAutoresFallecidos(año);
            if(!autoresYaFallecidos.isEmpty()){
                System.out.println("AUTORES YA FALLECIDOS");
                autoresYaFallecidos.forEach(a -> System.out.println(
                        "----- AUTOR -----" +
                                "\nNombre: " + a.getNombre() +
                                "\nFecha de nacimiento: " + a.getFechaDeNacimiento() +
                                "\nFecha de fallecimiento: " + a.getFechaDeFallecimiento() +
                                "\nLibros: " + a.getLibros() +
                                "\n-----------------\n"));
            }else {
                System.out.println("No existen autores que fallecieron en la BD");
            }
        }catch (Exception e){
            System.out.println("Ocurrio algun error inesperado"+e.getMessage());
        }
    }

    private void listarAutoresPorFallecimiento() {
        System.out.println("Introduce el año de fallecimiento que deseas buscar:");
        try {
            String input = teclado.nextLine();
            var añoFallecimiento = Integer.valueOf(input);
            List<Autor> autoresPorFallecimiento = service.buscarAutoresPorFallecimiento(añoFallecimiento);
            if(!autoresPorFallecimiento.isEmpty()){
                autoresPorFallecimiento.forEach(a -> System.out.println(
                        "----- AUTOR -----" +
                                "\nNombre: " + a.getNombre() +
                                "\nFecha de nacimiento: " + a.getFechaDeNacimiento() +
                                "\nFecha de fallecimiento: " + a.getFechaDeFallecimiento() +
                                "\nLibros: " + a.getLibros() +
                                "\n-----------------\n"
                ));
            }else {
                System.out.println("No existen autores con el año de fallecimiento: " +añoFallecimiento);
            }
        }catch (NumberFormatException e){
            System.out.println("Ingresa un año valido por favor");
        }catch (Exception e){
            System.out.println("Ocurrio un error"+e.getMessage());
        }
    }

    private void listarAutoresPorNacimiento() {
        System.out.println("Introduce el año de nacimiento que deseas buscar:");
        try {
            String input = teclado.nextLine();
            var añoNacimiento = Integer.valueOf(input);
            List<Autor> autoresPorNacimiento = service.buscarAutoresPorNacimiento(añoNacimiento);
            if(!autoresPorNacimiento.isEmpty()){
                autoresPorNacimiento.forEach(a -> System.out.println(
                        "----- AUTOR -----" +
                                "\nNombre: " + a.getNombre() +
                                "\nFecha de nacimiento: " + a.getFechaDeNacimiento() +
                                "\nFecha de fallecimiento: " + a.getFechaDeFallecimiento() +
                                "\nLibros: " + a.getLibros() +
                                "\n-----------------\n"
                ));
            }else {
                System.out.println("No existen autores con el año de nacimiento: " +añoNacimiento);
            }
        }catch (NumberFormatException e){
            System.out.println("Ingresa un año valido por favor");
        }catch (Exception e){
            System.out.println("Ocurrio un error"+e.getMessage());
        }
    }
}
