package su.nextlevel.launcher.util;

public enum LaunchStatus {
    INDEXING("Индексирование"), DOWNLOADING_MODS("Загрузка модов"),
    DOWNLOADING_CLIENT("Загрузка основных файлов"), HASHING("Хэширование"),
    STARTING("Запуск"), UNPACKING("Распаковка");
    public String status;

    LaunchStatus(String status) {
        this.status = status;
    }
}
