// Domain model for aggregated system settings including maintenance mode
package edu.univ.erp.domain;
public class SystemSetting {
    private boolean maintenanceMode;
    private String message;

    public SystemSetting() {}

    public SystemSetting(boolean mode, String msg) {
        this.maintenanceMode = mode;
        this.message = msg;
    }

    public boolean isMaintenanceMode() { return maintenanceMode; }
    public void setMaintenanceMode(boolean maintenanceMode) { this.maintenanceMode = maintenanceMode; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}