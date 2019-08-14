import 'dart:async';

import 'package:flutter/services.dart';

class FlutterSmartlink {
  static const MethodChannel _channel =
      const MethodChannel('flutter_smartlink');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
