package Tracker.BusinessLogic;

import Tracker.Infrastructure.ToyDatabaseServer.Model.File;

import java.util.ArrayList;

public interface DataDB
{
    int join(String ipAddress);

    int exit(String ipAddress);

    ArrayList<File> getFiles();

    File getFile(String name);

    int removeOwner(String ipAddress, String hash);

    int upload(String ipAddress, ArrayList<File> newFiles);
}
