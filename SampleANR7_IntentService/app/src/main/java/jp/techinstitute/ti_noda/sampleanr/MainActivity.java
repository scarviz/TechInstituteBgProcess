package jp.techinstitute.ti_noda.sampleanr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {
	private TextView mTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("Sample ANR", "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mTextView = (TextView) findViewById(R.id.textview);

		findViewById(R.id.btnStart).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mTextView.setText("処理を開始しました");
				// Serviceを起動する
				startService(new Intent(MainActivity.this, MyIntentService.class));
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
