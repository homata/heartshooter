Heartshooter
----------------

2015/10/24(土)〜10/25(日)に行われた、MashupAwards主催で行われた「DESIGNERS’ ハッカソン」で、結成されたチーム「HEART SHOOTER」で@homataが開発したAndroidデバイスとAndroidWearのソースコードです。
開発途中の為、完全に動作しません。

* [Mashup Awards](http://mashupaward.jp/)
* [直感を信じろ！五感に訴える作品を作れ‼︎ DESIGNERS’ ハッカソン #MA11 @TDW](http://mashupaward.jp/designers-hackathon-2015/)
* [HEART SHOOTER〜あなたのハートに〜](http://hacklog.jp/works/5644)

### 開発端末

* Android端末 4.1以上
* [Samsung Gear Live]( http://www.samsung.com/global/microsite/gear/gearlive_design.html)

### 機能
Android Wareの心拍センサーの心拍数を、Android端末経由でMilkcocoaのサーバに送信するだけです。


### サーバ開発情報
- [Milkcocoa](https://mlkcca.com/)
- [Milkcocoa SDK for Android](https://github.com/milk-cocoa/milkcocoa_for_android)

### Android Wear 参考情報
- [Enable an IoT wearable app with Android Gear Live and Kii (Part 1)](http://en.kii.com/blog/enable-an-iot-wearable-app-with-android-gear-live-and-kii-part-1/)
- [How to access heart rate sensor in Android Wearable?](http://stackoverflow.com/questions/26489281/how-to-access-heart-rate-sensor-in-android-wearable)
- [Android Wear: Heart Rate and Samsung Gear Live. (basic example)](https://gist.github.com/gabrielemariotti/d23bfe583e900a4f9276)
- [Android Wearからスマホ本体側にsendMessageでメッセージを送る](http://shokai.org/blog/archives/9787)

### Android Wear デバック方法

* [[Android] Android Wear簡単なウェアラブル アプリを作る](https://akira-watson.com/android/android-wear.html)

Bluetooth経由のデバッグ方法
    Android Wearアプリ経由でおこなう

    $ adb forward tcp:4444 localabstract:/adb-hub
    $ adb connect localhost:4444
    $ adb devices

* [Android Wearアプリ](https://play.google.com/store/apps/details?id=com.google.android.wearable.app&hl=ja)
