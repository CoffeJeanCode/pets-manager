package boilerplate.desktop.view;

import boilerplate.desktop.models.Vaccine;
import boilerplate.desktop.models.dto.VaccineForm;
import boilerplate.desktop.services.PetService;
import boilerplate.desktop.services.VaccineService;
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

public class VaccineListView extends VBox {
    
    private final VaccineService vaccineService;
    private final PetService petService;
    private final ObservableList<Vaccine> vaccines;
    private final FilteredList<Vaccine> filteredVaccines;
    private final TableView<Vaccine> table;
    private TextField searchField;
    private ComboBox<Long> petFilter;
    private Label statusLabel;
    private StackPane toastContainer;
    
    public VaccineListView(VaccineService vaccineService, PetService petService) {
        this.vaccineService = vaccineService;
        this.petService = petService;
        this.vaccines = FXCollections.observableArrayList();
        this.filteredVaccines = new FilteredList<>(vaccines);
        
        setSpacing(15);
        setPadding(new Insets(20));
        getStyleClass().add("pet-list-view");
        
        // Create toast container
        toastContainer = new StackPane();
        toastContainer.setMouseTransparent(true);
        toastContainer.setPickOnBounds(false);
        
        table = createTable();
        VBox controls = createControls();
        HBox toolbar = createToolbar();
        
        VBox content = new VBox(toolbar, controls, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        
        // Wrap in StackPane for toast notifications
        StackPane container = new StackPane(content, toastContainer);
        StackPane.setAlignment(toastContainer, Pos.TOP_CENTER);
        getChildren().add(container);
        
        loadVaccines();
        loadPetsForFilter();
    }
    
    private HBox createToolbar() {
        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(0, 0, 10, 0));
        
        Label title = new Label("Gestión de Vacunas");
        title.getStyleClass().add("title");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button refreshBtn = new Button("Actualizar", new FontIcon(MaterialDesignR.REFRESH));
        refreshBtn.setOnAction(e -> loadVaccines());
        
        Button addBtn = new Button("Agregar Vacuna", new FontIcon(MaterialDesignA.ACCOUNT_PLUS));
        addBtn.getStyleClass().add("primary-button");
        addBtn.setOnAction(e -> showVaccineFormDialog(null));
        
        toolbar.getChildren().addAll(title, spacer, refreshBtn, addBtn);
        return toolbar;
    }
    
    private VBox createControls() {
        VBox controls = new VBox(10);
        controls.setPadding(new Insets(10));
        controls.getStyleClass().add("controls-panel");
        
        HBox searchBox = new HBox(10);
        searchField = new TextField();
        searchField.setPromptText("Buscar por nombre de vacuna...");
        searchField.setPrefWidth(400);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            // Optimistic UI: apply filter immediately
            applyFilters();
        });
        
        Label searchLabel = new Label("Buscar:");
        searchBox.getChildren().addAll(searchLabel, searchField);
        
        HBox filterBox = new HBox(15);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        
        Label petLabel = new Label("Mascota:");
        petFilter = new ComboBox<>();
        petFilter.setPromptText("Todas las Mascotas");
        petFilter.setOnAction(e -> {
            // Optimistic UI: apply filter immediately
            applyFilters();
        });
        
        filterBox.getChildren().addAll(petLabel, petFilter);
        
        this.statusLabel = new Label();
        this.statusLabel.getStyleClass().add("status-label");
        
        controls.getChildren().addAll(searchBox, filterBox, this.statusLabel);
        return controls;
    }
    
    private TableView<Vaccine> createTable() {
        TableView<Vaccine> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tableView.getStyleClass().add("pet-table");
        
        TableColumn<Vaccine, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> {
            Vaccine vaccine = data.getValue();
            return new javafx.beans.property.SimpleObjectProperty<>(vaccine != null ? vaccine.id() : null);
        });
        idCol.setPrefWidth(60);
        
        TableColumn<Vaccine, Long> petIdCol = new TableColumn<>("ID Mascota");
        petIdCol.setCellValueFactory(data -> {
            Vaccine vaccine = data.getValue();
            return new javafx.beans.property.SimpleObjectProperty<>(vaccine != null ? vaccine.petId() : null);
        });
        petIdCol.setPrefWidth(80);
        
        TableColumn<Vaccine, String> nameCol = new TableColumn<>("Nombre de Vacuna");
        nameCol.setCellValueFactory(data -> {
            Vaccine vaccine = data.getValue();
            return new javafx.beans.property.SimpleStringProperty(vaccine != null && vaccine.vaccineName() != null ? vaccine.vaccineName() : "");
        });
        nameCol.setPrefWidth(200);
        
        TableColumn<Vaccine, LocalDate> appDateCol = new TableColumn<>("Fecha de Aplicación");
        appDateCol.setCellValueFactory(data -> {
            Vaccine vaccine = data.getValue();
            return new javafx.beans.property.SimpleObjectProperty<>(vaccine != null ? vaccine.applicationDate() : null);
        });
        appDateCol.setPrefWidth(150);
        appDateCol.setCellFactory(column -> new TableCell<Vaccine, LocalDate>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setText(empty || date == null ? null : date.format(formatter));
            }
        });
        
        TableColumn<Vaccine, LocalDate> nextDoseCol = new TableColumn<>("Próxima Dosis");
        nextDoseCol.setCellValueFactory(data -> {
            Vaccine vaccine = data.getValue();
            return new javafx.beans.property.SimpleObjectProperty<>(vaccine != null ? vaccine.nextDose() : null);
        });
        nextDoseCol.setPrefWidth(150);
        nextDoseCol.setCellFactory(column -> new TableCell<Vaccine, LocalDate>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setText(empty || date == null ? null : date.format(formatter));
            }
        });
        
        TableColumn<Vaccine, String> vetCol = new TableColumn<>("Veterinario");
        vetCol.setCellValueFactory(data -> {
            Vaccine vaccine = data.getValue();
            return new javafx.beans.property.SimpleStringProperty(vaccine != null && vaccine.veterinarian() != null ? vaccine.veterinarian() : "");
        });
        vetCol.setPrefWidth(150);
        
        TableColumn<Vaccine, String> batchCol = new TableColumn<>("Número de Lote");
        batchCol.setCellValueFactory(data -> {
            Vaccine vaccine = data.getValue();
            return new javafx.beans.property.SimpleStringProperty(vaccine != null && vaccine.batchNumber() != null ? vaccine.batchNumber() : "");
        });
        batchCol.setPrefWidth(120);
        
        TableColumn<Vaccine, Void> actionsCol = new TableColumn<>("Acciones");
        actionsCol.setPrefWidth(150);
        actionsCol.setCellFactory(column -> new TableCell<Vaccine, Void>() {
            private final Button editBtn = new Button("", new FontIcon(MaterialDesignP.PENCIL));
            private final Button deleteBtn = new Button("", new FontIcon(MaterialDesignD.DELETE));
            private final HBox buttons = new HBox(5, editBtn, deleteBtn);
            
            {
                editBtn.getStyleClass().add("icon-button");
                deleteBtn.getStyleClass().add("icon-button");
                deleteBtn.getStyleClass().add("danger-button");
                buttons.setAlignment(Pos.CENTER);
                
                editBtn.setOnAction(e -> {
                    Vaccine vaccine = getTableView().getItems().get(getIndex());
                    showVaccineFormDialog(vaccine);
                });
                
                deleteBtn.setOnAction(e -> {
                    Vaccine vaccine = getTableView().getItems().get(getIndex());
                    deleteVaccine(vaccine);
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
        
        tableView.getColumns().addAll(idCol, petIdCol, nameCol, appDateCol, nextDoseCol, vetCol, batchCol, actionsCol);
        
        SortedList<Vaccine> sortedList = new SortedList<>(filteredVaccines);
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedList);
        
        return tableView;
    }
    
    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase();
        Long petId = petFilter.getValue();
        
        filteredVaccines.setPredicate(vaccine -> {
            if (vaccine == null) return false;
            
            boolean matchesSearch = searchText.isEmpty() ||
                    (vaccine.vaccineName() != null && vaccine.vaccineName().toLowerCase().contains(searchText));
            
            boolean matchesPet = petId == null || (vaccine.petId() != null && vaccine.petId().equals(petId));
            
            return matchesSearch && matchesPet;
        });
        
        updateStatusLabel();
    }
    
    private void updateStatusLabel() {
        int total = vaccines.size();
        int filtered = filteredVaccines.size();
        statusLabel.setText(String.format("Mostrando %d de %d vacunas", filtered, total));
    }
    
    private void loadVaccines() {
        statusLabel.setText("Cargando...");
        vaccineService.getAllVaccines()
                .thenAccept(vaccineList -> {
                    Platform.runLater(() -> {
                        if (vaccineList != null && !vaccineList.isEmpty()) {
                            vaccines.clear();
                            vaccines.addAll(vaccineList);
                            applyFilters();
                            statusLabel.setText(String.format("Cargadas %d vacunas", vaccineList.size()));
                        } else {
                            vaccines.clear();
                            applyFilters();
                            statusLabel.setText("No se encontraron vacunas");
                        }
                    });
                })
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        String errorMsg = throwable.getMessage();
                        if (errorMsg == null || errorMsg.isEmpty()) {
                            errorMsg = "No se puede conectar a la API. Por favor verifique que el servidor esté ejecutándose en localhost:8001";
                        }
                        showError("Error al cargar vacunas", errorMsg);
                        statusLabel.setText("Error al cargar vacunas");
                        vaccines.clear();
                        applyFilters();
                    });
                    return null;
                });
    }
    
    private void loadPetsForFilter() {
        petService.getAllPets()
                .thenAccept(pets -> {
                    Platform.runLater(() -> {
                        petFilter.getItems().clear();
                        petFilter.getItems().add(null); // "Todas" option
                        if (pets != null) {
                            pets.forEach(pet -> {
                                if (pet != null && pet.id() != null) {
                                    petFilter.getItems().add(pet.id());
                                }
                            });
                        }
                    });
                });
    }
    
    private void showVaccineFormDialog(Vaccine vaccine) {
        VaccineFormDialog dialog = new VaccineFormDialog(vaccine, petService);
        Optional<VaccineForm> result = dialog.showAndWait();
        
        result.ifPresent(form -> {
            if (vaccine == null) {
                createVaccine(form);
            } else {
                updateVaccine(vaccine.id(), form);
            }
        });
    }
    
    private void createVaccine(VaccineForm form) {
        statusLabel.setText("Creando vacuna...");
        vaccineService.createVaccine(form.toDto())
                .thenAccept(created -> {
                    Platform.runLater(() -> {
                        loadVaccines();
                        showSuccess("¡Vacuna creada exitosamente!");
                    });
                })
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        showError("Error al crear vacuna", throwable.getMessage());
                        statusLabel.setText("Error al crear vacuna");
                    });
                    return null;
                });
    }
    
    private void updateVaccine(Long id, VaccineForm form) {
        statusLabel.setText("Actualizando vacuna...");
        vaccineService.updateVaccine(id, form.toUpdateDto())
                .thenAccept(updated -> {
                    Platform.runLater(() -> {
                        loadVaccines();
                        showSuccess("¡Vacuna actualizada exitosamente!");
                    });
                })
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        showError("Error al actualizar vacuna", throwable.getMessage());
                        statusLabel.setText("Error al actualizar vacuna");
                    });
                    return null;
                });
    }
    
    private void deleteVaccine(Vaccine vaccine) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmar Eliminación");
        confirmDialog.setHeaderText("Eliminar Vacuna");
        confirmDialog.setContentText(String.format("¿Está seguro de que desea eliminar la vacuna %s (ID: %d)?", 
                vaccine.vaccineName(), vaccine.id()));
        
        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            statusLabel.setText("Eliminando vacuna...");
            vaccineService.deleteVaccine(vaccine.id())
                    .thenRun(() -> {
                        Platform.runLater(() -> {
                            loadVaccines();
                            showSuccess("¡Vacuna eliminada exitosamente!");
                        });
                    })
                    .exceptionally(throwable -> {
                        Platform.runLater(() -> {
                            showError("Error al eliminar vacuna", throwable.getMessage());
                            statusLabel.setText("Error al eliminar vacuna");
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

