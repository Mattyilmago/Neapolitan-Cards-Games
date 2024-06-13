package it.MM.LeTreCarte;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.MM.LeTreCarte.model.card.Card;
import it.MM.LeTreCarte.model.card.cardcontainer.Deck;
import jakarta.websocket.EncodeException;
import jakarta.websocket.MessageHandler;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.*;


public class TableController implements Initializable {
                    // { nomeGiocatore1: [imgvw1, imgvw2], nomeGiocatore2: [...] }
    private final HashMap<String, ArrayList<ImageView>> handsWithCardsImView = new HashMap<>();
    private final HashMap<String, GridPane> handsWithGridPane = new HashMap<>();


    @FXML
    private AnchorPane root;

    @FXML
    private AnchorPane Card;
    @FXML
    private GridPane table;
    @FXML
    private Button createButton;
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
    void createLobby(ActionEvent event) {

    }

    //fixed dimensioni della grid Table
    final int tableRows = 3;
    final int tableCols = 5;

    //contiene false se non è presente nessuna carta, true viceversa
    ArrayList<Boolean> tableSupport = new ArrayList<>();

    Number calculateY(double startY,double endY){
        if(startY<endY){
            return -Math.abs(endY-startY);
        }else{
            return endY-startY;
        }
    }

    Number calculateX(double startX,double endX){
        if(startX>endX){
            return endX-startX;
        }else{
            return endX-startX;
        }
    }

    private void startBackgroundListener(){
        SharedData.getInstance().getMoves().addListener(
                new ListChangeListener<JsonObject>() {
                    @Override
                    public void onChanged(Change<? extends JsonObject> c) {
                        Platform.runLater(()->{
                            JsonObject response = SharedData.getInstance().getMoves().getLast();

                            if(Objects.equals(response.get("type").getAsString(), "move") && !Objects.equals(response.get("clientAKA").getAsString(), SharedData.getInstance().getPlayerName())){
                                String clientAKA = response.get("clientAKA").getAsString();
                                int cardIndexInHand = response.get("cardIndexInHand").getAsInt()-1;

                                System.out.println(clientAKA);
                                System.out.println(handsWithGridPane);
                                System.out.println(handsWithCardsImView);


                                //#
                                ImageView cardToMove = handsWithCardsImView.get(clientAKA).get(cardIndexInHand);
                                handsWithCardsImView.get(clientAKA).remove(cardIndexInHand);



                                int targetCol = response.get("targetCol").getAsInt();
                                int targetRow = response.get("targetRow").getAsInt();
                                GridPane gridviewAKA = handsWithGridPane.get(clientAKA);
                                int cardVal = response.get("card-val").getAsInt();
                                Character cardSeed = response.get("card-seed").getAsString().toCharArray()[0];

                                Translate translate2 = new Translate();
                                cardToMove.getTransforms().add(translate2);
                                //coordinate globali
                                double startX = cardToMove.localToScene(0,0).getX();
                                double startY = cardToMove.localToScene(0,0).getY();
                                double endX = table.localToScene(table.getBoundsInLocal()).getMinX() + targetCol * table.getPrefWidth()/tableCols;
                                double endY = table.localToScene(table.getBoundsInLocal()).getMinY() + targetRow * table.getPrefHeight()/tableRows;

                                //creo una copia di iv
                                ImageView cardInTable = new ImageView(new Image(getClass().getResource(new Card(cardVal, cardSeed).getImage()).toExternalForm()));
                                cardInTable.setFitWidth(hand.getPrefWidth()/11.5);
                                cardInTable.setFitHeight(hand.getPrefHeight()*0.9);
                                cardInTable.setVisible(false);

                                table.getChildren().remove(targetRow+targetCol);
                                table.add(cardInTable, targetCol,targetRow);
                                tableSupport.set(targetRow*targetCol+targetCol, true);


                                //create animation
                                Timeline cardToTableAnimationENEMY = new Timeline(
                                        new KeyFrame(Duration.ZERO,
                                                new KeyValue(translate2.xProperty(), 0),
                                                new KeyValue(translate2.yProperty(), 0)
                                        ),

                                        new KeyFrame(new Duration(300), e ->{
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



                                        },
                                                new KeyValue(translate2.xProperty(),calculateX(startX, endX)),
                                                new KeyValue(translate2.yProperty(), calculateY(startY, endY))
                                        )



                                );

                                cardToTableAnimationENEMY.play();


                            }
                        });

                    }
                }
        );

    }





    void sortLobbyPlayers(){
        String me = SharedData.getInstance().getPlayerName();

        ObservableList<String> tmp = FXCollections.observableArrayList();
        tmp.add(me);

        int indexOfme = SharedData.getInstance().getLobbyPlayers().indexOf(me);
        for (int index = indexOfme+1; index < SharedData.getInstance().getLobbyPlayers().size(); index++) {
            tmp.add(SharedData.getInstance().getLobbyPlayers().get(index));
        }

        for(int j=0; j<indexOfme; j++){
            tmp.add(SharedData.getInstance().getLobbyPlayers().get(j));
        }

        SharedData.getInstance().getLobbyPlayers().clear();
        SharedData.getInstance().getLobbyPlayers().addAll(tmp);
        System.out.println(tmp);


    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        startBackgroundListener();
        sortLobbyPlayers();

        table.setGridLinesVisible(true);
        table.setAlignment(Pos.CENTER);

        for (int i = 0; i < tableCols; i++) {
            for (int j = 0; j < tableRows; j++) {
                table.add(new Pane(), i, j);
                tableSupport.add(false);
            }
        }


        try {
            ArrayList<Card> frontCards = new ArrayList<>(SharedData.getInstance().getPlayerCards());
            ArrayList<GridPane> hands = new ArrayList<>();

            if(SharedData.getInstance().getLobbyPlayers().size()==2){
                hands.add(hand);            //mano giocatore principale
                hands.add(handPlayer2);     //mano giocatore difronte
            }else{
                //4 giocatori
                hands.add(hand);            //mano giocatore principale
                hands.add(handPlayer1);     //mano giocatore a sx
                hands.add(handPlayer2);     //mano giocatore difronte
                hands.add(handPlayer3);     //mano giocatore a dx
            }



            for (GridPane g : hands) {
                g.setGridLinesVisible(true);
                g.setAlignment(Pos.CENTER);
                setupGridPaneCols(g);
            }

            int tmp = 0;
            for (String clientName : SharedData.getInstance().getLobbyPlayers()){
                handsWithGridPane.put(clientName, hands.get(tmp));
                handsWithCardsImView.put(clientName, new ArrayList<>());

                if(tmp==0){
                    generatePlayersCards(hand, frontCards, false, 0);
                }else{
                    generatePlayersCards(hands.get(tmp), new ArrayList<Card>(), true,tmp);
                }
                tmp++;
            }



//            //Creo la lista delle carte di fronte
//
//
//            //Creo la lista delle carte girate
//            for (int i = 0; i < 3; i++) {
//                ArrayList<Card> backCards = new ArrayList<>();
//                for (int j = 0; j < 10; j++) {
//                    //backCards.add(deck1.getFirst());
//                }
//
//            }

        } catch (RuntimeException e) {
            System.out.println(e);
        }




    }


    /**
     * The function fills the gridview with ImageViews containing the cards.
     * If the back is true then the
     * @param gridview
     * @param cards
     * @param back
     */
    public void generatePlayersCards(GridPane gridview, ArrayList<Card> cards, boolean back, int playerIndex) {

        System.out.println("players: "+SharedData.getInstance().getLobbyPlayers().toString() +" - "+ SharedData.getInstance().getLobbyPlayers().get(playerIndex).toString());
        for (int cardIndex = 0; cardIndex < 10; cardIndex++) {
            Image image = new Image(getClass().getResource(back ? "Cards_png/back.png" : cards.get(cardIndex).getImage()).toExternalForm());
            ImageView iv = new ImageView(image);
            iv.setPreserveRatio(true);

            System.out.println("porcoddio: "+SharedData.getInstance().getLobbyPlayers().get(playerIndex));
            handsWithCardsImView.get(SharedData.getInstance().getLobbyPlayers().get(playerIndex)).add(iv);


            GridPane.setHalignment(iv, HPos.CENTER);
            GridPane.setValignment(iv, VPos.CENTER);
            GridPane.setMargin(iv, new Insets(0));

            Pane pane = new Pane(iv);

            iv.setFitWidth(gridview.getPrefWidth()/11);
            iv.setFitHeight(gridview.getPrefHeight());
            gridview.addColumn(cardIndex, pane);
            gridview.setAlignment(Pos.CENTER);

            if(!back){
                pane.setOnMouseEntered(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        iv.setTranslateY(iv.getTranslateY() - 50);
                    }
                });

                pane.setOnMouseExited(new EventHandler<MouseEvent>() {
                    @Override public void handle (MouseEvent mouseEvent){
                        iv.setTranslateY(iv.getTranslateY() + 50);
                    }
                });


                int finalCardIndex = cardIndex;
                pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    boolean waitUntilAnimation = false;
                    @Override
                    public void handle(MouseEvent mouseEvent) {

                        if(!waitUntilAnimation){
                            waitUntilAnimation = true;
                            //trova cardIndex aggiornato, per risolvere il problema delle carte in mano che diminuiscono
                            int currentCardIndex= (int) gridview.getChildren().stream().takeWhile(node -> !node.equals(pane)).count();
                            System.out.println("currentCardIndex: "+currentCardIndex);

                            //recupera la posizione globale della carta
                            Translate translate = new Translate();
                            iv.getTransforms().add(translate);


                            //target coordinates
                            int index = findIndexOfFirstEmptyCell();
                            int targetRow = index/tableCols;
                            int targetCol = targetRow == 0 ? index : index%tableCols;

                            //Sending move to server
                            try {
                                SharedData.getGSCInstance().sendMove("cardToTable", cards.get(finalCardIndex), currentCardIndex, targetRow, targetCol, gridview.getColumnCount());
                            } catch (EncodeException | IOException e) {
                                System.out.println("Connection error: Could not send move to table.");
                                throw new RuntimeException(e);
                            }

                            //creo una copia di iv
                            ImageView tmpIV = new ImageView(iv.getImage());
                            tmpIV.setFitWidth(gridview.getPrefWidth()/11.5);
                            tmpIV.setFitHeight(gridview.getPrefHeight()*0.9);
                            tmpIV.setVisible(false);



                            System.out.println("Row: "+targetRow+"| col: "+targetCol);

                            //t
                            table.getChildren().remove(targetRow+targetCol);
                            table.add(tmpIV, targetCol,targetRow);
                            tableSupport.set(index, true);



                            //coordinate globali
                            double startX = iv.localToScene(0,0).getX();
                            double startY = iv.localToScene(0,0).getY();
                            double endX = table.localToScene(table.getBoundsInLocal()).getMinX() + targetCol * table.getPrefWidth()/tableCols;
                            double endY = table.localToScene(table.getBoundsInLocal()).getMinY() + targetRow * table.getPrefHeight()/tableRows;

                            System.out.println("endX: "+endX+"| endY: "+endY);

                            //create animation
                            Timeline cardToTableAnimation = new Timeline(
                                    new KeyFrame(Duration.ZERO,
                                            new KeyValue(translate.xProperty(), 0),
                                            new KeyValue(translate.yProperty(), 0)
                                    ),

                                    new KeyFrame(new Duration(300), e ->{
                                        gridview.getChildren().remove(pane);
                                        tmpIV.setVisible(true);


                                        // Sposta i nodi rimanenti (si potrebbe fare animato ehh)
                                        for (Node node : gridview.getChildren()) {
                                            Integer currCol = GridPane.getColumnIndex(node);
                                            if (currCol != null && currCol > (currentCardIndex-1)) {
                                                GridPane.setColumnIndex(node, currCol - 1);
                                            }
                                        }
                                        gridview.getColumnConstraints().removeLast();
                                        waitUntilAnimation=false;



                                    },
                                            new KeyValue(translate.xProperty(), calculateX(startX,endX)),
                                            new KeyValue(translate.yProperty(), calculateY(startY,endY))
                                    )
                            );

                            cardToTableAnimation.play();

                        }



                    }
                });
            }
        }




    }

    private int findIndexOfFirstEmptyCell() {
        int index=0;
        for(Boolean cellaOccupata : tableSupport){
            if(!cellaOccupata){
                System.out.println("index: " + index);
                return index;
            }
            index++;
        }
        return -1;
    }

    private void setupGridPaneCols(GridPane gridPane) {
        // Rimuovi eventuali RowConstraints esistenti
        gridPane.getColumnConstraints().clear();

        for (int i = 0; i < 10; i++) {
            ColumnConstraints cols = new ColumnConstraints();
            cols.setPercentWidth(10);
            gridPane.getColumnConstraints().add(cols);
        }
        gridPane.setHgap(0);
        gridPane.setVgap(0);
    }



}

