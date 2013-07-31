package protocol;

import java.util.Stack;

public abstract class Report {

    protected boolean success;  //тип результата
    protected int code;         //код ответа
    protected String explanation;  //комментарий результата
    protected Stack<String> log = new Stack<String>();

    public abstract void setInstance(String str) throws ProtocolException;

    public String getExplanation() {
        return explanation;
    }

    public int getCode() {
        return code;
    }

    public boolean isSuccessful() {
        return success;
    }

    public Stack<String> getLog() {
        return log;
    }

    @Override
    public String toString() {
        return explanation;
    }
}
