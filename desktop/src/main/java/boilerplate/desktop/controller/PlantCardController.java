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
            double price = plant.getPrice();
            if (price > 0) {
                priceLabel.setText(String.format("%.1f $", price));
            } else {
                priceLabel.setText("Ver precio");
            }
        }
        if (descriptionLabel != null) {
            String description = plant.getDescription();
            if (description != null && !description.isBlank()) {
                descriptionLabel.setText(description);
            } else {
                descriptionLabel.setText("Planta decorativa para tu jardín.");
            }
        }

        if (plantImage != null && plant.getImageUrl() != null && !plant.getImageUrl().isBlank()) {
            String imageUrl = plant.getImageUrl();
            loadImage(imageUrl, plantImage);
        } else if (plantImage != null) {
            plantImage.setImage(null);
        }
    }
    
    private void loadImage(String imageUrl, ImageView imageView) {
        if (imageUrl == null || imageUrl.isBlank()) {
            imageView.setImage(null);
            return;
        }
        
        try {
            Image image = null;
            
            // Check if it's a remote URL (http/https)
            if (isRemoteUrl(imageUrl)) {
                // Load from remote URL
                image = new Image(imageUrl, true);
            } else {
                // Load from local assets
                String localPath = normalizeLocalPath(imageUrl);
                java.net.URL resourceUrl = getClass().getResource(localPath);
                
                if (resourceUrl != null) {
                    image = new Image(resourceUrl.toExternalForm(), true);
                } else {
                    // Try alternative paths - check various formats
                    String filename = extractFilename(imageUrl);
                    String[] alternativePaths = {
                        "/boilerplate/desktop/assets/images/" + filename,
                        "/boilerplate/desktop/boilerplate/desktop/assets/images/" + filename,
                        localPath,
                        imageUrl.startsWith("/") ? imageUrl : "/" + imageUrl,
                        "/boilerplate/desktop/assets/" + filename,
                        "/images/" + filename,
                        "/assets/images/" + filename,
                        "images/" + filename,
                        "assets/images/" + filename
                    };
                    
                    for (String altPath : alternativePaths) {
                        resourceUrl = getClass().getResource(altPath);
                        if (resourceUrl != null) {
                            image = new Image(resourceUrl.toExternalForm(), true);
                            break;
                        }
                    }
                    
                    if (image == null) {
                        System.err.println("Local image not found: " + imageUrl + " (tried: " + localPath + ")");
                        imageView.setImage(null);
                        return;
                    }
                }
            }
            
            if (image != null) {
                imageView.setImage(image);
                
                // Handle image loading errors silently
                image.errorProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue && imageView != null) {
                        imageView.setImage(null); // Clear invalid image
                    }
                });
            }
        } catch (IllegalArgumentException e) {
            // Invalid URL, don't set image
            System.err.println("Invalid image path: " + imageUrl);
            imageView.setImage(null);
        }
    }
    
    private boolean isRemoteUrl(String url) {
        if (url == null || url.isBlank()) {
            return false;
        }
        try {
            java.net.URI uri = new java.net.URI(url);
            return uri.getScheme() != null && (uri.getScheme().equals("http") || uri.getScheme().equals("https"));
        } catch (java.net.URISyntaxException e) {
            return false;
        }
    }
    
    private String normalizeLocalPath(String path) {
        if (path == null || path.isBlank()) {
            return null;
        }
        
        // Remove leading/trailing whitespace
        String cleaned = path.trim();
        
        // If it's already the full resource path, return it
        if (cleaned.startsWith("/boilerplate/desktop/assets/images/")) {
            return cleaned;
        }
        
        // Extract filename from various path formats
        String filename;
        
        // Handle paths like "/images/girasol.png" or "images/girasol.png"
        if (cleaned.contains("/images/") || cleaned.startsWith("images/")) {
            filename = cleaned.substring(cleaned.lastIndexOf("/images/") + "/images/".length());
        }
        // Handle paths like "/assets/images/girasol.png" or "assets/images/girasol.png"
        else if (cleaned.contains("/assets/images/") || cleaned.startsWith("assets/images/")) {
            filename = cleaned.substring(cleaned.lastIndexOf("/assets/images/") + "/assets/images/".length());
        }
        // Handle full path like "/boilerplate/desktop/assets/images/girasol.png"
        else if (cleaned.contains("boilerplate/desktop/assets/images/")) {
            filename = cleaned.substring(cleaned.lastIndexOf("boilerplate/desktop/assets/images/") + 
                                         "boilerplate/desktop/assets/images/".length());
        }
        // If it's just a filename (e.g., "girasol.png")
        else {
            // Remove leading slash if present
            filename = cleaned.startsWith("/") ? cleaned.substring(1) : cleaned;
        }
        
        // Normalize to the full resource path
        return "/boilerplate/desktop/assets/images/" + filename;
    }
    
    private String extractFilename(String path) {
        if (path == null || path.isBlank()) {
            return path;
        }
        
        String cleaned = path.trim();
        
        // Extract filename from various formats
        if (cleaned.contains("/")) {
            return cleaned.substring(cleaned.lastIndexOf("/") + 1);
        }
        
        return cleaned;
    }
}

