package librarysystem.gui.books;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import librarysystem.models.BorrowedBooks;
import librarysystem.models.ListBooksButtonTypes;
import librarysystem.models.ListBooksReturnType;
import librarysystem.models.Model;
import librarysystem.resources.Resources;

import java.sql.Date;
import java.util.ArrayList;

import static librarysystem.gui.Dashboard.alertUser;

public class BooksManagementUIReturnBook {
    public static void returnBook(String userID, ListBooksReturnType listBooksReturnType) {
        if (listBooksReturnType.bookIds.size() == 0) {
            System.out.println("HERE 3!!!!");
            alertUser(Resources.getString("you_need_to_select_at_least_a_single_cell"), Alert.AlertType.ERROR,
                    Resources.getString("error"));
            return;
        }
        if (listBooksReturnType.buttonClicked == ListBooksButtonTypes.RETURN_BUTTON) {
            String bookId = (String) listBooksReturnType.bookIds.toArray()[0];
            BorrowedBooks borrowedBookss = new BorrowedBooks();
            boolean errorOccured = false;

            ArrayList<Model> theBooks = borrowedBookss.whereAnd(borrowedBookss.BOOK_ID_FIELD, "=", bookId,
                    borrowedBookss.USER_ID_FIELD, "=", userID);

            // Check if borrowed book has surpassed deadline:
            Date currentDate = new Date(System.currentTimeMillis());
            BorrowedBooks theBorrowedBook = (BorrowedBooks) theBooks.get(0);

            if (theBorrowedBook.bookDeadline.compareTo(currentDate) < 0) {
                // User incurs fine
                alertUser(Resources.getString("user_should_pay_fine"), Alert.AlertType.INFORMATION,
                        Resources.getString("warning"));
            }
            if (!theBorrowedBook.delete()) {
                errorOccured = true;
            }
            Alert alert;

            if (errorOccured) {
                System.out.println(Resources.getString("book_could_not_be_returned"));
                alert = new Alert(Alert.AlertType.ERROR, Resources.getString("book_could_not_be_returned"), ButtonType.OK);
                alert.setTitle(Resources.getString("error"));
            } else {
                System.out.println(Resources.getString("book_returned_successfully"));
                alert = new Alert(Alert.AlertType.INFORMATION, Resources.getString("book_returned_successfully"),
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