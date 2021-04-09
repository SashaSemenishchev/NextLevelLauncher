package su.nextlevel.launcher.login;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import su.nextlevel.launcher.token.SimpleTokenCodec;
import su.nextlevel.launcher.token.TokenCodec;
import su.nextlevel.launcher.util.ConstantPool;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public final class LoginData {
    private static final String AUTH_HOST = ConstantPool.SITE_URL + "user/";
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final String login;
    private final String username;
    private final String skinSignature;
    private final float money;
    private final long daysLeft;
    private final long playTime;
    private final String token;
    private final String uuid;

    public LoginData(
            final String token,
            final String uuid,
            final String login,
            final String username,
            final String skinSignature,
            final float money,
            final long daysLeft,
            final long playTime) {
        this.token = token;
        this.login = login;
        this.money = money;
        this.daysLeft = daysLeft;
        this.playTime = playTime;
        this.skinSignature = skinSignature;
        this.username = username;
        this.uuid = uuid;
    }

    public static LoginData instance(String token) {
        try {
            URL url = new URL(AUTH_HOST);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.addRequestProperty("User-Agent", "PostmanRuntime/7.26.8");
            connection.addRequestProperty("Authorization", "Bearer " + token.replaceAll("\n", "").replaceAll("\r", ""));
            connection.addRequestProperty("Content-Type", "application/json");
            connection.addRequestProperty("Accept", "application/json");
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                connection.disconnect();
                return null;
            }
            StringBuilder sb = new StringBuilder();
            Scanner scanner = new Scanner(connection.getInputStream());
            while (scanner.hasNext()) {
                String nextLine = scanner.nextLine();
                sb.append(nextLine);
            }
            connection.disconnect();
            scanner.close();
            JSONParser parser = new JSONParser();
            JSONObject root = (JSONObject) parser.parse(sb.toString());
            if (!Boolean.parseBoolean(root.get("is_active").toString())) {
                return null;
            }
            return new LoginData(token, root.get("uuid").toString(), root.get("email").toString(),
                    root.get("username").toString(),
                    ((JSONObject) root.get("skin")).get("skin_head").toString(),
                    Float.parseFloat(root.get("balance").toString()),
                    DATE_FORMAT.parse(root.get("server_access").toString().replace("T", " ")
                            .replace("Z", "")).getTime(),
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

    public float getMoney() {
        return money;
    }

    public long getDaysLeft() {
        return daysLeft;
    }

    public long getPlayTime() {
        return playTime;
    }

    public String getLogin() {
        return login;
    }

    public String getSkinSignature() {
        return skinSignature;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public String getUuid() {
        return uuid;
    }
}
