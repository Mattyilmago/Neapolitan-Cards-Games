package com.example.Game;

import com.google.gson.JsonObject;
import jakarta.websocket.*;
import org.junit.Before;
import org.junit.Test;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public class GameServerClient {

    final String WebSocketServerURI = "ws://studio32bunker.ddns.net:1234";
    private WebSocketContainer container;
    private GameServerEndpoint endpoint;

    @Before
    public void initialize(){
        this.container = ContainerProvider.getWebSocketContainer();
        this.endpoint = new GameServerEndpoint();

    }

    @Test
    public void createRoom() throws URISyntaxException, DeploymentException, IOException, InterruptedException, EncodeException {
        Session activeSession = this.container.connectToServer(this.endpoint, new URI(WebSocketServerURI));


        JsonObject dataToSend = new JsonObject();
        dataToSend.addProperty("type", "createRoom");

        this.endpoint.sendJSON(dataToSend);

        Thread.sleep(1000);

    }

}
