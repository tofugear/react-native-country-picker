package com.tofugear.countrypicker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

public class CountryPicker extends DialogFragment implements
		Comparator<Country> {
	/**
	 * View components
	 */
	private EditText searchEditText;
	private ListView countryListView;

	/**
	 * Adapter for the listview
	 */
	private CountryListAdapter adapter;

	/**
	 * Hold all countries, sorted by country name
	 */
	private List<Country> allCountriesList;

	/**
	 * Hold countries that matched user query
	 */
	private List<Country> selectedCountriesList;

	/**
	 * Listener to which country user selected
	 */
	private CountryPickerListener listener;

	/**
	 * Set listener
	 *
	 * @param listener
	 */
	public void setListener(CountryPickerListener listener) {
		this.listener = listener;
	}

	/**
	 * Set all countries with code and name from res/raw/countries.json
	 *
	 * @return
	 */
	private List<Country> setAllCountriesFromJSON() {
		if (allCountriesList == null) {
			try {
				allCountriesList = new ArrayList<Country>();

				// Read from local file
				String allCountriesString = readFileAsString(getActivity());
				JSONObject jsonObject = new JSONObject(allCountriesString);
				Iterator<?> keys = jsonObject.keys();

				// Add the data to all countries list
				while (keys.hasNext()) {
					String key = (String) keys.next();
					Country country = new Country();
					country.setCode(key);
					String[] parts=jsonObject.getString(key).split("\\|");
					country.setPhoneCode(parts[0]);
					country.setName(parts[1]);
					allCountriesList.add(country);
				}

				// Sort the all countries list based on country name
				Collections.sort(allCountriesList, this);

				// Initialize selected countries with all countries
				selectedCountriesList = new ArrayList<Country>();
				selectedCountriesList.addAll(allCountriesList);

				// Return
				return allCountriesList;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Set all countries from the country list given
	 *
	 * Per entry in the format of: code|phone|name
	 */
	private void setAllCountries(ArrayList<String> countries) {
		if (allCountriesList != null)
			return;
		allCountriesList = new ArrayList<Country>();
		for (String country_s: countries) {
			String[] parts = country_s.split("\\|");
			Country country = new Country();
			country.setCode(parts[0]);
			country.setPhoneCode(parts[1]);
			country.setName(parts[2]);
			allCountriesList.add(country);
		}
		// Sort the all countries list based on country name
		Collections.sort(allCountriesList, this);
		// Initialize selected countries with all countries
		selectedCountriesList = new ArrayList<Country>();
		selectedCountriesList.addAll(allCountriesList);
	}

	/**
	 * move the countries in the preferred list to top
	 *
	 * @param preferredList
	 * @return
	 */
	private void setPreferredCountries(String preferredList) {
		if (preferredList != null && !preferredList.isEmpty()) {
			String[] codes = preferredList.split(" ");

			// get code list
			ArrayList<String> codeList = new ArrayList<>();
			Iterator<Country> keys = allCountriesList.iterator();
			while (keys.hasNext()) {
				Country country = (Country) keys.next();
				codeList.add(country.getCode());
			}

			ArrayList<Country> preferredCountries = new ArrayList<>();
			// find index of certain country
			for (String code: codes) {
				int index = codeList.indexOf(code);
				Country country = allCountriesList.get(index);
				preferredCountries.add(country);
			}
			// remove those countries and add to top
			for (int i = preferredCountries.size()-1; i >= 0; i--) {
				allCountriesList.remove(preferredCountries.get(i));
				allCountriesList.add(0, preferredCountries.get(i));
			}
			// Initialize selected countries with all countries
			selectedCountriesList = new ArrayList<Country>();
			selectedCountriesList.addAll(allCountriesList);
		}
	}

	/**
	 * R.string.countries is a json string which is Base64 encoded to avoid
	 * special characters in XML. It's Base64 decoded here to get original json.
	 *
	 * @param context
	 * @return
	 * @throws java.io.IOException
	 */
	private static String readFileAsString(Context context)
			throws java.io.IOException {
		String base64 = context.getResources().getString(R.string.countries);
		byte[] data = Base64.decode(base64, Base64.DEFAULT);
		return new String(data, "UTF-8");
	}

	/**
	 * To support show as dialog
	 *
	 * @param dialogTitle The title
	 * @param searchHint The hint placeholder to show
	 * @param preferredCountryCodes List of preferred codes, delimited by space
	 * @praam countries Optional list of countries to be used. Default list is used if not provided
	 *
	 * @return New instance of the CountryPicker
	 */
	public static CountryPicker newInstance(
			String dialogTitle,
			String searchHint,
			String preferredCountryCodes,
			ArrayList<String> countries) {
		CountryPicker picker = new CountryPicker();
		Bundle bundle = new Bundle();
		bundle.putString("dialogTitle", dialogTitle);
		bundle.putString("searchHint", searchHint);
		bundle.putString("preferredCountryCodes", preferredCountryCodes);
		bundle.putStringArrayList("countries", countries);
		picker.setArguments(bundle);
		return picker;
	}

	/**
	 * Create view
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate view
		View view = inflater.inflate(R.layout.country_picker, null);

		Bundle args = getArguments();
		if (args.getStringArrayList("countries") != null) {
			setAllCountries(args.getStringArrayList("countries"));
		} else {
			setAllCountriesFromJSON();
		}

		String codes = args.getString("preferredCountryCodes");
		setPreferredCountries(codes);

		String dialogTitle = args.getString("dialogTitle");
		getDialog().setTitle(dialogTitle);

		String searchHint = args.getString("searchHint");

		int width = getResources().getDimensionPixelSize(
				R.dimen.cp_dialog_width);
		int height = getResources().getDimensionPixelSize(
				R.dimen.cp_dialog_height);
		getDialog().getWindow().setLayout(width, height);

		// Get view components
		searchEditText = (EditText) view
				.findViewById(R.id.country_picker_search);
		// set hint text
		searchEditText.setHint(searchHint);
		countryListView = (ListView) view
				.findViewById(R.id.country_picker_listview);

		// Set adapter
		adapter = new CountryListAdapter(getActivity(), selectedCountriesList);
		countryListView.setAdapter(adapter);

		// Inform listener
		countryListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (listener != null) {
					Country country = selectedCountriesList.get(position);
					listener.onSelectCountry(country.getName(),
							country.getCode(),country.getPhoneCode());
				}
			}
		});

		// Search for which countries matched user query
		searchEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				search(s.toString());
			}
		});

		return view;
	}

	/**
	 * Search allCountriesList contains text and put result into
	 * selectedCountriesList
	 *
	 * @param text
	 */
	@SuppressLint("DefaultLocale")
	private void search(String text) {
		selectedCountriesList.clear();

		for (Country country : allCountriesList) {
			if (country.getName().toLowerCase(Locale.ENGLISH)
					.contains(text.toLowerCase())) {
				selectedCountriesList.add(country);
			}
		}

		adapter.notifyDataSetChanged();
	}

	/**
	 * Support sorting the countries list
	 */
	@Override
	public int compare(Country lhs, Country rhs) {
		return lhs.getName().compareTo(rhs.getName());
	}

}
