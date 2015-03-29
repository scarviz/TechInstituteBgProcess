package jp.techinstitute.ti_noda.sampleanr;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


public class MyIntentService extends IntentService {
	private static final String TAG = "MyIntentService";

	public MyIntentService() {
		super("MyIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "onHandleIntent");

		for (int i = 0; i < 30; i++) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			String text = (i + 1) + "秒経過";
			Log.d("Sample ANR", text);
			Notify(text);
		}
		Notify("処理が完了しました");
	}
	/**
	 * 通知を表示する
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

		int id = (int)Thread.currentThread().getId();
		NotificationManager manager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
		manager.notify(id, notification.build());
	}
}
