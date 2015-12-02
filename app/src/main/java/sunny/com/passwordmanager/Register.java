package sunny.com.passwordmanager;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import sunny.com.tools.SQLiteHelper;

public class Register extends AppCompatActivity implements View.OnClickListener{
    private EditText et_username;
    private Button btn_next;
    public static String nickname;//用户自己取的名字
    public static String secretname = "secret";//存放用户密钥的表的名字
    public static String DataBaseuserdata = "userdata";//存放用户信息的数据库名称
    public static String Tablesecret = "secret";//存放用户密钥信息的表名
    private HandlerThread thread;
    private Handler handler;
    private SQLiteHelper sqLiteHelper;//数据库帮助类
    private SQLiteDatabase SUP;//存放用户SUP的数据库
    public static SQLiteDatabase secret;//存放用户密钥信息的数据库
    private Runnable database = new Runnable() {
        @Override
        public void run() {
            sqLiteHelper = new SQLiteHelper(Register.this,DataBaseuserdata,null,1);
            SUP = sqLiteHelper.getWritableDatabase();
            secret = sqLiteHelper.getWritableDatabase();
            SUP.execSQL("Create Table if not exists " + nickname + " (Service Varchar,User Varchar," +
                    " Password Varchar);");
            secret.execSQL("Create Table if not exists " + secretname + " (nickname Varchar,uid Varchar,Ke Varchar," +
                    " K1 Varchar,EncK1Ke Varchar);");
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }

    private void init() {
        et_username = (EditText) findViewById(R.id.et_username);
        btn_next = (Button) findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);
        thread = new HandlerThread("Register");
        thread.start();
        handler = new Handler(thread.getLooper());
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_next:
                if(TextUtils.isEmpty(et_username.getText())){
                    Toast.makeText(Register.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                }else {
                    nickname = et_username.getText().toString().trim();
                    handler.post(database);
                    startActivity(new Intent(Register.this, Camera.class));
                    break;
                }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(database);
    }
}
