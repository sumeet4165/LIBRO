package LIBRO.libro.Exceptions;

import LIBRO.libro.Entities.Genre;

public class GenreExceptions extends  Exception {
   public  GenreExceptions(String msg) {
        super("Error in Genre " + msg);


    }
}
