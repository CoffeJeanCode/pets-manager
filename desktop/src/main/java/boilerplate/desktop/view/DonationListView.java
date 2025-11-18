package boilerplate.desktop.view;

import boilerplate.desktop.models.Donation;
import boilerplate.desktop.models.dto.DonationForm;
import boilerplate.desktop.services.DonationService;
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

public class DonationListView extends VBox {
    
    private final DonationService donationService;
    private final ObservableList<Donation> donations;
    private final FilteredList<Donation> filteredDonations;
    private final TableView<Donation> table;
    private TextField searchField;
    private ComboBox<String> typeFilter;
    private Label statusLabel;
    private Label totalLabel;
    private StackPane toastContainer;
    
    public DonationListView(DonationService donationService) {
        this.donationService = donationService;
        this.donations = FXCollections.observableArrayList();
        this.filteredDonations = new FilteredList<>(donations);
        
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
        
        loadDonations();
        loadTotal();
    }
    
    private HBox createToolbar() {
        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(0, 0, 10, 0));
        
        Label title = new Label("Gestión de Donaciones");
        title.getStyleClass().add("title");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button refreshBtn = new Button("Actualizar", new FontIcon(MaterialDesignR.REFRESH));
        refreshBtn.setOnAction(e -> {
            loadDonations();
            loadTotal();
        });
        
        Button addBtn = new Button("Agregar Donación", new FontIcon(MaterialDesignA.ACCOUNT_PLUS));
        addBtn.getStyleClass().add("primary-button");
        addBtn.setOnAction(e -> showDonationFormDialog(null));
        
        toolbar.getChildren().addAll(title, spacer, refreshBtn, addBtn);
        return toolbar;
    }
    
    private VBox createControls() {
        VBox controls = new VBox(10);
        controls.setPadding(new Insets(10));
        controls.getStyleClass().add("controls-panel");
        
        HBox searchBox = new HBox(10);
        searchField = new TextField();
        searchField.setPromptText("Buscar por nombre del donante o email...");
        searchField.setPrefWidth(400);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            // Optimistic UI: apply filter immediately
            applyFilters();
        });
        
        Label searchLabel = new Label("Buscar:");
        searchBox.getChildren().addAll(searchLabel, searchField);
        
        HBox filterBox = new HBox(15);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        
        Label typeLabel = new Label("Tipo:");
        typeFilter = new ComboBox<>(FXCollections.observableArrayList("Todas", "Monetaria", "Comida", "Medicina", "Juguetes"));
        typeFilter.setValue("Todas");
        typeFilter.setOnAction(e -> {
            // Optimistic UI: apply filter immediately
            applyFilters();
        });
        
        filterBox.getChildren().addAll(typeLabel, typeFilter);
        
        HBox statusBox = new HBox(15);
        this.statusLabel = new Label();
        this.statusLabel.getStyleClass().add("status-label");
        
        this.totalLabel = new Label();
        this.totalLabel.getStyleClass().add("status-label");
        this.totalLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #4CAF50;");
        
        statusBox.getChildren().addAll(this.statusLabel, this.totalLabel);
        
        controls.getChildren().addAll(searchBox, filterBox, statusBox);
        return controls;
    }
    
    private TableView<Donation> createTable() {
        TableView<Donation> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tableView.getStyleClass().add("pet-table");
        
        TableColumn<Donation, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> {
            Donation donation = data.getValue();
            return new javafx.beans.property.SimpleObjectProperty<>(donation != null ? donation.id() : null);
        });
        idCol.setPrefWidth(60);
        
        TableColumn<Donation, String> donorCol = new TableColumn<>("Nombre del Donante");
        donorCol.setCellValueFactory(data -> {
            Donation donation = data.getValue();
            return new javafx.beans.property.SimpleStringProperty(donation != null && donation.donorName() != null ? donation.donorName() : "");
        });
        donorCol.setPrefWidth(150);
        
        TableColumn<Donation, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(data -> {
            Donation donation = data.getValue();
            return new javafx.beans.property.SimpleStringProperty(donation != null && donation.donorEmail() != null ? donation.donorEmail() : "");
        });
        emailCol.setPrefWidth(200);
        
        TableColumn<Donation, String> typeCol = new TableColumn<>("Tipo");
        typeCol.setCellValueFactory(data -> {
            Donation donation = data.getValue();
            String type = donation != null && donation.donationType() != null ? donation.donationType() : "";
            String display = type.equals("monetary") ? "Monetaria" :
                           type.equals("food") ? "Comida" :
                           type.equals("medicine") ? "Medicina" :
                           type.equals("toys") ? "Juguetes" : type;
            return new javafx.beans.property.SimpleStringProperty(display);
        });
        typeCol.setPrefWidth(100);
        
        TableColumn<Donation, String> amountCol = new TableColumn<>("Monto");
        amountCol.setCellValueFactory(data -> {
            Donation donation = data.getValue();
            return new javafx.beans.property.SimpleStringProperty(donation != null && donation.amount() != null ? donation.amount() : "");
        });
        amountCol.setPrefWidth(100);
        
        TableColumn<Donation, String> receivedCol = new TableColumn<>("Recibido");
        receivedCol.setCellValueFactory(data -> {
            Donation donation = data.getValue();
            return new javafx.beans.property.SimpleStringProperty(donation != null && donation.amountReceived() != null ? donation.amountReceived() : "");
        });
        receivedCol.setPrefWidth(100);
        
        TableColumn<Donation, LocalDateTime> dateCol = new TableColumn<>("Fecha");
        dateCol.setCellValueFactory(data -> {
            Donation donation = data.getValue();
            return new javafx.beans.property.SimpleObjectProperty<>(donation != null ? donation.donationDate() : null);
        });
        dateCol.setPrefWidth(150);
        dateCol.setCellFactory(column -> new TableCell<Donation, LocalDateTime>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            
            @Override
            protected void updateItem(LocalDateTime date, boolean empty) {
                super.updateItem(date, empty);
                setText(empty || date == null ? null : date.format(formatter));
            }
        });
        
        TableColumn<Donation, Void> actionsCol = new TableColumn<>("Acciones");
        actionsCol.setPrefWidth(150);
        actionsCol.setCellFactory(column -> new TableCell<Donation, Void>() {
            private final Button editBtn = new Button("", new FontIcon(MaterialDesignP.PENCIL));
            private final Button deleteBtn = new Button("", new FontIcon(MaterialDesignD.DELETE));
            private final HBox buttons = new HBox(5, editBtn, deleteBtn);
            
            {
                editBtn.getStyleClass().add("icon-button");
                deleteBtn.getStyleClass().add("icon-button");
                deleteBtn.getStyleClass().add("danger-button");
                buttons.setAlignment(Pos.CENTER);
                
                editBtn.setOnAction(e -> {
                    Donation donation = getTableView().getItems().get(getIndex());
                    showDonationFormDialog(donation);
                });
                
                deleteBtn.setOnAction(e -> {
                    Donation donation = getTableView().getItems().get(getIndex());
                    deleteDonation(donation);
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
        
        tableView.getColumns().addAll(idCol, donorCol, emailCol, typeCol, amountCol, receivedCol, dateCol, actionsCol);
        
        SortedList<Donation> sortedList = new SortedList<>(filteredDonations);
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedList);
        
        return tableView;
    }
    
    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase();
        String type = typeFilter.getValue();
        
        filteredDonations.setPredicate(donation -> {
            if (donation == null) return false;
            
            boolean matchesSearch = searchText.isEmpty() ||
                    (donation.donorName() != null && donation.donorName().toLowerCase().contains(searchText)) ||
                    (donation.donorEmail() != null && donation.donorEmail().toLowerCase().contains(searchText));
            
            boolean matchesType = "Todas".equals(type) ||
                    (donation.donationType() != null && donation.donationType().equalsIgnoreCase(type)) ||
                    ("Monetaria".equals(type) && "monetary".equalsIgnoreCase(donation.donationType())) ||
                    ("Comida".equals(type) && "food".equalsIgnoreCase(donation.donationType())) ||
                    ("Medicina".equals(type) && "medicine".equalsIgnoreCase(donation.donationType())) ||
                    ("Juguetes".equals(type) && "toys".equalsIgnoreCase(donation.donationType()));
            
            return matchesSearch && matchesType;
        });
        
        updateStatusLabel();
    }
    
    private void updateStatusLabel() {
        int total = donations.size();
        int filtered = filteredDonations.size();
        statusLabel.setText(String.format("Mostrando %d de %d donaciones", filtered, total));
    }
    
    private void loadDonations() {
        statusLabel.setText("Cargando...");
        donationService.getAllDonations()
                .thenAccept(donationList -> {
                    Platform.runLater(() -> {
                        if (donationList != null && !donationList.isEmpty()) {
                            donations.clear();
                            donations.addAll(donationList);
                            applyFilters();
                            statusLabel.setText(String.format("Cargadas %d donaciones", donationList.size()));
                        } else {
                            donations.clear();
                            applyFilters();
                            statusLabel.setText("No se encontraron donaciones");
                        }
                    });
                })
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        String errorMsg = throwable.getMessage();
                        if (errorMsg == null || errorMsg.isEmpty()) {
                            errorMsg = "No se puede conectar a la API. Por favor verifique que el servidor esté ejecutándose en localhost:8001";
                        }
                        showError("Error al cargar donaciones", errorMsg);
                        statusLabel.setText("Error al cargar donaciones");
                        donations.clear();
                        applyFilters();
                    });
                    return null;
                });
    }
    
    private void loadTotal() {
        donationService.getTotalDonations()
                .thenAccept(total -> {
                    Platform.runLater(() -> {
                        totalLabel.setText(String.format("Total Donaciones: $%.2f", total));
                    });
                })
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        totalLabel.setText("Total: Error al cargar");
                    });
                    return null;
                });
    }
    
    private void showDonationFormDialog(Donation donation) {
        DonationFormDialog dialog = new DonationFormDialog(donation);
        Optional<DonationForm> result = dialog.showAndWait();
        
        result.ifPresent(form -> {
            if (donation == null) {
                createDonation(form);
            } else {
                updateDonation(donation.id(), form);
            }
        });
    }
    
    private void createDonation(DonationForm form) {
        statusLabel.setText("Creando donación...");
        donationService.createDonation(form.toDto())
                .thenAccept(created -> {
                    Platform.runLater(() -> {
                        loadDonations();
                        loadTotal();
                        showSuccess("¡Donación creada exitosamente!");
                    });
                })
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        showError("Error al crear donación", throwable.getMessage());
                        statusLabel.setText("Error al crear donación");
                    });
                    return null;
                });
    }
    
    private void updateDonation(Long id, DonationForm form) {
        statusLabel.setText("Actualizando donación...");
        donationService.updateDonation(id, form.toUpdateDto())
                .thenAccept(updated -> {
                    Platform.runLater(() -> {
                        loadDonations();
                        loadTotal();
                        showSuccess("¡Donación actualizada exitosamente!");
                    });
                })
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        showError("Error al actualizar donación", throwable.getMessage());
                        statusLabel.setText("Error al actualizar donación");
                    });
                    return null;
                });
    }
    
    private void deleteDonation(Donation donation) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmar Eliminación");
        confirmDialog.setHeaderText("Eliminar Donación");
        confirmDialog.setContentText(String.format("¿Está seguro de que desea eliminar la donación de %s (ID: %d)?", 
                donation.donorName(), donation.id()));
        
        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            statusLabel.setText("Eliminando donación...");
            donationService.deleteDonation(donation.id())
                    .thenRun(() -> {
                        Platform.runLater(() -> {
                            loadDonations();
                            loadTotal();
                            showSuccess("¡Donación eliminada exitosamente!");
                        });
                    })
                    .exceptionally(throwable -> {
                        Platform.runLater(() -> {
                            showError("Error al eliminar donación", throwable.getMessage());
                            statusLabel.setText("Error al eliminar donación");
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

