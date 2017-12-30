package cop5556fa17;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556fa17.Scanner.LexicalException;
import cop5556fa17.Scanner.Token;
import cop5556fa17.Parser.SyntaxException;

import static cop5556fa17.Scanner.Kind.*;

public class SimpleParserTest {

	//set Junit to be able to catch exceptions
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	
	//To make it easy to print objects and turn this output on and off
	static final boolean doPrint = true;
	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}


	/**
	 * Simple test case with an empty program.  This test 
	 * expects an SyntaxException because all legal programs must
	 * have at least an identifier
	 *   
	 * @throws LexicalException
	 * @throws SyntaxException 
	 */
	@Test
	public void testEmpty() throws LexicalException, SyntaxException {
		String input = "";  //The input is the empty string.  This is not legal
		show(input);        //Display the input 
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
		parser.parse();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	
	/** Another example.  This is a legal program and should pass when 
	 * your parser is implemented.
	 * 
	 * @throws LexicalException
	 * @throws SyntaxException
	 */

	@Test
	public void testDec1() throws LexicalException, SyntaxException {
		String input = "prog int k;";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		parser.parse();
	}
	

	/**
	 * This example invokes the method for expression directly. 
	 * Effectively, we are viewing Expression as the start
	 * symbol of a sub-language.
	 *  
	 * Although a compiler will always call the parse() method,
	 * invoking others is useful to support incremental development.  
	 * We will only invoke expression directly, but 
	 * following this example with others is recommended.  
	 * 
	 * @throws SyntaxException
	 * @throws LexicalException
	 */
	@Test
	public void expression1() throws SyntaxException, LexicalException {
		String input = "2";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);  
		parser.expression();  //Call expression directly.  
	}
	
	
	@Test
	public void testlhs1() throws LexicalException, SyntaxException {
		String input = "abc";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		parser.lhs();
	}
	
	@Test
	public void testlhs2() throws LexicalException, SyntaxException {
		String input = "abc [[x,y]]";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		parser.lhs();
	}
	
	@Test
	public void testlhs3() throws LexicalException, SyntaxException {
		String input = "abc [[r,A]]";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		parser.lhs();
	}
	
	@Test
	public void testlhs4() throws LexicalException, SyntaxException {
		String input = "abc [[]]";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		thrown.expect(SyntaxException.class);
		try {
			parser.lhs();  //Parse the program
			}
			catch (SyntaxException e) {
				show(e);
				throw e;
			}
	}
	
	@Test
	public void testlhs5() throws LexicalException, SyntaxException {
		String input = "abc t";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		thrown.expect(SyntaxException.class);
		try {
			parser.parse();  //Parse the program
			}
			catch (SyntaxException e) {
				show(e);
				throw e;
			}
	}
		
	@Test
	public void testparser1() throws LexicalException, SyntaxException {
		String input = "abc=8 ";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		thrown.expect(SyntaxException.class);
		try {
			parser.parse();  //Parse the program
			}
			catch (SyntaxException e) {
				show(e);
				throw e;
			}
	}
	
	@Test
	public void testparser2() throws LexicalException, SyntaxException {
		String input = "abc hello";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		thrown.expect(SyntaxException.class);
		try {
			parser.parse();  //Parse the program
			}
			catch (SyntaxException e) {
				show(e);
				throw e;
			}
	}
	
	@Test
	public void testImageInStatement1() throws LexicalException, SyntaxException {
		String input = "gator <- gator3_uo";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		parser.imageInStatement();
	}
	
	@Test
	public void testImageInStatement2() throws LexicalException, SyntaxException {
		String input = "gator <- 123";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		thrown.expect(SyntaxException.class);
		try {
			parser.imageInStatement();
			}
			catch (SyntaxException e) {
				show(e);
				throw e;
			}
	}
	
	@Test
	public void testImageInStatement3() throws LexicalException, SyntaxException {
		String input = "gator_123 <- @+!+-1234";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		parser.imageInStatement();
	}
	
	@Test
	public void testImageInStatement4() throws LexicalException, SyntaxException {
		String input = "_gatorUF <-_tomato";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		parser.imageInStatement();
	}
	
	@Test
	public void testImageInStatement5() throws LexicalException, SyntaxException {
		String input = "_gator$UF <- \"mango\"";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		parser.imageInStatement();
	}
	
	@Test
	public void testImageInStatement6() throws LexicalException, SyntaxException {
		String input = "_gator$UF <- @true*true";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		parser.imageInStatement();
	}
	
	@Test
	public void testUnaryExpression1() throws LexicalException, SyntaxException {
		String input = "-+x";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		parser.unaryExpression();
	}
	
	@Test
	public void testUnaryExpression2() throws LexicalException, SyntaxException {
		String input = "-234";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		parser.unaryExpression();
	}
	
	@Test
	public void testUnaryExpression3() throws LexicalException, SyntaxException {
		String input = "cool [ x, !!!abc * !!!abc? !!!abc : !!!abc ";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		thrown.expect(SyntaxException.class);
		try {
			parser.unaryExpression();
			}
			catch (SyntaxException e) {
				show(e);
				throw e;
			}
	}
	
	@Test
	public void testUnaryExpression4() throws LexicalException, SyntaxException {
		String input = "cool [ x, !!!abc * !!!abc? !!!abc";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		thrown.expect(SyntaxException.class);
		try {
			parser.unaryExpression();
			}
			catch (SyntaxException e) {
				show(e);
				throw e;
			}
	}
	
	@Test
	public void testImageOutStatement1() throws LexicalException, SyntaxException {
		String input = "parking -> park";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
	
		try {
			parser.imageOutStatement();
			}
			catch (SyntaxException e) {
				show(e);
				throw e;
			}
	}
	
	@Test
	public void testFunctionApplication() throws LexicalException, SyntaxException {
		String input = "sin (truefalse <  | !x == -!+A ? !x == -!+A :!x == -!+A)";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		thrown.expect(SyntaxException.class);
		try {
			parser.functionApplication();  //Parse the program
			}
			catch (SyntaxException e) {
				show(e);
				throw e;
			}
	}
	
	@Test
	public void testFunctionApplication2() throws LexicalException, SyntaxException {
		String input = "sin (true * false <  | !x == -!+A ? !x == -!+A :!x == -!+A)";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		thrown.expect(SyntaxException.class);
		try {
			parser.functionApplication();  //Parse the program
			}
			catch (SyntaxException e) {
				show(e);
				throw e;
			}
	}
	
	
	@Test
	public void testFunctionprogram() throws LexicalException, SyntaxException {
		String input = "mango \n url $gogreen = \"getmesomething\"; \nlhs = red <= green | violet != blue & 789 * 123| mind % 78;\r\nboolean hop = Z + Y;\nimage [cart_x (hundred),flower ] give <- strawberry;\r\nlost -> symbol;\nparty <- @+-false;\r\nint street ;\ngame [[x,y]] = true;\nfile blueberry = strawberry;\r\n";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		
		try {
			parser.parse();  //Parse the program
			}
			catch (SyntaxException e) {
				show(e);
				throw e;
			}
	}
	
}

