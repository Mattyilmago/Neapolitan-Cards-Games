package com.example.Game;

import com.google.gson.JsonObject;
import jakarta.websocket.*;
import org.junit.Before;
import org.junit.Test;


import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public class GameServerClient {

    final String WebSocketServerURI = "ws://studio32bunker.ddns.net:1234";
    private WebSocketContainer container;
    private GameServerEndpoint endpoint;
    //private Session activeSession;

    @Before
    public void initialize(){
        this.container = ContainerProvider.getWebSocketContainer();
        this.endpoint = new GameServerEndpoint();
    }

    @Test
    public void createRoom() throws URISyntaxException, DeploymentException, IOException, InterruptedException, EncodeException {
        //activeSession = this.container.connectToServer(this.endpoint, new URI(WebSocketServerURI));
        connect();

        JsonObject dataToSend = new JsonObject();
        dataToSend.addProperty("type", "createRoom");
        dataToSend.addProperty("clientID", SharedData.getInstance().getClientID());
        dataToSend.addProperty("gameType", SharedData.getInstance().getSelectedGame());

        this.endpoint.sendJSON(dataToSend);
    }


    public void joinRoom(String roomCode) throws URISyntaxException, DeploymentException, IOException, InterruptedException, EncodeException {
        //Session activeSession = this.container.connectToServer(this.endpoint, new URI(WebSocketServerURI));
        connect();

        JsonObject dataToSend = new JsonObject();
        dataToSend.addProperty("type", "joinRoom");
        dataToSend.addProperty("clientID", SharedData.getInstance().getClientID());
        dataToSend.addProperty("roomCode", roomCode);

        this.endpoint.sendJSON(dataToSend);
    }

    public void connect() throws URISyntaxException, DeploymentException, IOException {
        Session activeSession = this.container.connectToServer(this.endpoint, new URI(WebSocketServerURI));
        //activeSession.setMaxIdleTimeout(600000);

        ///servirebbe per evitare la porcoddio di disconnessione del cazzo dal fottutissimo server ma a quanto pare non serve ad un cazzo
        ///sono le 4 voglio andare a dormire e sto cesso di merda non parte. Solo due parole: VAFFANCULO e Buonanotte

//        if(SharedData.getInstance().getClientID()==null){
//            JsonObject json = new JsonObject();
//            json.addProperty("type", "register");
//            try {
//                activeSession.getBasicRemote().sendObject(json);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            } catch (EncodeException e) {
//                throw new RuntimeException(e);
//            }
//        }else{
//            JsonObject json = new JsonObject();
//            json.addProperty("type", "register");
//            json.addProperty("clientID", SharedData.getInstance().getClientID());
//            try {
//                activeSession.getBasicRemote().sendObject(json);
//
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            } catch (EncodeException e) {
//                throw new RuntimeException(e);
//            }
//        }

    }

}
