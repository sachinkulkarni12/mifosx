/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.dataqueries.data;

import java.util.List;

/**
 * Immutable data object representing datatable data.
 */
public class DatatableData {

	private final Integer id;
    @SuppressWarnings("unused")
    private final String applicationTableName;
    @SuppressWarnings("unused")
    private final String registeredTableName;
    @SuppressWarnings("unused")
    private final Boolean combineWithMainEntity;
    @SuppressWarnings("unused")
    private Integer minimumNoOfRows;
    private final Boolean isMultiRow;
    private final List<ResultsetColumnHeaderData> columnHeaderData;


    public static DatatableData create(final Integer id, final String applicationTableName, final String registeredTableName,
    		final Boolean combineWithMainEntity, final Integer minimumNoOfRows, final Boolean isMultiRow, final List<ResultsetColumnHeaderData> columnHeaderData) {
        return new DatatableData(id, applicationTableName, registeredTableName, combineWithMainEntity, minimumNoOfRows, isMultiRow, columnHeaderData);
    }

    private DatatableData(final Integer id, final String applicationTableName, final String registeredTableName,
            final Boolean combineWithMainEntity, final Integer minimumNoOfRows, final Boolean isMultiRow, final List<ResultsetColumnHeaderData> columnHeaderData) {
    	this.id = id;
        this.applicationTableName = applicationTableName;
        this.registeredTableName = registeredTableName;
        this.combineWithMainEntity = combineWithMainEntity;
        this.minimumNoOfRows = minimumNoOfRows;
        this.isMultiRow = isMultiRow;
        this.columnHeaderData = columnHeaderData;

    }
    
    public Integer getId() {
		return this.id;
	}
}