package boilerplate.desktop.models;

import boilerplate.desktop.models.dto.CategoryDto;
import com.google.gson.annotations.SerializedName;

/**
 * Plant item shown inside the main catalog.
 * Matches PlantResponse schema from API.
 */
public class Plant extends GardenItem {

    @SerializedName("category_id")
    private Integer categoryId;
    
    @SerializedName("light")
    private String light;  // Alta, Media, Baja
    
    @SerializedName("irrigation")
    private String irrigation;  // Alta, Media, Baja
    
    @SerializedName("environment")
    private String environment;  // Interior, Exterior
    
    private String description;
    
    @SerializedName("image_url")
    private String imageUrl;
    
    private CategoryDto.CategorySimple category;

    public Plant() {
        super();
    }

    // Legacy constructor for mock data compatibility
    public Plant(Long id, String name, double price, String imageUrl,
                 int lightLevel, String wateringType, String description, String season) {
        super(id, name, price, imageUrl != null ? imageUrl : "");
        // Map legacy values to API format
        this.light = mapLightLevel(lightLevel);
        this.irrigation = mapWateringType(wateringType);
        this.environment = "Interior";
        this.description = description;
        this.imageUrl = imageUrl;
    }

    // Constructor for API response
    public Plant(Integer id, String name, Integer categoryId, String light, 
                 String irrigation, String environment, String description, 
                 String imageUrl, CategoryDto.CategorySimple category) {
        super(id != null ? id.longValue() : 0L, name, 0.0, imageUrl != null ? imageUrl : "");
        this.categoryId = categoryId;
        this.light = light;
        this.irrigation = irrigation;
        this.environment = environment;
        this.description = description;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    // Helper methods for legacy compatibility
    private String mapLightLevel(int level) {
        if (level >= 4) return "Alta";
        if (level >= 2) return "Media";
        return "Baja";
    }

    private String mapWateringType(String type) {
        if (type == null) return "Media";
        switch (type.toLowerCase()) {
            case "alto": return "Alta";
            case "medio": return "Media";
            case "bajo": return "Baja";
            default: return "Media";
        }
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getLight() {
        return light;
    }

    public void setLight(String light) {
        this.light = light;
    }

    public String getIrrigation() {
        return irrigation;
    }

    public void setIrrigation(String irrigation) {
        this.irrigation = irrigation;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getImageUrl() {
        return imageUrl != null ? imageUrl : super.getImageUrl();
    }

    @Override
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        super.setImageUrl(imageUrl);
    }

    public CategoryDto.CategorySimple getCategoryObject() {
        return category;
    }

    public void setCategory(CategoryDto.CategorySimple category) {
        this.category = category;
    }

    // Legacy methods for compatibility
    public int getLightLevel() {
        if (light == null) return 3;
        switch (light) {
            case "Alta": return 5;
            case "Media": return 3;
            case "Baja": return 1;
            default: return 3;
        }
    }

    public String getWateringType() {
        if (irrigation == null) return "medio";
        switch (irrigation) {
            case "Alta": return "alto";
            case "Media": return "medio";
            case "Baja": return "bajo";
            default: return "medio";
        }
    }

    public String getSeason() {
        return "Todo el año"; // Default, not in API schema
    }

    @Override
    public String getCategory() {
        return category != null ? category.name() : "planta";
    }
}

