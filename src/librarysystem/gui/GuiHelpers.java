package librarysystem.gui;

import javafx.scene.control.Dialog;

import java.util.Optional;

public class GuiHelpers {
    public static boolean displayConfirmationPrompt(String title, String message) {
        Dialog<Boolean> confirmationPrompt = new Dialog<>();
        confirmationPrompt.setTitle(title);
        confirmationPrompt.setHeaderText(message);
        Optional<Boolean> userInput = confirmationPrompt.showAndWait();

        return userInput.orElse(false);
    }
}