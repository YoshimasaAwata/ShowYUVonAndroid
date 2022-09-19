# ShowYUVonAndroid

AndroidでYUVファイルを動画表示するためのサンプルコードです。

最初はViewに描画していますが、最終的にはGLSurfaceView上にOpenGL ESを使用して表示します。

YUV⇒RGB変換については最初はJavaで記述しますが、最終的にはGLSLを用いてGPU側で行うようにします。

IDEはAndroid Studioを使用しています。

なおYUVのファイルは[Arizona State Universityのページ](http://trace.eas.asu.edu/yuv/)のCIFファイルを使用してください。

詳細な説明は YUV⇒RGB変換(Android編) を参照してください。

tagは以下の章に対応しています。

- とりあえずアプリケーションを作成:タイマー間隔の指定
- SurfaceViewを使用したYUVデータの動画表示:TimerTaskでShowYUVViewの描画メソッドのコール
- OpenGL ES 2.0を使用したYUVデータの動画表示:onDrawFrameメソッドの修正
- OpenGL ES 2.0を使用したYUVデータの動画表示2:setNextTextureメソッドのマルチテクスチャ対応
