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

    public File getFile(String name)
    {
        return connection.getFile(name);
    }

    public int upload(ArrayList<File> newFiles, String ipAddress) {return connection.addFiles(newFiles, ipAddress);}

    public int join(String ipAddress)
    {
        return connection.addNewUser(ipAddress);
    }

    public int exit(String ipAddress)
    {
        return connection.removeUser(ipAddress);
    }
}
