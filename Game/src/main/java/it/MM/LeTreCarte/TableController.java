package it.MM.LeTreCarte;

import com.google.gson.JsonObject;
import it.MM.LeTreCarte.model.GameManagers.GameManagerBriscola;
import it.MM.LeTreCarte.model.GameManagers.GameManagerScopa;
import it.MM.LeTreCarte.model.GameManagers.GameManagerTressette;
import it.MM.LeTreCarte.model.card.Card;
import it.MM.LeTreCarte.model.card.cardcontainer.Deck;
import it.MM.LeTreCarte.model.card.cardcontainer.Table;
import jakarta.websocket.EncodeException;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.ResourceBundle;


public class TableController implements Initializable {
    //fixed dimensioni della grid Table
    final int tableRows = 3;
    final int tableCols = 5;

    // { nomeGiocatore1: [imgvw1, imgvw2], nomeGiocatore2: [...] }
    private final HashMap<String, ArrayList<ImageView>> handsWithCardsImView = new HashMap<>();
    private final HashMap<String, GridPane> handsWithGridPane = new HashMap<>();

    //indice del giocatore che deve giocare
    public int indexPlayerInTurn = 0;
    String currGame = SharedData.getInstance().getSelectedGame();

    //contiene false se non è presente nessuna carta, true viceversa
    ArrayList<Boolean> tableSupport = new ArrayList<>();
    @FXML
    private AnchorPane Card;
    @FXML
    private GridPane tableGridPane;
    @FXML
    private StackPane deck;
    @FXML
    private GridPane hand;
    @FXML
    private GridPane handPlayer1;
    @FXML
    private GridPane handPlayer2;
    @FXML
    private GridPane handPlayer3;
    @FXML
    private Label namePlayer;

    @FXML
    private Label namePlayer1;

    @FXML
    private Label namePlayer2;

    @FXML
    private Label namePlayer3;

    private ArrayList<Label> names = new ArrayList<Label>(){{
        add(namePlayer);
        add(namePlayer1);
        add(namePlayer2);
        add(namePlayer3);
    }};

    @FXML
    private Label MaradonaPoints;

    @FXML
    private Label VesuvioPoints;

    @FXML
    private AnchorPane teamMaradona;

    @FXML
    private AnchorPane teamVesuvio;

    private Table table = new Table();

    //contiene i giocatori sortati per inserirli a schermo
    static ArrayList<String> playersSorted = new ArrayList<>(){{
        addAll(SharedData.getInstance().getLobbyPlayers());
    }};

    //contiene i giocatori in ordine di turno
    private ArrayList<String> playersTurn = new ArrayList<>(){{
        addAll(SharedData.getInstance().getLobbyPlayers());
    }};

    boolean twoPlayers = playersSorted.size() == 2;

    Character briscola;

    Player winnerPlayerTurn; //Il giocatore che vince il turno


    public void updatePointsLabel(){
        MaradonaPoints.setText(String.valueOf(table.getTeam(0).getPoints()));
        VesuvioPoints.setText(String.valueOf(table.getTeam(1).getPoints()));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        startBackgroundListener();
        sortPlayersSorted();

        configureTableGridPane(tableGridPane);
        configureTable(table);
        table.getTeam(0).setPoints(12);
        table.getTeam(1).setPoints(7);
        updatePointsLabel();

        generateHands();

        //TODO deck1 = SharedData.getDeck();
        Deck deck1 = new Deck();


        //Butta 4 carte a terra se si gioca a scopa
        if (currGame.equals("Scopa")) {
            for (int i = 0; i < 4; i++) {
                Card card = deck1.getFirst();
                ImageView iv = new ImageView(new Image(getClass().getResource(card.getImage()).toExternalForm()));
                iv.setFitWidth(hand.getPrefWidth() / 11.5);
                iv.setFitHeight(hand.getPrefHeight() * 0.9);

                tableGridPane.getChildren().remove(i);
                tableGridPane.add(iv, i, 0);
                tableSupport.set(i, true);
            }
        }

        //Salva il seme della briscola
        if(currGame.equals("Briscola")){
            briscola = deck1.getCards().getLast().getSeed(); //il seme dell'ultima carta del mazzo
        }

    }

    /**
     * Return a new card given by the URL of the card's image
     * @param url url of the image
     * @return Card associated to the imageview
     */
    private Card cardFromUrl(String url){
        int value = 0;
        Character seed;
        if(url.charAt(109) == '0'){
            value = 10;
            seed = url.charAt(111);
            return new Card(value, seed);
        }
        else{
            value = Character.getNumericValue(url.charAt(108));
            seed = url.charAt(110);
            return new Card(value, seed);
        }
    }

    /** Starts the processes that handles responses of server
        type of responses:
        - type:move when a player put a card on the table
        - type:cardsOnTable when the server needs to refresh current cards on table
        - type:yourTurn when the server notify that is the turn of a player
     * */
    private void startBackgroundListener() {
        SharedData.getInstance().getMoves().addListener(new ListChangeListener<JsonObject>() {
            @Override
            public void onChanged(Change<? extends JsonObject> c) {
                Platform.runLater(() -> {
                    JsonObject response = SharedData.getInstance().getMoves().getLast();

                    if (Objects.equals(response.get("type").getAsString(), "move") && !Objects.equals(response.get("clientAKA").getAsString(), SharedData.getInstance().getPlayerName())) {
                        String clientAKA = response.get("clientAKA").getAsString();
                        int cardIndexInHand = response.get("cardIndexInHand").getAsInt() - 1;

                        System.out.println(clientAKA);
                        System.out.println(handsWithGridPane);
                        System.out.println(handsWithCardsImView);


                        //#
                        ImageView cardToMove = handsWithCardsImView.get(clientAKA).get(cardIndexInHand);

                        Card card = cardFromUrl(handsWithCardsImView.get(clientAKA).get(cardIndexInHand).getImage().getUrl());
                        handsWithCardsImView.get(clientAKA).remove(cardIndexInHand);


                        int targetCol = response.get("targetCol").getAsInt();
                        int targetRow = response.get("targetRow").getAsInt();
                        GridPane gridviewAKA = handsWithGridPane.get(clientAKA);
                        int cardVal = response.get("card-val").getAsInt();
                        Character cardSeed = response.get("card-seed").getAsString().toCharArray()[0];

                        Translate translate2 = new Translate();
                        cardToMove.getTransforms().add(translate2);
                        //coordinate globali
                        double startX = cardToMove.localToScene(0, 0).getX();
                        double startY = cardToMove.localToScene(0, 0).getY();
                        double endX = tableGridPane.localToScene(tableGridPane.getBoundsInLocal()).getMinX() + targetCol * tableGridPane.getPrefWidth() / tableCols;
                        double endY = tableGridPane.localToScene(tableGridPane.getBoundsInLocal()).getMinY() + targetRow * tableGridPane.getPrefHeight() / tableRows;

                        //creo una copia di iv
                        ImageView cardInTable = new ImageView(new Image(getClass().getResource(new Card(cardVal, cardSeed).getImage()).toExternalForm()));
                        cardInTable.setFitWidth(hand.getPrefWidth() / 11.5);
                        cardInTable.setFitHeight(hand.getPrefHeight() * 0.9);
                        cardInTable.setVisible(false);

                        tableGridPane.getChildren().remove(targetRow + targetCol);
                        tableGridPane.add(cardInTable, targetCol, targetRow);
                        tableSupport.set(targetRow * targetCol + targetCol, true);


                        //create animation
                        Timeline cardToTableAnimationENEMY = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(translate2.xProperty(), 0), new KeyValue(translate2.yProperty(), 0)),

                                new KeyFrame(new Duration(300), e -> {
                                    cardInTable.setVisible(true);
                                    cardToMove.setVisible(false);
                                    gridviewAKA.getChildren().remove(cardToMove);


                                    // Sposta i nodi rimanenti (si potrebbe fare animato ehh)
                                    for (Node node : gridviewAKA.getChildren()) {
                                        Integer currCol = GridPane.getColumnIndex(node);
                                        if (currCol != null && currCol > (cardIndexInHand)) {
                                            GridPane.setColumnIndex(node, currCol - 1);
                                        }
                                    }
                                    gridviewAKA.getColumnConstraints().removeLast();


                                }, new KeyValue(translate2.xProperty(), calculateX(playersSorted.indexOf(clientAKA), startX, endX)), new KeyValue(translate2.yProperty(), calculateY(playersSorted.indexOf(clientAKA), startY, endY)))


                        );

                        cardToTableAnimationENEMY.play();

                        //vedo se posso calcolare la presa e aggiorno variabile indexPayerTurn
                        if(currGame.equals("Scopa"))
                            calculateWinForScopa(card);
                        else {
                            //Se l'ultimo del turno ha giocato la carta vedo la presa del turno se gioco a Tressette o Briscola
                            if (indexPlayerInTurn == playersTurn.size() - 1) {
                                calculateEndTurnWinner();
                            }
                        }
                        if(indexPlayerInTurn == playersTurn.size() - 1)
                            indexPlayerInTurn = 0;
                        else
                            indexPlayerInTurn++;



                    }
                });
                if(indexPlayerInTurn == playersTurn.size() - 1)
                    indexPlayerInTurn = 0;
                else
                    indexPlayerInTurn++;
            }
        });

    }

    /**
     * The function fills the gridview with ImageViews containing the cards (3 if the game is Scopa or Briscola and 10 if the game is Tressette).
     *
     * @param gridview Gridpane to fill
     * @param cards    Cards to put in the gridpane
     * @param back     if true it will put the back of the card
     */
    public void generatePlayersCards(GridPane gridview, ArrayList<Card> cards, boolean back, int playerIndex) {

        System.out.println("players: " + playersSorted.toString() + " - " + playersSorted.get(playerIndex).toString());
        int cardToGenerate = 10; //TODO remove
        //int cardToGenerate = currGame.equals("Tressette") ? 10 : 3;

        for (int cardIndex = 0; cardIndex < cardToGenerate; cardIndex++) {
            Card card = back ? new Card(true) : cards.get(cardIndex);

            Image image = new Image(getClass().getResource(card.getImage()).toExternalForm());
            ImageView iv = new ImageView(image);
            iv.setPreserveRatio(true);

            handsWithCardsImView.get(playersSorted.get(playerIndex)).add(iv);


            GridPane.setHalignment(iv, HPos.CENTER);
            GridPane.setValignment(iv, VPos.CENTER);
            GridPane.setMargin(iv, new Insets(0));

            Pane pane = new Pane(iv);

            iv.setFitWidth(gridview.getPrefWidth() / (cardToGenerate+1));
            iv.setFitHeight(gridview.getPrefHeight());
            gridview.addColumn(cardIndex, pane);
            gridview.setAlignment(Pos.CENTER);

            if (!back) {
                pane.setOnMouseEntered(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        iv.setTranslateY(iv.getTranslateY() - 50);
                    }
                });

                pane.setOnMouseExited(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        iv.setTranslateY(iv.getTranslateY() + 50);
                    }
                });


                int finalCardIndex = cardIndex;

                //Se è il turno del giocatore può scegliere le carte
                if (playersTurn.indexOf(SharedData.getInstance().getPlayerName()) == indexPlayerInTurn) {
                    pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        boolean waitUntilAnimation = false;


                        @Override
                        public void handle(MouseEvent mouseEvent) {

                            if (!waitUntilAnimation) {
                                waitUntilAnimation = true;

                                //se non gioco a scopa aggiungo al table per poter usare la funzione riscorsiva per calcolare la presa migliore per scopa
                                if(!currGame.equals("Scopa"))
                                    table.addCard(card);

                                //trova cardIndex aggiornato, per risolvere il problema delle carte in mano che diminuiscono
                                int currentCardIndex = (int) gridview.getChildren().stream().takeWhile(node -> !node.equals(pane)).count();
                                System.out.println("currentCardIndex: " + currentCardIndex);

                                //recupera la posizione globale della carta
                                Translate translate = new Translate();
                                iv.getTransforms().add(translate);


                                //target coordinates
                                int index = findIndexOfFirstEmptyCell();
                                int targetRow = index / tableCols;
                                int targetCol = targetRow == 0 ? index : index % tableCols;

                                //Sending move to server
                                try {
                                    SharedData.getGSCInstance().sendMove("cardToTable", cards.get(finalCardIndex), currentCardIndex, targetRow, targetCol, gridview.getColumnCount());
                                } catch (EncodeException | IOException e) {
                                    System.out.println("Connection error: Could not send move to table.");
                                    throw new RuntimeException(e);
                                }

                                //creo una copia di iv
                                ImageView tmpIV = new ImageView(iv.getImage());
                                tmpIV.setFitWidth(gridview.getPrefWidth() / 11.5);
                                tmpIV.setFitHeight(gridview.getPrefHeight() * 0.9);
                                tmpIV.setVisible(false);


                                System.out.println("Row: " + targetRow + "| col: " + targetCol);

                                //t
                                tableGridPane.getChildren().remove(targetRow + targetCol);
                                tableGridPane.add(tmpIV, targetCol, targetRow);
                                tableSupport.set(index, true);


                                //coordinate globali
                                double startX = iv.localToScene(0, 0).getX();
                                double startY = iv.localToScene(0, 0).getY();
                                double endX = tableGridPane.localToScene(tableGridPane.getBoundsInLocal()).getMinX() + targetCol * tableGridPane.getPrefWidth() / tableCols;
                                double endY = tableGridPane.localToScene(tableGridPane.getBoundsInLocal()).getMinY() + targetRow * tableGridPane.getPrefHeight() / tableRows;

                                System.out.println("endX: " + endX + "| endY: " + endY);

                                //Animazione aggiunta carta al Gridpane del tavolo
                                Timeline cardToTableAnimation = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(translate.xProperty(), 0), new KeyValue(translate.yProperty(), 0)),

                                        new KeyFrame(new Duration(300), e -> {
                                            gridview.getChildren().remove(pane);
                                            tmpIV.setVisible(true);


                                            // Sposta i nodi rimanenti (si potrebbe fare animato ehh)
                                            for (Node node : gridview.getChildren()) {
                                                Integer currCol = GridPane.getColumnIndex(node);
                                                if (currCol != null && currCol > (currentCardIndex - 1)) {
                                                    GridPane.setColumnIndex(node, currCol - 1);
                                                }
                                            }
                                            gridview.getColumnConstraints().removeLast();
                                            waitUntilAnimation = false;


                                        }, new KeyValue(translate.xProperty(), calculateX(0, startX, endX)), new KeyValue(translate.yProperty(), calculateY(0, startY, endY))));

                                cardToTableAnimation.play();

                            }


                        }

                    });
                    //TODO dovrebbe stare dentro setOnMouseClicked
//                    if(currGame.equals("Scopa"))
//                        calculateWinForScopa(card);
//                    else {
//                        //Se l'ultimo del turno ha giocato la carta vedo la presa del turno se gioco a Tressette o Briscola
//                        if (indexPlayerInTurn == playersTurn.size() - 1) {
//                            calculateEndTurnWinner();
//                        }
//                    }
//                    if(indexPlayerInTurn == playersTurn.size() - 1)
//                        indexPlayerInTurn = 0;
//                    else
//                        indexPlayerInTurn++;


                }
            }
        }


    }

    /**
     * calculate Scopa's cards won, remove the cards from the table and move in player's deckplayer
     * @param card
     */
    public void calculateWinForScopa(Card card){
        {
            Player currPlayer = new Player(playersTurn.get(indexPlayerInTurn));
            ArrayList<Card> cardsWon = GameManagerScopa.calculateWinTurn(card, currPlayer, table);
            if(!cardsWon.isEmpty()) {
                //se ho vinto qualche carta le levo dal tavolo e le aggiungo al mazzetto delle carte vinte
                clearCardsFromTable(cardsWon);
                table.getTeam(playersTurn.indexOf(currPlayer.getId()) % 2).getDeckPlayer().addAll(cardsWon);
            }
            else{
                //se non ho preso nessuna carta aggiungo la mia carta al tavolo e al gridpane
                int index = findIndexOfFirstEmptyCell();
                ImageView cardIV = new ImageView(new Image(getClass().getResource(card.getImage()).toExternalForm()));
                tableGridPane.getChildren().add(index, cardIV);
                tableSupport.set(index, true);
                table.addCard(card);
            }
        }
    }

    /**
     * Calculate who wins the turn for Briscola and Tressette and clear the table
     */
    private void calculateEndTurnWinner(){
        switch (currGame) {
            case "Briscola":
                winnerPlayerTurn = GameManagerBriscola.calculateWinnerTurn(table, briscola);

                //Aggiungo le carte vinte del turno al deckplayer della squadra che si è aggiudicata la mano
                table.getTeam(playersTurn.indexOf(winnerPlayerTurn.getId()) % 2).getDeckPlayer().addAll(table.getCards());

                //TODO aggiungere animazione delle carte dal tavolo al giocatore che ha vinto

                //rimuovo le carte dal table e dal gridpane del tavolo
                clearTable();
                break;
            case "Tressette":
                winnerPlayerTurn = GameManagerTressette.calculateWinnerTurn(table);

                //Aggiungo le carte vinte del turno al deckplayer della squadra che si è aggiudicata la mano
                table.getTeam(playersTurn.indexOf(winnerPlayerTurn.getId()) % 2).getDeckPlayer().addAll(table.getCards());
                //TODO aggiungere animazione delle carte dal tavolo al giocatore che ha vinto
                clearTable();
        }
    }

    /**
     * Remove the cards of the cardsToRemove from the table, its gridpane and the tablesupport
     * @param cardsToRemove
     */
    private void clearCardsFromTable(ArrayList<Card> cardsToRemove){
        for(Card c : table.getCards()){
            if(cardsToRemove.contains(c)){
                table.removeCard(c);
                int index = findIndexOfTheCard(c);
                //TODO animazioni carte che si spostano verso il player che le ha vinte
                tableGridPane.getChildren().remove(index);
                tableGridPane.add(new Pane(), index % tableCols, index / tableCols);
                tableSupport.set(index, false);

            }
        }
    }

    /**
     * Return the String of cards'image from the url of the image
     * @param url
     * @return the String of cards'image from the url of the image
     */
    private String getImagefromURL(String url){
        String[] strings = url.split("/");
        String res = strings[strings.length - 2] + "/" + (strings[strings.length - 1]);
        return res;
    }

    /**
     * Find the index of the card in the table gridpane
     * @param card
     * @return index of the card
     */
    private int findIndexOfTheCard(Card card){
        for(int i = 0; i < tableRows * tableCols; i++){
            //se le immagini sono uguali ritorno l'indice
            if((getImagefromURL(((ImageView)tableGridPane.getChildren().get(i)).getImage().getUrl()).equals(card.getImage()))){
                return i;
            }
        }
        return -1;
    }

    /**
     * Clear the table, its gridpane and tableSupport
     */
    private void clearTable(){
        table.getCards().clear();
        for(int i = 0; i < tableCols * tableRows; i++){
            if(tableSupport.get(i)){
                tableGridPane.getChildren().remove(i);
                tableGridPane.add(new Pane(), i % tableCols, i / tableCols);
                tableSupport.set(i, false);
            }
        }

    }

    ///TODO rifalla in modo intelligente
    private void sortPlayersSorted() {
        String me = SharedData.getInstance().getPlayerName();

        ObservableList<String> tmp = FXCollections.observableArrayList();
        tmp.add(me);

        int indexOfme = playersSorted.indexOf(me);
        for (int index = indexOfme + 1; index < playersSorted.size(); index++) {
            tmp.add(playersSorted.get(index));
        }

        for (int j = 0; j < indexOfme; j++) {
            tmp.add(playersSorted.get(j));
        }

        playersSorted.clear();
        playersSorted.addAll(tmp);
    }

    private int findIndexOfFirstEmptyCell() {
        int index = 0;
        for (Boolean cellaOccupata : tableSupport) {
            if (!cellaOccupata) {
                System.out.println("index: " + index);
                return index;
            }
            index++;
        }
        return -1;
    }


    public void configureTableGridPane(GridPane table) {
        table.setGridLinesVisible(true);
        table.setAlignment(Pos.CENTER);

        for (int i = 0; i < tableCols; i++) {
            for (int j = 0; j < tableRows; j++) {
                table.add(new Pane(), i, j);
                tableSupport.add(false);
            }
        }
    }

    /**
     * Configure the teams and the players in the table
     * @param table
     */
    public void configureTable(Table table) {
        //table = new Table(new ArrayList<Player>(2), new ArrayList<Player>());
        table.setTeams(new ArrayList<>(2));
        table.getTeams().add(new Player(playersTurn.get(0)));
        table.getTeams().add(new Player(playersTurn.get(1)));

        for(String Id : playersTurn){
            table.getPlayers().add(new Player(Id));
        }
    }

    /**
     * Set the color of the label
     */
    public void setNamesLabelColor(){
        if (twoPlayers) {
            if(SharedData.getInstance().getLobbyPlayers().indexOf(playersSorted.getFirst()) % 2 == 0){
                System.out.println(playersSorted.getFirst() + " dentro l'if--viola");
                namePlayer.setStyle("-fx-text-fill: #521a6a");
                namePlayer2.setStyle("-fx-text-fill: #6a1a32");
            }
            else {
                System.out.println(playersSorted.getFirst() + " dentro l'else--magenta");
                namePlayer.setStyle("-fx-text-fill: #6a1a32");
                namePlayer2.setStyle("-fx-text-fill: #521a6a");
            }
        }
        else {
            if (SharedData.getInstance().getLobbyPlayers().indexOf(playersSorted.getFirst()) % 2 == 0) {
                namePlayer.setStyle("-fx-text-fill: #521a6a");
                namePlayer2.setStyle("-fx-text-fill: #521a6a");
                namePlayer1.setStyle("-fx-text-fill: #6a1a32");
                namePlayer3.setStyle("-fx-text-fill: #6a1a32");
            } else {
                namePlayer1.setStyle("-fx-text-fill: #521a6a");
                namePlayer3.setStyle("-fx-text-fill: #521a6a");
                namePlayer.setStyle("-fx-text-fill: #6a1a32");
                namePlayer2.setStyle("-fx-text-fill: #6a1a32");
            }
        }
    }
    /**
     * Generate the hands of the players
     */
    public void generateHands() {
        try {

            ArrayList<GridPane> hands = new ArrayList<>();

            if (twoPlayers) {
                hands.add(hand);            //mano giocatore principale
                hands.add(handPlayer2);     //mano giocatore difronte
                namePlayer.setText(playersSorted.getFirst());
                namePlayer2.setText(playersSorted.getLast());
                namePlayer1.setVisible(false);
                namePlayer3.setVisible(false);

            } else {
                //4 giocatori
                hands.add(hand);            //mano giocatore principale
                hands.add(handPlayer1);     //mano giocatore a sx
                hands.add(handPlayer2);     //mano giocatore difronte
                hands.add(handPlayer3);     //mano giocatore a dx
                namePlayer.setText(playersSorted.getFirst());
                namePlayer1.setText((playersSorted.get(1)));
                namePlayer2.setText((playersSorted.get(2)));
                namePlayer3.setText(playersSorted.getLast());
            }

            setNamesLabelColor();

            for (GridPane g : hands) {
                g.setGridLinesVisible(true);
                g.setAlignment(Pos.CENTER);
                setupGridPaneCols(g);
            }

            int tmp = 0;
            for (String clientName : playersSorted) {
                handsWithGridPane.put(clientName, hands.get(tmp));
                handsWithCardsImView.put(clientName, new ArrayList<>());

                if (tmp == 0) {
                    generatePlayersCards(hand, new ArrayList<>(SharedData.getInstance().getPlayerCards()), false, 0);
                } else {
                    generatePlayersCards(hands.get(tmp), new ArrayList<Card>(), true, tmp);
                }
                tmp++;
            }
        } catch (RuntimeException e) {
            System.out.println(e);
        }
    }

    @FXML
    void createLobby(ActionEvent event) {

    }

    Number calculateY(int position,double startY, double endY) {
        return switch (position){
            case 0 -> endY-startY;
            case 1 -> (endY-startY);
            case 2 -> -(endY-startY);
            default -> {

                System.out.println("def");
                yield 5;
            }
        };
    }

    Number calculateX(int position, double startX, double endX) {
        return switch (position){
            case 0 -> endX-startX;
            case 1 -> (endX-startX);
            case 2 -> -(endX-startX);
            default -> {

                System.out.println("def");
                yield 5;
            }
        };


        //        if (startX < endX) {
//            return Math.abs(endX - startX);
//        } else {
//            return -Math.abs(endX);
//        }


    }

    private void setupGridPaneCols(GridPane gridPane) {
        // Rimuovi eventuali RowConstraints esistenti
        gridPane.getColumnConstraints().clear();
//        int numberOfCards = currGame.equals("Tressette") ? 10 : 3;

        for (int i = 0; i < 10; i++) {
            ColumnConstraints cols = new ColumnConstraints();
            cols.setPercentWidth(10);
            gridPane.getColumnConstraints().add(cols);
        }
        gridPane.setHgap(0);
        gridPane.setVgap(0);
    }

}

