package su.nextlevel.launcher.token;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

public class SimpleTokenCodec implements TokenCodec {
    private static final TokenCodec INSTANCE = new SimpleTokenCodec();

    private final int hash;


    public static TokenCodec getInstance() {
        return INSTANCE;
    }

    public SimpleTokenCodec() {
        int hash1;
        try {
            byte[] arr = InetAddress.getLocalHost().getAddress();
            Set<Byte> set = new HashSet<>();
            for (byte b : arr) set.add(b);
            hash1 = set.hashCode();
        } catch (UnknownHostException e) {
            hash1 = 0;
            e.printStackTrace();
            System.exit(0);
        }
        hash = hash1;
    }

    @Override
    public String fromCoded(String token) {
        String[] splited = token.split("~");
        StringBuilder stringToken = new StringBuilder();
        for (String string : splited)
            stringToken.append((char) (Integer.parseInt(string) - hash));
        return stringToken.toString();
    }

    @Override
    public String toCoded(String token) {
        StringBuilder string = new StringBuilder();
        for (char c : token.toCharArray())
            string.append(((int) c + hash)).append("~");
        string.delete(string.length() - 1, string.length() - 1);
        return string.toString();
    }
}
