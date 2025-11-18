package boilerplate.desktop.view;

import boilerplate.desktop.models.AdoptionApplication;
import boilerplate.desktop.models.dto.AdoptionApplicationForm;
import boilerplate.desktop.services.AdoptionApplicationService;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class AdoptionApplicationListView extends VBox {
    
    private final AdoptionApplicationService applicationService;
    private final PetService petService;
    private final ObservableList<AdoptionApplication> applications;
    private final FilteredList<AdoptionApplication> filteredApplications;
    private final TableView<AdoptionApplication> table;
    private TextField searchField;
    private ComboBox<String> statusFilter;
    private ComboBox<Long> petFilter;
    private Label statusLabel;
    private StackPane toastContainer;
    
    public AdoptionApplicationListView(AdoptionApplicationService applicationService, PetService petService) {
        this.applicationService = applicationService;
        this.petService = petService;
        this.applications = FXCollections.observableArrayList();
        this.filteredApplications = new FilteredList<>(applications);
        
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
        
        loadApplications();
        loadPetsForFilter();
    }
    
    private HBox createToolbar() {
        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(0, 0, 10, 0));
        
        Label title = new Label("Gestión de Solicitudes de Adopción");
        title.getStyleClass().add("title");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button refreshBtn = new Button("Actualizar", new FontIcon(MaterialDesignR.REFRESH));
        refreshBtn.setOnAction(e -> loadApplications());
        
        Button addBtn = new Button("Nueva Solicitud", new FontIcon(MaterialDesignA.ACCOUNT_PLUS));
        addBtn.getStyleClass().add("primary-button");
        addBtn.setOnAction(e -> showApplicationFormDialog(null));
        
        toolbar.getChildren().addAll(title, spacer, refreshBtn, addBtn);
        return toolbar;
    }
    
    private VBox createControls() {
        VBox controls = new VBox(10);
        controls.setPadding(new Insets(10));
        controls.getStyleClass().add("controls-panel");
        
        HBox searchBox = new HBox(10);
        searchField = new TextField();
        searchField.setPromptText("Buscar por nombre del solicitante o email...");
        searchField.setPrefWidth(400);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            // Optimistic UI: apply filter immediately
            applyFilters();
        });
        
        Label searchLabel = new Label("Buscar:");
        searchBox.getChildren().addAll(searchLabel, searchField);
        
        HBox filterBox = new HBox(15);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        
        Label statusLabel = new Label("Estado:");
        statusFilter = new ComboBox<>(FXCollections.observableArrayList("Todos", "pendiente", "aprobada", "rechazada"));
        statusFilter.setValue("Todos");
        statusFilter.setOnAction(e -> {
            // Optimistic UI: apply filter immediately
            applyFilters();
        });
        
        Label petLabel = new Label("Mascota:");
        petFilter = new ComboBox<>();
        petFilter.setPromptText("Todas las Mascotas");
        petFilter.setOnAction(e -> {
            // Optimistic UI: apply filter immediately
            applyFilters();
        });
        
        filterBox.getChildren().addAll(statusLabel, statusFilter, petLabel, petFilter);
        
        this.statusLabel = new Label();
        this.statusLabel.getStyleClass().add("status-label");
        
        controls.getChildren().addAll(searchBox, filterBox, this.statusLabel);
        return controls;
    }
    
    private TableView<AdoptionApplication> createTable() {
        TableView<AdoptionApplication> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tableView.getStyleClass().add("pet-table");
        
        TableColumn<AdoptionApplication, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> {
            AdoptionApplication app = data.getValue();
            return new javafx.beans.property.SimpleObjectProperty<>(app != null ? app.id() : null);
        });
        idCol.setPrefWidth(60);
        
        TableColumn<AdoptionApplication, Long> petIdCol = new TableColumn<>("ID Mascota");
        petIdCol.setCellValueFactory(data -> {
            AdoptionApplication app = data.getValue();
            return new javafx.beans.property.SimpleObjectProperty<>(app != null ? app.petId() : null);
        });
        petIdCol.setPrefWidth(80);
        
        TableColumn<AdoptionApplication, String> nameCol = new TableColumn<>("Nombre del Solicitante");
        nameCol.setCellValueFactory(data -> {
            AdoptionApplication app = data.getValue();
            return new javafx.beans.property.SimpleStringProperty(app != null && app.applicantName() != null ? app.applicantName() : "");
        });
        nameCol.setPrefWidth(150);
        
        TableColumn<AdoptionApplication, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(data -> {
            AdoptionApplication app = data.getValue();
            return new javafx.beans.property.SimpleStringProperty(app != null && app.email() != null ? app.email() : "");
        });
        emailCol.setPrefWidth(200);
        
        TableColumn<AdoptionApplication, String> phoneCol = new TableColumn<>("Teléfono");
        phoneCol.setCellValueFactory(data -> {
            AdoptionApplication app = data.getValue();
            return new javafx.beans.property.SimpleStringProperty(app != null && app.phone() != null ? app.phone() : "");
        });
        phoneCol.setPrefWidth(120);
        
        TableColumn<AdoptionApplication, String> housingCol = new TableColumn<>("Vivienda");
        housingCol.setCellValueFactory(data -> {
            AdoptionApplication app = data.getValue();
            String housing = app != null && app.housingType() != null ? app.housingType() : "";
            String display = housing.equals("house") ? "Casa" : 
                           housing.equals("apartment") ? "Apartamento" : 
                           housing.equals("farm") ? "Finca" : housing;
            return new javafx.beans.property.SimpleStringProperty(display);
        });
        housingCol.setPrefWidth(100);
        
        TableColumn<AdoptionApplication, Boolean> otherPetsCol = new TableColumn<>("Tiene Otras Mascotas");
        otherPetsCol.setCellValueFactory(data -> {
            AdoptionApplication app = data.getValue();
            return new javafx.beans.property.SimpleBooleanProperty(app != null ? app.hasOtherPets() : false);
        });
        otherPetsCol.setPrefWidth(120);
        
        TableColumn<AdoptionApplication, String> statusCol = new TableColumn<>("Estado");
        statusCol.setCellValueFactory(data -> {
            AdoptionApplication app = data.getValue();
            return new javafx.beans.property.SimpleStringProperty(app != null && app.status() != null ? app.status() : "");
        });
        statusCol.setPrefWidth(100);
        statusCol.setCellFactory(column -> new TableCell<AdoptionApplication, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    getStyleClass().removeAll("status-pending", "status-approved", "status-rejected");
                } else {
                    String displayText = status.equals("pending") ? "Pendiente" :
                                       status.equals("approved") ? "Aprobada" :
                                       status.equals("rejected") ? "Rechazada" : status;
                    setText(displayText);
                    getStyleClass().removeAll("status-pending", "status-approved", "status-rejected");
                    switch (status.toLowerCase()) {
                        case "pending":
                            getStyleClass().add("status-in-process");
                            break;
                        case "approved":
                            getStyleClass().add("status-available");
                            break;
                        case "rejected":
                            getStyleClass().add("status-adopted");
                            break;
                    }
                }
            }
        });
        
        TableColumn<AdoptionApplication, LocalDateTime> dateCol = new TableColumn<>("Fecha de Solicitud");
        dateCol.setCellValueFactory(data -> {
            AdoptionApplication app = data.getValue();
            return new javafx.beans.property.SimpleObjectProperty<>(app != null ? app.applicationDate() : null);
        });
        dateCol.setPrefWidth(150);
        dateCol.setCellFactory(column -> new TableCell<AdoptionApplication, LocalDateTime>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            
            @Override
            protected void updateItem(LocalDateTime date, boolean empty) {
                super.updateItem(date, empty);
                setText(empty || date == null ? null : date.format(formatter));
            }
        });
        
        TableColumn<AdoptionApplication, Void> actionsCol = new TableColumn<>("Acciones");
        actionsCol.setPrefWidth(200);
        actionsCol.setCellFactory(column -> new TableCell<AdoptionApplication, Void>() {
            private final Button editBtn = new Button("", new FontIcon(MaterialDesignP.PENCIL));
            private final Button approveBtn = new Button("Aprobar");
            private final Button rejectBtn = new Button("Rechazar");
            private final Button deleteBtn = new Button("", new FontIcon(MaterialDesignD.DELETE));
            private final HBox buttons = new HBox(5, editBtn, approveBtn, rejectBtn, deleteBtn);
            
            {
                editBtn.getStyleClass().add("icon-button");
                approveBtn.getStyleClass().add("primary-button");
                approveBtn.setStyle("-fx-padding: 4 8; -fx-font-size: 11px;");
                rejectBtn.getStyleClass().add("danger-button");
                rejectBtn.setStyle("-fx-padding: 4 8; -fx-font-size: 11px;");
                deleteBtn.getStyleClass().add("icon-button");
                deleteBtn.getStyleClass().add("danger-button");
                buttons.setAlignment(Pos.CENTER);
                
                editBtn.setOnAction(e -> {
                    AdoptionApplication app = getTableView().getItems().get(getIndex());
                    showApplicationFormDialog(app);
                });
                
                approveBtn.setOnAction(e -> {
                    AdoptionApplication app = getTableView().getItems().get(getIndex());
                    if (app != null && app.id() != null) {
                        updateStatus(app.id(), "approved");
                    }
                });
                
                rejectBtn.setOnAction(e -> {
                    AdoptionApplication app = getTableView().getItems().get(getIndex());
                    if (app != null && app.id() != null) {
                        updateStatus(app.id(), "rejected");
                    }
                });
                
                deleteBtn.setOnAction(e -> {
                    AdoptionApplication app = getTableView().getItems().get(getIndex());
                    deleteApplication(app);
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
        
        tableView.getColumns().addAll(idCol, petIdCol, nameCol, emailCol, phoneCol, 
                                     housingCol, otherPetsCol, statusCol, dateCol, actionsCol);
        
        SortedList<AdoptionApplication> sortedList = new SortedList<>(filteredApplications);
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedList);
        
        return tableView;
    }
    
    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase();
        String status = statusFilter.getValue();
        Long petId = petFilter.getValue();
        
        filteredApplications.setPredicate(app -> {
            if (app == null) return false;
            
            boolean matchesSearch = searchText.isEmpty() ||
                    (app.applicantName() != null && app.applicantName().toLowerCase().contains(searchText)) ||
                    (app.email() != null && app.email().toLowerCase().contains(searchText));
            
            boolean matchesStatus = "Todos".equals(status) ||
                    (app.status() != null && app.status().equalsIgnoreCase(status)) ||
                    ("pendiente".equals(status) && "pending".equalsIgnoreCase(app.status())) ||
                    ("aprobada".equals(status) && "approved".equalsIgnoreCase(app.status())) ||
                    ("rechazada".equals(status) && "rejected".equalsIgnoreCase(app.status()));
            
            boolean matchesPet = petId == null || (app.petId() != null && app.petId().equals(petId));
            
            return matchesSearch && matchesStatus && matchesPet;
        });
        
        updateStatusLabel();
    }
    
    private void updateStatusLabel() {
        int total = applications.size();
        int filtered = filteredApplications.size();
        statusLabel.setText(String.format("Mostrando %d de %d solicitudes", filtered, total));
    }
    
    private void loadApplications() {
        statusLabel.setText("Cargando...");
        applicationService.getAllApplications()
                .thenAccept(appList -> {
                    Platform.runLater(() -> {
                        if (appList != null && !appList.isEmpty()) {
                            applications.clear();
                            applications.addAll(appList);
                            applyFilters();
                            statusLabel.setText(String.format("Cargadas %d solicitudes", appList.size()));
                        } else {
                            applications.clear();
                            applyFilters();
                            statusLabel.setText("No se encontraron solicitudes");
                        }
                    });
                })
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        String errorMsg = throwable.getMessage();
                        if (errorMsg == null || errorMsg.isEmpty()) {
                            errorMsg = "No se puede conectar a la API. Por favor verifique que el servidor esté ejecutándose en localhost:8001";
                        }
                        showError("Error al cargar solicitudes", errorMsg);
                        statusLabel.setText("Error al cargar solicitudes");
                        applications.clear();
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
                        if (pets != null && !pets.isEmpty()) {
                            pets.forEach(pet -> {
                                if (pet != null && pet.id() != null) {
                                    petFilter.getItems().add(pet.id());
                                }
                            });
                        }
                    });
                })
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        // If loading pets fails, just show empty filter
                        petFilter.getItems().clear();
                        petFilter.getItems().add(null);
                    });
                    return null;
                });
    }
    
    private void showApplicationFormDialog(AdoptionApplication application) {
        AdoptionApplicationFormDialog dialog = new AdoptionApplicationFormDialog(application, petService);
        Optional<AdoptionApplicationForm> result = dialog.showAndWait();
        
        result.ifPresent(form -> {
            if (application == null) {
                createApplication(form);
            } else {
                // For editing, we'd need an update method
                showSuccess("Application updated (update method not implemented)");
            }
        });
    }
    
    private void createApplication(AdoptionApplicationForm form) {
        statusLabel.setText("Creando solicitud...");
        applicationService.createApplication(form.toDto())
                .thenAccept(created -> {
                    Platform.runLater(() -> {
                        loadApplications();
                        showSuccess("¡Solicitud creada exitosamente!");
                    });
                })
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        showError("Error al crear solicitud", throwable.getMessage());
                        statusLabel.setText("Error al crear solicitud");
                    });
                    return null;
                });
    }
    
    private void updateStatus(Long id, String status) {
        statusLabel.setText("Actualizando estado...");
        applicationService.updateApplicationStatus(id, status)
                .thenAccept(updated -> {
                    Platform.runLater(() -> {
                        loadApplications();
                        showSuccess("¡Estado actualizado exitosamente!");
                    });
                })
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        showError("Error al actualizar estado", throwable.getMessage());
                        statusLabel.setText("Error al actualizar estado");
                    });
                    return null;
                });
    }
    
    private void deleteApplication(AdoptionApplication application) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmar Eliminación");
        confirmDialog.setHeaderText("Eliminar Solicitud");
        confirmDialog.setContentText(String.format("¿Está seguro de que desea eliminar la solicitud de %s (ID: %d)?", 
                application.applicantName(), application.id()));
        
        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            statusLabel.setText("Eliminando solicitud...");
            applicationService.deleteApplication(application.id())
                    .thenRun(() -> {
                        Platform.runLater(() -> {
                            loadApplications();
                            showSuccess("¡Solicitud eliminada exitosamente!");
                        });
                    })
                    .exceptionally(throwable -> {
                        Platform.runLater(() -> {
                            showError("Error al eliminar solicitud", throwable.getMessage());
                            statusLabel.setText("Error al eliminar solicitud");
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

