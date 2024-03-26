package librarysystem.models;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Members - Represents normal users who can have their accounts created and borrow books.
 */
public class Members extends Model {

    private final static String TABLE_NAME = "Members";
    private final static ModelFunctionality model = set -> {
        ArrayList<Model> data = new ArrayList<>();
        try {
            while (set.next()) {
                Members admins = new Members();
                admins.email = set.getString(admins.EMAIL_FIELD);
                admins.firstName = set.getString(admins.FIRST_NAME_FIELD);
                admins.lastName = set.getString(admins.LAST_NAME_FIELD);
                admins.paymentMethod = set.getString(admins.PAYMENT_METHOD_FIELD);
                admins.ID = set.getString(admins.ID_FIELD);
                data.add(admins);
            }
            set.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return data;
    };
    public final String ID_FIELD = "ID";
    public final String FIRST_NAME_FIELD = "firstName";
    public final String LAST_NAME_FIELD = "lastName";
    public final String PAYMENT_METHOD_FIELD = "paymentMethod";
    public final String EMAIL_FIELD = "email";
    public String ID = "";
    public String firstName = "";
    public String lastName = "";
    public String paymentMethod = "";
    public String email = "";

    public Members() {
        super(TABLE_NAME, model);
    }

    @Override
    public boolean create() {
        // Setting attributes:
        string(ID_FIELD).primary().notNullable().unique();
        string(FIRST_NAME_FIELD).notNullable();
        string(LAST_NAME_FIELD).notNullable();
        string(EMAIL_FIELD).unique().notNullable();
        string(PAYMENT_METHOD_FIELD).nullable();
        return this.createModel();
    }

    @Override
    public boolean update() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        JsonObject fieldsAndValues = null;
        if (this.email.equals("") && this.firstName.equals("") && this.lastName.equals("") && this.paymentMethod.equals("")) {
            throw new NullPointerException("You need to set at least the value of one field");
        }
        if (Objects.equals(this.ID, "")) {
            throw new NullPointerException("You need to at least call: where, whereAnd, all, to specify which values you'd like to update");
        }

        if (!this.email.equals("")) {
            builder.add(this.EMAIL_FIELD, this.email);
        }
        if (!this.firstName.equals("")) {
            builder.add(this.FIRST_NAME_FIELD, this.firstName);
        }
        if (!this.lastName.equals("")) {
            builder.add(this.LAST_NAME_FIELD, this.lastName);
        }
        if (!this.paymentMethod.equals("")) {
            builder.add(this.PAYMENT_METHOD_FIELD, this.paymentMethod);
        }
        fieldsAndValues = builder.build();

        // TODO Auto-generated method stub
        return updateModel(fieldsAndValues, this.ID_FIELD, "=", this.ID);
    }

    @Override
    public boolean save() {
        //Perform checks to ensure fields have been set correctly
        if (Objects.equals(firstName, "") || Objects.equals(lastName, "") || Objects.equals(email, "")) {
            throw new NullPointerException("One or more field values have not been set. If you're trying to update fields, use the update method instead.");
        }
        JsonObject fieldsAndValues;
        JsonObjectBuilder builder = Json.createObjectBuilder();
        ID = generateID();
        builder.add(ID_FIELD, ID);
        builder.add(FIRST_NAME_FIELD, firstName);
        builder.add(LAST_NAME_FIELD, lastName);
        builder.add(EMAIL_FIELD, email);
        builder.add(PAYMENT_METHOD_FIELD, paymentMethod != null ? paymentMethod : "");
        fieldsAndValues = builder.build();

        return super.saveModelData(fieldsAndValues);
    }

    @Override
    public boolean delete() {
        if (Objects.equals(ID, "")) {
            return false;
        }
        return super.deleteFromModel(ID);
    }

}