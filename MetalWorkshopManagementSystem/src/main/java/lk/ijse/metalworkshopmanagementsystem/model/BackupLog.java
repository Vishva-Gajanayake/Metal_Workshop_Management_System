package lk.ijse.metalworkshopmanagementsystem.model;

import java.time.LocalDateTime;

public class BackupLog {
    private int backupId;
    private int createdBy;
    private BackupType backupType;
    private String filePath;
    private BackupStatus backupStatus;
    private LocalDateTime createdAt;

    public BackupLog() {
    }

    public BackupLog(int backupId, int createdBy, BackupType backupType, String filePath, BackupStatus backupStatus, LocalDateTime createdAt) {
        this.backupId = backupId;
        this.createdBy = createdBy;
        this.backupType = backupType;
        this.filePath = filePath;
        this.backupStatus = backupStatus;
        this.createdAt = createdAt;
    }

    public int getBackupId() {
        return backupId;
    }

    public void setBackupId(int backupId) {
        this.backupId = backupId;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public BackupType getBackupType() {
        return backupType;
    }

    public void setBackupType(BackupType backupType) {
        this.backupType = backupType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public BackupStatus getBackupStatus() {
        return backupStatus;
    }

    public void setBackupStatus(BackupStatus backupStatus) {
        this.backupStatus = backupStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "BackupLog{" + "backupId=" + backupId + ", createdBy=" + createdBy + ", backupType=" + backupType + ", filePath=" + filePath + ", backupStatus=" + backupStatus + ", createdAt=" + createdAt + '}';
    }
   
}
