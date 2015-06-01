/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.entityaccess.domain;

public class MifosEntityAccessType {
	
	private Long id;
	private String str;
	private MifosEntityType leftEntityType;
	private MifosEntityType rightEntityType;
	
	public static final MifosEntityAccessType OFFICE_ACCESS_TO_LOAN_PRODUCTS = new MifosEntityAccessType(1L, "office_access_to_loan_products",
			MifosEntityType.OFFICE, MifosEntityType.LOAN_PRODUCT);
	public static final MifosEntityAccessType OFFICE_ACCESS_TO_SAVINGS_PRODUCTS = new MifosEntityAccessType(2L, "office_access_to_savings_products",
			MifosEntityType.OFFICE, MifosEntityType.SAVINGS_PRODUCT);
	public static final MifosEntityAccessType OFFICE_ACCESS_TO_CHARGES = new MifosEntityAccessType(3L, "office_access_to_fees/charges",
			MifosEntityType.OFFICE, MifosEntityType.CHARGE);
	public static final MifosEntityAccessType ROLE_ACCESS_TO_LOAN_PRODUCTS = new MifosEntityAccessType(4L, "role_access_to_loan_products",
			MifosEntityType.ROLE, MifosEntityType.LOAN_PRODUCT);
	public static final MifosEntityAccessType ROLE_ACCESS_TO_SAVINGS_PRODUCTS = new MifosEntityAccessType(5L, "role_access_to_savings_products",
			MifosEntityType.ROLE, MifosEntityType.SAVINGS_PRODUCT);
    
    private MifosEntityAccessType (Long id, String str, MifosEntityType leftEntityType,
    		MifosEntityType rightEntityType) {
    	this.str = str;
    	this.id = id;
    	this.leftEntityType = leftEntityType;
    	this.rightEntityType = rightEntityType;
    }
    
    public String toStr () {
    	return this.str;
    }
    
    public Long id() {
    	return this.id;
    }
    
    public MifosEntityType getLeftEntityType() {
    	return this.leftEntityType;
    }
    
    public MifosEntityType getRightEntityType() {
    	return this.rightEntityType;
    }
    
    public static MifosEntityAccessType get (String type) {
    	
    	MifosEntityAccessType retType = null;
    	
    	if (type.equals(OFFICE_ACCESS_TO_LOAN_PRODUCTS.str)) {
    		retType =  OFFICE_ACCESS_TO_LOAN_PRODUCTS;
    	} else if (type.equals(OFFICE_ACCESS_TO_SAVINGS_PRODUCTS.str)) {
    		retType = OFFICE_ACCESS_TO_SAVINGS_PRODUCTS;
    	} else if (type.equals(OFFICE_ACCESS_TO_CHARGES.str)) { 
    			retType = OFFICE_ACCESS_TO_CHARGES;
    	} else if (type.equals(ROLE_ACCESS_TO_LOAN_PRODUCTS.str)) { 
			retType = ROLE_ACCESS_TO_LOAN_PRODUCTS;
    	} else if (type.equals(ROLE_ACCESS_TO_SAVINGS_PRODUCTS.str)) { 
			retType = ROLE_ACCESS_TO_SAVINGS_PRODUCTS;
    	}
    	
    	return retType;
    }
    
    public static MifosEntityAccessType get (Long id) {
    	
    	MifosEntityAccessType retType = null;
    	
    	if ( id == OFFICE_ACCESS_TO_LOAN_PRODUCTS.id) {
    		retType =  OFFICE_ACCESS_TO_LOAN_PRODUCTS;
    	} else if (id == OFFICE_ACCESS_TO_SAVINGS_PRODUCTS.id) {
    		retType = OFFICE_ACCESS_TO_SAVINGS_PRODUCTS;
    	} else if (id == OFFICE_ACCESS_TO_CHARGES.id) { 
    			retType = OFFICE_ACCESS_TO_CHARGES;
    	} else if (id == ROLE_ACCESS_TO_LOAN_PRODUCTS.id) { 
			retType = ROLE_ACCESS_TO_LOAN_PRODUCTS;
    	} else if (id == ROLE_ACCESS_TO_SAVINGS_PRODUCTS.id) { 
			retType = ROLE_ACCESS_TO_SAVINGS_PRODUCTS;
    	}
    	
    	return retType;
    }
}
