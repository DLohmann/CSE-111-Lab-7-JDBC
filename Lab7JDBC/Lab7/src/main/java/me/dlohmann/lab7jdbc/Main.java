package me.dlohmann.lab7jdbc;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.util.Scanner;

/**
 * Created on 10/24/2018.
 *
 * Btw, you shouldn't name your Main class "Main."
 * Usually you'd name it the same as the project name.
 *
 * @author DLohmann
 */


public class Main
{
    public static int wareHouseKeysAmount = 0;
    public static String insertString = "INSERT INTO warehouse (?, ?, ?, ?, ?, ?);";
    public static void fillWarehouse (Statement stat) throws  SQLException{

        stat.executeUpdate("drop table if exists warehouse");
        stat.executeUpdate(
                "CREATE TABLE warehouse (" +
                    "w_warehousekey decimal(3,0) not null,\n" +
                    "w_name char(25) not null,\n" +
                    "w_supplierkey decimal(2,0) not null,\n" +
                    "w_capacity decimal(6,2) not null,\n" +
                    "w_address varchar(40) not null,\n" +
                    "w_nationkey decimal(2,0) not null" +
                ")"
        );

    }

    public static PreparedStatement promptForEntry  (Scanner userInput, Connection con) throws SQLException{
        System.out.println("Please enter a warehouse entry");

        System.out.print("Name: ");
        String name = userInput.nextLine();

        System.out.print("Supplier: ");
        String supplier = userInput.nextLine();

        System.out.print("Capacity: ");
        int capacity = userInput.nextInt();

        System.out.print("Address: ");
        String address = userInput.nextLine();

        System.out.print("Nation: ");
        String nation = userInput.nextLine();

        int wareHouseKey = wareHouseKeysAmount;
        wareHouseKeysAmount++;

        PreparedStatement ps = con.prepareStatement(insertString);

        ps.setInt    (1, wareHouseKey);
        ps.setString (2, name);
        ps.setString (3, supplier);
        ps.setInt    (4, capacity);
        ps.setString (5, address);
        ps.setString (6, nation);

        return ps;
    }
    
    public static void main(String[] args)
    {
        System.out.println("Welcome to Warehouse Database Manager!");
        try
        {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:sample.db");
            
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
            connection.commit();

            userInput.close();
            connection.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            System.out.println("looks like you executed the wrong jar (you don't want to run the unshaded one!)");
            return;
        }
        System.out.println("Goodbye.\nWasn't that easy?");
    }
}
