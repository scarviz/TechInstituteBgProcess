package jp.techinstitute.ti_noda.sampleanr;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

/**
 * AsyncTaskを継承した非同期処理をするクラス
 * AsyncTaskの<>の中は、
 * 最初(Integer)のがdoInBackgroundメソッドの引数の型、
 * 2つ目(Integer)がonProgressUpdateメソッドの引数の型、
 * 3つ目(String)がonPostExecuteメソッドの引数の型になる
 */
public class CountUpTask extends AsyncTask<Integer, Integer, String> {
	private static final String TAG = "CountUpTask";

	private Context mContext;
	private TextView mTextView;
	private ProgressDialog mDialog;

	/**
	 * コンストラクタ
	 *
	 * @param context  ActivityのContext
	 * @param textView 非同期処理の開始と終了を表示するTextView
	 */
	public CountUpTask(Context context, TextView textView) {
		mContext = context;
		mTextView = textView;
	}

	/**
	 * 非同期処理の実行前に実施する処理
	 * UIスレッド上で実行される
	 */
	@Override
	protected void onPreExecute() {
		mTextView.setText("処理を開始しました");

		// 進捗表示するためのプログレスダイアログを用意する
		mDialog = new ProgressDialog(mContext);
		// プログレスダイアログで進捗を表示する形式を設定する。STYLE_HORIZONTALは進捗バーになる
		// STYLE_SPINNERなら円でぐるぐる回っているものになる
		mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		// 進捗状況を不確定表示(処理中かどうかのみで、進捗の数値を更新しない)するかどうか
		mDialog.setIndeterminate(false);
		// 進捗値の最大値を設定する
		mDialog.setMax(100);
		// プログレスダイアログを表示する
		mDialog.show();
	}

	/**
	 * 非同期処理を実行する
	 *
	 * @param params ループ回数。UIスレッドでexecuteメソッドに渡した引数。型に「...」がついているのは可変(複数渡せる)になっている
	 * @return onPostExecuteメソッドに渡す値。非同期処理を実行した結果になる
	 */
	@Override
	protected String doInBackground(Integer... params) {
		int count = params[0];
		for (int i = 0; i < count; i++) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Log.d(TAG, (i + 1) + "秒経過");

			int progress = ((i + 1) * 100 / count);
			// 進捗を更新する
			publishProgress(progress);
		}

		return "処理が完了しました";
	}

	/**
	 * 進捗を表示する
	 * UIスレッド上で実行される
	 *
	 * @param values 進捗値
	 */
	@Override
	protected void onProgressUpdate(Integer... values) {
		// 進捗表示
		mDialog.setProgress(values[0]);
		// UIスレッド上なのでTextViewでも表示可能
		mTextView.setText(values[0] + "％経過");
	}

	/**
	 * 非同期処理の実行後に実施する処理
	 * UIスレッド上で実行される
	 *
	 * @param s 非同期処理の結果。doInBackgroundの戻り値が渡される
	 */
	@Override
	protected void onPostExecute(String s) {
		// プログレスダイアログを閉じる
		mDialog.dismiss();

		mTextView.setText(s);
	}
}

