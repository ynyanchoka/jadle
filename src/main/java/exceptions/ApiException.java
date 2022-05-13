package exceptions;

public class ApiException extends RuntimeException {
    private final int statusCode;
    public ApiException (int statusCode, String msg){
        super(msg);//calling the superclass' (i.e. the object class we are inheriting from, the parent class)
        this.statusCode = statusCode;//affecting that object that the call to super created.
    }

    public int getStatusCode() {
        return statusCode;
    }
}
