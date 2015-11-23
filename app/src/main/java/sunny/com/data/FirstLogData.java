package sunny.com.data;

import javax.crypto.SecretKey;

/**
 * Created by sunnys on 2015/11/12.
 */
public class FirstLogData {
    private String uid;
    private String ke;//这里的ke是按照String存储的，但是不知道是否还能通过这个string得到原来的secret
    private byte[] k1;
    private String EncK1Ke;//由K1加密的ke

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getKe() {
        return this.ke;
    }
    public void setKe(String stringkey){
        this.ke = stringkey;
    }
    public void setKe(SecretKey ke) {
        this.ke = ke.toString();
    }

    public String  getstringK1() {
        return k1.toString();
    }
    public byte[] getbyteK1(){
        return k1;
    }
    public void setK1(byte[] k1) {
        this.k1 = k1;
    }
    public void setK1(String strignk1){
        this.k1 = strignk1.getBytes();
    }
    public String getEncK1Ke() {
        return EncK1Ke;
    }

    public void setEncK1Ke(String encK1Ke) {
        EncK1Ke = encK1Ke;
    }
    private String convert(SecretKey secretKey){
        String stringkey = null;
        return stringkey;
    }
}
