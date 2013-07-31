package manage;

import java.io.IOException;
import java.net.Socket;
import javax.net.ssl.SSLSocketFactory;
import protocol.*;
import protocol.esmtp.ESMTP;
import protocol.imap.IMAP;
import protocol.pop3.POP3;

public class EmailManager {

    private AccountInfo info;

    public EmailManager(AccountInfo acInfo) {
        this.info = acInfo;
    }

    public AccountInfo getInfo() {
        return info;
    }

    public void setInfo(AccountInfo info) {
        this.info = info;
    }

    public int getMailsCount() throws IOException, ProtocolException {
        int count = 0;
        Socket s = this.getIncommingSocket();
        ReceiveProtocol protocol = this.getIncommingProtocol(s);

        protocol.startSession();
        protocol.authorization(info.getUsername(), info.getPassword());
        count = protocol.letterCount();
        protocol.closeSession();
        s.close();
        return count;
    }

    public Letter getLetter(int number) throws IOException, ProtocolException {
        Letter l;
        Socket s = this.getIncommingSocket();
        ReceiveProtocol protocol = this.getIncommingProtocol(s);

        protocol.startSession();
        protocol.authorization(info.getUsername(), info.getPassword());
        l = protocol.openMessage(number);
        protocol.closeSession();
        s.close();
        return l;
    }

    public LetterInfo[] getMailsInfoList() throws IOException, ProtocolException {
        LetterInfo[] letterInfo;
        Socket s = this.getIncommingSocket();
        ReceiveProtocol protocol = this.getIncommingProtocol(s);

        protocol.startSession();
        protocol.authorization(info.getUsername(), info.getPassword());
        int letterCount = protocol.letterCount();
        letterInfo = new LetterInfo[letterCount];
        for (int i = 0; i < letterCount; i++) {
            letterInfo[i] = protocol.messageInfo(letterCount - i);
        }
        protocol.closeSession();
        s.close();
        return letterInfo;
    }

    public boolean deleteMessage(int number) throws IOException, ProtocolException {
        boolean result = false;
        Socket s = this.getIncommingSocket();
        ReceiveProtocol protocol = this.getIncommingProtocol(s);

        protocol.startSession();
        protocol.authorization(info.getUsername(), info.getPassword());
        result = protocol.deleteMessage(number);
        protocol.closeSession();
        s.close();
        return result;
    }

    public boolean sendMessage(Letter l) throws IOException, ProtocolException {
        boolean result = false;
        Socket s = this.getOutGoingSocket();
        PostProtocol protocol = this.getOutGoingProtocol(s);

        protocol.startSession(info.getOutgoingServerName());
        protocol.authorization(info.getUsername(), info.getPassword());
        result = protocol.sendMessage(l);
        protocol.closeSession();
        s.close();
        return result;
    }

    private Socket getIncommingSocket() throws IOException {
        Socket s;
        if (info.isUseSSL()) {
            SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            s = sslsocketfactory.createSocket(info.getIncomingServerName(), info.getIncomingPort());
        } else {
            s = new Socket(info.getIncomingServerName(), info.getIncomingPort());
        }
        return s;
    }

    private Socket getOutGoingSocket() throws IOException {
        Socket s;
        if (info.isUseSSL()) {
            SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            s = sslsocketfactory.createSocket(info.getOutgoingServerName(), info.getOutgoingPort());
        } else {
            s = new Socket(info.getOutgoingServerName(), info.getOutgoingPort());
        }
        return s;
    }

    private ReceiveProtocol getIncommingProtocol(Socket s) throws IOException, ProtocolException {
        ReceiveProtocol protocol;
        if (info.getInProtocolType().equals("POP3")) {
            protocol = new POP3(s.getInputStream(), s.getOutputStream());
        } else {
            protocol = new IMAP(s.getInputStream(), s.getOutputStream());
        }
        return protocol;
    }

    private PostProtocol getOutGoingProtocol(Socket s) throws IOException {
        return new ESMTP(s.getInputStream(), s.getOutputStream());
    }
}