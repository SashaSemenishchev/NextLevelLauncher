package millida;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;
import javafx.util.Duration;
import millida.utils.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Controller {

    double x, y;
    boolean entered = false;

    @FXML public Button runButton;
    @FXML public AnchorPane rootPane;
    @FXML public ImageView playerHead;
    @FXML public Label playerNameLabel;
    @FXML public Label playTime;
    @FXML public Label launchStatus;
    @FXML public ImageView closeButton;
    @FXML public ImageView minimizeButton;
    @FXML public ImageView background;
    @FXML public HBox hbox;
    @FXML public ListView<String> newsList;
    @FXML public ProgressBar progressBar;
    @FXML public ImageView settingsButton;
    @FXML public ImageView closeButton1;
    @FXML public TextField ramArgument;
    @FXML public Pane settingsPane;
    @FXML public Hyperlink logoutButton;
    @FXML public Button clientDirectoryChooseButton;
    @FXML public TextField clientDirectory;
    @FXML public Button moveDirectoryButton;
    @FXML public Button reloadClientButton;
    @FXML public Label versionLabel;
    @FXML public Hyperlink profileLink;
    @FXML public ChoiceBox<String> serverChooserButton;
    @FXML public Label serverChooserLabel;
    @FXML public ListView<JSONObject> newsListView;

    public static String directoryToLaunch;

    public static String neededHash = "123";
    public static Thread bootstrapThread;
    public static String ram = "2G";
    public static ColorAdjust dark = new ColorAdjust();
    public static ColorAdjust normal = new ColorAdjust();

    static ColorAdjust colorAdjust = new ColorAdjust();
    public static HashMap<String, String> vitalFolders = new HashMap<>();
    public Server chosenServer = Server.VANILLA;

    public DirectoryChooser chooser = new DirectoryChooser();

    public static String versionsHashVanilla;
    public static String librariesHashVanilla;
    public static String assetsHashVanilla;
    public static String versionsHashMinigames;
    public static String librariesHashMinigames;
    public static String assetsHashMinigames;

    static {
        colorAdjust.setBrightness(0.0);
        normal.setBrightness(0);
        dark.setBrightness(-0.3);
        try {
            URL url = new URL("https://nextlevel.su/api/v1/hashes/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.addRequestProperty("User-Agent", "PostmanRuntime/7.26.8");
            connection.connect();
            if(connection.getResponseCode() != 200){
                System.exit(1);
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
            versionsHashMinigames = root.get("versionsMinigames").toString();
            librariesHashMinigames = root.get("librariesMinigames").toString();
            assetsHashMinigames = root.get("assetsMinigames").toString();
            vitalFolders.put("assets", root.get("assets").toString());
            vitalFolders.put("libraries", root.get("libraries").toString());
            vitalFolders.put("versions", root.get("versions").toString());
            neededHash = root.get("mods").toString();
            System.out.println(neededHash);
            System.out.println(vitalFolders);
        } catch (Exception exception){
            fatalError(exception);
        }
    }

    public void copyDirectory(File src, File dest){
        if(src.isDirectory()){
            if(!dest.exists()){
                if(!dest.mkdir()){
                    System.out.println("Failed to create folder");
                }
            }

            String[] list = src.list();

            for(String file : list){
                try {
                    copyDirectory(new File(src, file), new File(dest, file));
                } catch (Exception exception){
                    exception.printStackTrace();
                }
            }
        } else {
            try {
                InputStream in = new FileInputStream(src);
                OutputStream out = new FileOutputStream(dest);
                byte[] buffer = new byte[1024];
                int length;
                long total = src.length();
                long[] downloaded = {0};
                Platform.runLater(() -> progressBar.setProgress(0.0));
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                    downloaded[0] += length;
                    Platform.runLater(() -> progressBar.setProgress((float)downloaded[0] / total));
                }
            } catch (Exception exception){
                error(exception);
            }
        }
    }

    public static void error(Exception exception){
        Platform.runLater(() -> {
            exception.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Ошибка в исполнении программы");
            alert.setHeaderText(exception + "");
            alert.setTitle("Ошибка");
            alert.showAndWait();
        });
    }

    public static void error(Exception exception, String error){
        Platform.runLater(() -> {
            exception.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(error);
            alert.setHeaderText(exception + "");
            alert.setTitle("Ошибка");
            alert.showAndWait();
        });
    }

    public static void fatalError(Exception exception){Platform.runLater(() -> {
        exception.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText("Фатальная ошибка в исполнении программы");
        alert.setHeaderText(exception + "");
        alert.setTitle("Фатальная ошибка");
        alert.showAndWait();
        System.exit(-1);
    });}

    public void setupLaunchDir() {
        try {
            String server = (Main.readFile(Main.dataFolder.getCanonicalPath() + File.separator + "data.txt", StandardCharsets.UTF_8).replace("\n", "").replace("\r", ""));
            directoryToLaunch = server + (!server.endsWith(File.separator) ? File.separator : "") + chosenServer.name();
            directoryToLaunch += (directoryToLaunch.endsWith(File.separator) ? "" : File.separator);
            System.out.println(directoryToLaunch);
        } catch (IOException e) {
            error(e);
        }
    }

    public void setupDirLabel() throws IOException {
        String server = (Main.readFile(Main.dataFolder.getCanonicalPath() + File.separator + "data.txt", StandardCharsets.UTF_8).replace("\n", "").replace("\r", ""));
        server = server + (!server.endsWith(File.separator) ? File.separator : "");
//        System.out.println("server: " + server);
        clientDirectory.setText(server);
    }

    @FXML
    public void initialize() throws IOException {
        newsListView.setVisible(false);
        profileLink.setOnAction(event -> {
            profileLink.setBorder(null);
            Main.hostServices.showDocument("https://nextlevel.su/profile/");
            profileLink.setVisited(false);
        });
        runButton.setStyle("-fx-background-radius: 100; -fx-pref-height: 51; -fx-pref-width: 220; -fx-max-width: 220; -fx-max-height: 51; -fx-background-color:  #F25B49; -fx-effect: dropshadow(three-pass-box, rgba(242, 91, 73, 0.5), 100, 0, 0, 0);");
        launchStatus.setVisible(false);
        progressBar.setVisible(false);
        settingsPane.setVisible(false);
        serverChooserButton.getItems().clear();
        for(Server server : Server.values()){
            serverChooserButton.getItems().add(server.name);
        }
//        directoryToLaunch = (Main.readFile(Main.dataFolder.getCanonicalPath() + File.separator + "data.txt", StandardCharsets.UTF_8).replace("\n", "").replace("\r", "")) + chosenServer.name;
        setupLaunchDir();
        setupDirLabel();
        if(Main.loginData == null){
            Main.primaryStageStatic.setScene(new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("login.fxml"))), 350, 438));
            return;
        }
        versionLabel.setText("master/" + Main.hash + " ©Millida Studio 2020-2021");
        serverChooserButton.setOnMouseEntered(event -> entered = true);
        serverChooserButton.setOnMouseExited(event -> entered = false);
        runButton.setOnMouseEntered(e -> {
            Platform.runLater(()-> {
                Timeline fadeInTimeline = new Timeline(
                        new KeyFrame(Duration.seconds(0),
                                new KeyValue(colorAdjust.brightnessProperty(), colorAdjust.brightnessProperty().getValue(), Interpolator.LINEAR)),
                        new KeyFrame(Duration.seconds(0.3), new KeyValue(colorAdjust.brightnessProperty(), -0.3, Interpolator.LINEAR)
                        ));
                fadeInTimeline.setCycleCount(1);
                fadeInTimeline.setAutoReverse(false);
                fadeInTimeline.play();
            });
        });
        serverChooserLabel.setText(chosenServer.name);
        serverChooserButton.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            Server newServer = Server.MINIGAMES.getByName(serverChooserButton.getItems().get((Integer)newValue));
            serverChooserLabel.setText(newServer.name);
            chosenServer = newServer;
            setupLaunchDir();
        });
        runButton.setOnMouseExited(e -> {
            Platform.runLater(()-> {
                Timeline fadeOutTimeline = new Timeline(
                        new KeyFrame(Duration.seconds(0),
                                new KeyValue(colorAdjust.brightnessProperty(), colorAdjust.brightnessProperty().getValue(), Interpolator.LINEAR)),
                        new KeyFrame(Duration.seconds(0.3), new KeyValue(colorAdjust.brightnessProperty(), 0, Interpolator.LINEAR)
                        ));
                fadeOutTimeline.setCycleCount(1);
                fadeOutTimeline.setAutoReverse(false);
                fadeOutTimeline.play();
            });
        });

        logoutButton.setOnAction(event -> {
            try {
                File file = new File(Main.dataFolder.getCanonicalPath() + File.separator + "token.txt");
                PrintWriter writer = new PrintWriter(file);
                writer.write("");
                writer.close();
                Main.primaryStageStatic.setScene(new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("login.fxml"))));
            } catch (Exception exception) {
                error(exception);
            }
        });

        SetupController.configureDirectoryChooser(chooser);

        clientDirectoryChooseButton.setOnAction(event -> {
            File directory = Main.clientDirectoryChooser(chooser);
            if(directory == null) return;
            try {
                clientDirectory.setText(directory.getCanonicalPath() + File.separator);
            } catch (Exception exception) {
                error(exception);
            }
        });

        moveDirectoryButton.setOnAction(event -> {
            String path;
            try {
                path = Main.dataFolder.getCanonicalPath() + File.separator + "data.txt";
            } catch (Exception e) {
                error(e);
                return;
            }
            if(path.trim().equals("")) return;
            final String oldPath = Main.readFile(path, StandardCharsets.UTF_8);
            try {
                if(new File(oldPath).getCanonicalPath().equals(new File(clientDirectory.getText()).getCanonicalPath()))return;
            } catch (Exception exception){
                error(exception);
                return;
            }
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Перемещение файлов");
            alert.setHeaderText(null);
            alert.setContentText("Хотите ли вы переместить все ваши файлы в новую директорию?");
            ButtonType okButton = new ButtonType("Да", ButtonBar.ButtonData.YES);
            ButtonType noButton = new ButtonType("Нет", ButtonBar.ButtonData.NO);
            ButtonType cancelButton = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(okButton, noButton, cancelButton);
            alert.showAndWait().ifPresent(type -> {
                if(type != cancelButton){
                    try{
                        PrintWriter writer = new PrintWriter(path);
                        writer.write(clientDirectory.getText());
                        writer.close();
                        setupLaunchDir();
                        if (type == ButtonType.OK) {
                            moveDirectoryButton.setDisable(true);
                            runButton.setVisible(false);
                            launchStatus.setText("Копирование ваших файлов");
                            launchStatus.setVisible(true);
                            progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
                            progressBar.setVisible(true);
                            Thread thread = new Thread(() -> {
                                Platform.runLater(() -> progressBar.setProgress(0.0));
                                File src = new File(oldPath);
                                copyDirectory(src, new File(clientDirectory.getText()));
                                Platform.runLater(() -> {
                                    moveDirectoryButton.setDisable(false);
                                    launchStatus.setVisible(false);
                                    progressBar.setVisible(false);
                                    runButton.setVisible(true);
                                });
                                Thread.currentThread().interrupt();
                            });
                            thread.start();
                        }
                    } catch (Exception exception){
                        error(exception);
                    }
                }
            });
        });

        reloadClientButton.setOnAction(event -> {
            runButton.setVisible(false);
            runButton.setDisable(true);
            launchStatus.setVisible(true);
            progressBar.setVisible(true);
            reloadClientButton.setDisable(true);
            progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
            new Thread(() -> {
               downloadClient();
               downloadMods();
               Platform.runLater(() -> {
                   runButton.setVisible(true);
                   runButton.setDisable(false);
                   launchStatus.setVisible(false);
                   progressBar.setVisible(false);
                   reloadClientButton.setDisable(false);
               });
               Thread.currentThread().interrupt();
            }).start();
        });

        runButton.setEffect(colorAdjust);
        runButton.setOnAction(event -> {
            runButton.setVisible(false);
            runButton.setDisable(true);
            launchStatus.setVisible(true);
            progressBar.setVisible(true);
            serverChooserButton.setVisible(false);
            serverChooserLabel.setVisible(false);
            progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
            launchStatus.setText(LaunchStatus.INDEXING.status);
            Thread thread = new Thread(() -> {
                try {
                    URL url = new URL("https://nextlevel.su/api/v1/game/launcher/");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.addRequestProperty("User-Agent", "PostmanRuntime/7.26.8");
                    connection.addRequestProperty("Authorization", "Bearer " + Main.loginData.token);
                    connection.addRequestProperty("Content-Type", "application/json");
                    connection.addRequestProperty("Accept", "application/json");
                    connection.setDoOutput(true);
                    String jsonString = "{\"username\":\"" + Main.loginData.username + "\"}";
                    connection.getOutputStream().write(jsonString.getBytes(StandardCharsets.UTF_8));
                    connection.connect();
                    if(connection.getResponseCode() != 200){
                        System.out.println(connection.getResponseCode() + " " + connection.getResponseMessage());
                        connection.disconnect();
                        Platform.runLater(() -> {
                            Alert.AlertType alertAlertType = AlertType.ERROR;
                            Alert alert = new Alert(alertAlertType);
                            alert.setContentText("Ошибка авторизации! Попробуйте зайти в настройки и нажать выйти");
                            alert.setHeaderText(null);
                            alert.setTitle("Ошибка");
                            alert.show();
                        });
                        return;
                    }
                    connection.disconnect();
                } catch (Exception exception){
                    error(exception);
                }
                try {
                    File launchDir = new File(directoryToLaunch);
                    File modsDir = new File(directoryToLaunch + "mods" + File.separator);
                    File sessionLock = new File(directoryToLaunch + "session.lock");
                    if(!launchDir.exists()){
                        Platform.runLater(() -> progressBar.setProgress(0.0));
                        launchDir.mkdirs();
                        downloadClient();
                        downloadMods();
                    } else {
                        if(!modsDir.exists()){
                            downloadMods();
                        } else {
                            Platform.runLater(() -> {launchStatus.setText(LaunchStatus.HASHING.status); progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);});
                            String hash = calcMD5HashForDir(modsDir, true, false, progressBar);
                            System.out.println(hash);
                            if(!hash.equals(neededHash) && !Main.debug){
                                if(!modsDir.delete()) {
                                    for(String file : modsDir.list()){
                                        new File(modsDir.getCanonicalPath() + File.separator + file).delete();
                                    }
                                }
                                downloadMods();
                            }
                        }
                        Platform.runLater(() -> {launchStatus.setText(LaunchStatus.HASHING.status); progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);});
                        boolean isNeedToReDownload;
                        ArrayList<File> collected = new ArrayList<>();
                        System.out.println("{");
                        for(File file : launchDir.listFiles()){
                            if(file.isDirectory()){
                                if(Main.debug){
                                    System.out.println("\"" + file.getName() + "\": \"" + calcMD5HashForDir(file, true, false, progressBar) + "\"");
                                } else {
                                    String hash = calcMD5HashForDir(file, true, false, progressBar);
                                    if(vitalFolders.containsKey(file.getName()) && hash.equals(vitalFolders.get(file.getName()))){
                                        collected.add(file);
                                    } else if(file.getName().equals("assets") && Long.parseLong(calcMD5HashForDir(file, true, false, progressBar)) > 330000000){
                                        collected.add(file);
                                    }
                                    System.out.println(file.getName() + ": " + hash);
                                }
                            }
                        }
                        System.out.println("}");

                        isNeedToReDownload = collected.size() < vitalFolders.size();
                        if(isNeedToReDownload){
                            if(!Main.debug) {
                                for(File file : launchDir.listFiles()){
                                    if(vitalFolders.containsKey(file.getName())){
                                        file.delete();
                                    }
                                }
                                downloadClient();
                                downloadMods();
                            }
                        }
                    }

                    Platform.runLater(()-> {
                        launchStatus.setText(LaunchStatus.STARTING.status);
                        progressBar.setVisible(false);
                        progressBar.setProgress(-1);
                    });
                    InputStream stream = getClass().getClassLoader().getResource("servers.dat").openStream();
                    copyInputStreamToFile(stream, new File(launchDir.getCanonicalPath() + File.separator + "servers.dat"));
                    stream.close();
                    System.out.println(directoryToLaunch);
                    String launchString;
                    if(chosenServer.equals(Server.VANILLA)){
                        launchString = "java -XX:+UseConcMarkSweepGC -XX:-UseAdaptiveSizePolicy -XX:+CMSParallelRemarkEnabled -XX:+ParallelRefProcEnabled -XX:+CMSClassUnloadingEnabled -XX:+UseCMSInitiatingOccupancyOnly -Xmx" + ram + " -Dfile.encoding=UTF-8 -XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump -Xss1M -Djava.library.path=" + directoryToLaunch + "versions" + File.separator + "nextlevel" + File.separator + "natives -Dminecraft.launcher.brand=java-minecraft-launcher -Dminecraft.launcher.version=1.6.84-j -cp " + directoryToLaunch + "libraries" + File.separator + "net" + File.separator + "minecraftforge" + File.separator + "forge" + File.separator + "1.16.5-36.0.58" + File.separator + "forge-1.16.5-36.0.58.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org" + File.separator + "ow2" + File.separator + "asm" + File.separator + "asm" + File.separator + "9.0" + File.separator + "asm-9.0.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org" + File.separator + "ow2" + File.separator + "asm" + File.separator + "asm-commons" + File.separator + "9.0" + File.separator + "asm-commons-9.0.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org" + File.separator + "ow2" + File.separator + "asm" + File.separator + "asm-tree" + File.separator + "9.0" + File.separator + "asm-tree-9.0.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org" + File.separator + "ow2" + File.separator + "asm" + File.separator + "asm-util" + File.separator + "9.0" + File.separator + "asm-util-9.0.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org" + File.separator + "ow2" + File.separator + "asm" + File.separator + "asm-analysis" + File.separator + "9.0" + File.separator + "asm-analysis-9.0.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "cpw" + File.separator + "mods" + File.separator + "modlauncher" + File.separator + "8.0.9" + File.separator + "modlauncher-8.0.9.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "cpw" + File.separator + "mods" + File.separator + "grossjava9hacks" + File.separator + "1.3.0" + File.separator + "grossjava9hacks-1.3.0.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "net" + File.separator + "minecraftforge" + File.separator + "accesstransformers" + File.separator + "3.0.1" + File.separator + "accesstransformers-3.0.1.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org" + File.separator + "antlr" + File.separator + "antlr4-runtime" + File.separator + "4.9.1" + File.separator + "antlr4-runtime-4.9.1.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "net" + File.separator + "minecraftforge" + File.separator + "eventbus" + File.separator + "4.0.0" + File.separator + "eventbus-4.0.0.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "net" + File.separator + "minecraftforge" + File.separator + "forgespi" + File.separator + "3.2.0" + File.separator + "forgespi-3.2.0.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "net" + File.separator + "minecraftforge" + File.separator + "coremods" + File.separator + "4.0.6" + File.separator + "coremods-4.0.6.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "net" + File.separator + "minecraftforge" + File.separator + "unsafe" + File.separator + "0.2.0" + File.separator + "unsafe-0.2.0.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "com" + File.separator + "electronwill" + File.separator + "night-config" + File.separator + "core" + File.separator + "3.6.3" + File.separator + "core-3.6.3.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "com" + File.separator + "electronwill" + File.separator + "night-config" + File.separator + "toml" + File.separator + "3.6.3" + File.separator + "toml-3.6.3.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org" + File.separator + "jline" + File.separator + "jline" + File.separator + "3.12.1" + File.separator + "jline-3.12.1.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org" + File.separator + "apache" + File.separator + "maven" + File.separator + "maven-artifact" + File.separator + "3.6.3" + File.separator + "maven-artifact-3.6.3.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "net" + File.separator + "jodah" + File.separator + "typetools" + File.separator + "0.8.3" + File.separator + "typetools-0.8.3.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org" + File.separator + "apache" + File.separator + "logging" + File.separator + "log4j" + File.separator + "log4j-api" + File.separator + "2.11.2" + File.separator + "log4j-api-2.11.2.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org" + File.separator + "apache" + File.separator + "logging" + File.separator + "log4j" + File.separator + "log4j-core" + File.separator + "2.11.2" + File.separator + "log4j-core-2.11.2.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "net" + File.separator + "minecrell" + File.separator + "terminalconsoleappender" + File.separator + "1.2.0" + File.separator + "terminalconsoleappender-1.2.0.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "net" + File.separator + "sf" + File.separator + "jopt-simple" + File.separator + "jopt-simple" + File.separator + "5.0.4" + File.separator + "jopt-simple-5.0.4.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org" + File.separator + "spongepowered" + File.separator + "mixin" + File.separator + "0.8.2" + File.separator + "mixin-0.8.2.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "net" + File.separator + "minecraftforge" + File.separator + "nashorn-core-compat" + File.separator + "15.1.1.1" + File.separator + "nashorn-core-compat-15.1.1.1.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "oshi-project" + File.separator + "oshi-core" + File.separator + "1.1" + File.separator + "oshi-core-1.1.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "net" + File.separator + "java" + File.separator + "dev" + File.separator + "jna" + File.separator + "jna" + File.separator + "4.4.0" + File.separator + "jna-4.4.0.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "net" + File.separator + "java" + File.separator + "dev" + File.separator + "jna" + File.separator + "platform" + File.separator + "3.4.0" + File.separator + "platform-3.4.0.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "com" + File.separator + "ibm" + File.separator + "icu" + File.separator + "icu4j" + File.separator + "66.1" + File.separator + "icu4j-66.1.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "com" + File.separator + "mojang" + File.separator + "javabridge" + File.separator + "1.0.22" + File.separator + "javabridge-1.0.22.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "net" + File.separator + "sf" + File.separator + "jopt-simple" + File.separator + "jopt-simple" + File.separator + "5.0.3" + File.separator + "jopt-simple-5.0.3.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "io" + File.separator + "netty" + File.separator + "netty-all" + File.separator + "4.1.25.Final" + File.separator + "netty-all-4.1.25.Final.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "com" + File.separator + "google" + File.separator + "guava" + File.separator + "guava" + File.separator + "21.0" + File.separator + "guava-21.0.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org" + File.separator + "apache" + File.separator + "commons" + File.separator + "commons-lang3" + File.separator + "3.5" + File.separator + "commons-lang3-3.5.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "commons-io" + File.separator + "commons-io" + File.separator + "2.5" + File.separator + "commons-io-2.5.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "commons-codec" + File.separator + "commons-codec" + File.separator + "1.10" + File.separator + "commons-codec-1.10.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "net" + File.separator + "java" + File.separator + "jinput" + File.separator + "jinput" + File.separator + "2.0.5" + File.separator + "jinput-2.0.5.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "net" + File.separator + "java" + File.separator + "jutils" + File.separator + "jutils" + File.separator + "1.0.0" + File.separator + "jutils-1.0.0.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "com" + File.separator + "mojang" + File.separator + "brigadier" + File.separator + "1.0.17" + File.separator + "brigadier-1.0.17.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "com" + File.separator + "mojang" + File.separator + "datafixerupper" + File.separator + "4.0.26" + File.separator + "datafixerupper-4.0.26.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "com" + File.separator + "google" + File.separator + "code" + File.separator + "gson" + File.separator + "gson" + File.separator + "2.8.0" + File.separator + "gson-2.8.0.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org" + File.separator + "apache" + File.separator + "commons" + File.separator + "commons-compress" + File.separator + "1.8.1" + File.separator + "commons-compress-1.8.1.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org" + File.separator + "apache" + File.separator + "httpcomponents" + File.separator + "httpclient" + File.separator + "4.3.3" + File.separator + "httpclient-4.3.3.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "commons-logging" + File.separator + "commons-logging" + File.separator + "1.1.3" + File.separator + "commons-logging-1.1.3.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org" + File.separator + "apache" + File.separator + "httpcomponents" + File.separator + "httpcore" + File.separator + "4.3.2" + File.separator + "httpcore-4.3.2.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "it" + File.separator + "unimi" + File.separator + "dsi" + File.separator + "fastutil" + File.separator + "8.2.1" + File.separator + "fastutil-8.2.1.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org" + File.separator + "apache" + File.separator + "logging" + File.separator + "log4j" + File.separator + "log4j-api" + File.separator + "2.8.1" + File.separator + "log4j-api-2.8.1.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org" + File.separator + "apache" + File.separator + "logging" + File.separator + "log4j" + File.separator + "log4j-core" + File.separator + "2.8.1" + File.separator + "log4j-core-2.8.1.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org" + File.separator + "lwjgl" + File.separator + "lwjgl" + File.separator + "3.2.2" + File.separator + "lwjgl-3.2.2.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org" + File.separator + "lwjgl" + File.separator + "lwjgl-jemalloc" + File.separator + "3.2.2" + File.separator + "lwjgl-jemalloc-3.2.2.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org" + File.separator + "lwjgl" + File.separator + "lwjgl-openal" + File.separator + "3.2.2" + File.separator + "lwjgl-openal-3.2.2.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org" + File.separator + "lwjgl" + File.separator + "lwjgl-opengl" + File.separator + "3.2.2" + File.separator + "lwjgl-opengl-3.2.2.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org" + File.separator + "lwjgl" + File.separator + "lwjgl-glfw" + File.separator + "3.2.2" + File.separator + "lwjgl-glfw-3.2.2.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org" + File.separator + "lwjgl" + File.separator + "lwjgl-stb" + File.separator + "3.2.2" + File.separator + "lwjgl-stb-3.2.2.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org" + File.separator + "lwjgl" + File.separator + "lwjgl-tinyfd" + File.separator + "3.2.2" + File.separator + "lwjgl-tinyfd-3.2.2.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "com" + File.separator + "mojang" + File.separator + "text2speech" + File.separator + "1.11.3" + File.separator + "text2speech-1.11.3.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "com" + File.separator + "mojang" + File.separator + "authlib" + File.separator + "2.1.28" + File.separator + "authlib-2.1.28.jar\";\"" + directoryToLaunch + "versions" + File.separator + "nextlevel" + File.separator + "nextlevel.jar cpw.mods.modlauncher.Launcher --username " + Main.loginData.username + " --version nextlevel --gameDir " + directoryToLaunch.substring(0, directoryToLaunch.length() - 1).intern() + " --assetsDir " + directoryToLaunch + "assets --assetIndex 1.16 --uuid " + Main.loginData.uuid + " --userType mojang --versionType release --width 925 --height 530 --launchTarget fmlclient --fml.forgeVersion 36.0.58 --fml.mcVersion 1.16.5 --fml.forgeGroup net.minecraftforge --fml.mcpVersion 20210115.111550 --accessToken " + Main.loginData.token;
                    }
                    else {
//                        launchString = "java -XX:+UseConcMarkSweepGC -XX:-UseAdaptiveSizePolicy -XX:+CMSParallelRemarkEnabled -XX:+ParallelRefProcEnabled -XX:+CMSClassUnloadingEnabled -XX:+UseCMSInitiatingOccupancyOnly -Xmx" + ram + " -Dfile.encoding=UTF-8 -XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump -Xss1M -Djava.library.path=\"" + directoryToLaunch + "versions" + File.separator + "OptiFine 1.16.5" + File.separator + "natives\" -Dminecraft.launcher.brand=java-minecraft-launcher -Dminecraft.launcher.version=1.6.84-j -cp \"" + directoryToLaunch + "libraries" + File.separator + "optifine" + File.separator + "OptiFine" + File.separator + "1.16.5_HD_U_G7" + File.separator + "OptiFine-1.16.5_HD_U_G7.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "optifine" + File.separator + "launchwrapper" + File.separator + "2.2" + File.separator + "launchwrapper-2.2.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "oshi-project" + File.separator + "oshi-core" + File.separator + "1.1" + File.separator + "oshi-core-1.1.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "net" + File.separator + "java" + File.separator + "dev" + File.separator + "jna" + File.separator + "jna" + File.separator + "4.4.0" + File.separator + "jna-4.4.0.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "net" + File.separator + "java" + File.separator + "dev" + File.separator + "jna" + File.separator + "platform" + File.separator + "3.4.0" + File.separator + "platform-3.4.0.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "com" + File.separator + "ibm" + File.separator + "icu" + File.separator + "icu4j" + File.separator + "66.1" + File.separator + "icu4j-66.1.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "com" + File.separator + "mojang" + File.separator + "javabridge" + File.separator + "1.0.22" + File.separator + "javabridge-1.0.22.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "net" + File.separator + "sf" + File.separator + "jopt-simple" + File.separator + "jopt-simple" + File.separator + "5.0.3" + File.separator + "jopt-simple-5.0.3.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "io" + File.separator + "netty" + File.separator + "netty-all" + File.separator + "4.1.25.Final" + File.separator + "netty-all-4.1.25.Final.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "com" + File.separator + "google" + File.separator + "guava" + File.separator + "guava" + File.separator + "21.0" + File.separator + "guava-21.0.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org" + File.separator + "apache" + File.separator + "commons" + File.separator + "commons-lang3" + File.separator + "3.5" + File.separator + "commons-lang3-3.5.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "commons-io" + File.separator + "commons-io" + File.separator + "2.5" + File.separator + "commons-io-2.5.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "commons-codec" + File.separator + "commons-codec" + File.separator + "1.10" + File.separator + "commons-codec-1.10.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "net" + File.separator + "java" + File.separator + "jinput" + File.separator + "jinput" + File.separator + "2.0.5" + File.separator + "jinput-2.0.5.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "net" + File.separator + "java" + File.separator + "jutils" + File.separator + "jutils" + File.separator + "1.0.0" + File.separator + "jutils-1.0.0.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "com" + File.separator + "mojang" + File.separator + "brigadier" + File.separator + "1.0.17" + File.separator + "brigadier-1.0.17.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "com" + File.separator + "mojang" + File.separator + "datafixerupper" + File.separator + "4.0.26" + File.separator + "datafixerupper-4.0.26.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "com" + File.separator + "google" + File.separator + "code" + File.separator + "gson" + File.separator + "gson" + File.separator + "2.8.0" + File.separator + "gson-2.8.0.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org" + File.separator + "apache" + File.separator + "commons" + File.separator + "commons-compress" + File.separator + "1.8.1" + File.separator + "commons-compress-1.8.1.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org" + File.separator + "apache" + File.separator + "httpcomponents" + File.separator + "httpclient" + File.separator + "4.3.3" + File.separator + "httpclient-4.3.3.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "commons-logging" + File.separator + "commons-logging" + File.separator + "1.1.3" + File.separator + "commons-logging-1.1.3.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org" + File.separator + "apache" + File.separator + "httpcomponents" + File.separator + "httpcore" + File.separator + "4.3.2" + File.separator + "httpcore-4.3.2.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "it" + File.separator + "unimi" + File.separator + "dsi" + File.separator + "fastutil" + File.separator + "8.2.1" + File.separator + "fastutil-8.2.1.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org" + File.separator + "apache" + File.separator + "logging" + File.separator + "log4j" + File.separator + "log4j-api" + File.separator + "2.8.1" + File.separator + "log4j-api-2.8.1.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org" + File.separator + "apache" + File.separator + "logging" + File.separator + "log4j" + File.separator + "log4j-core" + File.separator + "2.8.1" + File.separator + "log4j-core-2.8.1.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org" + File.separator + "lwjgl" + File.separator + "lwjgl" + File.separator + "3.2.2" + File.separator + "lwjgl-3.2.2.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org" + File.separator + "lwjgl" + File.separator + "lwjgl-jemalloc" + File.separator + "3.2.2" + File.separator + "lwjgl-jemalloc-3.2.2.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org" + File.separator + "lwjgl" + File.separator + "lwjgl-openal" + File.separator + "3.2.2" + File.separator + "lwjgl-openal-3.2.2.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org" + File.separator + "lwjgl" + File.separator + "lwjgl-opengl" + File.separator + "3.2.2" + File.separator + "lwjgl-opengl-3.2.2.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org" + File.separator + "lwjgl" + File.separator + "lwjgl-glfw" + File.separator + "3.2.2" + File.separator + "lwjgl-glfw-3.2.2.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org" + File.separator + "lwjgl" + File.separator + "lwjgl-stb" + File.separator + "3.2.2" + File.separator + "lwjgl-stb-3.2.2.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org" + File.separator + "lwjgl" + File.separator + "lwjgl-tinyfd" + File.separator + "3.2.2" + File.separator + "lwjgl-tinyfd-3.2.2.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "com" + File.separator + "mojang" + File.separator + "text2speech" + File.separator + "1.11.3" + File.separator + "text2speech-1.11.3.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "by" + File.separator + "ely" + File.separator + "authlib" + File.separator + "2.0.27.4" + File.separator + "authlib-2.0.27.4.jar\";\"" + directoryToLaunch + "versions" + File.separator + "OptiFine 1.16.5" + File.separator + "OptiFine 1.16.5.jar\" net.minecraft.launchwrapper.Launch --username " + Main.loginData.username + " --version OptiFine 1.16.5 --gameDir " + directoryToLaunch.substring(0, directoryToLaunch.length() - 1).intern() + " --assetsDir " + directoryToLaunch + "assets --assetIndex 1.16 --uuid " + Main.loginData.uuid + " --accessToken " + Main.loginData.token + " --userType mojang --versionType modified --width 925 --height 530 --tweakClass optifine.OptiFineTweaker";
                        try {
                            throw new Exception("Not implemented error");
                        } catch (Exception exception){
                            error(exception, "Not implemented yet");
                            return;
                        }
                    }

                    if(Main.debug) System.out.println("Full command (not escaped): " + launchString + "\n\n");
                    sessionLock.createNewFile();
                    PrintWriter sessionLockWriter = new PrintWriter(sessionLock);
                    sessionLockWriter.write(Main.loginData.token);
                    sessionLockWriter.close();

                    Platform.runLater(() -> {
                        ProcessBuilder processBuilder = new ProcessBuilder();
                        if(FileManager.osType == OsType.WIN){
                            processBuilder.command("cmd.exe", "/c", launchString);
                        } else if(FileManager.osType == OsType.MAC){
                            processBuilder.command(launchString);
                        } else {
                            processBuilder.command("/bin/bash", "-c", launchString);
                        }
                        if(!FileManager.osType.equals(OsType.MAC)){
                            processBuilder.directory(new File(directoryToLaunch));
                        }
                        try {
                            Main.primaryStageStatic.hide();
                            Process process = processBuilder.start();
                            StringBuilder output = new StringBuilder();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                            String line;
                            while ((line = reader.readLine()) != null) {
                                output.append(line).append("\n");
                            }
                            reader.close();
                            System.out.println(output.toString());
                            Main.primaryStageStatic.show();
                            runButton.setVisible(true);
                            launchStatus.setVisible(false);
                            runButton.setDisable(false);
                            serverChooserButton.setVisible(true);
                            serverChooserLabel.setVisible(true);
                            Thread.currentThread().stop();
                            LoginData newData = LoginData.instance(Main.loginData.token);
                            if(newData != null){
                                Main.loginData = newData;
                                long hours = Main.loginData.playTime / 60;
                                playTime.requestFocus();
                                playTime.setText("Всего в игре: " + stringFromHours(hours));
                                playTime.requestFocus();
                            }
                            bootstrapThread = null;
                            sessionLock.delete();
                        } catch (Exception e){
                            error(e);
                        }
                        Thread.currentThread().interrupt();
                    });
                } catch (Exception exception) {
                    error(exception);
                }
            });
            thread.start();
            bootstrapThread = thread;
        });

        settingsButton.setOnMouseClicked(event -> {
            settingsPane.setVisible(!settingsPane.isVisible());
        });

        if(ramArgument.getText() != null){
            if(!ramArgument.getText().trim().equals("")){
                ramArgument.setText(ram);
            } else {
                ramArgument.setText("2G");
            }
        } else {
            ramArgument.setText("2G");
        }

        settingsButton.setOnMouseEntered(event -> settingsButton.setEffect(dark));
        settingsButton.setOnMouseExited(event -> settingsButton.setEffect(normal));

//        directoryToLaunch = (Main.readFile(Main.dataFolder.getCanonicalPath() + File.separator + "data.txt", StandardCharsets.UTF_8).replace("\n", "").replace("\r", ""));
//        if(!directoryToLaunch.endsWith(File.separator)){
//            directoryToLaunch += File.separator;
//        }
        launchStatus.setVisible(false);
        playerNameLabel.setText(Main.loginData.username);
        long hours = Main.loginData.playTime / 60;
        playTime.requestFocus();
        playTime.setText(stringFromHours(hours));
        playTime.requestFocus();

        closeButton.setOnMouseClicked(event -> {
            Main.primaryStageStatic.close();
            Platform.exit();
        });

        closeButton.setOnMouseEntered(event -> {
            closeButton.setEffect(dark);
        });

        closeButton.setOnMouseExited(event -> {
            closeButton.setEffect(normal);
        });

        minimizeButton.setOnMouseClicked(event -> {
            Main.primaryStageStatic.setIconified(true);
        });

        minimizeButton.setOnMouseEntered(event -> {
            minimizeButton.setEffect(dark);
        });

        minimizeButton.setOnMouseExited(event -> {
            minimizeButton.setEffect(normal);
        });

        try {
            URL url = new URL("https://nextlevel.su" + Main.loginData.skinSignature);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.addRequestProperty("User-Agent", "PostmanRuntime/7.26.8");
            connection.connect();
            playerHead.setImage(SwingFXUtils.toFXImage(ImageIO.read(connection.getInputStream()), null));
            connection.disconnect();
            Rectangle clip = new Rectangle(playerHead.getFitHeight(), playerHead.getFitWidth());
            clip.setArcHeight(20);
            clip.setArcWidth(20);
            playerHead.setClip(clip);
        } catch (Exception e) {
           error(e);
           playerHead.setVisible(false);
        }

        closeButton1.setOnMouseClicked( event -> {
            if(ramArgument.getText().trim().equals("") || ramArgument.getText() == null){
                ramArgument.setText("2G");
            }
            ram = ramArgument.getText();
            try {
                File file = new File(Main.dataFolder.getCanonicalPath() + File.separator + "ram.txt");
                if(!file.exists()){
                    file.createNewFile();
                }
                PrintWriter ramWriter = new PrintWriter(file);
                ramWriter.write(ram);
                ramWriter.close();
            } catch (Exception e) {
                error(e);
            }
            settingsPane.setVisible(false);
        });

        closeButton1.setOnMouseEntered(event -> closeButton1.setEffect(dark));
        closeButton1.setOnMouseExited(event -> closeButton1.setEffect(normal));

        hbox.setOnMousePressed(event -> {
            x = event.getX();
            y = event.getY();
        });

        hbox.setOnMouseDragged(event -> {
            Main.primaryStageStatic.setX(event.getScreenX() - x);
            Main.primaryStageStatic.setY(event.getScreenY() - y);
        });

        try {
            URL url = new URL("https://nextlevel.su/api/v1/article/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.addRequestProperty("User-Agent", "PostmanRuntime/7.26.8");
            connection.addRequestProperty("Accept", "application/json");
            connection.connect();
            if(connection.getResponseCode() == 200){
                Scanner in = new Scanner(connection.getInputStream());
                StringBuilder sb = new StringBuilder();
                while(in.hasNext()){
                    sb.append(in.nextLine());
                }
                in.close();
                JSONParser parser = new JSONParser();
                JSONArray articles = (JSONArray) parser.parse(sb.toString());
                ObservableList<JSONObject> items = FXCollections.observableArrayList();
                for(Object object : articles){
                    JSONObject article = (JSONObject) object;
                    System.out.println(article.get("title"));
                    System.out.println(article.get("content"));
                    items.add(article);
                }
                newsListView.setCellFactory(article -> new NewsCell());
                newsListView.setItems(items);
            }
            connection.disconnect();
        } catch (Exception exception){
            exception.printStackTrace();
            // TODO: Label with error "Не можем проверить новости"
        }
    }

    public String stringFromHours(long hours) {
        StringBuilder sb = new StringBuilder(hours + " ");
        String stringHours = String.valueOf(hours);
        if(hours == 0){
            sb.append("часов");
        } else if((stringHours.endsWith("2") || stringHours.endsWith("3") || stringHours.endsWith("4")) && !stringHours.startsWith("1")){
            sb.append("часа");
        } else {
            sb.append("часов");
        }
        return sb.toString();
    }

    public static void copyInputStreamToFile(InputStream source, File destination) throws IOException {
        InputStream in = source;
        Throwable var3 = null;

        try {
            copyToFile(in, destination);
        } catch (Throwable var12) {
            var3 = var12;
            throw var12;
        } finally {
            if (source != null) {
                if (var3 != null) {
                    try {
                        in.close();
                    } catch (Throwable var11) {
                        var3.addSuppressed(var11);
                    }
                } else {
                    source.close();
                }
            }

        }

    }

    public void hideButtonAndShowAll(){
        Platform.runLater(() -> {

        });
    }

    static void copy(InputStream source, OutputStream target) throws IOException {
        byte[] buf = new byte[8192];
        int length;
        while ((length = source.read(buf)) > 0) {
            target.write(buf, 0, length);
        }
    }

    public static void copyToFile(InputStream source, File destination) throws IOException {
        InputStream in = source;
        Throwable var3 = null;

        try {
            OutputStream out = openOutputStream(destination, false);
            Throwable var5 = null;

            try {
                copy(in, out);
            } catch (Throwable var28) {
                var5 = var28;
                throw var28;
            } finally {
                if (out != null) {
                    if (var5 != null) {
                        try {
                            out.close();
                        } catch (Throwable var27) {
                            var5.addSuppressed(var27);
                        }
                    } else {
                        out.close();
                    }
                }

            }
        } catch (Throwable var30) {
            var3 = var30;
            throw var30;
        } finally {
            if (source != null) {
                if (var3 != null) {
                    try {
                        in.close();
                    } catch (Throwable var26) {
                        var3.addSuppressed(var26);
                    }
                } else {
                    source.close();
                }
            }

        }
    }

    public static FileOutputStream openOutputStream(File file, boolean append) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }

            if (!file.canWrite()) {
                throw new IOException("File '" + file + "' cannot be written to");
            }
        } else {
            File parent = file.getParentFile();
            if (parent != null && !parent.mkdirs() && !parent.isDirectory()) {
                throw new IOException("Directory '" + parent + "' could not be created");
            }
        }

        return new FileOutputStream(file, append);
    }

    public void downloadClient(){
        try {
            Platform.runLater(() -> {launchStatus.setText(LaunchStatus.DOWNLOADING_CLIENT.status); progressBar.setProgress(0.0);});
            URL url = new URL(chosenServer.clientDownloadURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.addRequestProperty("User-Agent", "PostmanRuntime/7.26.8");
            connection.connect();
            long completeFileSize = connection.getContentLength();
            InputStream inputStream = connection.getInputStream();
            FileOutputStream fileOS = new FileOutputStream(directoryToLaunch + "client.zip");
            final long[] downloaded = {0};
            byte[] data = new byte[1024];
            int byteContent;
            while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
                downloaded[0] += byteContent;
//                System.out.println(downloaded[0] + " " + (((double)downloaded[0]) / ((double)completeFileSize)) * 100000D);
                Platform.runLater(() -> progressBar.setProgress((((double)downloaded[0]) / ((double)completeFileSize))));
                fileOS.write(data, 0, byteContent);
            }
            inputStream.close();
            fileOS.close();
            connection.disconnect();
            unzip( directoryToLaunch + "client.zip", directoryToLaunch);
            File modsZip = new File(directoryToLaunch + "client.zip");
            System.out.println(modsZip.getCanonicalPath());
            if(!modsZip.delete()){
                System.out.println("Cannot delete client.zip. That's not critical. Just warning");
            }
        } catch (Exception e) {
            error(e, "Ошибка во время загрузки клиента.");
            progressBar.setVisible(false);
            progressBar.setProgress(0.0);
            runButton.setDisable(false);
            runButton.setVisible(true);
            launchStatus.setVisible(false);
        }
    }

    public void downloadMods(){
        if(chosenServer.modsDownloadURL == null){
            return;
        }
        try {
            Platform.runLater(() -> {launchStatus.setText(LaunchStatus.DOWNLOADING_MODS.status); progressBar.setProgress(0.0);});
            URL url = new URL(chosenServer.modsDownloadURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.addRequestProperty("User-Agent", "PostmanRuntime/7.26.8");
            connection.connect();
            long completeFileSize = connection.getContentLength();
            InputStream inputStream = connection.getInputStream();
            FileOutputStream fileOS = new FileOutputStream(directoryToLaunch + "mods.zip");
            long[] downloaded = {0};
            byte[] data = new byte[1024];
            int byteContent;
            while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
                downloaded[0] += byteContent;
                Platform.runLater(() -> progressBar.setProgress((((double)downloaded[0]) / ((double)completeFileSize))));
                fileOS.write(data, 0, byteContent);
            }
            inputStream.close();
            fileOS.close();
            connection.disconnect();
            unzip( directoryToLaunch + "mods.zip", directoryToLaunch);
            File modsZip = new File(directoryToLaunch + "mods.zip");
            System.out.println(modsZip.getCanonicalPath());
            if(!modsZip.delete()){
                System.out.println("Cannot delete mods.zip. That's not critical. Just warning");
            }
        } catch (Exception e) {
            error(e, "Во время загрузки модов возникла ошибка");
            progressBar.setVisible(false);
            progressBar.setProgress(0.0);
            runButton.setDisable(false);
            runButton.setVisible(true);
            launchStatus.setVisible(false);
        }
    }

    private static final int BUFFER_SIZE = 4096;

    public void unzip(String zipFilePath, String destDirectory) throws IOException {
        Platform.runLater(() -> launchStatus.setText(LaunchStatus.UNPACKING.status));
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        long totalSize = entry == null ? 0 : entry.getSize();
        long downloaded = 0;
        while (entry != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                extractFile(zipIn, filePath);
            } else {
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                dir.mkdirs();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
            downloaded++;
            final long percentage = totalSize / downloaded;
            Platform.runLater(() -> progressBar.setProgress(percentage));
        }
        zipIn.close();
    }

    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }

    public static String escape(String s){
        return s.replace("" + File.separator + "", "" + File.separator + "" + File.separator + "")
                .replace("\t", "" + File.separator + "t")
                .replace("\b", "" + File.separator + "b")
                .replace("\n", "" + File.separator + "n")
                .replace("\r", "" + File.separator + "r")
                .replace("\f", "" + File.separator + "f")
                .replace("\'", "" + File.separator + "'")
                .replace("\"", "" + File.separator + "\"");
    }

    public static String calcMD5HashForDir(File directory, boolean includeHiddenFiles, boolean isServer, ProgressBar progressBar) {
        long length = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile())
                length += file.length();
            else
                length += Long.parseLong(calcMD5HashForDir(file, true, true, progressBar));
        }
        return length + "";
    }

    private static void collectInputStreams(File dir, List<FileInputStream> foundStreams, boolean includeHiddenFiles) {

        File[] fileList = dir.listFiles();
        Arrays.sort(fileList, Comparator.comparing(File::getName));

        for (File f : fileList) {
            if (f.isDirectory()) {
                collectInputStreams(f, foundStreams, includeHiddenFiles);
            }
            else {
                try {
                    foundStreams.add(new FileInputStream(f));
                }
                catch (FileNotFoundException ignored) {
                }
            }
        }
    }

}
