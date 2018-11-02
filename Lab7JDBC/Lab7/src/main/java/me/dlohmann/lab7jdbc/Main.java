package me.dlohmann.lab7jdbc;

import java.sql.*;

import java.util.Scanner;

/**
 * Created on 10/24/2018.
 * <p>
 * Btw, you shouldn't name your Main class "Main."
 * Usually you'd name it the same as the project name.
 *
 * @author DLohmann
 */


public class Main {

    static boolean debug = true;

	public static void main(String[] args) {
		System.out.println("Welcome to Warehouse Database Manager!");
		try {
			//Connection connection = DriverManager.getConnection("jdbc:sqlite:sample.db");
			Connection connection = DriverManager.getConnection("jdbc:sqlite:../../TPCH.db");
			Statement stat = connection.createStatement();

			//Example student database code
            /*
            stat.executeUpdate("drop table if exists student");
            stat.executeUpdate("create table student (name, university);");
            stat.executeUpdate("insert into student values ('John Smith', 'UC Merced');");
            
            ResultSet rs = stat.executeQuery("select * from people;");
            while (rs.next()) {
                System.out.println("name = " + rs.getString("name"));
                System.out.println("university = " + rs.getString("occupation"));
	        }
	        rs.close();
	        */


			fillWarehouse(stat);



			PreparedStatement ps = promptForEntry(connection);

			//System.out.println("\nPrepared statement is: " + ps);
			//System.out.println("suppkey is: " + ps);

			// check if entry is already in table
			if (ps != null) {   // promptForEntry returns null if the entry is already in the database
                ps.executeUpdate();
            } else {
			    System.out.println("That entry is already in the database!");
            }

			//connection.commit();

			System.out.println("Max warehouse key is: " + getMaxWareHouseKey(connection) + " in the warehouse table!");

			// Test if it has the sample entry
			System.out.println("Warehouse has default entry: " + warehouseHasEntry(connection,
                    "Warehouse#000000001",
                    "Supplier#000000005",
                    100,
                    "10, Blumenstrasse, Berlin",
                    "Germany"
                    ));

            System.out.println("\n\nsupplier with smallest number of warehouses: " + suppWithSmallestNumWarehouses(connection));

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQL state is: " + e.getSQLState());
			return;
		}
		System.out.println("Goodbye.\nWasn't that easy?");
	}


	//public static int wareHouseKeysAmount = 0;
	//public static String insertString = "INSERT INTO warehouse (key, w_name, w_suppkey, w_capacity, w_address, w_nationkey) VALUES (?, ?, ?, ?, ?, ?);";

	public static void fillWarehouse(Statement stat) throws SQLException {

		stat.executeUpdate("drop table if exists warehouse");	// Deletes previous warehouse tables
		stat.executeUpdate(
				"CREATE TABLE warehouse (" +
                        "w_warehousekey decimal(3,0) not null,\n" +     //"w_warehousekey integer primary id autoincrement not null,\n" +
						"w_name char(25) not null,\n" +
						"w_suppkey decimal(2,0) not null,\n" +
						"w_capacity decimal(6,2) not null,\n" +
						"w_address varchar(40) not null,\n" +
						"w_nationkey decimal(2,0) not null" +
						")"
		);

	}

	public static PreparedStatement promptForEntry(Connection con) throws SQLException {

		/*
		"INSERT INTO warehouse (w_warehousekey, w_name, w_suppkey, w_capacity, w_address, w_nationkey) VALUES (?,
		SELECT s_suppkey
		FROM supplier
		WHERE , ?, ?, ?, ?);"
		*/
		
		/*
		INSERT INTO warehouse (w_warehousekey, w_name, w_suppkey, w_capacity, w_address, w_nationkey) VALUES (
			100,    --w_warehousekey decimal(3,0) not null
			"Ruso",    --w_name char(25) not null
			(
				SELECT s_suppkey    --w_suppkey decimal(2,0) not null
				FROM supplier
				WHERE s_name = "Supplier#000000002"
			),
			10,    --w_capacity decimal(6,2) not null
			"Merced",    --w_address varchar(40) not null
			(
				SELECT n_nationkey    --w_nationkey decimal(2,0) not null
				FROM nation
				WHERE n_name = "UNITED STATES"
			) 
		);
		*/
		
		/*
		INSERT INTO warehouse (w_warehousekey, w_name, w_suppkey, w_capacity, w_address, w_nationkey) VALUES (
			?,    --w_warehousekey decimal(3,0) not null
			?,    --w_name char(25) not null
			(
				SELECT s_suppkey    --w_suppkey decimal(2,0) not null
				FROM supplier
				WHERE s_name = ?
			),
			?,    --w_capacity decimal(6,2) not null
			?,    --w_address varchar(40) not null
			(
				SELECT n_nationkey    --w_nationkey decimal(2,0) not null
				FROM nation
				WHERE n_name = ?
			) 
		);
		*/
		
		String insertString = ""+
		"INSERT INTO warehouse (w_warehousekey, w_name, w_suppkey, w_capacity, w_address, w_nationkey) VALUES ( "+
		"	?, "+ //--key decimal(3,0) not null
		"	?, "+ //--w_name char(25) not null
		"	( "+
		"		SELECT s_suppkey "+
		"		FROM supplier "+
		"		WHERE s_name = ? "+ // --w_suppkey decimal(2,0) not null
		"	), "+
		"	?, "+ //--w_capacity decimal(6,2) not null
		"	?, "+ //--w_address varchar(40) not null
		"	("+
		"		SELECT n_nationkey "+
		"		FROM nation "+
		"		WHERE n_name = ? "+ //--w_nationkey decimal(2,0) not null
		"	) "+
		");";
		// TODO: Insert into database only if not exists


		System.out.println("Please enter a warehouse entry");

        Scanner userInput = new Scanner(System.in);

		System.out.print("Name: ");
		String warehouse_name = debug? "Warehouse#000000001" : userInput.nextLine();

		System.out.print("Supplier: ");
		String supplier_name = debug? "Supplier#000000005" : userInput.nextLine();

		System.out.print("Capacity: ");
		int capacity = debug? 100 : Integer.valueOf(userInput.nextLine());

		System.out.print("Address: ");
		String address = debug? "10, Blumenstrasse, Berlin" : userInput.nextLine();

		System.out.print("Nation: ");
		String nation_name = (debug? "Germany" : userInput.nextLine()).toUpperCase();

        userInput.close();


		// If the table already has this entry, then return null
		if (warehouseHasEntry(con, warehouse_name, supplier_name, capacity, address, nation_name)) {
		    return null;
        }

        // Get a unique warehouse ID (greater than the max ID)
		int wareHouseKey = getMaxWareHouseKey(con) + 1;	//wareHouseKeysAmount;
		//int supplierkey = getSupplierKeyOrCreateNew(con);
		//int nationkey = getNationKeyOrCreateNew(con, nation_name);
		//wareHouseKeysAmount++;

		PreparedStatement ps = con.prepareStatement(insertString);

		System.out.println("INSERTING ENTRY:");
		System.out.println ("\nwarehouse_name = " + warehouse_name +
                            "\nsupplier_name = " + supplier_name +
                            "\ncapacity = " + capacity +
                            "\naddress = " + address +
                            "\nnation_name = " + nation_name
        );

		ps.setInt(1, wareHouseKey);
		ps.setString(2, warehouse_name);
		ps.setString(3, supplier_name);
		ps.setInt(4, capacity);
		ps.setString(5, address);
		ps.setString(6, nation_name);

		return ps;
	}

	public static int getMaxWareHouseKey(Connection con) throws SQLException {
		Statement s = con.createStatement();
		ResultSet rs = s.executeQuery("SELECT MAX(w_warehousekey) FROM warehouse;");
		rs.next();
		return rs.getInt("MAX(w_warehousekey)");
	}

	public static boolean warehouseHasEntry (Connection con, String warehouse_name, String supplier_name, int capacity, String address, String nation_name) throws SQLException {
        nation_name = nation_name.toUpperCase();
        System.out.println("\n\nCHECKING FOR ENTRY:");
        System.out.println ("\nwarehouse_name = " + warehouse_name +
                "\nsupplier_name = " + supplier_name +
                "\ncapacity = " + capacity +
                "\naddress = " + address +
                "\nnation_name = " + nation_name
        );

	    String queryStr = "SELECT COUNT (w_warehousekey) AS numResults "+
        "FROM warehouse, supplier, nation "+
        "WHERE w_name = ? AND w_suppkey = s_suppkey AND s_name = ? AND w_capacity = ? AND w_address = ? AND w_nationkey = n_nationkey AND n_name = ?;";

	    PreparedStatement ps = con.prepareStatement(queryStr);
	    ps.setString(1, warehouse_name);
	    ps.setString(2, supplier_name);
        ps.setInt(3, capacity);
	    ps.setString(4, address);
	    ps.setString(5, nation_name.toUpperCase());

	    ResultSet rs = ps.executeQuery();

        rs.next();

        int count = rs.getInt("numResults");

	    return count > 0;
    }

    static String suppWithSmallestNumWarehouses(Connection con) {
        String queryStr = "";
	    return null;
    }

	/*
	public static int getSupplierKeyOrCreateNew (Connection con) throws SQLException {
		return 0;
	}

	public static int getNationKeyOrCreateNew( Connection con) throws  SQLException {
		return 0;
	}
	*/
}
