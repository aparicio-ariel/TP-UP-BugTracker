package model;

import utils.Utils;

public class UserContext {
    private static UserContext instance;
    private User currentUser;

    private UserContext() {}

    public static UserContext getInstance() {
        if (instance == null) {
            instance = new UserContext();
        }
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isAdminRole(){
        return currentUser.getRole().equals(Utils.ADMIN_ROLE);
    }

    public boolean isReporterRole(){
        return currentUser.getRole().equals(Utils.REPORTER_ROLE);
    }

    public boolean isStatusRole(){
        return currentUser.getRole().equals(Utils.STATUS_CHANGER_ROLE);
    }

    public boolean isTimerRole(){
        return currentUser.getRole().equals(Utils.TIME_TRACKER_ROLE);
    }

    public boolean isCloserRole(){
        return currentUser.getRole().equals(Utils.CLOSER_ROLE);
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

}
