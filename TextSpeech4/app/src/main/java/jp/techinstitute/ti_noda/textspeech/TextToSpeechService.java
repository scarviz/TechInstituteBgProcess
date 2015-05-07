package jp.techinstitute.ti_noda.textspeech;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.HashMap;
import java.util.Locale;

public class TextToSpeechService extends Service {
	private static final String TAG = "TextToSpeechService";
	private static final int NOTIFY_ID = 0;
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

		NotifyOff();
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
			// 読み上げ開始、完了、エラー時処理用リスナー用のMap
			// ここで指定した文字列の"utteranceId"が各メソッドの引数に渡る
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "utteranceId");

			// 読み上げる
			// ※API21以上ならdeprecatedになっている
			mTts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
			// 読み上げ開始、完了、エラー時処理用リスナーの設定
			setTtsListener();
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

	/**
	 * 読み上げ開始、完了、エラー時処理用リスナーを設定する
	 */
	private void setTtsListener(){
		// 読み上げ開始、完了、エラー時処理用リスナーを設定する
		mTts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
			/**
			 * 開始時処理
			 *
			 * @param utteranceId
			 */
			@Override
			public void onStart(String utteranceId) {
				Log.d(TAG, "onStart");
				Notify(getString(R.string.reading));
			}

			/**
			 * 完了時処理
			 *
			 * @param utteranceId
			 */
			@Override
			public void onDone(String utteranceId) {
				Log.d(TAG, "onDone");
				Notify(getString(R.string.complete));
			}

			/**
			 * エラー時処理
			 *
			 * @param utteranceId
			 */
			@Override
			public void onError(String utteranceId) {
				Log.d(TAG, "onError");
			}
		});
	}

	/**
	 * 通知を表示する
	 * @param text
	 */
	private void Notify(String text) {
		NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
				.setSmallIcon(R.mipmap.ic_launcher)
				.setContentTitle(getString(R.string.app_name))
				.setContentText(text)
				.setAutoCancel(true)
				// 空Intentでdismissさせるようにする
				.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(), 0))
				.setWhen(System.currentTimeMillis());

		NotificationManager manager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
		manager.notify(NOTIFY_ID, notification.build());
	}

	/**
	 *  通知を非表示にする
	 */
	private void NotifyOff() {
		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		manager.cancel(NOTIFY_ID);
	}
}
