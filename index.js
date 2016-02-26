'use strict';

var React = require('react-native');
var {
    NativeModules
} = React;

var RNCountryPicker= NativeModules.CountryPicker;
var CountryPicker = {};


CountryPicker.show = function (callBack) {
  NativeModules.CountryPicker.show(callBack);
};

CountryPicker.hide = function () {
  RNCountryPicker.hide();
};

module.exports = CountryPicker;
