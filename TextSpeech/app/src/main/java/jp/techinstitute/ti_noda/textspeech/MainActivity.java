package jp.techinstitute.ti_noda.textspeech;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;


public class MainActivity extends ActionBarActivity {
	private static final String TAG = "MainActivity";

	private TextView mTextView;

	private TextToSpeech mTts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mTextView = (TextView) findViewById(R.id.textview);
		findViewById(R.id.btnstart).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				speech(mTextView.getText().toString());
			}
		});
		findViewById(R.id.btnstop).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				stop();
			}
		});

		mTts = new TextToSpeech(getApplicationContext(), listener);

		// 受け取ったIntentから初期化処理を実施
		init(getIntent());
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

	/**
	 * 初期化処理
	 *
	 * @param intent
	 */
	private void init(Intent intent) {
		if (intent == null) {
			Log.d(TAG, "init / intent is null");
			return;
		}

		String action = intent.getAction();
		// テキストが送られてきた場合
		if (Intent.ACTION_SEND.equals(action)) {
			// 送られてきたテキストが格納されているもの
			Bundle extras = intent.getExtras();
			if (extras == null) {
				Log.d(TAG, "init / extras is null");
				return;
			}

			// テキストを取り出す
			CharSequence ext = extras.getCharSequence(Intent.EXTRA_TEXT);
			if (ext != null) {
				String text = ext.toString();
				// テキスト表示
				setText(text);
			}
		}
	}

	/**
	 * テキスト内容を設定する
	 *
	 * @param text
	 */
	private void setText(String text) {
		mTextView.setText(text);
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
	private void speech(String text) {
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
	private void stop() {
		Log.d(TAG, "stop");
		if (mTts != null && mTts.isSpeaking()) {
			mTts.stop();
			Log.d(TAG, "stop text to speech");
		}
	}




	/**
	 * ********************************************************
	 * ここから下はメニュー項目関連(今回の講義では使用しない)
	 * ********************************************************
	 */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
