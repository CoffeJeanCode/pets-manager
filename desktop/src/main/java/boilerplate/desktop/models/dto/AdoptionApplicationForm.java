package boilerplate.desktop.models.dto;

import boilerplate.desktop.models.AdoptionApplication;
import javafx.beans.property.*;
import java.time.LocalDateTime;

public class AdoptionApplicationForm {
    private final LongProperty id = new SimpleLongProperty();
    private final LongProperty petId = new SimpleLongProperty();
    private final StringProperty applicantName = new SimpleStringProperty();
    private final StringProperty applicantIdNumber = new SimpleStringProperty();
    private final StringProperty occupation = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty phone = new SimpleStringProperty();
    private final StringProperty address = new SimpleStringProperty();
    private final StringProperty housingType = new SimpleStringProperty();
    private final BooleanProperty hasOtherPets = new SimpleBooleanProperty();
    private final StringProperty adoptionReason = new SimpleStringProperty();
    private final StringProperty petExperience = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty("pending");
    private final ObjectProperty<LocalDateTime> applicationDate = new SimpleObjectProperty<>(LocalDateTime.now());
    
    public AdoptionApplicationForm() {}
    
    public AdoptionApplicationForm(AdoptionApplication app) {
        if (app != null) {
            this.id.set(app.id());
            this.petId.set(app.petId());
            this.applicantName.set(app.applicantName());
            this.applicantIdNumber.set(app.applicantIdNumber());
            this.occupation.set(app.occupation());
            this.email.set(app.email());
            this.phone.set(app.phone());
            this.address.set(app.address());
            this.housingType.set(app.housingType());
            this.hasOtherPets.set(app.hasOtherPets());
            this.adoptionReason.set(app.adoptionReason());
            this.petExperience.set(app.petExperience());
            this.status.set(app.status());
            this.applicationDate.set(app.applicationDate());
        }
    }
    
    // Getters and Property getters
    public Long getId() { return id.get(); }
    public LongProperty idProperty() { return id; }
    
    public Long getPetId() { return petId.get(); }
    public LongProperty petIdProperty() { return petId; }
    
    public String getApplicantName() { return applicantName.get(); }
    public StringProperty applicantNameProperty() { return applicantName; }
    
    public String getApplicantIdNumber() { return applicantIdNumber.get(); }
    public StringProperty applicantIdNumberProperty() { return applicantIdNumber; }
    
    public String getOccupation() { return occupation.get(); }
    public StringProperty occupationProperty() { return occupation; }
    
    public String getEmail() { return email.get(); }
    public StringProperty emailProperty() { return email; }
    
    public String getPhone() { return phone.get(); }
    public StringProperty phoneProperty() { return phone; }
    
    public String getAddress() { return address.get(); }
    public StringProperty addressProperty() { return address; }
    
    public String getHousingType() { return housingType.get(); }
    public StringProperty housingTypeProperty() { return housingType; }
    
    public Boolean getHasOtherPets() { return hasOtherPets.get(); }
    public BooleanProperty hasOtherPetsProperty() { return hasOtherPets; }
    
    public String getAdoptionReason() { return adoptionReason.get(); }
    public StringProperty adoptionReasonProperty() { return adoptionReason; }
    
    public String getPetExperience() { return petExperience.get(); }
    public StringProperty petExperienceProperty() { return petExperience; }
    
    public String getStatus() { return status.get(); }
    public StringProperty statusProperty() { return status; }
    
    public LocalDateTime getApplicationDate() { return applicationDate.get(); }
    public ObjectProperty<LocalDateTime> applicationDateProperty() { return applicationDate; }
    
    // Setters
    public void setId(Long id) { this.id.set(id); }
    public void setPetId(Long petId) { this.petId.set(petId); }
    public void setApplicantName(String name) { this.applicantName.set(name); }
    public void setApplicantIdNumber(String id) { this.applicantIdNumber.set(id); }
    public void setOccupation(String occ) { this.occupation.set(occ); }
    public void setEmail(String email) { this.email.set(email); }
    public void setPhone(String phone) { this.phone.set(phone); }
    public void setAddress(String addr) { this.address.set(addr); }
    public void setHousingType(String type) { this.housingType.set(type); }
    public void setHasOtherPets(Boolean has) { this.hasOtherPets.set(has); }
    public void setAdoptionReason(String reason) { this.adoptionReason.set(reason); }
    public void setPetExperience(String exp) { this.petExperience.set(exp); }
    public void setStatus(String status) { this.status.set(status); }
    public void setApplicationDate(LocalDateTime date) { this.applicationDate.set(date); }
    
    // Convert to DTO
    public AdoptionApplicationDto toDto() {
        return new AdoptionApplicationDto(
            getId(), getPetId(), getApplicantName(), getApplicantIdNumber(),
            getEmail(), getPhone(), getAddress(), getHousingType(),
            getHasOtherPets(), getStatus(), getApplicationDate()
        );
    }
}

