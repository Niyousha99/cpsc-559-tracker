package Tracker.Infrastructure.ToyDatabaseServer;

import Tracker.Infrastructure.ToyDatabaseServer.Model.File;
import Tracker.Infrastructure.ToyDatabaseServer.Model.User;

import java.util.concurrent.ConcurrentHashMap;

public record Database(ConcurrentHashMap<String, User> users, ConcurrentHashMap<String, File> files) {}
