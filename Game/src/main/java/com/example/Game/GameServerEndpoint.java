package com.example.Game;

import com.google.gson.JsonObject;
import jakarta.websocket.*;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

public class GameServerEndpoint extends Endpoint {

    private Session session;

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        System.out.println(STR."ClientEndpoint: server session opened\{session}");
        this.session = session;
        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                System.out.println(STR."message: \{message}");
            }
        });
    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    public void sendJSON(JsonObject json) throws IOException, EncodeException {
        this.session.getBasicRemote().sendObject(json);
        //this.session.getBasicRemote().get
    }

}
