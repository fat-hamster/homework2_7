package Client;

import Server.Message;

import java.io.IOException;

public interface ServerService {
    boolean isConnected();
    String getNick();

    void openConnection();
    void authentication(String login, String password) throws IOException;
    void closeConnection();

    void sendMessage(String message);
    Message readMessages();
}
