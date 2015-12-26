package sunny.com.passwordmanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import sunny.com.encrypt.encrypt;

public class Encrypt extends AppCompatActivity {

    private encrypt encryptsup;
    private String ke;    //从手机系统中获取的ke
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.encrypt);
        Button button=(Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nameET=(EditText)findViewById(R.id.edit1);
                EditText servicenameET=(EditText)findViewById(R.id.edit2);
                EditText mimaET=(EditText)findViewById(R.id.edit3);
                String username=nameET.getText().toString();
                String servercname=servicenameET.getText().toString();
                String mima=mimaET.getText().toString();
                String sup=username+servercname+mima;
                try {
                    encryptsup.encryptdata(sup, ke);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
