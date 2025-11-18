package boilerplate.desktop.view;

import boilerplate.desktop.models.Donation;
import boilerplate.desktop.models.dto.DonationForm;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;


public class DonationFormDialog extends Dialog<DonationForm> {
    
    private final DonationForm form;
    private final boolean isEdit;
    
    public DonationFormDialog(Donation donation) {
        this.isEdit = donation != null;
        this.form = donation != null ? new DonationForm(donation) : new DonationForm();
        
        setTitle(isEdit ? "Edit Donation" : "Add New Donation");
        setHeaderText(isEdit ? "Edit donation information" : "Enter donation information");
        
        DialogPane dialogPane = getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialogPane.getStyleClass().add("dialog-pane");
        dialogPane.setContent(createForm());
        
        Node okButton = dialogPane.lookupButton(ButtonType.OK);
        okButton.setDisable(!isValid());
        
        form.donorNameProperty().addListener((obs, oldVal, newVal) -> validateForm(okButton));
        form.donorEmailProperty().addListener((obs, oldVal, newVal) -> validateForm(okButton));
        
        setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return form;
            }
            return null;
        });
    }
    
    private GridPane createForm() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(18);
        grid.setPadding(new Insets(28));
        grid.getStyleClass().add("form-grid");
        
        int row = 0;
        
        // Donor Name
        grid.add(new Label("Donor Name *:"), 0, row);
        TextField nameField = new TextField();
        nameField.textProperty().bindBidirectional(form.donorNameProperty());
        nameField.setPromptText("Enter donor name");
        grid.add(nameField, 1, row);
        
        // Donor Email
        grid.add(new Label("Donor Email *:"), 0, ++row);
        TextField emailField = new TextField();
        emailField.textProperty().bindBidirectional(form.donorEmailProperty());
        emailField.setPromptText("Enter email address");
        grid.add(emailField, 1, row);
        
        // Donation Type
        grid.add(new Label("Donation Type:"), 0, ++row);
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("monetary", "food", "medicine", "toys");
        typeCombo.valueProperty().bindBidirectional(form.donationTypeProperty());
        grid.add(typeCombo, 1, row);
        
        // Amount
        grid.add(new Label("Amount:"), 0, ++row);
        TextField amountField = new TextField();
        amountField.textProperty().bindBidirectional(form.amountProperty());
        amountField.setPromptText("e.g., 100.00");
        grid.add(amountField, 1, row);
        
        // Amount Received
        grid.add(new Label("Amount Received:"), 0, ++row);
        TextField receivedField = new TextField();
        receivedField.textProperty().bindBidirectional(form.amountReceivedProperty());
        receivedField.setPromptText("e.g., 100.00");
        grid.add(receivedField, 1, row);
        
        // Payment Method
        grid.add(new Label("Payment Method:"), 0, ++row);
        TextField paymentField = new TextField();
        paymentField.textProperty().bindBidirectional(form.paymentMethodProperty());
        paymentField.setPromptText("e.g., Credit Card, Cash, Bank Transfer");
        grid.add(paymentField, 1, row);
        
        // Transaction Reference
        grid.add(new Label("Transaction Reference:"), 0, ++row);
        TextField refField = new TextField();
        refField.textProperty().bindBidirectional(form.transactionReferenceProperty());
        refField.setPromptText("Transaction ID or reference");
        grid.add(refField, 1, row);
        
        // Donation Date
        grid.add(new Label("Donation Date:"), 0, ++row);
        DatePicker datePicker = new DatePicker();
        if (form.getDonationDate() != null) {
            datePicker.setValue(form.getDonationDate().toLocalDate());
        }
        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                form.setDonationDate(newVal.atStartOfDay());
            }
        });
        grid.add(datePicker, 1, row);
        
        // Description
        grid.add(new Label("Description:"), 0, ++row);
        TextArea descArea = new TextArea();
        descArea.setPrefRowCount(3);
        descArea.textProperty().bindBidirectional(form.descriptionProperty());
        descArea.setPromptText("Additional details about the donation");
        grid.add(descArea, 1, row);
        
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
    
    private boolean isValid() {
        return form.getDonorName() != null && !form.getDonorName().trim().isEmpty() &&
               form.getDonorEmail() != null && !form.getDonorEmail().trim().isEmpty();
    }
    
    private void validateForm(Node okButton) {
        okButton.setDisable(!isValid());
    }
}

