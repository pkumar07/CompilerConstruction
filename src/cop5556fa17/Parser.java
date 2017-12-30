package cop5556fa17;



import java.util.ArrayList;
import java.util.Arrays;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.Token;
import cop5556fa17.Parser.SyntaxException;
import cop5556fa17.AST.*;

import static cop5556fa17.Scanner.Kind.*;

public class Parser {

	@SuppressWarnings("serial")
	public class SyntaxException extends Exception {
		Token t;

		public SyntaxException(Token t, String message) {
			super(message);
			this.t = t;
		}

	}


	Scanner scanner;
	Token t;

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}

	/**
	 * Main method called by compiler to parser input.
	 * Checks for EOF
	 * 
	 * @throws SyntaxException
	 */
	public Program parse() throws SyntaxException {
		Program p = program();
		matchEOF();
		return p;
	}
	

	/**
	 * Program ::=  IDENTIFIER   ( Declaration SEMI | Statement SEMI )*   
	 * 
	 * Program is start symbol of our grammar.
	 * 
	 * @throws SyntaxException
	 */
	
	Token consume() throws SyntaxException {  //Check the SyntaxException poornima
		Token temp = t;
		t =  scanner.nextToken();
		return temp;
	}
	

	
	Program program() throws SyntaxException {
		//TODO  implement this
		//throw new UnsupportedOperationException();
		
		Token firstToken = t;
		Token name = null;
		ArrayList<ASTNode> decsAndStatements = new ArrayList<>();
		if(t.kind == IDENTIFIER) {
			name = t;
			consume();

			while(t.kind == KW_int || t.kind == KW_boolean ||
					t.kind == KW_image || t.kind == KW_url || t.kind== KW_file ||
					t.kind == IDENTIFIER) {
				if(t.kind == KW_int || t.kind == KW_boolean ||
					t.kind == KW_image || t.kind == KW_url ||
					t.kind== KW_file ) {
					decsAndStatements.add(declaration());
				}
				else if(t.kind == IDENTIFIER) 
					decsAndStatements.add(statement());
				
				if(t.kind == SEMI)
					consume();
				else
					throw new SyntaxException(t, "Invalid program token found at position " + t.pos_in_line + " line " + t.line);
								
			}
		}
		else
			throw new SyntaxException(t, "Invalid program token found at position " + t.pos_in_line + " line " + t.line);
		
		return new Program(firstToken, name, decsAndStatements);
		
		
	}
	
	Declaration declaration() throws SyntaxException{
		Declaration d = null;
		if(t.kind == KW_int || t.kind == KW_boolean) {
			d = variableDeclaration();
		}
		else if( t.kind == KW_image) {
			d = imageDeclaration();
		}
		else if( t.kind == KW_url || t.kind== KW_file) {
			d = sourceSinkDeclaration();
		}
		else
			throw new SyntaxException(t, "Invalid declaration token found at position " + t.pos_in_line + " line " + t.line);
		return d;
	}
	
	Declaration_Variable variableDeclaration() throws SyntaxException{
		Token firstToken = t;
		Token type = t;
		Token name = null;
		Expression e = null;
		varType();
		if( t.kind == IDENTIFIER ) {
			name = t;
			consume();
			if( t.kind == OP_ASSIGN ) {
				consume();
				e = expression();
			}
			//No else
		}
		else 
			throw new SyntaxException(t, "Invalid variable declaration  token found at position " + t.pos_in_line + " line " + t.line);
		return new Declaration_Variable(firstToken, type, name, e);
			

	}
	
	
	void varType() throws SyntaxException {
		
		if(t.kind == KW_int || t.kind == KW_boolean )
			consume();
		else
			throw new SyntaxException(t, "Invalid var type token found at position " + t.pos_in_line + " line " + t.line);
	}
	
	Declaration_SourceSink sourceSinkDeclaration() throws SyntaxException {
			Token firstToken = t;
			Token type = null;
			Token name = null;
			Source source = null;
			type = t;
			sourceSinkType();
			if( t.kind == IDENTIFIER) {
				name = t;
				consume();
				if(t.kind == OP_ASSIGN) {
					consume();
					source = source();
				}
				else
					throw new SyntaxException(t, "Invalid sourceSinkDeclaration token found at position " + t.pos_in_line + " line " + t.line);
				return new Declaration_SourceSink(firstToken, type, name, source);
			}
			else
				throw new SyntaxException(t, "Invalid sourceSinkDeclaration token found at position " + t.pos_in_line + " line " + t.line);
		
		
	}
	
	Source source() throws SyntaxException {
		Token firstToken = t;
		Source s = null;
		
		Expression paramNum = null;
		if(t.kind == STRING_LITERAL) {
			s = new Source_StringLiteral(firstToken,t.getText());
			consume();
		}	
		else if(t.kind == OP_AT) {
			consume();
			paramNum = expression();
			s = new Source_CommandLineParam(firstToken, paramNum); 
		}
		else if(t.kind == IDENTIFIER) {
			s = new Source_Ident(firstToken, t);
			consume();
		}
			
		else
			throw new SyntaxException(t, "Invalid source token found at position " + t.pos_in_line + " line " + t.line);
		return s;
	}

	void sourceSinkType() throws SyntaxException {
		
		if(t.kind == KW_url || t.kind == KW_file)
			consume();
		else
			throw new SyntaxException(t, "Invalid sourceSinkType token found at position " + t.pos_in_line + " line " + t.line);
	}
	
	Declaration_Image imageDeclaration() throws SyntaxException{
		
		Token firstToken = t;
		Expression xSize = null;
		Expression ySize = null;
		Token name = null;
		Source source = null;
		if(t.kind == KW_image) {
			consume();
			if(t.kind == LSQUARE) {
				consume();
				xSize = expression();
				if(t.kind == COMMA)
					consume();
				else
					throw new SyntaxException(t, "Invalid imageDeclaration token found at position " + t.pos_in_line + " line " + t.line);
				ySize = expression();
				if(t.kind == RSQUARE)
					consume();
				else
					throw new SyntaxException(t, "Invalid imageDeclaration token found at position " + t.pos_in_line + " line " + t.line);
			}
			//No else
			if(t.kind == IDENTIFIER) {
				name = t;
				consume();
				if(t.kind == OP_LARROW) {
					consume();
					source = source();
				}
				//No else
			}
			else
				throw new SyntaxException(t, "Invalid imageDeclaration token found at position " + t.pos_in_line + " line " + t.line);
				
		}
		else
			throw new SyntaxException(t, "Invalid imageDeclaration token found at position " + t.pos_in_line + " line " + t.line);
		return new Declaration_Image(firstToken, xSize, ySize, name, source);
	}
	
	Statement statement() throws SyntaxException{
		Statement s = null;
		if( t.kind == IDENTIFIER ) {
			Token temp = scanner.peek();
			if(temp.kind == OP_RARROW) {
				//ImageOutStatement
				s = imageOutStatement();
			}
			else if(temp.kind == OP_LARROW) {
				s = imageInStatement();
			}
			else 
				s = assignmentStatement();
		}
		else 
			throw new SyntaxException(t, "Invalid statement token found at position " + t.pos_in_line + " line " + t.line);
		return s;
	
	}
	
	Statement_Out imageOutStatement() throws SyntaxException {
		Token firstToken = t;
		Token name = null;
		Sink sink = null;
		if(t.kind == IDENTIFIER) {
			name = t;
			consume();
			if(t.kind == OP_RARROW) {
				consume();
				sink = sink();
			}
			else {
				throw new SyntaxException(t, "Invalid imageOutStatement token found at position " + t.pos_in_line + " line " + t.line);
			}
			return new Statement_Out(firstToken, name, sink);	
		}
		else
			throw new SyntaxException(t, "Invalid imageOutStatement token found at position " + t.pos_in_line + " line " + t.line);
	}
	
	Sink sink() throws SyntaxException {
		Token firstToken = t;
		Sink s = null;
		if(t.kind == IDENTIFIER) {
			s = new Sink_Ident(firstToken, t);
			consume();	
		}
		else if( t.kind == KW_SCREEN) {
			s = new Sink_SCREEN(firstToken);
			consume();
		}	
		else
			throw new SyntaxException(t, "Invalid sink token found at position " + t.pos_in_line + " line " + t.line);
		return s;
	}
	
	Statement_In imageInStatement() throws SyntaxException {
		Token firstToken = t;
		Token name = null;
		Source source = null;
		if(t.kind == IDENTIFIER) {
			name = t;
			consume();
			if(t.kind == OP_LARROW) {
				consume();
				source = source();
			}
			else
				throw new SyntaxException(t, "Invalid imageInStatement token found at position " + t.pos_in_line + " line " + t.line);
			return new Statement_In(firstToken, name, source);
		}
		else
			throw new SyntaxException(t, "Invalid imageInStatement token found at position " + t.pos_in_line + " line " + t.line);
		
	}
	
	Statement_Assign assignmentStatement() throws SyntaxException{
		Token firstToken = t;
		LHS lhs = null;
		Expression e = null;
		lhs = lhs();
		if(t.kind == OP_ASSIGN) {
			consume();
			e = expression();
			
		}
		else
			throw new SyntaxException(t, "Invalid aassignmentStatement token found at position " + t.pos_in_line + " line " + t.line);
		return new Statement_Assign(firstToken, lhs, e);
	}

	/**
	 * Expression ::=  OrExpression  OP_Q  Expression OP_COLON Expression    | OrExpression
	 * 
	 * Our test cases may invoke this routine directly to support incremental development.
	 * 
	 * @throws SyntaxException
	 */
	Expression expression() throws SyntaxException {
		//TODO implement this.
		//throw new UnsupportedOperationException();
		Token firstToken = t;
		Expression trueExpression = null;
		Expression falseExpression = null;
				
		Expression condition = orExpression();
		if( t.kind == OP_Q) {
			consume();
			trueExpression = expression();
			if(t.kind == OP_COLON) {
				consume();
				falseExpression = expression();
			}
				
			else
				throw new SyntaxException(t, "Invalid expression token found at position " + t.pos_in_line + " line " + t.line);
			return new Expression_Conditional(firstToken,condition, trueExpression, falseExpression);
		}
		//No else
		return condition;
		
	}
	

	Expression orExpression() throws SyntaxException {
		Token firstToken = t;
		Expression e0 = null;
		Token op = null;
		Expression e1 = null;
		e0 = andExpression();
		while( t.kind == OP_OR) {
			op = t;
			consume();
			e1 = andExpression();
			e0 = new Expression_Binary(firstToken, e0, op, e1);
		}
		return e0;
	}

	Expression andExpression() throws SyntaxException {
		Token firstToken = t;
		Expression e0 = null;
		Token op = null;
		Expression e1 = null;
		
		e0 = eqExpression();
		while( t.kind == OP_AND) {
			op = t;
			consume();
			e1 = eqExpression();
			e0 = new Expression_Binary(firstToken, e0, op, e1);
		}
		return e0;

	}
	
	Expression eqExpression() throws SyntaxException {
		Token firstToken = t;
		Expression e0 = null;
		Token op = null;
		Expression e1 = null;
		
		e0 = relExpression();
		while(t.kind == OP_EQ || t.kind ==  OP_NEQ ) {
			op = t;
			consume();
			e1 = relExpression();
			e0 = new Expression_Binary(firstToken, e0, op, e1);
		}
		return e0;
	}
	
	Expression relExpression() throws SyntaxException {
		Token firstToken = t;
		Expression e0 = null;
		Token op = null;
		Expression e1 = null;
		
		e0 = addExpression();
		while(t.kind == OP_LT || t.kind ==  OP_GT || t.kind == OP_LE|| t.kind == OP_GE) {
			op = t;
			consume();
			e1 = addExpression();
			e0 = new Expression_Binary(firstToken, e0, op, e1);
		}
		return e0;
	}
	
	Expression addExpression() throws SyntaxException {
		Token firstToken = t;
		Expression e0 = null;
		Token op = null;
		Expression e1 = null;
		
		e0 = multExpression();
		while(t.kind == OP_PLUS || t.kind == OP_MINUS) {
			op = t;
			consume();
			e1 = multExpression();
			e0 = new Expression_Binary(firstToken, e0, op, e1);
			
		}
		return e0;

	}
	Expression multExpression() throws SyntaxException {
		Token firstToken = t;
		Expression e0 = null;
		Token op = null;
		Expression e1 = null;
		
		e0 = unaryExpression();
		while( t.kind == OP_TIMES || t.kind == OP_DIV || t.kind == OP_MOD) {
			op = t;
			consume();
			e1 = unaryExpression();
			e0 = new Expression_Binary(firstToken, e0, op, e1);
			
		}
		return e0;
		
	}
	
	Expression unaryExpression() throws SyntaxException {
		
		Token firstToken = t;
		
		if(t.kind == OP_PLUS) {
			Token op = t;
			consume();
			Expression e = unaryExpression();
			return new Expression_Unary(firstToken, op, e);
		}
		else if(t.kind == OP_MINUS) {
			Token op = t;
			consume();
			Expression e = unaryExpression();
			return new Expression_Unary(firstToken, op, e);
		}
		else {
			
			return unaryExpressionNotPlusMinus();
			
		}
						
	}
	
	
	
	Expression unaryExpressionNotPlusMinus() throws SyntaxException {
		Token firstToken = t;
			 
		if(t.kind == OP_EXCL) {
			Token op = t;
			consume();
			Expression e = unaryExpression();
			return new Expression_Unary(firstToken, op, e);
		}
		else if(t.kind == INTEGER_LITERAL ||   t.kind == LPAREN || 
				 t.kind == KW_sin || t.kind == KW_cos || t.kind == KW_atan || 
					t.kind == KW_abs || t.kind == KW_cart_x || t.kind == KW_cart_y || 
					t.kind == KW_polar_a || t.kind == KW_polar_r || t.kind == BOOLEAN_LITERAL ) {
			Expression e = primary();
			return e;
		}
			
		
		else if(t.kind == IDENTIFIER) {
			Expression e = identOrPixelSelectorExpression();
			return e;
		}
			
		else if( t.kind == KW_x || t.kind == KW_y || t.kind == KW_r ||
				t.kind == KW_a || t.kind == KW_X || t.kind == KW_Y || 
				t.kind == KW_Z || t.kind == KW_A || t.kind == KW_R ||
				t.kind == KW_DEF_X || t.kind == KW_DEF_Y) {
			Kind kind = t.kind;
			consume();
			return new Expression_PredefinedName(firstToken, kind);
		}
			
		else
			throw new SyntaxException(t, "Invalid unaryExpressionNotPlusMinus token found at position " + t.pos_in_line + " line " + t.line);

	}
	
	Expression primary() throws SyntaxException {
		Token firstToken = t;
		if( t.kind == INTEGER_LITERAL) {
			consume();
			return new Expression_IntLit(firstToken, firstToken.intVal());
		}
			
		else if( t.kind == LPAREN) {
			consume();
			Expression e = expression();
			if(t.kind == RPAREN )
				consume();
			else 
				throw new SyntaxException(t, "Invalid primary token found at position " + t.pos_in_line + " line " + t.line);
			return e;
		}
		else if( t.kind == KW_sin || t.kind == KW_cos || t.kind == KW_atan || 
				t.kind == KW_abs || t.kind == KW_cart_x || t.kind == KW_cart_y || 
				t.kind == KW_polar_a || t.kind == KW_polar_r ) {
			Expression e = functionApplication();
			return e;
		}
		else if( t.kind == BOOLEAN_LITERAL) {
			consume();
			return new Expression_BooleanLit(firstToken, Boolean.parseBoolean(firstToken.getText()));
		}
			
		
		else 
			throw new SyntaxException(t, "Invalid primary token found at position " + t.pos_in_line + " line " + t.line);
		
	}
	
	Expression identOrPixelSelectorExpression() throws SyntaxException {
		Token firstToken = t;
		Token name = null;
		
		if(t.kind == IDENTIFIER) {
			name = t;
			consume();
			if(t.kind == LSQUARE) {
				consume();
				Index index = selector();
				if(t.kind == RSQUARE)
					consume();
				else
					throw new SyntaxException(t, "Invalid identOrPixelSelectorExpression token found at position " + t.pos_in_line + " line " + t.line);
				return new Expression_PixelSelector(firstToken, name, index);
			}
			return new Expression_Ident(firstToken, name);
			//No corresponding else because of epsilon
		}
		else
			throw new SyntaxException(t, "Invalid identOrPixelSelectorExpression token found at position " + t.pos_in_line + " line " + t.line);
	}
	
	LHS lhs() throws SyntaxException {
		Token firstToken = t;
		Token name = null;
		Index index = null;
		
		if(t.kind == IDENTIFIER) {
			name = t;
			consume();
			if(t.kind == LSQUARE) {
				consume();
				index = lhsSelector();
				if(t.kind == RSQUARE)
					consume();
				else
					throw new SyntaxException(t, "Invalid lhs token found at position " + t.pos_in_line + " line " + t.line);
			}
			//No else because of epsilon
		}
		else
			throw new SyntaxException(t, "Invalid lhs token found at position " + t.pos_in_line + " line " + t.line);
		return new LHS(firstToken, name, index);
	}
	
	
	Expression_FunctionApp functionApplication() throws SyntaxException {
		Token firstToken = t;  
			functionName();
			if( t.kind == LPAREN) {
				consume();
				Expression arg = expression();
				if(t.kind == RPAREN)
					consume();
				else
					throw new SyntaxException(t, "Invalid functionApplication token found at position " + t.pos_in_line + " line " + t.line);
				return new Expression_FunctionAppWithExprArg(firstToken, firstToken.kind, arg);
			}
			else if( t.kind == LSQUARE) {
				consume();
				Index index = selector();
				if(t.kind == RSQUARE)
					consume();
				else
					throw new SyntaxException(t, "Invalid functionApplication token found at position " + t.pos_in_line + " line " + t.line);
				return new Expression_FunctionAppWithIndexArg(firstToken, firstToken.kind, index);
			}
			else
				throw new SyntaxException(t, "Invalid functionApplication token found at position " + t.pos_in_line + " line " + t.line);
	}
	
	void functionName() throws SyntaxException {
		
		if( t.kind == KW_sin || t.kind == KW_cos || t.kind == KW_atan || 
			t.kind == KW_abs || t.kind == KW_cart_x || t.kind == KW_cart_y || 
			t.kind == KW_polar_a || t.kind == KW_polar_r )
			consume();
		else 
			throw new SyntaxException(t, "Invalid functionName token found at position " + t.pos_in_line + " line " + t.line);
	}
	
	Index lhsSelector() throws SyntaxException {
		Index index = null;
		
		if(t.kind == LSQUARE) {
			consume();
			if(t.kind == KW_x) {
				index = xySelector();
				if(t.kind == RSQUARE)
					consume();
				else 
					throw new SyntaxException(t, "Invalid lhsSelector token found at position " + t.pos_in_line + " line " + t.line);
			}
			else if(t.kind == KW_r) {
				index = raSelector();
				if(t.kind == RSQUARE)
					consume();
				else 
					throw new SyntaxException(t, "Invalid lhsSelector token found at position " + t.pos_in_line + " line " + t.line);
			}
			else
				throw new SyntaxException(t, "Invalid lhsSelector token found at position " + t.pos_in_line + " line " + t.line);
			
		}
		else
			throw new SyntaxException(t, "Invalid lhsSelector token found at position " + t.pos_in_line + " line " + t.line);
		return index;

	}
	
	
	Index xySelector() throws SyntaxException {
		Token firstToken = t;
		Expression e0 = null;
		Expression e1 = null;
		if(t.kind == KW_x) {
			e0 = new Expression_PredefinedName(firstToken, firstToken.kind);
			consume();
			if(t.kind == COMMA) {
				consume();
				if(t.kind == KW_y) {
					e1 = new Expression_PredefinedName(t, t.kind);
					consume();
				}
				else
					throw new SyntaxException(t, "Invalid xySelector token found at position " + t.pos_in_line + " line " + t.line);
			}				
			else
				throw new SyntaxException(t, "Invalid xySelector token found at position " + t.pos_in_line + " line " + t.line);
			return new Index(firstToken, e0, e1);
		}
		else
			throw new SyntaxException(t, "Invalid xySelector token found at position " + t.pos_in_line + " line " + t.line);
	}
	
	Index raSelector() throws SyntaxException {
		Expression e0 = null;
		Expression e1 = null;
		Token firstToken = t;
		if(t.kind == KW_r) {
			e0 = new Expression_PredefinedName(firstToken, firstToken.kind);
			consume();
			if(t.kind == COMMA) {
				consume();
				//Changed here
				if(t.kind == KW_a) {
					e1 = new Expression_PredefinedName(t, t.kind);
					consume();
				}
				else
					throw new SyntaxException(t, "Invalid raSelector token found at position " + t.pos_in_line + " line " + t.line);
			}				
			else
				throw new SyntaxException(t, "Invalid raSelector token found at position " + t.pos_in_line + " line " + t.line);
			return new Index(firstToken, e0, e1);
		}
		else
			throw new SyntaxException(t, "Invalid raSelector token found at position " + t.pos_in_line + " line " + t.line);
	}
	
	
	Index selector() throws SyntaxException {
		Expression e0 = null;
		Expression e1 = null;
		Token firstToken = t;
		e0 = expression();
		if(t.kind == COMMA) {
			consume();
			e1 = expression();
		}
		else
			throw new SyntaxException(t, "Invalid selector token found at position " + t.pos_in_line + " line " + t.line);
		return new Index(firstToken, e0, e1);
	}
	

	/**
	 * Only for check at end of program. Does not "consume" EOF so no attempt to get
	 * nonexistent next Token.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	 private Token matchEOF() throws SyntaxException {
		if (t.kind == EOF) {
			return t;
		}
		String message =  "Expected EOL at " + t.line + ":" + t.pos_in_line;
		throw new SyntaxException(t, message);
	}
}
