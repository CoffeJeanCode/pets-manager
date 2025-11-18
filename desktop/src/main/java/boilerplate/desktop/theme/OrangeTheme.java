package boilerplate.desktop.theme;

import java.util.Set;

public class OrangeTheme implements Theme {

    @Override
    public String getName() {
        return "Orange";
    }

    @Override
    public Set<String> getStylesheets() {
        return Set.of(THEME_DIR + "orange.css");
    }

    @Override
    public boolean isLight() {
        return false;
    }
}

