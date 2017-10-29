package com.myopicmobile.textwarrior.common;

import java.util.List;
import java.io.IOException;

public class JavaLexerThread extends LexerThread {
	public JavaLexerThread (DocumentProvider doc) {
		super(doc);
	}
	public void tokenize () {
		CharSeqReader reader = new CharSeqReader(doc);
		JavaLexer lexer = new JavaLexer(reader);
		while (true) {
			JavaToken token;
			try {
				token = lexer.yylex();
			} catch (Throwable e) {
				break;
			}
			int type = Lexer.NORMAL;
			switch (token) {
				case ABSTRACT:
					case INTERFACE:case PUBLIC:
				case PRIVATE:case PROTECTED:case PACKAGE:
				case RETURN:case STATIC:case FINAL:case IF:
				case FOR:case WHILE:case ELSE:
				case SUPER:case IMPORT:case CLASS:
				case DO:case DEFAULT:case CONTINUE:
				case BREAK:case CASE:case CATCH:
				case TRY:
				case FINALLY:
				case CONST:
				case GOTO:
				case EXTENDS:
				case IMPLEMENTS:
				case INSTANCEOF:
				case NATIVE:
				case NEW:
				case STRICTFP:
				case SWITCH:
				case SYNCHRONIZED:
				case THIS:
				case THROW:
				case THROWS:
				case TRANSIENT:
				case VOLATILE:
					type = Lexer.KEYWORD;
					break;
				case BOOLEAN:
				case VOID:
				case CHAR:
				case ASSERT:
				case ENUM:
				case FLOAT:
				case INT:
				case LONG:
				case DOUBLE:
				case SHORT:
				case BYTE:
					type = Lexer.NAME;
					break;
				case TRUE:
				case FALSE:
				case INTLITERAL:
				case LONGLITERAL:
				case FLOATLITERAL:
				case DOUBLELITERAL:
				case CHARLITERAL:
				case STRINGLITERAL:
				case NULL:
					type = Lexer.LITERAL;
					break;
				case UNDERSCORE://("_", Tag.NAMED),
				case ARROW://("->"),
				case COLCOL://("::"),
				case LPAREN://("("),
				case RPAREN://(")"),
				case LBRACE://("{"),
				case RBRACE://("}"),
				case LBRACKET://("["),
				case RBRACKET://("]"),
				case SEMI://(";"),
				case COMMA://(","),
				case DOT://("."),
				case ELLIPSIS://("...")
				case EQ://("="),
				case GT://(">"),
				case LT://("<"),
				case BANG://("!"),
				case TILDE://("~"),
				case QUES://("?"),
				case COLON://(":"),
				case EQEQ://("=="),
				case LTEQ://("<="),
				case GTEQ://(">="),
				case BANGEQ://("!="),
				case AMPAMP://("&&"),
				case BARBAR://("||"),
				case PLUSPLUS://("++"),
				case SUBSUB://("--"),
				case PLUS://("+"),
				case SUB://("-"),
				case STAR://("*"),
				case SLASH://("/"),
				case AMP://("&"),
				case BAR://("|"),
				case CARET://("^"),
				case PERCENT://("%"),
				case LTLT://("<<"),
				case GTGT://(">>"),
				case GTGTGT://(">>>"),
				case PLUSEQ://("+="),
				case SUBEQ://("-="),
				case STAREQ://("*="),
				case SLASHEQ://("/="),
				case AMPEQ://("&="),
				case BAREQ://("|="),
				case CARETEQ://("^="),
				case PERCENTEQ://("%="),
				case LTLTEQ://("<<="),
				case GTGTEQ://(">>="),
				case GTGTGTEQ://(">>>="),
				case MONKEYS_AT://("@"),
					type = Lexer.OPERATOR;
					break;
				case IDENTIFIER:
					type = parseIdentfiter(token.
										   name().toString());
					break;
				case COMMENT:
					type = Lexer.DOUBLE_SYMBOL_LINE;
					break;

			}
			int len = lexer.yylength();
			if(token==JavaToken.STRINGLITERAL)
				len++;
			_tokens.add(new Pair(len, type));
		}
	}
	private static int parseIdentfiter (String id) {
		/*if (id.matches("[A-Z].*"))
			return Lexer.NAME;*/
		return Lexer.NORMAL;
	}

}
