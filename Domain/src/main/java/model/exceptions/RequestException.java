package model.exceptions;

public class RequestException extends RuntimeException{
    public RequestException(String message){
        super(message);
    }
}
