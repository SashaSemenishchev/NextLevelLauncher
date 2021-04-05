package millida;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import millida.utils.FileManager;
import millida.utils.LoginData;
import org.apache.commons.codec.digest.DigestUtils;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class Main extends Application {

    public static Thread rpcThread;
    public static File dataFolder;
    public static final FileManager manager = new FileManager();
    public static boolean isNewBool;
    public static HostServices hostServices;

    public static LoginData loginData = null;

    public static Stage primaryStageStatic;

    public static String launchString;

    public static boolean debug = true;
    public static String hash;

    public static Stage setupStage;

    public static File clientDirectoryChooser(DirectoryChooser chooser){
        File directory = chooser.showDialog(Main.primaryStageStatic);
        if(directory == null) return null;
        if(!directory.isDirectory()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Необходимо выбрать папку, не файл!");
            alert.setHeaderText(null);
            alert.setTitle("Ошибка");
            alert.showAndWait();
            return null;
        }
        return directory;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        hostServices = getHostServices();
        System.out.println(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        primaryStage.setTitle("NextLevel.su");
        primaryStage.initStyle(StageStyle.UNDECORATED);
        if(manager.mustRunIntoSetup()){
            System.out.println("Must setup");
            Stage stage = new Stage();
            stage.setScene(new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("setup.fxml"))), 328, 192));
            primaryStage = stage;
            primaryStage.show();
            stage.setTitle("NextLevel.su - Установка");
            setupStage = stage;
        } else {
            if (isNewBool) {
                primaryStage.setScene(new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("login.fxml"))), 350, 438));
            } else {
                if (loginData == null) {
                    primaryStage.setScene(new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("login.fxml"))), 350, 438));
                } else {
                    primaryStage.setScene(new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("launcher.fxml"))), 951, 580));
                }
            }
        }
        primaryStage.getIcons().add(SwingFXUtils.toFXImage(ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("logo.png"))), null));
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStageStatic = primaryStage;
    }

    public static void main(String[] args) {
        try {
            DiscordRPC rpc = DiscordRPC.INSTANCE;
            DiscordEventHandlers handlers = new DiscordEventHandlers();
            handlers.ready = (ex) -> System.out.println("ready");
            rpc.Discord_Initialize("817443623113064469", handlers, true, "");
            DiscordRichPresence presence = new DiscordRichPresence();
            presence.largeImageKey = "picture";
            presence.partySize = 10;
            presence.partyMax = 100;
            presence.startTimestamp = System.currentTimeMillis() / 1000;
            presence.details = "Лучшие приватные сервера";
            rpc.Discord_UpdatePresence(presence);
            rpcThread = new Thread(() -> {
                while(!Thread.currentThread().isInterrupted()){
                    rpc.Discord_RunCallbacks();
                    try{
                        Thread.sleep(2600);
                    } catch (InterruptedException ignored) {
                        ignored.printStackTrace();
                    }
                }
            }, "NextLevel-RPC-Handler");

            rpcThread.start();
            Pair<File, Boolean> isNew = manager.get();
            System.out.println("reached after file mngr");
            File warningFile = new File(isNew.getKey().getCanonicalPath() + File.separator + "Прочитай меня!!!.txt");
            if(!warningFile.exists() && !warningFile.createNewFile()){
                System.out.println("Cannot create file");
                System.exit(1);
                return;
            }
            PrintWriter writer = new PrintWriter(warningFile);
            writer.println("ВНИМАНИЕ!!! ЕСЛИ ВАС ПОПРОСИЛИ СКИНУТЬ ЭТУ ПАПКУ ИЛИ ПАПКУ data ТО НЕ В КОЕМ СЛУЧАЕ НЕ ДЕЛАЙТЕ ЭТОГО!!");
            writer.close();

            File dataDirectory = new File(isNew.getKey().getCanonicalPath() + File.separator + "data");
            dataDirectory.mkdirs();
            if(!isNew.getValue()){
                isNewBool = false;
                loginData = LoginData.instance(readFile(dataDirectory.getCanonicalPath() + File.separator + "token.txt", StandardCharsets.UTF_8));
                if(loginData == null){
                    PrintWriter tokenWriter = new PrintWriter(dataDirectory.getCanonicalPath() + File.separator + "token.txt");
                    tokenWriter.write("");
                    tokenWriter.close();
                }
            } else {
                new File(dataDirectory.getCanonicalPath() + File.separator + "token.txt").createNewFile();
                isNewBool = true;
            }
            dataFolder = dataDirectory;
            File ramFile = new File(dataDirectory.getCanonicalPath() + File.separator + "ram.txt");
            if(ramFile.exists()){
                Controller.ram = readFile(ramFile.getCanonicalPath(), StandardCharsets.UTF_8);
            } else {
                Controller.ram = "2G";
            }
            try {
                hash = DigestUtils.md5Hex(Files.newInputStream(new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toPath())).substring(0, 7).intern();
            } catch (Exception ignored){
                hash = "MisterFunny01";
            }

            launch(args);
        } catch (Exception exception){
            rpcThread.interrupt();
            exception.printStackTrace();
            System.exit(-1);
        }
        new File(Controller.directoryToLaunch + "session.lock").delete();
    }

    public static String readFile(String path, Charset encoding) {
        byte[] encoded = new byte[0];
        try {
            encoded = Files.readAllBytes(Paths.get(path));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return new String(encoded, encoding).replaceAll("\n", "").replaceAll("\r", "");
    }

    @Override
    public void stop() throws Exception {
        rpcThread.stop();
        if(Controller.bootstrapThread != null){
            Controller.bootstrapThread.stop();
        }
        new File(Controller.directoryToLaunch + "session.lock").delete();
    }
}
