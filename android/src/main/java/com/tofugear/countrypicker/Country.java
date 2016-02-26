package com.tofugear.countrypicker;

/**
 * POJO
 *
 */
public class Country {
	private String code;
	private String name;
	private String phoneCode;
  public String getPhoneCode(){
		return phoneCode;
	}
	public String getCode() {
		return code;
	}
  public void setPhoneCode(String phoneCode){
		this.phoneCode=phoneCode;
	}
	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
