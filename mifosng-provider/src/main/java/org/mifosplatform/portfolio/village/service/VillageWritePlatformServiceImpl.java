package org.mifosplatform.portfolio.village.service;

import org.joda.time.LocalDate;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandProcessingService;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.office.domain.Office;
import org.mifosplatform.organisation.office.domain.OfficeRepository;
import org.mifosplatform.organisation.office.exception.OfficeNotFoundException;
import org.mifosplatform.portfolio.group.domain.Group;
import org.mifosplatform.portfolio.group.domain.GroupRepository;
import org.mifosplatform.portfolio.group.exception.CenterNotFoundException;
import org.mifosplatform.portfolio.village.api.VillageTypeApiConstants;
import org.mifosplatform.portfolio.village.domain.Village;
import org.mifosplatform.portfolio.village.domain.VillageRepositoryWrapper;
import org.mifosplatform.portfolio.village.exception.InvalidVillageStateTransitionException;
import org.mifosplatform.portfolio.village.serialization.VillageDataValidator;
import org.mifosplatform.useradministration.domain.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VillageWritePlatformServiceImpl implements VillageWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(VillageWritePlatformServiceImpl.class);
    
    private final PlatformSecurityContext context;
    private final VillageDataValidator fromApiJsonDeserializer;
    private final VillageRepositoryWrapper villageRepository;
    private final OfficeRepository officeRepository;
    private final GroupRepository centerRepository;
    private final CommandProcessingService commandProcessingService;
    
    @Autowired
    public VillageWritePlatformServiceImpl(PlatformSecurityContext context, VillageDataValidator fromApiJsonDeserializer, VillageRepositoryWrapper villageRepository, 
            OfficeRepository officeRepository, GroupRepository centerRepository, 
            CommandProcessingService commandProcessingService) {

        this.context = context;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.villageRepository = villageRepository;
        this.officeRepository = officeRepository;
        this.centerRepository = centerRepository;
        this.commandProcessingService = commandProcessingService;
    }
    
    
    @Transactional
    @Override
    public CommandProcessingResult createVillage(final JsonCommand command) {

        try{
            final AppUser currentUser = this.context.authenticatedUser();
            this.fromApiJsonDeserializer.validateForCreateVillage(command);
            
            final Long officeId = command.longValueOfParameterNamed(VillageTypeApiConstants.officeIdParamName);
            
            final Office villageOffice = this.officeRepository.findOne(officeId);
            if (villageOffice == null) {
                throw  new OfficeNotFoundException(officeId);
            }
            
            final String villageName = command.stringValueOfParameterNamed(VillageTypeApiConstants.villageNameParamName);
            final Long count = command.longValueOfParameterNamed(VillageTypeApiConstants.countParamName);
            
            final LocalDate activationDate = command.localDateValueOfParameterNamed(VillageTypeApiConstants.activationDateParamName);
            
            validateOfficeOpeningDateIsAfterVillageOpeningDate(villageOffice, activationDate);
            
            final Long centerId = command.longValueOfParameterNamed(VillageTypeApiConstants.centerIdParamName);
            
            Group centerOfVillage = null;
            if (centerId != null) {
                centerOfVillage = this.centerRepository.findOne(centerId);
                if (centerOfVillage == null) { throw new CenterNotFoundException(centerId); }
            }
            
            final boolean active = command.booleanPrimitiveValueOfParameterNamed(VillageTypeApiConstants.activeParamName);
            LocalDate submittedOnDate = new LocalDate();
            if (active && submittedOnDate.isAfter(activationDate)) {
                submittedOnDate = activationDate;
            }
            
            if (command.hasParameter(VillageTypeApiConstants.submittedOnDateParamName)) {
                submittedOnDate = command.localDateValueOfParameterNamed(VillageTypeApiConstants.submittedOnDateParamName);
            }
            
            final Village newVillage = Village.newVillage(villageOffice, villageName, count, currentUser, active, activationDate, submittedOnDate);
            
            if (centerOfVillage != null) {
                newVillage.setCenter(centerOfVillage);
            }
            
            boolean rollbackTransaction = false;
            if (newVillage.isActive()) {
                final CommandWrapper commandWrapper = new CommandWrapperBuilder().activateVillage(null).build();
                rollbackTransaction = this.commandProcessingService.validateCommand(commandWrapper, currentUser);
            }
            this.villageRepository.save(newVillage);
            
            return new CommandProcessingResultBuilder() //
                        .withCommandId(command.commandId()) //
                        .withOfficeId(villageOffice.getId()) // 
                        .withCenterId(centerId) //
                        .withVillageId(newVillage.getId()) //
                        .withEntityId(newVillage.getId()) //
                        .setRollbackTransaction(rollbackTransaction) //
                        .build();
        }catch(final DataIntegrityViolationException dive) {
            handleVillageDataIntegrityIssues(command, dive);
            return CommandProcessingResult.empty(); 
        }
    }

    private void handleVillageDataIntegrityIssues(JsonCommand command, DataIntegrityViolationException dive) {

        final Throwable realCause = dive.getMostSpecificCause();
        String errorMessageForUser = null;
        String errorMessageForMachine = null;
        
        if (realCause.getMessage().contains("villageName")) {
            final String name = command.stringValueOfParameterNamed(VillageTypeApiConstants.villageNameParamName);
            errorMessageForUser = "village with name" + name + " already exists.";
            errorMessageForMachine = "error.msg.village.duplicate.name";
            throw new PlatformDataIntegrityException(errorMessageForMachine, errorMessageForUser, VillageTypeApiConstants.villageNameParamName, name);
        } 
        logger.error(dive.getMessage(), dive);
        throw new PlatformDataIntegrityException("error.msg.village.unknown.data.integrity.issue", "Unknown data integrity issue with resource."); 
    }

    private void validateOfficeOpeningDateIsAfterVillageOpeningDate(Office villageOffice, LocalDate activationDate) {

        if (activationDate != null && villageOffice.getOpeningLocalDate().isAfter(activationDate)) {
            
            final String errorMessage = "activation date should be greater than or equal to the parent Office's creation date " + activationDate.toString();
            
            throw new InvalidVillageStateTransitionException("activate.date", "cannot.be. before.office.activation.date", errorMessage, activationDate, 
                    villageOffice.getOpeningLocalDate());
        }
    }
    
}
