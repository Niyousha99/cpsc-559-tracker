package Tracker.Infrastructure;

import Tracker.BusinessLogic.DataDB;
import Tracker.Infrastructure.ToyDatabaseServer.DatabaseConnection.DatabaseConnection;;

public class DataDBImpl implements DataDB {
    private final DatabaseConnection connection;

    public DataDBImpl(DatabaseConnection connection) {
        this.connection = connection;
    }


    
}
