package sunny.com.passwordmanager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.animation.AccelerateInterpolator;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sunnys on 2015/11/10.
 */
public class BaseActivity extends Activity{
    protected File Photo_dirs;//存放照片的文件夹
    protected File Photo;//拍摄的照片
    protected void startCamera(){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Photo_dirs = new File(Environment.getExternalStorageDirectory()+"/MyCamera");
            if(!Photo_dirs.exists()){
                Photo_dirs.mkdirs();
            }
            Photo = new File(Photo_dirs+"/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+".jpg");
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(Photo));
            startActivityForResult(intent, 0);
        }else {
            Toast.makeText(this, "SD卡不存在", Toast.LENGTH_SHORT).show();
        }
    }
}
