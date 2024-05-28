package it.MM.LeTreCarte;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.MM.LeTreCarte.model.card.Card;
import jakarta.websocket.*;
import javafx.application.Platform;
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
                            SharedData.getInstance().setRoomOwner(true);
                        }break;
                        case "allPlayers":{
                            String content = response.get("content").getAsString();
                            JsonArray giocatori = JsonParser.parseString(content).getAsJsonArray();
                            SharedData.getInstance().getLobbyPlayers().clear();
                            for (int i = 0; i < giocatori.size(); i++) {
                                int finalI = i;
                                Platform.runLater(()->{
                                    SharedData.getInstance().getLobbyPlayers().add(giocatori.get(finalI).getAsString());
                                });
                            }
                            System.out.println(SharedData.getInstance().getLobbyPlayers().toString());

                            //aggiungiGiocatoriAllaLista(giocatori);

                        }break;
                        case "playerCards":{
                            String content = response.get("content").getAsString();
                            JsonArray carte = JsonParser.parseString(content).getAsJsonArray();

                            SharedData.getInstance().getPlayerCards().clear();

                            new Thread(()->{
                                for (int i = 0; i < carte.size(); i++) {
                                    JsonArray carta = carte.get(i).getAsJsonArray();
                                    SharedData.getInstance().addPlayerCard(new Card(carta.get(0).getAsInt()+1,carta.get(1).getAsString().toCharArray()[0]));
                                }
                            }).start();

                        }break;
                        case "move":{
                            System.out.println("ricevuto1");
                            new Thread(()->{
                                SharedData.getInstance().addMove(response);
                            }).start();
                        }break;



                    }
                }else{
                    switch (response.get("error").getAsString()){
                        case "room_not_found":{
                            SharedData.getInstance().setRoomCode("-1");
                            System.out.println("room_not_found");
                        }break;
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
