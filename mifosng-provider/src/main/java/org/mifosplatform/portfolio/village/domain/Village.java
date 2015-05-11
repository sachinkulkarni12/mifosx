package org.mifosplatform.portfolio.village.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.ObjectUtils;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.organisation.office.domain.Office;
import org.mifosplatform.portfolio.group.domain.Group;
import org.mifosplatform.portfolio.village.api.VillageTypeApiConstants;
import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "chai_villages")
public class Village extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name= "office_id")
    private Office officeId;
    
    @Column(name="village_name")
    private String villageName;
    
    @Column(name="counter")
    private Long count;
    
    @Column(name="activatedon_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date activationDate;
    
    @ManyToOne(optional = true)
    @JoinColumn(name="activatedon_userid", nullable = true)
    private AppUser activedBy;
    
    @Column(name = "submitedon_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date submittedOnDate;
    
    @ManyToOne(optional = true)
    @JoinColumn(name="submitedon_userid", nullable = true)
    private AppUser submitedBy;
    
    @Column(name="status")
    private Integer status;
    
    /*
    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany
    @JoinTable(name="chai_village_center", joinColumns= @JoinColumn(name="village_id"), inverseJoinColumns = @JoinColumn(name="center_id"))
    private final Set<Group> centerMembers = new HashSet<>(); 
    */
    
    public Village() {
    }

    public static Village newVillage(final Office office, final String villageName, final Long count, final AppUser currentUser,
            final boolean active, final LocalDate activationDate, final LocalDate submittedOnDate, final Group centerOfVillage){
        
        VillageTypeStatus status = VillageTypeStatus.PENDING;
        LocalDate villageActivaionDate = null;
        if (active) {
            status = VillageTypeStatus.ACTIVE;
            villageActivaionDate = activationDate;
        }
        
        return new Village(office, villageName, count, currentUser, status, activationDate, submittedOnDate, centerOfVillage);
    }
    
    private Village(final Office office, final String villageName, final Long count, final AppUser currentUser, final VillageTypeStatus status,
            final LocalDate activationDate, final LocalDate submittedOnDate, final Group centerOfVillage){
       
        final List<ApiParameterError> dataValidationErorrs = new ArrayList<>();
        this.officeId = office;
        this.villageName = villageName;
        this.count = count;
        this.activedBy = currentUser;
        this.submittedOnDate = submittedOnDate.toDate();
        
        if (centerOfVillage != null) {
            // this.centerMembers.add(centerOfVillage);
        }
       
        setStatus(activationDate, currentUser, status, dataValidationErorrs);
        
        throwExceptionIfErrors(dataValidationErorrs);
    }
    
    private void setStatus(final LocalDate activationDate, final AppUser loginUser, final VillageTypeStatus status, List<ApiParameterError> dataValidationErrors){
        
        if (status.isActive()) {
            activate(loginUser, activationDate, dataValidationErrors);
        }else{
            this.status = status.getValue();
        }
    }
    
    private void activate(final AppUser currentUser, final LocalDate activationLocalDate, final List<ApiParameterError> dataValidationErrors){
        validateStatusNotEqualToActiveAndLogError(dataValidationErrors);
        if (dataValidationErrors.isEmpty()) {
            this.status = VillageTypeStatus.ACTIVE.getValue();
            setActivationDate(activationLocalDate.toDate(), currentUser, dataValidationErrors);
        }
    }
    
    public void activate(final AppUser currentUser, final LocalDate activationLocalDate){
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        activate(currentUser, activationLocalDate, dataValidationErrors);
        
        throwExceptionIfErrors(dataValidationErrors);
    }
    
    private void setActivationDate(final Date activationDate, final AppUser loginUser, final List<ApiParameterError> dataValidationErrors){
        if (activationDate != null) {
            this.activationDate = activationDate;
            this.activedBy = loginUser;
        }
        
        validateActivationDate(dataValidationErrors);
    }
    
    public LocalDate getSubmittedOnDate(){
        return (LocalDate) ObjectUtils.defaultIfNull(new LocalDate(this.submittedOnDate), null);
    }
    
    public LocalDate getActivationLocalDate(){
        LocalDate activationLocalDate = null;
        if (this.activationDate != null) {
            activationLocalDate = new LocalDate(this.activationDate);
        }
        return activationLocalDate;
    }
    
    private boolean isDateInTheFuture(final LocalDate localDate){
        return localDate.isAfter(DateUtils.getLocalDateOfTenant());
    }
    
    public boolean isActive() {
        return this.status != null ? VillageTypeStatus.fromInt(this.status).isActive() : false; 
    }
    
    private void validateActivationDate(final List<ApiParameterError> dataValidationErrors){
        
        if (getSubmittedOnDate() != null && isDateInTheFuture(getSubmittedOnDate())) {
            
            final String defaultUserMessage = "Submitted on date cannot be in the future.";
            final String globalisationMessageCode = "error.message.village.submittedOnDate.in.the.future";
            final ApiParameterError error = ApiParameterError.parameterError(globalisationMessageCode, 
                    defaultUserMessage, VillageTypeApiConstants.submittedOnDateParamName, this.submittedOnDate);
            
            dataValidationErrors.add(error);
        }
        
        if (getActivationLocalDate() != null && getSubmittedOnDate() != null && getSubmittedOnDate().isAfter(getActivationLocalDate())) {
            
            final String defaultUserMessage = "submitted date cannot be after the activation date";
            final ApiParameterError error = ApiParameterError.parameterError("error.msg.village.submittedOnDate.after.activation.date", 
                    defaultUserMessage, VillageTypeApiConstants.submittedOnDateParamName, this.submittedOnDate);
            dataValidationErrors.add(error);
        }
        
        if (getActivationLocalDate() != null && isDateInTheFuture(getActivationLocalDate())) {
            
            final String defaultUserMessage = "Activation date cannot be in the future.";
            final ApiParameterError error = ApiParameterError.parameterError("error.msg.group.activationDate.in.the.future",
                    defaultUserMessage, VillageTypeApiConstants.activationDateParamName, getActivationLocalDate());

            dataValidationErrors.add(error);
        }
        
        if (getActivationLocalDate() != null) {
            if (this.officeId.isOpeningDateAfter(getActivationLocalDate())) {
                final String defaultUserMessage = "Activation date cannot be a date before the office opening date.";
                final ApiParameterError error = ApiParameterError.parameterError(
                        "error.msg.group.activationDate.cannot.be.before.office.activation.date", defaultUserMessage,
                        VillageTypeApiConstants.activationDateParamName, getActivationLocalDate());
                dataValidationErrors.add(error);
            }
        }
    }
    
    private void validateStatusNotEqualToActiveAndLogError(final List<ApiParameterError> dataValidationErrors) {
      
        if (isActive()) {
            final String defaultUserMessage = "Cannot activate group. Group is already active.";
            final String globalisationMessageCode = "error.msg.group.already.active";
            final ApiParameterError error = ApiParameterError.parameterError(globalisationMessageCode, 
                    defaultUserMessage, VillageTypeApiConstants.activeParamName, true);
            dataValidationErrors.add(error);
        }
    }
    
    private void throwExceptionIfErrors(final List<ApiParameterError> dataValidationErrors) {
        
        if (!dataValidationErrors.isEmpty()) {
            throw new PlatformApiDataValidationException(dataValidationErrors);
        }
    }
    
    public Long officeId() {
       return this.officeId.getId();
    }
    
    public boolean isOfficeIdentifiedBy(final Long officeId) {
        return this.officeId.identifiedBy(officeId);
    }
    
    public void incrementCount() {
        this.count += 1;
    }

}
