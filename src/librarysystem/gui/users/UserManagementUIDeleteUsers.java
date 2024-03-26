package librarysystem.gui.users;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import librarysystem.gui.GuiHelpers;
import librarysystem.models.ListUsersReturnType;
import librarysystem.models.Members;
import librarysystem.resources.Resources;
import librarysystem.utils.Log;

public class UserManagementUIDeleteUsers {
    public static void deleteUsers(ListUsersReturnType listUsersReturnType) {
        if (listUsersReturnType.userIds.size() > 0) {
            boolean res = GuiHelpers.displayConfirmationPrompt(Resources.getString("delete_prompt_title"),
                    Resources.getString("delete_prompt_description")
            );
            if (!res) return;
            Members membersAgain = new Members();
            membersAgain.ID = "";
            boolean errorOccured = false;
            if (listUsersReturnType.userIds.size() != 0) {

                for (String theID : listUsersReturnType.userIds) {
                    membersAgain.ID = theID;
                    if (membersAgain.delete()) {
                        Members theMember = (Members) membersAgain.where(membersAgain.ID, "=", theID).get(0);
                        Log.log("Deleted Member " + theMember.email + ": " + theMember.firstName);
                    } else {
                        errorOccured = true;
                    }
                }
            } else {
                // Do nothing
                errorOccured = true;
            }

            Alert alert;
            if (errorOccured) {
                alert = new Alert(Alert.AlertType.ERROR, Resources.getString("user_could_not_be_deleted"), ButtonType.OK);
                alert.setTitle(Resources.getString("error"));
            } else {
                alert = new Alert(Alert.AlertType.INFORMATION, Resources.getString("user_deleted_successfully"),
                        ButtonType.OK);
                alert.setTitle(Resources.getString("success"));
            }
            alert.setHeaderText(null);

            alert.showAndWait();

            if (alert.getResult() == ButtonType.OK) {
                alert.hide();
            }
        }
    }
}