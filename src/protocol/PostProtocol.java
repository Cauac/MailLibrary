package protocol;

import java.io.IOException;

public interface PostProtocol {

    public boolean startSession(String serverName) throws IOException, ProtocolException;

    public boolean authorization(String userName, String password) throws IOException, ProtocolException;

    public boolean sendMessage(Letter letter) throws IOException, ProtocolException;

    public boolean closeSession() throws IOException, ProtocolException;
}
