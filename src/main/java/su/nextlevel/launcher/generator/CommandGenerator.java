package su.nextlevel.launcher.generator;

import su.nextlevel.launcher.util.Server;

public interface CommandGenerator {
    String generateLauncherCommand(String ram, String directoryToLaunch, Server server);
}
