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
import java.util.Collection;
import java.util.Date;

import org.openmrs.Auditable;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.User;

/**
 * This class represents CSV Configurations objects that can be saved in the database
 * 
 * @author owais.hussain@irdresearch.org
 */
public class CsvImporterConfiguration extends BaseOpenmrsObject implements Serializable, Auditable {
	
	private static final long serialVersionUID = 57787749892753328L;
	
	private Integer configId;
	
	private String configName;
	
	private Date dateCreated;
	
	private User creator;
	
	private Date dateChanged;
	
	private User changedBy;
	
	private String csvHeader;
	
	private Character fieldSeparator;
	
	private Character lineSeparator;
	
	private String dateFormat;
	
	private String timeFormat;
	
	private String configDescription;
	
	private Collection<CsvImporterMapping> mappings;
	
	public CsvImporterConfiguration() {
		super();
		this.configName = "";
		this.dateCreated = null;
		this.creator = null;
		this.changedBy = null;
		this.dateChanged = null;
		this.csvHeader = null;
		this.fieldSeparator = ',';
		this.lineSeparator = '\n';
		this.dateFormat = "yyyy-MM-dd";
		this.timeFormat = "hh:mm:ss";
		this.configDescription = null;
	}
	
	public CsvImporterConfiguration(Integer configId, String configName, Date dateCreated, User creator, User changedBy,
	    Date dateChanged, String csvHeader, Character fieldSeparator, Character lineSeparator, String dateFormat,
	    String timeFormat, String configDescription) {
		super();
		this.configId = configId;
		this.configName = configName;
		this.dateCreated = dateCreated;
		this.creator = creator;
		this.changedBy = changedBy;
		this.dateChanged = dateChanged;
		this.csvHeader = csvHeader;
		this.fieldSeparator = fieldSeparator;
		this.lineSeparator = lineSeparator;
		this.dateFormat = dateFormat;
		this.timeFormat = timeFormat;
		this.configDescription = configDescription;
	}
	
	public Integer getId() {
		return configId;
	}
	
	public void setId(Integer configId) {
		this.configId = configId;
	}
	
	public String getConfigName() {
		return configName;
	}
	
	public void setConfigName(String configName) {
		this.configName = configName;
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
	
	public String getCsvHeader() {
		return csvHeader;
	}
	
	public void setCsvHeader(String csvHeader) {
		this.csvHeader = csvHeader;
	}
	
	public Character getFieldSeparator() {
		return fieldSeparator;
	}
	
	public void setFieldSeparator(Character fieldSeparator) {
		this.fieldSeparator = fieldSeparator;
	}
	
	public Character getLineSeparator() {
		return lineSeparator;
	}
	
	public void setLineSeparator(Character lineSeparator) {
		this.lineSeparator = lineSeparator;
	}
	
	public String getDateFormat() {
		return dateFormat;
	}
	
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}
	
	public String getTimeFormat() {
		return timeFormat;
	}
	
	public void setTimeFormat(String timeFormat) {
		this.timeFormat = timeFormat;
	}
	
	public String getConfigDescription() {
		return configDescription;
	}
	
	public void setConfigDescription(String configDescription) {
		this.configDescription = configDescription;
	}
	
	public Collection<CsvImporterMapping> getMappings() {
		return mappings;
	}
	
	public void setMappings(Collection<CsvImporterMapping> mappings) {
		this.mappings = mappings;
	}
}
