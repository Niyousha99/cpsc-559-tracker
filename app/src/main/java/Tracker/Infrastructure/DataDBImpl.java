package Tracker.Infrastructure;

import Tracker.BusinessLogic.DataDB;
import Tracker.Infrastructure.ToyDatabaseServer.Database;
import Tracker.Infrastructure.ToyDatabaseServer.DatabaseConnection.DatabaseConnection;
import Tracker.Infrastructure.ToyDatabaseServer.Model.File;

import java.util.ArrayList;

public class DataDBImpl implements DataDB
{
    private final DatabaseConnection connection;

    public DataDBImpl(DatabaseConnection connection)
    {
        this.connection = connection;
    }

    // Get a serialized version of the local DB
    public Database getDB()
    {
        return connection.getDB();
    }

    // Get a list of all the available files in the DB
    public ArrayList<File> getFiles()
    {
        return connection.getFiles();
    }

    // Get a list of peers for a specific file
    public File getFile(String hash)
    {
        return connection.getFile(hash);
    }

    // Remove IP from being the host of a specific file
    public int removeOwner(String ipAddress, String hash) {return connection.removeOwner(ipAddress, hash);}

    // Adds the IP to be the host of all the files provided
    public int upload(String ipAddress, ArrayList<File> files) {return connection.addFiles(ipAddress, files);}

    // Removes an IP from being the host of any file
    public int exit(String ipAddress)
    {
        return connection.removeUser(ipAddress);
    }
}
