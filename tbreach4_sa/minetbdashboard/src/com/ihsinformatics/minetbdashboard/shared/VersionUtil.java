/*
Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html
Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
package com.ihsinformatics.minetbdashboard.shared;

import java.text.ParseException;

/**
 * @author owais.hussain@ihsinformatics.com
 */
public class VersionUtil implements Comparable<VersionUtil> {

	private String version;
	private boolean isAlpha;
	private boolean isBeta;
	private boolean isRelease;
	private int major;
	private int minor;
	private int micro;

	public VersionUtil() {
		version = "";
		isAlpha = isBeta = isRelease = false;
		major = minor = micro = 0;
	}

	/**
	 * @param isAlpha
	 * @param isBeta
	 * @param isRelease
	 * @param major
	 * @param minor
	 * @param micro
	 */
	public VersionUtil(boolean isAlpha, boolean isBeta, boolean isRelease,
			int major, int minor, int micro) {
		super();
		this.isAlpha = isAlpha;
		this.isBeta = isBeta;
		this.isRelease = isRelease;
		this.major = major;
		this.minor = minor;
		this.micro = micro;
	}

	/**
	 * Version must be in one of the following formats:
	 * MAJOR.Minor.micro-(alpha|beta); MAJOR.Minor.micro
	 * 
	 * @param version
	 */
	public void parseVersion(String version) throws NullPointerException,
			ParseException, NumberFormatException {
		if (version == null) {
			throw new NullPointerException();
		}
		if ("".equals(version)) {
			throw new ParseException("Cannot parse empty string", 0);
		}
		String[] parts = version.split("[.]");
		if (parts.length != 3) {
			throw new NumberFormatException(
					"Cannot parse version in invalid format");
		}
		setAlpha(false);
		setBeta(false);
		setRelease(true);
		setMajor(Integer.parseInt(parts[0]));
		setMinor(Integer.parseInt(parts[1]));
		// Check if release type and/or build number are given
		if (parts[2].contains("-")) {
			String[] further = parts[2].split("[-]");
			if (further.length != 2) {
				throw new NumberFormatException(
						"Cannot parse version in invalid format");
			}
			setMicro(Integer.parseInt(further[0]));
			// Decide what type of release this is
			if (further[1].equalsIgnoreCase("alpha")) {
				setAlpha(true);
				setBeta(false);
				setRelease(false);
			} else if (further[1].equalsIgnoreCase("beta")) {
				setAlpha(false);
				setBeta(true);
				setRelease(false);
			} else {
				throw new ParseException("Cannot parse version type "
						+ further[1], 1);
			}
		} else {
			setMicro(Integer.parseInt(parts[2]));
		}
		if (getMajor() == 0 && getMinor() == 0 && getMicro() == 0) {
			setRelease(false);
		}
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @return the isAlpha
	 */
	public boolean isAlpha() {
		return isAlpha;
	}

	/**
	 * @param isAlpha
	 *            the isAlpha to set
	 */
	public void setAlpha(boolean isAlpha) {
		this.isAlpha = isAlpha;
	}

	/**
	 * @return the isBeta
	 */
	public boolean isBeta() {
		return isBeta;
	}

	/**
	 * @param isBeta
	 *            the isBeta to set
	 */
	public void setBeta(boolean isBeta) {
		this.isBeta = isBeta;
	}

	/**
	 * @return the isRelease
	 */
	public boolean isRelease() {
		return isRelease;
	}

	/**
	 * @param isRelease
	 *            the isRelease to set
	 */
	public void setRelease(boolean isRelease) {
		this.isRelease = isRelease;
	}

	/**
	 * @return the major
	 */
	public int getMajor() {
		return major;
	}

	/**
	 * @param major
	 *            the major to set
	 */
	public void setMajor(int major) {
		this.major = major;
	}

	/**
	 * @return the minor
	 */
	public int getMinor() {
		return minor;
	}

	/**
	 * @param minor
	 *            the minor to set
	 */
	public void setMinor(int minor) {
		this.minor = minor;
	}

	/**
	 * @return the micro
	 */
	public int getMicro() {
		return micro;
	}

	/**
	 * @param micro
	 *            the micro to set
	 */
	public void setMicro(int micro) {
		this.micro = micro;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isAlpha ? 1231 : 1237);
		result = prime * result + (isBeta ? 1231 : 1237);
		result = prime * result + (isRelease ? 1231 : 1237);
		result = prime * result + major;
		result = prime * result + micro;
		result = prime * result + minor;
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VersionUtil other = (VersionUtil) obj;
		if (isAlpha != other.isAlpha)
			return false;
		if (isBeta != other.isBeta)
			return false;
		if (isRelease != other.isRelease)
			return false;
		if (major != other.major)
			return false;
		if (micro != other.micro)
			return false;
		if (minor != other.minor)
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

	/**
	 * Two versions are compatible if their Major and Minor versions are same
	 * 
	 * @param version
	 * @return
	 */
	public boolean isCompatible(VersionUtil version) {
		boolean result = false;
		if (version.compareTo(this) == 0)
			result = true;
		else if (version.getMajor() == this.getMajor()
				&& version.getMinor() == this.getMinor())
			result = true;
		return result;
	}

	/**
	 * Returns 0 if both versions are same; 1 if version passed is ahead; -1 if
	 * version passed is behind
	 */
	@Override
	public int compareTo(VersionUtil version) {
		int result = -1;
		if (this.equals(version))
			result = 0;
		if (version.getMajor() == this.getMajor()) {
			if (version.getMinor() == this.getMinor()) {
				if (version.getMicro() == this.getMinor())
					result = 0;
				else if (version.getMicro() > this.getMicro())
					result = 1;
			} else if (version.getMinor() > this.getMinor())
				result = 1;
		} else if (version.getMajor() > this.getMajor())
			result = 1;
		return result;
	}

	@Override
	public String toString() {
		return getMajor() + "." + getMinor() + "." + getMicro()
				+ (isAlpha ? "-alpha" : isBeta ? "-beta" : "");
	}
}
