package protocol;

import java.util.Stack;

public class ProtocolException extends Exception {

    String lastReport = "";
    Report report;

    public ProtocolException(String message) {
        lastReport = message;
    }

    public ProtocolException(Report rep) {
        report = rep;
    }

    public String getLastResponse() {
        return report.explanation;
    }

    public Stack<String> getlog() {
        return report.getLog();
    }
}
