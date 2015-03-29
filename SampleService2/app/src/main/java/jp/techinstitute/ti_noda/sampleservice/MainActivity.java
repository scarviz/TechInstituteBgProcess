package jp.techinstitute.ti_noda.sampleservice;

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


public class MainActivity extends ActionBarActivity {
	private static final String TAG = "MainActivity";
	private MyService mBoundService;
	private boolean mIsBound;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 各ボタンの押下時処理を設定する
		findViewById(R.id.btnStart).setOnClickListener(onClickListener);
		findViewById(R.id.btnStop).setOnClickListener(onClickListener);
		findViewById(R.id.btnBind).setOnClickListener(onClickListener);
		findViewById(R.id.btnUnBind).setOnClickListener(onClickListener);
	}

	/**
	 * ボタン押下時の処理
	 */
	private View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// リソースIDによって処理を分ける
			switch (v.getId()) {
				// Start Serviceボタン押下時
				case R.id.btnStart:
					// Serviceを起動する
					startService(new Intent(MainActivity.this, MyService.class));
					break;
				// Stop Serviceボタン押下時
				case R.id.btnStop:
					// Serviceを停止する
					stopService(new Intent(MainActivity.this, MyService.class));
					break;
				// Bind Serviceボタン押下時
				case R.id.btnBind:
					doBindService();
					break;
				// UnBind Serviceボタン押下時
				case R.id.btnUnBind:
					doUnbindService();
					break;
			}
		}
	};

	/**
	 * ServiceのBind処理
	 */
	private void doBindService() {
		// Serviceとの接続を確立
		bindService(new Intent(MainActivity.this, MyService.class), mConnection, BIND_AUTO_CREATE);
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
			// mBoundServiceにMyServiceのインスタンスが格納されるので、
			// mBoundServiceを使って、Serviceのメソッド呼び出しなどが出来るようになる
			mBoundService = ((MyService.MyServiceIBinder) service).getService();
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
