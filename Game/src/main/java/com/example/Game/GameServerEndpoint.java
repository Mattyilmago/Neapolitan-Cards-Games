package com.example.Game;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.websocket.*;
import javafx.collections.ObservableList;


import java.io.IOException;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GameServerEndpoint extends Endpoint {

    private Session session;

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        System.out.println(STR."ClientEndpoint: server session opened\{session}");
        this.session = session;
        //this.session.setMaxIdleTimeout(600000);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                System.out.println(STR."message: \{message}");
                JsonObject response = JsonParser.parseString(message).getAsJsonObject();
                if(response.get("success").getAsBoolean()){
                    switch (response.get("type").getAsString()){
                        case "register": {
                            SharedData.getInstance().setClientID(response.get("clientID").getAsString());
                        }break;
                        case "joinRoom": {
                            SharedData.getInstance().setRoomCode(response.get("roomCode").getAsString());
                            SharedData.getInstance().setSelectedGame(response.get("gameType").getAsString());
                            SharedData.getInstance().setPlayerName(response.get("playerName").getAsString());

                        }break;
                        case "createRoom":{
                            SharedData.getInstance().setRoomCode(response.get("roomCode").getAsString());
                            SharedData.getInstance().setPlayerName(response.get("playerName").getAsString());
                            SharedData.getInstance().getLobbyPlayers().add(response.get("playerName").getAsString());
                        }break;
                        case "allPlayers":{
                            String content = response.get("content").getAsString();
                            JsonArray giocatori = JsonParser.parseString(content).getAsJsonArray();
                            System.out.println("++"+giocatori.toString()+"+"+giocatori.size());
                            for (int i = 0; i < giocatori.size(); i++) {
                                SharedData.getInstance().getLobbyPlayers().add(giocatori.get(i).getAsString());
                                System.out.println("aggiunto");
                            }
                            System.out.println(SharedData.getInstance().getLobbyPlayers().toString());

                            //aggiungiGiocatoriAllaLista(giocatori);

                        }break;

                    }
                }else{
                    switch (response.get("error").getAsString()){
                        case "room_not_found":{
                            SharedData.getInstance().setLastError("room_not_found");
                            System.out.println("room_not_found");
                        }
                    }
                }
            }
        });
    }

    public void sendMessage(String message) throws IOException {
        session.getBasicRemote().sendText(message);
    }

    public void sendJSON(JsonObject json) throws IOException, EncodeException {
        session.getBasicRemote().sendObject(json);
        //this.session.getBasicRemote().get
    }

    public Session getSession() {
        return session;
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {

        try {
            SharedData.getGSCInstance().connect();
            System.out.println("Client riconnesso"+closeReason.toString());
            Thread.sleep(2000);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (DeploymentException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        super.onClose(session, closeReason);
    }
}
