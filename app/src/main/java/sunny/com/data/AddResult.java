package sunny.com.data;

/**
 * Created by Sunny on 2015/10/14.
 */
public class AddResult {
    private String result;
    private String message;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "AddResult{" +
                "result='" + result + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
