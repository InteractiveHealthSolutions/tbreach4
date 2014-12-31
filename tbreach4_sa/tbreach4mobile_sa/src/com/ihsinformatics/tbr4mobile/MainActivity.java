
package com.ihsinformatics.tbr4mobile;

import java.util.Locale;

import com.ihsinformatics.tbr4mobile.shared.AlertType;
import com.ihsinformatics.tbr4mobile.util.DatabaseUtil;
import com.ihsinformatics.tbr4mobile.util.ServerService;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;

public class MainActivity extends Activity
{
	public static final String	TAG	= "MainActivity";
	private static DatabaseUtil	dbUtil;
	public static ServerService	serverService;
	Button						screening;
	Button						screeningLog;

	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		MainActivity.resetPreferences (this);
		// Set theme
		setTheme (App.getTheme ());
		super.onCreate (savedInstanceState);
		serverService = new ServerService (this);
		try
		{
			dbUtil = new DatabaseUtil (this);
			dbUtil.buildDatabase (false);
		}
		catch (Exception e)
		{
			Log.e (TAG, e.getMessage ());
		}
		// Check connection with server
//		if (!serverService.checkInternetConnection ())
//		{
//			AlertDialog alertDialog = App.getAlertDialog (this, AlertType.ERROR, getResources ().getString (R.string.data_connection_error));
//			alertDialog.setTitle (getResources ().getString (R.string.error_title));
//			alertDialog.setButton (AlertDialog.BUTTON_POSITIVE, "OK", new AlertDialog.OnClickListener ()
//			{
//				@Override
//				public void onClick (DialogInterface dialog, int which)
//				{
//					finish ();
//				}
//			});
//			alertDialog.show ();
//		}
//		else
		{
			String versionMatch = "SUCCESS";
			// versionMatch = serverService.matchVersions();
			if (versionMatch.equals ("SUCCESS"))
			{
				// runTests ();
				Intent intent = new Intent (this, LoginActivity.class);
				startActivity (intent);
				finish ();
			}
			else
			{
				AlertDialog alertDialog = App.getAlertDialog (this, AlertType.ERROR, versionMatch);
				alertDialog.setTitle (getResources ().getString (R.string.error_title));
				alertDialog.setButton (AlertDialog.BUTTON_POSITIVE, "OK", new AlertDialog.OnClickListener ()
				{
					@Override
					public void onClick (DialogInterface dialog, int which)
					{
						finish ();
					}
				});
				alertDialog.show ();
			}
		}
	}

	/**
	 * Reads preferences from application preferences and loads into App class
	 * members
	 */
	public static void resetPreferences (Context context)
	{
		PreferenceManager.setDefaultValues (context, R.xml.preferences, false);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences (context);
		App.setServer (preferences.getString (Preferences.SERVER, ""));
		App.setUseSsl (preferences.getBoolean (Preferences.USE_SSL, true));
		App.setUsername (preferences.getString (Preferences.USERNAME, ""));
		App.setPassword (preferences.getString (Preferences.PASSWORD, ""));
		App.setLocation (preferences.getString (Preferences.LOCATION, ""));
		App.setScreeningType (preferences.getString (Preferences.SCREENING_TYPE, ""));
		App.setScreeningStrategy (preferences.getString (Preferences.SCREENING_STRATEGY, ""));
		App.setDistrict (preferences.getString (Preferences.DISTRICT, ""));
		App.setFacility (preferences.getString (Preferences.FACILITY, ""));
		App.setSupportContact (preferences.getString (Preferences.SUPPORT_CONTACT, ""));
		App.setSupportEmail (preferences.getString (Preferences.SUPPORT_EMAIL, ""));
		App.setCity (preferences.getString (Preferences.CITY, ""));
		App.setCountry (preferences.getString (Preferences.COUNTRY, ""));
		App.setDelay (Integer.parseInt (preferences.getString (Preferences.DELAY, "30000")));
		App.setAutoLogin (preferences.getBoolean (Preferences.AUTO_LOGIN, false));
		Locale locale = new Locale (preferences.getString (Preferences.LANGUAGE, "en").substring (0, 2));
		Locale.setDefault (locale);
		Configuration config = new Configuration ();
		config.locale = locale;
		context.getApplicationContext ().getResources ().updateConfiguration (config, null);
		App.setCurrentLocale (locale);
		String version = "0";
		try
		{
			version = context.getPackageManager ().getPackageInfo (context.getPackageName (), 0).versionName;
		}
		catch (NameNotFoundException e)
		{
			e.printStackTrace ();
		}
		App.setVersion (version);
	}

	public void runTests ()
	{
		final String TAG = "Test";
		Log.d (TAG, "Running tests");
	}
}