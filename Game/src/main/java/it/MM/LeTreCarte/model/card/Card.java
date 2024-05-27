package it.MM.LeTreCarte.model.card;

import it.MM.LeTreCarte.App;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

//import javafx.embed.swing.SwingFXUtils;

/**
 * Class card that accept only cards with value between 1 and 10 and seeds in [BCDS]
 * usage: B -> bastoni, C -> coppe, D -> denari, S -> spade
 */
public class Card {
    int value;
    Character seed; //seme della carta, uso: B -> bastoni, C -> coppe, D -> denari, S -> spade
    String image;
    public static final Character[] seeds = {'B', 'C', 'D', 'S'};

    public Card(int value, Character seed){
        setValue(value);
        this.value = value;
        this.seed = checkSeed(seed);
        setImage();
        //this.image = loadImage("/com/example/Game/Cards_png/" + value + "-" + seed + ".png");
//        try {
//            BufferedImage bi = ImageIO.read(getClass().getResource("/com/example/Game/Cards_png/" + value + "-" + seed + ".png"));
//            this.image=convertToFxImage(bi);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }
    //private Image convertToFxImage(BufferedImage bufferedImage) {
//        return SwingFXUtils.toFXImage(bufferedImage, null);
//    }
//    private Image loadImage(String path) {
//        URL imageUrl = getClass().getResource(path);
//        if (imageUrl == null) {
//            imageUrl = Thread.currentThread().getContextClassLoader().getResource(path.substring(1));
//        }
//        if (imageUrl == null) {
//            System.err.println("Immagine non trovata: " + path);
//            return null;
//        }
//        return new Image(imageUrl.toExternalForm());
//    }
//

    public String getImage() {
        return image;
    }

    public void setImage() {
        //this.image = new Image(getClass().getResource("/Cards_png/" +this.value+"-"+this.seed+".png").toString());
        this.image = "Cards_png/" + getValue() + "-" + getSeed() + ".png";
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        if (value < 1 || value > 10)
            throw new IllegalArgumentException("ERRORE il valore della carta non è compreso tra 1 e 10");

        this.value = value;
    }

    public char getSeed() {
        return seed;
    }

    public void setSeed(Character seed) {
        this.seed = checkSeed(seed);
    }

    public Character checkSeed(Character seed){
        if (!seed.toString().toUpperCase().matches("[BCDS]"))
            throw new IllegalArgumentException("ERRORE il seme non è corretto.");

        return Character.toUpperCase(seed);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return value == card.value && Objects.equals(seed, card.seed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, seed);
    }

    @Override
    public String toString() {
        return "Card{" +
                "value=" + value +
                ", seed=" + seed +
                '}';
    }

//    public static void main(String[] args) {
//        Card c = new Card(1,'B');
//        Image n = c.getImage();
//        System.out.println(n);
//        System.out.println(c);
//    }
}
