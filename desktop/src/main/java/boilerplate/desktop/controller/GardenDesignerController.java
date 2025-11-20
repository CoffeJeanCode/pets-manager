package boilerplate.desktop.controller;

import boilerplate.desktop.models.Plant;
import boilerplate.desktop.services.MockCatalogService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;

public class GardenDesignerController {

    @FXML
    private FlowPane paletteContainer;

    @FXML
    private AnchorPane canvasPane;

    private MockCatalogService catalogService;

    public void setCatalogService(MockCatalogService catalogService) {
        this.catalogService = catalogService;
        populatePalette();
    }

    private void populatePalette() {
        if (catalogService == null) return;
        paletteContainer.getChildren().clear();

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
                card.getStyleClass().add("palette-card");
                card.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> addPlantToCanvas(plant));
                paletteContainer.getChildren().add(card);
            } catch (Exception e) {
                throw new IllegalStateException("Unable to load palette card", e);
            }
        }
    }

    private void addPlantToCanvas(Plant plant) {
        if (plant.getImageUrl() == null || plant.getImageUrl().isBlank()) {
            return;
        }
        
        ImageView plantImageView = new ImageView();
        Image image = new Image(plant.getImageUrl(), true);
        plantImageView.setImage(image);
        plantImageView.setFitWidth(120);
        plantImageView.setFitHeight(120);
        plantImageView.setPreserveRatio(true);
        plantImageView.setSmooth(true);
        plantImageView.getStyleClass().add("canvas-plant-image");
        
        double centerX = Math.max(60, Math.min(canvasPane.getWidth() / 2 - 60, canvasPane.getWidth() - 120));
        double centerY = Math.max(60, Math.min(canvasPane.getHeight() / 2 - 60, canvasPane.getHeight() - 120));
        plantImageView.setLayoutX(centerX);
        plantImageView.setLayoutY(centerY);
        
        enableDrag(plantImageView);
        canvasPane.getChildren().add(plantImageView);
    }

    private void enableDrag(Node node) {
        final Delta dragDelta = new Delta();
        node.setOnMousePressed(event -> {
            dragDelta.x = node.getLayoutX() - event.getSceneX();
            dragDelta.y = node.getLayoutY() - event.getSceneY();
            node.setCursor(Cursor.CLOSED_HAND);
        });
        node.setOnMouseReleased(event -> node.setCursor(Cursor.OPEN_HAND));
        node.setOnMouseDragged(event -> {
            double newX = event.getSceneX() + dragDelta.x;
            double newY = event.getSceneY() + dragDelta.y;
            double maxX = canvasPane.getWidth() - (node instanceof javafx.scene.image.ImageView ? 
                ((javafx.scene.image.ImageView) node).getFitWidth() : 100);
            double maxY = canvasPane.getHeight() - (node instanceof javafx.scene.image.ImageView ? 
                ((javafx.scene.image.ImageView) node).getFitHeight() : 40);
            node.setLayoutX(Math.max(0, Math.min(newX, maxX)));
            node.setLayoutY(Math.max(0, Math.min(newY, maxY)));
        });
        node.setCursor(Cursor.OPEN_HAND);
    }

    private static class Delta {
        double x, y;
    }
}

