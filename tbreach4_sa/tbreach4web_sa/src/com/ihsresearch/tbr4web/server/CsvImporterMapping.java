/**
 * Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
 * You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html
 * Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
 * Contributors: Owais
 */
package com.ihsresearch.tbr4web.server;

import java.io.Serializable;
import java.util.Date;

import org.openmrs.Auditable;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.User;

/**
 * This class represents mapping of a single header column to respective OpenMRS object in CSV
 * Importer Configuration
 * 
 * @author owais.hussain@irdresearch.org
 */
public class CsvImporterMapping extends BaseOpenmrsObject implements Serializable, Auditable {
	
	private static final long serialVersionUID = 4869902029373613415L;
	
	private Integer mappingId;
	
	private Date dateCreated;
	
	private User creator;
	
	private User changedBy;
	
	private Date dateChanged;
	
	private CsvImporterConfiguration configuration;
	
	private String columnName; // Header name
	
	private String objectCategory; // Person attribute, Obs, etc.
	
	private String objectName; // OpenMRS object to map to
	
	private String otherArgs; // Like encounter
	
	public CsvImporterMapping() {
		super();
		this.mappingId = null;
		this.dateCreated = null;
		this.creator = null;
		this.changedBy = null;
		this.dateChanged = null;
		this.configuration = null;
		this.columnName = null;
		this.objectCategory = null;
		this.objectName = null;
		this.otherArgs = null;
	}
	
	public CsvImporterMapping(Integer mappingId, Date dateCreated, User creator, User changedBy, Date dateChanged,
	    CsvImporterConfiguration configuration, String columnName, String objectCategory, String objectName, String otherArgs) {
		super();
		this.mappingId = mappingId;
		this.dateCreated = dateCreated;
		this.creator = creator;
		this.changedBy = changedBy;
		this.dateChanged = dateChanged;
		this.configuration = configuration;
		this.columnName = columnName;
		this.objectCategory = objectCategory;
		this.objectName = objectName;
		this.otherArgs = otherArgs;
	}
	
	public Integer getId() {
		return mappingId;
	}
	
	public void setId(Integer mappingId) {
		this.mappingId = mappingId;
	}
	
	public Date getDateCreated() {
		return dateCreated;
	}
	
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	
	public User getCreator() {
		return creator;
	}
	
	public void setCreator(User creator) {
		this.creator = creator;
	}
	
	public User getChangedBy() {
		return changedBy;
	}
	
	public void setChangedBy(User changedBy) {
		this.changedBy = changedBy;
	}
	
	public Date getDateChanged() {
		return dateChanged;
	}
	
	public void setDateChanged(Date dateChanged) {
		this.dateChanged = dateChanged;
	}
	
	public CsvImporterConfiguration getConfiguration() {
		return configuration;
	}
	
	public void setConfiguration(CsvImporterConfiguration configuration) {
		this.configuration = configuration;
	}
	
	public String getColumnName() {
		return columnName;
	}
	
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	
	public String getObjectCategory() {
		return objectCategory;
	}
	
	public void setObjectCategory(String objectCategory) {
		this.objectCategory = objectCategory;
	}
	
	public String getObjectName() {
		return objectName;
	}
	
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	
	public String getOtherArgs() {
		return otherArgs;
	}
	
	public void setOtherArgs(String otherArgs) {
		this.otherArgs = otherArgs;
	}
}
