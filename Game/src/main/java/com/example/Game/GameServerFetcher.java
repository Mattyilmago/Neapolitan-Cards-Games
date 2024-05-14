package com.example.Game;

import java.io.IOException;

public class GameServerFetcher extends Thread {
    private boolean running = true;

    final GameServerClient GSC;
    GameServerFetcher(GameServerClient GSC) {
        this.GSC = GSC;
    }

    @Override
    public void run() {

        while (running) {
            System.out.println(GSC.ClientID);
            try {
                GSC.fetchMessages();
            } catch (IOException e) {
                running=false;
                throw new RuntimeException(e);
            }



            try {
                Thread.sleep(500); // Sleep for 1 second
            } catch (InterruptedException e) {
                running=false;
                throw new RuntimeException(e);
            }
        }
    }

}
