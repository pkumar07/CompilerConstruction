package cop5556fa17;

import java.util.ArrayList;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

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
import cop5556fa17.AST.Source;
import cop5556fa17.AST.Source_CommandLineParam;
import cop5556fa17.AST.Source_Ident;
import cop5556fa17.AST.Source_StringLiteral;
import cop5556fa17.AST.Statement_In;
import cop5556fa17.AST.Statement_Out;
import cop5556fa17.Scanner.Kind;
import cop5556fa17.AST.Statement_Assign;
//import cop5556fa17.image.ImageFrame;
//import cop5556fa17.image.ImageSupport;

public class CodeGenVisitor implements ASTVisitor, Opcodes {

	/**
	 * All methods and variable static.
	 */


	/**
	 * @param DEVEL
	 *            used as parameter to genPrint and genPrintTOS
	 * @param GRADE
	 *            used as parameter to genPrint and genPrintTOS
	 * @param sourceFileName
	 *            name of source file, may be null.
	 */
	public CodeGenVisitor(boolean DEVEL, boolean GRADE, String sourceFileName) {
		super();
		this.DEVEL = DEVEL;
		this.GRADE = GRADE;
		this.sourceFileName = sourceFileName;
	}

	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;

	MethodVisitor mv; // visitor of method currently under construction
	FieldVisitor fv;
	
	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;
	


	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		className = program.name;  
		classDesc = "L" + className + ";";
		String sourceFileName = (String) arg;
		cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object", null);
		cw.visitSource(sourceFileName, null);
		// create main method
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
		// initialize
		mv.visitCode();		
		//add label before first instruction
		Label mainStart = new Label();
		mv.visitLabel(mainStart);		
		// if GRADE, generates code to add string to log
		//CodeGenUtils.genLog(GRADE, mv, "entering main");

		// visit decs and statements to add field to class
		//  and instructions to main method, respectivley
		ArrayList<ASTNode> decsAndStatements = program.decsAndStatements;
		for (ASTNode node : decsAndStatements) {
			node.visit(this, arg);
		}

		//generates code to add string to log
		//CodeGenUtils.genLog(GRADE, mv, "leaving main");
		
		//adds the required (by the JVM) return statement to main
		mv.visitInsn(RETURN);
		
		//adds label at end of code
		Label mainEnd = new Label();
		mv.visitLabel(mainEnd);
		
		//handles parameters and local variables of main. Right now, only args
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart, mainEnd, 0);

		//mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart, mainEnd, 0);
		fv = cw.visitField(ACC_STATIC, "x", "I", null, 0);
		fv.visitEnd();
		fv = cw.visitField(ACC_STATIC, "y", "I", null, 0);
		fv.visitEnd();
		fv = cw.visitField(ACC_STATIC, "r", "I", null, new Integer(0));
		fv.visitEnd();
		fv = cw.visitField(ACC_STATIC, "a", "I", null, new Integer(0));
		fv.visitEnd();
		fv = cw.visitField(ACC_STATIC, "Z", "I", null, new Integer(0xFFFFFF));
		fv.visitEnd();
		fv = cw.visitField(ACC_STATIC, "X", "I", null, 0);
		fv.visitEnd();
		fv = cw.visitField(ACC_STATIC, "Y", "I", null, 0);
		fv.visitEnd();
		fv = cw.visitField(ACC_STATIC, "R", "I", null, new Integer(0));
		fv.visitEnd();
		fv = cw.visitField(ACC_STATIC, "A", "I", null, new Integer(0));
		fv.visitEnd();
		fv = cw.visitField(ACC_STATIC, "DEF_X", "I", null, new Integer(256));
		fv.visitEnd();
		fv = cw.visitField(ACC_STATIC, "DEF_Y", "I", null, new Integer(256));
		fv.visitEnd();
		
		
		
		
		//Sets max stack size and number of local vars.
		//Because we use ClassWriter.COMPUTE_FRAMES as a parameter in the constructor,
		//asm will calculate this itself and the parameters are ignored.
		//If you have trouble with failures in this routine, it may be useful
		//to temporarily set the parameter in the ClassWriter constructor to 0.
		//The generated classfile will not be correct, but you will at least be
		//able to see what is in it.
		mv.visitMaxs(0, 0);
		
		//terminate construction of main method
		mv.visitEnd();
		
		//terminate class construction
		cw.visitEnd();

		//generate classfile as byte array and return
		return cw.toByteArray();
	}
	@Override
	public Object visitDeclaration_Variable(Declaration_Variable declaration_Variable, Object arg) throws Exception {
		
		if(declaration_Variable.e != null)
			declaration_Variable.e.visit(this, arg);
		
		String desc = "";
		Type type = declaration_Variable.Type;
		int opcode = ACC_STATIC;
		String name = declaration_Variable.name;
		
		switch(type) {
			case BOOLEAN:
					desc = "Z";
					fv = cw.visitField(opcode, name, desc, null, new Boolean(false));
					fv.visitEnd();
					break;
				
			case INTEGER:
					desc = "I";
					fv = cw.visitField(opcode, name, desc, null, new Integer(0));
					fv.visitEnd();
					break;	
			default:
				    break;
		}		
		if(declaration_Variable.e != null)
			mv.visitFieldInsn(PUTSTATIC, className, declaration_Variable.name, desc);
		
		return null;
			
	}

	@Override
	public Object visitExpression_Binary(Expression_Binary expression_Binary, Object arg) throws Exception {
		expression_Binary.e0.visit(this, arg);
		expression_Binary.e1.visit(this, arg);
		
		Label startLabel = new Label();
		Label endLabel = new Label();
		
		boolean is_Relational = false;
		Kind kind = expression_Binary.op;
		
		switch(kind) {
		
			//Logical
			case OP_AND:
				mv.visitInsn(IAND);
				break;
			case OP_OR:
				mv.visitInsn(IOR);
				break;
			
			//Arithmetic
			case OP_PLUS:
				mv.visitInsn(IADD);
				break;
			case OP_MINUS:
				mv.visitInsn(ISUB);
				break;
			case OP_TIMES:
				mv.visitInsn(IMUL);
				break;
			case OP_MOD:
				mv.visitInsn(IREM);
				break;
			case OP_DIV:
				mv.visitInsn(IDIV);
				break;
				
			//Relational Operators
			case OP_GT:
				mv.visitJumpInsn(IF_ICMPGT, startLabel);
				is_Relational = true;
				break;
			case OP_LT:
				mv.visitJumpInsn(IF_ICMPLT, startLabel);
				is_Relational = true;
				break;
			case OP_EQ:
				if(expression_Binary.e0.Type == Type.INTEGER || 
				   expression_Binary.e1.Type == Type.BOOLEAN) 
					mv.visitJumpInsn(IF_ICMPEQ, startLabel);
				else 
					mv.visitJumpInsn(IF_ACMPEQ, startLabel);
				is_Relational = true;
				break;
			case OP_NEQ:
				if(expression_Binary.e0.Type == Type.INTEGER || 
				   expression_Binary.e1.Type == Type.BOOLEAN) 
					mv.visitJumpInsn(IF_ICMPNE, startLabel);
				else
					mv.visitJumpInsn(IF_ACMPNE, startLabel);
				is_Relational = true;
				break;
			case OP_GE:
				mv.visitJumpInsn(IF_ICMPGE, startLabel);
				is_Relational = true;
				break;
			case OP_LE:
				mv.visitJumpInsn(IF_ICMPLE, startLabel);
				is_Relational = true;
				break;
			default:
				break;
		}
		
		if(is_Relational) {
			mv.visitLdcInsn(new Boolean(false));
			mv.visitJumpInsn(GOTO, endLabel);
			mv.visitLabel(startLabel);
			mv.visitLdcInsn(new Boolean(true));
			mv.visitLabel(endLabel);
		}
		//CodeGenUtils.genLogTOS(GRADE, mv, expression_Binary.Type);
		return null;
	}

	@Override
	public Object visitExpression_Unary(Expression_Unary expression_Unary, Object arg) throws Exception {
		expression_Unary.e.visit(this, arg);
		
		Kind kind = expression_Unary.op;
		
		switch(kind) {
			case OP_PLUS:
				break;
			case OP_MINUS:
				mv.visitInsn(INEG);
				break;
			case OP_EXCL:
				if(expression_Unary.Type == Type.BOOLEAN) 
					mv.visitInsn(ICONST_1);
				
				else if(expression_Unary.Type == Type.INTEGER) 
					mv.visitLdcInsn(new Integer(Integer.MAX_VALUE));
				
				mv.visitInsn(IXOR);
				break;
			default:
				break;
		}
				
		//CodeGenUtils.genLogTOS(GRADE, mv, expression_Unary.Type);
		return null;
	}

	// generate code to leave the two values on the stack
	@Override
	public Object visitIndex(Index index, Object arg) throws Exception {
		// TODO HW6
		//Lecture video 33
		//throw new UnsupportedOperationException();
		index.e0.visit(this, arg);
		index.e1.visit(this, arg);
		
		int opcode = INVOKESTATIC;
		String owner = RuntimeFunctions.className;
	
		if(!index.isCartesian()) {
			mv.visitInsn(DUP2);
			mv.visitMethodInsn(opcode, owner, "cart_x", RuntimeFunctions.cart_xSig, false);
			mv.visitInsn(DUP_X2);
			mv.visitInsn(POP);
			mv.visitMethodInsn(opcode, owner, "cart_y", RuntimeFunctions.cart_ySig, false);
		}
		return null;
		
	}

	@Override
	public Object visitExpression_PixelSelector(Expression_PixelSelector expression_PixelSelector, Object arg)
			throws Exception {
		mv.visitFieldInsn(GETSTATIC, className, expression_PixelSelector.name, ImageSupport.ImageDesc);
		expression_PixelSelector.index.visit(this, arg);
		mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getPixel", ImageSupport.getPixelSig, false);
		return null;
	}

	@Override
	public Object visitExpression_Conditional(Expression_Conditional expression_Conditional, Object arg)
			throws Exception {
		
		Label startLabel = new Label();
		Label endLabel = new Label();
		
		expression_Conditional.condition.visit(this, arg);
		
		mv.visitJumpInsn(IFNE, startLabel);
		expression_Conditional.falseExpression.visit(this, arg);
		mv.visitJumpInsn(GOTO, endLabel);
		mv.visitLabel(startLabel);
		expression_Conditional.trueExpression.visit(this, arg);
		mv.visitLabel(endLabel);
		
		//throw new UnsupportedOperationException();
		//CodeGenUtils.genLogTOS(GRADE, mv, expression_Conditional.trueExpression.Type);
		return null;
	}


	@Override
	public Object visitDeclaration_Image(Declaration_Image declaration_Image, Object arg) throws Exception {
		// TODO HW6
		fv = cw.visitField(ACC_STATIC, declaration_Image.name, ImageSupport.ImageDesc, null, null);
		fv.visitEnd();
		if(declaration_Image.source != null) {
			declaration_Image.source.visit(this, arg);
			if(declaration_Image.xSize == null && declaration_Image.ySize == null) {
				mv.visitInsn(ACONST_NULL);
				mv.visitInsn(ACONST_NULL);
			}
			else {
				declaration_Image.xSize.visit(this, arg);
				String owner = "java/lang/Integer";
				String name = "valueOf";
				String desc = "(I)Ljava/lang/Integer;";
				mv.visitMethodInsn(INVOKESTATIC, owner, name, desc, false);
				declaration_Image.ySize.visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, owner, name, desc, false);
			}
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "readImage", ImageSupport.readImageSig, false);
			mv.visitFieldInsn(PUTSTATIC, className, declaration_Image.name, ImageSupport.ImageDesc);
		}
		else {
			if(declaration_Image.xSize == null && declaration_Image.ySize == null) {
				mv.visitLdcInsn(256);
				mv.visitLdcInsn(256);
			}
			else {
				declaration_Image.xSize.visit(this, arg);
				declaration_Image.ySize.visit(this, arg);
				
			}
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "makeImage", ImageSupport.makeImageSig, false);
			mv.visitFieldInsn(PUTSTATIC, className, declaration_Image.name, ImageSupport.ImageDesc);
		}
		
		return null;
		
	}
	
  
	@Override
	public Object visitSource_StringLiteral(Source_StringLiteral source_StringLiteral, Object arg) throws Exception {
		// TODO HW6
		//throw new UnsupportedOperationException();
		mv.visitLdcInsn(source_StringLiteral.fileOrUrl);
		return null;
	}

	

	@Override
	public Object visitSource_CommandLineParam(Source_CommandLineParam source_CommandLineParam, Object arg)
			throws Exception {
		mv.visitVarInsn(ALOAD, 0);
		source_CommandLineParam.paramNum.visit(this, arg);
		mv.visitInsn(AALOAD);
		//throw new UnsupportedOperationException();
		return null;
		
	}

	@Override
	public Object visitSource_Ident(Source_Ident source_Ident, Object arg) throws Exception {
		// TODO HW6
		mv.visitFieldInsn(GETSTATIC, className, source_Ident.name, ImageSupport.StringDesc);
		return null;
		//throw new UnsupportedOperationException();
	}


	@Override
	public Object visitDeclaration_SourceSink(Declaration_SourceSink declaration_SourceSink, Object arg)
			throws Exception {
		// TODO HW6
		//throw new UnsupportedOperationException();
		fv = cw.visitField(ACC_STATIC, declaration_SourceSink.name, ImageSupport.StringDesc, null, null);
		fv.visitEnd();
		if(declaration_SourceSink.source != null) {
			declaration_SourceSink.source.visit(this,arg);
		}
		mv.visitFieldInsn(PUTSTATIC, className, declaration_SourceSink.name, ImageSupport.StringDesc);
		return null;
	}
	


	@Override
	public Object visitExpression_IntLit(Expression_IntLit expression_IntLit, Object arg) throws Exception {
		mv.visitLdcInsn(expression_IntLit.value);
		//CodeGenUtils.genLogTOS(GRADE, mv, Type.INTEGER);
		return null;
	}

	@Override
	public Object visitExpression_FunctionAppWithExprArg(
			Expression_FunctionAppWithExprArg expression_FunctionAppWithExprArg, Object arg) throws Exception {
		// TODO HW6
		//throw new UnsupportedOperationException();
		int opcode = INVOKESTATIC;
		String owner = RuntimeFunctions.className;
		
		expression_FunctionAppWithExprArg.arg.visit(this, arg);
		if(expression_FunctionAppWithExprArg.function == Kind.KW_abs)
			mv.visitMethodInsn(opcode, owner, "abs", RuntimeFunctions.absSig, false);
		else 
			throw new UnsupportedOperationException("No need to log");
		return null;
	}

	@Override
	public Object visitExpression_FunctionAppWithIndexArg(
			Expression_FunctionAppWithIndexArg expression_FunctionAppWithIndexArg, Object arg) throws Exception {
		// TODO HW6
		//throw new UnsupportedOperationException();
		expression_FunctionAppWithIndexArg.arg.e0.visit(this, arg);
		expression_FunctionAppWithIndexArg.arg.e1.visit(this, arg);
		
		Kind kind = expression_FunctionAppWithIndexArg.function;
		int opcode = INVOKESTATIC;
		String owner = RuntimeFunctions.className;
		
		switch(kind) {
			case KW_cart_x:
				mv.visitMethodInsn(opcode, owner, "cart_x", RuntimeFunctions.cart_xSig, false);
				break;
				
			case KW_cart_y:
				mv.visitMethodInsn(opcode, owner, "cart_y", RuntimeFunctions.cart_ySig, false);
				break;
					
			case KW_polar_r:
				mv.visitMethodInsn(opcode, owner, "polar_r", RuntimeFunctions.polar_rSig, false);
				break;
				
			default:
				mv.visitMethodInsn(opcode, owner, "polar_a", RuntimeFunctions.polar_aSig, false);
		}
		return null;
		
	}

	@Override
	public Object visitExpression_PredefinedName(Expression_PredefinedName expression_PredefinedName, Object arg)
			throws Exception {
		// TODO HW6
		//throw new UnsupportedOperationException();
		
	
		Kind kind = expression_PredefinedName.kind;
		int opcode = GETSTATIC;
		String owner = className;
		String desc = "I";
		
		switch(kind) {
			case KW_r:
				mv.visitFieldInsn(opcode, owner, "x", desc);
				mv.visitFieldInsn(opcode, owner, "y", desc);
				mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_r", RuntimeFunctions.polar_rSig, false);
				break;
		
			case KW_a:
				mv.visitFieldInsn(opcode, owner, "x", desc);
				mv.visitFieldInsn(opcode, owner, "y", desc);
				mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_a", RuntimeFunctions.polar_aSig, false);	
			break;
			
			case KW_R:
				mv.visitFieldInsn(opcode, owner, "X", desc);
				mv.visitFieldInsn(opcode, owner, "Y", desc);
				mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_r", RuntimeFunctions.polar_rSig, false);
			break;
			
			case KW_A:
				mv.visitFieldInsn(opcode, owner, "X", desc);	
				mv.visitFieldInsn(opcode, owner, "Y", desc);	
				mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_a", RuntimeFunctions.polar_aSig, false);
			break;
				
			case KW_Z:
				mv.visitFieldInsn(opcode, owner, "Z", desc);
				break;
				
			case KW_x:
				mv.visitFieldInsn(opcode, owner, "x", desc);
				break;
				
			case KW_y:
				mv.visitFieldInsn(opcode, owner, "y", desc);
				break;
				
			case KW_X:
				mv.visitFieldInsn(opcode, owner, "X", desc);
				break;
		
			case KW_Y:
				mv.visitFieldInsn(opcode, owner, "Y", desc);
				break;
			
			case KW_DEF_X:
				mv.visitFieldInsn(opcode, owner, "DEF_X", desc);
				break;
			
			case KW_DEF_Y:
				mv.visitFieldInsn(opcode, owner, "DEF_Y", desc);
				break;	
			
		}
		return null;
		
			
					
	}

	/** For Integers and booleans, the only "sink"is the screen, so generate code to print to console.
	 * For Images, load the Image onto the stack and visit the Sink which will generate the code to handle the image.
	 */
	@Override
	public Object visitStatement_Out(Statement_Out statement_Out, Object arg) throws Exception {
		mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		
		Type type = statement_Out.getDec().Type;
		String desc_Field = "";
		String desc_Method = "";
		int opcode = GETSTATIC;
		String owner = className;
		
		switch(type) {
			case BOOLEAN:
				desc_Field = "Z";
				desc_Method = "(Z)V";
				mv.visitFieldInsn(opcode, owner, statement_Out.name, desc_Field);
				CodeGenUtils.genLogTOS(GRADE, mv, statement_Out.getDec().Type); //from discussion
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",desc_Method, false);
				break;
			
			case INTEGER:
				desc_Field = "I";
				desc_Method = "(I)V";
				mv.visitFieldInsn(opcode, owner, statement_Out.name, desc_Field);
				CodeGenUtils.genLogTOS(GRADE, mv, statement_Out.getDec().Type); //from discussion
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", desc_Method, false);
				break;
			
			case IMAGE:
				mv.visitFieldInsn(opcode, owner, statement_Out.name, ImageSupport.ImageDesc);
				CodeGenUtils.genLogTOS(GRADE, mv, statement_Out.getDec().Type); //from discussion
				statement_Out.sink.visit(this,arg);
				break;
			}
		return null;
					
	}

	/**
	 * Visit source to load rhs, which will be a String, onto the stack
	 * 
	 *  In HW5, you only need to handle INTEGER and BOOLEAN
	 *  Use java.lang.Integer.parseInt or java.lang.Boolean.parseBoolean 
	 *  to convert String to actual type. 
	 *  
	 *  TODO HW6 remaining types
	 */
	
	//<-
	@Override
	public Object visitStatement_In(Statement_In statement_In, Object arg) throws Exception {
		if(statement_In.source != null)
			statement_In.source.visit(this, arg);
		
		String owner = "";
		String name = "";
		String desc_method = "";
		String desc_field = "";
		Type type = statement_In.getDec().Type;
		
		switch(type) {
		
			case BOOLEAN:
				owner = "java/lang/Boolean";
				name = "parseBoolean";
				desc_method = "(Ljava/lang/String;)Z";
				desc_field = "Z";
				mv.visitMethodInsn(INVOKESTATIC, owner, name, desc_method, false);
				mv.visitFieldInsn(PUTSTATIC, className, statement_In.name, desc_field);
				break;
			
			case INTEGER:
				owner = "java/lang/Integer";
				name = "parseInt";
				desc_method = "(Ljava/lang/String;)I";
				desc_field = "I";
				mv.visitMethodInsn(INVOKESTATIC, owner, name, desc_method, false);
				mv.visitFieldInsn(PUTSTATIC, className, statement_In.name, desc_field);
				break;
			
			case IMAGE:
				Declaration_Image declaration_image = (Declaration_Image) statement_In.getDec();
				
				if(declaration_image.xSize == null && declaration_image.ySize == null) {
					mv.visitInsn(ACONST_NULL);
					mv.visitInsn(ACONST_NULL);
				}
				else {	
					declaration_image.xSize.visit(this, arg);
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
					declaration_image.ySize.visit(this, arg);
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
				}
				mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "readImage", ImageSupport.readImageSig, false);
				mv.visitFieldInsn(PUTSTATIC, className, declaration_image.name, ImageSupport.ImageDesc);
				break;
		}

		return null;
		//TODO for other types
	}

	
	/**
	 * In HW5, only handle INTEGER and BOOLEAN types.
	 */
	@Override
	public Object visitStatement_Assign(Statement_Assign statement_Assign, Object arg) throws Exception {
		
		if(statement_Assign.lhs.Type == Type.INTEGER || statement_Assign.lhs.Type == Type.BOOLEAN) {
			statement_Assign.e.visit(this, arg);
			statement_Assign.lhs.visit(this, arg);
		}
		else if(statement_Assign.lhs.Type == Type.IMAGE) {
			Label label1 = new Label();
			Label label2 = new Label();
			Label label3 = new Label();
			Label label4 = new Label();
			
			mv.visitFieldInsn(GETSTATIC, className, statement_Assign.lhs.name, ImageSupport.ImageDesc);
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getY", ImageSupport.getYSig, false);
			mv.visitFieldInsn(PUTSTATIC, className, "Y", "I");
			
			mv.visitFieldInsn(GETSTATIC, className, statement_Assign.lhs.name, ImageSupport.ImageDesc);
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getX", ImageSupport.getXSig, false);
			mv.visitFieldInsn(PUTSTATIC, className, "X", "I");
			
			mv.visitLdcInsn(ICONST_0);
			mv.visitFieldInsn(GETSTATIC, className, "Y", "I");
			mv.visitFieldInsn(GETSTATIC, className, "X", "I");
			mv.visitFieldInsn(GETSTATIC, className, "Y", "I");
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_r", RuntimeFunctions.polar_rSig, false);
			mv.visitFieldInsn(PUTSTATIC, className, "R", "I");
			
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_a", RuntimeFunctions.polar_aSig, false);
			mv.visitFieldInsn(PUTSTATIC, className, "A", "I");
			
			mv.visitInsn(ICONST_0);
			mv.visitInsn(DUP);
			mv.visitLabel(label1);
			
			mv.visitFieldInsn(PUTSTATIC, className, "y", "I");
			mv.visitFieldInsn(GETSTATIC, className, "Y", "I");
			mv.visitJumpInsn(IF_ICMPGE, label4);
			
			mv.visitInsn(ICONST_0);
			mv.visitInsn(DUP);
			mv.visitLabel(label2);
			
			mv.visitFieldInsn(PUTSTATIC, className, "x", "I");
			mv.visitFieldInsn(GETSTATIC, className, "X", "I");
			mv.visitJumpInsn(IF_ICMPGE, label3);
			
			statement_Assign.e.visit(this, arg);
			statement_Assign.lhs.visit(this, arg);
			
			mv.visitFieldInsn(GETSTATIC, className, "x", "I");
			
			mv.visitInsn(ICONST_1);
			mv.visitInsn(IADD);
			mv.visitInsn(DUP);
			
			mv.visitJumpInsn(GOTO, label2);
			mv.visitLabel(label3);
			
			mv.visitFieldInsn(GETSTATIC, className, "y", "I");
			
			mv.visitInsn(ICONST_1);
			mv.visitInsn(IADD);
			mv.visitInsn(DUP);
			
			mv.visitJumpInsn(GOTO, label1);
			mv.visitLabel(label4);
			
		}
		
		
		return null;
		
	}

	/**
	 * In HW5, only handle INTEGER and BOOLEAN types.
	 */
	@Override
	public Object visitLHS(LHS lhs, Object arg) throws Exception {
		
		Type type = lhs.Type;
		String owner = className;
		
		switch(type) {
	
			case BOOLEAN:
				mv.visitFieldInsn(PUTSTATIC, owner, lhs.name, "Z");
				break;
				
			case INTEGER:
				mv.visitFieldInsn(PUTSTATIC, owner, lhs.name, "I");
				break;
				
			case IMAGE:
				mv.visitFieldInsn(GETSTATIC, owner, lhs.name, ImageSupport.ImageDesc);
				mv.visitFieldInsn(GETSTATIC, owner, "x", "I");
				mv.visitFieldInsn(GETSTATIC, owner, "y", "I");
				mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "setPixel", ImageSupport.setPixelSig, false);
				break;

			default:
				throw new UnsupportedOperationException();
		}
		
		return null;
		//TODO: for other types
	}
	

	@Override
	public Object visitSink_SCREEN(Sink_SCREEN sink_SCREEN, Object arg) throws Exception {
		//TODO HW6
		//throw new UnsupportedOperationException();
		mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "makeFrame", ImageSupport.makeFrameSig, false);
		mv.visitInsn(POP);
		return null;
	}

	@Override
	public Object visitSink_Ident(Sink_Ident sink_Ident, Object arg) throws Exception {
		//TODO HW6
		//throw new UnsupportedOperationException();
		mv.visitFieldInsn(GETSTATIC, className, sink_Ident.name, "Ljava/lang/String;");
		mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "write", ImageSupport.writeSig, false);
		return null;
	}

	@Override
	public Object visitExpression_BooleanLit(Expression_BooleanLit expression_BooleanLit, Object arg) throws Exception {
		mv.visitLdcInsn(expression_BooleanLit.value);
		//CodeGenUtils.genLogTOS(GRADE, mv, Type.BOOLEAN);
		return null;
	}

	@Override
	public Object visitExpression_Ident(Expression_Ident expression_Ident,
			Object arg) throws Exception {
		
		Type type = expression_Ident.Type;
		int opcode = GETSTATIC;
		String owner = className;
		
		switch(type) {
			case BOOLEAN:
				mv.visitFieldInsn(opcode, owner, expression_Ident.name, "Z");
				break;
				
			case INTEGER:
				mv.visitFieldInsn(opcode, owner, expression_Ident.name, "I");
				break;
				
			default:
				throw new UnsupportedOperationException(); //TODO; Implement other types
		}
		//CodeGenUtils.genLogTOS(GRADE, mv, expression_Ident.Type);
		return null;
	}
}
