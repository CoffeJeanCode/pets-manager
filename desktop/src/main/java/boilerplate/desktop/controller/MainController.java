package boilerplate.desktop.controller;

import boilerplate.desktop.services.MockCatalogService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainController {

    @FXML
    private StackPane contentContainer;

    @FXML
    private ToggleButton catalogToggle;

    @FXML
    private ToggleButton recommendationsToggle;

    @FXML
    private ToggleButton designerToggle;

    @FXML
    private Label greetingLabel;

    private final MockCatalogService catalogService = new MockCatalogService();
    private final Map<String, Node> viewCache = new HashMap<>();

            @FXML
            private void initialize() {
                greetingLabel.setText("Plant Shop");
                catalogToggle.setSelected(true);
                showCatalog();
            }

    @FXML
    private void showCatalog() {
        showView("catalog", "/boilerplate/desktop/ui/CatalogView.fxml", loader -> {
            CatalogController controller = loader.getController();
            controller.setCatalogService(catalogService);
        });
    }

    @FXML
    private void showRecommendations() {
        showView("recommendations", "/boilerplate/desktop/ui/RecommendationsView.fxml", loader -> {
            RecommendationsController controller = loader.getController();
            controller.setCatalogService(catalogService);
        });
    }

    @FXML
    private void showDesigner() {
        showView("designer", "/boilerplate/desktop/ui/DesignerView.fxml", loader -> {
            GardenDesignerController controller = loader.getController();
            controller.setCatalogService(catalogService);
        });
    }

    private void showView(String key,
                          String resource,
                          ViewConfigurator configurator) {
        try {
            Node view = viewCache.computeIfAbsent(key, k -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
                    Node loadedView = loader.load();
                    configurator.configure(loader);
                    return loadedView;
                } catch (IOException e) {
                    throw new IllegalStateException("Unable to load view " + resource, e);
                }
            });

            contentContainer.getChildren().setAll(view);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to render view " + key, e);
        }
    }

    @FunctionalInterface
    private interface ViewConfigurator {
        void configure(FXMLLoader loader) throws IOException;
    }
}

