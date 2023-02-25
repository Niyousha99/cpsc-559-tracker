package Tracker.Infrastructure.ToyDatabaseServer;

import Tracker.Infrastructure.ToyDatabaseServer.Model.File;
import Tracker.Infrastructure.ToyDatabaseServer.Model.User;
import com.google.gson.Gson;

import java.util.ArrayList;

public final class DatabaseEngine
{
    private static Database database;

    public static synchronized void setDatabase(Database db)
    {
        database = db;
    }

    private static <T> T deepClone(T input)
    {
        try
        {
            Gson serializer = new Gson();
            return serializer.fromJson(serializer.toJson(input, input.getClass()), (Class<T>) input.getClass());
        } catch (Exception exception)
        {
            return null;
        }
    }

    public static synchronized ArrayList<File> getFiles()
    {
        return new ArrayList<File>(deepClone(database).files().values());
    }

    public static synchronized File getFile(String hash)
    {
        return deepClone(database).files().get(hash);
    }

    public static synchronized int addUser(String ipAddress)
    {
        try
        {
            database.users().putIfAbsent(ipAddress, new User(ipAddress));
            return 0;
        } catch (Exception exception)
        {
            return -1;
        }
    }

    public static synchronized int removeUser(String ipAddress)
    {
        try
        {
            database.files().forEach((hash, file) -> file.owners().remove(database.users().get(ipAddress)));
            database.users().remove(ipAddress);
            return 0;
        } catch (Exception exception)
        {
            return -1;
        }
    }

    public static synchronized int removeOwner(String ipAddress, String hash)
    {
        try
        {
            database.files().get(hash).owners().remove(database.users().get(ipAddress));
            return 0;
        } catch (Exception exception)
        {
            return -1;
        }
    }

    public static synchronized int addFiles(String ipAddress, ArrayList<File> newFiles)
    {
        newFiles.forEach(newFile -> {
            if (database.files().containsKey(newFile.hash()))
            {
                if (!database.files().get(newFile.hash()).owners().contains(database.users().get(ipAddress)))
                    database.files().get(newFile.hash()).owners().add(database.users().get(ipAddress));
            } else
            {
                database.files().put(newFile.hash(), newFile);
                newFile.owners().add(database.users().get(ipAddress));
            }
        });
        return 0;
    }
}
