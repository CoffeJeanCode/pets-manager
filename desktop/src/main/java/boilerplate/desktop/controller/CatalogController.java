package boilerplate.desktop.controller;

import boilerplate.desktop.models.Plant;
import boilerplate.desktop.services.MockCatalogService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;

public class CatalogController {

    @FXML
    private FlowPane cardContainer;

    private MockCatalogService catalogService;

    public void setCatalogService(MockCatalogService catalogService) {
        this.catalogService = catalogService;
        populateCards();
    }

    private void populateCards() {
        if (catalogService == null || cardContainer == null) {
            return;
        }
        cardContainer.getChildren().clear();
        for (Plant plant : catalogService.getPlants()) {
            try {
                java.net.URL resourceUrl = getClass().getResource("/boilerplate/desktop/ui/PlantCard.fxml");
                if (resourceUrl == null) {
                    throw new IllegalStateException("PlantCard.fxml not found in classpath. " +
                        "Expected at: /boilerplate/desktop/ui/PlantCard.fxml");
                }
                FXMLLoader loader = new FXMLLoader(resourceUrl);
                Region card = loader.load();
                PlantCardController controller = loader.getController();
                controller.setPlant(plant);
                cardContainer.getChildren().add(card);
            } catch (Exception e) {
                throw new IllegalStateException("Unable to render plant card", e);
            }
        }
    }
}

