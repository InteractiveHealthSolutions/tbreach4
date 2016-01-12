package com.ihsinformatics.minetbdashboard.client;

import java.util.Date;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.gwt.charts.client.ChartLoader;
import com.googlecode.gwt.charts.client.ChartPackage;
import com.googlecode.gwt.charts.client.ColumnType;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.corechart.CoreChartWidget;
import com.googlecode.gwt.charts.client.corechart.LineChart;
import com.googlecode.gwt.charts.client.corechart.LineChartOptions;
import com.googlecode.gwt.charts.client.options.HAxis;
import com.googlecode.gwt.charts.client.options.PointShapeType;
import com.googlecode.gwt.charts.client.options.TitlePosition;
import com.googlecode.gwt.charts.client.options.VAxis;
import com.ihsinformatics.minetbdashboard.shared.CustomMessage;
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
		KeyDownHandler {
	private static ServerServiceAsync service = GWT.create(ServerService.class);

	private static LoadingWidget loading = new LoadingWidget();
	private static RootPanel rootPanel;

	static VerticalPanel verticalPanel = new VerticalPanel();
	private FlexTable headerFlexTable = new FlexTable();
	private FlexTable loginFlexTable = new FlexTable();
	private FlexTable optionsTable = new FlexTable();

	private Label formHeadingLabel = new Label("USER AUTHENTICATION");
	private Label userNameLabel = new Label("User ID:");
	private Label passwordLabel = new Label("Password:");

	private TextBox userTextBox = new TextBox();
	private PasswordTextBox passwordTextBox = new PasswordTextBox();

	private ListBox reportsList = new ListBox();
	private ListBox locationDimensionList = new ListBox();
	private ListBox timeDimensionList = new ListBox();

	private Button loginButton = new Button("Login");
	private Button showButton = new Button("Show Report");

	/* Chart objects */
	ChartLoader chartLoader;
	private SimpleLayoutPanel layoutPanel;

	/**
	 * This is the entry point method.
	 */
	@Override
	public void onModuleLoad() {
		rootPanel = RootPanel.get();
		rootPanel.setStyleName("rootPanel");
		rootPanel.setSize("1000px", "50%");
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

		fillLists();
		optionsTable.setWidget(0, 0, new Label("Report:"));
		optionsTable.setWidget(0, 1, reportsList);
		optionsTable.setWidget(1, 0, new Label("Reporting Level:"));
		optionsTable.setWidget(1, 1, locationDimensionList);
		optionsTable.setWidget(2, 0, new Label("Grouping:"));
		optionsTable.setWidget(2, 1, timeDimensionList);

		loginButton.addClickHandler(this);
		showButton.addClickHandler(this);
		passwordTextBox.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				boolean enterPressed = event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER;
				if (enterPressed) {
					loginButton.click();
				}
			}
		});
	}

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
	
	private void drawScreening() {
		final String location = MineTBClient.get(locationDimensionList);
		final String time = MineTBClient.get(timeDimensionList);
		Parameter[] params = null;

		StringBuilder query = new StringBuilder("select 'Test Strategy' as strategy, location_name as facility, month(date_entered) as month, sum(if(haemoptysis='Yes', 1, 0)) as suspects, sum(if(haemoptysis='No', 1, 0)) as non_suspects from sz_dw.om_enc_screening where year(date_entered) = 2015 group by location_name , month(date_entered) having suspects > 0");
		try {
			service.getTableData(query.toString(), new AsyncCallback<String[][]>() {
				@Override
				public void onSuccess(final String[][] result) {
					ChartLoader chartLoader = new ChartLoader(ChartPackage.CORECHART);
					chartLoader.loadApi(new Runnable() {
						@Override
						public void run() {
							if (layoutPanel == null) {
								layoutPanel = new SimpleLayoutPanel();
							}
							LineChart chart = new LineChart();
							String[] locationNames = new String[] { "01000 Ugu", "02000 eThekwini" };
							int[] months = new int[] { 2012, 2013, 2014, 2015, 2016 };
							double[][] values = new double[][] { { 1336, 1538, 1576, 1600, 19 }, { 979, 916, 997, 941, 9 } };

							DataTable dataTable = DataTable.create();
							dataTable.addColumn(ColumnType.STRING, time);
							for (int i = 0; i < locationNames.length; i++) {
								dataTable.addColumn(ColumnType.NUMBER, locationNames[i]);
							}
							dataTable.addRows(months.length);
							for (int i = 0; i < months.length; i++) {
								dataTable.setValue(i, 0, String.valueOf(months[i]));
							}
							for (int col = 0; col < values.length; col++) {
								for (int row = 0; row < values[col].length; row++) {
									dataTable.setValue(row, col + 1, values[col][row]);
								}
							}
							// Set options
							LineChartOptions options = LineChartOptions.create();
							options.setBackgroundColor("#f0f0f0");
							options.setTitle("Screening by " + location + " per " + time);
							options.setHAxis(HAxis.create(time));
							options.setVAxis(VAxis.create(location));
							options.setPointShape(PointShapeType.DIAMOND);
							options.setTitlePosition(TitlePosition.OUT);
							verticalPanel.add(chart);
							chart.draw(dataTable);
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
					verticalPanel.add(showButton);
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
		}
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		Object source = event.getSource();
		if (source == passwordTextBox || source == userTextBox) {
			doLogin();
		}
	}
}
