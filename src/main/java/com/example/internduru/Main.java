//SRC İLE AYNI PATH'E RESOURCES DOSYASI OLUŞTUR VE DOSYA PATHLERİNİ ORDAN AL
//MAVEN'DAN GRADLE'A GEÇ

package com.example.internduru;

public class Main { //JAR dosyası olarak export edebilmek için herhangi bir şey extendlemeyen, main metodu içeren class lazım
                     //bu classla jar başlatılıyor
    public static void main(String[] args) {
        StageHandler.main(args);
    }
}
