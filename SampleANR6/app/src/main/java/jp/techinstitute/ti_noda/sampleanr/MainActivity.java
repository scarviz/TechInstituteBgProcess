package jp.techinstitute.ti_noda.sampleanr;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {
	private TextView mTextView;

	// ループ回数用のキー値
	private static final String KEY_COUNTER = "Counter";
	// AsyncTaskLoaderのID
	private static final int ID = 0;

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

				// 0番(ID)のLoaderを取得する
				Loader<String> loader = getLoaderManager().getLoader(ID);
				// 既に存在する場合
				if(loader != null) {
					// 一旦リセットする
					loader.reset();
					// 再スタート
					loader.startLoading();
				} else {
					// Loaderの初期化
					initAsyncTaskLoader();
				}
			}
		});
	}

	/**
	 * AsyncTaskLoaderの初期化処理
	 */
	private void initAsyncTaskLoader(){
		Log.d("Sample ANR", "initAsyncTaskLoader");

		Bundle args = new Bundle();
		args.putInt(KEY_COUNTER, 20);

		// Loaderを初期化する
		// IDとして0番、
		// ループ回数(AsyncTaskLoaderのコンストラクタの引数で渡す)を格納したBundle、
		// Loaderのコールバック処理を渡す
		getLoaderManager().initLoader(ID, args, loaderCallbacks);
	}

	@Override
	protected void onDestroy() {
		Log.d("Sample ANR", "onDestroy");
		super.onDestroy();
	}

	/**
	 * Loaderのコールバック処理
	 */
	LoaderManager.LoaderCallbacks<String> loaderCallbacks = new LoaderManager.LoaderCallbacks<String>() {
		/**
		 * 初期化処理
		 * CountUpTaskLoaderのインスタンスを生成する
		 * LoaderManagerのinitLoaderメソッドを呼んだ時に呼び出される
		 * @param id AsyncTaskLoaderのID
		 * @param args AsyncTaskLoaderのコンストラクタの引数で渡す用の値を格納したもの
		 * @return
		 */
		@Override
		public Loader<String> onCreateLoader(int id, Bundle args) {
			if(args == null) {
				return null;
			}

			// 格納したループ回数を取り出す
			Integer count = args.getInt(KEY_COUNTER);
			// CountUpTaskLoaderを実行する
			return new CountUpTaskLoader(MainActivity.this, count);
		}

		/**
		 * 非同期処理完了後に呼び出される
		 * @param loader Loader
		 * @param data 完了時に渡される結果の値(CountUpTaskLoaderでは完了メッセージが渡ってくる)
		 */
		@Override
		public void onLoadFinished(Loader<String> loader, String data) {
			mTextView.setText(data);
		}

		/**
		 * リセット時処理
		 * @param loader
		 */
		@Override
		public void onLoaderReset(Loader<String> loader) {
			// リセット時の処理をかく
			// 今回はなし
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
