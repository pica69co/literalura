### Challenge Literalura Backend G6 Next-Oracle Alura-Latam

LiterAlura aplicación de backend / challenge propuesto por Alura Latam. 
Consumiendo Api de gutendex.com
- gestionar un catálogo de libros, 
- búsqueda por título, listar libros registrados, 
- listar autores,
- buscar autores vivos en un año específico
- filtrar libros por idioma. 
- generar Estadisticas,
- Top 10
- buscar libros por idioma
- manejo de errores y
- evitar la inserción duplicada de libros 
  en la base de datos.

### Tecnologías utilizadas

- Java
- Spring Boot
- Spring Data JPA
- PostgreSQL

### Uso

### Instrucciones de instalación

1. Clona el repositorio.
2. Configura base de datos PostgreSQL.
3. Configuración de archivo `application.properties` sea correcta.
  `spring.datasource.url = jdbc:postgresql://DBHOST/DB_NAME
     spring.datasource.username='your DB_USER_NAME'
     spring.datasource.password='your DB_PASSWORD'
     spring.datasource.driver-class-name=org.postgresql.Driver
    hibernate.dialect=org.hibernate.dialect.HSQLDialect

    spring.jpa.hibernate.ddl-auto = update
    #spring.jpa.show-sql=true
    #spring.jpa.format-sql=true
   `
4. Actualiza dependencias Maven   
5. Ejecuta la aplicación.
