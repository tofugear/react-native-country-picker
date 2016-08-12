'use strict';

var React = require('react-native');
var {
    NativeModules
} = React;

var RNCountryPicker= NativeModules.CountryPicker;
var CountryPicker = {};


CountryPicker.show = function (title, searchHint, codes, callBack) {
  NativeModules.CountryPicker.show(title, searchHint, codes, callBack);
};

CountryPicker.hide = function () {
  RNCountryPicker.hide();
};

module.exports = CountryPicker;
