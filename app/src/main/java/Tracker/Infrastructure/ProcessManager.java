package Tracker.Infrastructure;

import java.io.IOException;
import java.net.UnknownHostException;

import Tracker.Infrastructure.Election.ElectionManager;

public class ProcessManager implements Runnable {
    private long waitTime = 10000;

    private void loop() {
        try {
            Thread.sleep(waitTime);
        }
        catch (InterruptedException ie) {
            ie.printStackTrace();
        }

        while (true) {
            if (ElectionManager.detectFailure()) {
                ElectionManager.initiateElection();

                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        loop();
    }
    
}
