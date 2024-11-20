package com.example.agroventa.data;

public class UserData {
    private String name;
    private String email;
    private String phone;
    private String apellido;


    public UserData(String name, String apellido, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.apellido = apellido;
    }

    public String getName() {
        return name;
    }

    public String getApellido() {
        return apellido;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }
}

