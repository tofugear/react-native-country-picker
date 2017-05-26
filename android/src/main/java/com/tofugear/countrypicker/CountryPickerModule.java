package com.tofugear.countrypicker;

import android.app.Activity;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableNativeArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.UiThreadUtil;

import java.util.ArrayList;

public class CountryPickerModule extends ReactContextBaseJavaModule implements LifecycleEventListener {
    private CountryPicker mostRecentCountryPicker;

    // note that webView.isPaused() is not Xwalk compatible, so tracking it poor-man style
    private boolean isPaused;

    public CountryPickerModule(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addLifecycleEventListener(this);
    }

    @Override
    public String getName() {
        return "CountryPicker";
    }

    @ReactMethod
    public void show(final ReadableMap configs, final Callback callback) throws Exception {
        if (this.isPaused) {
            return;
        }
        final Activity currentActivity = getCurrentActivity();
        if (currentActivity == null) {
            return;
        }

        UiThreadUtil.runOnUiThread(new Runnable() {
            public void run() {
                ArrayList<String> countries = null;
                if (configs.hasKey("countryList")) {
                    final ReadableArray countryList = configs.getArray("countryList");
                    countries = new ArrayList<>(countryList.size());
                    for (int i = 0; i < countryList.size(); ++i) {
                        // In the format of: code|phone|name
                        String country = countryList.getString(i);
                        if (country != null) {
                          countries.add(country);
                        }
                    }
                }
                final CountryPicker picker = CountryPicker.newInstance(
                        configs.getString("title"),
                        configs.getString("searchHintText"),
                        configs.getString("preferredCountryCodes"),
                        countries);
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
                picker.show(currentActivity.getFragmentManager(), "COUNTRY_PICKER");
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
