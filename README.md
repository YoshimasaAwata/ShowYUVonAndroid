# ShowYUVonAndroid

AndroidでYUVファイルを動画表示するためのサンプルコードです。

最初はViewに描画していますが、最終的にはGLSurfaceView上にOpenGL ESを使用して表示します。

YUV⇒RGB変換については最初はJavaで記述しますが、最終的にはGLSLを用いてGPU側で行うようにします。

IDEはAndroid Studioを使用しています。

なおYUVのファイルは[Arizona State Universityのページ](http://trace.eas.asu.edu/yuv/)のCIFファイルを使用してください。

詳細な説明は YUV⇒RGB変換(Android編) を参照してください。

tagは以下の章に対応しています。

- タイマー間隔の指定

