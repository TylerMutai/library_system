package librarysystem.gui.books;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import librarysystem.gui.Dashboard;
import librarysystem.models.BorrowedBooks;
import librarysystem.models.ListBooksButtonTypes;
import librarysystem.models.ListBooksReturnType;
import librarysystem.resources.Resources;

import java.sql.Date;

import static librarysystem.gui.Dashboard.alertUser;

public class BooksManagementUIBorrowBook {
    public static void borrowBook(String userID, ListBooksReturnType listBooksReturnType) {
        if (listBooksReturnType.bookIds.size() == 0) {
            alertUser(Resources.getString("you_need_to_select_at_least_a_single_cell"), Alert.AlertType.ERROR,
                    Resources.getString("error"));
            return;
        }
        if (listBooksReturnType.buttonClicked == ListBooksButtonTypes.BORROW_BUTTON) {
            String bookID = (String) listBooksReturnType.bookIds.toArray()[0];
            BorrowedBooks borrowedBooks = new BorrowedBooks();

            // Check if the user already has this book:
            if (borrowedBooks
                    .whereAnd(borrowedBooks.BOOK_ID_FIELD, "=", bookID, borrowedBooks.USER_ID_FIELD, "=", userID)
                    .size() >= 1) {
                Dashboard.alertUser(Resources.getString("user_has_this_book"), Alert.AlertType.ERROR, Resources.getString("error"));
                return;
            }
            boolean errorOccured = false;
            borrowedBooks.BookID = bookID;
            borrowedBooks.UserID = userID;
            // 2 weeks in milliseconds: 1209600000
            borrowedBooks.bookDeadline = new Date(System.currentTimeMillis() + 1209600000);
            if (!borrowedBooks.save()) {
                errorOccured = true;
            }
            Alert alert;

            if (errorOccured) {
                alert = new Alert(Alert.AlertType.ERROR, Resources.getString("book_could_not_be_borrowed"), ButtonType.OK);
                alert.setTitle(Resources.getString("error"));
            } else {
                alert = new Alert(Alert.AlertType.INFORMATION, Resources.getString("book_borrowed_successfully") +
                        "\r\n" + Resources.getString("deadline") + ": " + borrowedBooks.bookDeadline,
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