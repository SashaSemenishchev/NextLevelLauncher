package su.nextlevel.launcher.login;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.TextAlignment;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import su.nextlevel.launcher.Controller;
import su.nextlevel.launcher.Main;
import su.nextlevel.launcher.token.SimpleTokenCodec;
import su.nextlevel.launcher.util.ConstantPool;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;

public class LoginController {

    @FXML
    public PasswordField passwordField;
    @FXML
    public TextField loginField;
    @FXML
    public Button loginButton;
    @FXML
    public Label authError;

    @FXML
    public void initialize() {
        authError.setVisible(false);
        authError.setTextAlignment(TextAlignment.CENTER);
        loginButton.setOnAction(event -> {
            try {
                URL url = new URL(ConstantPool.SITE_URL + "auth/");
                String login = loginField.getText();
                String password = passwordField.getText();
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.addRequestProperty("User-Agent", "PostmanRuntime/7.26.8");
                connection.addRequestProperty("Content-Type", "application/json");
                connection.addRequestProperty("Accept", "application/json");
                String jsonInputString = String.format("{\"email\":\"%s\", \"password\": \"%s\"}", login, password);
                connection.setDoOutput(true);
                try (OutputStream out = connection.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                    out.write(input, 0, input.length);
                }
                connection.connect();

                if (connection.getResponseCode() != 200) {
                    authError.setText("?????????????????? ???????????????????????? ?????????????????? ?????????? ?? ????????????");
                    authError.setVisible(true);
                    return;
                }
                StringBuilder sb = new StringBuilder();
                Scanner scanner = new Scanner(connection.getInputStream());
                while (scanner.hasNext()) {
                    sb.append(scanner.nextLine());
                }
                scanner.close();
                JSONParser parser = new JSONParser();
                JSONObject root = (JSONObject) parser.parse(sb.toString());
                if (root.containsKey("verify")) {
                    System.out.println(Controller.escape(root.get("verify").toString()));
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setTitle("?????????????????????????? ????????????????????????????");
                    dialog.setHeaderText("?????????????? ?????? ?????????????????????????? ????????????????????????????");
                    dialog.setContentText("??????: ");
                    Optional<String> result = dialog.showAndWait();
                    if (result.isPresent()) {
                        URL sendUrl = new URL(root.get("verify").toString());
                        HttpURLConnection sendConnection = (HttpURLConnection) sendUrl.openConnection();
                        sendConnection.setRequestMethod("POST");
                        sendConnection.addRequestProperty("User-Agent", "PostmanRuntime/7.26.8");
                        sendConnection.addRequestProperty("Content-Type", "application/json");
                        sendConnection.addRequestProperty("Accept", "application/json");
                        String sendConnectionJsonString = "{\"code\":\"" + result.get() + "\"}";
                        sendConnection.setDoOutput(true);
                        try (OutputStream out = sendConnection.getOutputStream()) {
                            out.write(sendConnectionJsonString.getBytes(StandardCharsets.UTF_8));
                        }
                        sendConnection.connect();
                        if (sendConnection.getResponseCode() != 200) {
                            StringBuilder response = new StringBuilder();
                            Scanner responseScanner = new Scanner(connection.getInputStream());
                            while (responseScanner.hasNext()) {
                                response.append(responseScanner.nextLine());
                            }
                            sendConnection.disconnect();
                            responseScanner.close();
                            authError.setText("???????????? ?????????????????????? " + sendConnection.getResponseCode() + " " + sendConnection.getResponseMessage() + "\n" + response.toString());
                            authError.setVisible(true);
                        } else {
                            StringBuilder factorSB = new StringBuilder();
                            Scanner factorScanner = new Scanner(sendConnection.getInputStream());
                            while (factorScanner.hasNext()) {
                                factorSB.append(factorScanner.nextLine());
                            }
                            factorScanner.close();
                            JSONObject factorRoot = (JSONObject) parser.parse(factorSB.toString());
                            String token = factorRoot.get("token").toString();
                            PrintWriter writer = new PrintWriter(Main.dataFolder.getCanonicalPath() + File.separator + "token.txt");

                            String codedToken = SimpleTokenCodec.getInstance().toCoded(token);

                            writer.write(codedToken.replaceAll("\n", "").replaceAll("\r", ""));
                            writer.close();

                            connection.disconnect();
                            Main.loginData = LoginData.instance(token);
                            if (Main.loginData == null) {
                                authError.setText("???? ?????????????? ?????????????????????? ?????????????????? ????????????");
                                authError.setVisible(true);
                                return;
                            }
                            Platform.runLater(() -> {
                                try {
                                    Main.primaryStageStatic.setScene(new Scene(FXMLLoader.load(Objects.requireNonNull(ConstantPool.CLASS_LOADER.getResource("launcher.fxml"))), 951, 580));
                                } catch (IOException exception) {
                                    authError.setText("???? ?????????????? ?????????????????????? ?????????????????? ????????????");
                                    authError.setVisible(true);
                                    exception.printStackTrace();
                                }
                            });
                        }
                    } else {
                        authError.setVisible(true);
                    }
                } else {
                    String token = root.get("token").toString();
                    PrintWriter writer = new PrintWriter(Main.dataFolder.getCanonicalPath() + File.separator + "token.txt");

                    String codedToken = SimpleTokenCodec.getInstance().toCoded(token);

                    writer.write(codedToken.replaceAll("\n", "").replaceAll("\r", ""));
                    writer.close();

                    connection.disconnect();
                    Main.loginData = LoginData.instance(token);
                    if (Main.loginData == null) {
                        authError.setText("???? ?????????????? ?????????????????????? ?????????????????? ????????????");
                        authError.setVisible(true);
                        return;
                    }
                    Platform.runLater(() -> {
                        try {
                            Main.primaryStageStatic.setScene(new Scene(FXMLLoader.load(Objects.requireNonNull(ConstantPool.CLASS_LOADER.getResource("launcher.fxml"))), 951, 580));
                        } catch (IOException exception) {
                            exception.printStackTrace();
                        }
                    });
                }
            } catch (Exception e) {
                authError.setVisible(true);
                e.printStackTrace();
            }
        });
    }
}
