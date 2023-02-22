package Tracker.Infrastructure.ToyDatabaseServer.Model;

public class User {
    private final String ipAddress;
    
    public User(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public User deepClone() {
        String clonedIpAddress = new String(this.ipAddress);
        return new User(clonedIpAddress);
    }
}