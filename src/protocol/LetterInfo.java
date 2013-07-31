package protocol;

public class LetterInfo {

    private String sender;
    private String acceptor;
    private String date;
    private String subject;
    private String id;
    private String contentType;

    public LetterInfo() {
        this.sender = "";
        this.acceptor = "";
        this.date = "";
        this.subject = "";
        this.id = "";
        this.contentType = "";
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }

    public String getAcceptor() {
        return acceptor;
    }

    public String getDate() {
        return date;
    }

    public String getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public String getSubject() {
        return subject;
    }

    public void setAcceptor(String acceptor) {
        this.acceptor = acceptor;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
