/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.entityaccess.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;

import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.infrastructure.dataqueries.service.GenericDataServiceImpl;
import org.mifosplatform.infrastructure.entityaccess.MifosEntityAccessConstants;
import org.mifosplatform.infrastructure.entityaccess.data.MifosEntityAccessData;
import org.mifosplatform.infrastructure.entityaccess.data.MifosEntityRelationData;
import org.mifosplatform.infrastructure.entityaccess.data.MifosEntityToEntityMappingData;
import org.mifosplatform.infrastructure.entityaccess.domain.MifosEntity;
import org.mifosplatform.infrastructure.entityaccess.domain.MifosEntityAccessType;
import org.mifosplatform.infrastructure.entityaccess.domain.MifosEntityType;
import org.mifosplatform.infrastructure.entityaccess.exception.MifosEntityAccessConfigurationException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.useradministration.domain.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class MifosEntityAccessReadServiceImpl implements MifosEntityAccessReadService {
        
        private final PlatformSecurityContext context;
        private final JdbcTemplate jdbcTemplate;
    private final static Logger logger = LoggerFactory.getLogger(GenericDataServiceImpl.class);

    @Autowired
    public MifosEntityAccessReadServiceImpl(final PlatformSecurityContext context,
            final RoutingDataSource dataSource) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /*
     * (non-Javadoc)
     * @see org.mifosplatform.infrastructure.entityaccess.service.MifosEntityAccessReadService#getSQLQueryWithListOfIDsForEntityAccess
     *          (Long,
     *                  org.mifosplatform.infrastructure.entityaccess.domain.MifosEntityType, 
     *                  org.mifosplatform.infrastructure.entityaccess.domain.MifosEntityAccessType, 
     *                  org.mifosplatform.infrastructure.entityaccess.domain.MifosEntityType, 
     *                  boolean)
     * 
     * This method returns the list of entity IDs as a comma separated list
     * Or null if there is no entity restrictions or if there  
     */
    @Override
        public String getSQLQueryInClause_WithListOfIDsForEntityAccess (
                        String firstEntityIds,
                        MifosEntityAccessType relationshipType,
                        boolean includeAllOffices) {
        Collection<MifosEntityAccessData> accesslist = retrieveEntityAccessFor (
                        firstEntityIds, relationshipType, includeAllOffices);
        String returnIdListStr = null;
        StringBuffer accessListCSVStrBuf = null;
        if ( (accesslist != null) && (accesslist.size() > 0) ) {
                        logger.debug("Found " + accesslist.size() + 
                                        " access type restrictions while getting entity access configuration for: " + 
                                        firstEntityIds + " with relationship type: " + relationshipType.id() + "/" + relationshipType.toStr());
                accessListCSVStrBuf = new StringBuffer (" ");
                for (int i = 0; i < accesslist.size(); i++) {
                        MifosEntityAccessData accessData = (MifosEntityAccessData) accesslist.toArray()[i];
                        if (accessData == null) {
                                throw new MifosEntityAccessConfigurationException(firstEntityIds, relationshipType); 
                        }
                        if (accessData.getSecondEntity().getId() == 0) { // If there is any ID that zero, then allow access to all
                                accessListCSVStrBuf = null;
                                break;
                        }
                        if (i>0) {
                                accessListCSVStrBuf.append(',');
                        }
                        accessListCSVStrBuf.append(accessData.getSecondEntity().getId());
                }

        } else {
                        logger.debug("Found zero access type restrictions while getting entity access configuration for: " + 
                        		firstEntityIds + " with relationship type: " + relationshipType.id() + "/" + relationshipType.toStr());
                        accessListCSVStrBuf = new StringBuffer();
                        accessListCSVStrBuf.append ("false"); // Append false so that no rows will be returned
        }
        if (accessListCSVStrBuf != null) {
                returnIdListStr =  accessListCSVStrBuf.toString();
        }
        logger.debug("List of IDs applicable:" + returnIdListStr);
        return returnIdListStr;
    }
                        
    @Override
    public Collection<MifosEntityAccessData> retrieveEntityAccessFor(
                    String firstEntityIds,
                    MifosEntityAccessType relationshipType, 
                    boolean includeAllSubOffices) {
    	final AppUser currentUser = this.context.authenticatedUser();
        
        Collection<MifosEntityAccessData> entityAccessData = null;
        
        if (relationshipType != null) {
	        MifosEntityType firstEntityType = relationshipType.getLeftEntityType();
	        MifosEntityType secondEntityType = relationshipType.getRightEntityType();
	        
	        String hierarchySearchString = null;
	        if (includeAllSubOffices) {
	        	final String hierarchy = currentUser.getOffice().getHierarchy();
	            hierarchySearchString = hierarchy + "%";
	        }
	        
	        String sql = getSQLForRetrieveEntityAccessFor(firstEntityType,
	        		relationshipType, secondEntityType, includeAllSubOffices);
	        
	        
	        MifosEntityAccessDataMapper mapper = new MifosEntityAccessDataMapper();
	        
	        if (includeAllSubOffices && (firstEntityType.getTable().equals("m_office")) ) {
	                entityAccessData = this.jdbcTemplate.query(sql, mapper, new Object[] { relationshipType.id(), firstEntityIds, hierarchySearchString });
	        } else {
	                entityAccessData = this.jdbcTemplate.query(sql, mapper, new Object[] { relationshipType.id(), firstEntityIds });
	        }
        }

        return entityAccessData;
    }
        
        private String getSQLForRetrieveEntityAccessFor (
                        MifosEntityType firstEntityType, 
                        MifosEntityAccessType accessType, 
                        MifosEntityType secondEntityType,
                        boolean includeAllSubOffices) {             
                StringBuffer str = new StringBuffer ("select eem.from_id as entity_id, ");
                str.append("er.from_entity_type as from_entity_type, eem.rel_id as relationship_type_id, ");
                str.append("firstentity.id as from_entity_id, firstentity.name as from_entity_name, ");
                str.append("otherentity.id as to_entity_id, otherentity.name as to_entity_name, ");
                str.append("er.to_entity_type as to_entity_type, ");
                str.append("er.code_name as relationship_desc ");
                str.append("from m_entity_to_entity_mapping eem ");
                str.append("left join m_entity_relation er on (er.id = eem.rel_id) ");
                str.append("left join ");
                str.append(firstEntityType.getTable());
                str.append(" firstentity on (eem.from_id = firstentity.id)        left join ");
                str.append(secondEntityType.getTable());
                str.append(" otherentity on (eem.to_id = otherentity.id) ");
                str.append("where eem.rel_id = ? ");
                
                if (includeAllSubOffices && (firstEntityType.getTable().equals("m_office")) ) {
                	str.append(" AND ( (eem.from_id IN ( ? ) ");
	                str.append (" OR ( firstentity.hierarchy like ? ))) order by firstEntity.hierarchy ");
	                
                } else {
                	str.append(" AND eem.from_id IN ( ? ) ");
                }

                logger.debug(str.toString());
                return str.toString();
        }
        
        private static final class MifosEntityAccessDataMapper implements RowMapper<MifosEntityAccessData> {            
                @Override
                public MifosEntityAccessData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Long fromEntityType = rs.getLong("from_entity_type");
            final Long fromEntityId = rs.getLong("from_entity_id");
            final String fromEntityName = rs.getString("from_entity_name");
            final Long accessId = rs.getLong("relationship_type_id");
            final String accessTypeDesc = rs.getString("relationship_desc");
            final Long toEntityId = rs.getLong("to_entity_id");
            final String toEntityName = rs.getString("to_entity_name");
            final Long toEntityType = rs.getLong("to_entity_type");
            
            MifosEntity firstEntity = null;
            MifosEntityType etype = MifosEntityType.get(fromEntityType);
            if (fromEntityId != null && etype != null) {
                firstEntity = new MifosEntity(fromEntityId, etype);
            }
            
            MifosEntity secondEntity = null;
            MifosEntityType secondetype = MifosEntityType.get(toEntityType);
            if (toEntityId != null && secondetype != null) {
                secondEntity = new MifosEntity(toEntityId, secondetype);
            }
            
            MifosEntityAccessType accessType = null;
            if (accessTypeDesc != null) {
                accessType = MifosEntityAccessType.get(accessTypeDesc);
            }
           
            MifosEntityAccessData returnMifosEntityAccessData = null;
            if (firstEntity != null && secondEntity != null && accessType != null) {
                returnMifosEntityAccessData = new MifosEntityAccessData(firstEntity, accessType, secondEntity);
            }
            return returnMifosEntityAccessData;
            }
        }

        @Override
        public String getSQLQueryInClauseIDList_ForLoanProductsForOffice(
                        Long officeId, boolean includeAllOffices) {

    		String officeIds = officeId.toString();
            return getSQLQueryInClause_WithListOfIDsForEntityAccess (
                    officeIds, MifosEntityAccessType.OFFICE_ACCESS_TO_LOAN_PRODUCTS,
                    includeAllOffices);
        }       

        @Override
        public String getSQLQueryInClauseIDList_ForSavingsProductsForOffice(
                        Long officeId, boolean includeAllOffices) {

        	String officeIds = officeId.toString();
            return getSQLQueryInClause_WithListOfIDsForEntityAccess (
                    officeIds, MifosEntityAccessType.OFFICE_ACCESS_TO_SAVINGS_PRODUCTS,
                    includeAllOffices);
        }
        
        @Override
        public String getSQLQueryInClauseIDList_ForChargesForOffice(
                        Long officeId, boolean includeAllOffices) {

        	String officeIds = officeId.toString();
            return getSQLQueryInClause_WithListOfIDsForEntityAccess (
                    officeIds, MifosEntityAccessType.OFFICE_ACCESS_TO_CHARGES, 
                    includeAllOffices);
        }

        @Override
        public String getSQLQueryInClauseIDList_ForLoanProductsForRoles (
                        String commaSeparatedRoleIds) {

                return getSQLQueryInClause_WithListOfIDsForEntityAccess (
                		commaSeparatedRoleIds, MifosEntityAccessType.ROLE_ACCESS_TO_LOAN_PRODUCTS, 
                        false);
        }
        
        @Override
        public String getSQLQueryInClauseIDList_ForSavingsProductsForRoles (
        		String commaSeparatedRoleIds) {

                return getSQLQueryInClause_WithListOfIDsForEntityAccess (
                		commaSeparatedRoleIds, MifosEntityAccessType.ROLE_ACCESS_TO_SAVINGS_PRODUCTS, 
                        false);
        }

        @Override
        public Collection<MifosEntityRelationData> retrieveAllSupportedMappingTypes() {
            EntityRelationMapper entityMapper = new EntityRelationMapper();
            final String sql = entityMapper.schema();
            final Collection<MifosEntityRelationData> mapTypes = this.jdbcTemplate.query(sql, entityMapper, new Object[] {});
            return mapTypes;
        }

        private static final class EntityRelationMapper implements RowMapper<MifosEntityRelationData> {

            private final StringBuilder sqlBuilder = new StringBuilder("select id as id,code_name as mapping_Types from m_entity_relation ");

            public String schema() {
                return this.sqlBuilder.toString();
            }

            @Override
            public MifosEntityRelationData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
                final Long mappingTypesId = rs.getLong("id");
                final String mappingTypes = rs.getString("mapping_Types");
                return MifosEntityRelationData.getMappingTypes(mappingTypesId, mappingTypes);
            }
        }

        @Override
        public Collection<MifosEntityToEntityMappingData> retrieveEntityToEntityMappings(Long mapId, Long fromId, Long toId) {

            EntityToEntityMapper entityToEntityMapper = new EntityToEntityMapper();
            String sql = entityToEntityMapper.schema();
            final Collection<MifosEntityToEntityMappingData> mapTypes = this.jdbcTemplate.query(sql, entityToEntityMapper, new Object[] {
                    mapId, fromId, fromId, toId, toId });
            return mapTypes;

        }

        @Override
        public Collection<MifosEntityToEntityMappingData> retrieveOneMapping(Long mapId) {
            GetOneEntityMapper entityMapper = new GetOneEntityMapper();
            String sql = entityMapper.schema();
            final Collection<MifosEntityToEntityMappingData> mapTypes = this.jdbcTemplate.query(sql, entityMapper, new Object[] { mapId });
            return mapTypes;
        }

        private static final class GetOneEntityMapper implements RowMapper<MifosEntityToEntityMappingData> {

            private final String schema;

            public GetOneEntityMapper() {

                StringBuffer str = new StringBuffer("select eem.rel_id as relId, ");
                str.append("eem.from_id as fromId,eem.to_Id as toId,eem.start_date as startDate,eem.end_date as endDate ");
                str.append("from m_entity_to_entity_mapping eem ");
                str.append("where eem.id= ? ");
                this.schema = str.toString();
            }

            public String schema() {
                return this.schema;
            }

            @Override
            public MifosEntityToEntityMappingData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

                final Long relId = rs.getLong("relId");
                final Long fromId = rs.getLong("fromId");
                final Long toId = rs.getLong("toId");
                final Date startDate = rs.getDate("startDate");
                final Date endDate = rs.getDate("endDate");
                return MifosEntityToEntityMappingData.getRelatedEntities(relId, fromId, toId, startDate, endDate);
            }

        }

        private static final class EntityToEntityMapper implements RowMapper<MifosEntityToEntityMappingData> {

            private final String schema;

            public EntityToEntityMapper() {

                StringBuffer str = new StringBuffer("select eem.id as mapId, ");
                str.append("eem.rel_id as relId, ");
                str.append("eem.from_id as from_id, ");
                str.append("eem.to_id as to_id, ");
                str.append("eem.start_date as startDate, ");
                str.append("eem.end_date as endDate, ");
                str.append("case er.code_name ");
                str.append("when 'office_access_to_loan_products' then ");
                str.append("o.name ");
                str.append("when 'office_access_to_savings_products' then ");
                str.append("o.name ");
                str.append("when 'office_access_to_fees/charges' then ");
                str.append("o.name ");
                str.append("when 'role_access_to_loan_products' then ");
                str.append("r.name ");
                str.append("when 'role_access_to_savings_products' then ");
                str.append("r.name ");
                str.append("end as from_name, ");
                str.append("case er.code_name ");
                str.append("when 'office_access_to_loan_products' then ");
                str.append("lp.name ");
                str.append("when 'office_access_to_savings_products' then ");
                str.append("sp.name ");
                str.append("when 'office_access_to_fees/charges' then ");
                str.append("charge.name ");
                str.append("when 'role_access_to_loan_products' then ");
                str.append("lp.name ");
                str.append("when 'role_access_to_savings_products' then ");
                str.append("sp.name ");
                str.append("end as to_name, ");
                str.append("er.code_name ");
                str.append("from m_entity_to_entity_mapping eem ");
                str.append("join m_entity_relation er on eem.rel_id = er.id ");
                str.append("left join m_office o on er.from_entity_type = 1 and eem.from_id = o.id ");
                str.append("left join m_role r on er.from_entity_type = 5 and eem.from_id = r.id ");
                str.append("left join m_product_loan lp on er.to_entity_type = 2 and eem.to_id = lp.id ");
                str.append("left join m_savings_product sp on er.to_entity_type = 3 and eem.to_id = sp.id ");
                str.append("left join m_charge charge on er.to_entity_type = 4 and eem.to_id = charge.id ");
                str.append("where ");
                str.append("er.id = ? and ");
                str.append("( ? = 0 or from_id = ? ) and ");
                str.append("( ? = 0 or to_id = ? ) ");

                this.schema = str.toString();
            }

            public String schema() {
                return this.schema;
            }

            @Override
            public MifosEntityToEntityMappingData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
                final Long mapId = rs.getLong("mapId");
                final Long relId = rs.getLong("relId");
                final Long fromId = rs.getLong("from_id");
                final Long toId = rs.getLong("to_id");
                final String fromEntity = rs.getString("from_name");
                final String toEntity = rs.getString("to_name");
                final Date startDate = rs.getDate("startDate");
                final Date endDate = rs.getDate("endDate");
                return MifosEntityToEntityMappingData.getRelatedEntities(mapId, relId, fromId, toId, startDate, endDate, fromEntity, toEntity);
            }
        }

}
