import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import java.util.Scanner;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SSHClient {
    Session session;
    InputStream commandOutput;
    BufferedReader reader;
    JSch jsch;
    Channel channel;
    boolean ready;
    String username;
    String password;
    String host;
    int port = 22;
    String publicKeyPath;
    String publicKeyPassphrase;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        final String len = "=============================================================";
        SSHClient client = new SSHClient();

        System.out.println("Please enter host name:");
        String host = sc.nextLine();
        client.setHost(host);

        System.out.println("Please enter username:");
        String urs = sc.nextLine();
        client.setUsername(urs);

        System.out.println("Please enter Password:");
        String password = sc.nextLine();
        client.setPassword(password);

        try {
            boolean result = client.connect();
            if (!result) {
                System.out.println(len);
                System.out.println(stringPadding("Cannot Connect", len));
                System.out.println(len);
                return;
            } else {
                System.out.println(len);
                System.out.println(stringPadding("Connected", len));
                System.out.println(stringPadding("Welcome to the SSH I made", len));
                System.out.println(stringPadding("Hours Taken: Don't ask please", len));
                System.out.println(len);
            }

            client.channel = client.session.openChannel("shell");
            client.channel.setInputStream(System.in);
            client.channel.setOutputStream(System.out);
            client.channel.connect();

            while (true) {
                if (client.channel.isClosed()) {
                    client.channel.disconnect();
                    client.session.disconnect();
                    System.exit(1);
                    return;
                }
            }

        } catch (JSchException e) {
            System.out.println("Cannot connect");
            e.printStackTrace();
        }
        finally {
            client.channel.disconnect();
        }
    }

    static public String stringPadding(String input, String len) {
        int strLen = input.length() + 2;
        int lenLen = len.length();
        String result = "";
        if ((strLen + lenLen) % 2 == 0) {
            for (int i = 0; i < (lenLen - strLen) / 2; i++) {
                result += "=";
            }
            result += " ";
            result += input;
            result += " ";
            for (int i = 0; i < (lenLen - strLen) / 2; i++) {
                result += "=";
            }
            return result;
        }
        else {
            for (int i = 0; i < (lenLen - strLen) / 2 - 1; i++) {
                result += "=";
            }
            result += " ";
            result += input;
            result += " ";
            for (int i = 0; i < (lenLen - strLen) / 2; i++) {
                result += "=";
            }
            return result;
        }
    }

    /**
     * Connect to host with a timeout of 30 seconds and skipping host key check
     * @return true in case of success, false if it fails
     * @error JSchException
     */
    public boolean connect() throws JSchException {

        try {
            this.jsch = new JSch();
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            config.put("PreferredAuthentications", "password");


            this.session = this.jsch.getSession(this.username, this.host);
            this.session.setPassword(this.password);
            this.session.setConfig(config);
            this.session.connect(3000);
            this.ready = true;

            return true;
        } catch (Exception e) {
            this.ready = false;
        }
        return false;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setHost(String host) {
        this.host = host;
    }
}