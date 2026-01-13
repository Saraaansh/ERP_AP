// Service for managing system maintenance mode state
package edu.univ.erp.service;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.data.SettingDAO;
import edu.univ.erp.domain.SystemSetting;

public class MaintenanceService {

    private final SettingDAO settingsDAO = new SettingDAO();

    public SystemSetting getCurrentSettings() throws Exception {
        return settingsDAO.loadSettings();
    }

    public boolean updateSettings(boolean on, String message) throws Exception {

        if (!SessionManager.isLoggedIn()) {
            throw new Exception("You must be logged in to change maintenance mode.");
        }

        String role = SessionManager.getCurrentRole();
        if (role == null || !"ADMIN".equalsIgnoreCase(role)) {
            throw new Exception("Only ADMIN can change maintenance mode.");
        }

        return settingsDAO.saveSettings(on, message);
    }

    public boolean isMaintenanceActive() {
        return settingsDAO.isMaintenanceOn();
    }

    public void ensureSystemAvailable() throws Exception {

        boolean maintenance = settingsDAO.isMaintenanceOn();
        if (!maintenance)
            return;

        if (SessionManager.isLoggedIn()
                && "ADMIN".equalsIgnoreCase(SessionManager.getCurrentRole())) {
            return;
        }

        throw new Exception("🚧 System under maintenance. Try again later.");
    }

    public static boolean checkMaintenance(java.awt.Component parent) {
        try {
            new MaintenanceService().ensureSystemAvailable();
            return true;
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(parent,
                    e.getMessage(),
                    "Maintenance Mode",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            return false;
        }
    }
}
