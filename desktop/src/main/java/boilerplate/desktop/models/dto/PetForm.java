package boilerplate.desktop.models.dto;

import javafx.beans.property.*;
import java.time.LocalDate;

public class PetForm {
    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty species = new SimpleStringProperty();
    private final StringProperty breed = new SimpleStringProperty();
    private final IntegerProperty approxAge = new SimpleIntegerProperty();
    private final ObjectProperty<LocalDate> approxBirthdate = new SimpleObjectProperty<>();
    private final StringProperty sex = new SimpleStringProperty();
    private final StringProperty size = new SimpleStringProperty();
    private final StringProperty weight = new SimpleStringProperty();
    private final StringProperty healthStatus = new SimpleStringProperty("healthy");
    private final StringProperty currentLocation = new SimpleStringProperty();
    private final StringProperty microchip = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty specialNeeds = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> intakeDate = new SimpleObjectProperty<>(LocalDate.now());
    private final StringProperty photoUrl = new SimpleStringProperty();

    // Constructor vacío para nuevo
    public PetForm() {}

    // Constructor desde DTO existente (para edición)
    public PetForm(PetDto pet) {
        if (pet != null) {
            this.id.set(pet.id());
            this.name.set(pet.name());
            this.species.set(pet.species());
            this.breed.set(pet.breed());
            this.approxAge.set(pet.approxAge() != null ? pet.approxAge() : 0);
            this.approxBirthdate.set(pet.approxBirthdate());
            this.sex.set(pet.sex());
            this.size.set(pet.size());
            this.weight.set(pet.weight());
            this.healthStatus.set(pet.healthStatus());
            this.currentLocation.set(pet.currentLocation());
            this.microchip.set(pet.microchip());
            this.description.set(pet.description());
            this.specialNeeds.set(pet.specialNeeds());
            this.intakeDate.set(pet.intakeDate());
            this.photoUrl.set(pet.photoUrl());
        }
    }

    // Getters y Property getters
    public Long getId() { return id.get(); }
    public LongProperty idProperty() { return id; }

    public String getName() { return name.get(); }
    public StringProperty nameProperty() { return name; }

    public String getSpecies() { return species.get(); }
    public StringProperty speciesProperty() { return species; }

    public String getBreed() { return breed.get(); }
    public StringProperty breedProperty() { return breed; }

    public Integer getApproxAge() { return approxAge.get(); }
    public IntegerProperty approxAgeProperty() { return approxAge; }

    public LocalDate getApproxBirthdate() { return approxBirthdate.get(); }
    public ObjectProperty<LocalDate> approxBirthdateProperty() { return approxBirthdate; }

    public String getSex() { return sex.get(); }
    public StringProperty sexProperty() { return sex; }

    public String getSize() { return size.get(); }
    public StringProperty sizeProperty() { return size; }

    public String getWeight() { return weight.get(); }
    public StringProperty weightProperty() { return weight; }

    public String getHealthStatus() { return healthStatus.get(); }
    public StringProperty healthStatusProperty() { return healthStatus; }

    public String getCurrentLocation() { return currentLocation.get(); }
    public StringProperty currentLocationProperty() { return currentLocation; }

    public String getMicrochip() { return microchip.get(); }
    public StringProperty microchipProperty() { return microchip; }

    public String getDescription() { return description.get(); }
    public StringProperty descriptionProperty() { return description; }

    public String getSpecialNeeds() { return specialNeeds.get(); }
    public StringProperty specialNeedsProperty() { return specialNeeds; }

    public LocalDate getIntakeDate() { return intakeDate.get(); }
    public ObjectProperty<LocalDate> intakeDateProperty() { return intakeDate; }

    public String getPhotoUrl() { return photoUrl.get(); }
    public StringProperty photoUrlProperty() { return photoUrl; }
    
    // Setters
    public void setId(Long id) { this.id.set(id); }
    public void setName(String name) { this.name.set(name); }
    public void setSpecies(String species) { this.species.set(species); }
    public void setBreed(String breed) { this.breed.set(breed); }
    public void setApproxAge(Integer age) { this.approxAge.set(age != null ? age : 0); }
    public void setApproxBirthdate(LocalDate date) { this.approxBirthdate.set(date); }
    public void setSex(String sex) { this.sex.set(sex); }
    public void setSize(String size) { this.size.set(size); }
    public void setWeight(String weight) { this.weight.set(weight); }
    public void setHealthStatus(String status) { this.healthStatus.set(status); }
    public void setCurrentLocation(String location) { this.currentLocation.set(location); }
    public void setMicrochip(String microchip) { this.microchip.set(microchip); }
    public void setDescription(String description) { this.description.set(description); }
    public void setSpecialNeeds(String needs) { this.specialNeeds.set(needs); }
    public void setIntakeDate(LocalDate date) { this.intakeDate.set(date); }
    public void setPhotoUrl(String url) { this.photoUrl.set(url); }

    // Convertir a DTO para enviar a la API
    public PetDto toDto() {
        return new PetDto(
            getId(), getName(), getSpecies(), getBreed(), getApproxAge(), getApproxBirthdate(),
            getSex(), getSize(), getWeight(), "available", // default status for new pets
            getHealthStatus(), getCurrentLocation(), getMicrochip(),
            getDescription(), getSpecialNeeds(), getIntakeDate(), getPhotoUrl(),
            null, null // createdAt, updatedAt are set by server
        );
    }

    public PetUpdateDto toUpdateDto() {
        return new PetUpdateDto(
            getName(), getSpecies(), getBreed(), getApproxAge(), getApproxBirthdate(),
            getSex(), getSize(), getWeight(), null, // adoptionStatus no editable directamente
            getHealthStatus(), getCurrentLocation(), getMicrochip(),
            getDescription(), getSpecialNeeds(), getIntakeDate(), getPhotoUrl()
        );
    }
} 
