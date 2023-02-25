package Tracker.Infrastructure.ToyDatabaseServer.Model;

import java.util.ArrayList;

public record File(String filename, String hash, long size, ArrayList<User> owners) {}
