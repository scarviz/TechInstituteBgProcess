package jp.techinstitute.ti_noda.sampleanr;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

public class CountUpTaskLoader extends AsyncTaskLoader<String> {
	private static final String TAG = "CountUpTaskLoader";

	// ループ回数。引数で渡ってきたものを保持するため
	private int mCount = 0;
	// 完了メッセージ。既にこのスレッドで処理が完了していれば値が入っていることになる
	private String mCmplMessage = null;

	/**
	 * コンストラクタ
	 * @param context コンテキスト
	 * @param count ループ回数
	 */
	public CountUpTaskLoader(Context context, int count) {
		super(context);
		mCount = count;
	}

	/**
	 * 非同期処理
	 * @return
	 */
	@Override
	public String loadInBackground() {
		Log.d(TAG, "loadInBackground");

		for (int i = 0; i < mCount; i++) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Log.d("Sample ANR", (i + 1) + "秒経過");
		}

		mCmplMessage = "処理が完了しました";
		return mCmplMessage;
	}

	/**
	 * 非同期処理前に実行する処理
	 * startLoadingメソッドを呼んだ時に呼び出される
	 */
	@Override
	protected void onStartLoading(){
		Log.d(TAG, "onStartLoading");

		// 既に完了メッセージが格納されている場合は、非同期処理を完了しているので、そのまま返す
		if(mCmplMessage != null){
			deliverResult(mCmplMessage);
		}

		// Loader停止中にコンテンツが変わった、または完了メッセージが格納されていない(未処理)
		if(takeContentChanged() || mCmplMessage == null){
			// 非同期処理を開始する
			forceLoad();
		}
	}

	/**
	 * onLoadCompleteリスナーに結果を送る
	 *
	 * @param message リスナーに送る完了メッセージ
	 */
	@Override
	public void deliverResult(String message) {
		// Loaderがリセット状態かどうか
		if (isReset()) {
			return;
		}

		// 完了メッセージを格納する
		mCmplMessage = message;
		super.deliverResult(message);
	}

	/**
	 * キャンセル処理
	 * stopLoadingメソッドを呼んだ時に呼び出される
	 */
	@Override
	protected void onStopLoading(){
		Log.d(TAG, "onStopLoading");

		// 非同期処理をキャンセルする
		cancelLoad();
	}

	/**
	 * キャンセルとデータの破棄処理
	 * resetメソッドを呼んだ時に呼び出される
	 */
	@Override
	protected void onReset(){
		Log.d(TAG, "onReset");

		super.onReset();
		// キャンセル処理を実施する
		stopLoading();
		// 完了メッセージのクリア
		mCmplMessage = null;
	}
}
