# TechInstituteBgProcess
2015/03/29 TechInstitute大阪での第10回(第12章)の講義内容ソースになります。

### 各フォルダ内容
* SampleANR  
ANRが発生するサンプルプログラム。

* SampleANR2  
Threadを使用したもの。

* SampleANR3  
UIスレッドでの処理をHandler.postで実施したもの。

* SampleANR4  
UIスレッドでの処理をrunOnUiThreadで実施したもの。

* SampleANR5  
AsyncTaskを使用したもの。

* SampleANR6  
AsyncTaskLoaderを使用したもの。

* SampleANR7_IntentService  
IntentServiceを使用したもの。

* SampleService  
Serviceのサンプルプログラム。

* SampleService2  
Serviceのサンプルプログラム。
通知表示するように追加で実装。

* TextSpeech  
読み上げアプリ。

* TextSpeech2  
読み上げアプリをService化したもの。  
Activityから読み上げ処理をServiceに移動。  
バインドしてServiceの読み上げ開始メソッドや停止メソッドを呼べるようにした。  

* TextSpeech3  
読み上げアプリをService化したもの。  
TextSpeech2ではActivityがフォアグラウンド状態でないと停止してしまう。  
Serviceを永続化するにはStartServiceでServiceを起動する必要がある。  
そのため、Switchを使って、StartService、StopServiceを呼び出すようにした。(MainActivity.java p.58)  
```java
// ServiceのStart/Stopを切り替えるスイッチ
mSwService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
	@Override
	public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
		if (isChecked) {
			StartService();
		} else {
			StopService();
		}
	}
});
```
ActivityManagerからServiceが起動しているか確認したり、Switchの状態でServiceが起動しているか確認したりしているところもポイント。  

* TextSpeech4  
読み上げアプリをService化したもの。  
TextSpeech3に、読み上げ開始時、読み上げ完了時にNotification(通知)を表示するように追加実装した。  
また、Service停止時には通知を非表示にする。  
読み上げ開始時、読み上げ完了時に処理するためにリスナーを使っているところがポイント。(TextToSpeechService.java p.93)
```java
public void speech(String text) {
    Log.d(TAG, "speech");
    stop();

    if (mTts != null) {
        // 読み上げ開始、完了、エラー時処理用リスナー用のMap
        // ここで指定した文字列の"utteranceId"が各メソッドの引数に渡る
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "utteranceId");

        // 読み上げる
        // ※API21以上ならdeprecatedになっている
        mTts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
        // 読み上げ開始、完了、エラー時処理用リスナーの設定
        setTtsListener();
        Log.d(TAG, "start text to speech");
    }
}
```

* TextSpeech5  
読み上げアプリをService化したもの。  
Switchを使ってServiceの起動、停止をするのは使い勝手が悪い。  
TextSpeech4で読み上げ完了時に処理できるようになったので、読み上げ完了時にstopSelfメソッドを呼び、Serviceを停止するようにした。  
stopSelfメソッドはstopServiceを呼んだ時の動作と同じ動作をする。そのため、バインドしているActivityが存在する場合、即座には停止しない。アンバインドされた後に停止処理が走るようになる。  
参照:
[stopSelf](http://developer.android.com/reference/android/app/Service.html#stopSelf%28%29) ,
[stopService](http://developer.android.com/reference/android/content/Context.html#stopService%28android.content.Intent%29)  
(TextToSpeechService.java p.153)  
```java
@Override
public void onDone(String utteranceId) {
    Log.d(TAG, "onDone");
    Notify(getString(R.string.complete));

    // 読み上げが完了時にService(自分自身)を停止する
    // stopServiceを呼んだ時の動作と同じ動作(※)をする
    // ※バインドしているActivityが存在する場合、即座に停止しない
    //   アンバインドされた後に停止処理が走るようになる
    stopSelf();
}
```
Serviceに読み上げ中かどうかを取得するメソッドを用意し、もし読み上げ中でなければ、ActivityのonPauseでもServiceを停止するようにした。  
(TextToSpeechService.java p.127)  
```java
public boolean isSpeaking(){
    return (mTts != null && mTts.isSpeaking());
}
```
(MainActivity.java p.56)  
```java
@Override
protected void onPause() {
    Log.d(TAG, "onPause");
    super.onPause();
    // 読み上げ中でない場合
    if (mBoundService != null && !mBoundService.isSpeaking()) {
        // サービスを停止する
        StopService();
    } else {
        // 切断する
        doUnbindService();
    }
}
```
また、起動はActivityのonResumeでするようにした。既にServiceが起動中の場合は、ServiceのonCreateは呼ばれず、バインドは同一インスタンスに対してバインドされる。  

* TextSpeech6  
読み上げアプリをService化したもの。  
TextSpeech5では、Activityがフォアグラウンド状態で、読み上げが完了、または、読み上げ中に停止ボタンで停止すると、stopSelfが呼ばれる。  
その後に、再度読み上げを開始し、読み上げ中にActivityをフォアグラウンドからバックグラウンドに移動させる(onPauseが呼ばれる)と、Serviceが停止してしまう。  
そのため、stopSelfでServiceを停止するのではなく、読み上げ完了時にコールバックでActivity側でStopServiceを呼ぶようにする。  
この場合、読み上げボタン押下時に再バインドする必要がある。Service開始からバインドするまでには少し時間がかかるため、別スレッドにし、バインドするまでループして待つようにする。  
また、連続で押されないように、スレッド実行前にボタンのEnableをfalseにし、再バインド後にEnableをtrueにする処理を入れる。  
(MainActivity.java p.124)  
```java
private void speech(String text) {
	Log.d(TAG, "speech");

	if(!mIsBound){
		// 連続押しされないように無効にする
		mBtnStart.setEnabled(false);
		// Serviceを開始する
		StartService();

		final String fText = text;
		new Thread(new Runnable() {
			@Override
			public void run() {
				// バインドされるまで待つ
				while (!mIsBound){
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				// バインドされた後は有効に戻す
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mBtnStart.setEnabled(true);
					}
				});
				// 読み上げを開始する
				speech(fText);
			}
		}).start();
	}
	else if (mBoundService != null) {
		mBoundService.speech(text,new TextToSpeechService.Callback(){
			@Override
			public void onFinished() {
				// 読み上げ完了時にServiceを停止する
				StopService();
			}
		});
	}
}
```
mIsBoundの値設定もバインド成功時(onServiceConnected)に変更している。  
(MainActivity.java p.233)  
```java
@Override
public void onServiceConnected(ComponentName name, IBinder service) {
	Log.d(TAG, "onServiceConnected");
	// TextToSpeechServiceのインスタンスを取得する
	mBoundService = ((TextToSpeechService.TextToSpeechServiceIBinder) service).getService();
	mIsBound = true;
}
```