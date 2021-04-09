package su.nextlevel.launcher.generator;

import su.nextlevel.launcher.Main;
import su.nextlevel.launcher.util.Server;

import java.io.File;

public final class SimpleCommandGenerator implements CommandGenerator {
    private static final String JAVA_COMMAND_PREFIX =
            "java -XX:+UseConcMarkSweepGC -XX:-UseAdaptiveSizePolicy -XX:+CMSParallelRemarkEnabled " +
                    "-XX:+ParallelRefProcEnabled -XX:+CMSClassUnloadingEnabled -XX:+UseCMSInitiatingOccupancyOnly";

    @Override
    public String generateLauncherCommand(String ram, String directoryToLaunch, Server server) {
        return JAVA_COMMAND_PREFIX
                + " -Xmx" + ram + " -Dfile.encoding=UTF-8 " +
                "-XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump " +
                "-Xss1M -Djava.library.path=" + directoryToLaunch + "versions" + File.separator + "nextlevel"
                + File.separator + "natives -Dminecraft.launcher.brand=java-minecraft-launcher" +
                " -Dminecraft.launcher.version=1.6.84-j" +
                " -cp " + directoryToLaunch + "libraries" + File.separator + "net" + File.separator
                + "minecraftforge" + File.separator + "forge" + File.separator + "1.16.5-36.0.58"
                + File.separator + "forge-1.16.5-36.0.58.jar\";\"" + directoryToLaunch + "libraries" + File.separator
                + "org" + File.separator + "ow2" + File.separator + "asm" + File.separator + "asm" + File.separator + "9.0"
                + File.separator + "asm-9.0.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org"
                + File.separator + "ow2" + File.separator + "asm" + File.separator + "asm-commons"
                + File.separator + "9.0" + File.separator + "asm-commons-9.0.jar\";\"" + directoryToLaunch + "libraries"
                + File.separator + "org" + File.separator + "ow2" + File.separator + "asm" + File.separator + "asm-tree"
                + File.separator + "9.0" + File.separator + "asm-tree-9.0.jar\";\"" + directoryToLaunch + "libraries"
                + File.separator + "org" + File.separator + "ow2" + File.separator + "asm" + File.separator + "asm-util"
                + File.separator + "9.0" + File.separator + "asm-util-9.0.jar\";\"" + directoryToLaunch + "libraries"
                + File.separator + "org" + File.separator + "ow2" + File.separator + "asm" + File.separator + "asm-analysis"
                + File.separator + "9.0" + File.separator + "asm-analysis-9.0.jar\";\"" + directoryToLaunch + "libraries"
                + File.separator + "cpw" + File.separator + "mods" + File.separator + "modlauncher" + File.separator + "8.0.9"
                + File.separator + "modlauncher-8.0.9.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "cpw"
                + File.separator + "mods" + File.separator + "grossjava9hacks" + File.separator + "1.3.0" + File.separator
                + "grossjava9hacks-1.3.0.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "net" + File.separator
                + "minecraftforge" + File.separator + "accesstransformers" + File.separator + "3.0.1" + File.separator
                + "accesstransformers-3.0.1.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org" + File.separator
                + "antlr" + File.separator + "antlr4-runtime" + File.separator + "4.9.1" + File.separator
                + "antlr4-runtime-4.9.1.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "net"
                + File.separator + "minecraftforge" + File.separator + "eventbus" + File.separator + "4.0.0" + File.separator
                + "eventbus-4.0.0.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "net" + File.separator
                + "minecraftforge" + File.separator + "forgespi" + File.separator + "3.2.0" + File.separator
                + "forgespi-3.2.0.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "net" + File.separator
                + "minecraftforge" + File.separator + "coremods" + File.separator + "4.0.6" + File.separator
                + "coremods-4.0.6.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "net" + File.separator
                + "minecraftforge" + File.separator + "unsafe" + File.separator + "0.2.0" + File.separator
                + "unsafe-0.2.0.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "com" + File.separator
                + "electronwill" + File.separator + "night-config" + File.separator + "core" + File.separator + "3.6.3"
                + File.separator + "core-3.6.3.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "com"
                + File.separator + "electronwill" + File.separator + "night-config" + File.separator + "toml" + File.separator
                + "3.6.3" + File.separator + "toml-3.6.3.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org"
                + File.separator + "jline" + File.separator + "jline" + File.separator + "3.12.1" + File.separator
                + "jline-3.12.1.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org" + File.separator
                + "apache" + File.separator + "maven" + File.separator + "maven-artifact" + File.separator + "3.6.3"
                + File.separator + "maven-artifact-3.6.3.jar\";\"" + directoryToLaunch + "libraries" + File.separator
                + "net" + File.separator + "jodah" + File.separator + "typetools" + File.separator + "0.8.3" + File.separator
                + "typetools-0.8.3.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org" + File.separator + "apache"
                + File.separator + "logging" + File.separator + "log4j" + File.separator + "log4j-api" + File.separator + "2.11.2"
                + File.separator + "log4j-api-2.11.2.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org"
                + File.separator + "apache" + File.separator + "logging" + File.separator + "log4j" + File.separator
                + "log4j-core" + File.separator + "2.11.2" + File.separator + "log4j-core-2.11.2.jar\";\"" + directoryToLaunch
                + "libraries" + File.separator + "net" + File.separator + "minecrell" + File.separator + "terminalconsoleappender"
                + File.separator + "1.2.0" + File.separator + "terminalconsoleappender-1.2.0.jar\";\"" + directoryToLaunch
                + "libraries" + File.separator + "net" + File.separator + "sf" + File.separator + "jopt-simple" + File.separator
                + "jopt-simple" + File.separator + "5.0.4" + File.separator + "jopt-simple-5.0.4.jar\";\"" + directoryToLaunch
                + "libraries" + File.separator + "org" + File.separator + "spongepowered" + File.separator + "mixin"
                + File.separator + "0.8.2" + File.separator + "mixin-0.8.2.jar\";\"" + directoryToLaunch + "libraries"
                + File.separator + "net" + File.separator + "minecraftforge" + File.separator + "nashorn-core-compat"
                + File.separator + "15.1.1.1" + File.separator + "nashorn-core-compat-15.1.1.1.jar\";\"" + directoryToLaunch
                + "libraries" + File.separator + "oshi-project" + File.separator + "oshi-core" + File.separator + "1.1"
                + File.separator + "oshi-core-1.1.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "net"
                + File.separator + "java" + File.separator + "dev" + File.separator + "jna" + File.separator + "jna"
                + File.separator + "4.4.0" + File.separator + "jna-4.4.0.jar\";\"" + directoryToLaunch + "libraries"
                + File.separator + "net" + File.separator + "java" + File.separator + "dev" + File.separator + "jna"
                + File.separator + "platform" + File.separator + "3.4.0" + File.separator + "platform-3.4.0.jar\";\""
                + directoryToLaunch + "libraries" + File.separator + "com" + File.separator + "ibm" + File.separator + "icu"
                + File.separator + "icu4j" + File.separator + "66.1" + File.separator + "icu4j-66.1.jar\";\"" + directoryToLaunch
                + "libraries" + File.separator + "com" + File.separator + "mojang" + File.separator + "javabridge"
                + File.separator + "1.0.22" + File.separator + "javabridge-1.0.22.jar\";\"" + directoryToLaunch + "libraries"
                + File.separator + "net" + File.separator + "sf" + File.separator + "jopt-simple" + File.separator
                + "jopt-simple" + File.separator + "5.0.3" + File.separator + "jopt-simple-5.0.3.jar\";\"" + directoryToLaunch
                + "libraries" + File.separator + "io" + File.separator + "netty" + File.separator + "netty-all" + File.separator
                + "4.1.25.Final" + File.separator + "netty-all-4.1.25.Final.jar\";\"" + directoryToLaunch + "libraries"
                + File.separator + "com" + File.separator + "google" + File.separator + "guava" + File.separator + "guava"
                + File.separator + "21.0" + File.separator + "guava-21.0.jar\";\"" + directoryToLaunch + "libraries"
                + File.separator + "org" + File.separator + "apache" + File.separator + "commons" + File.separator
                + "commons-lang3" + File.separator + "3.5" + File.separator + "commons-lang3-3.5.jar\";\""
                + directoryToLaunch + "libraries" + File.separator + "commons-io" + File.separator + "commons-io"
                + File.separator + "2.5" + File.separator + "commons-io-2.5.jar\";\"" + directoryToLaunch
                + "libraries" + File.separator + "commons-codec" + File.separator + "commons-codec" + File.separator
                + "1.10" + File.separator + "commons-codec-1.10.jar\";\"" + directoryToLaunch + "libraries"
                + File.separator + "net" + File.separator + "java" + File.separator + "jinput" + File.separator
                + "jinput" + File.separator + "2.0.5" + File.separator + "jinput-2.0.5.jar\";\"" + directoryToLaunch
                + "libraries" + File.separator + "net" + File.separator + "java" + File.separator + "jutils"
                + File.separator + "jutils" + File.separator + "1.0.0" + File.separator + "jutils-1.0.0.jar\";\""
                + directoryToLaunch + "libraries" + File.separator + "com" + File.separator + "mojang" + File.separator
                + "brigadier" + File.separator + "1.0.17" + File.separator + "brigadier-1.0.17.jar\";\"" + directoryToLaunch
                + "libraries" + File.separator + "com" + File.separator + "mojang" + File.separator + "datafixerupper"
                + File.separator + "4.0.26" + File.separator + "datafixerupper-4.0.26.jar\";\"" + directoryToLaunch
                + "libraries" + File.separator + "com" + File.separator + "google" + File.separator + "code" + File.separator
                + "gson" + File.separator + "gson" + File.separator + "2.8.0" + File.separator + "gson-2.8.0.jar\";\""
                + directoryToLaunch + "libraries" + File.separator + "org" + File.separator + "apache" + File.separator
                + "commons" + File.separator + "commons-compress" + File.separator + "1.8.1" + File.separator
                + "commons-compress-1.8.1.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org"
                + File.separator + "apache" + File.separator + "httpcomponents" + File.separator + "httpclient"
                + File.separator + "4.3.3" + File.separator + "httpclient-4.3.3.jar\";\"" + directoryToLaunch + "libraries"
                + File.separator + "commons-logging" + File.separator + "commons-logging" + File.separator + "1.1.3"
                + File.separator + "commons-logging-1.1.3.jar\";\"" + directoryToLaunch + "libraries" + File.separator
                + "org" + File.separator + "apache" + File.separator + "httpcomponents" + File.separator + "httpcore"
                + File.separator + "4.3.2" + File.separator + "httpcore-4.3.2.jar\";\"" + directoryToLaunch + "libraries"
                + File.separator + "it" + File.separator + "unimi" + File.separator + "dsi" + File.separator + "fastutil"
                + File.separator + "8.2.1" + File.separator + "fastutil-8.2.1.jar\";\"" + directoryToLaunch + "libraries"
                + File.separator + "org" + File.separator + "apache" + File.separator + "logging" + File.separator
                + "log4j" + File.separator + "log4j-api" + File.separator + "2.8.1" + File.separator
                + "log4j-api-2.8.1.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org"
                + File.separator + "apache" + File.separator + "logging" + File.separator + "log4j" + File.separator
                + "log4j-core" + File.separator + "2.8.1" + File.separator + "log4j-core-2.8.1.jar\";\"" + directoryToLaunch
                + "libraries" + File.separator + "org" + File.separator + "lwjgl" + File.separator + "lwjgl" + File.separator
                + "3.2.2" + File.separator + "lwjgl-3.2.2.jar\";\"" + directoryToLaunch + "libraries" + File.separator
                + "org" + File.separator + "lwjgl" + File.separator + "lwjgl-jemalloc" + File.separator + "3.2.2"
                + File.separator + "lwjgl-jemalloc-3.2.2.jar\";\"" + directoryToLaunch + "libraries" + File.separator
                + "org" + File.separator + "lwjgl" + File.separator + "lwjgl-openal" + File.separator + "3.2.2"
                + File.separator + "lwjgl-openal-3.2.2.jar\";\"" + directoryToLaunch + "libraries" + File.separator
                + "org" + File.separator + "lwjgl" + File.separator + "lwjgl-opengl" + File.separator + "3.2.2" + File.separator
                + "lwjgl-opengl-3.2.2.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "org" + File.separator
                + "lwjgl" + File.separator + "lwjgl-glfw" + File.separator + "3.2.2" + File.separator + "lwjgl-glfw-3.2.2.jar\";\""
                + directoryToLaunch + "libraries" + File.separator + "org" + File.separator + "lwjgl" + File.separator + "lwjgl-stb"
                + File.separator + "3.2.2" + File.separator + "lwjgl-stb-3.2.2.jar\";\"" + directoryToLaunch + "libraries"
                + File.separator + "org" + File.separator + "lwjgl" + File.separator + "lwjgl-tinyfd" + File.separator + "3.2.2"
                + File.separator + "lwjgl-tinyfd-3.2.2.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "com"
                + File.separator + "mojang" + File.separator + "text2speech" + File.separator + "1.11.3" + File.separator
                + "text2speech-1.11.3.jar\";\"" + directoryToLaunch + "libraries" + File.separator + "com"
                + File.separator + "mojang" + File.separator + "authlib" + File.separator + "2.1.28" + File.separator
                + "authlib-2.1.28.jar\";\"" + directoryToLaunch + "versions" + File.separator + "nextlevel" + File.separator
                + "nextlevel.jar cpw.mods.modlauncher.Launcher --username " + Main.loginData.getUsername()
                + " --version nextlevel --gameDir " + directoryToLaunch.substring(0, directoryToLaunch.length() - 1).intern()
                + " --assetsDir " + directoryToLaunch + "assets --assetIndex 1.16 --uuid " + Main.loginData.getUuid()
                + " --userType mojang --versionType release --width 925 --height 530 --launchTarget fmlclient --fml.forgeVersion " +
                "36.0.58 --fml.mcVersion 1.16.5 --fml.forgeGroup net.minecraftforge --fml.mcpVersion 20210115.111550 --accessToken "
                + Main.loginData.getToken();
    }
}
