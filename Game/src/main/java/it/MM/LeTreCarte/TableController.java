package it.MM.LeTreCarte;

import it.MM.LeTreCarte.model.card.Card;
import it.MM.LeTreCarte.model.card.cardcontainer.Deck;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class TableController implements Initializable {

    final Deck deck1 = new Deck();

    private ArrayList<Group> imageGroups = new ArrayList<>();

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        deck1.shuffle();
        table.setGridLinesVisible(true);
        System.out.println(table.getChildren().isEmpty());
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 3; j++) {
                table.add(new Pane(), i, j);
            }
        }


        try {
            ArrayList<Card> tmp = new ArrayList<>();
            ArrayList<GridPane> hands = new ArrayList<>();
            hands.add(hand);
            hands.add(handPlayer1);
            hands.add(handPlayer2);
            hands.add(handPlayer3);
            for (GridPane g : hands) {
                g.setGridLinesVisible(true);
                if (hands.indexOf(g) % 2 == 0) {
                    setupGridPaneCols(g);
                } else {
                    setupGridPaneRows(g);
                }
            }

            for (int j = 0; j < 10; j++) {
                tmp.add(deck1.getFirst());
            }

            generatePlayersCards(hand, hands, tmp, false);

            for (int i = 0; i < 3; i++) {
                ArrayList<Card> tmp2 = new ArrayList<>();
                for (int j = 0; j < 10; j++) {
                    tmp2.add(deck1.getFirst());
                }
                generatePlayersCards(hands.get(i + 1), hands, tmp2, true);
            }

        } catch (RuntimeException e) {
            System.out.println(e);
        }


//        //mano giocatore hand
//        for(int i=0; i<10; i++) {
//            ImageView im = new ImageView(
//                    new Image(getClass().getResource(deck1.getCards().removeLast().getImage()).toExternalForm())
//            );
//
//            imageGroups.add(new Group(im));
//
//
//
//            int finalI = i;
//            im.setOnMouseClicked(new EventHandler<MouseEvent>() {
//                TranslateTransition transition = new TranslateTransition(Duration.millis(300), im);
//
//                @Override
//                public void handle(MouseEvent mouseEvent) {
//                        transition.setByX(0);
//                        transition.setByY(-200);
//                        transition.play();
//
//
////                    System.out.println(finalI);
////                    transition.setFromX(imageGroups.get(finalI).getTranslateX());
////                    transition.setFromY(imageGroups.get(finalI).getTranslateY());
////
////                    transition.setToX(imageGroups.get(finalI).getTranslateX());
////                    transition.setToX(imageGroups.get(finalI).getTranslateY()-300);
////                    transition.play();
////                    System.out.println("played");
//                }
//            });
//
//            im.setFitHeight(135);
//            im.setFitWidth(1000/11);
//            hand.addColumn(i, im);
//        }
//
//        //mano altri giocatori
//


    }
//
//
//    //Bisogna aggiungere l'inizializzazione del deck e aggiungere una carta ad una listview
//
//    @Override
//    public void initialize(URL url, ResourceBundle resourceBundle) {
//        //cardCreate.setImage(new Image(getClass().getResource(new Card(2,'D').getImage()).toExternalForm()));
//        //ImageIO.read(getClass().getResource("/com/example/Game/Cards_jpg/" + value + "-" + seed + ".jpg"));

    //    }
    public void generatePlayersCards(GridPane gridview, ArrayList<GridPane> hands, ArrayList<Card> cards, boolean back) {

        for (int i = 0; i < 10; i++) {
            ImageView im = new ImageView(new Image(getClass().getResource(back ? "Cards_jpg/back.jpg" : cards.get(i).getImage()).toExternalForm()));

            StackPane stackPane = new StackPane();
            stackPane.getChildren().add(im);
            stackPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            imageGroups.add(new Group(im));


            int finalI = i;
            im.setOnMouseClicked(new EventHandler<MouseEvent>() {
                TranslateTransition transition = new TranslateTransition(Duration.millis(300), im);

                @Override
                public void handle(MouseEvent mouseEvent) {
                    transition.setByX(0);
                    transition.setByY(-200);
                    transition.play();


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

            if (hands.indexOf(gridview) % 2 == 0) {
                im.setFitWidth(gridview.getPrefWidth() / 11);
                im.setFitHeight(2188 * im.getFitWidth() / 1324);
                gridview.addColumn(i, im);
            } else {
                im.setRotate(90);
                im.setFitWidth(gridview.getPrefHeight() / 11);
                im.setFitHeight(2188 * im.getFitWidth() / 1324);
                gridview.addRow(i, im);
            }
            GridPane.setHalignment(im, HPos.CENTER);
            GridPane.setValignment(im, VPos.CENTER);
        }


    }


    //TODO aggiustare la funzione
    public static double[] getCellCoordinates (GridPane gridPane,int rowIndex, int colIndex){
        double[] coordinates = new double[2];

        // Trova o crea un nodo temporaneo nella cella specificata
        Node cellNode = getNodeFromGridPane(gridPane, rowIndex, colIndex);
        boolean isTempNode = false;

        if (cellNode == null) {
            // Creare un nodo temporaneo
            cellNode = new Region();
            gridPane.add(cellNode, colIndex, rowIndex);
            isTempNode = true;
        }

        final Node finalCellNode = cellNode;
        Platform.runLater(() -> {
            // Converti le coordinate del nodo alla scena
            Bounds cellBounds = finalCellNode.localToScene(finalCellNode.getBoundsInLocal());
            coordinates[0] = cellBounds.getMinX();
            coordinates[1] = cellBounds.getMinY();


            System.out.println("Cella [" + rowIndex + ", " + colIndex + "]: X = " + coordinates[0] + ", Y = " + coordinates[1]);
        });

        // Rimuovi il nodo temporaneo se è stato aggiunto in questo metodo
        if (isTempNode) {
            gridPane.getChildren().remove(finalCellNode);
        }
        return coordinates;
    }

    /**
     * Ottiene il nodo nella cella specificata del GridPane.
     *
     * @param gridPane Il GridPane
     * @param rowIndex L'indice della riga
     * @param colIndex L'indice della colonna
     * @return Il nodo nella cella, o null se non c'è nessun nodo
     */
    public static Node getNodeFromGridPane(GridPane gridPane, int rowIndex, int colIndex) {
        for (Node node : gridPane.getChildren()) {
            Integer row = GridPane.getRowIndex(node);
            Integer col = GridPane.getColumnIndex(node);
            if (row != null && col != null && row == rowIndex && col == colIndex) {
                return node;
            }
        }
        return null;
    }

    private void setupGridPaneRows(GridPane gridPane) {
        // Rimuovi eventuali RowConstraints esistenti
        gridPane.getRowConstraints().clear();

        // Aggiungi 10 RowConstraints con altezza percentuale del 10%
        for (int i = 0; i < 10; i++) {
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(10); // Imposta l'altezza percentuale al 10%
            gridPane.getRowConstraints().add(row);
        }
        gridPane.setHgap(0);
        gridPane.setVgap(0);
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
