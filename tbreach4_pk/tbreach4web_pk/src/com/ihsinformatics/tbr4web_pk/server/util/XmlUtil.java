
package com.ihsinformatics.tbr4web_pk.server.util;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class XmlUtil
{

	public static String docToString (Document doc)
	{
		TransformerFactory factory = TransformerFactory.newInstance ();
		Transformer transformer;
		try
		{
			transformer = factory.newTransformer ();
		}
		catch (TransformerConfigurationException e)
		{
			e.printStackTrace ();
			return "";
		}
		StringWriter writer = new StringWriter ();
		Result result = new StreamResult (writer);
		Source source = new DOMSource (doc);
		try
		{
			transformer.transform (source, result);
		}
		catch (TransformerException e)
		{
			e.printStackTrace ();
		}
		try
		{
			writer.close ();
		}
		catch (IOException e)
		{
			e.printStackTrace ();
		}
		String xml = writer.toString ();
		return xml;
	}

	public static String createSuccessXml ()
	{
		String xml = null;
		Document doc = null;

		try
		{
			doc = DocumentBuilderFactory.newInstance ().newDocumentBuilder ().newDocument ();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace ();
			return "";
		}
		Element responseNode = doc.createElement ("response");
		// responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);

		Element statusNode = doc.createElement ("status");
		Text statusValue = doc.createTextNode ("success");
		statusNode.appendChild (statusValue);

		responseNode.appendChild (statusNode);

		doc.appendChild (responseNode);

		xml = docToString (doc);
		System.out.println (xml + "this is this the xml returned");
		return xml;

	}

	public static String createRIFSuccessXml ()
	{
		String xml = null;
		Document doc = null;

		try
		{
			doc = DocumentBuilderFactory.newInstance ().newDocumentBuilder ().newDocument ();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace ();
			return "";
		}
		Element responseNode = doc.createElement ("response");
		// responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);

		Element statusNode = doc.createElement ("status");
		Text statusValue = doc.createTextNode ("RIFSUCCESS");
		statusNode.appendChild (statusValue);

		responseNode.appendChild (statusNode);

		doc.appendChild (responseNode);

		xml = docToString (doc);
		System.out.println (xml + "this is this the xml returned");
		return xml;

	}

	public static String createErrorXml (String errMsg)
	{
		String xml = null;
		Document doc = null;

		try
		{
			doc = DocumentBuilderFactory.newInstance ().newDocumentBuilder ().newDocument ();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace ();
			return "";
		}
		Element responseNode = doc.createElement ("response");
		// responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);

		Element statusNode = doc.createElement ("status");
		Text statusValue = doc.createTextNode ("error");
		statusNode.appendChild (statusValue);

		Element msgNode = doc.createElement ("msg");
		Text msgValue = doc.createTextNode (errMsg);
		msgNode.appendChild (msgValue);

		responseNode.appendChild (statusNode);
		responseNode.appendChild (msgNode);

		doc.appendChild (responseNode);

		xml = docToString (doc);
		return xml;
	}

}
