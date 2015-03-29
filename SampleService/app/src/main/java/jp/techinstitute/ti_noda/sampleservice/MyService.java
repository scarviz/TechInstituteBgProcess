package jp.techinstitute.ti_noda.sampleservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {
	private static final String TAG = "MyService";

	public MyService() {
	}

	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
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
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind");
		return mBinder;
	}

	/**
	 * Binder
	 */
	private final IBinder mBinder = new MyServiceIBinder();
	public class MyServiceIBinder extends Binder {
		/**
		 * Serviceインスタンスの取得
		 */
		MyService getService() {
			return MyService.this;
		}
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(TAG, "onUnbind");
		// trueを返すようにすると、startServiceで起動したServiceに対してbindServiceし、
		// unbindServiceし、その後にbindServiceを呼ぶとonRebindが呼ばれるようになる
		// 再接続時に何か処理したい場合はonRebindを呼ぶようにtrueにしておく
		return true;
	}

	@Override
	public void onRebind(Intent intent) {
		Log.d(TAG, "onRebind");
		// 再接続時に何かしたい処理をかく
	}
}
