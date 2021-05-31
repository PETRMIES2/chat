package utils.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import javax.sql.DataSource;

public class CleanDatabase {

    private static final String disableForeignKeysSql = "SET FOREIGN_KEY_CHECKS=0;";
    private static final String enableForeignKeysSql = "SET FOREIGN_KEY_CHECKS=1;";
    public void clearDatabase(DataSource datasource) {
        try {

            try (Connection connection = datasource.getConnection()) {
                try (Statement statement = connection.createStatement()) {

                    // Disable FK
                                statement.execute(disableForeignKeysSql);
                    // This clears everything but next test cannot find user (throws 401)
//                    statement.execute("DROP ALL OBJECTS");

                                // Find all tables and truncate them
                                Set<String> tables = new HashSet<String>();
                                ResultSet rs = statement.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES  where TABLE_SCHEMA='sope_integ'");
                                while (rs.next()) {
                                    tables.add(rs.getString(1));
                                }
                                rs.close();
                    
                                for (String table : tables) {
                                    try {
                                        System.out.println("Clearing " + table);
                                        statement.executeUpdate("TRUNCATE TABLE " + table);
                                    } catch (Exception e) {
                                        System.out.println("Cannot clear table");
                                        e.printStackTrace();
                                    }
                                }
                    
                                rs.close();
 
                    
                    // Enable FK
                    statement.execute(enableForeignKeysSql);
                }
            }
        } catch (Exception e) {
            System.out.println("CANNOT CLEAR DATABASE");
            e.printStackTrace();
            System.exit(0);
        }

    }
}
