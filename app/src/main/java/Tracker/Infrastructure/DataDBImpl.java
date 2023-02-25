package Tracker.Infrastructure;

import Tracker.BusinessLogic.DataDB;
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

    public ArrayList<File> getFiles()
    {
        return connection.getFiles();
    }

    public File getFile(String hash)
    {
        return connection.getFile(hash);
    }

    public int removeOwner(String ipAddress, String hash) {return connection.removeOwner(ipAddress, hash);}

    public int upload(String ipAddress, ArrayList<File> newFiles) {return connection.addFiles(ipAddress, newFiles);}

    public int join(String ipAddress)
    {
        return connection.addUser(ipAddress);
    }

    public int exit(String ipAddress)
    {
        return connection.removeUser(ipAddress);
    }
}
