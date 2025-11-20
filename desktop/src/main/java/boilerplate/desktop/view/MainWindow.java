package boilerplate.desktop.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

public class MainWindow extends StackPane {

    public MainWindow() {
        try {
            java.net.URL resourceUrl = getClass().getResource("/boilerplate/desktop/ui/MainView.fxml");
            if (resourceUrl == null) {
                throw new IllegalStateException("MainView.fxml not found in classpath. " +
                    "Expected at: /boilerplate/desktop/ui/MainView.fxml");
            }
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Node view = loader.load();
            getChildren().add(view);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to load EcoGallery UI", e);
        }
    }
}
