package boilerplate.desktop;

import boilerplate.desktop.view.MainWindow;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import static boilerplate.desktop.Resources.*;
import static boilerplate.desktop.theme.Theme.SUPPORTED_THEMES;
import static java.nio.charset.StandardCharsets.UTF_8;

public class Launcher extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        loadApplicationProperties();
        Image appIcon = new Image(getResourceAsStream(ASSETS_DIR + "app-icon.png"));

        MainWindow mainWindow = new MainWindow();
        Scene scene = new Scene(mainWindow, 1400, 900);
        
        // Load CSS files using proper resource paths
        try {
            String fontsCss = getClass().getResource(FONTS_DIR + "index.css").toExternalForm();
            String stylesCss = getClass().getResource(STYLES_DIR + "index.css").toExternalForm();
            scene.getStylesheets().addAll(fontsCss, stylesCss);
        } catch (Exception e) {
            // If CSS files don't exist, continue without them (they're optional)
            System.err.println("Warning: Could not load CSS files: " + e.getMessage());
        }

        stage.setScene(scene);
        stage.setTitle(System.getProperty("app.name"));
        stage.getIcons().add(appIcon);
        stage.setResizable(true);
        stage.setMinWidth(1000);
        stage.setMinHeight(600);
        stage.setOnCloseRequest(t -> Platform.exit());
        
        // Apply initial theme after scene is set
        Platform.runLater(() -> {
            mainWindow.selectTheme(SUPPORTED_THEMES.get(0));
        });

        Platform.runLater(() -> {
            stage.show();
            stage.requestFocus();
        });
    }

    private void loadApplicationProperties() {
        try {
            Properties properties = new Properties();
            properties.load(new InputStreamReader(getResourceAsStream(MODULE_DIR + "application.properties"), UTF_8));
            properties.forEach((key, value) -> System.setProperty(
                    String.valueOf(key),
                    String.valueOf(value)
            ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
