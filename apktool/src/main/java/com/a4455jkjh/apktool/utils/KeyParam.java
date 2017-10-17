package com.a4455jkjh.apktool.utils;

public class KeyParam implements CharSequence {
	public int type;
	public String keyPath;
	public String certOrAlias;
	public String keyPass;
	public String storePass;
	public String commonName;
	public String organizationUnit;
	public String organizationName;
	public String localityName;
	public String stateName;
	public String country;
	public long days;

	@Override
	public int length () {
		return toString().length();
	}

	@Override
	public char charAt (int p1) {
		return toString().charAt(p1);
	}

	@Override
	public CharSequence subSequence (int p1, int p2) {
		return toString().subSequence(p1, p2);
	}

	@Override
	public String toString () {
		StringBuilder sb = new StringBuilder();
		sb.append("commonName=").
			append(commonName).
			append("\norganizationUnit=").
			append(organizationUnit).
			append("\norganizationName=").
			append(organizationName).
			append("\nlocalityName=").
			append(localityName).
			append("\nstateName=").
			append(stateName).
			append("\ncountry=").
			append(country);
		return sb.toString();
	}

}
