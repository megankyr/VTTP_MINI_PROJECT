package com.ssf.mini.project.model;

import jakarta.validation.constraints.NotEmpty;

public class User {

    @NotEmpty(message = "Name must be provided")
    private String name;

    public User() {
    }

    public User(@NotEmpty(message = "Name must be provided") String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
