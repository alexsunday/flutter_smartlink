package com.iotserv.flutter.plugin.flutter_smartlink;

import android.app.Activity;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

//smartlink
import com.hiflying.smartlink.ISmartLinker;
import com.hiflying.smartlink.OnSmartLinkListener;
import com.hiflying.smartlink.SmartLinkedModule;
import com.hiflying.smartlink.v3.SnifferSmartLinker;
import com.hiflying.smartlink.v7.MulticastSmartLinker;

import java.util.HashMap;
import java.util.Map;

/** FlutterSmartlinkPlugin */
public class FlutterSmartlinkPlugin implements MethodCallHandler {
  private String ssid;
  private String bssid;
  private String password = null;
  private int timeout = 60;//miao
  private Activity activity;
  private MethodChannel channel;

  protected ISmartLinker mSmartLinker;

  public FlutterSmartlinkPlugin(Activity activity,MethodChannel channel) {
    this.activity = activity;
    this.channel = channel;
  }

  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "flutter_smartlink");
    channel.setMethodCallHandler(new FlutterSmartlinkPlugin(registrar.activity(),channel));
  }

  @Override
  public void onMethodCall(MethodCall call, final Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else if (call.method.equals("start")){
      //参数获取
      ssid = call.argument("ssid");
      bssid = call.argument("bssid");
      password = call.argument("password");
      try {
        timeout = call.argument("timeout");
      }catch (Exception e){
        e.printStackTrace();
        timeout = 30;
      }

      mSmartLinker = MulticastSmartLinker.getInstance();
      mSmartLinker.setTimeoutPeriod(timeout * 1000);
      mSmartLinker.setOnSmartLinkListener(
              new OnSmartLinkListener() {
                Map<String, String> ret = new HashMap<String, String>();
                @Override
                public void onLinked(SmartLinkedModule smartLinkedModule) {
                  ret.put("result","success");
                  ret.put("Id",smartLinkedModule.getId());
                  ret.put("Mac",smartLinkedModule.getMac());
                  ret.put("Ip",smartLinkedModule.getIp());
                  result.success(ret);
                }

                @Override
                public void onCompleted() {
                  ret.put("result","completed");
                  result.success(ret);
                }

                @Override
                public void onTimeOut() {
                  ret.put("result","timeout");
                  result.success(ret);
                }
              }
      );
      //开始 smartLink
      try {
        mSmartLinker.start(activity.getApplicationContext(), password,ssid);
      }catch (Exception e) {
        e.printStackTrace();
        Map<String, String> ret = new HashMap<String, String>();
        ret.put("result","exception");
        result.success(ret);
      }


    }else {
      result.notImplemented();
    }
  }
}
