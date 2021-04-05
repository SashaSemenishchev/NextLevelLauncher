package millida.utils;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class NewsData {
    String title1;
    String content1;
    @FXML public Label title;
    @FXML public Label content;
    @FXML public AnchorPane mainPane;
    public NewsData(String title1, String content1){
        setup(title1, content1);
    }
    @FXML
    public void initialize(){
        title.setText(title1);
        content.setText(content1);
    }
    public void setup(String title1, String content1){
        this.title1 = title1;
        this.content1 = content1;
    }
}
