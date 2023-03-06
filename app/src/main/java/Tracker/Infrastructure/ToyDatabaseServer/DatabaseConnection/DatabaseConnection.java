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

    public Database getDB()
    {
        return DatabaseEngine.getDB();
    }

    public ArrayList<File> getFiles()
    {
        return DatabaseEngine.getFiles();
    }

    public File getFile(String hash)
    {
        return DatabaseEngine.getFile(hash);
    }

    public int removeUser(String ipAddress)
    {
        return DatabaseEngine.removeUser(ipAddress);
    }

    public int removeOwner(String ipAddress, String hash)
    {
        return DatabaseEngine.removeOwner(ipAddress, hash);
    }

    public int addFiles(String ipAddress, ArrayList<File> newFiles)
    {
        return DatabaseEngine.addFiles(ipAddress, newFiles);
    }
}
