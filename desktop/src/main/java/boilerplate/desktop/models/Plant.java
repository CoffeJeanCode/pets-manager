package boilerplate.desktop.models;

/**
 * Plant item shown inside the main catalog.
 */
public class Plant extends GardenItem {

    private int lightLevel;      // 1-5 stars
    private String wateringType; // bajo | medio | alto (stored as string for UI)
    private String description;
    private String season;

    public Plant() {
        super();
    }

    public Plant(Long id, String name, double price, String imageUrl,
                 int lightLevel, String wateringType, String description, String season) {
        super(id, name, price, imageUrl);
        this.lightLevel = lightLevel;
        this.wateringType = wateringType;
        this.description = description;
        this.season = season;
    }

    public int getLightLevel() {
        return lightLevel;
    }

    public void setLightLevel(int lightLevel) {
        this.lightLevel = lightLevel;
    }

    public String getWateringType() {
        return wateringType;
    }

    public void setWateringType(String wateringType) {
        this.wateringType = wateringType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    @Override
    public String getCategory() {
        return "planta";
    }
}

