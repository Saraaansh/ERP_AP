// Domain model representing a system user with authentication details
package edu.univ.erp.domain;
import java.time.LocalDateTime;
public class User {

        private int userId;
        private String username;
        private String role;
        private String status;
        private int failedAttempts;
        private LocalDateTime lockTime;
        
        public User(){

        }
        public User(int userId , String username ,String role , String status ){
            this.userId = userId;
            this.username = username;
            this.role = role;
            this.status=status;
        }
        
        public User(int userId, String username, String role, String status, int failedAttempts, LocalDateTime lockTime) {
            this.userId = userId;
            this.username = username;
            this.role = role;
            this.status = status;
            this.failedAttempts = failedAttempts;
            this.lockTime = lockTime;
        }
        public int getUserId() {
            return userId;

        }
        public void setUserId(int userId) {
            this.userId = userId;
        }
        public String getUsername() {
            return username;
        }
        public void setUsername(String username) {
            this.username = username;
        }
        public String getRole() {
            return role;
        }
        public void setRole(String role) {
            this.role = role;
        }
        public String getStatus() {
            return status;
        }
        public void setStatus(String status) {
            this.status = status;
        }
        
        public int getFailedAttempts() {
            return failedAttempts;
        }
        
        public void setFailedAttempts(int failedAttempts) {
            this.failedAttempts = failedAttempts;
        }
        
        public LocalDateTime getLockTime() {
            return lockTime;
        }
        
        public void setLockTime(LocalDateTime lockTime) {
            this.lockTime = lockTime;
        }
        
        @Override
        public String toString() {
            return "User{" +
                    "userid=" + userId +
                    ", username='" + username + '\'' +
                    ", role='" + role + '\'' +
                    ", status='" + status +
                    '}';
        }
}
