package com.ihsinformatics.tbr4reporterweb.client;

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
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.ihsinformatics.tbr4reporterweb.shared.CustomMessage;
import com.ihsinformatics.tbr4reporterweb.shared.ErrorType;
import com.ihsinformatics.tbr4reporterweb.shared.InfoType;
import com.ihsinformatics.tbr4reporterweb.shared.TBR4;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Tbr4reporterweb implements EntryPoint, ClickHandler,
		KeyDownHandler {
	private static ServerServiceAsync service = GWT.create(ServerService.class);

	private static LoadingWidget loading = new LoadingWidget();
	private static RootPanel rootPanel;

	static VerticalPanel verticalPanel = new VerticalPanel();
	private FlexTable headerFlexTable = new FlexTable();
	private FlexTable loginFlexTable = new FlexTable();

	private Label formHeadingLabel = new Label("USER AUTHENTICATION");
	private Label userNameLabel = new Label("User ID:");
	private Label passwordLabel = new Label("Password:");

	private TextBox userTextBox = new TextBox();
	private PasswordTextBox passwordTextBox = new PasswordTextBox();

	private Button loginButton = new Button("Login");

	private final ReportsComposite reportsComposite = new ReportsComposite();

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		rootPanel = RootPanel.get();
		rootPanel.setStyleName("rootPanel");
		rootPanel.setSize("800px", "50%");
		// verticalPanel.addStyleName("verticalPanel");
		rootPanel.add(verticalPanel);
		headerFlexTable.setWidget(0, 1, formHeadingLabel);
		headerFlexTable.getRowFormatter().addStyleName(0, "TBR3Header");
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
		verticalPanel.setSize("800px", "");
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

		// verticalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel.setBorderWidth(1);
		loginButton.addClickHandler(this);
		passwordTextBox.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				boolean enterPressed = event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER;
				if (enterPressed) {
					loginButton.click();
				}
			}
		});
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

		TBR4.setCurrentUser(userName);
		TBR4.setPassCode(password);
		if (!userName.equals(""))
			Cookies.setCookie("UserName", TBR4.getCurrentUser());
		if (!password.equals(""))
			Cookies.setCookie("Password", TBR4.getPassCode());
		if (!passCode.equals("")) {
			Cookies.setCookie("Pass", TBR4.getPassCode());
			Cookies.setCookie("LoginTime", String.valueOf(new Date().getTime()));
			Cookies.setCookie("SessionLimit",
					String.valueOf(new Date().getTime() + TBR4.sessionLimit));
		}
	}

	private void doLogin() {
		// Check for empty fields
		if (TbrClient.get(userTextBox).equals("")
				|| TbrClient.get(passwordTextBox).equals("")) {
			Window.alert(CustomMessage
					.getErrorMessage(ErrorType.EMPTY_DATA_ERROR));
			return;
		}
		try {
			service.authenticate(TbrClient.get(userTextBox),
					TbrClient.get(passwordTextBox),
					new AsyncCallback<Boolean>() {
						@Override
						public void onSuccess(Boolean result) {
							if (result) {
								Window.alert(CustomMessage
										.getInfoMessage(InfoType.ACCESS_GRANTED));
								setCookies(
										TbrClient.get(userTextBox),
										String.valueOf(TbrClient
												.getSimpleCode(TbrClient
														.get(passwordTextBox)
														.substring(0, 3))),
										TbrClient.get(passwordTextBox));
								login();
							} else {
								Window.alert(CustomMessage
										.getErrorMessage(ErrorType.AUTHENTICATION_ERROR));
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
					verticalPanel.add(reportsComposite);
				}

				public void onFailure(Throwable caught) {
					caught.printStackTrace();
				}
			});
		} catch (Exception e) {
			loginFlexTable.setVisible(true);
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
		if (TbrClient.verifyClientPasscode(passcode)) {
			Window.alert(CustomMessage.getInfoMessage(InfoType.SESSION_RENEWED));
			return true;
		}
		Window.alert(CustomMessage
				.getErrorMessage(ErrorType.AUTHENTICATION_ERROR));
		return false;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event
	 * .dom.client.ClickEvent)
	 */
	@Override
	public void onClick(ClickEvent event) {
		Widget sender = (Widget) event.getSource();
		if (sender == loginButton) {
			doLogin();
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
