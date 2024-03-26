package librarysystem.gui;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import librarysystem.models.Model;
import librarysystem.models.User;
import librarysystem.resources.Resources;

import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dashboard extends Application {

    final static double SPACING = 10;
    final static Insets LABELS_BUTTONS = new Insets(10, 20, 10, 20);
    final static Insets COLUMNS = new Insets(10, 0, 10, 0);
    final static int ROW_HEIGHT = 24;
    static Scene scene;
    static Model theModel;
    static ArrayList<String> ID = new ArrayList<>();
    static String bookID = "";
    // stores the id after the user meets minimum requirements.
    static String userID = "";
    static Stage myStage;

    private static void handle() {
        // Parent root
        VBox parentVerticalLayout = new VBox();
        HBox parentHorizontalLayout = new HBox();
        HBox mainLabelHorizontalLayout = new HBox();
        ObservableList<Node> parentHorizontalLayoutChildren = parentHorizontalLayout.getChildren();
        ObservableList<Node> mainLabelHorizontalLayoutChildren = mainLabelHorizontalLayout.getChildren();

        //User Columns:
        UserManagementColumn.draw(parentHorizontalLayoutChildren);

        // Books Column
        BooksManagementColumn.draw(parentHorizontalLayoutChildren);

        // User Management Column
        EmployeeManagementColumn.draw(parentHorizontalLayoutChildren, mainLabelHorizontalLayoutChildren);


        // Autoresize children according to parent (Main Parent)
        for (Node n : parentHorizontalLayoutChildren) {
            HBox.setHgrow(n, Priority.ALWAYS);
        }
        parentHorizontalLayout.setSpacing(SPACING);

        Label mainLabel = new Label(Resources.getString("welcome") + ": " + ((User) theModel).username);
        mainLabelHorizontalLayoutChildren.addAll(mainLabel);
        mainLabelHorizontalLayout.setStyle("-fx-border-color: black");
        mainLabelHorizontalLayout.setAlignment(Pos.CENTER);

        ObservableList<Node> parentVerticalLayoutChildren = parentVerticalLayout.getChildren();
        parentVerticalLayoutChildren.add(mainLabelHorizontalLayout);
        parentVerticalLayoutChildren.add(parentHorizontalLayout);
        parentVerticalLayout.setSpacing(SPACING);
        parentVerticalLayout.setPadding(new Insets(30, 30, 30, 30));

        scene = new Scene(parentVerticalLayout);
    }

    static Optional<String> inputDialog(String title, String message, String content) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(message);
        dialog.setContentText(content);
        return dialog.showAndWait();
    }

    public static void launchDashboard(Stage stage, Model model) {
        stage.setTitle(Resources.getString("library_system"));
        theModel = model;
        handle();
        myStage = stage;
        stage.setScene(scene);
    }

    public static void handleFromMain(Model model) {
        theModel = model;
        launch();
    }

    public static void alertUser(String message, AlertType alertType, String title) {
        Alert alert = new Alert(alertType, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.OK) {
            alert.hide();
        }
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    public void start(Stage stage) throws Exception {
        handle();
        stage.setTitle(Resources.getString("library_system"));
        myStage = stage;
        stage.setScene(scene);
        stage.show();
    }

}