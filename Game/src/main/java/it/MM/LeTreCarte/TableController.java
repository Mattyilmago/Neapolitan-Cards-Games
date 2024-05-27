package it.MM.LeTreCarte;

import it.MM.LeTreCarte.model.card.Card;
import it.MM.LeTreCarte.model.card.cardcontainer.Deck;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
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

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class TableController implements Initializable {

    final Deck deck1 = new Deck();

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



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        deck1.shuffle();
        table.setGridLinesVisible(true);
        table.setAlignment(Pos.CENTER);

        for (int i = 0; i < tableCols; i++) {
            for (int j = 0; j < tableRows; j++) {
                table.add(new Pane(), i, j);
                tableSupport.add(false);
            }
        }


        try {
            ArrayList<Card> frontCards = new ArrayList<>();
            ArrayList<GridPane> hands = new ArrayList<>();
            hands.add(hand);
            hands.add(handPlayer1);
            hands.add(handPlayer2);
            hands.add(handPlayer3);

            for (GridPane g : hands) {
                g.setGridLinesVisible(true);
                g.setAlignment(Pos.CENTER);
                setupGridPaneCols(g);
            }

            //Creo la lista delle carte di fronte
            for (int j = 0; j < 10; j++) {
                frontCards.add(deck1.getFirst());
            }

            generatePlayersCards(hand, hands, frontCards, false);

            //Creo la lista delle carte girate
            for (int i = 0; i < 3; i++) {
                ArrayList<Card> backCards = new ArrayList<>();
                for (int j = 0; j < 10; j++) {
                    backCards.add(deck1.getFirst());
                }
                generatePlayersCards(hands.get(i + 1), hands, backCards, true);
            }

        } catch (RuntimeException e) {
            System.out.println(e);
        }
    }


    /**
     * The function fills the gridview with ImageViews containing the cards.
     * If the back is true then the
     * @param gridview
     * @param hands
     * @param cards
     * @param back
     */
    public void generatePlayersCards(GridPane gridview, ArrayList<GridPane> hands, ArrayList<Card> cards, boolean back) {


        for (int cardIndex = 0; cardIndex < 10; cardIndex++) {
            Image image = new Image(getClass().getResource(back ? "Cards_png/back.png" : cards.get(cardIndex).getImage()).toExternalForm());
            ImageView iv = new ImageView(image);
            iv.setPreserveRatio(true);

            GridPane.setHalignment(iv, HPos.CENTER);
            GridPane.setValignment(iv, VPos.CENTER);
            GridPane.setMargin(iv, new Insets(0));

            Pane pane = new Pane(iv);

            iv.setFitWidth(gridview.getPrefWidth()/11);
            iv.setFitHeight(gridview.getPrefHeight());
            gridview.addColumn(cardIndex, pane);

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

                pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        //trova cardIndex aggiornato, per risolvere il problema delle carte in mano che diminuiscono
                        int currentCardIndex=0;
                        for(Node node : gridview.getChildren()){
                            if(node.equals(pane)){
                                break;
                            }
                            currentCardIndex++;
                        }
                        System.out.println("currentCardIndex: "+currentCardIndex);

                        //recupera la posizione globale della carta
                        Translate translate = new Translate();
                        iv.getTransforms().add(translate);

                        //target I, J in table (I x J)

                        //assert (index==-1); throw new RuntimeException("La table non contiene celle vuote");
                        //Node clonedNode = null;
//                    int tmpNodeIndex = 0;
//                    int nodeIndex = -1;

//                    for(Node tmpNode : gridview.getChildren()) {
//                        if(tmpNode.equals(iv)){
//                            System.out.println("---------------------- node found");
//                            node = tmpNode;
//                            //clonedNode = tmpNode;
//                            node.getTransforms().addCard(translate);
//                            //nodeIndex=tmpNodeIndex;
//                        }
//                        //tmpNodeIndex++;
//                    }

                        //trovo l'indice della prima cella vuota, ricavo rows & cols
                        int index = findIndexOfFirstEmptyCell();
                        int targetRow = index/tableCols;
                        int targetCol = targetRow == 0 ? index : index%tableCols;

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
//                    double endX = tableCellNode.localToScene(tableCellNode.getBoundsInLocal()).getMinX();
//                    double endY = tableCellNode.localToScene(tableCellNode.getBoundsInLocal()).getMinY();

                        double endX = table.localToScene(table.getBoundsInLocal()).getMinX() + targetCol * table.getPrefWidth()/tableCols;
                        double endY = table.localToScene(table.getBoundsInLocal()).getMinY() + targetRow * table.getPrefHeight()/tableRows;

                        System.out.println("endX: "+endX+"| endY: "+endY);

                        //crea l'animazione
//                    int finalNodeIndex = nodeIndex;
//                    Node finalClonedNode = clonedNode;

                        Timeline timeline = new Timeline(
                                new KeyFrame(Duration.ZERO,
                                        new KeyValue(translate.xProperty(), 0),
                                        new KeyValue(translate.yProperty(), 0)
                                ),
                                new KeyFrame(new Duration(300), e ->{
                                    gridview.getChildren().remove(pane);
                                    tmpIV.setVisible(true);
                                },
                                        new KeyValue(translate.xProperty(), endX-startX),
                                        new KeyValue(translate.yProperty(), endY-startY)
                                )
                        );
                        timeline.play();


                        gridview.getColumnConstraints().remove(currentCardIndex-1);
                        // Sposta i nodi rimanenti
                        for (Node node : gridview.getChildren()) {
                            Integer currCol = GridPane.getColumnIndex(node);
                            if (currCol != null && currCol > (currentCardIndex-1)) {
                                GridPane.setColumnIndex(node, currCol - 1);
                            }
                        }

//                    transition.setByX(0);
//                    transition.setByY(-200);
//                    transition.play();


//                    System.out.println(finalI);
//                    transition.setFromX(imageGroups.get(finalI).getTranslateX());
//                    transition.setFromY(imageGroups.get(finalI).getTranslateY());
//
//                    transition.setToX(getCellCoordinates(table, 2, 2)[0]);
//                    transition.setToY(getCellCoordinates(table, 2, 2)[1]);
//                    transition.play();
//                    System.out.println("played");
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


//    //TODO aggiustare la funzione
////    public static double[] getCellCoordinates (GridPane gridPane,int rowIndex, int colIndex){
////        double[] coordinates = new double[2];
////
////        // Trova o crea un nodo temporaneo nella cella specificata
////        Node cellNode = getNodeFromGridPane(gridPane, rowIndex, colIndex);
////        boolean isTempNode = false;
////
////        if (cellNode == null) {
////            // Creare un nodo temporaneo
////            cellNode = new Region();
////            gridPane.addCard(cellNode, colIndex, rowIndex);
////            isTempNode = true;
////        }
////
////        final Node finalCellNode = cellNode;
////        Platform.runLater(() -> {
////            // Converti le coordinate del nodo alla scena
////            Bounds cellBounds = finalCellNode.localToScene(finalCellNode.getBoundsInLocal());
////            coordinates[0] = cellBounds.getMinX();
////            coordinates[1] = cellBounds.getMinY();
////
////
////            System.out.println("Cella [" + rowIndex + ", " + colIndex + "]: X = " + coordinates[0] + ", Y = " + coordinates[1]);
////        });
////
////        // Rimuovi il nodo temporaneo se è stato aggiunto in questo metodo
////        if (isTempNode) {
////            gridPane.getChildren().removeCard(finalCellNode);
////        }
////        return coordinates;
////    }
//
//    /**
//     * Ottiene il nodo nella cella specificata del GridPane.
//     *
//     * //@param gridPane Il GridPane
//     * @param rowIndex L'indice della riga
//     * @param colIndex L'indice della colonna
//     * @return Il nodo nella cella, o null se non c'è nessun nodo
//     */
    public static Node getNodeFromGridPane(GridPane gp ,int rowIndex, int colIndex) {
        for (Node node : gp.getChildren()) {
            Integer row = gp.getRowIndex(node);
            Integer col = gp.getColumnIndex(node);
            if (row != null && col != null && row == rowIndex && col == colIndex) {
                return node;
            }
        }
        return null;
    }
//
//    private void setupGridPaneRows(GridPane gridPane) {
//        // Rimuovi eventuali RowConstraints esistenti
//        gridPane.getRowConstraints().clear();
//
//        // Aggiungi 10 RowConstraints con altezza percentuale del 10%
//        for (int i = 0; i < 10; i++) {
//            RowConstraints row = new RowConstraints();
//            row.setPercentHeight(8); // Imposta l'altezza percentuale al 10%
//            gridPane.getRowConstraints().addCard(row);
//        }
////        gridPane.setHgap(0);
////        gridPane.setVgap(0);
//    }

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

