package Tracker.Infrastructure.ToyDatabaseServer.DatabaseConnection;

import Tracker.Infrastructure.ToyDatabaseServer.Database;
import Tracker.Infrastructure.ToyDatabaseServer.DatabaseEngine;
import Tracker.Infrastructure.ToyDatabaseServer.Model.File;

import java.util.ArrayList;

public class DatabaseConnection
{
    public static DatabaseConnection getConnection()
    {
        return new DatabaseConnection();
    }

    // Get a serialized version of the local DB
    public Database getDB() {return DatabaseEngine.getDB();}

    // Get a list of all the available files in the DB
    public ArrayList<File> getFiles()
    {
        return DatabaseEngine.getFiles();
    }

    // Get a list of peers for a specific file
    public File getFile(String hash)
    {
        return DatabaseEngine.getFile(hash);
    }

    // Removes an IP from being the host of any file
    public int removeUser(String ipAddress)
    {
        return DatabaseEngine.removeUser(ipAddress);
    }

    // Remove IP from being the host of a specific file
    public int removeOwner(String ipAddress, String hash)
    {
        return DatabaseEngine.removeOwner(ipAddress, hash);
    }

    // Adds the IP to be the host of all the files provided
    public int addFiles(String ipAddress, ArrayList<File> files)
    {
        return DatabaseEngine.addFiles(ipAddress, files);
    }
}
