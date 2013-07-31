package protocol.pop3;

import protocol.ProtocolException;
import protocol.Report;

public class POP3Report extends Report {

    public POP3Report() {
    }

    public POP3Report(String str) {
        explanation = str;
    }

    @Override
    public void setInstance(String str) throws ProtocolException {
        log.add(str);
        if (str.length() <= 0) {
            return;
        }
        success = str.charAt(0) != '-';
        explanation = str.substring(str.indexOf(" ") + 1);
        if (!success) {
            ProtocolException e = new ProtocolException(this);
            throw e;
        }
    }
}
