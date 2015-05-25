package jp.techinstitute.ti_noda.textspeech;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {
	private static final String TAG = "MainActivity";

	private TextView mTextView;
	private TextToSpeechService mBoundService;
	private boolean mIsBound;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mTextView = (TextView) findViewById(R.id.textview);

		// 開始ボタン押下時処理
		findViewById(R.id.btnstart).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				speech(mTextView.getText().toString());
			}
		});
		// 停止ボタン押下時処理
		findViewById(R.id.btnstop).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				stop();
			}
		});

		// 受け取ったIntentから初期化処理を実施
		init(getIntent());
	}

	@Override
	protected void onResume() {
		super.onResume();
		// サービスを起動する(既に起動中の場合はServiceのOnCreateは呼ばれず、同一インスタンスを扱うようになる)
		StartService();
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "onPause");
		super.onPause();
		// 読み上げ中でない場合
		if (mBoundService != null && !mBoundService.isSpeaking()) {
			// サービスを停止する
			StopService();
		} else {
			// 切断する
			doUnbindService();
		}
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
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
	 * テキストを読み上げる
	 *
	 * @param text
	 */
	private void speech(String text) {
		Log.d(TAG, "speech");
		if (mBoundService != null) {
			mBoundService.speech(text);
		}
	}

	/**
	 * 停止する
	 */
	private void stop() {
		Log.d(TAG, "stop");
		if (mBoundService != null) {
			mBoundService.stop();
		}
	}

	/**
	 * Serviceを開始する
	 */
	private void StartService() {
		Log.d(TAG, "StartService");
		// Serviceと接続
		Intent intent = new Intent(this, TextToSpeechService.class);
		startService(intent);
		doBindService();
	}

	/**
	 * Serviceを停止する
	 */
	private void StopService() {
		Log.d(TAG, "StopService");
		doUnbindService();

		Intent intent = new Intent(this, TextToSpeechService.class);
		stopService(intent);
	}

	/**
	 * ServiceのBind処理
	 */
	private void doBindService() {
		// Serviceとの接続を確立
		bindService(new Intent(MainActivity.this, TextToSpeechService.class), mConnection, BIND_AUTO_CREATE);
		mIsBound = true;
	}

	/**
	 * ServiceのUnbind処理
	 */
	private void doUnbindService() {
		if (mIsBound) {
			// Serviceとの接続を解除
			unbindService(mConnection);
			mIsBound = false;
		}
	}

	/**
	 * Serviceと接続するためのコネクション
	 */
	private ServiceConnection mConnection = new ServiceConnection() {
		/**
		 * Serviceと接続できた場合に呼ばれる
		 * @param name
		 * @param service
		 */
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d(TAG, "onServiceConnected");
			// TextToSpeechServiceのインスタンスを取得する
			mBoundService = ((TextToSpeechService.TextToSpeechServiceIBinder) service).getService();
		}

		/**
		 * Serviceとの接続が意図しないタイミングで切断された(異常系)場合に呼ばれる
		 * @param name
		 */
		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.d(TAG, "onServiceDisconnected");
			mBoundService = null;
		}
	};


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
