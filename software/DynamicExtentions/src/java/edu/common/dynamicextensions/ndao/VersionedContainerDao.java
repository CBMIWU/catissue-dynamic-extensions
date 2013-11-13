package edu.common.dynamicextensions.ndao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.common.dynamicextensions.domain.nui.VersionedContainerInfo;

public class VersionedContainerDao {
	private JdbcDao jdbcDao;
	
	public VersionedContainerDao(JdbcDao jdbcDao) {
		this.jdbcDao = jdbcDao;
	}
	
	public String getFormName(Long formId) {
		ResultSet rs = null;
		
		try {
			List<Long> params = Collections.singletonList(formId);
			rs = jdbcDao.getResultSet(GET_FORM_NAME_BY_FORM_ID_SQL, params);
			return rs.next() ? rs.getString("FORM_NAME") : null;
		} catch (Exception e) {
			throw new RuntimeException("Error obtaining form name for id: " + formId, e);
		} finally {
			jdbcDao.close(rs);
		}
	}
	
	public Long getFormId(String formName) {
		ResultSet rs = null;
		
		try {
			List<String> params = Collections.singletonList(formName);
			rs = jdbcDao.getResultSet(GET_FORM_ID_BY_FORM_NAME_SQL, params);
			return rs.next() ? rs.getLong("IDENTIFIER") : null;
		} catch (Exception e) {
			throw new RuntimeException("Error obtaining form with name : " + formName, e);
		} finally {
			jdbcDao.close(rs);
		}
	}
	
	public Long getFormIdByDraftContainerId(Long draftContainerId) {
		ResultSet rs = null;
		Long formId = null;
		
		try {
			List<Long> params = Collections.singletonList(draftContainerId);
			rs = jdbcDao.getResultSet(GET_FORM_ID_BY_DRAFT_CONTAINER_ID_SQL, params);
			if (rs.next()) {
				formId = rs.getLong("IDENTIFIER");
			}
			
			return formId;
		} catch (Exception e) {
			throw new RuntimeException("Error obtaining form id: " + draftContainerId, e);
		} finally {
			jdbcDao.close(rs);
		}
	}
	
	
	public VersionedContainerInfo getDraftContainerInfo(Long formId) {
		VersionedContainerInfo draftInfo = null;
		ResultSet rs = null;
		
		try {
			rs = jdbcDao.getResultSet(GET_DRAFT_CONTAINER_INFO_SQL, Collections.singletonList(formId));
			if (rs.next()) {
				draftInfo = getInfo(formId, "draft", rs);
			}
			
			return draftInfo;
		} catch (Exception e) {
			throw new RuntimeException("Error getting draft container info for form: " + formId, e);
		} finally {
			jdbcDao.close(rs);
		}		
	}
	
	public List<VersionedContainerInfo> getPublishedContainersInfo(Long formId) {
		List<VersionedContainerInfo> result = new ArrayList<VersionedContainerInfo>();
		ResultSet rs = null;
		try {
			rs = jdbcDao.getResultSet(GET_PUBLISHED_CONTAINER_INFO_SQL, Collections.singletonList(formId));
			while (rs.next()) {
				result.add(getInfo(formId, "published", rs));
			}
			
			return result;
		} catch (Exception e) {
			throw new RuntimeException("Error getting published container info: " + formId, e);
		} finally {
			jdbcDao.close(rs);
		}
	}
	
	public List<VersionedContainerInfo> getPublishedContainersInfo(String formName) {
		List<VersionedContainerInfo> result = new ArrayList<VersionedContainerInfo>();
		ResultSet rs = null;
		try {
			rs = jdbcDao.getResultSet(GET_PUBLISHED_CONTAINER_INFO_BY_FORM_NAME_SQL, Collections.singletonList(formName));
			while (rs.next()) {
				result.add(getInfo(rs.getLong("IDENTIFIER"), "published", rs));
			}
			
			return result;
		} catch (Exception e) {
			throw new RuntimeException("Error getting published container info: " + formName, e);
		} finally {
			jdbcDao.close(rs);
		}
	}
	
	public void insertVersionedContainerInfo(VersionedContainerInfo info) {
		ResultSet rs = null;
		
		try {
			List<Object> params = new ArrayList<Object>();

			params.add(info.getFormId());
			params.add(info.getContainerId());
			params.add(info.getActivationDate());
			params.add(info.getCreatedBy());
			params.add(info.getCreationTime());
			params.add(info.getStatus());
			
			jdbcDao.executeUpdate(INSERT_VERSIONED_CONTAINER_INFO_SQL, params);
		
		} catch (Exception e) {
			throw new RuntimeException("Error inserting versioned container info", e);
		} finally {
			jdbcDao.close(rs);
		}
	}
	
	private VersionedContainerInfo getInfo(Long formId, String status, ResultSet rs) 
	throws Exception {
		VersionedContainerInfo info = new VersionedContainerInfo();
		info.setFormId(formId);
		info.setContainerId(rs.getLong("CONTAINER_ID"));
		info.setActivationDate(rs.getDate("ACTIVATION_DATE"));
		info.setCreatedBy(rs.getLong("CREATED_BY"));
		info.setCreationTime(rs.getDate("CREATE_TIME"));		
		info.setStatus(status);
		return info;		
	}
		
	public Long insertFormInfo(String formName) {
		ResultSet rs = null;
		Long formId = null;
		try {
			rs = jdbcDao.getResultSet(GET_NEXT_ID_SQL, null);
		
			if (rs.next()) {
				formId = rs.getLong(1);
			} else {
				throw new RuntimeException("Failed to obtain next form id");
			}
			
			List<Object> params = new ArrayList<Object>();
			params.add(formId);
			params.add(formName);
			
			jdbcDao.executeUpdate(INSERT_FORM_INFO_SQL, params);
			
			return formId;
		} catch (Exception e) {
			throw new RuntimeException("Error inserting versioned container info", e);
		} finally {
			jdbcDao.close(rs);
		}
	}
	private final static String GET_DRAFT_CONTAINER_INFO_SQL = 
			"SELECT CONTAINER_ID, ACTIVATION_DATE, CREATED_BY, CREATE_TIME " +
			"FROM DYEXTN_VERSIONED_CONTAINERS " +
			"WHERE IDENTIFIER = ? AND STATUS = 'draft'";
	
	private final static String GET_PUBLISHED_CONTAINER_INFO_SQL =
			"SELECT CONTAINER_ID, ACTIVATION_DATE, CREATED_BY, CREATE_TIME " +
			"FROM DYEXTN_VERSIONED_CONTAINERS " +
			"WHERE IDENTIFIER = ? AND STATUS = 'published' " +
			"ORDER BY ACTIVATION_DATE";
	
	private final static String GET_PUBLISHED_CONTAINER_INFO_BY_FORM_NAME_SQL =
			"SELECT VC.IDENTIFIER, CONTAINER_ID, ACTIVATION_DATE, CREATED_BY, CREATE_TIME " +
			"FROM DYEXTN_VERSIONED_CONTAINERS VC " +
			"INNER JOIN DYEXTN_FORMS DF ON VC.IDENTIFIER = DF.IDENTIFIER " +
			"WHERE FORM_NAME = ? AND STATUS = 'published' " +
			"ORDER BY ACTIVATION_DATE";
	
	private final static String INSERT_VERSIONED_CONTAINER_INFO_SQL = 
			"INSERT INTO DYEXTN_VERSIONED_CONTAINERS (IDENTIFIER, CONTAINER_ID, ACTIVATION_DATE, CREATED_BY, CREATE_TIME, STATUS) " +
			"VALUES (?, ?, ?, ?, ?, ?)";
	
	private final static String GET_FORM_ID_BY_DRAFT_CONTAINER_ID_SQL =
			"SELECT IDENTIFIER FROM DYEXTN_VERSIONED_CONTAINERS WHERE CONTAINER_ID = ? AND STATUS = 'draft'";
	
	private final static String INSERT_FORM_INFO_SQL = 
			"INSERT INTO DYEXTN_FORMS (IDENTIFIER, FORM_NAME) VALUES (?, ?)";
	
	private final static String GET_FORM_ID_BY_FORM_NAME_SQL =
			"SELECT IDENTIFIER FROM DYEXTN_FORMS WHERE FORM_NAME = ? ";
	
	private final static String GET_FORM_NAME_BY_FORM_ID_SQL =
			"SELECT FORM_NAME FROM DYEXTN_FORMS WHERE IDENTIFIER = ?";
	
	private final static String GET_NEXT_ID_SQL = 
			"SELECT DYEXTN_FORMS_SEQ.NEXTVAL FROM DUAL";

}