package com.warrieta.literalura.literalura.model;

public enum Idioma {
    ES("es", "Español"),
    EN("en", "Inglés"),
    FR("fr", "Francés"),
    PT("pt", "Portugués");

    private final String idiomaOmdb;
    private String idiomaEspanol;
    Idioma(String idiomaOmdb, String idiomaEspanol){
        this.idiomaOmdb = idiomaOmdb;
        this.idiomaEspanol = idiomaEspanol;
    }

    public static Idioma fromString(String text){
        for(Idioma idioma : Idioma.values()){
            if(idioma.idiomaOmdb.equalsIgnoreCase(text)){
                return idioma;
            }
        }
        throw new IllegalArgumentException("Ninguna idioma encontrado: " + text);
    }

    public static Idioma fromEspanol(String text){
        for(Idioma idioma : Idioma.values()){
            if(idioma.idiomaEspanol.equalsIgnoreCase(text)){
                return idioma;
            }
        }
        throw new IllegalArgumentException("Ninguna idioma encontrado: " + text);
    }

    public String getIdiomaOmdb(){
        return this.idiomaOmdb;
    }
}
