package librarysystem.models;

import java.sql.ResultSet;
import java.util.ArrayList;

public interface ModelFunctionality {
    ArrayList<Model> process(ResultSet set);

}