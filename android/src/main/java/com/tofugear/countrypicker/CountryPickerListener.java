package com.tofugear.countrypicker;

/**
 * Inform the client which country has been selected
 *
 */
public interface CountryPickerListener {
	public void onSelectCountry(String name, String code, String phoneCode);
}
