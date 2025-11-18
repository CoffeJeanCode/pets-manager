package boilerplate.desktop.models.dto;

import boilerplate.desktop.models.Donation;
import javafx.beans.property.*;
import java.time.LocalDateTime;

public class DonationForm {
    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty donorName = new SimpleStringProperty();
    private final StringProperty donorEmail = new SimpleStringProperty();
    private final StringProperty donationType = new SimpleStringProperty();
    private final StringProperty amount = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty paymentMethod = new SimpleStringProperty();
    private final StringProperty transactionReference = new SimpleStringProperty();
    private final StringProperty amountReceived = new SimpleStringProperty();
    private final ObjectProperty<LocalDateTime> donationDate = new SimpleObjectProperty<>(LocalDateTime.now());
    
    public DonationForm() {}
    
    public DonationForm(Donation donation) {
        if (donation != null) {
            this.id.set(donation.id());
            this.donorName.set(donation.donorName());
            this.donorEmail.set(donation.donorEmail());
            this.donationType.set(donation.donationType());
            this.amount.set(donation.amount());
            this.description.set(donation.description());
            this.paymentMethod.set(donation.paymentMethod());
            this.transactionReference.set(donation.transactionReference());
            this.amountReceived.set(donation.amountReceived());
            this.donationDate.set(donation.donationDate());
        }
    }
    
    // Getters and Property getters
    public Long getId() { return id.get(); }
    public LongProperty idProperty() { return id; }
    
    public String getDonorName() { return donorName.get(); }
    public StringProperty donorNameProperty() { return donorName; }
    
    public String getDonorEmail() { return donorEmail.get(); }
    public StringProperty donorEmailProperty() { return donorEmail; }
    
    public String getDonationType() { return donationType.get(); }
    public StringProperty donationTypeProperty() { return donationType; }
    
    public String getAmount() { return amount.get(); }
    public StringProperty amountProperty() { return amount; }
    
    public String getDescription() { return description.get(); }
    public StringProperty descriptionProperty() { return description; }
    
    public String getPaymentMethod() { return paymentMethod.get(); }
    public StringProperty paymentMethodProperty() { return paymentMethod; }
    
    public String getTransactionReference() { return transactionReference.get(); }
    public StringProperty transactionReferenceProperty() { return transactionReference; }
    
    public String getAmountReceived() { return amountReceived.get(); }
    public StringProperty amountReceivedProperty() { return amountReceived; }
    
    public LocalDateTime getDonationDate() { return donationDate.get(); }
    public ObjectProperty<LocalDateTime> donationDateProperty() { return donationDate; }
    
    // Setters
    public void setId(Long id) { this.id.set(id); }
    public void setDonorName(String name) { this.donorName.set(name); }
    public void setDonorEmail(String email) { this.donorEmail.set(email); }
    public void setDonationType(String type) { this.donationType.set(type); }
    public void setAmount(String amount) { this.amount.set(amount); }
    public void setDescription(String desc) { this.description.set(desc); }
    public void setPaymentMethod(String method) { this.paymentMethod.set(method); }
    public void setTransactionReference(String ref) { this.transactionReference.set(ref); }
    public void setAmountReceived(String amount) { this.amountReceived.set(amount); }
    public void setDonationDate(LocalDateTime date) { this.donationDate.set(date); }
    
    // Convert to DTO
    public DonationDto toDto() {
        return new DonationDto(
            getId(), getDonorName(), getDonorEmail(), getDonationType(),
            getAmount(), getAmountReceived(), getDonationDate()
        );
    }
    
    public DonationUpdateDto toUpdateDto() {
        return new DonationUpdateDto(
            getDonorName(), getDonorEmail(), getDonationType(), getAmount(),
            getDescription(), getPaymentMethod(), getTransactionReference(),
            getAmountReceived(), getDonationDate()
        );
    }
}

