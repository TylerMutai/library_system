package librarysystem.gui;

import com.sun.javafx.application.PlatformImpl;
import javafx.application.Platform;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;
import librarysystem.resources.Resources;

import java.util.Optional;

public class GuiHelpers {
    public static boolean displayConfirmationPrompt(String title, String message) {
        Dialog<Boolean> confirmationPrompt = new Dialog<>();
        confirmationPrompt.setTitle(title);
        confirmationPrompt.setHeaderText(title);

        DialogPane pane = new DialogPane();
        pane.setContentText(message);

        Optional<Boolean> userInput = confirmationPrompt.showAndWait();

        return userInput.orElse(false);
    }

    public static void changeLanguage() {
        ChoiceDialog<String> confirmationPrompt = new ChoiceDialog<>("Select Language", "English", "Swahili");

        confirmationPrompt.setTitle(Resources.getString("change_language"));
        confirmationPrompt.setHeaderText(Resources.getString("change_language"));
        confirmationPrompt.setContentText(Resources.getString("change_language_description"));

        Optional<String> res = confirmationPrompt.showAndWait();

        switch (res.orElse("Swahili")) {
            case "English" -> Resources.setLanguage("english");
            case "Swahili" -> Resources.setLanguage("swahili");
            default -> {
                return;
            }
        }

        // Refresh UI
        Dashboard.myStage.close();
        Dashboard.launchDashboard(Dashboard.myStage, Dashboard.theModel);
        Dashboard.myStage.show();
        Dashboard.myStage.toFront();
    }
}