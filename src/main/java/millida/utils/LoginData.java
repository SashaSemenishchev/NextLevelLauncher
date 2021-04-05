package millida.utils;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import millida.Main;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.Scanner;

public final class LoginData {
    public String login;
    public String username;
    public String skinSignature;
    public float money;
    public long daysLeft;
    public long playTime;
    public String token;
    public String uuid;

    public static final String authHost = "https://nextlevel.su/api/v1/user/";

    public LoginData(String token, String uuid , String login, String username, String skinSignature, float money, long daysLeft, long playTime) {
        this.token = token;
        this.login = login;
        this.money = money;
        this.daysLeft = daysLeft;
        this.playTime = playTime;
        this.skinSignature = skinSignature;
        this.username = username;
        this.uuid = uuid;
    }

    public static LoginData instance(String token){
        try {
            URL url = new URL(authHost);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.addRequestProperty("User-Agent", "PostmanRuntime/7.26.8");
            connection.addRequestProperty("Authorization", "Bearer " + token.replaceAll("\n", "").replaceAll("\r", ""));
            connection.addRequestProperty("Content-Type", "application/json");
            connection.addRequestProperty("Accept", "application/json");
            connection.connect();
            int responseCode = connection.getResponseCode();
            if(responseCode != 200){
                connection.disconnect();
                return null;
            }
            StringBuilder sb = new StringBuilder();
            Scanner scanner = new Scanner(connection.getInputStream());
            while(scanner.hasNext()) {
                String nextLine = scanner.nextLine();
                sb.append(nextLine);
            }
            connection.disconnect();
            scanner.close();
            JSONParser parser = new JSONParser();
            JSONObject root = (JSONObject) parser.parse(sb.toString());
            if(!Boolean.parseBoolean(root.get("is_active").toString())){
                return null;
            }
            return new LoginData(token, root.get("id").toString(), root.get("email").toString(),
                    root.get("username").toString(),
                    ((JSONObject)root.get("skin")).get("skin_head").toString(),
                    Float.parseFloat(root.get("balance").toString()),
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(root.get("server_access").toString().replace("T", " ").replace("Z", "")).getTime(),
                    (long) Float.parseFloat(root.get("play_time").toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String toString() {
        return "LoginData{" +
                "login='" + login + '\'' +
                ", username='" + username + '\'' +
                ", skinSignature='" + skinSignature + '\'' +
                ", money=" + money +
                ", daysLeft=" + daysLeft +
                ", playTime=" + playTime +
                ", token='" + token + '\'' +
                ", uuid='" + uuid + '\'' +
                '}';
    }
}
