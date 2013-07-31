package protocol.pop3;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.io.*;
import java.util.Scanner;
import java.util.StringTokenizer;
import protocol.*;

public class POP3 implements ReceiveProtocol {

    protected PrintWriter out;
    protected BufferedReader in;
    protected Report report = new POP3Report();
    private String answer = "";

    public POP3(InputStream instream, OutputStream outstream) throws IOException, ProtocolException {
        in = new BufferedReader(new InputStreamReader(instream));
        out = new PrintWriter(outstream);
    }

    public boolean startSession() throws IOException, ProtocolException {
        answer = in.readLine();
        report.setInstance(answer);
        return report.isSuccessful();
    }

    public boolean authorization(String username, String password) throws IOException, ProtocolException {
        out.println("USER " + username);
        out.flush();
        answer = in.readLine();
        report.setInstance(answer);
        out.println("PASS " + password);
        out.flush();
        answer = in.readLine();
        report.setInstance(answer);
        return report.isSuccessful();
    }

    public Letter openMessage(int number) throws IOException, ProtocolException {
        out.println("RETR  " + number);
        out.flush();
        StringBuilder str = new StringBuilder();
        while (!(answer = in.readLine()).equals("")) {
            report.setInstance(answer);
            str.append(answer).append("\r\n");
        }
        Letter letter = new Letter(messageInfoDisassemble(str.toString()));

        str = new StringBuilder();
        while (!(answer = in.readLine()).equals(".")) {
            str.append(answer).append("\r\n");
        }
        int n = str.indexOf("charset=") + "charset=".length();
        if (n > "charset=".length()) {
            String charset = str.substring(n, str.indexOf("\r\n", n));
            if (charset.contains(";")) {
                charset = charset.substring(0, charset.indexOf(";"));
            }
            int k = str.indexOf("Content-Transfer-Encoding: base64", n) + "Content-Transfer-Encoding: base64".length();
            if (k > "Content-Transfer-Encoding: base64".length()) {
                int y = str.indexOf("\r\n\r\n", k);
                int m = str.indexOf("-", y);
                String s = str.substring(y, m);
                str.replace(k + 2, m, new String(Base64.decode(s), charset));
            }
        }
        letter.setText(str.toString());
        return letter;
    }

    public LetterInfo messageInfo(int number) throws IOException, ProtocolException {
        LetterInfo info = new LetterInfo();
        out.println("TOP  " + number + " 0");
        out.flush();
        StringBuilder str = new StringBuilder();
        while (!(answer = in.readLine()).equals(".")) {
            report.setInstance(answer);
            str.append(answer).append("\r\n");
        }
        return messageInfoDisassemble(str.toString());
    }

    public boolean deleteMessage(int number) throws IOException, ProtocolException {
        out.println("DELE " + number);
        out.flush();
        answer = in.readLine();
        report.setInstance(answer);
        return report.isSuccessful();
    }

    public int letterCount() throws IOException, ProtocolException {
        out.println("STAT");
        out.flush();
        answer = in.readLine();
        report.setInstance(answer);
        return Integer.parseInt(answer.split(" ")[1]);
    }

    public boolean keepUpSession() throws IOException, ProtocolException {
        out.println("NOOP");
        out.flush();
        answer = in.readLine();
        report.setInstance(answer);
        return report.isSuccessful();
    }

    public boolean closeSession() throws IOException, ProtocolException {
        out.println("QUIT");
        out.flush();
        answer = in.readLine();
        report.setInstance(answer);
        return report.isSuccessful();
    }

    public int messageSize(int number) throws IOException, ProtocolException {
        out.println("LIST " + number);
        out.flush();
        answer = in.readLine();
        report.setInstance(answer);
        return Integer.parseInt(answer.split(" ")[2]);
    }

    public int mailboxSize() throws IOException, ProtocolException {
        out.println("STAT");
        out.flush();
        answer = in.readLine();
        report.setInstance(answer);
        return Integer.parseInt(answer.split(" ")[2]);
    }

    private LetterInfo messageInfoDisassemble(String str) {
        LetterInfo info = new LetterInfo();
        Scanner cs = new Scanner(str);
        while (cs.hasNextLine()) {
            String nextLine = cs.nextLine();
            if (nextLine.indexOf("From:") == 0) {
                int m = nextLine.indexOf(">");
                int n = nextLine.indexOf("<");
                if (m > 0 && n > 0) {
                    info.setSender(nextLine.substring(n + 1, m));
                }
            }
            if (nextLine.indexOf("To:") == 0) {
                int m = nextLine.indexOf("To:") + "To:".length();
                info.setAcceptor(nextLine.substring(m));
            }
            if (nextLine.indexOf("Date:") == 0) {
                int m = nextLine.indexOf("Date:") + "Date:".length();
                info.setDate(nextLine.substring(m));
            }
            if (nextLine.indexOf("Subject:") == 0) {
                int m = nextLine.indexOf("Subject:") + "Subject:".length();
                info.setSubject(nextLine.substring(m));
            }
            if (nextLine.indexOf("Content-Type:") == 0) {
                int m = nextLine.indexOf("Content-Type:") + "Content-Type:".length();
                info.setContentType(nextLine.substring(m));
            }
            if (nextLine.indexOf("Message-Id:") == 0) {
                int m = nextLine.indexOf(">");
                int n = nextLine.indexOf("<");
                if (m > 0 && n > 0) {
                    info.setId(nextLine.substring(n + 1, m));
                }
            }
            if (nextLine.indexOf("Message-ID:") == 0) {
                int m = nextLine.indexOf(">");
                int n = nextLine.indexOf("<");
                if (m > 0 && n > 0) {
                    info.setId(nextLine.substring(n + 1, m));
                }
            }
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
