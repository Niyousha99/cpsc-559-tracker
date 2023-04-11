package Tracker.BusinessLogic;

import Tracker.Infrastructure.ToyDatabaseServer.Database;
import Tracker.Infrastructure.ToyDatabaseServer.Model.File;

import java.util.ArrayList;

public interface DataDB
{
    // Removes an IP from being the host of any file
    int exit(String ipAddress);

    // Get a serialized version of the local DB
    Database getDB();

    // Get a list of all the available files in the DB
    ArrayList<File> getFiles();

    // Get a list of peers for a specific file
    File getFile(String hash);

    // Remove IP from being the host of a specific file
    int removeOwner(String ipAddress, String hash);

    // Adds the IP to be the host of all the files provided
    int upload(String ipAddress, ArrayList<File> files);
}
