/*
*   Created by: Fredrik Öberg
*
*   Date: 2019-12-10
*
*   Description: This code was written as part of the course Computer Storage which is run by 
*   KTH Royal Institute of Technology and serves the purpose of making a number of embedded
*   SQL queries to a MySQL-database. 
*/


import java.sql.*;
import java.util.Scanner;

/**
 * The main class of the file.
 */
public class DBJDBCM
{
    static protected Connection connection;
    private String URL = "jdbc:mysql://localhost:3306/labb?serverTimezone=CET";
    private String driver = "com.mysql.jdbc.Driver";
    private String userID = "root";
    private String password = "1234";

    /**
     * Connects to the MySQL database.
     */
    public void connect() {
        try {
            connection = DriverManager.getConnection(URL, userID, password);
            connection.setAutoCommit(false);
			System.out.println("Connected to " + URL + " using "+ driver + "\n");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the entire content of the MySQL database.
     *
     * @throws Exception is thrown when database is not accessible.
     */
    public void showDatabaseContent() throws Exception {

        String query = "select * from bil";
        Statement statement = connection.createStatement();
        ResultSet resultSet;

        resultSet = statement.executeQuery(query);

        System.out.println("Relation 'bil':\n");

        while (resultSet.next()) {
            System.out.print(resultSet.getString("regnr"));
            System.out.print(" " + resultSet.getString("marke"));
            System.out.print(" " + resultSet.getString("farg"));
            System.out.println(" " + resultSet.getString("agare"));
        }

        System.out.println();

        query = "select * from person";
        resultSet = statement.executeQuery(query);

        System.out.println("Relation 'person':\n");

        while (resultSet.next()) {
            System.out.print(resultSet.getString("id"));
            System.out.print(" " + resultSet.getString("fnamn"));
            System.out.print(" " + resultSet.getString("enamn"));
            System.out.println(" " + resultSet.getString("stad"));
        }

        System.out.println();


        statement.close();
    }

    /**
     * Shows the brands of the cars in the MySQL database.
     *
     * @throws Exception is thrown when database is not accessible.
     */
    public void showCarBrands() throws Exception{
        String query;
        ResultSet resultSet;
        Statement statement;

        query = "select distinct marke from bil";

        statement = connection.createStatement();
        resultSet = statement.executeQuery(query);

        System.out.println("Bilmärken i databasen:\n");

        while (resultSet.next())
            System.out.println(resultSet.getString("marke"));


        System.out.println();
        statement.close();
    }

    /**
     * Shows the cars in the MySQl database based on the given city.
     *
     * @param scanner is used for getting user inputs.
     * @throws Exception is thrown when database is not accessible.
     */
    public void showCarsByCity(Scanner scanner) throws Exception{

        String query;
        ResultSet resultSet;
        PreparedStatement preparedStatement;
        String userEntry;

        System.out.print("Ange en stad: ");
        userEntry = scanner.nextLine();
        System.out.println();

        query = "select regnr, marke, farg from bil where agare in (select id from person where stad in(?))";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, userEntry);
        resultSet = preparedStatement.executeQuery();



        if(!resultSet.next())
            System.out.println("Inga bilar ägs i staden " + userEntry);
        else {
            System.out.println("Dessa bilar ägs av någon som bor i " + userEntry + ":\n");

            do{
                System.out.print(resultSet.getString("regnr"));
                System.out.print(" " + resultSet.getString("marke"));
                System.out.println(" " + resultSet.getString("farg"));
            }while (resultSet.next());
        }

        System.out.println();
        preparedStatement.close();

    }

    /**
     * Changes the color of a car based on user-entered parameters.
     *
     * @param scanner is used for getting user inputs.
     * @throws Exception is thrown when database is not accessible.
     */
    public void changeCarColor(Scanner scanner) throws Exception{

        String query = "update bil set farg = ? where regnr = ?;\n";
        PreparedStatement preparedStatement;
        String registrationNumber;
        String color;

        System.out.print("Ange ett registreringsnummer: ");
        registrationNumber = scanner.nextLine();
        System.out.println();

        System.out.print("Ange en färg: ");
        color = scanner.nextLine();
        System.out.println();

        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, color);
        preparedStatement.setString(2, registrationNumber);

        if (0 == preparedStatement.executeUpdate())
            System.out.println("Det fanns ingen bil med registreringsnummer " + registrationNumber);

        System.out.println();
        preparedStatement.close();

    }

    /**
     * Called for when the program is executed.
     *
     * @param argv is an array of user given parameters entered when the program is executed.
     * @throws Exception  is thrown when database is not accessible.
     */
    public static void main(String[] argv) throws Exception {

        DBJDBCM databaseConnectivity = new DBJDBCM();
        Scanner scanner = new Scanner(System.in);

        System.out.println("-------- connect() ---------\n");
        databaseConnectivity.connect();
        System.out.println("-------- showDatabaseContent() ---------\n");
        databaseConnectivity.showDatabaseContent();
        System.out.println("-------- showCarBrands() ---------\n");
        databaseConnectivity.showCarBrands();
        System.out.println("-------- showCarsByCity() ---------\n");
        databaseConnectivity.showCarsByCity(scanner);
        System.out.println("-------- changeCarColor() ---------\n");
        databaseConnectivity.changeCarColor(scanner);

        connection.commit();

        System.out.println("-------- showDataBaseContent() ---------\n");
        databaseConnectivity.showDatabaseContent();

        connection.close();
    }
}
