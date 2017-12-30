package cop5556fa17;

import java.net.MalformedURLException;
import java.net.URL;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.Token;
import cop5556fa17.TypeUtils.Type;
import cop5556fa17.AST.ASTNode;
import cop5556fa17.AST.ASTVisitor;
import cop5556fa17.AST.Declaration;
import cop5556fa17.AST.Declaration_Image;
import cop5556fa17.AST.Declaration_SourceSink;
import cop5556fa17.AST.Declaration_Variable;
import cop5556fa17.AST.Expression;
import cop5556fa17.AST.Expression_Binary;
import cop5556fa17.AST.Expression_BooleanLit;
import cop5556fa17.AST.Expression_Conditional;
import cop5556fa17.AST.Expression_FunctionAppWithExprArg;
import cop5556fa17.AST.Expression_FunctionAppWithIndexArg;
import cop5556fa17.AST.Expression_Ident;
import cop5556fa17.AST.Expression_IntLit;
import cop5556fa17.AST.Expression_PixelSelector;
import cop5556fa17.AST.Expression_PredefinedName;
import cop5556fa17.AST.Expression_Unary;
import cop5556fa17.AST.Index;
import cop5556fa17.AST.LHS;
import cop5556fa17.AST.Program;
import cop5556fa17.AST.Sink_Ident;
import cop5556fa17.AST.Sink_SCREEN;
import cop5556fa17.AST.Source_CommandLineParam;
import cop5556fa17.AST.Source_Ident;
import cop5556fa17.AST.Source_StringLiteral;
import cop5556fa17.AST.Statement_Assign;
import cop5556fa17.AST.Statement_In;
import cop5556fa17.AST.Statement_Out;

public class TypeCheckVisitor implements ASTVisitor {
	
	
		@SuppressWarnings("serial")
		public static class SemanticException extends Exception {
			Token t;

			public SemanticException(Token t, String message) {
				super("line " + t.line + " pos " + t.pos_in_line + ": "+  message);
				this.t = t;
			}

		}		
		

	
	/**
	 * The program name is only used for naming the class.  It does not rule out
	 * variables with the same name.  It is returned for convenience.
	 * 
	 * @throws Exception 
	 */
		
	public SymbolTable symbolTable = new SymbolTable();
		
	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		for (ASTNode node: program.decsAndStatements) {
			node.visit(this, arg);
		}
		return program.name;
	}

	@Override
	public Object visitDeclaration_Variable(
			Declaration_Variable declaration_Variable, Object arg)
			throws Exception {
		
		if(declaration_Variable.e != null)
			declaration_Variable.e.visit(this, null);
		if(symbolTable.lookupType(declaration_Variable.name) != null)
			throw new SemanticException(declaration_Variable.firstToken, "declaration_Variable is already present in the SymbolTable");
		else {
			symbolTable.insert(declaration_Variable.name, declaration_Variable);
			declaration_Variable.Type = TypeUtils.getType(declaration_Variable.type);
			if(declaration_Variable.e != null)
				if(declaration_Variable.Type != declaration_Variable.e.Type)
					throw new SemanticException(declaration_Variable.firstToken, "declaration_Variable requirement does't satisfy");
		}
		return declaration_Variable;
	}

	@Override
	public Object visitExpression_Binary(Expression_Binary expression_Binary,
			Object arg) throws Exception {
		
		if(expression_Binary.e0 != null)
			expression_Binary.e0.visit(this, null);
		if(expression_Binary.e1 != null)
			expression_Binary.e1.visit(this, null);
		if(expression_Binary.op == Kind.OP_EQ || expression_Binary.op == Kind.OP_NEQ)
			expression_Binary.Type = Type.BOOLEAN;
		else if((expression_Binary.op == Kind.OP_GE || expression_Binary.op == Kind.OP_GT || 
				 expression_Binary.op == Kind.OP_LT || expression_Binary.op == Kind.OP_LE) &&
				 expression_Binary.e0.Type == Type.INTEGER)
			expression_Binary.Type = Type.BOOLEAN;
		else if((expression_Binary.op == Kind.OP_AND || expression_Binary.op == Kind.OP_OR) &&
				(expression_Binary.e0.Type == Type.INTEGER || expression_Binary.e0.Type == Type.BOOLEAN) )
			expression_Binary.Type = expression_Binary.e0.Type;
		else if( (expression_Binary.op == Kind.OP_DIV || expression_Binary.op == Kind.OP_MINUS ||
				  expression_Binary.op == Kind.OP_MOD  || expression_Binary.op == Kind.OP_PLUS ||
				  expression_Binary.op == Kind.OP_POWER  || expression_Binary.op == Kind.OP_TIMES) &&
				(expression_Binary.e0.Type == Type.INTEGER))
			expression_Binary.Type = Type.INTEGER;
		else 
			expression_Binary.Type = null;
		if(expression_Binary.e0.Type != expression_Binary.e1.Type || expression_Binary.Type == null)
			throw new SemanticException(expression_Binary.firstToken, "expression_Binary doesn't satisfy requirements");
		return expression_Binary;
		
	}

	@Override
	public Object visitExpression_Unary(Expression_Unary expression_Unary,
			Object arg) throws Exception {
		
		if(expression_Unary.e != null)
			expression_Unary.e.visit(this,null);
		Type t = expression_Unary.e.Type;
		if(expression_Unary.op == Kind.OP_EXCL && (t == Type.BOOLEAN || t == Type.INTEGER))
			expression_Unary.Type = t;
		else if( (expression_Unary.op == Kind.OP_PLUS || expression_Unary.op == Kind.OP_MINUS) &&
				  (t == Type.INTEGER) )
			expression_Unary.Type = Type.INTEGER;
		else
			expression_Unary.Type = null;
		if(expression_Unary.Type == null)
			throw new SemanticException(expression_Unary.firstToken, "Expression_unary type is null");
		return expression_Unary;
		
	}

	@Override
	public Object visitIndex(Index index, Object arg) throws Exception {
		
		if(index.e0 != null)
			index.e0.visit(this, null);
		if(index.e1 != null)
			index.e1.visit(this, null);
		if(index.e0.Type == Type.INTEGER && index.e1.Type == Type.INTEGER) {
			if(index.e0.getClass() == Expression_PredefinedName.class && index.e1.getClass() == Expression_PredefinedName.class) {
				Expression_PredefinedName expr0 = (Expression_PredefinedName)index.e0;
				Expression_PredefinedName expr1 = (Expression_PredefinedName)index.e1;
				index.setCartesian(!(expr0.kind == Kind.KW_r && expr1.kind == Kind.KW_a));
			}
			else {
				index.setCartesian(true);
			}
		}
		else
			throw new SemanticException(index.firstToken, "Index doesn't meet the requirement");
		return index;
		
	}

	@Override
	public Object visitExpression_PixelSelector(
			Expression_PixelSelector expression_PixelSelector, Object arg)
			throws Exception {
		
		if(expression_PixelSelector.index != null)
			expression_PixelSelector.index.visit(this, null);
		Declaration declaration;
		declaration = symbolTable.lookupType(expression_PixelSelector.name);
		if(declaration == null)
			throw new SemanticException(expression_PixelSelector.firstToken, "ExpressionPixelSelector not found in Symbol Table.");
		
		if(declaration.Type == Type.IMAGE) {
			expression_PixelSelector.Type = Type.INTEGER;
			//Implies that the index is not null. Hence visit its children //TODO uncomment the line below
			//expression_PixelSelector.index.visit(this, arg);
		}
			
		else if(expression_PixelSelector.index == null)
			expression_PixelSelector.Type = declaration.Type;
		else
			expression_PixelSelector.Type = null;
		if(expression_PixelSelector.Type == null)
			throw new SemanticException(expression_PixelSelector.firstToken, "PixelSelector type is null");
		return expression_PixelSelector;
	}

	@Override
	public Object visitExpression_Conditional(
			Expression_Conditional expression_Conditional, Object arg)
			throws Exception {
		
		Expression condition = expression_Conditional.condition;
		Expression trueExpression = expression_Conditional.trueExpression;
		Expression falseExpression = expression_Conditional.falseExpression;
		
		if(condition != null)
			condition.visit(this, null);
		if(trueExpression != null)
			trueExpression.visit(this, null);
		if(falseExpression != null)
			falseExpression.visit(this, null);
		
		if( !(condition.Type == Type.BOOLEAN && trueExpression.Type == falseExpression.Type) )
			throw new SemanticException(expression_Conditional.firstToken, "ExpressionTrue and ExpressionFalse have same Type");
		expression_Conditional.Type = trueExpression.Type;
		return expression_Conditional;
	}

	@Override
	public Object visitDeclaration_Image(Declaration_Image declaration_Image,
			Object arg) throws Exception {
		
		if(declaration_Image.xSize != null)
			declaration_Image.xSize.visit(this, null);
		if(declaration_Image.ySize != null)
			declaration_Image.ySize.visit(this, null);
		if(declaration_Image.source != null)
			declaration_Image.source.visit(this, null);
		
		if(symbolTable.lookupType(declaration_Image.name) != null)
			throw new SemanticException(declaration_Image.firstToken, "declaration_Image is already there in SymbolTable");
		else {
			symbolTable.insert(declaration_Image.name, declaration_Image);
			declaration_Image.Type = Type.IMAGE;
			if(declaration_Image.xSize != null) {
				if(declaration_Image.ySize == null || declaration_Image.xSize.Type != Type.INTEGER || declaration_Image.ySize.Type != Type.INTEGER)
					throw new SemanticException(declaration_Image.firstToken, "Declaration_Image requirement does not satisfy");
			}
		}
		return declaration_Image;
		
	}

	@Override
	public Object visitSource_StringLiteral(
			Source_StringLiteral source_StringLiteral, Object arg)
			throws Exception {
		
		try {
			URL url = new URL(source_StringLiteral.fileOrUrl);
			source_StringLiteral.Type = Type.URL;
		}
		catch(MalformedURLException e) {
			source_StringLiteral.Type = Type.FILE;
		}
		return source_StringLiteral;
	}

	@Override
	public Object visitSource_CommandLineParam(
			Source_CommandLineParam source_CommandLineParam, Object arg)
			throws Exception {
		if(source_CommandLineParam.paramNum != null) 
			source_CommandLineParam.paramNum.visit(this, null);
		
		source_CommandLineParam.Type = null;
		if(source_CommandLineParam.paramNum.Type != Type.INTEGER)
			throw new SemanticException(source_CommandLineParam.firstToken, "Source_CommandLineParam.Type is not an Integer");
		return source_CommandLineParam;
			
	}

	@Override
	public Object visitSource_Ident(Source_Ident source_Ident, Object arg)
			throws Exception {
		Declaration declaration;
		declaration = symbolTable.lookupType(source_Ident.name);
		if(declaration == null)
			throw new SemanticException(source_Ident.firstToken, "Source_ident type not found in Symbol Table.");
		source_Ident.Type = declaration.Type;
		if(!(source_Ident.Type == Type.FILE || source_Ident.Type == Type.URL))
			throw new SemanticException(source_Ident.firstToken, "Source_ident type is not FILE or URL");
		return source_Ident;
		
	}

	@Override
	public Object visitDeclaration_SourceSink(
			Declaration_SourceSink declaration_SourceSink, Object arg)
			throws Exception {
		
		if(declaration_SourceSink.source != null)
			declaration_SourceSink.source.visit(this, null);
		if(symbolTable.lookupType(declaration_SourceSink.name) != null)
			throw new SemanticException(declaration_SourceSink.firstToken, "Declaration_SourceSink is already there in map");
		else {
			symbolTable.insert(declaration_SourceSink.name, declaration_SourceSink);
			if(declaration_SourceSink.type == Kind.KW_file)
				declaration_SourceSink.Type = Type.FILE;
			else if(declaration_SourceSink.type == Kind.KW_url)
				declaration_SourceSink.Type = Type.URL;
			else
				throw new SemanticException(declaration_SourceSink.firstToken, "Declaration SourceSink Type is neither file or url");
			//discussion
			if(declaration_SourceSink.source.Type != declaration_SourceSink.Type && declaration_SourceSink.source.Type != null)
				throw new SemanticException(declaration_SourceSink.firstToken, "Declaration SourceSink Type and the Source Type does not match");
		}
		return declaration_SourceSink;		
	}

	@Override
	public Object visitExpression_IntLit(Expression_IntLit expression_IntLit,
			Object arg) throws Exception {
		expression_IntLit.Type = Type.INTEGER;
		return expression_IntLit;
	}

	@Override
	public Object visitExpression_FunctionAppWithExprArg(
			Expression_FunctionAppWithExprArg expression_FunctionAppWithExprArg,
			Object arg) throws Exception {
		
		if(expression_FunctionAppWithExprArg.arg != null)
			expression_FunctionAppWithExprArg.arg.visit(this, null);
		if(expression_FunctionAppWithExprArg.arg.Type != Type.INTEGER)
			throw new SemanticException(expression_FunctionAppWithExprArg.firstToken, "expression_FunctionAppWithExprArg is not an integer");
		expression_FunctionAppWithExprArg.Type = Type.INTEGER;
		return expression_FunctionAppWithExprArg;
	}

	@Override
	public Object visitExpression_FunctionAppWithIndexArg(
			Expression_FunctionAppWithIndexArg expression_FunctionAppWithIndexArg,
			Object arg) throws Exception {
		
		if(expression_FunctionAppWithIndexArg.arg != null)
			expression_FunctionAppWithIndexArg.arg.visit(this,null);
		expression_FunctionAppWithIndexArg.Type = Type.INTEGER;
		return expression_FunctionAppWithIndexArg;
	}

	@Override
	public Object visitExpression_PredefinedName(
			Expression_PredefinedName expression_PredefinedName, Object arg)
			throws Exception {
		
		expression_PredefinedName.Type = Type.INTEGER;
		return expression_PredefinedName;
	}

	@Override
	public Object visitStatement_Out(Statement_Out statement_Out, Object arg)
			throws Exception {
		
		if(statement_Out.sink != null)
			statement_Out.sink.visit(this, null);
		Declaration declaration = symbolTable.lookupType(statement_Out.name);
		if(declaration == null)
			throw new SemanticException(statement_Out.firstToken, "Statement_Out declaration not present in Symbol Table");
		statement_Out.setDec(declaration);
		if( !(((declaration.Type == Type.INTEGER || declaration.Type == Type.BOOLEAN) &&
			statement_Out.sink.Type == Type.SCREEN) ||
			(declaration.Type == Type.IMAGE && 
			(statement_Out.sink.Type == Type.FILE || statement_Out.sink.Type == Type.SCREEN))) )
			throw new SemanticException(statement_Out.firstToken, "Statement_Out doesn't meet requirements");
		return statement_Out;
	}

	@Override
	public Object visitStatement_In(Statement_In statement_In, Object arg)
			throws Exception {
		
		if(statement_In.source != null)
			statement_In.source.visit(this, null);
		Declaration declaration = symbolTable.lookupType(statement_In.name);
		statement_In.setDec(declaration);
		
		//TODO remove if and throw
		//if(declaration == null || declaration.Type != statement_In.source.Type)
		//	throw new SemanticException(statement_In.firstToken, "declaration of statementIn not present in Symbol Table or declaration type doesn't match statementIn source type");
		return statement_In;
	}

	@Override
	public Object visitStatement_Assign(Statement_Assign statement_Assign,
			Object arg) throws Exception {
		
		if(statement_Assign.lhs != null)
			statement_Assign.lhs.visit(this, null);
		if(statement_Assign.e != null)
			statement_Assign.e.visit(this, null);
		
		if( !((statement_Assign.lhs.Type == Type.IMAGE && statement_Assign.e.Type == Type.INTEGER) || 
			(statement_Assign.lhs.Type == statement_Assign.e.Type) ))
				throw new SemanticException(statement_Assign.firstToken, "lhs type and expression type is not same in Statement_Assign");
		statement_Assign.setCartesian(statement_Assign.lhs.isCartesian);
		return statement_Assign;
	}

	@Override
	public Object visitLHS(LHS lhs, Object arg) throws Exception {
		
		if(lhs.index != null)
			lhs.index.visit(this,null);
		Declaration declaration = null;
		declaration = symbolTable.lookupType(lhs.name);
		
		if(declaration == null)
			throw new SemanticException(lhs.firstToken, "LHS Declaration not found in Symbol Table.");
		
		lhs.declaration = declaration;
		lhs.Type = declaration.Type;
		if(lhs.index != null) {
			lhs.isCartesian = lhs.index.isCartesian();
		}
		else
			lhs.isCartesian = false;
		return lhs;
		
		
	}

	@Override
	public Object visitSink_SCREEN(Sink_SCREEN sink_SCREEN, Object arg)
			throws Exception {
		
		sink_SCREEN.Type = Type.SCREEN;
		return sink_SCREEN;
	}

	@Override
	public Object visitSink_Ident(Sink_Ident sink_Ident, Object arg)
			throws Exception {
		
		Declaration declaration = symbolTable.lookupType(sink_Ident.name);
		if(declaration == null)
			throw new SemanticException(sink_Ident.firstToken, "Sink_Ident Declaration not found in Symbol Table.");
		sink_Ident.Type = declaration.Type;
		if(sink_Ident.Type != Type.FILE)
			throw new SemanticException(sink_Ident.firstToken,"Sink_ident type is not file.");
		return sink_Ident;
	}

	@Override
	public Object visitExpression_BooleanLit(
			Expression_BooleanLit expression_BooleanLit, Object arg)
			throws Exception {
		
		expression_BooleanLit.Type = Type.BOOLEAN;
		return expression_BooleanLit;
	}

	@Override
	public Object visitExpression_Ident(Expression_Ident expression_Ident,
			Object arg) throws Exception {
		
		Declaration declaration = null;
		declaration = symbolTable.lookupType(expression_Ident.name);
		
		if(declaration == null)
			throw new SemanticException(expression_Ident.firstToken, "Expression ident Declaration not found in Symbol Table.");
		
		expression_Ident.Type = declaration.Type;
		return expression_Ident;
	}

}
