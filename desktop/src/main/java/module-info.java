module boilerplate.desktop {

    requires boilerplate.base;

    requires java.desktop;
    requires javafx.controls;
    requires javafx.graphics;
    requires transitive javafx.base;

    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.materialdesign2;
    requires java.net.http;
    requires com.google.gson;

    exports boilerplate.desktop;
    exports boilerplate.desktop.theme;
    exports boilerplate.desktop.view;
    exports boilerplate.desktop.services;
    exports boilerplate.desktop.models;
    exports boilerplate.desktop.models.dto;
    exports boilerplate.desktop.repositories;

    // resources - these are resource directories, not packages
    // opens is used for reflection access to resources
}