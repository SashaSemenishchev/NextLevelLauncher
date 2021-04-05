package millida;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import millida.utils.FileManager;
import millida.utils.OsType;

import static millida.Controller.error;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Objects;

public class SetupController {
    public static boolean showX = false;

    @FXML public Button browseButton;
    @FXML public TextField directoryLine;
    @FXML public Button runButton;

    public DirectoryChooser chooser = new DirectoryChooser();

    @FXML
    public void initialize(){
        System.out.println("setup point1");
        try {
            if(FileManager.osType.equals(OsType.WIN)){
                directoryLine.setPromptText(System.getenv("SystemDrive") + File.separator + "NextLevel");
            } else if(FileManager.osType.equals(OsType.LIN)) {
                directoryLine.setPromptText(System.getProperty("user.dir") + "/NextLevel");
            } else {
                directoryLine.setPromptText(System.getProperty("user.home") + "/NextLevel");
            }
        } catch (Exception exception) {
            error(exception);
            System.exit(-1);
        }
        System.out.println("setup point2");
        if(Main.primaryStageStatic != null){
            Main.primaryStageStatic.hide();
        }
        configureDirectoryChooser(chooser);
        System.out.println("setup point3");
        browseButton.setOnAction(event -> {
            File directory = Main.clientDirectoryChooser(chooser);
            if(directory == null) return;
            try {
                directoryLine.setText(directory.getCanonicalPath() + File.separator);
            } catch (Exception exception) {
                error(exception);
            }
        });

        if(directoryLine.getText() == null){
            directoryLine.setText(directoryLine.getPromptText());
        } else if(directoryLine.getText() != null){
            if (directoryLine.getText().trim().length() == 0){
                directoryLine.setText(directoryLine.getPromptText());
            }
        }

        directoryLine.setText(directoryLine.getText().replaceAll("\"", ""));
        System.out.println("setup point4");
        runButton.setOnAction(event -> {
           if (directoryLine.getText().contains(".nextlevel")){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Вы не можете установить в клиент в эту папку!");
                alert.setHeaderText(null);
                alert.setTitle("Ошибка");
                alert.showAndWait();
                return;
            }
            try {
                File file = new File(Main.dataFolder.getCanonicalPath() + File.separator + "data.txt");
                PrintWriter dataWriter = new PrintWriter(file);
                dataWriter.write(directoryLine.getText());
                dataWriter.close();
                if(!new File(directoryLine.getText()).mkdirs()){
                    System.out.println("Cannot create root dir");
                }
                System.out.println("setup point5");
            } catch (Exception e) {
                e.printStackTrace();
            }
            Main.setupStage.close();
            try {
                restartApplication();
            } catch (Exception exception) {
                exception.printStackTrace();
                System.out.println("setup point6");
                try{
                    if (Main.isNewBool) {
                        System.out.println("setup point7");
                        Main.primaryStageStatic.setScene(new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("login.fxml")))));
                    } else {
                        System.out.println("setup point8");
                        if (Main.loginData == null) {
                            Main.primaryStageStatic.setScene(new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("login.fxml")))));
                        } else {
                            Main.primaryStageStatic.setScene(new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("launcher.fxml"))), 951, 580));
                        }
                        System.out.println("setup point9");
                    }
                } catch (Exception exception2){
                    exception2.printStackTrace();
                }
            }

        });
    }

    public static void configureDirectoryChooser(DirectoryChooser directoryChooser) {
        // Set title for DirectoryChooser
        directoryChooser.setTitle("Выберите директорию, куда будут скачиваться данные игры");

        // Set Initial Directory
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
    }

    public void restartApplication() throws Exception {
        final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        final File currentJar = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());

        /* is it a jar file? */
        if(!currentJar.getName().endsWith(".jar"))
            return;

        /* Build command: java -jar application.jar */
        final ArrayList<String> command = new ArrayList<String>();
        command.add(javaBin);
        command.add("-jar");
        command.add(currentJar.getPath());

        final ProcessBuilder builder = new ProcessBuilder(command);
        builder.start();
        System.exit(0);
    }
}
