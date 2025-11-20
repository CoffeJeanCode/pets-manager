package boilerplate.desktop.models;

/**
 * Base class for every catalog item shown inside EcoGallery.
 * Code stays in English as requested, user-facing text will remain Spanish.
 */
public abstract class GardenItem {

    private Long id;
    private String name;
    private double price;
    private String imageUrl;

    protected GardenItem() {
        // Required by reflection / Gson
    }

    protected GardenItem(Long id, String name, double price, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public abstract String getCategory();

    public String summary() {
        return "%s - $%.2f".formatted(name, price);
    }
}

