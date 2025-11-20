package boilerplate.desktop.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

public class MainWindow extends StackPane {

    public MainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/boilerplate/desktop/ui/MainView.fxml"));
            Node view = loader.load();
            getChildren().add(view);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to load EcoGallery UI", e);
        }
    }
}
