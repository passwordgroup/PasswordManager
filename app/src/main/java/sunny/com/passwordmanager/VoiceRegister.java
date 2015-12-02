package sunny.com.passwordmanager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sinovoice.hcicloudsdk.api.HciCloudSys;
import com.sinovoice.hcicloudsdk.api.vpr.HciCloudVpr;
import com.sinovoice.hcicloudsdk.common.HciErrorCode;
import com.sinovoice.hcicloudsdk.common.InitParam;
import com.sinovoice.hcicloudsdk.common.vpr.VprConfig;
import com.sinovoice.hcicloudsdk.common.vpr.VprInitParam;

import sunny.com.vpr_data.AccountInfo;
import sunny.com.vpr_data.AnimUtils;
import sunny.com.vpr_data.AudioRecordUtil;
import sunny.com.vpr_data.BaseLoadingView;
import sunny.com.vpr_data.DConfig;
import sunny.com.vpr_data.ErrorCode;
import sunny.com.vpr_data.HciCloudFuncHelper;

public class VoiceRegister extends AppCompatActivity {
    private UIHandler uiHandler;//改变界面的handler
    private AccountInfo mAccountInfo;//加载用户信息的类
    private int PCM_FLAG;//录制PCM文件的标志
    private int mstate = -1;//0表示正在录音，-1表示没有录音
    private final static int CMD_RECORDFAIL = 2000;//录音失败的标志
    private final static int CMD_RECORDING_TIME = 2001;//正在录音的标志
    private UIThread uiThread;//录音的时候启动的线程，记录录音的时间
    private int recordtime;//录音的时间
    private boolean isRegistered = false;
    public static final String REGIST_VPR_KEY = "vpr_regist";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        login();

    }
    /**
     * 第一步，初始化
     */
    private TextView tv_cancle;
    private void login() {
        //加载界面
        setContentView(R.layout.voice_step_1_init);
        tv_cancle = (TextView) findViewById(R.id.tv_cancle);
        tv_cancle.setOnClickListener(cancle_listener);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                init();
            }
        },1000);
    }
    //第二个textview提示用户录音时间太短
    private TextView tv_cancle_stept2;
    private TextView voice_step2_error_txt;
    private ImageView voice_step2_tip;
    //这是一个progressbar，在上传声音的时候显示出来
    private BaseLoadingView voice_step2_loading;
    //这是在按住按钮的时候显示动画效果的view
    private RelativeLayout voice_step2_say_btn_halo;
    //这个是用户录音时需要按住的按钮
    private Button voice_step2_say_btn;
    /**
     * 第二步，录音，注册
     */
    private void addStep2Layout() {
        //加载界面
        setContentView(R.layout.voice_step_2_say);
        tv_cancle_stept2 = (TextView) findViewById(R.id.tv_cancle_stept2);
        voice_step2_error_txt = (TextView) findViewById(R.id.voice_step2_error_txt);
        voice_step2_tip = (ImageView) findViewById(R.id.voice_step2_tip);
        voice_step2_loading = (BaseLoadingView) findViewById(R.id.voice_step2_loading);
        voice_step2_say_btn_halo = (RelativeLayout) findViewById(R.id.voice_step2_say_btn_halo);
        voice_step2_say_btn = (Button) findViewById(R.id.voice_step2_say_btn);
        tv_cancle_stept2.setOnClickListener(cancle_listener);
        voice_step2_say_btn.setOnTouchListener(onTouchListener);
    }
    private TextView tv_cancle_stept3;
    private Button btn_next_step_3;

    /**
     * 第三步，next，进入第四步
     */
    private void addStep3Layout(){
        setContentView(R.layout.voice_step_3_next);
        tv_cancle_stept3 = (TextView) findViewById(R.id.tv_cancle_stept3);
        btn_next_step_3 = (Button) findViewById(R.id.btn_next_step_3);
        tv_cancle_stept3.setOnClickListener(cancle_listener);
        btn_next_step_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStep4Layout();
            }
        });
    }

    /**
     * 第五步，确认，再次录音
     */
    private void addStep4Layout(){
        setContentView(R.layout.voice_step_2_say);
        //加载界面,和第二步相同，把代码复制了一遍
        setContentView(R.layout.voice_step_2_say);
        tv_cancle_stept2 = (TextView) findViewById(R.id.tv_cancle_stept2);
        voice_step2_error_txt = (TextView) findViewById(R.id.voice_step2_error_txt);
        voice_step2_tip = (ImageView) findViewById(R.id.voice_step2_tip);
        voice_step2_loading = (BaseLoadingView) findViewById(R.id.voice_step2_loading);
        voice_step2_say_btn_halo = (RelativeLayout) findViewById(R.id.voice_step2_say_btn_halo);
        voice_step2_say_btn = (Button) findViewById(R.id.voice_step2_say_btn);
//        tv_cancle_stept2.setOnClickListener(cancle_listener);
        voice_step2_say_btn.setOnTouchListener(onTouchListener);
        //可能是觉得第二遍用户就知道该怎么操作了把，就把这个tip给省了
        voice_step2_tip.setVisibility(View.INVISIBLE);
        tv_cancle_stept2.setVisibility(View.INVISIBLE);

    }
    private void addStep5Layout(){
        Toast.makeText(this,"声音注册成功",Toast.LENGTH_SHORT).show();
        startActivity(new Intent(VoiceRegister.this,MainActivity.class));
    }
    /**
     * 初始化灵云账号，
     * 初始化能力
     */
    private void init() {
        //初始化UIhandler
        uiHandler = new UIHandler();
        initvpr();
    }

    private void initvpr() {
        mAccountInfo = AccountInfo.getInstance();
        boolean loadResult = mAccountInfo.loadAccountInfo(this);
        if (loadResult) {
        } else {
            Toast.makeText(this, "加载灵云账号失败！请在assets/AccountInfo.txt文件中填写正确的灵云账户信息，账户需要从www.hcicloud.com开发者社区上注册申请。", Toast.LENGTH_SHORT).show();
            return;
        }
        // 加载信息,返回InitParam, 获得配置参数的字符串
        InitParam initParam = HciCloudFuncHelper.getInitParam(this);
        String strConfig = initParam.getStringConfig();
        // 初始化
        int errCode = HciCloudSys.hciInit(strConfig, this);
        if (errCode != HciErrorCode.HCI_ERR_NONE && errCode != HciErrorCode.HCI_ERR_SYS_ALREADY_INIT) {
            Toast.makeText(this, "\nhciInit error: " + HciCloudSys.hciGetErrorInfo(errCode), Toast.LENGTH_SHORT).show();
            Log.i("aaaaaaaaaaaaaaaaaaaaaa", "错误码"+ errCode);
            return;
        } else {
//			ToastUtil.showToast(this, "\nhciInit error: " + HciCloudSys.hciGetErrorInfo(errCode), Toast.LENGTH_SHORT);
            Log.i("aaaaaaaaaaaaaaaaaaaaaa", "错误码"+ errCode);
        }

        // 获取授权/更新授权文件 :
        errCode = HciCloudFuncHelper.checkAuthAndUpdateAuth();
        if (errCode != HciErrorCode.HCI_ERR_NONE) {
            // 由于系统已经初始化成功,在结束前需要调用方法hciRelease()进行系统的反初始化
            HciCloudSys.hciRelease();
            return;
        }else{
            addStep2Layout();
        }
        // HciCloudFuncHelper.Func(this, mAccountInfo.getCapKey(), mLogView);
        // 初始化VPR
        // 构造VPR初始化的帮助类的实例
        VprInitParam vprInitParam = new VprInitParam();
        // 获取App应用中的lib的路径,放置能力所需资源文件。如果使用/data/data/packagename/lib目录,需要添加android_so的标记
//		String dataPath = context.getFilesDir().getAbsolutePath().replace("files", "lib");
//		initParam.addParam(VprInitParam.PARAM_KEY_DATA_PATH, dataPath);
        initParam.addParam(VprInitParam.PARAM_KEY_FILE_FLAG, VprInitParam.VALUE_OF_PARAM_FILE_FLAG_ANDROID_SO);
        initParam.addParam(VprInitParam.PARAM_KEY_INIT_CAP_KEYS, "vpr.cloud.verify");
//		ShowMessage("HciVprInit config :" + initParam.getStringConfig());
        int vprErrCode = HciCloudVpr.hciVprInit(initParam.getStringConfig());
        if (vprErrCode != HciErrorCode.HCI_ERR_NONE) {
//			ShowMessage("HciVprInit error:"	+ HciCloudSys.hciGetErrorInfo(errCode));
            Log.i("aaaaaaaaaaaaaaaaaaaa", "错误码："+vprErrCode);
            return;
        } else {
            Log.i("aaaaaaaaaaaaaaaaaaa", "HciVprInit Success");
        }
    }
    private View.OnClickListener cancle_listener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            startActivity(new Intent(VoiceRegister.this,MainActivity.class));
        }
    };
   private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
       @Override
       public boolean onTouch(View v, MotionEvent event) {
           int action = event.getAction();
           switch (action){
               case MotionEvent.ACTION_DOWN:
                   startRecord_view();
                   record(PCM_FLAG);
                   break;
               case MotionEvent.ACTION_UP:
                   stopRecord_view();
                   stopRecord();
                   if(recordtime < 1){
                       voice_step2_error_txt.setVisibility(View.VISIBLE);
                       setLoading(false);
                   }else{
                        if(isRegistered){
                            verifyVoice();
                        }else{
                            registerVoice();
                        }
                   }
                   break;
               case MotionEvent.ACTION_CANCEL:
                   break;
           }
           return false;
       }
   };
    //开始录音
    private void record(int state) {
        if(mstate != -1){
            Message msg = new Message();
            Bundle b = new Bundle();// 存放数据
            b.putInt("cmd", CMD_RECORDFAIL);
            b.putInt("msg", ErrorCode.E_STATE_RECODING);
            msg.setData(b);
            uiHandler.sendMessage(msg); // 向Handler发送消息,更新UI
            return;
        }
        int mResult = -1;
        AudioRecordUtil mRecord_1 = AudioRecordUtil.getInstance();
        mResult = mRecord_1.startRecordAndFile();
        if (mResult == ErrorCode.SUCCESS) {
            uiThread = new UIThread();
            new Thread(uiThread).start();
            mstate = state;
        } else {
            Message msg = new Message();
            Bundle b = new Bundle();// 存放数据
            b.putInt("cmd", CMD_RECORDFAIL);
            b.putInt("msg", mResult);
            msg.setData(b);
            uiHandler.sendMessage(msg); // 向Handler发送消息,更新UI
        }
    }
    //设置录音时候的界面
    private void startRecord_view() {
        voice_step2_error_txt.setVisibility(View.INVISIBLE);
        voice_step2_tip.setVisibility(View.INVISIBLE);
        voice_step2_say_btn_halo.setVisibility(View.VISIBLE);
        setLoading(false);
        AnimUtils.animVoiceBtnScale(voice_step2_say_btn_halo);
    }
    //设置停止录音时候的界面
    private void stopRecord_view() {
        //正在上传文件
        setLoading(true);
        voice_step2_say_btn_halo.setVisibility(View.INVISIBLE);
    }
    //停止录音
    private void stopRecord(){
        if (mstate != -1) {
            AudioRecordUtil mRecord_1 = AudioRecordUtil.getInstance();
            mRecord_1.stopRecordAndFile();
            if (uiThread != null) {
                uiThread.stopThread();
                uiHandler.removeCallbacks(uiThread);
            }
            mstate = -1;
        }
    }
    //上传录音文件的时候显示progressbar
    private void setLoading(boolean isuploading) {
        if(isuploading){
            voice_step2_loading.setVisibility(View.VISIBLE);
        }else {
            voice_step2_loading.setVisibility(View.INVISIBLE);
        }
    }

    class UIThread implements Runnable {
        int mTimeMill = 0;
        boolean vRun = true;

        public void stopThread() {
            vRun = false;
        }

        public void run() {
            while (vRun) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                mTimeMill++;
                Log.d("thread", "mThread........" + mTimeMill);
                Message msg = new Message();
                Bundle b = new Bundle();// 存放数据
                b.putInt("cmd", CMD_RECORDING_TIME);
                b.putInt("msg", mTimeMill);
                msg.setData(b);
                VoiceRegister.this.uiHandler.sendMessage(msg); // 向Handler发送消息,更新UI
            }

        }
    }
    private void registerVoice(){
        String capKey = mAccountInfo.getCapKey();
        final String userId = Register.nickname;
        boolean enrollResult = false;
        VprConfig enrollConfig = new VprConfig();
        enrollConfig.addParam(VprConfig.UserConfig.PARAM_KEY_USER_ID, userId);
        enrollConfig.addParam(VprConfig.AudioConfig.PARAM_KEY_AUDIO_FORMAT, VprConfig.AudioConfig.VALUE_OF_PARAM_AUDIO_FORMAT_PCM_16K16BIT);
        enrollResult = HciCloudFuncHelper.Enroll(capKey, enrollConfig);
        if (enrollResult) {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    isRegistered = true;
                    setLoading(false);
                    addStep3Layout();
                }
            });
            AudioRecordUtil.getInstance().removePcmFile();
            DConfig.Preference.setStringPref(this, REGIST_VPR_KEY, userId.toString());
        } else {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    isRegistered = false;
                    setLoading(false);
                    Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
    }
    //确认之前输入的声音
    private void verifyVoice(){
        boolean verifyResult = false;
        VprConfig verifyConfig = new VprConfig();
        verifyConfig.addParam(VprConfig.UserConfig.PARAM_KEY_USER_ID, Register.nickname);
//		HciCloudFuncHelper.Verify(mAccountInfo.getAppKey(), verifyConfig);
        verifyResult = HciCloudFuncHelper.Verify("vpr.cloud.verify", verifyConfig);
        setLoading(false);
        if (verifyResult) {
            AudioRecordUtil.getInstance().removePcmFile();
            Toast.makeText(this, "确认成功", Toast.LENGTH_SHORT).show();
            addStep5Layout();
        } else {
            AudioRecordUtil.getInstance().removePcmFile();
            Toast.makeText(this, "确认失败", Toast.LENGTH_SHORT).show();
        }
    }
    class UIHandler extends Handler {
        public UIHandler() {
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            int cmd = bundle.getInt("cmd");
            switch (cmd){
                case CMD_RECORDING_TIME:
                    recordtime = bundle.getInt("msg");
                    break;
                case CMD_RECORDFAIL:
                    //录音失败后在这里处理ui
                    break;
            }
        }
    }

}
