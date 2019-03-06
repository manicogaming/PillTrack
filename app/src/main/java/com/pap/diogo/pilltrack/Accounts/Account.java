package com.pap.diogo.pilltrack.Accounts;

public class Account {
    private String name, idade;

    public Account() {

    }

    public Account(String name, String idade) {
        this.name = name;
        this.idade = idade;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdade() {
        return idade;
    }

    public void setIdade(String idade) {
        this.idade = idade;
    }
}
