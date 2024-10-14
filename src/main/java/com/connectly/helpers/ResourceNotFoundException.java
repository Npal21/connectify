package com.connectly.helpers;

public class ResourceNotFoundException extends RuntimeException{

    public  ResourceNotFoundException(String message){
        super(message);
    }

    public  ResourceNotFoundException(){
        super("User not found");
    }


}
