package com.sope.domain.user;

import java.util.Date;

import javax.persistence.Embeddable;

import org.hibernate.annotations.ColumnDefault;
import org.joda.time.LocalDateTime;

@Embeddable
public class UserPermissions {

    private Date banDateTime;

    private Date banReleaseDateTime;

    private Date lastBanDate;

    @ColumnDefault("0")
    private int totalBanTimes = 0;

    @ColumnDefault("0")
    private int warningCount = 0;

    public Date getBanDateTime() {
        return banDateTime;
    }

    public void setBanDateTime(Date banDateTime) {
        this.banDateTime = banDateTime;
    }

    public Date getBanReleaseDateTime() {
        return banReleaseDateTime;
    }

    public void setBanReleaseDateTime(Date banReleaseDateTime) {
        this.banReleaseDateTime = banReleaseDateTime;
    }

    public int getWarningCount() {
        return warningCount;
    }

    public void setWarningCount(int warningCount) {
        this.warningCount = warningCount;
    }

    public boolean isBanned() {
        if (banReleaseDateTime != null) {
            LocalDateTime releaseDateTime = new LocalDateTime(banReleaseDateTime);
            LocalDateTime now = LocalDateTime.now();
            return now.isEqual(releaseDateTime) || now.isAfter(releaseDateTime);
        }
        
        return false;
    }

    public void bannedFor(int minutes) {
        banDateTime = new Date();
        banReleaseDateTime = new LocalDateTime(banDateTime).plusMinutes(minutes).toDate();
    }

    public void addWarning() {
        warningCount = warningCount + 1;

    }

    public void releaseBan() {
        lastBanDate = banDateTime;
        totalBanTimes += 1;
        warningCount = 0;
        banDateTime = null;
        banReleaseDateTime = null;
    }

    public Date getLastBanDate() {
        return lastBanDate;
    }

    public void setLastBanDate(Date lastBanDate) {
        this.lastBanDate = lastBanDate;
    }

    public int getTotalBanTimes() {
        return totalBanTimes;
    }

    public void setTotalBanTimes(int totalBanTimes) {
        this.totalBanTimes = totalBanTimes;
    }

    public boolean shouldReleaseBan() {
        return banReleaseDateTime != null && new Date().after(banReleaseDateTime);
    }

    public boolean isExtendedBan(int extendedBanTime) {
        return totalBanTimes % extendedBanTime == 0 && totalBanTimes != 0;
    }

}
