package com.rifiandev.siramaja;

import java.io.Serializable;

public class siramModel implements Serializable {

    private String key, namaTanaman, fotoTanaman, jamSiram, users;
    private int hourSiram, minuteSiram;

    public siramModel(){

    }

    public siramModel(String namaTanaman, String fotoTanaman, String jamSiram, String users, int hourSiram, int minuteSiram) {
        if (namaTanaman.trim().equals("")){
            namaTanaman = "Tanpa Nama";
        }
        this.namaTanaman = namaTanaman;
        this.fotoTanaman = fotoTanaman;
        this.jamSiram = jamSiram;
        this.users = users;
        this.hourSiram = hourSiram;
        this.minuteSiram = minuteSiram;
    }

    public String getKey() {
        return key;
    }

    public String getNamaTanaman() {
        return namaTanaman;
    }

    public String getFotoTanaman() {
        return fotoTanaman;
    }

    public String getJamSiram() {
        return jamSiram;
    }

    public String getUsers() {
        return users;
    }

    public int getHourSiram() {
        return hourSiram;
    }

    public int getMinuteSiram() {
        return minuteSiram;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setNamaTanaman(String namaTanaman) {
        this.namaTanaman = namaTanaman;
    }

    public void setFotoTanaman(String fotoTanaman) {
        this.fotoTanaman = fotoTanaman;
    }

    public void setJamSiram(String jamSiram) {
        this.jamSiram = jamSiram;
    }

    public void setUsers(String users) {
        this.users = users;
    }

    public void setHourSiram(int hourSiram) {
        this.hourSiram = hourSiram;
    }

    public void setMinuteSiram(int minuteSiram) {
        this.minuteSiram = minuteSiram;
    }

}
