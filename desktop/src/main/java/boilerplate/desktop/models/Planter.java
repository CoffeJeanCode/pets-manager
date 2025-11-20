package boilerplate.desktop.models;

/**
 * Planter/pot item available in the catalog.
 */
public class Planter extends GardenItem {

    private String material;   // cerámica, plástico, barro
    private String size;       // pequeña, mediana, grande
    private String color;
    private boolean drainage;

    public Planter() {
        super();
    }

    public Planter(Long id, String name, double price, String imageUrl,
                   String material, String size, String color, boolean drainage) {
        super(id, name, price, imageUrl);
        this.material = material;
        this.size = size;
        this.color = color;
        this.drainage = drainage;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean hasDrainage() {
        return drainage;
    }

    public void setDrainage(boolean drainage) {
        this.drainage = drainage;
    }

    @Override
    public String getCategory() {
        return "maceta";
    }
}

