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
        // Try both paths (duplicated and normal) due to Maven resource configuration
        String[] cssPaths = {
            "/boilerplate/desktop/boilerplate/desktop/assets/styles/index.css",
            STYLES_DIR + "index.css"
        };
        
        boolean cssLoaded = false;
        for (String cssPath : cssPaths) {
            try {
                java.net.URL stylesUrl = getClass().getResource(cssPath);
                if (stylesUrl != null) {
                    scene.getStylesheets().add(stylesUrl.toExternalForm());
                    System.out.println("CSS loaded from: " + stylesUrl.toExternalForm());
                    cssLoaded = true;
                    break;
                }
            } catch (Exception e) {
                // Try next path
            }
        }
        
        if (!cssLoaded) {
            System.err.println("Warning: Could not load main styles CSS from any path");
        }
        
        // Load fonts CSS (optional - may not exist)
        String[] fontPaths = {
            "/boilerplate/desktop/boilerplate/desktop/assets/fonts/index.css",
            FONTS_DIR + "index.css"
        };
        
        for (String fontPath : fontPaths) {
            try {
                java.net.URL fontsUrl = getClass().getResource(fontPath);
                if (fontsUrl != null) {
                    scene.getStylesheets().add(fontsUrl.toExternalForm());
                    break;
                }
            } catch (Exception e) {
                // Optional, ignore
            }
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
