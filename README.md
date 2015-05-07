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
