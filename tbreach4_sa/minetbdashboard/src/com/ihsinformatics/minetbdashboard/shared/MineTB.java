/**
 * This class contains constants and project-specific methods which will be used throughout the System
 */

package com.ihsinformatics.minetbdashboard.shared;


/*
 * @author owais.hussain@ihsinformatics.com
 * 
 */
public final class MineTB {
	public static final String hashingAlgorithm = "SHA";
	public static final String projectTitle = "MINE-TB Dashboard";
	public static final char separatorChar = ',';
	public static final int sessionLimit = 900000;
	public static final MineTB xsms = new MineTB();
	private static VersionUtil version;
	private static String currentUser;
	private static String passCode;
	public static String[] formOptions = { "YES", "NO", "DONT KNOW", "REJECTED" };
	public static String[] userRoles = { "ADMIN", "GUEST"};
	public static String[] userStatuses = { "ACTIVE", "SUSPENDED" };

	private MineTB() {
		currentUser = "";
		passCode = "";
	}

	/**
	 * Concatenate an Array of Strings into single String
	 * 
	 * @param array
	 * @return string
	 */
	public static String concatenateArray(String[] array) {
		StringBuilder concatenated = new StringBuilder();
		for (String s : array) {
			concatenated.append(s);
			concatenated.append(MineTB.separatorChar);
		}
		concatenated.deleteCharAt(concatenated.length() - 1);
		return concatenated.toString();
	}

	/**
	 * @return the version
	 */
	public static VersionUtil getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public static void setVersion(VersionUtil version) {
		MineTB.version = version;
	}

	/**
	 * Get a list of values which will be constant throughout the application
	 * 
	 * @param listType
	 * @return array
	 */
	public static String[] getList(ListType listType) {
		switch (listType) {
		case FORM_OPTION:
			return formOptions;
		case USER_ROLE:
			return userRoles;
		case USER_STATUS:
			return userStatuses;
		default:
			break;
		}
		return new String[] {};
	}

	/**
	 * Get secret question
	 * 
	 * @return array
	 */
	public static String[] getSecretQuestions() {
		String[] questions = { "WHO IS YOUR FAVOURITE NATIONAL HERO?",
				"WHAT PHONE MODEL ARE YOU PLANNING TO PURCHASE NEXT?",
				"WHERE WAS YOUR MOTHER BORN?",
				"WHEN DID YOU BUY YOUR FIRST CAR?",
				"WHAT WAS YOUR CHILDHOOD NICKNAME?",
				"WHAT IS YOUR FAVOURITE CARTOON CHARACTER?" };
		return questions;
	}

	/**
	 * Get current User Name (saved in cookies on client-side)
	 * 
	 * @return currentUser
	 */
	public static String getCurrentUser() {
		return currentUser;
	}

	/**
	 * Set current user
	 * 
	 * @param currentUser
	 */
	public static void setCurrentUser(String currentUser) {
		MineTB.currentUser = currentUser.toUpperCase();
	}

	/**
	 * Get pass code (first 4 characters of User's password)
	 * 
	 * @return passCode
	 */
	public static String getPassCode() {
		return passCode;
	}

	/**
	 * Set pass code for current user
	 * 
	 * @param passCode
	 */
	public static void setPassCode(String passCode) {
		MineTB.passCode = passCode;
	}
}
