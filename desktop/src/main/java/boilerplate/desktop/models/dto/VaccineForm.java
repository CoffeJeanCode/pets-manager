package boilerplate.desktop.models.dto;

import boilerplate.desktop.models.Vaccine;
import javafx.beans.property.*;
import java.time.LocalDate;

public class VaccineForm {
    private final LongProperty id = new SimpleLongProperty();
    private final LongProperty petId = new SimpleLongProperty();
    private final StringProperty vaccineName = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> applicationDate = new SimpleObjectProperty<>(LocalDate.now());
    private final ObjectProperty<LocalDate> nextDose = new SimpleObjectProperty<>();
    private final StringProperty veterinarian = new SimpleStringProperty();
    private final StringProperty batchNumber = new SimpleStringProperty();
    private final StringProperty notes = new SimpleStringProperty();
    
    public VaccineForm() {}
    
    public VaccineForm(Vaccine vaccine) {
        if (vaccine != null) {
            this.id.set(vaccine.id());
            this.petId.set(vaccine.petId());
            this.vaccineName.set(vaccine.vaccineName());
            this.applicationDate.set(vaccine.applicationDate());
            this.nextDose.set(vaccine.nextDose());
            this.veterinarian.set(vaccine.veterinarian());
            this.batchNumber.set(vaccine.batchNumber());
            this.notes.set(vaccine.notes());
        }
    }
    
    // Getters and Property getters
    public Long getId() { return id.get(); }
    public LongProperty idProperty() { return id; }
    
    public Long getPetId() { return petId.get(); }
    public LongProperty petIdProperty() { return petId; }
    
    public String getVaccineName() { return vaccineName.get(); }
    public StringProperty vaccineNameProperty() { return vaccineName; }
    
    public LocalDate getApplicationDate() { return applicationDate.get(); }
    public ObjectProperty<LocalDate> applicationDateProperty() { return applicationDate; }
    
    public LocalDate getNextDose() { return nextDose.get(); }
    public ObjectProperty<LocalDate> nextDoseProperty() { return nextDose; }
    
    public String getVeterinarian() { return veterinarian.get(); }
    public StringProperty veterinarianProperty() { return veterinarian; }
    
    public String getBatchNumber() { return batchNumber.get(); }
    public StringProperty batchNumberProperty() { return batchNumber; }
    
    public String getNotes() { return notes.get(); }
    public StringProperty notesProperty() { return notes; }
    
    // Setters
    public void setId(Long id) { this.id.set(id); }
    public void setPetId(Long petId) { this.petId.set(petId); }
    public void setVaccineName(String name) { this.vaccineName.set(name); }
    public void setApplicationDate(LocalDate date) { this.applicationDate.set(date); }
    public void setNextDose(LocalDate date) { this.nextDose.set(date); }
    public void setVeterinarian(String vet) { this.veterinarian.set(vet); }
    public void setBatchNumber(String batch) { this.batchNumber.set(batch); }
    public void setNotes(String notes) { this.notes.set(notes); }
    
    // Convert to DTO
    public VaccineDto toDto() {
        return new VaccineDto(
            getId(), getPetId(), getVaccineName(), 
            getApplicationDate(), getNextDose()
        );
    }
    
    public VaccineUpdateDto toUpdateDto() {
        return new VaccineUpdateDto(
            getPetId(), getVaccineName(), getApplicationDate(), 
            getNextDose(), getVeterinarian(), getBatchNumber(), getNotes()
        );
    }
}

