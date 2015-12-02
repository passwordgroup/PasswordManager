package sunny.com.photo_data;

/**
 * Created by Sunny on 2015/10/14.
 */
public class Similar {
    private float similar;
    private String faceid;
    private String appkey;
    private String nick;
    private String createtime;
    private String base64feature;
    private String base64faceimage;

    @Override
    public String toString() {
        return "similarpersonface{" +
                "similar=" + similar +
                ", faceid='" + faceid + '\'' +
                ", appkey='" + appkey + '\'' +
                ", nick='" + nick + '\'' +
                ", createtime='" + createtime + '\'' +
                ", base64feature='" + base64feature + '\'' +
                ", base64faceimage='" + base64faceimage + '\'' +
                '}';
    }

    public float getSimilar() {
        return similar;
    }

    public void setSimilar(float similar) {
        this.similar = similar;
    }

    public String getFaceid() {
        return faceid;
    }

    public void setFaceid(String faceid) {
        this.faceid = faceid;
    }

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getBase64feature() {
        return base64feature;
    }

    public void setBase64feature(String base64feature) {
        this.base64feature = base64feature;
    }

    public String getBase64faceimage() {
        return base64faceimage;
    }

    public void setBase64faceimage(String base64faceimage) {
        this.base64faceimage = base64faceimage;
    }
}
