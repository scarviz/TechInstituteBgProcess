package jp.techinstitute.ti_noda.textspeech;

import android.app.ActivityManager;
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
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;


public class MainActivity extends ActionBarActivity {
	private static final String TAG = "MainActivity";

	private TextView mTextView;
	private Switch mSwService;
	private TextToSpeechService mBoundService;
	private boolean mIsBound;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mTextView = (TextView) findViewById(R.id.textview);
		mSwService = (Switch) findViewById(R.id.swService);

		// 開始ボタン押下時処理
		findViewById(R.id.btnstart).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// サービスが起動状態ならテキストを読み上げる
				if (activeService()) {
					speech(mTextView.getText().toString());
				} else {
					// サービスを起動する
					mSwService.setChecked(true);
				}
			}
		});
		// 停止ボタン押下時処理
		findViewById(R.id.btnstop).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				stop();
			}
		});

		// ServiceのStart/Stopを切り替えるスイッチ
		mSwService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
				if (isChecked) {
					StartService();
				} else {
					StopService();
				}
			}
		});

		// 受け取ったIntentから初期化処理を実施
		init(getIntent());

		// Serviceの状態
		boolean isRunning = isRunningService(TextToSpeechService.class.getName());
		mSwService.setChecked(isRunning);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// サービスが起動状態の場合
		if (activeService()) {
			// 接続を確立する
			doBindService();
		}
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "onPause");
		super.onPause();
		// 切断する
		doUnbindService();
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
	 * サービスが起動状態かどうか
	 *
	 * @return
	 */
	boolean activeService() {
		return mSwService.isChecked();
	}

	/**
	 * サービスが起動中かどうか
	 *
	 * @param className
	 * @return
	 */
	boolean isRunningService(String className) {
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceInfos = am.getRunningServices(Integer.MAX_VALUE);
		int serviceNum = serviceInfos.size();
		for (int i = 0; i < serviceNum; i++) {
			if (serviceInfos.get(i).service.getClassName().equals(className)) {
				return true;
			}
		}
		return false;
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
