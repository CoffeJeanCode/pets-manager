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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/boilerplate/desktop/ui/PlantCard.fxml"));
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

