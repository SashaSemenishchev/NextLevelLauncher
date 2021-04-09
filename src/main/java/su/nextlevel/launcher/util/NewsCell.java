package su.nextlevel.launcher.util;

import javafx.scene.control.ListCell;
import org.json.simple.JSONObject;

public class NewsCell extends ListCell<JSONObject> {

    @Override
    protected void updateItem(JSONObject item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
            setGraphic(new NewsData(item.get("title").toString(), item.get("content").toString()).mainPane);
        }
    }
}
