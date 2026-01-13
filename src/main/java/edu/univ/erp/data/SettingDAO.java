
// Data access object for system settings operations
package edu.univ.erp.data;

import edu.univ.erp.domain.SystemSetting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SettingDAO {
    private static final Logger log = LoggerFactory.getLogger(SettingDAO.class);

    public SystemSetting loadSettings() throws Exception {
        SystemSetting s = new SystemSetting();
        s.setMaintenanceMode(false); 
        s.setMessage("");

        String sql = "SELECT settings_key, value FROM settings WHERE settings_key IN ('maintenance', 'maintenance_message')";

        try (Connection conn = DBconnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String key = rs.getString("settings_key");
                String val = rs.getString("value");

                if ("maintenance".equalsIgnoreCase(key)) {
                    s.setMaintenanceMode(Boolean.parseBoolean(val));
                } else if ("maintenance_message".equalsIgnoreCase(key)) {
                    s.setMessage(val);
                }
            }
        }
        return s;
    }

    public boolean saveSettings(boolean on, String msg) throws Exception {
        String sql = "UPDATE settings SET value = ? WHERE settings_key = ?";

        try (Connection conn = DBconnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, String.valueOf(on));
            ps.setString(2, "maintenance");
            ps.addBatch();

            ps.setString(1, msg);
            ps.setString(2, "maintenance_message");
            ps.addBatch();

            int[] results = ps.executeBatch();
            return results.length > 0;
        }
    }

    public boolean setMaintenance(boolean on) throws Exception {
        String sql = "UPDATE settings SET value = ? WHERE settings_key = 'maintenance'";
        try (Connection conn = DBconnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, String.valueOf(on));
            return ps.executeUpdate() > 0;
        }
    }

    public boolean isMaintenanceOn() {
        try {
            String sql = "SELECT value FROM settings WHERE settings_key = 'maintenance'";
            try (Connection conn = DBconnection.getConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Boolean.parseBoolean(rs.getString("value"));
                }
            }
        } catch (Exception e) {
            log.error("Error reading maintenance flag", e);
        }
        return false;
    }
}
