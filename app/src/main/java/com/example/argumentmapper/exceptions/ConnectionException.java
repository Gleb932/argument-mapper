package com.example.argumentmapper.exceptions;

import java.io.IOException;

public class ConnectionException extends IOException
{
    @Override
    public String getMessage() {
        return "Unable to connect to the server";
    }
}
