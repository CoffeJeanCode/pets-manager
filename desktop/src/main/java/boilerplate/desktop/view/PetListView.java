package boilerplate.desktop.view;

import boilerplate.desktop.models.Pet;
import boilerplate.desktop.models.dto.PetForm;
import boilerplate.desktop.services.PetService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignA;
import org.kordamp.ikonli.materialdesign2.MaterialDesignD;
import org.kordamp.ikonli.materialdesign2.MaterialDesignP;
import org.kordamp.ikonli.materialdesign2.MaterialDesignR;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class PetListView extends VBox {
    
    private final PetService petService;
    private final ObservableList<Pet> pets;
    private final FilteredList<Pet> filteredPets;
    private final TableView<Pet> table;
    private TextField searchField;
    private ComboBox<String> speciesFilter;
    private ComboBox<String> statusFilter;
    private Label statusLabel;
    private StackPane toastContainer;
    
    public PetListView(PetService petService) {
        this.petService = petService;
        this.pets = FXCollections.observableArrayList();
        this.filteredPets = new FilteredList<>(pets);
        
        setSpacing(15);
        setPadding(new Insets(20));
        getStyleClass().add("pet-list-view");
        
        // Create toast container
        toastContainer = new StackPane();
        toastContainer.setMouseTransparent(true);
        toastContainer.setPickOnBounds(false);
        
        // Create UI components
        table = createTable();
        VBox controls = createControls();
        HBox toolbar = createToolbar();
        
        VBox content = new VBox(toolbar, controls, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        
        // Wrap in StackPane for toast notifications
        StackPane container = new StackPane(content, toastContainer);
        StackPane.setAlignment(toastContainer, Pos.TOP_CENTER);
        getChildren().add(container);
        
        // Load initial data
        loadPets();
    }
    
    private HBox createToolbar() {
        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(0, 0, 10, 0));
        
        Label title = new Label("Gestión de Mascotas");
        title.getStyleClass().add("title");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button refreshBtn = new Button("Actualizar", new FontIcon(MaterialDesignR.REFRESH));
        refreshBtn.setOnAction(e -> loadPets());
        
        Button addBtn = new Button("Agregar Mascota", new FontIcon(MaterialDesignA.ACCOUNT_PLUS));
        addBtn.getStyleClass().add("primary-button");
        addBtn.setOnAction(e -> showPetFormDialog(null));
        
        toolbar.getChildren().addAll(title, spacer, refreshBtn, addBtn);
        return toolbar;
    }
    
    private VBox createControls() {
        VBox controls = new VBox(10);
        controls.setPadding(new Insets(10));
        controls.getStyleClass().add("controls-panel");
        
        // Search field
        HBox searchBox = new HBox(10);
        searchField = new TextField();
        searchField.setPromptText("Buscar por nombre, raza o descripción...");
        searchField.setPrefWidth(400);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            // Optimistic UI: apply filter immediately
            applyFilters();
        });
        
        Label searchLabel = new Label("Buscar:");
        searchBox.getChildren().addAll(searchLabel, searchField);
        
        // Filter boxes
        HBox filterBox = new HBox(15);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        
        Label speciesLabel = new Label("Especie:");
        speciesFilter = new ComboBox<>(FXCollections.observableArrayList("Todas", "Perro", "Gato", "Ave", "Conejo", "Otro"));
        speciesFilter.setValue("Todas");
        speciesFilter.setOnAction(e -> {
            // Optimistic UI: apply filter immediately
            applyFilters();
        });
        
        Label statusLabel = new Label("Estado:");
        statusFilter = new ComboBox<>(FXCollections.observableArrayList("Todos", "disponible", "adoptado", "en_proceso"));
        statusFilter.setValue("Todos");
        statusFilter.setOnAction(e -> {
            // Optimistic UI: apply filter immediately
            applyFilters();
        });
        
        filterBox.getChildren().addAll(speciesLabel, speciesFilter, statusLabel, statusFilter);
        
        // Status label
        this.statusLabel = new Label();
        this.statusLabel.getStyleClass().add("status-label");
        
        controls.getChildren().addAll(searchBox, filterBox, this.statusLabel);
        return controls;
    }
    
    private TableView<Pet> createTable() {
        TableView<Pet> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tableView.getStyleClass().add("pet-table");
        
        // ID Column
        TableColumn<Pet, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> {
            Pet pet = data.getValue();
            return new javafx.beans.property.SimpleObjectProperty<>(pet != null ? pet.id() : null);
        });
        idCol.setPrefWidth(60);
        
        // Name Column
        TableColumn<Pet, String> nameCol = new TableColumn<>("Nombre");
        nameCol.setCellValueFactory(data -> {
            Pet pet = data.getValue();
            return new javafx.beans.property.SimpleStringProperty(pet != null && pet.name() != null ? pet.name() : "");
        });
        nameCol.setPrefWidth(120);
        
        // Species Column
        TableColumn<Pet, String> speciesCol = new TableColumn<>("Especie");
        speciesCol.setCellValueFactory(data -> {
            Pet pet = data.getValue();
            return new javafx.beans.property.SimpleStringProperty(pet != null && pet.species() != null ? pet.species() : "");
        });
        speciesCol.setPrefWidth(100);
        
        // Breed Column
        TableColumn<Pet, String> breedCol = new TableColumn<>("Raza");
        breedCol.setCellValueFactory(data -> {
            Pet pet = data.getValue();
            return new javafx.beans.property.SimpleStringProperty(pet != null && pet.breed() != null ? pet.breed() : "");
        });
        breedCol.setPrefWidth(120);
        
        // Age Column
        TableColumn<Pet, Integer> ageCol = new TableColumn<>("Edad");
        ageCol.setCellValueFactory(data -> {
            Pet pet = data.getValue();
            return new javafx.beans.property.SimpleObjectProperty<>(pet != null ? pet.approxAge() : null);
        });
        ageCol.setPrefWidth(60);
        
        // Sex Column
        TableColumn<Pet, String> sexCol = new TableColumn<>("Sexo");
        sexCol.setCellValueFactory(data -> {
            Pet pet = data.getValue();
            String sex = pet != null && pet.sex() != null ? pet.sex() : "";
            return new javafx.beans.property.SimpleStringProperty(sex.equals("male") ? "Macho" : sex.equals("female") ? "Hembra" : sex);
        });
        sexCol.setPrefWidth(70);
        
        // Size Column
        TableColumn<Pet, String> sizeCol = new TableColumn<>("Tamaño");
        sizeCol.setCellValueFactory(data -> {
            Pet pet = data.getValue();
            String size = pet != null && pet.size() != null ? pet.size() : "";
            return new javafx.beans.property.SimpleStringProperty(
                size.equals("small") ? "Pequeño" : 
                size.equals("medium") ? "Mediano" : 
                size.equals("large") ? "Grande" : size
            );
        });
        sizeCol.setPrefWidth(80);
        
        // Status Column with styling
        TableColumn<Pet, String> statusCol = new TableColumn<>("Estado");
        statusCol.setCellValueFactory(data -> {
            Pet pet = data.getValue();
            return new javafx.beans.property.SimpleStringProperty(pet != null && pet.adoptionStatus() != null ? pet.adoptionStatus() : "");
        });
        statusCol.setPrefWidth(100);
        statusCol.setCellFactory(column -> new TableCell<Pet, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    getStyleClass().removeAll("status-available", "status-adopted", "status-in-process");
                } else {
                    String displayText = status.equals("available") ? "Disponible" :
                                       status.equals("adopted") ? "Adoptado" :
                                       status.equals("in_process") ? "En Proceso" : status;
                    setText(displayText);
                    getStyleClass().removeAll("status-available", "status-adopted", "status-in-process");
                    switch (status.toLowerCase()) {
                        case "available":
                            getStyleClass().add("status-available");
                            break;
                        case "adopted":
                            getStyleClass().add("status-adopted");
                            break;
                        case "in_process":
                            getStyleClass().add("status-in-process");
                            break;
                    }
                }
            }
        });
        
        // Health Status Column
        TableColumn<Pet, String> healthCol = new TableColumn<>("Salud");
        healthCol.setCellValueFactory(data -> {
            Pet pet = data.getValue();
            return new javafx.beans.property.SimpleStringProperty(pet != null && pet.healthStatus() != null ? pet.healthStatus() : "");
        });
        healthCol.setPrefWidth(100);
        
        // Intake Date Column
        TableColumn<Pet, LocalDate> intakeCol = new TableColumn<>("Fecha de Ingreso");
        intakeCol.setCellValueFactory(data -> {
            Pet pet = data.getValue();
            return new javafx.beans.property.SimpleObjectProperty<>(pet != null ? pet.intakeDate() : null);
        });
        intakeCol.setPrefWidth(120);
        intakeCol.setCellFactory(column -> new TableCell<Pet, LocalDate>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setText(empty || date == null ? null : date.format(formatter));
            }
        });
        
        // Actions Column
        TableColumn<Pet, Void> actionsCol = new TableColumn<>("Acciones");
        actionsCol.setPrefWidth(150);
        actionsCol.setCellFactory(column -> new TableCell<Pet, Void>() {
            private final Button editBtn = new Button("", new FontIcon(MaterialDesignP.PENCIL));
            private final Button deleteBtn = new Button("", new FontIcon(MaterialDesignD.DELETE));
            private final HBox buttons = new HBox(5, editBtn, deleteBtn);
            
            {
                editBtn.getStyleClass().add("icon-button");
                deleteBtn.getStyleClass().add("icon-button");
                deleteBtn.getStyleClass().add("danger-button");
                buttons.setAlignment(Pos.CENTER);
                
                editBtn.setOnAction(e -> {
                    Pet pet = getTableView().getItems().get(getIndex());
                    showPetFormDialog(pet);
                });
                
                deleteBtn.setOnAction(e -> {
                    Pet pet = getTableView().getItems().get(getIndex());
                    deletePet(pet);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttons);
                }
            }
        });
        
        tableView.getColumns().addAll(idCol, nameCol, speciesCol, breedCol, ageCol, 
                                     sexCol, sizeCol, statusCol, healthCol, intakeCol, actionsCol);
        
        // Bind filtered list to table
        SortedList<Pet> sortedList = new SortedList<>(filteredPets);
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedList);
        
        return tableView;
    }
    
    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase();
        String species = speciesFilter.getValue();
        String status = statusFilter.getValue();
        
        filteredPets.setPredicate(pet -> {
            // Search filter
            boolean matchesSearch = searchText.isEmpty() ||
                    (pet.name() != null && pet.name().toLowerCase().contains(searchText)) ||
                    (pet.breed() != null && pet.breed().toLowerCase().contains(searchText)) ||
                    (pet.description() != null && pet.description().toLowerCase().contains(searchText));
            
            // Species filter
            boolean matchesSpecies = "Todas".equals(species) || 
                    (pet.species() != null && pet.species().equalsIgnoreCase(species)) ||
                    ("Perro".equals(species) && "dog".equalsIgnoreCase(pet.species())) ||
                    ("Gato".equals(species) && "cat".equalsIgnoreCase(pet.species()));
            
            // Status filter
            boolean matchesStatus = "Todos".equals(status) ||
                    (pet.adoptionStatus() != null && pet.adoptionStatus().equalsIgnoreCase(status)) ||
                    ("disponible".equals(status) && "available".equalsIgnoreCase(pet.adoptionStatus())) ||
                    ("adoptado".equals(status) && "adopted".equalsIgnoreCase(pet.adoptionStatus())) ||
                    ("en_proceso".equals(status) && "in_process".equalsIgnoreCase(pet.adoptionStatus()));
            
            return matchesSearch && matchesSpecies && matchesStatus;
        });
        
        updateStatusLabel();
    }
    
    private void updateStatusLabel() {
        int total = pets.size();
        int filtered = filteredPets.size();
        statusLabel.setText(String.format("Mostrando %d de %d mascotas", filtered, total));
    }
    
    private void loadPets() {
        statusLabel.setText("Cargando...");
        petService.getAllPets()
                .thenAccept(petList -> {
                    Platform.runLater(() -> {
                        if (petList != null) {
                            pets.clear();
                            pets.addAll(petList);
                            applyFilters();
                            statusLabel.setText(String.format("Cargadas %d mascotas", petList.size()));
                        } else {
                            pets.clear();
                            applyFilters();
                            statusLabel.setText("No se encontraron mascotas");
                        }
                    });
                })
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        String errorMsg = throwable.getMessage();
                        if (errorMsg == null || errorMsg.isEmpty()) {
                            errorMsg = "No se puede conectar a la API. Por favor verifique que el servidor esté ejecutándose en localhost:8001";
                        }
                        showError("Error al cargar mascotas", errorMsg);
                        statusLabel.setText("Error al cargar mascotas");
                        pets.clear();
                        applyFilters();
                    });
                    return null;
                });
    }
    
    private void showPetFormDialog(Pet pet) {
        PetFormDialog dialog = new PetFormDialog(pet);
        Optional<PetForm> result = dialog.showAndWait();
        
        result.ifPresent(form -> {
            if (pet == null) {
                // Create new pet
                createPet(form);
            } else {
                // Update existing pet
                updatePet(pet.id(), form);
            }
        });
    }
    
    private void createPet(PetForm form) {
        statusLabel.setText("Creando mascota...");
        petService.createPet(form.toDto())
                .thenAccept(createdPet -> {
                    Platform.runLater(() -> {
                        // Refresh from server to get complete data
                        loadPets();
                        showSuccess("¡Mascota creada exitosamente!");
                    });
                })
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        showError("Error al crear mascota", throwable.getMessage());
                        statusLabel.setText("Error al crear mascota");
                    });
                    return null;
                });
    }
    
    private void updatePet(Long id, PetForm form) {
        statusLabel.setText("Actualizando mascota...");
        petService.updatePet(id, form.toUpdateDto())
                .thenAccept(updatedPet -> {
                    Platform.runLater(() -> {
                        // Find and update the pet in the list
                        for (int i = 0; i < pets.size(); i++) {
                            if (pets.get(i).id().equals(id)) {
                                pets.set(i, updatedPet);
                                break;
                            }
                        }
                        // Also refresh from server to ensure consistency
                        loadPets();
                        showSuccess("¡Mascota actualizada exitosamente!");
                    });
                })
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        showError("Error al actualizar mascota", throwable.getMessage());
                        statusLabel.setText("Error al actualizar mascota");
                    });
                    return null;
                });
    }
    
    private void deletePet(Pet pet) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmar Eliminación");
        confirmDialog.setHeaderText("Eliminar Mascota");
        confirmDialog.setContentText(String.format("¿Está seguro de que desea eliminar %s (ID: %d)?", 
                pet.name(), pet.id()));
        
        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            statusLabel.setText("Eliminando mascota...");
            petService.deletePet(pet.id())
                    .thenRun(() -> {
                        Platform.runLater(() -> {
                            // Refresh from server to ensure consistency
                            loadPets();
                            showSuccess("¡Mascota eliminada exitosamente!");
                        });
                    })
                    .exceptionally(throwable -> {
                        Platform.runLater(() -> {
                            showError("Error al eliminar mascota", throwable.getMessage());
                            statusLabel.setText("Error al eliminar mascota");
                        });
                        return null;
                    });
        }
    }
    
    private void showSuccess(String message) {
        Toast.showToast(toastContainer, message, Toast.ToastType.SUCCESS);
    }
    
    private void showError(String title, String message) {
        Toast.showToast(toastContainer, title + ": " + message, Toast.ToastType.ERROR);
    }
}

