package jp.techinstitute.ti_noda.sampleservice;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class MyService extends Service {
	private static final String TAG = "MyService";
	private static final int NOTIFY_ID = 0;

	public MyService() {
	}

	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand");
		// 通知表示
		NotifyOn();
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
		// 通知非表示
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

	/**
	 * 通知を表示する
	 */
	private void NotifyOn() {
		NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
				.setSmallIcon(R.mipmap.ic_launcher)
				.setContentTitle(getString(R.string.app_name))
				.setContentText(getString(R.string.start_service))
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
