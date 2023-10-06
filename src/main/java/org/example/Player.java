package org.example;

public class Player {
    private String name;

    Player(String name){
        if (name == null || name.isBlank()){
            throw new IllegalArgumentException("New player name cannot be null or blank!");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
