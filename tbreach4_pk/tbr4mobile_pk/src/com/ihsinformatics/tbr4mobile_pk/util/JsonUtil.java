/**
 * This class reads and writes JSON objects/arrays
 */

package com.ihsinformatics.tbr4mobile_pk.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class JsonUtil
{
	private static final String	TAG	= "JSONParser";

	public static JSONObject getJSONObject (String jsonText)
	{
		// try parse the string to a JSON object
		try
		{
			JSONObject jsonObj = new JSONObject (jsonText);
			return jsonObj;
		}
		catch (JSONException e)
		{
			Log.e (TAG, "Error parsing data. " + e.getMessage ());
			return null;
		}
	}

	public static JSONObject[] getJSONArrayFromObject (JSONObject jsonObj, String arrayElement)
	{
		try
		{
			JSONArray jsonArray = jsonObj.getJSONArray (arrayElement);
			JSONObject[] jsonObjects = new JSONObject[jsonArray.length ()];
			for (int i = 0; i < jsonArray.length (); i++)
			{
				jsonObjects[i] = JsonUtil.getJSONObject (jsonArray.getString (i));
			}
			return jsonObjects;
		}
		catch (JSONException e)
		{
			Log.e (TAG, "Error parsing array from JSON Object using element \'" + arrayElement + "\'. " + e.getMessage ());
			return null;
		}
	}

	/**
	 * Encodes a Json object into string encoded in UTF-8. Used to pass Json
	 * data in query string
	 * 
	 * @param json
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String getEncodedJson (JSONObject json) throws UnsupportedEncodingException
	{
		return URLEncoder.encode (json.toString (), "UTF-8");
	}
}
