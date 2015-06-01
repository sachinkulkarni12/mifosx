package org.mifosplatform.integrationtests;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.mifosplatform.integrationtests.common.Utils;
import org.mifosplatform.integrationtests.common.VillageHelper;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;


public class VillageTest {

    private ResponseSpecification responseSpec;
    private RequestSpecification requestSpec;
    
    @Before
    public void setup() {
        
        Utils.initializeRESTAssured();
        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
    }
    
    @Test
    public void checkVillageFunctions() {
        
        Integer villageID = VillageHelper.createVillage(this.requestSpec, this.responseSpec);
        VillageHelper.verifyVillageCreatedOnServer(this.requestSpec, this.responseSpec, villageID);
        
        HashMap<String, Object> status = VillageHelper.getVillageStatus(requestSpec, responseSpec, String.valueOf(villageID));
        VillageStatusChecker.verifyVillageIsActive(status);
    }
}
