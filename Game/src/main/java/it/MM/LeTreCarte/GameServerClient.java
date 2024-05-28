package it.MM.LeTreCarte;

import com.google.gson.JsonObject;
import it.MM.LeTreCarte.model.card.Card;
import jakarta.websocket.*;
import org.junit.Before;
import org.junit.Test;


import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameServerClient {

    final String WebSocketServerURI = "ws://studio32bunker.ddns.net:1234";
    private WebSocketContainer container;
    private GameServerEndpoint endpoint;
    public Session activeSession;

    @Before
    public void initialize(){
        this.container = ContainerProvider.getWebSocketContainer();
        this.endpoint = new GameServerEndpoint();
    }


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
        activeSession = this.container.connectToServer(this.endpoint, new URI(WebSocketServerURI));

        ///servirebbe per evitare la porcoddio di disconnessione del cazzo dal fottutissimo server ma a quanto pare non serve ad un cazzo
        ///sono le 4 voglio andare a dormire e sto cesso di merda non parte. Solo due parole: VAFFANCULO e Buonanotte

        //saves clientID in case of disconnection
        if(SharedData.getInstance().getClientID()==null){
            JsonObject json = new JsonObject();
            json.addProperty("type", "register");
            try {
                activeSession.getBasicRemote().sendObject(json);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (EncodeException e) {
                throw new RuntimeException(e);
            }
        }else{
            JsonObject json = new JsonObject();
            json.addProperty("type", "register");
            json.addProperty("clientID", SharedData.getInstance().getClientID());
            try {
                activeSession.getBasicRemote().sendObject(json);

            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (EncodeException e) {
                throw new RuntimeException(e);
            }
        }

    }


    public void sendMove(String moveType, Card card, int cardIndexInHand, int targetRow, int targetCol, int remainingCards) throws EncodeException, IOException {
        /*accepted moves:
            - put card on table
        */
        JsonObject dataToSend = new JsonObject();
        dataToSend.addProperty("success", "true");
        dataToSend.addProperty("type", "move");
        dataToSend.addProperty("clientID", SharedData.getInstance().getPlayerName());
        dataToSend.addProperty("roomCode", SharedData.getInstance().getRoomCode().toString());
        dataToSend.addProperty("cardIndexInHand", cardIndexInHand);
        dataToSend.addProperty("card-val", card.getValue());
        dataToSend.addProperty("card-seed", card.getSeed());
        dataToSend.addProperty("targetRow", targetRow);
        dataToSend.addProperty("targetCol", targetCol);
        dataToSend.addProperty("remainingCards", remainingCards);



        this.endpoint.sendJSON(dataToSend);
    }

    public void requestCards() throws EncodeException, IOException {
        JsonObject dataToSend = new JsonObject();
        dataToSend.addProperty("type", "getPlayerCards");
        dataToSend.addProperty("roomCode", SharedData.getInstance().getRoomCode());

        this.endpoint.sendJSON(dataToSend);
    }

}
