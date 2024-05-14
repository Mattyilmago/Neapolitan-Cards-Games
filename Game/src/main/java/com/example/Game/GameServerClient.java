package com.example.Game;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameServerClient {



    private InputStream in;
    private OutputStream out;
    public String ClientID;
    private int RoomID = -1;
    public Socket socket;
    private ArrayList<JsonObject> messages = new ArrayList<JsonObject>();

    public void connectToServer(){
        try {
            this.socket = new Socket("studio32bunker.ddns.net", 1234);
            this.in = socket.getInputStream();
            this.out = socket.getOutputStream();

        } catch (IOException e) {
            System.out.println(e);
        }
    }


    public void register() throws IOException {
        this.connectToServer();
        JsonObject dataToSend = new JsonObject();
        dataToSend.addProperty("action", "register");
        dataToSend.addProperty("payload", 1235);

        out.write(dataToSend.toString().getBytes());


        byte[] responseBytes = new byte[1024];
        int bytesRead = in.read(responseBytes);
        String jsonString = new String(responseBytes, 0, bytesRead);
        JsonObject response = JsonParser.parseString(jsonString).getAsJsonObject();

        if(response.get("success").getAsBoolean()){
            System.out.println("Client connesso correttamente");
            ClientID = response.get("message").getAsString();
            System.out.println("Client ID: " + response.get("message").getAsString());
        }else{
            System.out.println("Errore di connessione");
        }
    }

    public void createRoom(String roomName) throws IOException {
        this.connectToServer();
        JsonObject dataToSend = new JsonObject();
        dataToSend.addProperty("action", "create");
        dataToSend.addProperty("payload", roomName);
        dataToSend.addProperty("identifier", this.ClientID);

        //manda il messaggio
        out.write(dataToSend.toString().getBytes());

        //ricevi la risposta
        byte[] responseBytes = new byte[1024];
        int bytesRead = in.read(responseBytes);
        String jsonString = new String(responseBytes, 0, bytesRead);
        JsonObject response = JsonParser.parseString(jsonString).getAsJsonObject();
        System.out.println("res: " + jsonString);

        if(response.get("success").getAsBoolean()){
            System.out.println("Stanza creata correttamente");
            RoomID = response.get("message").getAsInt();
            System.out.println("Room ID: " + RoomID);
        }else{
            System.out.println("Errore di connessione");
        }
    }

    public void fetchMessages() throws IOException {
        this.connectToServer();

        byte[] messageBytes = new byte[1024];
        int bytesRead = in.read(messageBytes);
        String jsonString = new String(messageBytes, 0, bytesRead);
        JsonObject response = JsonParser.parseString(jsonString).getAsJsonObject();
        System.out.println("res: " + jsonString);

        this.messages.add(response);
    }

    public void send(String message) throws IOException {
        if(RoomID==-1){
            throw new RuntimeException("SEND_ERROR: You must be in a room first");
        }

        this.connectToServer();

        JsonObject msgToSend = new JsonObject();
        msgToSend.addProperty("message", message);

        JsonObject dataToSend = new JsonObject();
        dataToSend.addProperty("action", "send");
        dataToSend.addProperty("payload", msgToSend.getAsString());
        dataToSend.addProperty("room_id", RoomID);
        dataToSend.addProperty("identifier", ClientID);

        //manda il messaggio
        out.write(dataToSend.toString().getBytes());

    }

    public ArrayList<JsonObject> getMessages(){
        return this.messages;
    }


}