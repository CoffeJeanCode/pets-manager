package boilerplate.desktop.view;

import boilerplate.desktop.repositories.ApiClient;
import boilerplate.desktop.services.AdoptionApplicationService;
import boilerplate.desktop.services.DonationService;
import boilerplate.desktop.services.PetService;
import boilerplate.desktop.services.VaccineService;
import boilerplate.desktop.theme.Theme;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignP;
import org.kordamp.ikonli.materialdesign2.MaterialDesignA;

import java.util.Objects;

import static boilerplate.desktop.theme.Theme.SUPPORTED_THEMES;

public class MainWindow extends BorderPane {

    private ComboBox<Theme> themeSelect;
    private final PetService petService;
    private final VaccineService vaccineService;
    private final DonationService donationService;
    private final AdoptionApplicationService adoptionService;
    private TabPane tabPane;

    public MainWindow() {
        // Initialize services
        ApiClient apiClient = new ApiClient();
        this.petService = new PetService(apiClient);
        this.vaccineService = new VaccineService(apiClient);
        this.donationService = new DonationService(apiClient);
        this.adoptionService = new AdoptionApplicationService(apiClient);
        
        setTop(createTopPane());
        setCenter(createCentralPane());
        setPadding(new Insets(0));
    }

    public void selectTheme(Theme theme) {
        if (themeSelect != null) {
            themeSelect.setValue(Objects.requireNonNull(theme));
        }
    }

    private Node createTopPane() {
        HBox topPane = new HBox(15);
        topPane.setPadding(new Insets(15, 20, 15, 20));
        topPane.getStyleClass().add("top-pane");
        
        Label appTitle = new Label("Gestor de Mascotas");
        appTitle.getStyleClass().add("app-title");
        appTitle.setGraphic(new FontIcon(MaterialDesignP.PAW));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Improved theme selector with label
        HBox themeBox = new HBox(10);
        themeBox.setAlignment(Pos.CENTER);
        Label themeLabel = new Label("Tema:");
        themeLabel.getStyleClass().add("theme-label");
        
        themeSelect = new ComboBox<>(FXCollections.observableArrayList(SUPPORTED_THEMES));
        themeSelect.getStyleClass().add("theme-select");
        themeSelect.setPrefWidth(150);

        themeSelect.setConverter(new StringConverter<>() {
            @Override
            public String toString(Theme theme) {
                return theme != null ? theme.getName() : "";
            }

            @Override
            public Theme fromString(String name) {
                return SUPPORTED_THEMES.stream()
                        .filter(theme -> Objects.equals(name, theme.getName()))
                        .findFirst()
                        .orElse(null);
            }
        });

        themeSelect.valueProperty().addListener((obs, oldTheme, newTheme) -> {
            if (newTheme == null) return;
            applyThemeToScene(newTheme);
        });

        // Set default theme
        if (!SUPPORTED_THEMES.isEmpty()) {
            Theme defaultTheme = SUPPORTED_THEMES.get(0);
            themeSelect.setValue(defaultTheme);
        }
        
        themeBox.getChildren().addAll(themeLabel, themeSelect);
        topPane.getChildren().addAll(appTitle, spacer, themeBox);
        return topPane;
    }
    
    private void applyThemeToScene(Theme theme) {
        Scene scene = getScene();
        if (scene == null) {
            // Wait for scene to be available
            sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    applyThemeStylesheets(newScene, theme);
                }
            });
            return;
        }
        applyThemeStylesheets(scene, theme);
    }
    
    private void applyThemeStylesheets(Scene scene, Theme theme) {
        if (theme == null) return;
        
        // Remove all existing theme stylesheets
        scene.getStylesheets().removeIf(ss -> ss.contains("theme/"));
        
        // Add new theme stylesheets
        for (String stylesheet : theme.getStylesheets()) {
            // The stylesheet path from theme is already in the format: "/boilerplate/desktop/assets/styles/theme/..."
            String resourcePath = stylesheet;
            
            try {
                java.net.URL resource = getClass().getResource(resourcePath);
                if (resource != null) {
                    String fullPath = resource.toExternalForm();
                    if (!scene.getStylesheets().contains(fullPath)) {
                        scene.getStylesheets().add(fullPath);
                    }
                } else {
                    // Try alternative path resolution
                    resourcePath = resourcePath.startsWith("/") ? resourcePath : "/" + resourcePath;
                    resource = getClass().getResource(resourcePath);
                    if (resource != null) {
                        String fullPath = resource.toExternalForm();
                        if (!scene.getStylesheets().contains(fullPath)) {
                            scene.getStylesheets().add(fullPath);
                        }
                    } else {
                        System.err.println("Theme stylesheet not found: " + stylesheet);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error loading theme stylesheet: " + stylesheet + " - " + e.getMessage());
            }
        }
    }

    private Pane createCentralPane() {
        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getStyleClass().add("main-tab-pane");
        
        // Pets Tab
        Tab petsTab = new Tab("Mascotas");
        petsTab.setGraphic(new FontIcon(MaterialDesignP.PAW));
        PetListView petListView = new PetListView(petService);
        petsTab.setContent(petListView);
        
        // Vaccines Tab
        Tab vaccinesTab = new Tab("Vacunas");
        vaccinesTab.setGraphic(new FontIcon(MaterialDesignP.PILL));
        VaccineListView vaccineListView = new VaccineListView(vaccineService, petService);
        vaccinesTab.setContent(vaccineListView);
        
        // Donations Tab
        Tab donationsTab = new Tab("Donaciones");
        donationsTab.setGraphic(new FontIcon(MaterialDesignP.PIGGY_BANK));
        DonationListView donationListView = new DonationListView(donationService);
        donationsTab.setContent(donationListView);
        
        // Adoption Applications Tab
        Tab adoptionTab = new Tab("Solicitudes de Adopción");
        adoptionTab.setGraphic(new FontIcon(MaterialDesignA.ACCOUNT));
        AdoptionApplicationListView adoptionListView = new AdoptionApplicationListView(adoptionService, petService);
        adoptionTab.setContent(adoptionListView);
        
        tabPane.getTabs().addAll(petsTab, vaccinesTab, donationsTab, adoptionTab);
        
        VBox.setVgrow(tabPane, Priority.ALWAYS);
        return new VBox(tabPane);
    }
}
