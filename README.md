# React Native Country Picker
A country picker for react-native support for android

## Installation
```sh
npm install react-native-country-picker --save
```

### Installation (Android)
```gradle
...
include ':react-native-country-picker'
project(':react-native-country-picker').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-country-picker/android')
```

* In `android/app/build.gradle`

```gradle
...
dependencies {
    ...
    compile project(':react-native-country-picker')
}
```

* register module (in MainActivity.java)

```java
import com.tofugear.countrypicker.*;  // <--- import

public class MainActivity extends Activity implements DefaultHardwareBackBtnHandler {
  ......
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mReactRootView = new ReactRootView(this);

    mReactInstanceManager = ReactInstanceManager.builder()
      .setApplication(getApplication())
      .setBundleAssetName("index.android.bundle")
      .setJSMainModuleName("index.android")
      .addPackage(new MainReactPackage())
      .addPackage(new CountryPickerPackage())              // <------ add here
      .setUseDeveloperSupport(BuildConfig.DEBUG)
      .setInitialLifecycleState(LifecycleState.RESUMED)
      .build();

    mReactRootView.startReactApplication(mReactInstanceManager, "ExampleRN", null);

    setContentView(mReactRootView);
  }

  ......
}
```

### Screencasts
See [AndroidCountryPicker](https://github.com/roomorama/AndroidCountryPicker)

## Usage

### Example
```js
var React = require('react-native');
var {
    StyleSheet,
    View,
    Image
} = React;

var CountryPicker = require('react-native-country-picker');
var Button = require('react-native-simple-button');

module.exports = React.createClass({
    getInitialState() {
        return { code: null };
    },

    getSelectedCountry() {
        var _this = this;
        CountryPicker.show(function(country){
            _this.setState({ code: country.code });
        });
    },

    render() {
        return (
            <View style={styles.container}>
                <Text>{this.state.code}</Text>
                <Button onPress={this.getSelectedCountry.bind(this)}>
                    Launch Country Picker
                </Button>
            </View>
        );
    },
});


var styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'space-around',
        alignItems: 'center',
        backgroundColor: 'transparent',
        paddingVertical:150,
    }
});
```

### thanks
* Thanks to [roomorama](https://github.com/roomorama) for the [AndroidCountryPicker](https://github.com/roomorama/AndroidCountryPicker) Java Fragment code.
