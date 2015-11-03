import com.mysql.jdbc.Connection;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;



public final class DatabaseUtil {
    public Connection conn;
    public static DatabaseUtil db;
    
    
    private DatabaseUtil() {
    	
    	String url= ""; 
        String dbName = "";
        String driver = "";
        String userName = "";
        String password = "";
        
    	Properties prop = new Properties();
    	InputStream input = null;
    	
    	try {

    		input = getClass().getResourceAsStream("/config.properties");

    		// load a properties file
    		prop.load(input);

    		// get the property value and print it out
    		url = prop.getProperty("url");
    		dbName = prop.getProperty("database");
    		userName = prop.getProperty("dbuser");
    		password = prop.getProperty("dbpassword");
    		driver = prop.getProperty("driver");

    	} catch (IOException ex) {
    		ex.printStackTrace();
    	} finally {
    		if (input != null) {
    			try {
    				input.close();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    		}
    	}
    	
        try {
            Class.forName(driver).newInstance();
            this.conn = (Connection)DriverManager.getConnection(url+dbName,userName,password);
        }
        catch (Exception sqle) {
            sqle.printStackTrace();
        }
    }
    /**
     *
     * @return MysqlConnect Database connection object
     */
    public static synchronized DatabaseUtil getDbCon() {
        if ( db == null ) {
            db = new DatabaseUtil();
        }
        return db;
 
    }
   
    /**
	 * Execute native DML query to fetch data
	 * 
	 * @param query
	 * @param parameterValues
	 * @return
	 */
	public boolean execute (String query)
	{
		boolean result = false;
		try
		{
			if (conn.isClosed ())
			{
				if (getDbCon() == null)
				{
					return result;
				}
			}
			Statement statement = conn.createStatement ();
			result = statement.execute (query);
			statement.close ();
		}
		catch (SQLException e)
		{
			e.printStackTrace ();
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * Execute Query
	 * 
	 * @param query
	 * @return result set
	 */
	
	public String[][] executeQuery (String query, String[] parameterValues)
	{
		String[][] result = null;
		try
		{
			if (conn.isClosed ())
			{
				if (getDbCon() == null)
				{
					return result;
				}
			}
			PreparedStatement statement = conn.prepareStatement (query);
			int count = 1;
			if (parameterValues != null)
			{
				for (String s : parameterValues)
				{
					statement.setString (count++, s);
				}
			}
			ResultSet resultSet = statement.executeQuery ();
			resultSet.last ();
			int rows = resultSet.getRow ();
			resultSet.beforeFirst ();
			int columns = resultSet.getMetaData ().getColumnCount ();
			result = new String[rows][columns];
			int i = 0;
			while (resultSet.next ())
			{
				for (int j = 0; j < columns; j++)
				{
					result[i][j] = resultSet.getString (j + 1);
				}
				i++;
			}
			statement.close ();
		}
		catch (SQLException e)
		{
			e.printStackTrace ();
			result = null;
		}
		return result;
	}

	public ResultSet executeQueryResultSet (String query)
	{
		ResultSet result = null;
		try
		{
			if (conn.isClosed ())
			{
				if (getDbCon() == null)
				{
					return result;
				}
			}
			PreparedStatement statement = conn.prepareStatement (query);
			
			result= statement.executeQuery ();

			//statement.close ();
		}
		catch (SQLException e)
		{
			e.printStackTrace ();
			result = null;
		}
		return result;
	} 
	
	/**
	 * 
	 * Execute Query
	 * 
	 * @param query
	 * @return result set
	 */
	public String[][] resultSetToArray(ResultSet rs){
		
		String[][] result = null;
		
		try {
			rs.last ();
			int rows = rs.getRow ();
			rs.beforeFirst ();
			int columns = rs.getMetaData ().getColumnCount ();
			result = new String[rows][columns];
			int i = 0;
			while (rs.next ())
			{
				for (int j = 0; j < columns; j++)
				{
					result[i][j] = rs.getString (j + 1);
				}
				i++;
			}
			
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return result;
		
	}
    
 
}