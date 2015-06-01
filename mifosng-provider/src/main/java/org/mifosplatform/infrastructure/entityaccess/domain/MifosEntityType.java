/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.entityaccess.domain;

public class MifosEntityType {
	private Long id;
	private String type;
	private String description;
	private String table_name;
	
	public static MifosEntityType OFFICE = new MifosEntityType (1L, "office", "Offices", "m_office"); 
	public static MifosEntityType LOAN_PRODUCT = new MifosEntityType (2L, "loan_product", "Loan Products", "m_product_loan");
	public static MifosEntityType SAVINGS_PRODUCT = new MifosEntityType (3L, "savings_product", "Savings Products", "m_savings_product");
	public static MifosEntityType CHARGE = new MifosEntityType (4L, "charge", "Fees/Charges", "m_charge");
	public static MifosEntityType ROLE = new MifosEntityType (5L, "role", "Role", "m_role");
		
	private MifosEntityType (Long id, String type, String description, String table_name) {
		this.id = id;
		this.type = type;
		this.description = description;
		this.table_name = table_name;
	}
	
	public Long id() {
		return this.id;
	}
	
	public String getType () {
		return this.type;
	}
	
	public String getDescription () {
		return this.description;
	}
	
	public String getTable () {
		return this.table_name;
	}
	
	public static MifosEntityType get (String type) {

    	MifosEntityType retType = null;
    	
    	if (type.equals(OFFICE.type)) {
    		retType =  OFFICE;
    	} else if (type.equals(LOAN_PRODUCT.type)) { 
			retType = LOAN_PRODUCT;
    	} else if (type.equals(SAVINGS_PRODUCT)) { 
			retType = SAVINGS_PRODUCT;
    	} else if (type.equals(CHARGE)) { 
			retType = CHARGE; 
    	} else if (type.equals(ROLE)) {
    		retType = ROLE;
    	}
    	
    	return retType;
	}

	public static MifosEntityType get (Long id) {

    	MifosEntityType retType = null;
    	
    	if (id == OFFICE.id()) {
    		retType =  OFFICE;
    	} else if (id == LOAN_PRODUCT.id()) { 
			retType = LOAN_PRODUCT;
    	} else if (id == SAVINGS_PRODUCT.id()) { 
			retType = SAVINGS_PRODUCT;
    	} else if (id == CHARGE.id()) { 
			retType = CHARGE; 
    	} else if (id == ROLE.id()) {
    		retType = ROLE;
    	}
    	
    	return retType;
	}

}
