package it.MM.LeTreCarte;

import com.google.gson.JsonObject;
import it.MM.LeTreCarte.model.GameManagers.GameManagerBriscola;
import it.MM.LeTreCarte.model.GameManagers.GameManagerScopa;
import it.MM.LeTreCarte.model.GameManagers.GameManagerTressette;
import it.MM.LeTreCarte.model.card.Card;
import it.MM.LeTreCarte.model.card.cardcontainer.Hand;
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
import javafx.util.Pair;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Semaphore;


public class TableController implements Initializable {
    //contiene i giocatori sortati per inserirli a schermo
    static ArrayList<String> playersSorted = new ArrayList<>() {{
        addAll(SharedData.getInstance().getLobbyPlayers());
    }};
    //fixed dimensioni della grid Table
    final static int TABLE_ROWS = 3;
    final static int TABLE_COLS = 5;

    final HashMap<String, Double> sizeOfCardsInTable = new HashMap<>();
    // { nomeGiocatore1: [imgvw1, imgvw2], nomeGiocatore2: [...] }
    private final HashMap<String, ArrayList<ImageView>> handsWithCardsImView = new HashMap<>();
    private final HashMap<String, GridPane> handsWithGridPane = new HashMap<>();
    //indice del giocatore che deve giocare
    public int indexPlayerInTurn = 0;
    String currGame = SharedData.getInstance().getSelectedGame();
    //contiene false se non è presente nessuna carta, true viceversa
    ArrayList<Boolean> tableSupport = new ArrayList<>(TABLE_COLS * TABLE_ROWS);
    boolean twoPlayers = playersSorted.size() == 2;
    int cardGenerated = 0;
    Character briscola;
    Player winnerPlayerTurn; //Il giocatore che vince il turno
    @FXML
    private Label MaradonaPoints;

    @FXML
    private Label VesuvioPoints;

    @FXML
    private StackPane deckStackPane;

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

    @FXML
    private AnchorPane root;

    @FXML
    private GridPane tableGridPane;

    @FXML
    private AnchorPane teamMaradona;

    @FXML
    private AnchorPane teamVesuvio;

    @FXML
    private AnchorPane teamsWin;

    @FXML
    private ImageView winnerTeamImage;

    @FXML
    private Label winnerTeamLabel;
    @FXML
    private Label pointsCarteMaradona;

    @FXML
    private Label pointsCarteVesuvio;

    @FXML
    private Label pointsDenariMaradona;

    @FXML
    private Label pointsDenariVesuvio;

    @FXML
    private AnchorPane pointsEndTurnScopaAnchorPane;

    @FXML
    private Label pointsPrimieraMaradona;

    @FXML
    private Label pointsPrimieraVesuvio;

    @FXML
    private Label pointsSettebelloMaradona;

    @FXML
    private Label pointsSettebelloVesuvio;
    @FXML
    private Label pointsScopaMaradona;

    @FXML
    private Label pointsScopaVesuvio;

    private Player lastWinnerPlayerScopa = new Player();
    //private final int[4][2] positionCardsForTressetteAndBriscola


    private ArrayList<Label> names = new ArrayList<Label>() {{
        add(namePlayer);
        add(namePlayer1);
        add(namePlayer2);
        add(namePlayer3);
    }};

    private Table table = new Table();
    private Hand handPlayer = new Hand(currGame.equals("Tressette") ? 10 : 3);

    private ArrayList<GridPane> handsGridPanes = new ArrayList<>();
    //contiene i giocatori in ordine di turno
    private ArrayList<String> playersTurn = new ArrayList<>() {{
        addAll(SharedData.getInstance().getLobbyPlayers());
    }};

    public static void waitForRunLater() throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        Platform.runLater(() -> semaphore.release());
        semaphore.acquire();

    }

    public void updatePointsLabel() {
        MaradonaPoints.setText(String.valueOf(table.getTeam(0).getPoints()));
        VesuvioPoints.setText(String.valueOf(table.getTeam(1).getPoints()));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        startBackgroundListener();
        sortPlayersSorted();

        configureTableGridPane(tableGridPane);
        configureTable(table);

        updatePointsLabel();

        generateHands();

        putCardsOnTable(new ArrayList<>(SharedData.getInstance().getCardsOnTable()));
        if (cardGenerated != 40) {
            ImageView back = new ImageView(new Image(getClass().getResource("Cards_png/back.png").toExternalForm()));
            deckStackPane.getChildren().add(back);
        }

        //Salva il seme della briscola
        if (currGame.equals("Briscola")) {
            //TODO deve prendere l'ultima carta del mazzo
            briscola = 'C'; //il seme dell'ultima carta del mazzo
        }

    }

    private void putCardsOnTable(ArrayList<Card> cardsToPut) {
        for (int i = 0; i < cardsToPut.size(); i++) {
            Card card = cardsToPut.get(i);
            ImageView iv = new ImageView(new Image(getClass().getResource(card.getImage()).toExternalForm()));
            iv.setPreserveRatio(true);

            iv.setFitWidth(sizeOfCardsInTable.get("width"));
            iv.setFitHeight(sizeOfCardsInTable.get("height"));

            tableGridPane.getChildren().remove(i);
            tableGridPane.add(iv, i, 0);
            tableSupport.set(i, true);
            table.addCard(card);
            cardGenerated++;
        }
    }

    /**
     * Starts the processes that handles responses of server
     * type of responses:
     * - type:move when a player put a card on the table
     * - type:cardsOnTable when the server needs to refresh current cards on table
     * - type:yourTurn when the server notify that is the turn of a player
     */
    private void startBackgroundListener() {
        SharedData.getInstance().getMoves().addListener(new ListChangeListener<JsonObject>() {
            @Override
            public void onChanged(Change<? extends JsonObject> c) {

                Platform.runLater(()->{
                    JsonObject response = SharedData.getInstance().getMoves().getLast();

                    if (Objects.equals(response.get("type").getAsString(), "move") && !Objects.equals(response.get("clientAKA").getAsString(), SharedData.getInstance().getPlayerName())) {
                        System.out.println("sono entrato nell'if");

                        int cardVal = response.get("card-val").getAsInt();
                        Character cardSeed = response.get("card-seed").getAsString().toCharArray()[0];

                        String clientAKA = response.get("clientAKA").getAsString();
                        int cardIndexInHand = response.get("cardIndexInHand").getAsInt();

                        System.out.println(clientAKA);
                        System.out.println(handsWithGridPane);
                        System.out.println(handsWithCardsImView);


                        //#
                        ImageView cardToMove = handsWithCardsImView.get(clientAKA).get(cardIndexInHand);

                        //Card card = cardFromUrl(handsWithCardsImView.get(clientAKA).get(cardIndexInHand).getImage().getUrl());
                        handsWithCardsImView.get(clientAKA).remove(cardIndexInHand);


                        int targetCol = response.get("targetCol").getAsInt();
                        int targetRow = response.get("targetRow").getAsInt();
                        GridPane gridviewAKA = handsWithGridPane.get(clientAKA);


                        Translate translate2 = new Translate();
                        cardToMove.getTransforms().add(translate2);

                        //coordinate globali
                        double startX = cardToMove.localToScene(0, 0).getX();
                        double startY = cardToMove.localToScene(0, 0).getY();
                        double endX = tableGridPane.localToScene(tableGridPane.getBoundsInLocal()).getMinX() + targetCol * tableGridPane.getPrefWidth() / TABLE_COLS;
                        double endY = tableGridPane.localToScene(tableGridPane.getBoundsInLocal()).getMinY() + targetRow * tableGridPane.getPrefHeight() / TABLE_ROWS;

                        //creo una copia di iv
                        Card card = new Card(cardVal, cardSeed);
                        ImageView cardInTable = new ImageView(new Image(getClass().getResource(card.getImage()).toExternalForm()));
                        cardInTable.setPreserveRatio(true);
                        cardInTable.setFitWidth(sizeOfCardsInTable.get("width"));
                        cardInTable.setFitHeight(sizeOfCardsInTable.get("height"));
//                        cardInTable.setFitWidth(hand.getPrefWidth() / 11.5);
//                        cardInTable.setFitHeight(hand.getPrefHeight() * 0.9);
                        cardInTable.setVisible(false);

                        tableGridPane.getChildren().remove(targetRow * TABLE_COLS + targetCol);
                        tableGridPane.add(cardInTable, targetCol, targetRow);
                        tableSupport.set(targetRow * TABLE_COLS + targetCol, true);
                        if (!currGame.equals("Scopa")) {
                            table.addCard(card);
                        }


                        //create animation
                        Timeline cardToTableAnimationENEMY = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(translate2.xProperty(), 0), new KeyValue(translate2.yProperty(), 0)),

                                new KeyFrame(new Duration(300), e -> {
                                    System.out.println(gridviewAKA.getChildren().size() + " - " + gridviewAKA.getColumnConstraints().size());
                                    cardInTable.setVisible(true);
                                    cardToMove.setVisible(false);

                                    //gridviewAKA.getChildren().removeLast();
                                    gridviewAKA.getChildren().remove(cardToMove);
                                    System.out.println(STR."rimosso - \{gridviewAKA.getChildren().size()}");


                                    // Sposta i nodi rimanenti (si potrebbe fare animato ehh)
                                    for (Node node : gridviewAKA.getChildren()) {
                                        System.out.println("sono nel for");
                                        Integer currCol = GridPane.getColumnIndex(node);
                                        System.out.println(currCol + " - " + cardIndexInHand);
                                        if (currCol != null && currCol > (cardIndexInHand)) {
                                            System.out.println("sono nell'if del for"+ currCol);
                                            GridPane.setColumnIndex(node, currCol - 1);
                                        }
                                    }
                                    gridviewAKA.getColumnConstraints().removeLast();
                                    System.out.println(gridviewAKA.getChildren().size() + " - " + gridviewAKA.getColumnConstraints().size());



                                }, new KeyValue(translate2.xProperty(), calculateX(playersSorted.indexOf(clientAKA), startX, endX)), new KeyValue(translate2.yProperty(), calculateY(playersSorted.indexOf(clientAKA), startY, endY)))


                        );

                        cardToTableAnimationENEMY.play();

                        cardToTableAnimationENEMY.setOnFinished(new EventHandler<ActionEvent>() {

                            @Override
                            public void handle(ActionEvent event) {
                                System.out.println("Ricevuto: " + cardVal + cardSeed);
                                Card card = new Card(cardVal, cardSeed);
                                try {
                                    updateAndCalculateTurn(card);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }

                            }
                        });
                        indexPlayerInTurn = (indexPlayerInTurn == (playersTurn.size() - 1) ? 0 : indexPlayerInTurn + 1);
                    }

                });






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
        int cardToGenerate = cards.size();

        for (int cardIndex = 0; cardIndex < cardToGenerate; cardIndex++) {
            cardGenerated++;
            System.out.println("cardGenerated: " + cardGenerated);

            //pulizia gridpane
            gridview.getChildren().removeIf(node -> !(node instanceof ImageView || node instanceof Pane));


            Card card = back ? new Card(true) : cards.get(cardIndex);
            if (!back) handPlayer.addCard(card);

            Image image = new Image(getClass().getResource(card.getImage()).toExternalForm());
            ImageView iv = new ImageView(image);
            iv.setPreserveRatio(true);

            handsWithCardsImView.get(playersSorted.get(playerIndex)).add(iv);

            Pane pane = new Pane(iv);
            GridPane.setHalignment(pane, HPos.CENTER);
            GridPane.setValignment(pane, VPos.CENTER);
            GridPane.setMargin(pane, new Insets(0));

            //iv.setFitWidth(gridview.getPrefWidth() / (cardToGenerate+1));
            iv.setFitHeight(gridview.getPrefHeight());

            ColumnConstraints c = new ColumnConstraints();
            c.setPercentWidth(10);
            c.setHalignment(HPos.CENTER);
            gridview.getColumnConstraints().add(c);
            gridview.setAlignment(Pos.CENTER);
            setupGridPaneCols(gridview);
            if (cardToGenerate == 1) {
                if(!back){
                    gridview.addColumn(gridview.getChildren().size(), pane);
                }else{
                    gridview.addColumn(gridview.getChildren().size(), iv);
                }

            } else {
                if(!back){
                    gridview.addColumn(cardIndex, pane);
                }else{
                    gridview.addColumn(cardIndex, iv);
                }

            }


            gridview.setAlignment(Pos.CENTER);
            System.out.println("ok");

            if (sizeOfCardsInTable.isEmpty()) {
                sizeOfCardsInTable.put("width", gridview.getPrefWidth() / 11.5);
                sizeOfCardsInTable.put("height", gridview.getPrefHeight() * 0.9);
            }

            if (!back) {
                pane.setOnMouseEntered(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        //System.out.println(indexPlayerInTurn+" - "+playersTurn.indexOf(SharedData.getInstance().getPlayerName()));
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
                pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    boolean waitUntilAnimation = false;

                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        System.out.println("click");
                        if (playersTurn.indexOf(SharedData.getInstance().getPlayerName()) == indexPlayerInTurn && !waitUntilAnimation) {
                            waitUntilAnimation = true;
                            handsWithCardsImView.get(playersSorted.get(playerIndex)).remove(iv);
                            handPlayer.removeCard(card);

                            //se non gioco a scopa aggiungo al table per poter usare la funzione riscorsiva per calcolare la presa migliore per scopa
                            if (!currGame.equals("Scopa")) {
                                table.addCard(card);
                            }

                            //trova cardIndex aggiornato, per risolvere il problema delle carte in mano che diminuiscono
                            int currentCardIndex = 0;
                            try {
                                currentCardIndex = Objects.requireNonNull(findRowColumnOfCardInGridPane(card, gridview)).getKey();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }

                            //int currentCardIndex = (int) gridview.getChildren().stream().takeWhile(node -> !node.equals(pane)).count();
                            System.out.println("currentCardIndex: " + currentCardIndex);

                            //recupera la posizione globale della carta
                            Translate translate = new Translate();
                            iv.getTransforms().add(translate);


                            //target coordinates
                            int index = findIndexOfFirstEmptyCell();
                            int targetRow = index / TABLE_COLS;
                            int targetCol = targetRow == 0 ? index : index % TABLE_COLS;

                            //Sending move to server
                            try {
                                SharedData.getGSCInstance().sendMove("cardToTable", cards.get(finalCardIndex), currentCardIndex, targetRow, targetCol, gridview.getColumnCount());
                            } catch (EncodeException | IOException e) {
                                System.out.println("Connection error: Could not send move to table.");
                                throw new RuntimeException(e);
                            }

                            //creo una copia di iv
                            ImageView tmpIV = new ImageView(iv.getImage());

                            tmpIV.setFitWidth(sizeOfCardsInTable.get("width"));
                            tmpIV.setFitHeight(sizeOfCardsInTable.get("height"));
                            tmpIV.setVisible(false);


                            System.out.println("Row: " + targetRow + "| col: " + targetCol);

                            //t
                            tableGridPane.getChildren().remove(iv);
                            tableGridPane.add(tmpIV, targetCol, targetRow);
                            tableSupport.set(index, true);


                            //updateAndCalculateTurn(card);


                            //coordinate globali
                            double startX = iv.localToScene(0, 0).getX();
                            double startY = iv.localToScene(0, 0).getY();
                            double endX = tableGridPane.localToScene(tableGridPane.getBoundsInLocal()).getMinX() + targetCol * tableGridPane.getPrefWidth() / TABLE_COLS;
                            double endY = tableGridPane.localToScene(tableGridPane.getBoundsInLocal()).getMinY() + targetRow * tableGridPane.getPrefHeight() / TABLE_ROWS;

                            System.out.println("endX: " + endX + "| endY: " + endY);

                            //Animazione aggiunta carta al Gridpane del tavolo
                            int finalCurrentCardIndex = currentCardIndex;
                            Timeline cardToTableAnimation = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(translate.xProperty(), 0), new KeyValue(translate.yProperty(), 0)),

                                    new KeyFrame(new Duration(300), e -> {
                                        gridview.getChildren().remove(pane);
                                        tmpIV.setVisible(true);


                                        // Sposta i nodi rimanenti (si potrebbe fare animato ehh)
                                        for (Node node : gridview.getChildren()) {
                                            Integer currCol = GridPane.getColumnIndex(node);
                                            if (currCol != null && currCol > (finalCurrentCardIndex - 1)) {
                                                GridPane.setColumnIndex(node, currCol - 1);
                                            }
                                        }
                                        gridview.getColumnConstraints().removeLast();


                                        waitUntilAnimation = false;


                                    }, new KeyValue(translate.xProperty(), calculateX(0, startX, endX)), new KeyValue(translate.yProperty(), calculateY(0, startY, endY))));

                            cardToTableAnimation.play();
                            cardToTableAnimation.setOnFinished(new EventHandler<>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    Platform.runLater(() -> {
                                        try {
                                            updateAndCalculateTurn(card);
                                        } catch (InterruptedException e) {
                                            throw new RuntimeException(e);
                                        }

                                    });
                                    indexPlayerInTurn = (indexPlayerInTurn == (playersTurn.size() - 1) ? 0 : indexPlayerInTurn + 1);
                                }


                            });
//                            try {
//                                wait(5000);
//                            } catch (InterruptedException e) {
//                                throw new RuntimeException(e);
//                            }
//                            teamsWin.setVisible(false);
//                            pointsEndTurnScopaAnchorPane.setVisible(false);
                        }

                    }


                });

            }
        }
    }

    public void updateAndCalculateTurn(Card card) throws InterruptedException {
        if (currGame.equals("Scopa")) calculateWinForScopa(card);
        else {
            //Se l'ultimo del turno ha giocato la carta vedo la presa del turno se gioco a Tressette o Briscola
            if (indexPlayerInTurn == playersTurn.size() - 1) {
                calculateEndTurnWinner();
            }
        }

        if (indexPlayerInTurn == playersTurn.size() - 1) {
            if (cardGenerated == 40 && handPlayer.getCards().isEmpty()) {
                table.addCard(card);
                table.getTeam(SharedData.getInstance().getLobbyPlayers().indexOf(lastWinnerPlayerScopa.getId())).getDeckPlayer().getCards().addAll(table.getCards());
                clearTable();
                calculatePoints();
            } else {
                drawCards();
                if (cardGenerated == 40) {
                    System.out.println("Levo il mazzo");
                    deckStackPane.getChildren().clear();
                }
                System.out.println("cardGen: "+ cardGenerated);
            }
        }
    }


    private void restoreGridPaneConstraints(boolean withoutMyHand){
        for(int i=(withoutMyHand ? 1 : 0);i<handsWithGridPane.size();i++){
            setupGridPaneCols(handsWithGridPane.get(playersSorted.get(i)));
        }
    }

    public void drawCards() {

        switch (currGame) {
            case "Scopa": {
                if (handPlayer.getCards().isEmpty()) {
                    try {
                        System.out.println("ENTRATO");
                        SharedData.getGSCInstance().requestCards(3);
                        SharedData.getInstance().getPlayerCards().addListener(new ListChangeListener<it.MM.LeTreCarte.model.card.Card>() {
                            boolean done = false;
                            @Override
                            public void onChanged(Change<? extends it.MM.LeTreCarte.model.card.Card> change) {
                                if (!done && SharedData.getInstance().getPlayerCards().size() == 3) {
                                    Platform.runLater(() -> {
                                        restoreGridPaneConstraints(false);
                                        hand.getChildren().clear();

                                        generatePlayersCards(hand, new ArrayList<Card>(SharedData.getInstance().getPlayerCards()), false, 0);
                                        System.out.println("carte mie");
                                        for (int i = 1; i < handsGridPanes.size(); i++) {
                                            generatePlayersCards(handsGridPanes.get(i), new ArrayList<>(SharedData.getInstance().getPlayerCards()), true, i);
                                            System.out.println("carte altri - "+i);
                                        }
                                        done=true;
                                    });
                                }
                            }
                        });

                    } catch (Exception _) {
                        System.out.println("RequestCards Error");
                    }
                }
            }
            break;
            default: {
                try {
                    SharedData.getGSCInstance().requestCards(1);
                    SharedData.getInstance().getPlayerCards().addListener(new ListChangeListener<it.MM.LeTreCarte.model.card.Card>() {
                        @Override
                        public void onChanged(Change<? extends it.MM.LeTreCarte.model.card.Card> change) {
                            Platform.runLater(() -> {
                                //restoreGridPaneConstraints(false);
                                System.out.println("default - " + SharedData.getInstance().getPlayerCards());

                                System.out.println("THIS IS MY HAND: " + handPlayer);
                                if (SharedData.getInstance().getPlayerCards().size() == 1) {
                                    ArrayList<Card> cardsToGenerate = new ArrayList<>();
                                    cardsToGenerate.add(SharedData.getInstance().getPlayerCards().getFirst());

                                    if (!handPlayer.getLast().equals(cardsToGenerate.getFirst())) {

                                        System.out.println("if");
                                        //hand.getChildren().clear();


                                        System.out.println("CArds to generate " + cardsToGenerate);
                                        generatePlayersCards(hand, cardsToGenerate, false, 0);
                                        System.out.println("carte mie 2");
                                        for (int i = 1; i < handsGridPanes.size(); i++) {
                                            System.out.println("before: "+handsGridPanes.get(i).getColumnConstraints().size());
                                            generatePlayersCards(handsGridPanes.get(i), cardsToGenerate, true, i);
                                            System.out.println("carte altri 2 - "+i);
                                            System.out.println("after: "+handsGridPanes.get(i).getColumnConstraints().size());
                                        }
                                    }
                                }

                            });

                        }
                    });

                } catch (Exception _) {
                    System.out.println("RequestCards Error");
                }

            }
        }


    }

    private void showAndFillScopaEndTurnAnchorPane() {
        pointsEndTurnScopaAnchorPane.setVisible(true);
        pointsCarteMaradona.setText(String.valueOf(GameManagerScopa.pointsForTeam[0][0]));
        pointsDenariMaradona.setText(String.valueOf(GameManagerScopa.pointsForTeam[0][1]));
        pointsSettebelloMaradona.setText(String.valueOf(GameManagerScopa.pointsForTeam[0][2]));
        pointsPrimieraMaradona.setText(String.valueOf(GameManagerScopa.pointsForTeam[0][3]));
        pointsCarteVesuvio.setText(String.valueOf(GameManagerScopa.pointsForTeam[1][0]));
        pointsDenariVesuvio.setText(String.valueOf(GameManagerScopa.pointsForTeam[1][1]));
        pointsSettebelloVesuvio.setText(String.valueOf(GameManagerScopa.pointsForTeam[1][2]));
        pointsPrimieraVesuvio.setText(String.valueOf(GameManagerScopa.pointsForTeam[1][3]));
        pointsScopaVesuvio.setText(String.valueOf(GameManagerScopa.scope[1]));
        pointsScopaMaradona.setText(String.valueOf(GameManagerScopa.scope[0]));
        GameManagerScopa.scope = new Integer[]{0,0};
    }

    private void calculatePoints() throws InterruptedException {
        ArrayList<Card> tmp = new ArrayList<>(table.getTeam(0).getDeckPlayer().getCards());
        table.getTeam(0).getDeckPlayer().getCards().clear();
        table.getTeam(0).getDeckPlayer().getCards().addAll(table.getTeam(1).getDeckPlayer().getCards());

        table.getTeam(1).getDeckPlayer().getCards().clear();
        table.getTeam(1).getDeckPlayer().getCards().addAll(tmp);

        switch (currGame) {
            case "Scopa":
                GameManagerScopa.calculatePointsScopa(table);
                showAndFillScopaEndTurnAnchorPane();
                updatePointsLabel();

                if (table.getTeam(0).getPoints() >= 21 && table.getTeam(0).getPoints() > table.getTeam(1).getPoints()) {
                    showAndFIllTeamsWinAnchorPane(table.getTeam(0));
                } else if (table.getTeam(1).getPoints() >= 21) {
                    showAndFIllTeamsWinAnchorPane(table.getTeam(1));
                }

                break;
            case "Briscola":
                GameManagerBriscola.calculatePoints(table);
                if (table.getTeam(0).getPoints() >= 120 && table.getTeam(0).getPoints() > table.getTeam(1).getPoints()) {
                    showAndFIllTeamsWinAnchorPane(table.getTeam(0));
                } else if (table.getTeam(1).getPoints() >= 120) {
                    showAndFIllTeamsWinAnchorPane(table.getTeam(1));
                }
                break;
            case "Tressette":
                GameManagerTressette.calculatePoints(table);
                if (table.getTeam(0).getPoints() >= 21 && table.getTeam(0).getPoints() > table.getTeam(1).getPoints()) {
                    showAndFIllTeamsWinAnchorPane(table.getTeam(0));
                } else if (table.getTeam(1).getPoints() >= 21) {
                    showAndFIllTeamsWinAnchorPane(table.getTeam(1));
                }
                break;
        }
        updatePointsLabel();
        table.getTeam(0).getDeckPlayer().clear();
        table.getTeam(1).getDeckPlayer().clear();

    }

    /**
     * Show and update the anchorpane of the winner
     *
     * @param team winnerTeam
     */
    private void showAndFIllTeamsWinAnchorPane(Player team) {
        if (SharedData.getInstance().getLobbyPlayers().indexOf(team.getId()) == 0) {
            winnerTeamImage.setImage(new Image(getClass().getResource("Images/MaradonaIcon.png").toExternalForm()));
            winnerTeamLabel.setText("TEAM MARADONA");
            teamsWin.setStyle("-fx-background-color: #521a6a");
        } else {
            winnerTeamImage.setImage(new Image(getClass().getResource("Images/VesuvioIcon.png").toExternalForm()));
            winnerTeamLabel.setText("TEAM VESUVIO");
            teamsWin.setStyle("-fx-background-color: #6a1a32");
        }

        teamsWin.setVisible(true);
    }

    /**
     * calculate Scopa's cards won, remove the cards from the table and move in player's deckplayer
     *
     * @param card
     */
    public void calculateWinForScopa(Card card) {
        {
            Platform.runLater(() -> {
                Player currPlayer = new Player(playersTurn.get(indexPlayerInTurn));
                System.out.println("--------------------------------------------------------indice: "+indexPlayerInTurn);
                ArrayList<Card> cardsWon = GameManagerScopa.calculateWinTurn(card, table.getTeam(indexPlayerInTurn % 2), table);
                if (!cardsWon.isEmpty()) {
                    lastWinnerPlayerScopa.setId(table.getTeam(indexPlayerInTurn % 2).getId());
                    System.out.println("carte vinte da :" + currPlayer.getId() + "::" + cardsWon + " " + cardsWon.size());
                    //se ho vinto qualche carta le levo dal tavolo e le aggiungo al mazzetto delle carte vinte

                    try {
                        Thread.sleep(800); //Per non far scomparire le carte subito

                        clearCardsFromTable(cardsWon);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    table.getTeam(SharedData.getInstance().getLobbyPlayers().indexOf(currPlayer.getId()) % 2).getDeckPlayer().getCards().addAll(cardsWon);
                    System.out.println("MAzzetto squadra: " + table.getTeam(SharedData.getInstance().getLobbyPlayers().indexOf(currPlayer.getId()) % 2).getDeckPlayer().getCards().size() + " :::" + table.getTeam(SharedData.getInstance().getLobbyPlayers().indexOf(currPlayer.getId()) % 2).getDeckPlayer().getCards());
                } else {
                    table.addCard(card);
                }

                System.out.println("SCOPE : MARADONA " + GameManagerScopa.scope[0] + " " + "VESUVIO " + GameManagerScopa.scope[1]);
            });
        }
    }

    /**
     * Calculate who wins the turn for Briscola and Tressette and clear the table
     */
    private void calculateEndTurnWinner() {
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
                table.getTeam(SharedData.getInstance().getLobbyPlayers().indexOf(winnerPlayerTurn.getId()) % 2).getDeckPlayer().getCards().addAll(table.getCards());
                //TODO aggiungere animazione delle carte dal tavolo al giocatore che ha vinto
                System.out.println("carte vinte " + table.getCards() + " carte totali "+  table.getTeam(SharedData.getInstance().getLobbyPlayers().indexOf(winnerPlayerTurn.getId()) % 2).getDeckPlayer().getCards());
                clearTable();
        }
    }

    /**
     * Remove the cards of the cardsToRemove from the table, its gridpane and the tablesupport
     *
     * @param cardsToRemove
     */
    private void clearCardsFromTable(ArrayList<Card> cardsToRemove) throws InterruptedException {
        table.addCard(cardsToRemove.getLast());
        System.out.println("table:::" + table.getCards().toString());

//        cardsToRemove.forEach(card -> table.removeCard(card));
//
//        tableGridPane.getChildren().clear();
//
//        for (int i = 0; i < TABLE_COLS * TABLE_ROWS; i++) {
//
//            int targetRow = i / TABLE_COLS;
//            int targetCol = targetRow == 0 ? i : i % TABLE_COLS;
//            tableGridPane.add(new Pane(), targetCol, targetRow);
//            tableSupport.set(i, false);
//        }
//
//        for (int i = 0; i < table.getCards().size(); i++) {
//            Card card = table.getCards().get(i);
//            ImageView iv = new ImageView(new Image(getClass().getResource(card.getImage()).toExternalForm()));
//            iv.setPreserveRatio(true);
//
//            iv.setFitWidth(sizeOfCardsInTable.get("width"));
//            iv.setFitHeight(sizeOfCardsInTable.get("height"));
//
//            tableGridPane.getChildren().remove(0, table.getCards().size());
//            tableGridPane.add(iv, i, 0);
//            tableSupport.set(i, true);
//        }

        for (int i = 0; i < cardsToRemove.size(); i++) {
            System.out.println(i + " card " + cardsToRemove.get(i));
            System.out.println("TableSupport:: " + tableSupport);
            if (table.contains(cardsToRemove.get(i))) {
                table.removeCard(cardsToRemove.get(i));
                System.out.println("card to remove" + cardsToRemove.get(i));
                //int index = table.getCards().indexOf(c);
                //TODO animazioni carte che si spostano verso il player che le ha vinte

                //Pair(TargetCol, TargetRow)
                Pair<Integer, Integer> coordinates = findRowColumnOfCardInGridPane(cardsToRemove.get(i), tableGridPane);
                assert coordinates != null;
                removeNodeByRowColumnIndex(coordinates.getValue(), coordinates.getKey(), tableGridPane);

                int index = coordinates.getValue() * TABLE_COLS + coordinates.getKey();
                int targetRow = index / TABLE_COLS;
                int targetCol = targetRow == 0 ? index : index % TABLE_COLS;
                System.out.println("New table " + table.getCards());
                tableGridPane.add(new Pane(), targetCol, targetRow);
                tableSupport.set(index, false);
            }
        }
    }

    public void removeNodeByRowColumnIndex(final int row, final int column, GridPane gridPane) {

        ObservableList<Node> childrens = gridPane.getChildren();
        for (Node node : childrens) {
            if (node instanceof ImageView && GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {
                ImageView imageView = (ImageView) (node); // use what you want to remove
                gridPane.getChildren().remove(imageView);
                break;
            }
        }
    }

    /**
     * Return the String of cards'image from the url of the image
     *
     * @param url
     * @return the String of cards'image from the url of the image
     */
    private Card getCardfromURL(String url) {
        String[] strings = url.split("/");
        String res = strings[strings.length - 2] + "/" + (strings[strings.length - 1]);
        if (strings[strings.length - 1].charAt(1) == '0') {
            return new Card(10, strings[strings.length - 1].charAt(3));
        }
        return new Card(Character.getNumericValue(strings[strings.length - 1].charAt(0)), strings[strings.length - 1].charAt(2));
    }


    /**
     * Clear the table, its gridpane and tableSupport
     */
    private void clearTable() {
        System.out.println(table.getCards());
        System.out.println(tableGridPane.getChildren().size() + "-" + tableGridPane.getChildren());
        table.getCards().clear();
        tableGridPane.getChildren().clear();
        //tableGridPane.getChildren().removeAll();

        for (int i = 0; i < TABLE_COLS * TABLE_ROWS; i++) {

            int targetRow = i / TABLE_COLS;
            int targetCol = targetRow == 0 ? i : i % TABLE_COLS;
            tableGridPane.add(new Pane(), targetCol, targetRow);
            tableSupport.set(i, false);
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


    /**
     * Returns a Pair containing ( colIndex, rowIndex )
     * @param card
     * @param gridPane
     * @return
     * @throws InterruptedException
     */
    private Pair<Integer, Integer> findRowColumnOfCardInGridPane(Card card, GridPane gridPane) throws InterruptedException {
//        for (Node node : gridPane.getChildren()) {
//            try {
//                if (node instanceof Pane) {
//                    if (getCardfromURL(((ImageView) ((Pane)node).getChildren()).getImage().getUrl()).getImage().equals(card.getImage())) {
//                        System.out.println("è un pane");
//                        return new Pair<>(GridPane.getColumnIndex(node), GridPane.getRowIndex(node));
//                    }
//                } else if (node instanceof ImageView) {
//                    if (getCardfromURL(((ImageView) node).getImage().getUrl()).getImage().equals(card.getImage())) {
//                        System.out.println("è un IV");
//                        return new Pair<>(GridPane.getColumnIndex(node), GridPane.getRowIndex(node));
//                    }
//                }
        for (Node node : gridPane.getChildren()) {
            try {
                // Controllo se il nodo è un Pane
                if (node instanceof Pane) {
                    Pane pane = (Pane) node;
                    if (!pane.getChildren().isEmpty() && pane.getChildren().getFirst() instanceof ImageView) {
                        ImageView imageView = (ImageView) pane.getChildren().getFirst();
                        if (getCardfromURL(imageView.getImage().getUrl()).getImage().equals(card.getImage())) {
                            System.out.println("è un pane");
                            return new Pair<>(GridPane.getColumnIndex(node), GridPane.getRowIndex(node));
                        }
                    }
                }
                // Controllo se il nodo è un ImageView
                else if (node instanceof ImageView) {
                    ImageView imageView = (ImageView) node;
                    if (getCardfromURL(imageView.getImage().getUrl()).getImage().equals(card.getImage())) {
                        System.out.println("è un IV");
                        return new Pair<>(GridPane.getColumnIndex(node), GridPane.getRowIndex(node));
                    }
                }
            } catch (ClassCastException e) {
                throw new InterruptedException("Node not found");
            }
        }
        System.out.println("non ho trovato un cazzo");
        return null;
    }


    public void configureTableGridPane(GridPane table) {
        table.setGridLinesVisible(true);
        table.setAlignment(Pos.CENTER);

        for (int i = 0; i < TABLE_COLS; i++) {
            for (int j = 0; j < TABLE_ROWS; j++) {
                table.add(new Pane(), i, j);
                tableSupport.add(false);
            }
        }
    }

    /**
     * Configure the teams and the players in the table
     *
     * @param table
     */
    public void configureTable(Table table) {
        //table = new Table(new ArrayList<Player>(2), new ArrayList<Player>());
        table.setTeams(new ArrayList<>(2));
        table.getTeams().add(new Player(playersTurn.get(0)));
        table.getTeams().add(new Player(playersTurn.get(1)));

        for (String Id : playersTurn) {
            table.getPlayers().add(new Player(Id));
        }
    }

    /**
     * Set the color of the label
     */
    public void setNamesLabelColor() {
        if (twoPlayers) {
            if (SharedData.getInstance().getLobbyPlayers().indexOf(playersSorted.getFirst()) % 2 == 0) {
                System.out.println(playersSorted.getFirst() + " dentro l'if--viola");
                namePlayer.setStyle("-fx-text-fill: #521a6a");
                namePlayer2.setStyle("-fx-text-fill: #6a1a32");
            } else {
                System.out.println(playersSorted.getFirst() + " dentro l'else--magenta");
                namePlayer.setStyle("-fx-text-fill: #6a1a32");
                namePlayer2.setStyle("-fx-text-fill: #521a6a");
            }
        } else {
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

            if (twoPlayers) {
                handsGridPanes.add(hand);            //mano giocatore principale
                handsGridPanes.add(handPlayer2);     //mano giocatore difronte
                namePlayer.setText(playersSorted.getFirst());
                namePlayer2.setText(playersSorted.getLast());
                namePlayer1.setVisible(false);
                namePlayer3.setVisible(false);

            } else {
                //4 giocatori
                handsGridPanes.add(hand);            //mano giocatore principale
                handsGridPanes.add(handPlayer1);     //mano giocatore a sx
                handsGridPanes.add(handPlayer2);     //mano giocatore difronte
                handsGridPanes.add(handPlayer3);     //mano giocatore a dx
                namePlayer.setText(playersSorted.getFirst());
                namePlayer1.setText((playersSorted.get(1)));
                namePlayer2.setText((playersSorted.get(2)));
                namePlayer3.setText(playersSorted.getLast());
            }

            setNamesLabelColor();

            for (GridPane g : handsGridPanes) {
                g.setGridLinesVisible(true);
                g.setAlignment(Pos.CENTER);
                //setupGridPaneCols(g);
            }

            int tmp = 0;
            for (String clientName : playersSorted) {
                handsWithGridPane.put(clientName, handsGridPanes.get(tmp));
                handsWithCardsImView.put(clientName, new ArrayList<>());

                if (tmp == 0) {
                    generatePlayersCards(hand, new ArrayList<>(SharedData.getInstance().getPlayerCards()), false, 0);
                } else {
                    generatePlayersCards(handsGridPanes.get(tmp), new ArrayList<>(SharedData.getInstance().getPlayerCards()), true, tmp);
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

    Number calculateY(int position, double startY, double endY) {
        return switch (position) {
            case 0 -> endY - startY;
            case 1 -> (endY - startY);
            case 2 -> -(endY - startY);
            default -> {

                System.out.println("def");
                yield 5;
            }
        };
    }

    Number calculateX(int position, double startX, double endX) {
        return switch (position) {
            case 0 -> endX - startX;
            case 1 -> (endX - startX);
            case 2 -> -(endX - startX);
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
        int numberOfCards = currGame.equals("Tressette") ? 10 : 3;

        for (int i = 0; i < numberOfCards; i++) {
            ColumnConstraints cols = new ColumnConstraints();
            cols.setPercentWidth(10);
            gridPane.getColumnConstraints().add(cols);
        }
        gridPane.setHgap(0);
        gridPane.setVgap(0);
    }

}

