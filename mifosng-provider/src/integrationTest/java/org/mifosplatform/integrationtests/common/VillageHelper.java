package org.mifosplatform.integrationtests.common;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import com.google.gson.Gson;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;


public class VillageHelper {

    private final RequestSpecification requestSpec;
    private final ResponseSpecification responseSpec;
    
    private static final String CREATE_VILLAGE_URL = "/mifosng-provider/api/v1/villages?" + Utils.TENANT_IDENTIFIER;
    private static final String VILLAGE_URL = "/mifosng-provider/api/v1/villages";
    
    
    public VillageHelper(RequestSpecification requestSpec, ResponseSpecification responseSpec) {
        this.requestSpec = requestSpec;
        this.responseSpec = responseSpec;
    }

    public static Integer createVillage(final RequestSpecification requestSpec, final ResponseSpecification responseSpec) {
        
        return createVillage(requestSpec, responseSpec, "01 may 2015");
    }
    
    public static Integer createVillage(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final String activationDate) {
        return createVillage(requestSpec, responseSpec, activationDate, "1");
    }
    
    public static Integer createVillage(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final String activationDate, final String officeId) {
        System.out.println("---------------------------------CREATING A CLIENT---------------------------------------------");
        return Utils.performServerPost(requestSpec, responseSpec, CREATE_VILLAGE_URL, getTestVillageAsJSON(activationDate, officeId),
                "villageId");
    }
    
    public static String getTestVillageAsJSON(final String dateOfJoining, final String officeId) {
        final HashMap<String, String> map = new HashMap<>();
        map.put("officeId", officeId);
        map.put("villageName", Utils.randomNameGenerator("Village_Name", 5));
        map.put("externalId", randomIDGenerator("ID_", 7));
        map.put("dateFormat", "dd MMMM yyyy");
        map.put("locale", "en");
        map.put("active", "true");
        map.put("activationDate", dateOfJoining);
        System.out.println("map : " + map);
        return new Gson().toJson(map);
    }
    
    private static String randomIDGenerator(final String prefix, final int lenOfRandomSuffix) {
        return Utils.randomStringGenerator(prefix, lenOfRandomSuffix, "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }
    
    public static void verifyVillageCreatedOnServer(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final Integer generatedVillageID) {
        System.out.println("------------------------------CHECK VILLAGE DETAILS------------------------------------\n");
        final String VILLAGE_URL = "/mifosng-provider/api/v1/villages/" + generatedVillageID + "?" + Utils.TENANT_IDENTIFIER;
        final Integer responseVillageID = Utils.performServerGet(requestSpec, responseSpec, VILLAGE_URL, "id");
        assertEquals("ERROR IN CREATING THE VILLAGE", generatedVillageID, responseVillageID);
    }
    
    @SuppressWarnings("unchecked")
    public static HashMap<String, Object> getVillageStatus(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final String villageId) {
        return (HashMap<String, Object>) getVillage(requestSpec, responseSpec, villageId, "status");
    }
    
    public static Object getVillage(final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final String villageId,
            final String jsonReturn) {
        final String GET_VILLAGE_URL = "/mifosng-provider/api/v1/villages/" + villageId + "?" + Utils.TENANT_IDENTIFIER;
        System.out.println("---------------------------------GET A VILLAGE---------------------------------------------");
        return Utils.performServerGet(requestSpec, responseSpec, GET_VILLAGE_URL, jsonReturn);

    }
}
