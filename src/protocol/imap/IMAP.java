package protocol.imap;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.io.*;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import protocol.*;

public class IMAP implements ReceiveProtocol {

    protected PrintWriter out;
    protected BufferedReader in;
    protected Report report = new IMAPReport();
    String answer = "";

    public IMAP(InputStream instream, OutputStream outstream) throws IOException {
        in = new BufferedReader(new InputStreamReader(instream));
        out = new PrintWriter(outstream);
    }

    public boolean startSession() throws IOException, ProtocolException {
        answer = in.readLine();
        report.setInstance(answer);
        return report.isSuccessful();
    }

    public boolean authorization(String username, String password) throws IOException, ProtocolException {
        out.print(IMAPTags.LOGIN + " LOGIN " + username + " " + password + "\r\n");
        out.flush();
        do {
            answer = in.readLine();
            report.setInstance(answer);
        } while (!answer.contains(IMAPTags.LOGIN));
        out.print(IMAPTags.SELECT + " SELECT INBOX\r\n");
        out.flush();
        do {
            answer = in.readLine();
            report.setInstance(answer);
        } while (!(answer.contains(IMAPTags.SELECT)));
        return report.isSuccessful();
    }

    public Letter openMessage(int number) throws IOException, ProtocolException {
        Letter letter = new Letter();
        letter.setInfo(this.messageInfo(number));
        out.print(IMAPTags.FETCH_OPEN + " FETCH " + number + " BODY[TEXT]\r\n");
        out.flush();
        answer = in.readLine();
        report.setInstance(answer);
        if (answer.contains(IMAPTags.FETCH_OPEN)) {
            return letter;
        }
        StringBuilder text = new StringBuilder();
        answer = in.readLine();
        while (!answer.contains(IMAPTags.FETCH_OPEN) && !answer.equals(")")) {
            text.append(answer).append("\r\n");
            answer = in.readLine();
        }
        if (!answer.contains(IMAPTags.FETCH_OPEN)) {
            answer = in.readLine();
        }
        int n = text.indexOf("charset=") + "charset=".length();
        if (n > "charset=".length()) {
            String charset = text.substring(n, text.indexOf("\r\n", n));
            if(charset.contains(";")){
                charset=charset.substring(0,charset.indexOf(";"));
            }
            int k = text.indexOf("Content-Transfer-Encoding: base64", n) + "Content-Transfer-Encoding: base64".length();
            if (k > "Content-Transfer-Encoding: base64".length()) {
                int y=text.indexOf("\r\n\r\n",k);
                int m = text.indexOf("-", y);
                String s = text.substring(y, m);
                text.replace(k+2, m, new String(Base64.decode(s),charset));
            }
        }
        letter.setText(text.toString());
        report.setInstance(answer);
        return letter;
    }

    public int messageSize(int number) throws IOException, ProtocolException {
        int size = 0;
        out.print(IMAPTags.FETCH_SIZE + " FETCH " + number + " FAST\r\n");
        out.flush();
        answer = in.readLine();
        while (!answer.contains(IMAPTags.FETCH_SIZE)) {
            report.setInstance(answer);
            int k = answer.indexOf("SIZE");
            if (k > 0) {
                k += 1 + "SIZE".length();
                int m = answer.indexOf(")", k);
                size = Integer.parseInt(answer.substring(k, m));
            }
            answer = in.readLine();
        }
        report.setInstance(answer);
        return size;
    }

    public int mailboxSize() throws IOException, ProtocolException {
        int size = 0;
        int number = this.letterCount();
        if (number == 0) {
            return size;
        }
        out.print(IMAPTags.FETCH_MAILBOX_SIZE + " FETCH 1:" + number + " FAST\r\n");
        out.flush();
        answer = in.readLine();
        while (!answer.contains(IMAPTags.FETCH_MAILBOX_SIZE)) {
            report.setInstance(answer);
            int k = answer.indexOf("SIZE");
            if (k > 0) {
                k += 1 + "SIZE".length();
                int m = answer.indexOf(")", k);
                size += Integer.parseInt(answer.substring(k, m));
            }
            answer = in.readLine();
        }
        report.setInstance(answer);
        return size;
    }

    public LetterInfo messageInfo(int number) throws IOException, ProtocolException {
        out.print(IMAPTags.FETCH_INFO + " FETCH " + number + " BODY[HEADER.FIELDS (FROM DATE TO MESSAGE-ID SUBJECT Content-Type)]\r\n");
        out.flush();
        StringBuilder str = new StringBuilder();
        answer = in.readLine();
        report.setInstance(answer);
        while (!answer.contains(IMAPTags.FETCH_INFO)) {
            answer = in.readLine();
            str.append(answer).append("\r\n");
        }
        report.setInstance(answer);
        return this.messageInfoDisassemble(str.toString());
    }

    public int letterCount() throws IOException, ProtocolException {
        int count = 0;
        out.print(IMAPTags.STATUS + " STATUS INBOX (MESSAGES UNSEEN)\r\n");
        out.flush();
        answer = in.readLine();
        while (!answer.contains(IMAPTags.STATUS)) {
            report.setInstance(answer);
            int k = answer.indexOf("MESSAGES");
            if (k > 0) {
                k += 1 + "MESSAGES".length();
                int m = answer.indexOf(" ", k);
                String s = answer.substring(k, m);
                count = Integer.parseInt(answer.substring(k, m));
            }
            answer = in.readLine();
        }
        report.setInstance(answer);
        return count;
    }

    public boolean deleteMessage(int number) throws IOException, ProtocolException {
        out.print(IMAPTags.STORE + " STORE " + number + " +FLAGS (\\Deleted)\r\n");
        out.flush();
        answer = in.readLine();
        report.setInstance(answer);
        out.print(IMAPTags.EXPUNGE + " EXPUNGE\r\n");
        out.flush();
        do {
            answer = in.readLine();
            report.setInstance(answer);
        } while (!answer.contains(IMAPTags.EXPUNGE));
        return report.isSuccessful();
    }

    public boolean keepUpSession() throws IOException, ProtocolException {
        out.print(IMAPTags.NOOP + " NOOP\r\n");
        out.flush();
        answer = in.readLine();
        report.setInstance(answer);
        return report.isSuccessful();
    }

    public boolean closeSession() throws IOException, ProtocolException {
        out.print(IMAPTags.LOGOUT + " LOGOUT\r\n");
        out.flush();
        answer = in.readLine();
        report.setInstance(answer);
        return report.isSuccessful();
    }

    private LetterInfo messageInfoDisassemble(String str) {
        LetterInfo info = new LetterInfo();
        int k = 0, m = 0, n = 0;
        k = str.indexOf("From:");
        m = str.indexOf(">", k);
        n = str.indexOf("<", k);
        if (k >= 0 && m > k - 5) {
            info.setSender(str.substring(n + 1, m));
        }
        k = str.indexOf("To:");
        m = str.indexOf("\r\n", k);
        if (k >= 0 && m > k - 3) {
            info.setAcceptor(str.substring(k + 3, m));
        }
        k = str.indexOf("Date:");
        m = str.indexOf("\r\n", k);
        if (k >= 0 && m > k - 5) {
            info.setDate(str.substring(k + 5, m));
        }
        k = str.indexOf("Subject:");
        m = str.indexOf("\r\n", k);
        if (k >= 0 && m > k - 8) {
            info.setSubject(str.substring(k + 8, m));
        }
        k = str.indexOf("Message-Id:");
        m = str.indexOf(">", k);
        n = str.indexOf("<", k);
        if (k >= 0 && m > k - 11) {
            info.setId(str.substring(n + 1, m));
        } else {
            k = str.indexOf("Message-ID:");
            m = str.indexOf("\r\n", k);
            if (k >= 0 && m > k - 11) {
                info.setId(str.substring(k + 11, m));
            }
        }
        k = str.indexOf("Content-Type:");
        m = str.indexOf("\r\n", k);
        if (k >= 0 && m > k - 13) {
            info.setContentType(str.substring(k + 13, m));
        }
        if (info.getSubject().contains("=?")) {
            String charset = "";
            try {
                StringTokenizer tk = new StringTokenizer(info.getSubject());
                tk.nextToken("?");
                charset = tk.nextToken("?");
                tk.nextToken("?");
                info.setSubject(new String(Base64.decode(tk.nextToken("?")), charset));
            } catch (Exception ex) {
                info.setSubject("Not Impliment Charset");
            }
        }
        return info;
    }
}
