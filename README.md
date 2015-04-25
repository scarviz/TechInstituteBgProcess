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
