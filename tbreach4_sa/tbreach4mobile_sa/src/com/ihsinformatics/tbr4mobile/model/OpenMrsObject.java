/**
 * Abstract class to be inherited by other model persistance classes
 */

package com.ihsinformatics.tbr4mobile.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class OpenMrsObject
{
	private String	id;
	private String	type;
	private String	name;

	public OpenMrsObject (String id)
	{
		this.id = id;
	}

	public OpenMrsObject (String id, String type, String name)
	{
		super ();
		this.id = id;
		this.type = type;
		this.name = name;
	}

	public JSONObject getJSONObject ()
	{
		JSONObject jsonObject = new JSONObject ();
		try
		{
			jsonObject.put ("id", id);
			jsonObject.put ("type", type);
			jsonObject.put ("name", name);
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
			jsonObject = null;
		}
		return jsonObject;
	}

	public static OpenMrsObject parseJSONObject (JSONObject json)
	{
		OpenMrsObject openMrsObject = null;
		String id = "";
		String type = "";
		String name = "";
		try
		{
			id = json.getString ("id");
			type = json.getString ("type");
			name = json.getString ("name");
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
			openMrsObject = null;
		}
		openMrsObject = new OpenMrsObject (id, type, name);
		return openMrsObject;
	}

	public String getId ()
	{
		return id;
	}

	public void setId (String id)
	{
		this.id = id;
	}

	public String getType ()
	{
		return type;
	}

	public void setType (String type)
	{
		this.type = type;
	}

	public String getName ()
	{
		return name;
	}

	public void setName (String name)
	{
		this.name = name;
	}

	@Override
	public String toString ()
	{
		return id + ", " + type + ", " + name;
	}

}
