/**
 * This class reads and writes JSON objects/arrays
 */

package com.ihsresearch.tbr4web.server.util;

import com.ihsresearch.tbr4web.server.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class JsonUtil
{
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
			e.printStackTrace ();
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
			e.printStackTrace ();
			return null;
		}
	}

	public static JSONObject getJsonError (String errorMessage)
	{
		try
		{
			JSONObject jsonObj = new JSONObject ();
			jsonObj.put ("ERROR", errorMessage);
			return jsonObj;
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
			return null;
		}
	}
	
	public static JSONObject getJsonMessage (String message)
	{
		try
		{
			JSONObject jsonObj = new JSONObject ();
			jsonObj.put ("MESSAGE", message);
			return jsonObj;
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
			return null;
		}
	}
}
