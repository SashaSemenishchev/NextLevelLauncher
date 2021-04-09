package su.nextlevel.launcher.util;

public enum Server {
    VANILLA("Vanilla", "https://nextlevel.su/static/mods/mods.zip", "https://nextlevel.su/static/mods/server.zip"), MINIGAMES("Мини-игры", "https://nextlevel.su/static/mods/minigames.zip");
    public final String name;
    public final String modsDownloadURL;
    public final String clientDownloadURL;

    Server(String name, String modsDownloadURL, String clientDownloadURL) {
        this.name = name;
        this.modsDownloadURL = modsDownloadURL;
        this.clientDownloadURL = clientDownloadURL;
    }

    Server(String name, String clientDownloadURL) {
        this.name = name;
        this.modsDownloadURL = null;
        this.clientDownloadURL = clientDownloadURL;
    }

    public Server getByName(String name) {
        for (Server server : values()) {
            if (server.name.equalsIgnoreCase(name)) {
                return server;
            }
        }
        return VANILLA;
    }
}
