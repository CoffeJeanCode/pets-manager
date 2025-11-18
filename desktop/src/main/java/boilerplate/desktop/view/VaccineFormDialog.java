package boilerplate.desktop.view;

import boilerplate.desktop.models.Vaccine;
import boilerplate.desktop.models.dto.VaccineForm;
import boilerplate.desktop.services.PetService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class VaccineFormDialog extends Dialog<VaccineForm> {
    
    private final VaccineForm form;
    private final boolean isEdit;
    private ComboBox<Long> petCombo;
    
    public VaccineFormDialog(Vaccine vaccine, PetService petService) {
        this.isEdit = vaccine != null;
        this.form = vaccine != null ? new VaccineForm(vaccine) : new VaccineForm();
        
        setTitle(isEdit ? "Edit Vaccine" : "Add New Vaccine");
        setHeaderText(isEdit ? "Edit vaccine information" : "Enter vaccine information");
        
        DialogPane dialogPane = getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialogPane.getStyleClass().add("dialog-pane");
        dialogPane.setContent(createForm(petService));
        
        // Load pets for combo box
        loadPets(petService);
        
        // Validation
        Node okButton = dialogPane.lookupButton(ButtonType.OK);
        okButton.setDisable(!isValid());
        
        form.vaccineNameProperty().addListener((obs, oldVal, newVal) -> validateForm(okButton));
        form.petIdProperty().addListener((obs, oldVal, newVal) -> validateForm(okButton));
        
        setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return form;
            }
            return null;
        });
        
    }
    
    private GridPane createForm(PetService petService) {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(18);
        grid.setPadding(new Insets(28));
        grid.getStyleClass().add("form-grid");
        
        int row = 0;
        
        // Pet ID
        grid.add(new Label("Pet *:"), 0, row);
        ComboBox<Long> petCombo = new ComboBox<>();
        petCombo.setPromptText("Select a pet");
        petCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) form.setPetId(newVal);
        });
        form.petIdProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.longValue() != 0) {
                Long currentValue = petCombo.getValue();
                if (currentValue == null || !currentValue.equals(newVal.longValue())) {
                    petCombo.setValue(newVal.longValue());
                }
            }
        });
        this.petCombo = petCombo;
        grid.add(petCombo, 1, row);
        
        // Vaccine Name
        grid.add(new Label("Vaccine Name *:"), 0, ++row);
        TextField nameField = new TextField();
        nameField.textProperty().bindBidirectional(form.vaccineNameProperty());
        nameField.setPromptText("Enter vaccine name");
        grid.add(nameField, 1, row);
        
        // Application Date
        grid.add(new Label("Application Date:"), 0, ++row);
        DatePicker appDatePicker = new DatePicker();
        appDatePicker.valueProperty().bindBidirectional(form.applicationDateProperty());
        grid.add(appDatePicker, 1, row);
        
        // Next Dose
        grid.add(new Label("Next Dose:"), 0, ++row);
        DatePicker nextDosePicker = new DatePicker();
        nextDosePicker.valueProperty().bindBidirectional(form.nextDoseProperty());
        grid.add(nextDosePicker, 1, row);
        
        // Veterinarian
        grid.add(new Label("Veterinarian:"), 0, ++row);
        TextField vetField = new TextField();
        vetField.textProperty().bindBidirectional(form.veterinarianProperty());
        vetField.setPromptText("Veterinarian name");
        grid.add(vetField, 1, row);
        
        // Batch Number
        grid.add(new Label("Batch Number:"), 0, ++row);
        TextField batchField = new TextField();
        batchField.textProperty().bindBidirectional(form.batchNumberProperty());
        batchField.setPromptText("Batch number");
        grid.add(batchField, 1, row);
        
        // Notes
        grid.add(new Label("Notes:"), 0, ++row);
        TextArea notesArea = new TextArea();
        notesArea.setPrefRowCount(3);
        notesArea.textProperty().bindBidirectional(form.notesProperty());
        notesArea.setPromptText("Additional notes");
        grid.add(notesArea, 1, row);
        
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(160);
        col1.setPrefWidth(160);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        col2.setMinWidth(300);
        grid.getColumnConstraints().addAll(col1, col2);
        
        // Apply styles to all form elements
        grid.getChildren().forEach(node -> {
            Integer colIndex = GridPane.getColumnIndex(node);
            if (colIndex == null) colIndex = 0;
            
            if (node instanceof Label && colIndex == 0) {
                if (!node.getStyleClass().contains("form-label")) {
                    node.getStyleClass().add("form-label");
                }
            } else if (colIndex == 1) {
                if (node instanceof TextField && !node.getStyleClass().contains("form-input")) {
                    node.getStyleClass().add("form-input");
                } else if (node instanceof ComboBox) {
                    node.getStyleClass().add("form-input");
                } else if (node instanceof DatePicker) {
                    node.getStyleClass().add("form-input");
                } else if (node instanceof TextArea) {
                    node.getStyleClass().add("form-input");
                }
            }
        });
        
        return grid;
    }
    
    private void loadPets(PetService petService) {
        petService.getAllPets()
                .thenAccept(pets -> {
                    Platform.runLater(() -> {
                        petCombo.getItems().clear();
                        pets.forEach(pet -> petCombo.getItems().add(pet.id()));
                        if (form.getPetId() != null) {
                            petCombo.setValue(form.getPetId());
                        }
                    });
                });
    }
    
    private boolean isValid() {
        return form.getVaccineName() != null && !form.getVaccineName().trim().isEmpty() &&
               form.getPetId() != null && form.getPetId() > 0;
    }
    
    private void validateForm(Node okButton) {
        okButton.setDisable(!isValid());
    }
}

