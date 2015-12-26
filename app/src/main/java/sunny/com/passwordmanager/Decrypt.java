package sunny.com.passwordmanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import sunny.com.decrypt.decrypt;

public class Decrypt extends AppCompatActivity {
    private decrypt decrypt;
    private String ke;    //系统获得ke
    private String Enckeki;   //从网上下载Enckeki
    private String ensup;   //系统获得ensup
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show);
        try {
            String desup=decrypt.decrypmessage(ensup,Enckeki,ke);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String k1=null;
        String k2=null;
        TextView servicename=(TextView)findViewById(R.id.text1);
        TextView mima=(TextView)findViewById(R.id.text2);
        servicename.setText(k1);
        mima.setText(k2);
    }
}
