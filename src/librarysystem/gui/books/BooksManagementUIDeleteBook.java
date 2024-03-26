package librarysystem.gui.books;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import librarysystem.gui.GuiHelpers;
import librarysystem.models.Books;
import librarysystem.models.ListBooksButtonTypes;
import librarysystem.models.ListBooksReturnType;
import librarysystem.resources.Resources;
import librarysystem.utils.Log;

import static librarysystem.gui.Dashboard.alertUser;

public class BooksManagementUIDeleteBook {
    public static void deleteBook(ListBooksReturnType listBooksReturnType) {
        if(listBooksReturnType.bookIds.size() ==0){
            alertUser(Resources.getString("you_need_to_select_at_least_a_single_cell"), Alert.AlertType.ERROR,
                    Resources.getString("error"));
            return;
        }
        boolean res = GuiHelpers.displayConfirmationPrompt(Resources.getString("delete_prompt_title"),
                Resources.getString("delete_prompt_description")
        );
        if (!res) return;

        if (listBooksReturnType.buttonClicked == ListBooksButtonTypes.DELETE_BUTTON) {
            Books booksAgain = new Books();
            booksAgain.ID = "";
            boolean errorOccured = false;
            System.out.println(listBooksReturnType.bookIds.size());
            if (listBooksReturnType.bookIds.size() != 0) {

                for (String theID : listBooksReturnType.bookIds) {
                    booksAgain.ID = theID;
                    if (booksAgain.delete()) {
                        Books theBook = (Books) booksAgain.where(booksAgain.ID, "=", theID).get(0);
                        Log.log("Deleted Book " + theBook.ID + ": " + theBook.bookTitle);
                    } else {
                        errorOccured = true;
                    }
                }
            } else {
                errorOccured = true;
            }

            Alert alert;

            if (errorOccured) {
                alert = new Alert(Alert.AlertType.ERROR, Resources.getString("book_could_not_be_deleted"), ButtonType.OK);
                alert.setTitle(Resources.getString("error"));
            } else {
                alert = new Alert(Alert.AlertType.INFORMATION, Resources.getString("book_deleted_successfully"),
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