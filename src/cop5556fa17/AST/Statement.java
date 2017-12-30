package cop5556fa17.AST;

import cop5556fa17.TypeUtils;
import cop5556fa17.Scanner.Token;

public abstract class Statement extends ASTNode {
	public TypeUtils.Type Type;
	public Statement(Token firstToken) {
		super(firstToken);
	}

}
