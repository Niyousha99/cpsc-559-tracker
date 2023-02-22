package Tracker.Infrastructure.ToyDatabaseServer.DatabaseConnection.Model;

import java.util.ArrayList;

public class Database {
    private final ArrayList<User> users;
    private final ArrayList<File> files;

    public Database(ArrayList<User> users, ArrayList<File> files) {
        this.users = users;
        this.files = files;
    }

    public ArrayList<User> getUsers() {
        return this.users;
    }

    public ArrayList<File> getFiles() {
        return this.files;
    }
}
