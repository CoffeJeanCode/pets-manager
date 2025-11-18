package boilerplate.desktop.view;

import boilerplate.desktop.models.Pet;
import boilerplate.desktop.models.dto.PetDto;
import boilerplate.desktop.models.dto.PetForm;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class PetFormDialog extends Dialog<PetForm> {
    
    private final PetForm form;
    private final boolean isEdit;
    
    public PetFormDialog(Pet pet) {
        this.isEdit = pet != null;
        this.form = pet != null ? new PetForm(convertToDto(pet)) : new PetForm();
        
        setTitle(isEdit ? "Edit Pet" : "Add New Pet");
        setHeaderText(isEdit ? "Edit pet information" : "Enter pet information");
        
        DialogPane dialogPane = getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialogPane.getStyleClass().add("dialog-pane");
        dialogPane.setContent(createForm());
        
        // Validation
        Node okButton = dialogPane.lookupButton(ButtonType.OK);
        okButton.setDisable(!isValid());
        
        // Add listeners for validation
        form.nameProperty().addListener((obs, oldVal, newVal) -> validateForm(okButton));
        form.speciesProperty().addListener((obs, oldVal, newVal) -> validateForm(okButton));
        
        setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return form;
            }
            return null;
        });
    }
    
    private PetDto convertToDto(Pet pet) {
        return new PetDto(
            pet.id(), pet.name(), pet.species(), pet.breed(), pet.approxAge(),
            pet.approxBirthdate(), pet.sex(), pet.size(), pet.weight(),
            pet.adoptionStatus(), pet.healthStatus(), pet.currentLocation(),
            pet.microchip(), pet.description(), pet.specialNeeds(),
            pet.intakeDate(), pet.photoUrl(), pet.createdAt(), pet.updatedAt()
        );
    }
    
    private GridPane createForm() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(18);
        grid.setPadding(new Insets(28));
        grid.getStyleClass().add("form-grid");
        
        int row = 0;
        
        // Name
        Label nameLabel = new Label("Name *");
        nameLabel.getStyleClass().add("form-label");
        grid.add(nameLabel, 0, row);
        TextField nameField = new TextField();
        nameField.textProperty().bindBidirectional(form.nameProperty());
        nameField.setPromptText("Enter pet name");
        nameField.getStyleClass().add("form-input");
        grid.add(nameField, 1, row);
        
        // Species
        grid.add(new Label("Species *:"), 0, ++row);
        ComboBox<String> speciesCombo = new ComboBox<>();
        speciesCombo.getItems().addAll("Dog", "Cat", "Bird", "Rabbit", "Other");
        speciesCombo.valueProperty().bindBidirectional(form.speciesProperty());
        speciesCombo.setEditable(true);
        grid.add(speciesCombo, 1, row);
        
        // Breed
        grid.add(new Label("Breed:"), 0, ++row);
        TextField breedField = new TextField();
        breedField.textProperty().bindBidirectional(form.breedProperty());
        breedField.setPromptText("Enter breed");
        grid.add(breedField, 1, row);
        
        // Age
        grid.add(new Label("Approximate Age:"), 0, ++row);
        Spinner<Integer> ageSpinner = new Spinner<>(0, 30, form.getApproxAge() != null ? form.getApproxAge() : 0);
        ageSpinner.getValueFactory().valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) form.setApproxAge(newVal);
        });
        form.approxAgeProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.equals(ageSpinner.getValue())) {
                ageSpinner.getValueFactory().setValue(newVal.intValue());
            }
        });
        grid.add(ageSpinner, 1, row);
        
        // Birthdate
        grid.add(new Label("Approximate Birthdate:"), 0, ++row);
        DatePicker birthdatePicker = new DatePicker();
        birthdatePicker.valueProperty().bindBidirectional(form.approxBirthdateProperty());
        grid.add(birthdatePicker, 1, row);
        
        // Sex
        grid.add(new Label("Sex:"), 0, ++row);
        ComboBox<String> sexCombo = new ComboBox<>();
        sexCombo.getItems().addAll("male", "female");
        sexCombo.valueProperty().bindBidirectional(form.sexProperty());
        grid.add(sexCombo, 1, row);
        
        // Size
        grid.add(new Label("Size:"), 0, ++row);
        ComboBox<String> sizeCombo = new ComboBox<>();
        sizeCombo.getItems().addAll("small", "medium", "large");
        sizeCombo.valueProperty().bindBidirectional(form.sizeProperty());
        grid.add(sizeCombo, 1, row);
        
        // Weight
        grid.add(new Label("Weight:"), 0, ++row);
        TextField weightField = new TextField();
        weightField.textProperty().bindBidirectional(form.weightProperty());
        weightField.setPromptText("e.g., 5.5 kg");
        grid.add(weightField, 1, row);
        
        // Health Status
        grid.add(new Label("Health Status:"), 0, ++row);
        ComboBox<String> healthCombo = new ComboBox<>();
        healthCombo.getItems().addAll("healthy", "under_treatment", "recovering", "chronic_condition");
        healthCombo.valueProperty().bindBidirectional(form.healthStatusProperty());
        grid.add(healthCombo, 1, row);
        
        // Current Location
        grid.add(new Label("Current Location:"), 0, ++row);
        TextField locationField = new TextField();
        locationField.textProperty().bindBidirectional(form.currentLocationProperty());
        locationField.setPromptText("e.g., Kennel A-5");
        grid.add(locationField, 1, row);
        
        // Microchip
        grid.add(new Label("Microchip:"), 0, ++row);
        TextField microchipField = new TextField();
        microchipField.textProperty().bindBidirectional(form.microchipProperty());
        microchipField.setPromptText("Microchip number");
        grid.add(microchipField, 1, row);
        
        // Intake Date
        grid.add(new Label("Intake Date:"), 0, ++row);
        DatePicker intakePicker = new DatePicker();
        intakePicker.valueProperty().bindBidirectional(form.intakeDateProperty());
        grid.add(intakePicker, 1, row);
        
        // Photo URL
        grid.add(new Label("Photo URL:"), 0, ++row);
        TextField photoUrlField = new TextField();
        photoUrlField.textProperty().bindBidirectional(form.photoUrlProperty());
        photoUrlField.setPromptText("URL to pet photo");
        grid.add(photoUrlField, 1, row);
        
        // Description
        grid.add(new Label("Description:"), 0, ++row);
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPrefRowCount(3);
        descriptionArea.textProperty().bindBidirectional(form.descriptionProperty());
        descriptionArea.setPromptText("Enter pet description");
        grid.add(descriptionArea, 1, row);
        
        // Special Needs
        grid.add(new Label("Special Needs:"), 0, ++row);
        TextArea specialNeedsArea = new TextArea();
        specialNeedsArea.setPrefRowCount(2);
        specialNeedsArea.textProperty().bindBidirectional(form.specialNeedsProperty());
        specialNeedsArea.setPromptText("Any special care requirements");
        grid.add(specialNeedsArea, 1, row);
        
        // Set column constraints for better layout
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
                } else if (node instanceof Spinner) {
                    node.getStyleClass().add("form-input");
                }
            }
        });
        
        return grid;
    }
    
    private boolean isValid() {
        return form.getName() != null && !form.getName().trim().isEmpty() &&
               form.getSpecies() != null && !form.getSpecies().trim().isEmpty();
    }
    
    private void validateForm(Node okButton) {
        okButton.setDisable(!isValid());
    }
}

