package boilerplate.desktop.controller;

import boilerplate.desktop.models.Plant;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PlantCardController {

    @FXML
    private ImageView plantImage;

    @FXML
    private Label nameLabel;

    @FXML
    private Label priceLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Button actionButton;

    public void setPlant(Plant plant) {
        if (nameLabel != null) {
            nameLabel.setText(plant.getName() + " plant");
        }
        if (priceLabel != null) {
            priceLabel.setText(String.format("%.1f $", plant.getPrice()));
        }
        if (descriptionLabel != null) {
            descriptionLabel.setText(plant.getDescription());
        }

        if (plantImage != null && plant.getImageUrl() != null && !plant.getImageUrl().isBlank()) {
            Image image = new Image(plant.getImageUrl(), true);
            plantImage.setImage(image);
        }
    }
}

