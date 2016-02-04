package com.ihsinformatics.minetbdashboard.client;

import java.util.Date;
import java.util.HashSet;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.gwt.charts.client.ChartLoader;
import com.googlecode.gwt.charts.client.ChartPackage;
import com.googlecode.gwt.charts.client.ColumnType;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.corechart.LineChart;
import com.googlecode.gwt.charts.client.corechart.LineChartOptions;
import com.googlecode.gwt.charts.client.options.HAxis;
import com.googlecode.gwt.charts.client.options.VAxis;
import com.ihsinformatics.minetbdashboard.shared.CollectionsUtil;
import com.ihsinformatics.minetbdashboard.shared.CustomMessage;
import com.ihsinformatics.minetbdashboard.shared.DataType;
import com.ihsinformatics.minetbdashboard.shared.ErrorType;
import com.ihsinformatics.minetbdashboard.shared.InfoType;
import com.ihsinformatics.minetbdashboard.shared.LocationDimension;
import com.ihsinformatics.minetbdashboard.shared.MineTB;
import com.ihsinformatics.minetbdashboard.shared.Parameter;
import com.ihsinformatics.minetbdashboard.shared.TimeDimenstion;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Minetbdashboard implements EntryPoint, ClickHandler,
		KeyDownHandler, ChangeHandler {
	private static ServerServiceAsync service = GWT.create(ServerService.class);

	private static LoadingWidget loading = new LoadingWidget();
	private static RootPanel rootPanel;

	static VerticalPanel verticalPanel = new VerticalPanel();
	private FlexTable headerFlexTable = new FlexTable();
	private FlexTable loginFlexTable = new FlexTable();
	private FlexTable optionsTable = new FlexTable();
	private FlexTable dateFilterTable = new FlexTable();

	private Label formHeadingLabel = new Label("USER AUTHENTICATION");
	private Label userNameLabel = new Label("User ID:");
	private Label passwordLabel = new Label("Password:");

	private TextBox userTextBox = new TextBox();
	private PasswordTextBox passwordTextBox = new PasswordTextBox();

	private ListBox reportsList = new ListBox();
	private ListBox locationDimensionList = new ListBox();
	private ListBox timeDimensionList = new ListBox();
	
	private ListBox yearFrom = new ListBox();
	private ListBox yearTo = new ListBox();
	private ListBox quarterFrom = new ListBox();
	private ListBox quarterTo = new ListBox();
	private ListBox monthFrom = new ListBox();
	private ListBox monthTo = new ListBox();
	private ListBox weekFrom = new ListBox();
	private ListBox weekTo = new ListBox();

//	private DateBox fromDateBox = new DateBox();
//	private DateBox toDateBox = new DateBox();

	private Button loginButton = new Button("Login");
	private Button showButton = new Button("Show Report");
	private Button clearButton = new Button("Clear");

	/* Chart objects */
	ChartLoader chartLoader;
	private VerticalPanel chartPanel = new VerticalPanel();

	/**
	 * This is the entry point method.
	 */
	@Override
	public void onModuleLoad() {
		rootPanel = RootPanel.get();
		rootPanel.setStyleName("rootPanel");
		rootPanel.setSize("1080px", "50%");
		// verticalPanel.addStyleName("verticalPanel");
		rootPanel.add(verticalPanel);
		headerFlexTable.setWidget(0, 1, formHeadingLabel);
		headerFlexTable.getRowFormatter().addStyleName(0, "MineTBHeader");
		headerFlexTable.setSize("100%", "");

		// loginFlexTable.setBorderWidth(2);
		loginFlexTable.setWidget(1, 0, userNameLabel);
		userNameLabel.addStyleName("text");

		loginFlexTable.setWidget(1, 1, userTextBox);
		userTextBox.setAlignment(TextAlignment.JUSTIFY);
		userTextBox.addStyleName("textbox");

		loginFlexTable.setWidget(2, 0, passwordLabel);
		passwordLabel.addStyleName("text");

		loginFlexTable.setWidget(2, 1, passwordTextBox);
		passwordTextBox.setWidth("200");
		passwordTextBox.addStyleName("textbox");
		// loginButton.setStyleName("button:active");
		loginButton.setStyleName("submitButton");

		loginFlexTable.setWidget(3, 1, loginButton);
		loginButton.setSize("169", "30");

		loginFlexTable.setStyleName("flexTableCell");

		verticalPanel.add(headerFlexTable);
		verticalPanel.setCellHorizontalAlignment(headerFlexTable,
				HasHorizontalAlignment.ALIGN_CENTER);

		verticalPanel.add(loginFlexTable);
		verticalPanel.setSize("1000px", "");
		verticalPanel
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.setCellHorizontalAlignment(loginFlexTable,
				HasHorizontalAlignment.ALIGN_CENTER);
		chartPanel.setSize("1024px", "");
		loginFlexTable.setSize("100%", "");
		loginFlexTable.getCellFormatter().setHorizontalAlignment(1, 0,
				HasHorizontalAlignment.ALIGN_RIGHT);
		loginFlexTable.getCellFormatter().setHorizontalAlignment(2, 0,
				HasHorizontalAlignment.ALIGN_RIGHT);
		loginFlexTable.getCellFormatter().setHorizontalAlignment(0, 0,
				HasHorizontalAlignment.ALIGN_RIGHT);
		loginFlexTable.getCellFormatter().setVerticalAlignment(1, 1,
				HasVerticalAlignment.ALIGN_MIDDLE);
		loginFlexTable.getCellFormatter().setVerticalAlignment(0, 1,
				HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel.setBorderWidth(1);
		
		optionsTable.setWidget(0, 0, new Label("Report:"));
		optionsTable.setWidget(0, 1, reportsList);
		optionsTable.setWidget(1, 0, new Label("Reporting Level:"));
		optionsTable.setWidget(1, 1, locationDimensionList);
		optionsTable.setWidget(2, 0, new Label("Grouping:"));
		optionsTable.setWidget(2, 1, timeDimensionList);
		optionsTable.setWidget(3, 0, new Label("Select Date Range:"));
		optionsTable.setWidget(3, 1, dateFilterTable);

		fillLists();
		loginButton.addClickHandler(this);
		showButton.addClickHandler(this);
		clearButton.addClickHandler(this);
		passwordTextBox.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				boolean enterPressed = event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER;
				if (enterPressed) {
					loginButton.click();
				}
			}
		});
		timeDimensionList.addChangeHandler(this);
		createDateFilterWidgets(TimeDimenstion.YEAR);
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

	@SuppressWarnings("deprecation")
	public void fillLists() {
		String[] reports = { "Screening", "Presumptive & High Risk",
				"Submission", "Pending", "MTB Positive", "RIF Resistant",
				"Negative", "Error", "Rejected", "No Result",
				"Sensitive Cases Initiation", "MDR Cases Initiation" };
		for (String str : reports) {
			reportsList.addItem(str);
		}
		for (LocationDimension dim : LocationDimension.values()) {
			locationDimensionList.addItem(dim.toString());
		}
		for (TimeDimenstion dim : TimeDimenstion.values()) {
			timeDimensionList.addItem(dim.toString());
		}
		for (int year = 2013; year <= new Date().getYear() + 1900; year++) {
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
		// TODO: ONLY FOR TESTING
		userTextBox.setText("owais");
		passwordTextBox.setText("Jingle94$");
	}

	public void drawChart() {
		String report = MineTBClient.get(reportsList);
		if (report.equals("Screening")) {
			drawScreening();
		}
	}
	
	/**
	 * Applies filter picked from date fields w.r.t. selected time dimension
	 * @param params
	 * @return
	 */
	private String getFilter(Parameter[] params) {
		StringBuilder where = new StringBuilder(" where 1 = 1 ");
		// Append Date filter
		String yFrom = MineTBClient.get(yearFrom);
		String yTo = MineTBClient.get(yearTo);
		String qFrom = MineTBClient.get(quarterFrom);
		String qTo = MineTBClient.get(quarterTo);
		String mFrom = MineTBClient.get(monthFrom);
		String mTo = MineTBClient.get(monthTo);
		String wFrom = MineTBClient.get(weekFrom);
		String wTo = MineTBClient.get(weekTo);
		TimeDimenstion time = TimeDimenstion.valueOf(MineTBClient.get(timeDimensionList));
		switch(time) {
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
		if (params != null) {
			for (Parameter param : params) {
				DataType type = param.getType();
				switch (type) {
				case CHAR:
				case STRING:
					where.append(" and " + param.getName());
					where.append(" = '" + param.getValue() + "'");
					break;
				default:
					where.append(" and " + param.getName());
					where.append(" = " + param.getValue());
				}
			}
		}
		return where.toString();
	}
	
	/**
	 * Return set of unique values from given data array
	 * @param data
	 * @param columnIndex
	 * @return
	 */
	private String[] getUniqueValues(String[][] data, int columnIndex) {
		HashSet<String> values = new HashSet<String>();
		for (String[] record : data) {
			values.add(record[columnIndex]);
		}
		String[] array = values.toArray(new String[] {});
		return array;
	}
	
	private double findValueInData(String[][] data, String columnValue, String rowValue, int valueIndex) {
		double value = 0;
		for (int i = 0; i < data.length; i++) {
			if (data[i][1].equals(columnValue) && data[i][0].equals(rowValue)) {
				value = Double.parseDouble(data[i][valueIndex]);
			}
		}
		return value;
	}
	
	private void drawLineChart(String[][] data, int valueIndex, String title, String xLabel, String yLabel) {
		DataTable dataTable = DataTable.create();
		String[] timesStr = getUniqueValues(data, 0);
		Double[] times = CollectionsUtil.convertToNumeric(timesStr);
		times = CollectionsUtil.sortArray(times);
		String[] locations = getUniqueValues(data, 1);
		// Add grouping column for time dimension
		dataTable.addColumn(ColumnType.NUMBER, MineTBClient.get(timeDimensionList).toLowerCase());
		// Add number of rows equal to unique time dimensions
		dataTable.addRows(times.length);
		// Add locations as columns
		for (String location : locations) {
			dataTable.addColumn(ColumnType.NUMBER, location);
		}
		for (int i = 0; i < times.length; i++) {
			dataTable.setValue(i, 0, times[i].intValue());
		}
		// Convert values into 2D; 1st dimension is locations, 2nd is time
		for (int col = 0; col < locations.length; col++) {
			for (int row = 0; row < times.length; row++) {
				double value = findValueInData(data, locations[col], String.valueOf(times[row].intValue()), valueIndex);
				dataTable.setValue(row, col + 1, value);
			}
		}
		// Set options
		LineChartOptions options = LineChartOptions.create();
		options.setBackgroundColor("#f0f0f0");
		options.setTitle(title);
		options.setHAxis(HAxis.create(xLabel));
		options.setVAxis(VAxis.create(yLabel));
		// Screenings
		LineChart lineChart = new LineChart();
		HTML line = new HTML("<hr  style=\"width:100%;\" />");
		// Draw a line break
		chartPanel.add(line);
		lineChart.draw(dataTable, options);
		chartPanel.add(lineChart);
		// Draw another line break
		chartPanel.add(line);
	}
	
	private void drawScreening() {
		final String location = MineTBClient.get(locationDimensionList).toLowerCase();
		final String time = MineTBClient.get(timeDimensionList).toLowerCase();
		Parameter[] params = null;
		StringBuilder query = new StringBuilder();
		query.append("select " + time + ", ");
		query.append(location + ", ");
		query.append("sum(screened) as screened, sum(suspects) as suspects, sum(non_suspects) as non_suspects from fact_screening ");
		query.append(getFilter(params));
		query.append(" group by " + time + ", " + location);
		query.append(" order by " + time + ", " + location);
		try {
			service.getTableData(query.toString(), new AsyncCallback<String[][]>() {
				@Override
				public void onSuccess(final String[][] result) {
					ChartLoader chartLoader = new ChartLoader(ChartPackage.CORECHART);
					chartLoader.loadApi(new Runnable() {
						@Override
						public void run() {
							String title = "Screening by " + location + " per " + time;
							chartPanel.clear();
							drawLineChart(result, 2, title, time, "SCREENED");
							drawLineChart(result, 3, title, time, "PRESUMPTIVE");
							drawLineChart(result, 4, title, time, "NON-SUSPECTS");
							load(false);
						}
					});
				}
				
				@Override
				public void onFailure(Throwable caught) {
					Window.alert(CustomMessage.getErrorMessage(ErrorType.DATA_ACCESS_ERROR));
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void drawTreatmentInitiationQuery() {
	}

	public void drawSputumResultFaultQuery() {
	}

	public void drawNegativeResultQuery() {
	}

	public void drawRifResistantQuery() {
	}

	public void drawMtbPositiveQuery() {
	}

	public void drawSputumPendingQuery() {
	}

	public void drawSputumSubmissionQuery() {
	}

	public void drawSuspectQuery() {
	}

	/**
	 * Display/Hide main panel and loading widget
	 * 
	 * @param status
	 */
	public void load(boolean status) {
		verticalPanel.setVisible(!status);
		if (status)
			loading.show();
		else
			loading.hide();
	}

	/**
	 * Set browser cookies
	 */
	public static void setCookies(String userName, String passCode,
			String password) {
		Cookies.removeCookie("UserName");
		Cookies.removeCookie("Pass");
		Cookies.removeCookie("LoginTime");
		Cookies.removeCookie("SessionLimit");

		MineTB.setCurrentUser(userName);
		MineTB.setPassCode(password);
		if (!userName.equals(""))
			Cookies.setCookie("UserName", MineTB.getCurrentUser());
		if (!password.equals(""))
			Cookies.setCookie("Password", MineTB.getPassCode());
		if (!passCode.equals("")) {
			Cookies.setCookie("Pass", MineTB.getPassCode());
			Cookies.setCookie("LoginTime", String.valueOf(new Date().getTime()));
			Cookies.setCookie("SessionLimit",
					String.valueOf(new Date().getTime() + MineTB.sessionLimit));
		}
	}

	/**
	 * To renew browsing session
	 * 
	 * @return true if renew was successful
	 */
	public static boolean renewSession() {
		String passcode = Window
				.prompt(CustomMessage
						.getErrorMessage(ErrorType.SESSION_EXPIRED)
						+ "\n"
						+ "Please enter first 4 characters of your password to renew session.",
						"");
		if (MineTBClient.verifyClientPasscode(passcode)) {
			Window.alert(CustomMessage.getInfoMessage(InfoType.SESSION_RENEWED));
			return true;
		}
		Window.alert(CustomMessage
				.getErrorMessage(ErrorType.AUTHENTICATION_ERROR));
		return false;
	}

	private void doLogin() {
		// Check for empty fields
		if (MineTBClient.get(userTextBox).equals("")
				|| MineTBClient.get(passwordTextBox).equals("")) {
			Window.alert(CustomMessage
					.getErrorMessage(ErrorType.EMPTY_DATA_ERROR));
			return;
		}
		try {
			service.authenticate(MineTBClient.get(userTextBox),
					MineTBClient.get(passwordTextBox),
					new AsyncCallback<Boolean>() {
						@Override
						public void onSuccess(Boolean result) {
							if (result) {
								Window.alert(CustomMessage
										.getInfoMessage(InfoType.ACCESS_GRANTED));
								setCookies(MineTBClient.get(userTextBox),
										String.valueOf(MineTBClient
												.getSimpleCode(MineTBClient
														.get(passwordTextBox)
														.substring(0, 3))),
										MineTBClient.get(passwordTextBox));
								login();
							} else {
								Window.alert(CustomMessage
										.getErrorMessage(ErrorType.AUTHENTICATION_ERROR));
								load(false);
							}
						}

						@Override
						public void onFailure(Throwable caught) {
							caught.printStackTrace();
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handle User Login. If user is already logged in, main menu will display
	 * otherwise session renewal window will appear
	 */
	private void login() {
		String userName;
		String passCode;
		String password;
		String sessionLimit;
		try {
			// Try to get Cookies
			userName = Cookies.getCookie("UserName");
			passCode = Cookies.getCookie("Pass");
			password = Cookies.getCookie("Password");
			sessionLimit = Cookies.getCookie("SessionLimit");
			if (userName == null || passCode == null || sessionLimit == null)
				throw new Exception();
			userTextBox.setText(userName);

			// If session is expired then renew
			if (new Date().getTime() > Long.parseLong(sessionLimit))
				if (!renewSession())
					throw new Exception();
			setCookies(userName, passCode, password);
			service.setCurrentUser(userName, new AsyncCallback<Void>() {
				public void onSuccess(Void result) {
					verticalPanel.clear();
					verticalPanel.add(optionsTable);
					HorizontalPanel buttonsPanel = new HorizontalPanel();
					buttonsPanel.add(showButton);
					buttonsPanel.add(clearButton);
					verticalPanel.add(buttonsPanel);
					verticalPanel.add(chartPanel);
					load(false);
				}

				public void onFailure(Throwable caught) {
					caught.printStackTrace();
					load(false);
				}
			});
		} catch (Exception e) {
			loginFlexTable.setVisible(true);
			load(false);
		}
	}

	/**
	 * Log out the application
	 */
	public static void logout() {
		try {
			flushAll();
			setCookies("", "", "");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Remove all widgets from application
	 */
	public static void flushAll() {
		rootPanel.clear();
		rootPanel
				.add(new HTML(
						"Application has been shut down. It is now safe to close the Browser window."));
	}

	@Override
	public void onClick(ClickEvent event) {
		Widget sender = (Widget) event.getSource();
		load(true);
		if (sender == loginButton) {
			doLogin();
		} else if (sender == showButton) {
			drawChart();
		} else if (sender == clearButton) {
			load(false);
			chartPanel.clear();
		}
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		Object source = event.getSource();
		if (source == passwordTextBox || source == userTextBox) {
			doLogin();
		}
	}

	@Override
	public void onChange(ChangeEvent event) {
		Object source = event.getSource();
		if (source == timeDimensionList) {
			TimeDimenstion time = TimeDimenstion.valueOf(MineTBClient.get(timeDimensionList));
			createDateFilterWidgets(time);
		}
	}
}
