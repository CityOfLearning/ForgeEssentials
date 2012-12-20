package com.ForgeEssentials.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.ForgeEssentials.data.TaggedClass.SavedField;
import com.ForgeEssentials.util.OutputHandler;

public class SQLiteDataDriver extends DataDriver
{
	private String DriverClass = "org.sqlite.JDBC";
	private Connection dbConnection;
	private HashMap<Class, Boolean> classTableChecked = new HashMap<Class, Boolean>();
	
	// Default constructor is good enough for us.

	@Override
	public void parseConfigs(Configuration config, String worldName) throws SQLException, ClassNotFoundException
	{
		String type;

		// Set up the SQLite connection.
		Property prop = config.get("Data.SQLite", "dataFile", "ForgeEssentials/sqlite.db");
		prop.comment = "Path to the SQLite database file (only use leading slashes for an absolute path)";
		String path = prop.value;
		
		// Save any additional categories we may have created.
		config.save();

		try
		{
			Class driverClass = Class.forName(DriverClass);

			this.dbConnection = DriverManager.getConnection("jdbc:sqlite:" + path);
		}
		catch (SQLException e)
		{
			OutputHandler.SOP("Unable to connect to the database!");
			throw e;
		}
		catch (ClassNotFoundException e)
		{
			OutputHandler.SOP("Could not load the SQLite JDBC Driver! Does it exist in the lib directory?");
			throw e;
		}
	}

	@Override
	public void onClassRegisterred(TypeTagger tagger)
	{
		// If this is the first time registering a class that is NOT saved inline,
		//  attempt to create a table.
		if (!(tagger.inLine || this.classTableChecked.containsKey(tagger.forType)))
			this.createTable(tagger.forType);
	}

	@Override
	protected boolean saveData(Class type, TaggedClass fieldList)
	{
		boolean isSuccess = false;

		return isSuccess;
	}

	@Override
	protected TaggedClass loadData(Class type, Object uniqueKey)
	{
		TaggedClass reconstructed = null;
		
		return reconstructed;
	}

	@Override
	protected TaggedClass[] loadAll(Class type)
	{
		TaggedClass[] value = null;

		ArrayList<TaggedClass> values = new ArrayList<TaggedClass>();
		
		value = values.toArray(new TaggedClass[values.size()]);

		return value;
	}

	@Override
	protected boolean deleteData(Class type, Object uniqueObjectKey)
	{
		boolean isSuccess = false;

		return isSuccess;
	}
	
	/**
	 * Attempts to create a table to store the type passed to it. These should only be top-level types that
	 * need to be stored, such as PlayerInfo and Zones. Points, WorldPoints and other "simple" types that are
	 * contained within the top-level types will be unrolled automatically.
	 * @param type
	 * @return
	 */
	private boolean createTable(Class type)
	{
		boolean isSuccess = false;
		
		TypeTagger tagger = DataStorageManager.getTaggerForType(type);
		HashMap<String, Class> fields = tagger.getFieldToTypeMap();
		ArrayList<String> tableFields = new ArrayList<String>();
		String keyClause = null;
		
		for (Entry<String, Class> entry : fields.entrySet())
		{			
			if (!TypeTagger.isTypeComplex(entry.getValue()))
			{
				// Simple case. 1 value = 1 column.
				tableFields.add(this.simpleFieldToColumn(entry.getKey(), entry.getValue()));
			}
			else
			{
				// We're going to do some unrolling in here. 1 value = 1+ columns.
				tableFields.addAll(this.complexFieldToColumns(entry.getKey(), entry.getValue()));
			}
		}

		if (tagger.isUniqueKeyField)
		{
			keyClause = "PRIMARY KEY (" + tagger.uniqueKey + ")";
		}
		else
		{
			// Is a method. Extra field required.
			tableFields.add("uniqueIdentifier TEXT");
			keyClause = "PRIMARY KEY (uniqueIdentifier)";
		}
		
		// Build up the create statement
		StringBuilder tableCreate = new StringBuilder("CREATE TABLE " + type.getSimpleName() + " (");
		for (String s : tableFields)
		{
			tableCreate.append(s + ",");
		}
		// Add primary key clause.
		tableCreate.append(keyClause + ")");
		
		try
		{
			// Attempt to execute the statement.
			Statement s = this.dbConnection.createStatement();
			s.executeQuery(tableCreate.toString());
			
			isSuccess = true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return isSuccess;
	}
	
	private String simpleFieldToColumn(String fieldName, Class type)
	{
		String value = null;
		if (type.equals(int.class) || type.equals(Integer.class) ||
				type.equals(boolean.class) || type.equals(Boolean.class))
		{
			value = fieldName + " INTEGER";
		}
		else if (type.equals(float.class) || type.equals(Float.class) ||
				type.equals(double.class) || type.equals(Double.class))
		{
			value = fieldName + " REAL";
		}
		else if (type.equals(String.class) || type.equals(double[].class) ||
				type.equals(int[].class) || type.equals(boolean[].class) ||
				type.equals(String[].class))
		{
			// We are going to roll arrays up into arbitrary long text fields.
			value = fieldName + " TEXT";
		}
		else
		{
			// Unsupported. This will probably be crazy.
			value = fieldName + " BLOB";
		}
		return value;
	}
	
	private ArrayList<String> complexFieldToColumns(String fieldName, Class type)
	{
		ArrayList<String> fields = new ArrayList<String>();
		
		// Complex type we can't handle.
		TypeTagger tagger = DataStorageManager.getTaggerForType(type);
		Iterator<Entry<String, Class>> iterator = tagger.fieldToTypeMap.entrySet().iterator();
		
		// Iterate over the stored fields. Recurse if nessecary.
		while (iterator.hasNext())
		{
			Entry<String, Class> entry = iterator.next();
			if (!TypeTagger.isTypeComplex(entry.getValue()))
			{
				// Simple case. 1 value = 1 column.
				fields.add(this.simpleFieldToColumn(fieldName + "-" + entry.getKey(), entry.getValue()));
			}
			else
			{
				// We're going to do some unrolling in here. 1 value = 1+ columns.
				fields.addAll(this.complexFieldToColumns(fieldName + "-" + entry.getKey(), entry.getValue()));
			}
		}
	
		return fields;
	}
	
	private String valueToFieldEntry(String fieldName, Class type, Object value)
	{
		String data = null;
		if (type.equals(Integer.class) || type.equals(Boolean.class) || type.equals(Float.class) ||
				type.equals(Double.class) ||type.equals(String.class))
		{
			data = value.toString();
		}
		else if (type.equals(double[].class) && ((double[])value).length > 0)
		{
			double[] arr = (double[])value;
			data = String.valueOf(arr[0]);
			for (int i = 1; i < arr.length; ++i)
			{
				data = data + "," + String.valueOf(arr[i]);
			}
		}
		else if (type.equals(int[].class) && ((int[])value).length > 0)
		{
			int[] arr = (int[])value;
			data = String.valueOf(arr[0]);
			for (int i = 1; i < arr.length; ++i)
			{
				data = data + "," + String.valueOf(arr[i]);
			}			
		}
		else if (type.equals(boolean[].class) && ((boolean[])value).length > 0)
		{
			boolean[] arr = (boolean[])value;
			data = String.valueOf(arr[0]);
			for (int i = 1; i < arr.length; ++i)
			{
				data = data + "," + String.valueOf(arr[i]);
			}	
		}
		else if (type.equals(String[].class) && ((String[])value).length > 0)
		{
			String[] arr = (String[])value;
			data = String.valueOf(arr[0]);
			for (int i = 1; i < arr.length; ++i)
			{
				data = data + "!??!" + String.valueOf(arr[i]);
			}				
		}
		else if (type.equals(TaggedClass.class))
		{
			// Tricky business involving recursion.
			TaggedClass tc = (TaggedClass)value;
			data = "";
			
			for (SavedField f : tc.TaggedMembers.values())
			{
				this.valueToFieldEntry(fieldName + "-" + f.name, f.type, f.value);
			}
		}
		else // What the fuck? This will be unpredictable.
		{
			data = value.toString();
		}
		return data;
	}
}
