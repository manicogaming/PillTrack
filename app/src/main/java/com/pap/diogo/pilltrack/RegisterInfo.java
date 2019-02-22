package com.pap.diogo.pilltrack;

public class RegisterInfo {
    public String name;
    public String idade;

    public RegisterInfo(String name, String idade) {
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
