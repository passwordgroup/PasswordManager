package sunny.com.data;

/**
 * Created by Sunny on 2015/10/13.
 */
public class JsonResult {
    private int x;
    private int y;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "JsonResult [x=" + x + ", y=" + y + "]";
    }
}
