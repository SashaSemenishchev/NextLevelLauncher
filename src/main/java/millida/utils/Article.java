package millida.utils;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import static millida.Controller.error;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class Article extends ListCell<String> { // TODO: news
    HBox hBox = new HBox();
    Hyperlink more;
    ImageView imageView;

    public Article(String name, String truncatedText){
        super();
        BufferedImage image = null;
        try {
            image = ImageIO.read(getClass().getClassLoader().getResource("Rectangle.png"));
            Font font = new Font("Monsterrat", Font.BOLD, 18);
            Graphics2D graphics2D = (Graphics2D) image.getGraphics();
            graphics2D.setFont(font);
            graphics2D.setColor(Color.BLACK);
            hBox.getChildren().addAll(new Label(name), new Label(truncatedText), new ImageView(SwingFXUtils.toFXImage(image, null)));
        } catch (IOException exception) {
            exception.printStackTrace();
            error(exception);
        }
    }
}
