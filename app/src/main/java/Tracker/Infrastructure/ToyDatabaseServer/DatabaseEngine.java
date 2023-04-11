package Tracker.Infrastructure.ToyDatabaseServer;

import Tracker.Infrastructure.ToyDatabaseServer.Model.File;
import Tracker.Infrastructure.ToyDatabaseServer.Model.User;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Objects;

public final class DatabaseEngine
{
    private static Database database;

    public static synchronized void setDatabase(Database db)
    {
        database = db;
        purgeUnavailableFiles();
    }

    // Creates a deep clone of any object that is passed in to prevent the receiver from updating hte original instance
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

    // Provides a clone of the DB
    public static synchronized Database getDB()
    {
        try
        {
            return deepClone(database);
        } catch (Exception exception)
        {
            return null;
        }
    }


    // Get a list of all the available files in the DB
    public static synchronized ArrayList<File> getFiles()
    {
        try
        {
            return new ArrayList<>(Objects.requireNonNull(deepClone(database)).files().values());
        } catch (NullPointerException exception)
        {
            return null;
        }
    }

    // Gets the file with the matching hash
    public static synchronized File getFile(String hash)
    {
        try
        {
            return Objects.requireNonNull(deepClone(database)).files().get(hash);
        } catch (NullPointerException exception)
        {
            return null;
        }
    }

    // Adds a client to the list of online clients
    private static synchronized int addUser(String ipAddress)
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


    // Remove a client from the online list
    public static synchronized int removeUser(String ipAddress)
    {
        try
        {
            database.files().forEach((hash, file) -> file.owners().remove(database.users().get(ipAddress)));
            database.users().remove(ipAddress);
            purgeUnavailableFiles();
            return 0;
        } catch (Exception exception)
        {
            return -1;
        }
    }

    // Remove a client from being the host of a specific file
    public static synchronized int removeOwner(String ipAddress, String hash)
    {
        try
        {
            database.files().get(hash).owners().remove(database.users().get(ipAddress));
            purgeUnavailableFiles();
            return 0;
        } catch (Exception exception)
        {
            return -1;
        }
    }

    // Adds the IP to be the host of all the files provided
    public static synchronized int addFiles(String ipAddress, ArrayList<File> files)
    {
        try
        {
            if (database.users().get(ipAddress) == null) addUser(ipAddress);

            files.forEach(newFile -> {
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
        } catch (Exception exception)
        {
            return -1;
        }
    }

    // Purges files that have no owner online
    private static synchronized void purgeUnavailableFiles()
    {
        database.files().forEach((hash, file) -> {
            if (file.owners().isEmpty()) database.files().remove(hash);
        });
    }
}
