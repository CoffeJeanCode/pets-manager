package boilerplate.desktop.view;

import boilerplate.desktop.models.AdoptionApplication;
import boilerplate.desktop.models.dto.AdoptionApplicationForm;
import boilerplate.desktop.services.PetService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class AdoptionApplicationFormDialog extends Dialog<AdoptionApplicationForm> {
    
    private final AdoptionApplicationForm form;
    private final boolean isEdit;
    private ComboBox<Long> petCombo;
    
    public AdoptionApplicationFormDialog(AdoptionApplication application, PetService petService) {
        this.isEdit = application != null;
        this.form = application != null ? new AdoptionApplicationForm(application) : new AdoptionApplicationForm();
        
        setTitle(isEdit ? "Edit Application" : "New Adoption Application");
        setHeaderText(isEdit ? "Edit application information" : "Enter application information");
        
        DialogPane dialogPane = getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialogPane.getStyleClass().add("dialog-pane");
        dialogPane.setContent(createForm(petService));
        
        loadPets(petService);
        
        Node okButton = dialogPane.lookupButton(ButtonType.OK);
        okButton.setDisable(!isValid());
        
        form.applicantNameProperty().addListener((obs, oldVal, newVal) -> validateForm(okButton));
        form.emailProperty().addListener((obs, oldVal, newVal) -> validateForm(okButton));
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
        
        // Applicant Name
        grid.add(new Label("Applicant Name *:"), 0, ++row);
        TextField nameField = new TextField();
        nameField.textProperty().bindBidirectional(form.applicantNameProperty());
        nameField.setPromptText("Enter full name");
        grid.add(nameField, 1, row);
        
        // ID Number
        grid.add(new Label("ID Number:"), 0, ++row);
        TextField idField = new TextField();
        idField.textProperty().bindBidirectional(form.applicantIdNumberProperty());
        idField.setPromptText("National ID or passport");
        grid.add(idField, 1, row);
        
        // Occupation
        grid.add(new Label("Occupation:"), 0, ++row);
        TextField occField = new TextField();
        occField.textProperty().bindBidirectional(form.occupationProperty());
        grid.add(occField, 1, row);
        
        // Email
        grid.add(new Label("Email *:"), 0, ++row);
        TextField emailField = new TextField();
        emailField.textProperty().bindBidirectional(form.emailProperty());
        emailField.setPromptText("email@example.com");
        grid.add(emailField, 1, row);
        
        // Phone
        grid.add(new Label("Phone:"), 0, ++row);
        TextField phoneField = new TextField();
        phoneField.textProperty().bindBidirectional(form.phoneProperty());
        grid.add(phoneField, 1, row);
        
        // Address
        grid.add(new Label("Address:"), 0, ++row);
        TextArea addressArea = new TextArea();
        addressArea.setPrefRowCount(2);
        addressArea.textProperty().bindBidirectional(form.addressProperty());
        grid.add(addressArea, 1, row);
        
        // Housing Type
        grid.add(new Label("Housing Type:"), 0, ++row);
        ComboBox<String> housingCombo = new ComboBox<>();
        housingCombo.getItems().addAll("house", "apartment", "farm");
        housingCombo.valueProperty().bindBidirectional(form.housingTypeProperty());
        grid.add(housingCombo, 1, row);
        
        // Has Other Pets
        grid.add(new Label("Has Other Pets:"), 0, ++row);
        CheckBox otherPetsCheck = new CheckBox();
        otherPetsCheck.selectedProperty().bindBidirectional(form.hasOtherPetsProperty());
        grid.add(otherPetsCheck, 1, row);
        
        // Adoption Reason
        grid.add(new Label("Adoption Reason:"), 0, ++row);
        TextArea reasonArea = new TextArea();
        reasonArea.setPrefRowCount(2);
        reasonArea.textProperty().bindBidirectional(form.adoptionReasonProperty());
        grid.add(reasonArea, 1, row);
        
        // Pet Experience
        grid.add(new Label("Pet Experience:"), 0, ++row);
        TextArea expArea = new TextArea();
        expArea.setPrefRowCount(2);
        expArea.textProperty().bindBidirectional(form.petExperienceProperty());
        grid.add(expArea, 1, row);
        
        // Status (only for editing)
        if (isEdit) {
            grid.add(new Label("Status:"), 0, ++row);
            ComboBox<String> statusCombo = new ComboBox<>();
            statusCombo.getItems().addAll("pending", "approved", "rejected");
            statusCombo.valueProperty().bindBidirectional(form.statusProperty());
            grid.add(statusCombo, 1, row);
        }
        
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
        return form.getApplicantName() != null && !form.getApplicantName().trim().isEmpty() &&
               form.getEmail() != null && !form.getEmail().trim().isEmpty() &&
               form.getPetId() != null && form.getPetId() > 0;
    }
    
    private void validateForm(Node okButton) {
        okButton.setDisable(!isValid());
    }
}

