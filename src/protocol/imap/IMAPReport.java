package protocol.imap;

import java.util.HashMap;
import java.util.Map;
import protocol.ProtocolException;
import protocol.Report;

public class IMAPReport extends Report {

    public IMAPReport() {
    }

    public IMAPReport(String str) {
        explanation = str;
    }
    static private Map<String, String> errorMap = new HashMap<String, String>();

    static {
        errorMap.put(IMAPTags.SELECT, "Can't access mailbox");
        errorMap.put(IMAPTags.EXPUNGE, "Permission denied");
        errorMap.put(IMAPTags.STATUS, "Permission denied");
        errorMap.put(IMAPTags.LOGIN, "User name or password rejected");
        errorMap.put(IMAPTags.FETCH_OPEN, "Can't open this message");
        errorMap.put(IMAPTags.STORE, "Can't delete this message");
    }

    @Override
    public void setInstance(String str) throws ProtocolException {
        log.add(str);
        success = str.contains("OK") || str.contains("BYE");
        if (str.contains(IMAPTags.LOGIN + " OK")) {
            explanation = "Успешная аутентификация";
            return;
        }
        if (str.contains("BYE")) {
            explanation = "Соединение завершено";
            return;
        }
        if (str.contains("BAD")) {
            explanation = "Необходимо включить безопасное соединения SSL";
            throw new ProtocolException(this);
        }
        int pos = str.indexOf(" NO");
        if (pos > 0) {
            String operationCode = str.substring(0, pos);
            if (errorMap.containsKey(operationCode)) {
                explanation = errorMap.get(operationCode);
                throw new ProtocolException(this);
            }
            explanation = "Error in " + operationCode;
            throw new ProtocolException(this);
        }
    }
}
