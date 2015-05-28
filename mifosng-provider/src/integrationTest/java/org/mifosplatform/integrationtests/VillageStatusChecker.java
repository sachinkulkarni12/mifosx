package org.mifosplatform.integrationtests;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;


public class VillageStatusChecker {

    public static void verifyVillageIsActive(final HashMap<String, Object> villageStatusHashMap) {
        assertEquals((int) villageStatusHashMap.get("id"), 300);
    }
}
