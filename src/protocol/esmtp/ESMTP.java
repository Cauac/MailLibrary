package protocol.esmtp;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import protocol.Letter;
import protocol.PostProtocol;
import protocol.ProtocolException;
import protocol.Report;

public class ESMTP implements PostProtocol {

    protected PrintWriter out;
    protected BufferedReader in;
    protected String authinfo;
    protected Report report = new ESMTPReport();
    private String answer = "";
    protected static ArrayList<String> typeList;

    static {
        typeList = new ArrayList<String>();
        typeList.add("LOGIN");
        typeList.add("CRAM-MD5");
    }

    public ESMTP(InputStream instream, OutputStream outstream) {
        in = new BufferedReader(new InputStreamReader(instream));
        out = new PrintWriter(outstream);
    }

    public boolean startSession(String servername) throws IOException, ProtocolException {
        out.println("EHLO " + servername);
        out.flush();
        do {
            answer = in.readLine();
            report.setInstance(answer);
        } while (!answer.contains("AUTH"));
        authinfo = answer;
        return report.isSuccessful();
    }

    private void auth(String typeAuth, String username, String pass) throws IOException, ProtocolException {
        out.println("AUTH " + typeAuth);
        out.flush();
        do {
            answer = in.readLine();
            report.setInstance(answer);
        } while (report.getCode() != ESMTPCodes.START_AUTH);
        switch (typeList.indexOf(typeAuth)) {
            case 0:
                LOGIN(username, pass);
                break;
            case 1:
                CRAM_MD5(answer, username, pass);
        }
    }

    public boolean authorization(String username, String pass) throws IOException, ProtocolException {

        for (String s : typeList) {
            if (authinfo.contains(s)) {
                auth(s, username, pass);
                do {
                    answer = in.readLine();
                    report.setInstance(answer);
                } while (report.getCode() != ESMTPCodes.SUCCESS_AUTH);
                return report.isSuccessful();
            }
        }
        return false;
    }

    public boolean closeSession() throws IOException, ProtocolException {
        out.println("QUIT");
        out.flush();
        do {
            answer = in.readLine();
            report.setInstance(answer);
        } while (report.getCode() != ESMTPCodes.CLOSE_SESSION);
        return report.isSuccessful();
    }

    public boolean sendMessage(Letter letter) throws IOException, ProtocolException {
        out.println("MAIL FROM:<" + letter.getInfo().getSender() + ">");
        out.flush();
        report.setInstance(in.readLine());
        out.println("RCPT TO:<" + letter.getInfo().getAcceptor() + ">");
        out.flush();
        report.setInstance(in.readLine());
        out.println("DATA");
        out.flush();
        report.setInstance(in.readLine());
        out.println("From:<" + letter.getInfo().getSender() + ">");
        out.println("To:<" + letter.getInfo().getAcceptor() + ">");
        out.println("Date: " + letter.getInfo().getDate());
        out.println("Subject: " + letter.getInfo().getSubject());
        out.println("Message-ID: <" + letter.getInfo().getId() + "." + letter.getInfo().getSender() + ">");
        out.println();
        out.println(letter.getText());
        out.println(".");
        out.flush();
        report.setInstance(in.readLine());
        return report.isSuccessful();
    }

    protected void LOGIN(String username, String pass) {
        out.println(Base64.encode(username.getBytes()));
        out.flush();
        out.println(Base64.encode(pass.getBytes()));
        out.flush();
    }

    protected void CRAM_MD5(String serverstr, String username, String pass) {
        try {
            String dataString = new String(Base64.decode(serverstr));
            byte[] data = dataString.getBytes();
            byte[] key = pass.getBytes();
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            if (key.length > 64) {
                key = md5.digest(key);
            }
            byte[] k_ipad = new byte[64];
            byte[] k_opad = new byte[64];
            System.arraycopy(key, 0, k_ipad, 0, key.length);
            System.arraycopy(key, 0, k_opad, 0, key.length);
            for (int i = 0; i < 64; i++) {
                k_ipad[i] ^= 0x36;
                k_opad[i] ^= 0x5c;
            }
            byte[] i_temp = new byte[k_ipad.length + data.length];
            System.arraycopy(k_ipad, 0, i_temp, 0, k_ipad.length);
            System.arraycopy(data, 0, i_temp, k_ipad.length, data.length);
            i_temp = md5.digest(i_temp);
            byte[] o_temp = new byte[k_opad.length + i_temp.length];
            System.arraycopy(k_opad, 0, o_temp, 0, k_opad.length);
            System.arraycopy(i_temp, 0, o_temp, k_opad.length, i_temp.length);
            byte[] result = md5.digest(o_temp);
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < result.length; i++) {
                hexString.append(Integer.toHexString((result[i] >>> 4) & 0x0F));
                hexString.append(Integer.toHexString(0x0F & result[i]));
            }
            out.println(Base64.encode((username + " " + hexString.toString()).getBytes()));
            out.flush();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ESMTP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
