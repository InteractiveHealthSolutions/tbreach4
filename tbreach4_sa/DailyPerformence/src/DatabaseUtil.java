import com.mysql.jdbc.Connection;

import java.sql.*;



public final class DatabaseUtil {
    public Connection conn;
    public static DatabaseUtil db;
    
    
    private DatabaseUtil() {
        String url= "jdbc:mysql://localhost:3306/"; 
        String dbName = "openmrs_rpt";
        String driver = "com.mysql.jdbc.Driver";
        //String userName = "root";
       String userName = "root";
       // String password = "root";
        String password = "root";
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

	
	/**
	 * 
	 * Execute Query
	 * 
	 * @param query
	 * @return result set
	 */
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
	 * Converts result set to 2D array of strings
	 * 
	 * @param rs
	 * @return String[][]
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