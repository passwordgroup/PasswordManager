package sunny.com.passwordmanager;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import sunny.com.photo_data.AddFace;
import sunny.com.photo_data.AddResult;
import sunny.com.photo_data.Const;
import sunny.com.photo_data.DetectResult;
import sunny.com.photo_data.FaceModel;
import sunny.com.photo_data.FirstLogData;
import sunny.com.photo_data.PhotoDetectData;
import sunny.com.firstlog.firstlog;
import sunny.com.tools.BitmapUtil;
import sunny.com.tools.Client;
import sunny.com.tools.Sqltools;

public class Camera extends BaseActivity implements View.OnClickListener
{
    private ImageView iv_photo;//暂时存放照片的地方
    private Button btn_camera;//开始照相的按钮
    private ProgressBar pb_add;//运行添加操作时显示的progressbar
    private Bitmap bitmap;//存放拍摄照片的bitmap对象
    private String base64_img;//照片转化为base64编码
    private String s;//base64编码的json形式
    private PhotoDetectData detectData;//存放图片base64编码的类
    private Client client;//进行httppost操作的类
    private String response;//http请求返回的字符串
    private DetectResult detectResult;//整理之后的数据
    private IntentFilter intentFilter;//意图过滤器
    private AddFace addFace;//添加人脸的数据
    private String json_add;//添加人脸的字符串
    private AddResult addResult;//添加人脸的结果
    private String nickname = Register.nickname;//用户的昵称
    private EditText et_nick;//用户输入昵称的对话框
    private FirstLogData firstLogData;//第一次登陆得到的数据
    private firstlog login;//计算登陆数据用到的工具类
    private Sqltools sqltools;//向云数据库添加数据用到的工具类
    //广告接收器
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals("com.detect.end")){
                if(detectResult == null){
                    Toast.makeText(Camera.this,"网络状况不健康",Toast.LENGTH_SHORT).show();
                    pb_add.setVisibility(View.INVISIBLE);
                    btn_camera.setVisibility(View.VISIBLE);
                }else{
                    List<FaceModel> list = detectResult.getFacemodels();
                    if (!list.isEmpty()){
                        Toast.makeText(Camera.this,"检测到了人脸",Toast.LENGTH_SHORT).show();
                        handler.post(add);
                    }else{
                        Toast.makeText(Camera.this,"没有检测到人脸",Toast.LENGTH_SHORT).show();
                        pb_add.setVisibility(View.INVISIBLE);
                        btn_camera.setVisibility(View.VISIBLE);
                    }
                }
            }else if(action.equals("com.add.end")){
                Toast.makeText(Camera.this,"添加成功",Toast.LENGTH_SHORT).show();
                //开启子线程，在手机数据库上存储用户信息
                handler.post(devicesql);
                //开启子线程，在云数据库上创建以nickname命名的表
                handler.post(Alisql);
                pb_add.setVisibility(View.INVISIBLE);
                new AlertDialog.Builder(Camera.this)
                        .setTitle("是否进行声纹注册")
                        .setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Camera.this,MainActivity.class));
                            }
                        })
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Camera.this,VoiceRegister.class));
                            }
                        }).show();
            }
        }
    };
    //和线程相关的类
    private HandlerThread thread;
    private Handler handler;
    private Runnable detect = new Runnable() {
        @Override
        public void run() {
            response = client.Postmethod(Const.FaceDeteiveUrl, s);
            detectResult = JSON.parseObject(response, DetectResult.class);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //完成之后发送广播
            Intent intent = new Intent("com.detect.end");
            sendBroadcast(intent);
        }
    };
    private Runnable add = new Runnable() {
        @Override
        public void run() {
            //这里设置别名，可以设置为username
            addFace.setNick(nickname);
            addFace.setFaceid(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
            addFace.setBase64faceimage(detectData.getFaceImage());
            addFace.setBase64feature(detectResult.getFacemodels().get(0).getBase64feature());
            json_add = JSON.toJSONString(addFace);
            response = client.Postmethod(Const.FaceAdd, json_add);
            addResult = JSON.parseObject(response,AddResult.class);
            //完成之后发送广播
            Intent intent = new Intent("com.add.end");
            sendBroadcast(intent);
        }
    };
    private Runnable devicesql = new Runnable() {
        @Override
        public void run() {
            //这里面需要做的是把照片的base64编码存到云数据库中去，同时把需要算的东西算出来，
            //该存哪存哪
            login = new firstlog(base64_img);//初始化工具类
            firstLogData = login.getFirstLogData();//进行运算，并保存数据，
            //把有关用户密钥的东西存到手机中
            ContentValues values = new ContentValues();
            values.put("nickname",Register.nickname);
            values.put("uid",firstLogData.getUid());
            values.put("Ke",firstLogData.getKe());
            values.put("K1", firstLogData.getstringK1());
            values.put("EncK1Ke", firstLogData.getEncK1Ke());
            Register.secret.insert(Register.secretname, null, values);
            Toast.makeText(Camera.this,"uid"+firstLogData.getUid(), Toast.LENGTH_SHORT).show();
            Toast.makeText(Camera.this,"ke"+firstLogData.getKe(), Toast.LENGTH_SHORT).show();
            Toast.makeText(Camera.this,"k1"+firstLogData.getstringK1(), Toast.LENGTH_SHORT).show();
            Toast.makeText(Camera.this, "mp"+firstLogData.getMp(), Toast.LENGTH_SHORT).show();
            Toast.makeText(Camera.this,"手机数据库操作成功",Toast.LENGTH_SHORT).show();
        }
    };
    private Runnable Alisql = new Runnable() {
        //这个子线程中执行和数据库相关的操作
        @Override
        public void run() {
            //在这里在数据库中创建以nickname为名字的表
            sqltools = new Sqltools(base64_img);
            sqltools.InitSql();
            Toast.makeText(Camera.this,"云数据库操作成功",Toast.LENGTH_SHORT).show();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        init();
    }

    private void init() {
        //注册广播接收器
        intentFilter = new IntentFilter("com.detect.end");
        intentFilter.addAction("com.add.end");
        registerReceiver(receiver, intentFilter);
        addFace = new AddFace();
        addResult = new AddResult();
        detectData = new PhotoDetectData();
        client = new Client();
        thread = new HandlerThread("httppost");
        thread.start();
        handler = new Handler(thread.getLooper());
        et_nick = new EditText(this);
        iv_photo = (ImageView) findViewById(R.id.iv_head);
        btn_camera = (Button) findViewById(R.id.btn_photo);
        pb_add = (ProgressBar) findViewById(R.id.pb_add);
        btn_camera.setOnClickListener(this);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()){
            case R.id.btn_photo:
                startCamera();
                break;
        }
//        new AsyncTask<String ,String ,Boolean>() {
//            @Override
//            protected Boolean doInBackground(String... params) {
//                String ip = "www.baidu.com";
//                try {
//                    Process process = Runtime.getRuntime().exec("ping -c 1 -w 50 "+ip);
//                    int statue = process.waitFor();
//                    if(statue == 0){
//                        return true;
//                    }else{
//                        return false;
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Boolean aBoolean) {
//                if(aBoolean){
//                    switch (v.getId()){
//                        case R.id.btn_photo:
//                            startCamera();
//                            break;
//                    }
//                }else{
//                    Toast.makeText(Camera.this, "请检查网络连接状态", Toast.LENGTH_SHORT).show();
//                }
//                super.onPostExecute(aBoolean);
//            }
//        }.execute();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0){
            bitmap = BitmapUtil.saveBitmap(Photo.getAbsolutePath(), Photo);
        }
        if(bitmap != null){
            btn_camera.setVisibility(View.INVISIBLE);
            pb_add.setVisibility(View.VISIBLE);
            iv_photo.setImageBitmap(bitmap);
            base64_img = BitmapUtil.bitmaptoString(bitmap);
            detectData.setFaceImage(base64_img);
            s = JSON.toJSONString(detectData);
            handler.post(detect);
        }
    }

    @Override
    public void finish() {
        unregisterReceiver(receiver);
        sqltools.clear();
        handler.removeCallbacks(add);
        handler.removeCallbacks(devicesql);
        handler.removeCallbacks(detect);
        handler.removeCallbacks(Alisql);
        super.finish();
    }
}
