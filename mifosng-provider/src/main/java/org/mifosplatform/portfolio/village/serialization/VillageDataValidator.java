package org.mifosplatform.portfolio.village.serialization;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.portfolio.village.api.VillageApiResource;
import org.mifosplatform.portfolio.village.api.VillageTypeApiConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.policy.Resource;
import com.google.common.reflect.TypeToken;
import com.google.gson.JsonElement;


@Component
public class VillageDataValidator {

    private FromJsonHelper fromApiJsonHelper;

    @Autowired
    public VillageDataValidator(FromJsonHelper fromApiJsonHelper) {

        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    private void throwExceptionIfValidationWarningsExist(List<ApiParameterError> dataValidationErrors) {

        if (!dataValidationErrors.isEmpty()) {
            throw new PlatformApiDataValidationException(dataValidationErrors);
        }
    }
    
    public void validateForCreateVillage(final JsonCommand command) {
        
        final String json = command.json();
        
        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }
        
        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, VillageTypeApiConstants.VILLAGE_REQUEST_DATA_PARAMETERS);
        
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                    .resource(VillageTypeApiConstants.VILLAGE_RESOURCE_NAME);
        
        final JsonElement element = command.parsedJson();
        
        final Long officeId = this.fromApiJsonHelper.extractLongNamed(VillageTypeApiConstants.officeIdParamName, element);
        baseDataValidator.reset().parameter(VillageTypeApiConstants.officeIdParamName).value(officeId).notNull().integerGreaterThanZero();
        
        final String name = this.fromApiJsonHelper.extractStringNamed(VillageTypeApiConstants.villageNameParamName, element);
        baseDataValidator.reset().parameter(VillageTypeApiConstants.villageNameParamName).value(name).notNull();
        
        final Boolean active = this.fromApiJsonHelper.extractBooleanNamed(VillageTypeApiConstants.activeParamName, element);
        if (active != null) {
            if (active.booleanValue()) {
                final LocalDate joinedDate = this.fromApiJsonHelper.extractLocalDateNamed(VillageTypeApiConstants.activationDateParamName, element);
                baseDataValidator.reset().parameter(VillageTypeApiConstants.activationDateParamName).value(joinedDate).notNull();
            }else {
                final boolean isPendingApprovalEnabled = true;
                if (!isPendingApprovalEnabled) {
                    baseDataValidator.reset().parameter(VillageTypeApiConstants.activeParamName).failWithCode(".pending.status.not.allowed");
                }
            }
        }else {
            baseDataValidator.reset().parameter(VillageTypeApiConstants.activeParamName).value(active).trueOrFalseRequired(false);
        }
        
        if (this.fromApiJsonHelper.parameterExists(VillageTypeApiConstants.submittedOnDateParamName, element)) {
            final LocalDate submittedOnDate = this.fromApiJsonHelper.extractLocalDateNamed(VillageTypeApiConstants.submittedOnDateParamName, element);
            baseDataValidator.reset().parameter(VillageTypeApiConstants.submittedOnDateParamName).value(submittedOnDate).notNull();
        }
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }
}
