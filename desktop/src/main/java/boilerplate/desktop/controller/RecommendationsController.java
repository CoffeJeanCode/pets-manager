package boilerplate.desktop.controller;

import boilerplate.desktop.models.Plant;
import boilerplate.desktop.services.MockCatalogService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;

public class RecommendationsController {

    @FXML
    private Slider lightSlider;

    @FXML
    private Label levelLabel;

    @FXML
    private FlowPane recommendationContainer;

    @FXML
    private Button recommendButton;

    private MockCatalogService catalogService;

    @FXML
    private void initialize() {
        if (levelLabel != null && lightSlider != null) {
            levelLabel.setText("Nivel: 3/5");
            lightSlider.valueProperty().addListener((obs, oldVal, newVal) ->
                    levelLabel.setText("Nivel: " + newVal.intValue() + "/5"));
        }
    }

    public void setCatalogService(MockCatalogService catalogService) {
        this.catalogService = catalogService;
        renderRecommendations();
    }

    @FXML
    private void renderRecommendations() {
        if (catalogService == null || recommendationContainer == null) {
            return;
        }
        int level = lightSlider != null ? (int) lightSlider.getValue() : 3;
        recommendationContainer.getChildren().clear();
        for (Plant plant : catalogService.getRecommendations(level)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/boilerplate/desktop/ui/PlantCard.fxml"));
                Region card = loader.load();
                PlantCardController controller = loader.getController();
                controller.setPlant(plant);
                recommendationContainer.getChildren().add(card);
            } catch (Exception e) {
                throw new IllegalStateException("Unable to render recommendation card", e);
            }
        }
        if (recommendationContainer.getChildren().isEmpty()) {
            Label emptyLabel = new Label("Sin coincidencias para este nivel de luz.");
            emptyLabel.getStyleClass().add("muted-label");
            recommendationContainer.getChildren().add(emptyLabel);
        }
    }
}

