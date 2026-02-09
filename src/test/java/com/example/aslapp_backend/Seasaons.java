package com.example.aslapp_backend;

public enum Seasaons {
    Spring(1,"spring"),
    summer(2,"summer"),
    autumn(3,"autumn"),
    winter(4,"winter");
    final private int number;
    final private String name;
     Seasaons(int number, String name){
        this.number=number;
        this.name=name;
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public static String Seasaons(int num){
         for (Seasaons s : Seasaons.values()){
             if (s.number ==num) {
                 return s.getName();
             }
             }
         throw new IllegalArgumentException("Invalid soason number" +num);

    }
}
