package utils;

public class Utils {
    public static final String ADMIN_ROLE = "admin";
    public static final String REPORTER_ROLE = "reporter";
    public static final String STATUS_CHANGER_ROLE = "status_change";
    public static final String TIME_TRACKER_ROLE = "time_tracker";
    public static final String CLOSER_ROLE = "closer";
    public static final String OPEN_STATUS_= "Abierto";
    public static final String IN_PROGRESS_STATUS_= "En Progreso";
    public static final String CLOSED_STATUS= "Cerrado";

    public static String[] getRoles() {
        return new String[]{ADMIN_ROLE, REPORTER_ROLE, STATUS_CHANGER_ROLE, TIME_TRACKER_ROLE, CLOSER_ROLE};
    }

    public static String[] getStatus() {
        return new String[]{OPEN_STATUS_, IN_PROGRESS_STATUS_, CLOSED_STATUS};
    }

}
