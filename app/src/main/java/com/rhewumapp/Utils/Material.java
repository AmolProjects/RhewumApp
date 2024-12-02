package com.rhewumapp.Utils;

public class Material {
    private String name;
    private String densityKgM3;
    private String densityLbFt3;

    public Material(String name, String densityKgM3, String densityLbFt3) {
        this.name = name;
        this.densityKgM3 = densityKgM3;
        this.densityLbFt3 = densityLbFt3;
    }

    public String getName() {
        return name;
    }

    public String getDensityKgM3() {
        return densityKgM3;
    }

    public String getDensityLbFt3() {
        return densityLbFt3;
    }
}

