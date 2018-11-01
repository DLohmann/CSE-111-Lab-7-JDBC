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
			Scanner userInput = new Scanner(System.in);


			PreparedStatement ps = promptForEntry(userInput, connection);
			ps.executeUpdate();
			//connection.commit();

			System.out.println("There are " + (getMaxWareHouseKey(connection)+1) + " entries in the warehouse table!");

			userInput.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQL state is: " + e.getSQLState());
			return;
		}
		System.out.println("Goodbye.\nWasn't that easy?");
	}


	//public static int wareHouseKeysAmount = 0;
	public static String insertString = "INSERT INTO warehouse (key, w_name, w_supplierkey, w_capacity, w_address, w_nationkey) VALUES (?, ?, ?, ?, ?, ?);";

	public static void fillWarehouse(Statement stat) throws SQLException {

		stat.executeUpdate("drop table if exists warehouse");	// Deletes previous warehouse tables
		stat.executeUpdate(
				"CREATE TABLE warehouse (" +
						"key decimal(3,0) not null,\n" +
						"w_name char(25) not null,\n" +
						"w_supplierkey decimal(2,0) not null,\n" +
						"w_capacity decimal(6,2) not null,\n" +
						"w_address varchar(40) not null,\n" +
						"w_nationkey decimal(2,0) not null" +
						")"
		);

	}

	public static PreparedStatement promptForEntry(Scanner userInput, Connection con) throws SQLException {
		System.out.println("Please enter a warehouse entry");

		System.out.print("Name: ");
		String name = userInput.nextLine();

		System.out.print("Supplier: ");
		String supplier = userInput.nextLine();

		System.out.print("Capacity: ");
		int capacity = Integer.valueOf(userInput.nextLine());

		System.out.print("Address: ");
		String address = userInput.nextLine();

		System.out.print("Nation: ");
		String nation = userInput.nextLine();

		int wareHouseKey = getMaxWareHouseKey(con);	//wareHouseKeysAmount;
		int supplierkey = getSupplierKeyOrCreateNew(con);
		int nationkey = getNationKeyOrCreateNew(con);
		//wareHouseKeysAmount++;

		PreparedStatement ps = con.prepareStatement(insertString);

		ps.setInt(1, wareHouseKey);
		ps.setString(2, name);
		ps.setString(3, supplier);
		ps.setInt(4, capacity);
		ps.setString(5, address);
		ps.setString(6, nation);

		return ps;
	}

	public static int getMaxWareHouseKey(Connection con) throws SQLException {
		Statement s = con.createStatement();
		ResultSet rs = s.executeQuery("SELECT MAX(key) FROM warehouse;");
		rs.next();
		return rs.getInt("MAX(key)");
	}

	public static int getSupplierKeyOrCreateNew (Connection con) throws SQLException {
		return 0;
	}

	public static int getNationKeyOrCreateNew( Connection con) throws  SQLException {
		return 0;
	}

}
