
package it.MM.LeTreCarte;

import it.MM.LeTreCarte.model.card.Card;
import it.MM.LeTreCarte.model.card.cardcontainer.Deck;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class TableController implements Initializable {

        @FXML
        private AnchorPane Card;

        @FXML
        private AnchorPane table;

        @FXML
        private Button createButton;

        @FXML
        private StackPane deck;

        @FXML
        private GridPane hand;

        @FXML
        private AnchorPane handPlayer1;

        @FXML
        private AnchorPane handPlayer2;

        @FXML
        private AnchorPane handPlayer3;

        final Deck deck1 = new Deck();

        @FXML
        void createLobby(ActionEvent event) {

        }

        ArrayList<Group> imageGroups = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        for(int i=0; i<10; i++) {
            ImageView im = new ImageView(
                    new Image(getClass().getResource(deck1.getCards().removeLast().getImage()).toExternalForm())
            );

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
//                    transition.setToX(imageGroups.get(finalI).getTranslateX());
//                    transition.setToX(imageGroups.get(finalI).getTranslateY()-300);
//                    transition.play();
//                    System.out.println("played");
                }
            });

            im.setFitHeight(135);
            im.setFitWidth(1000/11);
            hand.addColumn(i, im);
        }




    }


//

//
//
//    //Bisogna aggiungere l'inizializzazione del deck e aggiungere una carta ad una listview
//
//    @Override
//    public void initialize(URL url, ResourceBundle resourceBundle) {
//        //cardCreate.setImage(new Image(getClass().getResource(new Card(2,'D').getImage()).toExternalForm()));
//        //ImageIO.read(getClass().getResource("/com/example/Game/Cards_jpg/" + value + "-" + seed + ".jpg"));
//    }



}
