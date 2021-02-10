package util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import java.text.DateFormatSymbols;
import java.util.Locale;
import java.util.ResourceBundle;

public class Helper {
    private static ObservableList<String> times;
    private static ObservableList<String> appointmentTypes;
    private static final ResourceBundle bundle = ResourceBundle.getBundle("resources.UIResources");
    private static final DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(Locale.getDefault());

    /**
     * Gets a list of available business hour times
     * for the UI to use.
     * @return ObservableList of times in string format.
     */
    public static ObservableList<String> getTimes(){
        if(times == null){
            times = FXCollections.observableArrayList(
                    "8:00 AM",
                    "9:00 AM",
                    "10:00 AM",
                    "11:00 AM",
                    "12:00 PM",
                    "1:00 PM",
                    "2:00 PM",
                    "3:00 PM",
                    "4:00 PM",
                    "5:00 PM",
                    "6:00 PM",
                    "7:00 PM",
                    "8:00 PM",
                    "9:00 PM",
                    "10:00 PM");
        }
        return times;
    }

    /**
     * Gets all available appointment types
     * @return ObservableList of string appointment types
     */
    public static ObservableList<String> getAppointmentTypes(){
        if(appointmentTypes == null){
            appointmentTypes = FXCollections.observableArrayList(
                    bundle.getString("phone"),
                    bundle.getString("inPerson"),
                    bundle.getString("email"),
                    bundle.getString("video"));
        }
        return appointmentTypes;
    }

    public static ObservableList<String> getMonths() {
        return FXCollections.observableArrayList(dateFormatSymbols.getMonths());
    }

    /**
     *
     * @return A label designed for no search results found.
     */
    public static Label getNoResultsLabel(){
        Label noResultsLabel = new Label();
        noResultsLabel.setText("No Results");
        return noResultsLabel;
    }

    /**
     *
     * @return A label designed for no search results found.
     */
    public static Label getNoAppointments(){
        Label noResultsLabel = new Label();
        noResultsLabel.setText("No Upcoming Appointments");
        return noResultsLabel;
    }
}
