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
        // Load fonts CSS (optional - may not exist)
        try {
            java.net.URL fontsUrl = getClass().getResource(FONTS_DIR + "index.css");
            if (fontsUrl != null) {
                scene.getStylesheets().add(fontsUrl.toExternalForm());
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not load fonts CSS: " + e.getMessage());
        }
        
        // Load main styles CSS (required)
        try {
            java.net.URL stylesUrl = getClass().getResource(STYLES_DIR + "index.css");
            if (stylesUrl != null) {
                scene.getStylesheets().add(stylesUrl.toExternalForm());
                System.out.println("CSS loaded from: " + stylesUrl.toExternalForm());
            } else {
                System.err.println("ERROR: Main styles CSS not found at: " + STYLES_DIR + "index.css");
            }
        } catch (Exception e) {
            System.err.println("ERROR: Could not load main styles CSS: " + e.getMessage());
            e.printStackTrace();
        }

        stage.setScene(scene);
        stage.setTitle(System.getProperty("app.name"));
        stage.getIcons().add(appIcon);
        stage.setResizable(true);
        stage.setMinWidth(1000);
        stage.setMinHeight(600);
        stage.setOnCloseRequest(t -> Platform.exit());
        
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
