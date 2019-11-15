package ca.uqac.alterra.database.exceptions;

public class AlterraAuthException extends Exception {

    public AlterraAuthException(){
        super("Unknown error");
    }

    public AlterraAuthException(Exception e){
        super(e);
    }
}
