package boilerplate.desktop.controller;

import boilerplate.desktop.models.Plant;
import boilerplate.desktop.models.dto.CategoryDto;
import boilerplate.desktop.services.CategoryService;
import boilerplate.desktop.services.PlantService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.util.Callback;

import java.util.List;

public class GardenDesignerController {

    @FXML
    private FlowPane paletteContainer;

    @FXML
    private AnchorPane canvasPane;

    @FXML
    private ComboBox<String> lightFilter;

    @FXML
    private ComboBox<String> riegoFilter;

    @FXML
    private ComboBox<String> ambienteFilter;

    @FXML
    private ComboBox<CategoryDto.CategoryResponse> categoriaFilter;

    @FXML
    private Button applyFilterButton;

    @FXML
    private Button clearFilterButton;

    private PlantService plantService;
    private CategoryService categoryService;
    private List<Plant> allPlants;

    @FXML
    private void initialize() {
        plantService = new PlantService();
        categoryService = new CategoryService();
        
        // Initialize filter options
        lightFilter.setItems(FXCollections.observableArrayList("Alta", "Media", "Baja"));
        riegoFilter.setItems(FXCollections.observableArrayList("Alta", "Media", "Baja"));
        ambienteFilter.setItems(FXCollections.observableArrayList("Interior", "Exterior"));
        
        // Configure category combo box to display category names
        categoriaFilter.setCellFactory(new Callback<ListView<CategoryDto.CategoryResponse>, ListCell<CategoryDto.CategoryResponse>>() {
            @Override
            public ListCell<CategoryDto.CategoryResponse> call(ListView<CategoryDto.CategoryResponse> param) {
                return new ListCell<CategoryDto.CategoryResponse>() {
                    @Override
                    protected void updateItem(CategoryDto.CategoryResponse item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.name());
                        }
                    }
                };
            }
        });
        
        categoriaFilter.setButtonCell(new ListCell<CategoryDto.CategoryResponse>() {
            @Override
            protected void updateItem(CategoryDto.CategoryResponse item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.name());
                }
            }
        });
        
        // Load categories
        loadCategories();
        
        // Load plants
        loadPlants();
    }


    private void loadCategories() {
        categoryService.getCategories().thenAccept(categories -> {
            Platform.runLater(() -> {
                ObservableList<CategoryDto.CategoryResponse> categoryList = 
                    FXCollections.observableArrayList(categories);
                categoriaFilter.setItems(categoryList);
            });
        }).exceptionally(throwable -> {
            System.err.println("Error loading categories: " + throwable.getMessage());
            return null;
        });
    }

    private void loadPlants() {
        plantService.getPlants().thenAccept(plants -> {
            Platform.runLater(() -> {
                allPlants = plants;
                populatePalette();
            });
        }).exceptionally(throwable -> {
            System.err.println("Error loading plants: " + throwable.getMessage());
            throwable.printStackTrace();
            Platform.runLater(() -> {
                // Show empty state
                allPlants = List.of();
                populatePalette();
            });
            return null;
        });
    }

    @FXML
    private void applyFilters() {
        String luz = lightFilter.getValue();
        String riego = riegoFilter.getValue();
        String ambiente = ambienteFilter.getValue();
        CategoryDto.CategoryResponse categoria = categoriaFilter.getValue();
        Integer categoriaId = categoria != null ? categoria.id() : null;

        plantService.filterPlants(luz, riego, ambiente, categoriaId)
            .thenAccept(filteredPlants -> {
                Platform.runLater(() -> {
                    allPlants = filteredPlants;
                    populatePalette();
                });
            })
            .exceptionally(throwable -> {
                System.err.println("Error filtering plants: " + throwable.getMessage());
                return null;
            });
    }

    @FXML
    private void clearFilters() {
        lightFilter.setValue(null);
        riegoFilter.setValue(null);
        ambienteFilter.setValue(null);
        categoriaFilter.setValue(null);
        loadPlants();
    }

    private void populatePalette() {
        if (allPlants == null) {
            allPlants = List.of();
        }
        
        paletteContainer.getChildren().clear();

        for (Plant plant : allPlants) {
            try {
                java.net.URL resourceUrl = getClass().getResource("/boilerplate/desktop/boilerplate/desktop/ui/PlantCard.fxml");
                if (resourceUrl == null) {
                    // Try alternative path
                    resourceUrl = getClass().getResource("/boilerplate/desktop/ui/PlantCard.fxml");
                }
                if (resourceUrl == null) {
                    throw new IllegalStateException("PlantCard.fxml not found in classpath. " +
                        "Tried: /boilerplate/desktop/boilerplate/desktop/ui/PlantCard.fxml and /boilerplate/desktop/ui/PlantCard.fxml");
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
        
        String imageUrl = plant.getImageUrl();
        ImageView plantImageView = new ImageView();
        Image image = loadImage(imageUrl);
        
        if (image == null) {
            // Image failed to load, skip this plant
            return;
        }
        
        plantImageView.setImage(image);
        
        // Handle image loading errors silently
        image.errorProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                plantImageView.setImage(null);
            }
        });
        
        plantImageView.setFitWidth(120);
        plantImageView.setFitHeight(120);
        plantImageView.setPreserveRatio(true);
        plantImageView.setSmooth(true);
        plantImageView.getStyleClass().add("canvas-plant-image");
        
        // Set high z-index to ensure images appear above other elements
        plantImageView.setViewOrder(-1.0);
        
        double centerX = Math.max(60, Math.min(canvasPane.getWidth() / 2 - 60, canvasPane.getWidth() - 120));
        double centerY = Math.max(60, Math.min(canvasPane.getHeight() / 2 - 60, canvasPane.getHeight() - 120));
        plantImageView.setLayoutX(centerX);
        plantImageView.setLayoutY(centerY);
        
        enableDrag(plantImageView);
        canvasPane.getChildren().add(plantImageView);
        
        // Bring to front to ensure it's on top
        plantImageView.toFront();
    }
    
    private Image loadImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return null;
        }
        
        try {
            // Check if it's a remote URL (http/https)
            if (isRemoteUrl(imageUrl)) {
                // Load from remote URL
                return new Image(imageUrl, true);
            } else {
                // Load from local assets
                String localPath = normalizeLocalPath(imageUrl);
                java.net.URL resourceUrl = getClass().getResource(localPath);
                
                if (resourceUrl != null) {
                    return new Image(resourceUrl.toExternalForm(), true);
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
                        "assets/images/" + filename,
                        imageUrl
                    };
                    
                    for (String altPath : alternativePaths) {
                        resourceUrl = getClass().getResource(altPath);
                        if (resourceUrl != null) {
                            return new Image(resourceUrl.toExternalForm(), true);
                        }
                    }
                    
                    System.err.println("Local image not found: " + imageUrl + " (tried: " + localPath + ")");
                    return null;
                }
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid image path: " + imageUrl);
            return null;
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
            // Bring to front when dragging to ensure it's always on top
            node.toFront();
        });
        node.setCursor(Cursor.OPEN_HAND);
    }

    private static class Delta {
        double x, y;
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

