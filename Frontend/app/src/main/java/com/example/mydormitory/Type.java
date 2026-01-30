package com.example.mydormitory;

import java.util.Arrays;
import java.util.Optional;

public enum Type {
    plumber("Сантехник"),
    carpenter("Плотник"),
    electrician("Электрик");
    String typeStr;

    Type(String typeStr) {
        this.typeStr = typeStr;
    }
    public static String getTypeStrByType(String type) {
        Optional<Type> first = Arrays.stream(Type.values()).filter(type1 -> type1.name().equals(type)).findFirst();
        Type type1 = first.orElse(null);
        if (type1 == null)
        {
            return  "Undefined";
        }
        else
        {
            return type1.typeStr;
        }
    }
}
