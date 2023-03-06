package Tracker.BusinessLogic;

import Tracker.Infrastructure.ToyDatabaseServer.Database;
import Tracker.Infrastructure.ToyDatabaseServer.Model.File;

import java.util.ArrayList;

public interface DataDB
{
    int exit(String ipAddress);

    Database getDB();

    ArrayList<File> getFiles();

    File getFile(String name);

    int removeOwner(String ipAddress, String hash);

    int upload(String ipAddress, ArrayList<File> newFiles);
}
