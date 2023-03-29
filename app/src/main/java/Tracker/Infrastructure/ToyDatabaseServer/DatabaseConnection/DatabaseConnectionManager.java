package Tracker.Infrastructure.ToyDatabaseServer.DatabaseConnection;

import Tracker.Infrastructure.ToyDatabaseServer.Database;
import Tracker.Infrastructure.ToyDatabaseServer.DatabaseEngine;
import Tracker.Infrastructure.ToyDatabaseServer.Model.File;
import Tracker.Infrastructure.ToyDatabaseServer.Model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class DatabaseConnectionManager
{
    private static Database database;

    public static void initialize(String path, boolean cleanDB) throws JsonIOException, JsonSyntaxException, FileNotFoundException
    {
        Database databaseModel;
        ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, File> files = new ConcurrentHashMap<>();

        if (cleanDB) databaseModel = new Gson().fromJson("{\"users\":{},\"files\":{}}", Database.class);
        else databaseModel = DatabaseBuilder.buildDatabaseFromFile(path);

        databaseModel.files().forEach((hash, file) -> {
            ArrayList<User> owners = new ArrayList<>();
            files.put(file.hash(), new File(file.filename(), file.hash(), file.size(), owners));
            file.owners().forEach(owner -> {
                if (users.containsKey(owner.ipAddress())) owners.add(users.get(owner.ipAddress()));
                else
                {
                    users.put(owner.ipAddress(), owner);
                    owners.add(owner);
                }
            });
        });

        databaseModel.users().forEach((ip, user) -> users.putIfAbsent(user.ipAddress(), user));

        database = new Database(users, files);
        DatabaseEngine.setDatabase(database);
    }

    public static void importDB(String jsonDB)
    {
        Database databaseModel;
        ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, File> files = new ConcurrentHashMap<>();

        databaseModel = DatabaseBuilder.buildDatabaseFromJSON(jsonDB);

        databaseModel.files().forEach((hash, file) -> {
            ArrayList<User> owners = new ArrayList<>();
            files.put(file.hash(), new File(file.filename(), file.hash(), file.size(), owners));
            file.owners().forEach(owner -> {
                if (users.containsKey(owner.ipAddress())) owners.add(users.get(owner.ipAddress()));
                else
                {
                    users.put(owner.ipAddress(), owner);
                    owners.add(owner);
                }
            });
        });

        databaseModel.users().forEach((ip, user) -> users.putIfAbsent(user.ipAddress(), user));

        database = new Database(users, files);
        DatabaseEngine.setDatabase(database);
    }

    public static void shutdown(String path)
    {
        try
        {
            FileWriter fileWriter = new FileWriter(path);
            fileWriter.write(new GsonBuilder().setPrettyPrinting().create().toJson(database));
            fileWriter.close();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static DatabaseConnection getConnection()
    {
        return DatabaseConnection.getConnection();
    }
}
