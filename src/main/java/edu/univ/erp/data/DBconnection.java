
package edu.univ.erp.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import edu.univ.erp.util.ConfigLoader;

import java.sql.Connection;
import java.sql.SQLException;

public class DBconnection {

    private static final HikariDataSource ds;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(ConfigLoader.get("erp.db.url"));
        config.setUsername(ConfigLoader.get("erp.db.user"));
        config.setPassword(ConfigLoader.get("erp.db.pass"));
        config.setMaximumPoolSize(15);
        config.setMinimumIdle(3);
        config.setPoolName("ERPDBPool");
        config.setLeakDetectionThreshold(15_000);
        config.setConnectionTimeout(10_000);
        config.setIdleTimeout(120_000);
        ds = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
