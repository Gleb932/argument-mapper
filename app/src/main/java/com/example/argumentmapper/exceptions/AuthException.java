package com.example.argumentmapper.exceptions;

import java.io.IOException;

public class AuthException extends IOException
{
    @Override
    public String getMessage() {
        return "Login to continue";
    }
}