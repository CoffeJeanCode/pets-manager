package boilerplate.desktop.models;

/**
 * Smart accessory such as sensors, timers or lamps.
 */
public class SmartAccessory extends GardenItem {

    private String feature;        // sensor, timer, lámpara...
    private String connectivity;   // WiFi, Bluetooth
    private String compatibility;  // apps o ecosistemas compatibles
    private String powerUsage;

    public SmartAccessory() {
        super();
    }

    public SmartAccessory(Long id, String name, double price, String imageUrl,
                          String feature, String connectivity, String compatibility,
                          String powerUsage) {
        super(id, name, price, imageUrl);
        this.feature = feature;
        this.connectivity = connectivity;
        this.compatibility = compatibility;
        this.powerUsage = powerUsage;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getConnectivity() {
        return connectivity;
    }

    public void setConnectivity(String connectivity) {
        this.connectivity = connectivity;
    }

    public String getCompatibility() {
        return compatibility;
    }

    public void setCompatibility(String compatibility) {
        this.compatibility = compatibility;
    }

    public String getPowerUsage() {
        return powerUsage;
    }

    public void setPowerUsage(String powerUsage) {
        this.powerUsage = powerUsage;
    }

    @Override
    public String getCategory() {
        return "accesorio";
    }
}
