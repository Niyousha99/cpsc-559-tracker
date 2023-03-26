package Tracker.Infrastructure;

import java.io.IOException;
import java.net.UnknownHostException;

import Tracker.Infrastructure.Election.ElectionManager;

public class ProcessManager implements Runnable {
    private long waitTime;

    private void loop() {
        while (true) {
            if (ElectionManager.detectFailure()) {
                try {
                    ElectionManager.initiateElection();
                } catch (UnknownHostException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
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
