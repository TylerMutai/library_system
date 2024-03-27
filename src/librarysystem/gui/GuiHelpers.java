package librarysystem.gui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import librarysystem.resources.Resources;

import java.util.Optional;

public class GuiHelpers {
    public static boolean displayConfirmationPrompt(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message);
        ButtonType buttonTypeYes = new ButtonType(Resources.getString("yes"), ButtonBar.ButtonData.YES);
        ButtonType buttonTypeNo = new ButtonType(Resources.getString("no"), ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
        alert.setTitle(title);

        Optional<ButtonType> userInput = alert.showAndWait();
        if (userInput.isPresent()) {
            ButtonType buttonType = userInput.get();
            return buttonType.getButtonData() == ButtonBar.ButtonData.YES;
        }

        return false;
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