package org.mifosplatform.portfolio.village.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class VillageTypeApiConstants {
    
    public static String VILLAGE_RESOURCE_NAME = "village";

    public static final String idParamName = "id";
    public static final String externalIdParamName = "externalId";
    public static final String officeIdParamName = "officeId";
    public static final String officeNameParamName = "officeName";
    public static final String officeOptionsParamName = "officeOptions";
    public static final String villageCodeParamName = "villageCode";
    public static final String villageNameParamName = "villageName";
    public static final String statusParamName = "status";
    public static final String talukParamName = "taluk";
    public static final String districtParamName = "district";
    public static final String pincodeParamName = "pincode";
    public static final String stateParamName = "state";
    public static final String countParamName = "count";
    public static final String localeParamName = "locale";
    public static final String dateFormatParamName = "dateFormat";
    public static final String submittedOnDateParamName = "submittedOnDate";
    public static final String activationDateParamName = "activatedOnDate";
    public static final String activeParamName = "active";
    public static final String centerIdParamName = "centerId";
    
    public static final String timelineParamName = "timeline";
       
    public static final Set<String> VILLAGE_RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList(idParamName, externalIdParamName, officeIdParamName, 
            officeNameParamName, villageCodeParamName, villageNameParamName, statusParamName, countParamName, talukParamName, districtParamName, 
            pincodeParamName, stateParamName, localeParamName,dateFormatParamName, submittedOnDateParamName, activationDateParamName, 
             officeOptionsParamName));

    public static final Set<String> VILLAGE_CREATE_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(externalIdParamName, officeIdParamName, 
            villageCodeParamName, villageNameParamName, countParamName, talukParamName, districtParamName, pincodeParamName, stateParamName,
            activeParamName, localeParamName,dateFormatParamName, activationDateParamName , submittedOnDateParamName, timelineParamName));
}
