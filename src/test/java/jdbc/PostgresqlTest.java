package jdbc;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.ResultSetHandler;
import org.junit.Ignore;
import org.junit.Test;
import org.openlogics.gears.jdbc.DataStore;
import org.openlogics.gears.jdbc.JdbcDataStore;
import org.openlogics.gears.jdbc.Query;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Miguel Vega
 * @version $Id: PostgresqlTest.java 0, 2012-11-21 19:30 mvega $
 */
public class PostgresqlTest {
    @Test
    @Ignore
    public void longQuery(){
        BasicDataSource bds = new BasicDataSource();
        bds.setUrl("jdbc:postgresql://localhost:5432/geodata");
        bds.setDriverClassName("org.postgresql.Driver");
        bds.setUsername("postgres");
        bds.setPassword("postgres");
        bds.setMaxIdle(10);

        DataStore ds = new JdbcDataStore(bds);
        ds.setAutoClose(false);
        try {
            ds.select(new Query("select * from countries"), new ResultSetHandler<Object>() {
                @Override
                public Object handle(ResultSet rs) throws SQLException {
                    while(rs.next()){
                        String s = rs.getString("name");
                        System.out.println(s);
                    }
                    return null;
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }finally{
            try {
                System.out.println("Sleep for a while");
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            try {
                ds.closeConnection();
                System.out.println("Connection closed");
            } catch (SQLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }
}
