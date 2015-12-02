package sunny.com.photo_data;

/**
 * Created by Sunny on 2015/10/14.
 */
public class AddFace {
    private String faceid;
    private String nick;
    private String base64feature;
    private String base64faceimage;

    public void setFaceid(String faceid) {
        this.faceid = faceid;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public void setBase64feature(String base64feature) {
        this.base64feature = base64feature;
    }

    public void setBase64faceimage(String base64faceimage) {
        this.base64faceimage = base64faceimage;
    }

    public String getFaceid() {
        return faceid;
    }

    public String getNick() {
        return nick;
    }

    public String getBase64feature() {
        return base64feature;
    }

    public String getBase64faceimage() {
        return base64faceimage;
    }
}
