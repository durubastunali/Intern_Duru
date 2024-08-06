package com.example.internduru;

import com.example.internduru.features.StageHandler;
import javafx.application.Application;

public class Launcher { //lazım olabilir belki??
    // bazı yerlerlede java fx jarı için herhangi bir şey extendlemeyen main'den maini çağıran class lazım diyorlar -> javafx components missing errorunu çözüyor
    public static void main(String[] args) {
        Application.launch(StageHandler.class, args);
    }
}
