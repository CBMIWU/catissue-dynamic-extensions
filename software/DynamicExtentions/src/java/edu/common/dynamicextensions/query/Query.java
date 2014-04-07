package edu.common.dynamicextensions.query;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.common.dynamicextensions.ndao.JdbcDaoFactory;
import edu.common.dynamicextensions.ndao.ResultExtractor;
import edu.common.dynamicextensions.query.ast.ExpressionNode;
import edu.common.dynamicextensions.query.ast.QueryExpressionNode;
import edu.common.dynamicextensions.query.ast.SelectListNode;

public class Query {
    private static final Logger logger = Logger.getLogger(Query.class);
    
    private JoinTree queryJoinTree;

    private QueryExpressionNode queryExpr;
        
    private boolean wideRows;
    
    private boolean ic;
    
    private String dateFormat;
    
    private boolean vcEnabled;
        
    public static Query createQuery() {
        return new Query();
    }
    
    private Query() {
    }
        
    public Query wideRows(boolean wideRows) {
        this.wideRows = wideRows;
        return this;
    }
    
    public Query ic(boolean ic) {
    	this.ic = ic;
    	return this;
    }
    
    public Query dateFormat(String dateFormat) {
    	this.dateFormat = dateFormat;
    	return this;
    }
    
    public Query enableVersionedForms(boolean vcEnabled) {
    	this.vcEnabled = vcEnabled;
    	return this;
    }
  
    public void compile(String rootFormName, String query) {
        compile(rootFormName, query, null);
    }
    
    public void compile(String rootFormName, String query, String restriction) {
        QueryCompiler compiler = new QueryCompiler(rootFormName, query, restriction);
        compiler.enabledVersionedForms(vcEnabled).compile();
        queryExpr     = compiler.getQueryExpr();
        queryJoinTree = compiler.getQueryJoinTree();        
    }

    public long getCount() {
        QueryGenerator gen = new QueryGenerator(wideRows, ic);
        String countSql = gen.getCountSql(queryExpr, queryJoinTree);

        long t1 = System.currentTimeMillis();            
        long count = JdbcDaoFactory.getJdbcDao().getResultSet(countSql, null, new ResultExtractor<Long>() {
        	@Override
        	public Long extract(ResultSet rs) throws SQLException {
        		return rs.next() ? rs.getLong(1) : -1L;
        	}
        });
        
        long t2 = System.currentTimeMillis();
        logger.info("Count SQL: " + countSql + "; Query Exec Time: " + (t2 - t1));            
        return count;
    }

    public QueryResultData getData() {
        return getData(0, 0);
    }

    public QueryResultData getData(int start, int numRows) {
        final String dataSql = getDataSql(wideRows, start, numRows);
        final long t1 = System.currentTimeMillis();
        return JdbcDaoFactory.getJdbcDao().getResultSet(dataSql, null, new ResultExtractor<QueryResultData>() {
        	@Override
        	public QueryResultData extract(ResultSet rs)
        	throws SQLException {
        		long t2 = System.currentTimeMillis();
        		
        		QueryResultData resultData = null;
        		if (wideRows) {
        			resultData = getWideRowData(rs);
        			if (resultData == null) {
        				// this will ensure at least the header columns are populated
        				resultData = new QueryResultData(getResultColumns(queryExpr), dateFormat);
        			}
        		} else {
        			resultData = getQueryResultData(rs);
        		}
        		
        		long t3 = System.currentTimeMillis();
        		logger.info("Data SQL: " + dataSql + "; Query Exec Time: " + (t2 - t1) + "; Result Prep Time: " + (t3 - t2));
        		return resultData;
        	}
        });
    }
        
    public String getDataSql() {
        return getDataSql(false, 0, 0);
    }
    
    private String getDataSql(boolean wideRows, int start, int numRows) {
        QueryGenerator gen = new QueryGenerator(wideRows, ic);
        return gen.getDataSql(queryExpr, queryJoinTree, start, numRows);        
    }

    private QueryResultData getQueryResultData(ResultSet rs)
    throws SQLException {
        List<ResultColumn> columns = getResultColumns(queryExpr);
        int columnCount = columns.size();
        
        QueryResultData queryResult = new QueryResultData(columns, dateFormat);
        while (rs.next()) {
            Object[] row = new Object[columnCount];
            for (int i = 0; i < columnCount; ++i) {
                row[i] = rs.getObject(i + 1);
            }
            
            queryResult.addRow(row);
        }

        return queryResult;
    }
    
    private QueryResultData getWideRowData(ResultSet rs) {
        ShallowWideRowGenerator wideRowGenerator = new ShallowWideRowGenerator(queryJoinTree, queryExpr);
        wideRowGenerator.dateFormat(dateFormat);
        wideRowGenerator.start();
        wideRowGenerator.processResultSet(rs);
        wideRowGenerator.end();
        return wideRowGenerator.getQueryResultData();
    }
           
    private List<ResultColumn> getResultColumns(QueryExpressionNode queryExpr) {
        SelectListNode selectList = queryExpr.getSelectList();
        List<ResultColumn> columns = new ArrayList<ResultColumn>();
        
        for (ExpressionNode node : selectList.getElements()) {
            columns.add(new ResultColumn(node, 0));
        }
        
        return columns;     
    }    
}
