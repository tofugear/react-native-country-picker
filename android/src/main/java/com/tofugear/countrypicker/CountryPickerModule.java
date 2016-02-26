package com.tofugear.countrypicker;

import android.view.Gravity;
import android.app.Activity;
import android.app.DialogFragment;
import android.widget.Toast;
import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.UiThreadUtil;


import com.tofugear.countrypicker.CountryPicker;

public class CountryPickerModule extends ReactContextBaseJavaModule implements LifecycleEventListener {
    private Activity mActivity=null;
    private CountryPicker mostRecentCountryPicker;

    // note that webView.isPaused() is not Xwalk compatible, so tracking it poor-man style
    private boolean isPaused;

    public CountryPickerModule(ReactApplicationContext reactContext, Activity activity) {
        super(reactContext);
        mActivity=activity;
    }

    @Override
    public String getName() {
        return "CountryPicker";
    }

    @ReactMethod
    public void show( final Callback callback) throws Exception {
        if (this.isPaused) {
            return;
        }

        UiThreadUtil.runOnUiThread(new Runnable() {
                public void run() {
                    final CountryPicker picker = CountryPicker.newInstance("Select Country");
                    picker.setListener(new CountryPickerListener() {
                        @Override
                        public void onSelectCountry(String name, String code, String phoneCode) {
                            WritableMap response= Arguments.createMap();

                            picker.dismiss();
                            response.putString("name",name);
                            response.putString("code",code);
                            response.putString("phoneCode",phoneCode);

                            callback.invoke(response);
                          }
                    });
                    mostRecentCountryPicker = picker;
                    picker.show(mActivity.getFragmentManager(), "COUNTRY_PICKER");
                }
        });
    }

    @ReactMethod
    public void hide() throws Exception {
        if (mostRecentCountryPicker != null) {
            //mostRecentCountryPicker.cancel();
        }
    }

    @Override
    public void initialize() {
        getReactApplicationContext().addLifecycleEventListener(this);
    }


    @Override
    public void onHostPause() {
        if (mostRecentCountryPicker != null) {
            //mostRecentCountryPicker.cancel();
        }
        this.isPaused = true;
    }

    @Override
    public void onHostResume() {
        this.isPaused = false;
    }

    @Override
    public void onHostDestroy() {
        this.isPaused = true;
    }
}
