/**
 * This class provides different rights and access that a User has in the System
 */

package com.ihsinformatics.tbr4reporterweb.shared;

/**
 * @author owais.hussain@ihsinformatics.com
 * 
 */
public class UserRightsUtil {
	private boolean deleteAccess;
	private boolean insertAccess;
	private boolean printAccess;
	private boolean searchAccess;
	private boolean updateAccess;

	public UserRightsUtil() {
		searchAccess = insertAccess = updateAccess = deleteAccess = printAccess = false;
	}

	public UserRightsUtil(boolean searchAccess, boolean insertAccess,
			boolean updateAccess, boolean deleteAccess, boolean printAccess) {
		this.searchAccess = searchAccess;
		this.insertAccess = insertAccess;
		this.updateAccess = updateAccess;
		this.deleteAccess = deleteAccess;
		this.printAccess = printAccess;
	}

	/**
	 * 
	 * @param userRole
	 * @param rights
	 */
	public void setRoleRights(String userRole, Boolean[] rights) {
		try {
			searchAccess = rights[0];
			insertAccess = rights[1];
			updateAccess = rights[2];
			deleteAccess = rights[3];
			printAccess = rights[4];
		} catch (Exception e) {
			searchAccess = insertAccess = updateAccess = deleteAccess = printAccess = false;
		}
	}

	public boolean getAccess(AccessType accessType) {
		boolean access = false;
		switch (accessType) {
		case SELECT:
			return searchAccess;
		case INSERT:
			return insertAccess;
		case UPDATE:
			return updateAccess;
		case DELETE:
			return deleteAccess;
		case PRINT:
			return printAccess;
		}
		return access;
	}
}