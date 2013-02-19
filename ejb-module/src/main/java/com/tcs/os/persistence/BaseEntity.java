package com.tcs.os.persistence;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.tcs.os.util.CommonUtil;

/*
 * This class should be extended for all the entities that are mapped to tables with audit data.
 */
@MappedSuperclass
public class BaseEntity {

    @Column(name = "CREATED_BY")
    protected Long createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_CREATED")
    protected Date lastCreated;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_UPDATED")
    protected Date lastUpdated;

    @Column(name = "UPDATED_BY")
    protected Long updatedBy;

    public BaseEntity() {
        super();
    }
    
    public BaseEntity(final Long createdBy, final Date lastCreated) {
        super();
        this.createdBy = createdBy;
        this.lastCreated = (Date) lastCreated.clone();
        this.lastUpdated = (Date) lastCreated.clone();
        this.updatedBy = createdBy;
    }
    
    @Transient
    private String createdByFullName;

    @Transient
    private String updatedByFullName;

    public String getCreatedByFullName() {
        return createdByFullName;
    }

    public void setCreatedByFullName(final String createdByFullName) {
        this.createdByFullName = createdByFullName;
    }

    public String getUpdatedByFullName() {
        return updatedByFullName;
    }

    public void setUpdatedByFullName(final String updatedByFullName) {
        this.updatedByFullName = updatedByFullName;
    }
     
    
    public Date getLastCreated() {
        if (this.lastCreated != null) {
            return CommonUtil.changeTimeZoneToSystem((Date) this.lastCreated.clone());
        }
        return null;
    }

    public void setLastCreated(final Date usrDateCreated) {
        this.lastCreated = CommonUtil.getDateInUTC(((Date) usrDateCreated.clone()));
    }

    public Date getLastUpdated() {
        if (this.lastUpdated != null) {
            return CommonUtil.changeTimeZoneToSystem((Date) this.lastUpdated.clone());
        }
        return null;
    }

    public void setLastUpdated(final Date usrLastUpdated) {
        this.lastUpdated = CommonUtil.getDateInUTC(((Date) usrLastUpdated.clone()));
    }

    public Long getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(final Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(final Long updatedBy) {
        this.updatedBy = updatedBy;
    }
    
    /*
     * This Method needs to be called if edit a particular record.
     * The updatedByFullName is also set since in case of edit,
     * we need to reflect the updatedBy name immediately
     */
    public void updateAuditDetails(final Long updatedById, final String updatedByFN) {
        this.updatedBy = updatedById;
        this.updatedByFullName = updatedByFN;
        this.setLastUpdated(new Date());
    }

    /*
     * This method needs to be called if you create a record.
     */
    public void setAuditDetails(final Long createdById) {
        this.createdBy = createdById;
        this.updatedBy = createdById;
        this.setLastCreated(new Date());
        this.setLastUpdated(new Date());
    }
    
}
