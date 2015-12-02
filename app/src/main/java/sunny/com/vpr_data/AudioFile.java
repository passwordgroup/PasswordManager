package sunny.com.vpr_data;

import android.media.MediaRecorder;
import android.os.Environment;

import java.io.File;

public class AudioFile {

	// ��Ƶ����-��˷�
	public final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;

	// ����Ƶ��
	// 44100��Ŀǰ�ı�׼������ĳЩ�豸��Ȼ֧��22050��16000��11025
	public final static int AUDIO_SAMPLE_RATE = 16000; // 16KHz
	// ¼������ļ�
	private final static String AUDIO_PCM_FILENAME = "PcmAudio.pcm";
	private final static String AUDIO_WAV_FILENAME = "FinalAudio.wav";
	public final static String AUDIO_AMR_FILENAME = "FinalAudio.amr";

	/**
	 * �ж��Ƿ����ⲿ�洢�豸sdcard
	 * 
	 * @return true | false
	 */
	public static boolean isSdcardExit() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}

	/**
	 * ��ȡ��˷������ԭʼ��Ƶ���ļ�·��
	 * 
	 * @return
	 */
	public static String getPcmFilePath() {
		String mAudioRawPath = "";
		if (isSdcardExit()) {
			String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath();
			mAudioRawPath = fileBasePath + "/" + AUDIO_PCM_FILENAME;
		}

		return mAudioRawPath;
	}

	/**
	 * ��ȡ������WAV��ʽ��Ƶ�ļ�·��
	 * 
	 * @return
	 */
	public static String getWavFilePath() {
		String mAudioWavPath = "";
		if (isSdcardExit()) {
			String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath();
			mAudioWavPath = fileBasePath + "/" + AUDIO_WAV_FILENAME;
		}
		return mAudioWavPath;
	}

	/**
	 * ��ȡ������AMR��ʽ��Ƶ�ļ�·��
	 * 
	 * @return
	 */
	public static String getAMRFilePath() {
		String mAudioAMRPath = "";
		if (isSdcardExit()) {
			String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath();
			mAudioAMRPath = fileBasePath + "/" + AUDIO_AMR_FILENAME;
		}
		return mAudioAMRPath;
	}

	/**
	 * ��ȡ�ļ���С
	 * 
	 * @param path
	 *            ,�ļ��ľ���·��
	 * @return
	 */
	public static long getFileSize(String path) {
		File mFile = new File(path);
		if (!mFile.exists())
			return -1;
		return mFile.length();
	}

}
