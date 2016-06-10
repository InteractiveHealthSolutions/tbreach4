/**
 * Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
 * You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html
 * Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
 * Contributors: Owais Hussain
 */
package com.ihsinformatics.tbr4reporterweb.client;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.ihsinformatics.tbr4reporterweb.shared.CustomMessage;
import com.ihsinformatics.tbr4reporterweb.shared.DataType;
import com.ihsinformatics.tbr4reporterweb.shared.ErrorType;
import com.ihsinformatics.tbr4reporterweb.shared.InfoType;
import com.ihsinformatics.tbr4reporterweb.shared.Parameter;
import com.ihsinformatics.tbr4reporterweb.shared.Report;
import com.ihsinformatics.tbr4reporterweb.shared.ReportFormat;
import com.ihsinformatics.tbr4reporterweb.shared.TBR4;
import com.ihsinformatics.tbr4reporterweb.shared.TimeDimenstion;

/**
 * @author owais.hussain@ihsinformatics.com
 */
public class ReportsComposite extends Composite implements IReport,
		ClickHandler, ChangeHandler, ValueChangeHandler<Boolean> {
	private static ServerServiceAsync service = GWT.create(ServerService.class);
	private static LoadingWidget loading = new LoadingWidget();
	private static Report[] reports;

	private VerticalPanel mainPanel = new VerticalPanel();
	private FlexTable flexTable = new FlexTable();
	private FlexTable dateFilterTable = new FlexTable();
	private FlexTable dataTable = new FlexTable();
	private Grid grid = new Grid(1, 4);

	private Button viewButton = new Button("Save");
	private Button refreshButton = new Button("Refresh");

	private Label lblSelectCategory = new Label("Select Category:");
	private Label lblSelectReport = new Label("Select Report:");
	private Label lblFilter = new Label("Filters:");

	private TextBox userIdTextBox = new TextBox();

	private ListBox categoryComboBox = new ListBox();
	private ListBox reportsListComboBox = new ListBox();
	private ListBox timeDimensionComboBox = new ListBox();
	private ListBox reportFormatComboBox = new ListBox();

	private ListBox yearFrom = new ListBox();
	private ListBox yearTo = new ListBox();
	private ListBox quarterFrom = new ListBox();
	private ListBox quarterTo = new ListBox();
	private ListBox monthFrom = new ListBox();
	private ListBox monthTo = new ListBox();
	private ListBox weekFrom = new ListBox();
	private ListBox weekTo = new ListBox();

	private CheckBox userIdCheckBox = new CheckBox("User ID:");

	public ReportsComposite() {
		initWidget(mainPanel);
		mainPanel.add(flexTable);
		mainPanel.add(dataTable);
		flexTable.setSize("100%", "100%");
		flexTable.setWidget(0, 0, lblSelectCategory);
		flexTable.setWidget(0, 1, categoryComboBox);
		flexTable.setWidget(1, 0, lblSelectReport);
		flexTable.setWidget(1, 1, reportsListComboBox);
		flexTable.setWidget(2, 0, lblFilter);
		flexTable.setWidget(2, 1, timeDimensionComboBox);
		flexTable.setWidget(3, 1, dateFilterTable);
		flexTable.setWidget(4, 0, userIdCheckBox);
		flexTable.setWidget(4, 1, userIdTextBox);
		flexTable.setWidget(5, 1, grid);

		userIdTextBox.setEnabled(false);
		userIdTextBox.setVisibleLength(20);
		userIdTextBox.setMaxLength(20);
		userIdTextBox.setWidth("200px");

		viewButton.setEnabled(false);
		viewButton.setText("View");

		grid.setWidget(0, 0, reportFormatComboBox);
		grid.setWidget(0, 1, viewButton);
		grid.setWidget(0, 2, refreshButton);

		reportsListComboBox.addChangeHandler(this);
		userIdCheckBox.addValueChangeHandler(this);
		categoryComboBox.addChangeHandler(this);
		timeDimensionComboBox.addChangeHandler(this);

		viewButton.addClickHandler(this);
		refreshButton.addClickHandler(this);

		createDateFilterWidgets(TimeDimenstion.YEAR);
		refreshList();
	}

	@SuppressWarnings("deprecation")
	private void refreshList() {
		categoryComboBox.addItem("-- Select Category --");
		categoryComboBox.addItem("Reports");
		categoryComboBox.addItem("Data Dumps");
		for (TimeDimenstion dim : TimeDimenstion.values()) {
			timeDimensionComboBox.addItem(dim.toString());
		}
		for (int year = 2012; year <= new Date().getYear() + 1900; year++) {
			yearFrom.addItem(String.valueOf(year));
			yearTo.addItem(String.valueOf(year));
		}
		for (int quarter = 1; quarter <= 4; quarter++) {
			quarterFrom.addItem(String.valueOf(quarter));
			quarterTo.addItem(String.valueOf(quarter));
		}
		for (int month = 1; month <= 12; month++) {
			monthFrom.addItem(String.valueOf(month));
			monthTo.addItem(String.valueOf(month));
		}
		for (int week = 1; week <= 52; week++) {
			weekFrom.addItem(String.valueOf(week));
			weekTo.addItem(String.valueOf(week));
		}
		for (ReportFormat f : ReportFormat.values()) {
			reportFormatComboBox.addItem(f.toString());
		}
	}

	public void createDateFilterWidgets(TimeDimenstion time) {
		dateFilterTable.clear();
		switch (time) {
		case YEAR:
			dateFilterTable.setWidget(0, 0, yearFrom);
			dateFilterTable.setWidget(0, 1, yearTo);
			break;
		case MONTH:
			dateFilterTable.setWidget(0, 0, yearFrom);
			dateFilterTable.setWidget(1, 0, monthFrom);
			dateFilterTable.setWidget(1, 1, monthTo);
			break;
		case QUARTER:
			dateFilterTable.setWidget(0, 0, yearFrom);
			dateFilterTable.setWidget(1, 0, quarterFrom);
			dateFilterTable.setWidget(1, 1, quarterTo);
			break;
		case WEEK:
			dateFilterTable.setWidget(0, 0, yearFrom);
			dateFilterTable.setWidget(1, 0, weekFrom);
			dateFilterTable.setWidget(1, 1, weekTo);
			break;
		default:
		}
	}

	/**
	 * Display/Hide main panel and loading widget
	 * 
	 * @param status
	 */
	public void load(boolean status) {
		flexTable.setVisible(!status);
		if (status)
			loading.show();
		else
			loading.hide();
	}

	/**
	 * Creates appropriate filter for given column names
	 * 
	 * @return
	 */
	private String getFilter() {
		StringBuilder where = new StringBuilder(" where 1 = 1 ");
		// Append Date filter
		String yFrom = TbrClient.get(yearFrom);
		String yTo = TbrClient.get(yearTo);
		String qFrom = TbrClient.get(quarterFrom);
		String qTo = TbrClient.get(quarterTo);
		String mFrom = TbrClient.get(monthFrom);
		String mTo = TbrClient.get(monthTo);
		String wFrom = TbrClient.get(weekFrom);
		String wTo = TbrClient.get(weekTo);
		TimeDimenstion time = TimeDimenstion.valueOf(TbrClient
				.get(timeDimensionComboBox));
		switch (time) {
		case YEAR:
			where.append(" and year between " + yFrom + " and " + yTo);
			break;
		case QUARTER:
			where.append(" and year = " + yFrom);
			where.append(" and quarter between " + qFrom + " and " + qTo);
			break;
		case MONTH:
			where.append(" and year = " + yFrom);
			where.append(" and month between " + mFrom + " and " + mTo);
			break;
		case WEEK:
			where.append(" and year = " + yFrom);
			where.append(" and week between " + wFrom + " and " + wTo);
			break;
		default:
			break;
		}
		return where.toString();
	}

	@Override
	public void clearUp() {
	}

	@Override
	public boolean validate() {
		return true;
	}

	public void refreshData() {
		try {
			service.executeProcedure("call generate_dashboard",
					new AsyncCallback<Boolean>() {
						@Override
						public void onSuccess(Boolean result) {
							Window.alert(CustomMessage
									.getInfoMessage(InfoType.UPDATED));
							load(false);
						}

						@Override
						public void onFailure(Throwable caught) {
							caught.printStackTrace();
							Window.alert(CustomMessage
									.getErrorMessage(ErrorType.UPDATE_ERROR));
							load(false);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
			load(false);
		}
	}

	private String getReportQuery(String reportName) {
		StringBuilder query = new StringBuilder();
		if (reportName.equals("Daily Screening by Adults & Paeds")) {
			query.append("select username, year, month, week, day, sum(if(encounter_type = 'Adult Screening', total, 0)) as adult_screening, sum(if(encounter_type = 'Paediatric Screening', total, 0)) as paediatric_screening, sum(total) as total from openmrs_rpt.rpt_screening ");
			query.append(getFilter() + " ");
			query.append("and screening_type = 'Suspect' ");
			query.append("group by username, year, month, week, day ");
		} else if (reportName.equals("Monthly Screening by Adults & Paeds")) {
			query.append("select username, year, month, sum(if(encounter_type = 'Adult Screening', total, 0)) as adult_screening, sum(if(encounter_type = 'Paediatric Screening', total, 0)) as paediatric_screening, sum(total) as total from openmrs_rpt.rpt_screening ");
			query.append(getFilter() + " ");
			query.append("and screening_type = 'Suspect' ");
			query.append("group by username, year, month ");
		} else if (reportName.equals("Weekly Screening by Adults & Paeds")) {
			query.append("select username, year, month, week, sum(if(encounter_type = 'Adult Screening', total, 0)) as adult_screening, sum(if(encounter_type = 'Paediatric Screening', total, 0)) as paediatric_screening, sum(total) as total from openmrs_rpt.rpt_screening ");
			query.append(getFilter() + " ");
			query.append("and screening_type = 'Suspect' ");
			query.append("group by username, year, week ");
		} else if (reportName.equals("Screening by Adults & Paeds")) {
			query.append("select username, year, sum(if(encounter_type = 'Adult Screening', total, 0)) as adult_screening, sum(if(encounter_type = 'Paediatric Screening', total, 0)) as paediatric_screening, sum(total) as total from openmrs_rpt.rpt_screening ");
			query.append(getFilter() + " ");
			query.append("and screening_type = 'Suspect' ");
			query.append("group by username");
		} else if (reportName
				.equals("Daily Household and Hospital Visits for Screening")) {
			query.append("select encounter_type as strategy, username, year, month, week, day, sum(if(screening_type = 'OPD Screening', total, 0)) as hospital_visits, sum(if(screening_type = 'Field Screening' , total, 0)) as field_visits, sum(total) as total from openmrs_rpt.rpt_visit ");
			query.append(getFilter() + " ");
			query.append("and encounter_type in ('Adult Screening', 'Paediatric Screening') ");
			query.append("group by encounter_type, username, year, month, week, day ");
		} else if (reportName
				.equals("Weekly Household and Hospital Visits for Screening")) {
			query.append("select encounter_type as strategy, username, year, month, week, sum(if(screening_type = 'OPD Screening', total, 0)) as hospital_visits, sum(if(screening_type = 'Field Screening' , total, 0)) as field_visits, sum(total) as total from openmrs_rpt.rpt_visit ");
			query.append(getFilter() + " ");
			query.append("and encounter_type in ('Adult Screening', 'Paediatric Screening') ");
			query.append("group by encounter_type, username, year, month, week ");
		} else if (reportName
				.equals("Monthly Household and Hospital Visits for Screening")) {
			query.append("select encounter_type as strategy, username, year, month, sum(if(screening_type = 'OPD Screening', total, 0)) as hospital_visits, sum(if(screening_type = 'Field Screening' , total, 0)) as field_visits, sum(total) as total from openmrs_rpt.rpt_visit ");
			query.append(getFilter() + " ");
			query.append("and encounter_type in ('Adult Screening', 'Paediatric Screening') ");
			query.append("group by encounter_type, username, year, month ");
		} else if (reportName
				.equals("Household and Hospital Visits for Screening")) {
			query.append("select username, encounter_type as strategy, sum(if(screening_type = 'OPD Screening', total, 0)) as hospital_visits, sum(if(screening_type = 'Field Screening' , total, 0)) as field_visits, sum(total) as total from openmrs_rpt.rpt_visit ");
			query.append(getFilter() + " ");
			query.append("and encounter_type in ('Adult Screening', 'Paediatric Screening') ");
			query.append("group by encounter_type, username ");
		} else if (reportName
				.equals("Daily Screening by Suspects & Non-Suspects")) {
			query.append("select username, year, month, week, day, sum(if(screening_type = 'Suspect', total, 0)) as suspects, sum(if(screening_type = 'Non-Suspect' , total, 0)) as non_suspects, sum(total) as total from openmrs_rpt.rpt_screening ");
			query.append(getFilter() + " ");
			query.append("group by username, year, month, week, day ");
		} else if (reportName
				.equals("Weekly Screening by Suspects & Non-Suspects")) {
			query.append("select username, year, month, week, sum(if(screening_type = 'Suspect', total, 0)) as suspects, sum(if(screening_type = 'Non-Suspect' , total, 0)) as non_suspects, sum(total) as total from openmrs_rpt.rpt_screening ");
			query.append(getFilter() + " ");
			query.append("group by username, year, month, week ");
		} else if (reportName
				.equals("Monthly Screening by Suspects & Non-Suspects")) {
			query.append("select username, year, month, sum(if(screening_type = 'Suspect', total, 0)) as suspects, sum(if(screening_type = 'Non-Suspect' , total, 0)) as non_suspects, sum(total) as total from openmrs_rpt.rpt_screening ");
			query.append(getFilter() + " ");
			query.append("group by username, year, month ");
		} else if (reportName.equals("Screening by Suspects & Non-Suspects")) {
			query.append("select username, sum(if(screening_type = 'Suspect', total, 0)) as suspects, sum(if(screening_type = 'Non-Suspect' , total, 0)) as non_suspects, sum(total) as total from openmrs_rpt.rpt_screening ");
			query.append(getFilter() + " ");
			query.append("group by username ");
		} else if (reportName.equals("Daily Visits for Contact Tracing")) {
			query.append("select encounter_type as strategy, username, year, month, week, day, sum(if(screening_type = 'Clinical Visit' , total, 0)) as clinical_visits, sum(if(screening_type = 'Home Visit' , total, 0)) as home_visits, sum(total) as total from openmrs_rpt.rpt_visit ");
			query.append(getFilter() + " ");
			query.append("and encounter_type in ('Adult Reverse Contact Tracing', 'Reverse Contact Tracing', 'Paediatric Contact Tracing') ");
			query.append("group by encounter_type, username, month, week, day ");
		} else if (reportName.equals("Weekly Visits for Contact Tracing")) {
			query.append("select encounter_type as strategy, username, year, month, week, day, sum(if(screening_type = 'Clinical Visit' , total, 0)) as clinical_visits, sum(if(screening_type = 'Home Visit' , total, 0)) as home_visits, sum(total) as total from openmrs_rpt.rpt_visit ");
			query.append(getFilter() + " ");
			query.append("and encounter_type in ('Adult Reverse Contact Tracing', 'Reverse Contact Tracing', 'Paediatric Contact Tracing') ");
			query.append("group by encounter_type, username, month, week ");
		} else if (reportName.equals("Monthly Visits for Contact Tracing")) {
			query.append("select encounter_type as strategy, username, year, month, week, day, sum(if(screening_type = 'Clinical Visit' , total, 0)) as clinical_visits, sum(if(screening_type = 'Home Visit' , total, 0)) as home_visits, sum(total) as total from openmrs_rpt.rpt_visit ");
			query.append(getFilter() + " ");
			query.append("and encounter_type in ('Adult Reverse Contact Tracing', 'Reverse Contact Tracing', 'Paediatric Contact Tracing') ");
			query.append("group by encounter_type, username, month ");
		} else if (reportName.equals("Visits for Contact Tracing")) {
			query.append("select encounter_type as strategy, username, year, month, week, day, sum(if(screening_type = 'Clinical Visit' , total, 0)) as clinical_visits, sum(if(screening_type = 'Home Visit' , total, 0)) as home_visits, sum(total) as total from openmrs_rpt.rpt_visit ");
			query.append(getFilter() + " ");
			query.append("and encounter_type in ('Adult Reverse Contact Tracing', 'Reverse Contact Tracing', 'Paediatric Contact Tracing') ");
			query.append("group by encounter_type, username ");
		}
		return query.toString();
	}

	private String getDataDumpQuery(String entity) {
		StringBuilder query = new StringBuilder();
		if (entity.equals("Patient Data")) {
			query.append("");
			query.append(getFilter());
		} else if (entity.equals("Locations Data")) {
			query.append("");
			query.append(getFilter());
		} else if (entity.equals("Users Data")) {
			query.append("");
			query.append(getFilter());
		} else {
			query.append("");
			query.append(getFilter());
		}
		return query.toString();
	}

	@Override
	public void viewData(final ReportFormat reportFormat) {
		String reportSelected = TbrClient.get(reportsListComboBox);
		String query = "";
		if (TbrClient.get(categoryComboBox).equals("Reports")) {
			query = getReportQuery(reportSelected);
		} else if (TbrClient.get(categoryComboBox).equals("Data Dumps")) {
			query = getDataDumpQuery(reportSelected);
		}
		load(true);
		if (TbrClient.get(categoryComboBox).equals("Data Dumps")) {
			try {
				service.generateCsvfromQuery(query,
						new AsyncCallback<String>() {
							@Override
							public void onSuccess(String result) {
								String url = Window.Location.getHref()
										+ "data/" + result;
								Window.open(url, "_blank", "");
								load(false);
							}

							@Override
							public void onFailure(Throwable caught) {
								caught.printStackTrace();
								load(false);
							}
						});
			} catch (Exception e) {
				e.printStackTrace();
				load(false);
			}
		} else {
			try {
				Parameter[] params = getParameters();
				service.generateReport(reportSelected, query, params,
						reportFormat, new AsyncCallback<String>() {
							@Override
							public void onSuccess(String result) {
								String url = Window.Location.getHref()
										+ "data/" + result;
								Window.open(url, "_blank", "");
								load(false);
							}

							@Override
							public void onFailure(Throwable caught) {
								caught.printStackTrace();
								load(false);
							}
						});
			} catch (Exception e) {
				load(false);
				e.printStackTrace();
			}
		}
	}

	/**
	 * Convert the form filters into respective parameters
	 * 
	 * @return
	 */
	public Parameter[] getParameters() {
		ArrayList<Parameter> params = new ArrayList<Parameter>();
		params.add(new Parameter("UserName", TBR4.getCurrentUser(),
				DataType.STRING));
		if (userIdCheckBox.getValue()) {
			params.add(new Parameter("UserID", TbrClient.get(userIdTextBox)
					+ "%", DataType.STRING));
		} else {
			params.add(new Parameter("UserID", "%", DataType.STRING));
		}
		return params.toArray(new Parameter[] {});
	}

	public void setRights(String menuName) {
		// Not implemented
	}

	@Override
	public void onClick(ClickEvent event) {
		Widget sender = (Widget) event.getSource();
		load(true);
		if (sender == viewButton) {
			ReportFormat format = ReportFormat.valueOf(TbrClient
					.get(reportFormatComboBox));
			viewData(format);
		} else if (sender == refreshButton) {
			refreshData();
		}
	}

	@Override
	public void onChange(ChangeEvent event) {
		Widget sender = (Widget) event.getSource();
		// Fill report names
		if (sender == categoryComboBox) {
			String text = TbrClient.get(sender);
			reportsListComboBox.clear();
			if (text.equals("Reports")) {
				// Fetch list of reports from server
				try {
					service.getReportsList(new AsyncCallback<Report[]>() {
						@Override
						public void onSuccess(Report[] result) {
							reports = result;
							for (Report report : reports) {
								reportsListComboBox.addItem(report.getName());
							}
						}

						@Override
						public void onFailure(Throwable caught) {
							Window.alert("List of reports cannot be populated. Have you copied the reports to rpt directory?");
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
				viewButton.setEnabled(true);
			} else if (text.equals("Data Dumps")) {
				reportsListComboBox.addItem("Patient Data");
				reportsListComboBox.addItem("Users Data");
				viewButton.setEnabled(false);
			} else {
				viewButton.setEnabled(false);
			}
		} else if (sender == timeDimensionComboBox) {
			TimeDimenstion time = TimeDimenstion.valueOf(TbrClient
					.get(timeDimensionComboBox));
			createDateFilterWidgets(time);
		}
	}

	@Override
	public void onValueChange(ValueChangeEvent<Boolean> event) {
		Widget sender = (Widget) event.getSource();
		if (sender == userIdCheckBox) {
			userIdTextBox.setEnabled(userIdCheckBox.getValue());
		}
	}
}
