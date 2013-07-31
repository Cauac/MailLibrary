package protocol;

import java.io.IOException;

public interface ReceiveProtocol {

    public boolean startSession() throws IOException, ProtocolException;

    public boolean authorization(String username, String password) throws IOException, ProtocolException;

    public Letter openMessage(int number) throws IOException, ProtocolException;

    public int letterCount() throws IOException, ProtocolException;

    public LetterInfo messageInfo(int number) throws IOException, ProtocolException;

    public int messageSize(int number) throws IOException, ProtocolException;

    public int mailboxSize() throws IOException, ProtocolException;

    public boolean deleteMessage(int number) throws IOException, ProtocolException;

    public boolean keepUpSession() throws IOException, ProtocolException;

    public boolean closeSession() throws IOException, ProtocolException;
}
