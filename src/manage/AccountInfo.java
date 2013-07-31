package manage;

public class AccountInfo {

    protected String incomingServerName = "";
    protected String outgoingServerName = "";
    protected String username = "";
    protected String password = "";
    protected int incomingPort = 0;
    protected int outgoingPort = 0;
    protected String inProtocolType = "";
    protected String outProtocolType = "";
    protected boolean useSSL = false;
    private int currentLetterCount = 0;

    public String getInProtocolType() {
        return inProtocolType;
    }

    public void setInProtocolType(String inProtocolType) {
        this.inProtocolType = inProtocolType;
    }

    public int getIncomingPort() {
        return incomingPort;
    }

    public void setIncomingPort(int incomingPort) {
        this.incomingPort = incomingPort;
    }

    public String getIncomingServerName() {
        return incomingServerName;
    }

    public void setIncomingServerName(String incomingServerName) {
        this.incomingServerName = incomingServerName;
    }

    public String getOutProtocolType() {
        return outProtocolType;
    }

    public void setOutProtocolType(String outProtocolType) {
        this.outProtocolType = outProtocolType;
    }

    public int getOutgoingPort() {
        return outgoingPort;
    }

    public void setOutgoingPort(int outgoingPort) {
        this.outgoingPort = outgoingPort;
    }

    public String getOutgoingServerName() {
        return outgoingServerName;
    }

    public void setOutgoingServerName(String outgoingServerName) {
        this.outgoingServerName = outgoingServerName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isUseSSL() {
        return useSSL;
    }

    public void setUseSSL(boolean useSSL) {
        this.useSSL = useSSL;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getCurrentLetterCount() {
        return currentLetterCount;
    }

    public void setCurrentLetterCount(int currentLetterCount) {
        this.currentLetterCount = currentLetterCount;
    }
}