package millida.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.util.Pair;

import java.io.File;
import java.net.URI;

public class FileManager {

    public static final String LAUNCHER_DIRECTORY = ".nextlevel";
    public static OsType osType;

    public Pair<File, Boolean> get() throws Exception {

        String fileFolder;

        System.out.println("Searching for system");

        String os = System.getProperty("os.name").toUpperCase();
        if (os.contains("WIN")) {
            fileFolder = System.getenv("APPDATA") + "\\" + LAUNCHER_DIRECTORY;
            osType = OsType.WIN;
        }
        else if (os.contains("MAC")) {
            System.out.println(System.getProperty("user.home"));
            fileFolder = new File(new URI( "file://" + System.getProperty("user.home") + "/Library/"
                    + LAUNCHER_DIRECTORY)).getCanonicalPath();
            System.out.println(fileFolder);
            osType = OsType.MAC;
        }
        else if (os.contains("NUX") || os.contains("UNIX")) {
            fileFolder = System.getProperty("user.dir") + File.separator + LAUNCHER_DIRECTORY;
            osType = OsType.LIN;
        } else {
            fileFolder = System.getenv("APPDATA") + File.separator + LAUNCHER_DIRECTORY;
        }
        System.out.println("Found OS: " + os);
        File directory = new File(fileFolder);

        boolean isNew = false;

        if (!directory.exists()) {
            System.out.println("reached");
            isNew = true;
            if(!directory.mkdirs()){
                System.out.println("Cannot create data directory");
                Alert.AlertType alertAlertType = AlertType.ERROR;
                Alert alert = new Alert(alertAlertType);
                alert.setTitle("Ошибка загрузки");
                alert.setHeaderText(null);
                alert.setContentText("Невозможно создать папку данных. Проверьте разрешения.\n" +
                        " Если не помогло, идите в %appdata% или /home/%user% и удаляете папку .nextlevel . Если ее нету, то создайте");
                alert.show();
                System.exit(1);
            }
        } else if(directory.exists() && !new File(directory.getCanonicalPath() + File.separator + "data").exists()){
            isNew = true;
        } else if(directory.exists() && new File(directory.getCanonicalPath() + File.separator + "data").exists()){
            if(!new File(directory.getCanonicalPath() + File.separator + "data" + File.separator + "token.txt").exists()){
                isNew = true;
            }
        }
        return new Pair<>(directory, isNew);
    }

    public boolean mustRunIntoSetup() throws Exception {
        File directory = get().getKey();
        File dataFile = new File(directory.getCanonicalPath() + File.separator + "data" + File.separator + "data.txt");
        if(dataFile.exists()){
            if(dataFile.isFile()){
                return dataFile.length() == 0;
            }
        } else {
            dataFile.createNewFile();
            return true;
        }
        return false;
    }
}

