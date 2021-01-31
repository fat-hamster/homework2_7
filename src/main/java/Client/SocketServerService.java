package Client;

import Server.AuthMessage;
import Server.Message;
import Server.MyServer;
import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketServerService implements ServerService {
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private boolean isConnected = false;
    private String nick;

    @Override
    public String getNick() {
        return this.nick;
    }

    @Override
    public boolean isConnected() {
        return this.isConnected;
    }

    @Override
    public void openConnection() {
        try {
            socket = new Socket("localhost", MyServer.PORT);
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void authentication(String login, String password) throws IOException {
        AuthMessage authMessage = new AuthMessage();
        authMessage.setLogin(login);
        authMessage.setPassword(password);
        dataOutputStream.writeUTF(new Gson().toJson(authMessage));
        dataOutputStream.flush();

        authMessage = new Gson().fromJson(dataInputStream.readUTF(), AuthMessage.class);
        if(authMessage.isAuthenticated()) {
            isConnected = true;
            this.nick = authMessage.getNick();
        }
    }

    @Override
    public void closeConnection() {
        try {
            dataOutputStream.close();
            dataInputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(String message) {
        Message msg = new Message();
        msg.setNick(this.getNick());
        msg.setMessage(message);
        try {
            dataOutputStream.writeUTF(new Gson().toJson(msg));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Message readMessages() {
        if(socket.isConnected()) {
            try {
                return new Gson().fromJson(dataInputStream.readUTF(), Message.class);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }
}
