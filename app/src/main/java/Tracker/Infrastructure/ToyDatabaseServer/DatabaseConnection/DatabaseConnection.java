package Tracker.Infrastructure.ToyDatabaseServer.DatabaseConnection;

import java.util.ArrayList;
import java.util.HashMap;

import Tracker.Infrastructure.ToyDatabaseServer.DatabaseEngine;
import Tracker.Infrastructure.Utils.Result;

public class DatabaseConnection {
    
    private DatabaseConnection() {

    }

    public static DatabaseConnection getConnection() {
        return new DatabaseConnection();
    }

    public Result<ArrayList> getObject(String objectType, HashMap<String, String> keyAttributePair) {
       return DatabaseEngine.getObject(objectType, keyAttributePair);
    }
}
