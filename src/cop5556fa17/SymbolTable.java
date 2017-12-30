package cop5556fa17;

import java.util.HashMap;
import java.util.ListIterator;
import java.util.Stack;

import cop5556fa17.TypeCheckVisitor.SemanticException;
import cop5556fa17.AST.Declaration;

public class SymbolTable {
	HashMap<String, Declaration> map; 

	public SymbolTable() {
		map = new HashMap<>();
	}

	public boolean insert(String ident, Declaration dec){
		if(map.containsKey(ident))
			return false;
		else {
			map.put(ident, dec);
			return true;
		}	
	}
	
	public Declaration lookupType(String ident){
		if(map.containsKey(ident)){
			return map.get(ident);
		}
		else
			return null;		
	}	
}
