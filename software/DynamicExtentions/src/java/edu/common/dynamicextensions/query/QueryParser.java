package edu.common.dynamicextensions.query;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import edu.common.dynamicextensions.query.antlr.AQLLexer;
import edu.common.dynamicextensions.query.antlr.AQLParser;
import edu.common.dynamicextensions.query.ast.QueryExpressionNode;

public class QueryParser {

	private String queryExpr;
	
	public QueryParser(String queryExpr) {
        this.queryExpr = queryExpr;
    }

    public QueryExpressionNode getQueryAst() {
        ANTLRInputStream input = new ANTLRInputStream(queryExpr);
        AQLLexer lexer = new AQLLexer(input);
        
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        AQLParser parser = new AQLParser(tokens);
        
        ParseTree tree = parser.query();
        QueryAstBuilder builder = new QueryAstBuilder();        
        return (QueryExpressionNode)builder.visit(tree);
    }
}