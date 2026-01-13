package edu.univ.erp.access;

import edu.univ.erp.data.SettingDAO;

public class MaintenanceFlag {

    private static final SettingDAO settingDAO = new SettingDAO();

    public static boolean isOff() {
        return !settingDAO.isMaintenanceOn();
    }

    public static boolean isOn() {
        return settingDAO.isMaintenanceOn();
    }
}
