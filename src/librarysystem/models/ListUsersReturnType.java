package librarysystem.models;

import java.util.HashSet;

public class ListUsersReturnType {
    public final ListUsersButtonTypes buttonClicked;
    public final HashSet<String> userIds;

    public ListUsersReturnType(ListUsersButtonTypes buttonClicked, HashSet<String> userIds) {
        this.buttonClicked = buttonClicked;
        this.userIds = userIds;
    }
}