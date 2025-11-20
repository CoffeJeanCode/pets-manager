module boilerplate.desktop {

    requires boilerplate.base;

    requires java.desktop;
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive javafx.base;

    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.materialdesign2;
    requires java.net.http;
    requires transitive com.google.gson;

    exports boilerplate.desktop;
    exports boilerplate.desktop.theme;
    exports boilerplate.desktop.view;
    exports boilerplate.desktop.services;
    exports boilerplate.desktop.models;
    exports boilerplate.desktop.models.dto;
    exports boilerplate.desktop.repositories;
    exports boilerplate.desktop.controller;

    opens boilerplate.desktop.controller to javafx.fxml;
    opens boilerplate.desktop.view to javafx.fxml;
    
    // Open packages for Gson reflection
    opens boilerplate.desktop.models to com.google.gson;
    opens boilerplate.desktop.models.dto to com.google.gson;
}