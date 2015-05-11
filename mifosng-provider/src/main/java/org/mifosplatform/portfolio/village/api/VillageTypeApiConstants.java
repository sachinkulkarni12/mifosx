package org.mifosplatform.portfolio.village.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class VillageTypeApiConstants {
    
    public static String VILLAGE_RESOURCE_NAME = "village";

    public static final String idParamName = "id";
    public static final String officeIdParamName = "officeId";
 //   public static final String officeNameParamName = "officeName";
    public static final String officeOptionsParamName = "officeOptions";
    public static final String villageNameParamName = "villageName";
    public static final String statusParamName = "status";
    public static final String countParamName = "count";
    public static final String localeParamName = "locale";
    public static final String dateFormatParamName = "dateFormat";
    public static final String submittedOnDateParamName = "submittedOnDate";
    public static final String activationDateParamName = "activatedOnDate";
    public static final String activeParamName = "active";
    public static final String centerIdParamName = "centerId";
       
    public static final Set<String> VILLAGE_RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList(idParamName, officeIdParamName, villageNameParamName,
             statusParamName, countParamName, localeParamName,dateFormatParamName, submittedOnDateParamName, activationDateParamName, 
             officeOptionsParamName));

    public static final Set<String> VILLAGE_CREATE_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(officeIdParamName, villageNameParamName, 
            countParamName, activeParamName, localeParamName,dateFormatParamName, activationDateParamName , submittedOnDateParamName));
}
