package Tracker.Infrastructure.ToyDatabaseServer.DatabaseConnection;

import Tracker.Infrastructure.ToyDatabaseServer.DatabaseEngine;
import Tracker.Infrastructure.ToyDatabaseServer.Model.File;
import Tracker.Infrastructure.Utils.Result;

import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseConnection {

    private DatabaseConnection() {

    }

    public static DatabaseConnection getConnection() {
        return new DatabaseConnection();
    }

    public Result<ArrayList> getObject(String objectType, HashMap<String, String> keyAttributePair) {
       return DatabaseEngine.getObject(objectType, keyAttributePair);
    }

    public ArrayList<File> getFiles()
    {
        return DatabaseEngine.getFiles();
    }

    public File getFile(String name)
    {
        return DatabaseEngine.getFile(name);
    }

    public int addNewUser(String ipAddress)
    {
        return DatabaseEngine.addNewUser(ipAddress);
    }

    public int removeUser(String ipAddress)
    {
        return DatabaseEngine.removeUser(ipAddress);
    }

    public int addFiles(ArrayList<File> newFiles, String ipAddress)
    {
        return DatabaseEngine.addFiles(newFiles, ipAddress);
    }
}
