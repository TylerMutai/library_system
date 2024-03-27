package librarysystem.gui.users;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import librarysystem.models.ListUsersButtonTypes;
import librarysystem.models.ListUsersReturnType;
import librarysystem.models.Members;
import librarysystem.models.Model;
import librarysystem.resources.Resources;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

import static librarysystem.gui.Dashboard.ROW_HEIGHT;
import static librarysystem.gui.Dashboard.SPACING;

public class UserManagementUIListUsers {
    public static Dialog<ListUsersReturnType> listUsers() {
        HashSet<String> selectedUsers = new HashSet<>();
        ScrollPane scrollbar = new ScrollPane();
        GridPane usersGridLayout = new GridPane();

        VBox IDsListViewAndLabel = new VBox();
        VBox firstNamesListViewAndLabel = new VBox();
        VBox lastNamesListViewAndLabel = new VBox();
        VBox emailsListViewAndLabel = new VBox();
        VBox paymentMethodsListViewAndLabel = new VBox();

        IDsListViewAndLabel.setSpacing(SPACING);
        firstNamesListViewAndLabel.setSpacing(SPACING);
        lastNamesListViewAndLabel.setSpacing(SPACING);
        emailsListViewAndLabel.setSpacing(SPACING);
        paymentMethodsListViewAndLabel.setSpacing(SPACING);

        ListView<String> IDsListView = new ListView<>();
        ListView<String> firstNamesListView = new ListView<>();
        ListView<String> lastNamesListView = new ListView<>();
        ListView<String> emailsListView = new ListView<>();
        ListView<String> paymentMethodsListView = new ListView<>();

        Label IDsListViewLabel = new Label(Resources.getString("user_id"));
        Label firstNamesListViewLabel = new Label(Resources.getString("first_name"));
        Label lastNamesListViewLabel = new Label(Resources.getString("last_name"));
        Label emailsListViewLabel = new Label(Resources.getString("email"));
        Label paymentMethodsListViewLabel = new Label(Resources.getString("payment_method"));

        IDsListView.setOrientation(Orientation.VERTICAL);
        firstNamesListView.setOrientation(Orientation.VERTICAL);
        lastNamesListView.setOrientation(Orientation.VERTICAL);
        emailsListView.setOrientation(Orientation.VERTICAL);
        paymentMethodsListView.setOrientation(Orientation.VERTICAL);

        Members members = new Members();
        ArrayList<Model> allUsers = members.all();

        ArrayList<String> IDsList = new ArrayList<>();
        ArrayList<String> firstNamesList = new ArrayList<>();
        ArrayList<String> lastNamesList = new ArrayList<>();
        ArrayList<String> emailsList = new ArrayList<>();
        ArrayList<String> paymentMethodsList = new ArrayList<>();

        for (Model user : allUsers) {
            Members member = (Members) user;
            IDsList.add(member.ID);
            firstNamesList.add(member.firstName);
            lastNamesList.add(member.lastName);
            emailsList.add(member.email);
            paymentMethodsList.add(member.paymentMethod);
        }

        ObservableList<String> IDsListViewItems = FXCollections.observableArrayList(IDsList);
        ObservableList<String> firstNamesListViewItems = FXCollections.observableArrayList(firstNamesList);
        ObservableList<String> lastNamesListViewItems = FXCollections.observableArrayList(lastNamesList);
        ObservableList<String> emailsListViewItems = FXCollections.observableArrayList(emailsList);
        ObservableList<String> paymentMethodsListViewItems = FXCollections.observableArrayList(paymentMethodsList);

        IDsListView.setItems(IDsListViewItems);
        firstNamesListView.setItems(firstNamesListViewItems);
        lastNamesListView.setItems(lastNamesListViewItems);
        emailsListView.setItems(emailsListViewItems);
        paymentMethodsListView.setItems(paymentMethodsListViewItems);

        // This sets the initial height of the ListView:
        IDsListView.setPrefHeight(IDsListViewItems.size() * ROW_HEIGHT + 2);
        firstNamesListView.setPrefHeight(firstNamesListViewItems.size() * ROW_HEIGHT + 2);
        lastNamesListView.setPrefHeight(lastNamesListViewItems.size() * ROW_HEIGHT + 2);
        emailsListView.setPrefHeight(emailsListViewItems.size() * ROW_HEIGHT + 2);
        paymentMethodsListView.setPrefHeight(paymentMethodsListViewItems.size() * ROW_HEIGHT + 2);

        IDsListViewAndLabel.getChildren().addAll(IDsListViewLabel, IDsListView);
        firstNamesListViewAndLabel.getChildren().addAll(firstNamesListViewLabel, firstNamesListView);
        lastNamesListViewAndLabel.getChildren().addAll(lastNamesListViewLabel, lastNamesListView);
        emailsListViewAndLabel.getChildren().addAll(emailsListViewLabel, emailsListView);
        paymentMethodsListViewAndLabel.getChildren().addAll(paymentMethodsListViewLabel, paymentMethodsListView);

        VBox parentLayout = new VBox();
        usersGridLayout.addColumn(0, IDsListViewAndLabel);
        usersGridLayout.addColumn(1, firstNamesListViewAndLabel);
        usersGridLayout.addColumn(2, lastNamesListViewAndLabel);
        usersGridLayout.addColumn(3, emailsListViewAndLabel);
        usersGridLayout.addColumn(4, paymentMethodsListViewAndLabel);
        scrollbar.setPrefHeight(10 * ROW_HEIGHT + 2);
        scrollbar.setContent(usersGridLayout);
        parentLayout.getChildren().addAll(scrollbar);

        // Display the created GUI inside a dialog box
        Dialog<ListUsersReturnType> listUserDialog = new Dialog<>();
        listUserDialog.setTitle(Resources.getString("list_users"));
        listUserDialog.setHeaderText(Resources.getString("list_users"));
        URL url = null;
        try {
            url = new File(Resources.RESOURCES_FOLDER + "/" + Resources.IMAGES_FOLDER + "/list_user.jpg").toURI()
                    .toURL();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        assert url != null;
        listUserDialog.setGraphic(new ImageView(url.toString()));

        // Multiple select in a list view:
        IDsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        ButtonType deleteUserButton = new ButtonType(Resources.getString("delete_user"), ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType cancelButton = new ButtonType(Resources.getString("cancel"), ButtonBar.ButtonData.BACK_PREVIOUS);
        ButtonType okButton = new ButtonType(Resources.getString("save"), ButtonBar.ButtonData.OK_DONE);

        // Dialog functionality
        listUserDialog.getDialogPane().getButtonTypes().addAll(deleteUserButton, cancelButton, okButton);

        // Store corresponding IDs to an ID variable
        IDsListView.getSelectionModel().selectedIndexProperty().addListener((arg0, oldIndex, newIndex) -> {
            selectedUsers.add(IDsListView.getItems().get(newIndex.intValue()));
        });

        listUserDialog.getDialogPane().setContent(parentLayout);

        listUserDialog.setResultConverter(dialogButton -> {
            if (dialogButton == deleteUserButton) {
                return new ListUsersReturnType(ListUsersButtonTypes.DELETE_BUTTON, selectedUsers);
            }
            if (dialogButton == cancelButton) {
                return new ListUsersReturnType(ListUsersButtonTypes.CANCEL_BUTTON, selectedUsers);
            }
            if (dialogButton == okButton) {
                return new ListUsersReturnType(ListUsersButtonTypes.OK_BUTTON, selectedUsers);
            }
            return null;
        });
        return listUserDialog;
    }
}