package protocol.esmtp;

import protocol.ProtocolException;
import protocol.Report;

public class ESMTPReport extends Report {

    @Override
    public void setInstance(String str) throws ProtocolException {
        log.add(str);
        code = Integer.parseInt(str.substring(0, 3));
        this.success = code < 400;

        switch (code) {
            case 220:
                explanation = "Service ready";
                break;
            case 221:
                explanation = "Service closing transmission channel";
                break;
            case 235:
                explanation = "Authorization completed";
                break;
            case 250:
                explanation = "Requested mail action okay, completed";
                break;
            case 334:
                explanation = "Start authorization";
                break;
            case 351:
                explanation = "Start mail input";
                break;
            case 421:
                explanation = "Service not available, closing transmission channel";
                throw new ProtocolException(this);
            case 450:
                explanation = "Requested mail action not taken: mailbox unavailable";
                throw new ProtocolException(this);
            case 451:
                explanation = "Requested action aborted: local error in processing";
                throw new ProtocolException(this);
            case 452:
                explanation = "Requested action not taken: insufficient system storage";
                throw new ProtocolException(this);
            case 500:
                explanation = "Syntax error, command unrecognized";
                throw new ProtocolException(this);
            case 501:
                explanation = "Syntax error in parameters or arguments";
                throw new ProtocolException(this);
            case 502:
                explanation = "Command not implemented";
                throw new ProtocolException(this);
            case 503:
                explanation = "Bad sequence of commands";
                throw new ProtocolException(this);
            case 504:
                explanation = "Command parameter not implemented";
                throw new ProtocolException(this);
            case 550:
                explanation = "Mailbox unavailable";
                throw new ProtocolException(this);
            case 551:
                explanation = "User not local";
                throw new ProtocolException(this);
            case 552:
                explanation = "Exceeded storage allocation";
                throw new ProtocolException(this);
            case 553:
                explanation = "Mailbox name not allowed";
                throw new ProtocolException(this);
            case 554:
                explanation = "Transaction failed";
                throw new ProtocolException(this);
        }
    }
}
