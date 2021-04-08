package su.nextlevel.launcher.token;

public interface TokenCodec {
    String fromCoded(String token);

    String toCoded(String token);
}
