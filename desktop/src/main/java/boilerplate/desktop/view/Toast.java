package boilerplate.desktop.view;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class Toast extends StackPane {
    
    public enum ToastType {
        SUCCESS, ERROR, INFO, WARNING
    }
    
    private static final double TOAST_DURATION = 3000; // 3 seconds
    
    public Toast(String message, ToastType type) {
        getStyleClass().addAll("toast", "toast-" + type.name().toLowerCase());
        setMaxWidth(400);
        
        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.getStyleClass().add("toast-message");
        
        HBox content = new HBox(10, messageLabel);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setPadding(new Insets(15, 20, 15, 20));
        
        getChildren().add(content);
        
        // Initial state
        setOpacity(0);
        setTranslateY(-20);
    }
    
    public void show(StackPane parent) {
        parent.getChildren().add(this);
        StackPane.setAlignment(this, Pos.TOP_CENTER);
        StackPane.setMargin(this, new Insets(20, 0, 0, 0));
        
        // Fade in and slide down
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), this);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        
        TranslateTransition slideDown = new TranslateTransition(Duration.millis(300), this);
        slideDown.setFromY(-20);
        slideDown.setToY(0);
        
        // Wait
        PauseTransition pause = new PauseTransition(Duration.millis(TOAST_DURATION));
        
        // Fade out and slide up
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), this);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        
        TranslateTransition slideUp = new TranslateTransition(Duration.millis(300), this);
        slideUp.setFromY(0);
        slideUp.setToY(-20);
        
        fadeOut.setOnFinished(e -> parent.getChildren().remove(this));
        
        // Play sequence
        SequentialTransition sequence = new SequentialTransition(
            fadeIn, slideDown, pause, fadeOut, slideUp
        );
        sequence.play();
    }
    
    public static void showToast(StackPane parent, String message, ToastType type) {
        Toast toast = new Toast(message, type);
        toast.show(parent);
    }
}

