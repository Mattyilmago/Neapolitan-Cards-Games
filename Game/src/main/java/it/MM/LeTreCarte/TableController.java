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
import javafx.collections.ListChangeListener;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.ResourceBundle;


public class TableController implements Initializable {
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

    private void startBackgroundListener(){
        SharedData.getInstance().getMoves().addListener(
                new ListChangeListener<JsonObject>() {
                    @Override
                    public void onChanged(Change<? extends JsonObject> c) {
                        Platform.runLater(()->{
                            JsonObject response = SharedData.getInstance().getMoves().getLast();

                            if(!Objects.equals(response.get("clientID").getAsString(), SharedData.getInstance().getPlayerName()) && Objects.equals(response.get("type").getAsString(), "move")){
                                String clientID = response.get("clientID").getAsString();
                                int cardIndexInHand = response.get("cardIndexInHand").getAsInt();

                                System.out.println(clientID);
                                System.out.println(handsWithGridPane);

                                ImageView cardToMove = handsWithCardsImView.get(clientID).get(cardIndexInHand);
                                int targetCol = response.get("targetCol").getAsInt();
                                int targetRow = response.get("targetRow").getAsInt();
                                GridPane gridview = handsWithGridPane.get(clientID);
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
                                cardInTable.setFitWidth(gridview.getPrefWidth()/11);
                                cardInTable.setFitHeight(gridview.getPrefHeight());
                                cardInTable.setVisible(false);
                                //table.getChildren().remove(targetRow+targetCol);
                                table.add(cardInTable, targetCol,targetRow);
                                tableSupport.set(targetRow*targetCol+targetCol, true);


                                //create animation
                                Timeline cardToTableAnimation = new Timeline(
                                        new KeyFrame(Duration.ZERO,
                                                new KeyValue(translate2.xProperty(), 0),
                                                new KeyValue(translate2.yProperty(), 0)
                                        ),

                                        new KeyFrame(new Duration(150),
                                                new KeyValue(translate2.xProperty(), 0),
                                                new KeyValue(translate2.yProperty(), 0)
                                        ),

                                        new KeyFrame(new Duration(300), e ->{
                                            cardInTable.setVisible(true);


                                            //Sposta i nodi rimanenti (si potrebbe fare animato ehh)
                                            for (Node node : gridview.getChildren()) {
                                                Integer currCol = GridPane.getColumnIndex(node);
                                                if (currCol != null && currCol > (response.get("cardIndexInHand").getAsInt()-1)) {
                                                    GridPane.setColumnIndex(node, currCol - 1);
                                                }
                                            }
                                            gridview.getColumnConstraints().removeLast();



                                        },
                                                new KeyValue(translate2.xProperty(), endX-startX),
                                                new KeyValue(translate2.yProperty(), endY-startY)
                                        )
                                );

                                cardToTableAnimation.play();


                            }
                        });

                    }
                }
        );

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        startBackgroundListener();

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
            hands.add(hand);
            hands.add(handPlayer1);
            hands.add(handPlayer2);
            hands.add(handPlayer3);

            int tmp = 0;
            for (String clientName : SharedData.getInstance().getLobbyPlayers()){
                handsWithGridPane.put(clientName, hands.get(tmp));
                handsWithCardsImView.put(clientName, new ArrayList<>());
                tmp++;
            }

            for (GridPane g : hands) {
                g.setGridLinesVisible(true);
                g.setAlignment(Pos.CENTER);
                setupGridPaneCols(g);
            }

            //Creo la lista delle carte di fronte
            generatePlayersCards(hand, frontCards, false, 0);

            //Creo la lista delle carte girate
            for (int i = 0; i < 3; i++) {
                ArrayList<Card> backCards = new ArrayList<>();
                for (int j = 0; j < 10; j++) {
                    //backCards.add(deck1.getFirst());
                }
                generatePlayersCards(hands.get(i + 1), backCards, true,i+1);
            }

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


        for (int cardIndex = 0; cardIndex < 10; cardIndex++) {
            Image image = new Image(getClass().getResource(back ? "Cards_png/back.png" : cards.get(cardIndex).getImage()).toExternalForm());
            ImageView iv = new ImageView(image);
            iv.setPreserveRatio(true);

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
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        //trova cardIndex aggiornato, per risolvere il problema delle carte in mano che diminuiscono
                        int currentCardIndex= (int) gridview.getChildren().stream().takeWhile(node -> !node.equals(pane)).count();
                        System.out.println("currentCardIndex: "+currentCardIndex);

                        //recupera la posizione globale della carta
                        Translate translate = new Translate();
                        iv.getTransforms().add(translate);


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



                                },
                                        new KeyValue(translate.xProperty(), endX-startX),
                                        new KeyValue(translate.yProperty(), endY-startY)
                                )
                        );

                        cardToTableAnimation.play();

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

