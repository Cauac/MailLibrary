package protocol;

public class Letter {
    
    private String text;
    private LetterInfo info;
    
    
    public Letter() {
        this.info = new LetterInfo();
        this.text = "";
    }
    
    public Letter(LetterInfo info) {
        this.setInfo(info);
        this.text = "";
    }
    
    public LetterInfo getInfo() {
        return info;
    }
    
    public void setInfo(LetterInfo info) {
        this.info = info;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
}
