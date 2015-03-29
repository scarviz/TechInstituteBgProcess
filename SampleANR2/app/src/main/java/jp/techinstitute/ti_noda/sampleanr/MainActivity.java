package jp.techinstitute.ti_noda.sampleanr;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {
	private TextView mTextView;
	private Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("Sample ANR", "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mTextView = (TextView) findViewById(R.id.textview);
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				String text = (String) msg.obj;
				mTextView.setText(text);
			}
		};

		findViewById(R.id.btnStart).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mTextView.setText("処理を開始しました");

				new Thread(new Runnable() {
					@Override
					public void run() {
						for (int i = 0; i < 30; i++) {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}

							String text = (i + 1) + "秒経過";
							Log.d("Sample ANR", text);

							Message msg = mHandler.obtainMessage();
							msg.obj = text;
							mHandler.sendMessage(msg);
						}

						Message msg = mHandler.obtainMessage();
						msg.obj = "処理が完了しました";
						mHandler.sendMessage(msg);
					}
				}).start();
			}
		});
	}

	@Override
	protected void onDestroy() {
		Log.d("Sample ANR", "onDestroy");
		super.onDestroy();
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
