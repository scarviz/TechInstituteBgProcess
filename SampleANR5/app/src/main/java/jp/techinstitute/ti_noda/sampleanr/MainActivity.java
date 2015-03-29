package jp.techinstitute.ti_noda.sampleanr;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
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
				// AsyncTaskを継承したCountUpTaskクラスのインスタンスを生成する
				// コンストラクタにはContextとTextViewを渡す
				// ContextはActivityが継承しているので、そのままthisで渡すことも可能
				CountUpTask task = new CountUpTask(MainActivity.this, mTextView);

				// 非同期処理を実行する
				// 引数には非同期処理内のfor文のループ回数
				task.execute(20);
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
