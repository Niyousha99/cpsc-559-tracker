package Tracker.BusinessLogic;

import Tracker.Infrastructure.ToyDatabaseServer.Model.File;

import java.util.ArrayList;

public interface DataDB
{
    public int join(String ipAddress);

    public int exit(String ipAddress);

    public ArrayList<File> getFiles();

    public File getFile(String name);

    public int upload(ArrayList<File> mewFiles, String ipAddress);
}
