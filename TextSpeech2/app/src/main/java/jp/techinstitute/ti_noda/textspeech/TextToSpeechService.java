package jp.techinstitute.ti_noda.textspeech;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class TextToSpeechService extends Service {
	private static final String TAG = "TextToSpeechService";
	private TextToSpeech mTts;

	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
		super.onCreate();

		mTts = new TextToSpeech(getApplicationContext(), listener);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand");
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();

		if (mTts != null) {
			stop();
			mTts.shutdown();
			mTts = null;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind");
		return mBinder;
	}

	/**
	 * Binder
	 */
	private final IBinder mBinder = new TextToSpeechServiceIBinder();

	public class TextToSpeechServiceIBinder extends Binder {
		/**
		 * Serviceインスタンスの取得
		 */
		TextToSpeechService getService() {
			return TextToSpeechService.this;
		}
	}

	/**
	 * TextToSpeechの初期化処理リスナー
	 */
	private TextToSpeech.OnInitListener listener = new TextToSpeech.OnInitListener() {
		@Override
		public void onInit(int status) {
			if (status == TextToSpeech.SUCCESS) {
				// 言語設定してある言語でスピーチするように設定
				if (mTts.isLanguageAvailable(Locale.getDefault()) >= TextToSpeech.LANG_AVAILABLE) {
					mTts.setLanguage(Locale.getDefault());
				}
				// もし言語設定してある言語が使用できない場合は、英語を設定する
				else if (mTts.isLanguageAvailable(Locale.US) >= TextToSpeech.LANG_AVAILABLE) {
					mTts.setLanguage(Locale.US);
				}
			}
		}
	};

	/**
	 * テキストを読み上げる
	 *
	 * @param text
	 */
	public void speech(String text) {
		Log.d(TAG, "speech");
		stop();

		if (mTts != null) {
			// 読み上げる
			// ※API21以上ならdeprecatedになっている
			mTts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
			Log.d(TAG, "start text to speech");
		}
	}

	/**
	 * 停止する
	 */
	public void stop() {
		Log.d(TAG, "stop");
		if (mTts != null && mTts.isSpeaking()) {
			mTts.stop();
			Log.d(TAG, "stop text to speech");
		}
	}
}
