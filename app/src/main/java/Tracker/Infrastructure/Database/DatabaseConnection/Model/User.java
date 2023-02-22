package Tracker.Infrastructure.Database.DatabaseConnection.Model;

public class User {
    private final String ipAddress;
    
    public User(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }
}