package com.myopicmobile.textwarrior.common;
import java.io.IOException;
import android.util.Log;

public class XMLFlexerThread extends LexerThread {
	public XMLFlexerThread (CharSequence doc) {
		super(doc);
	}

	@Override
	protected void tokenize () {
		CharSeqReader r=new CharSeqReader(doc);
		XMLFlexer lexer = new XMLFlexer(r);
		while (true) {
			XMLToken token;
			try {
				token = lexer.yylex();
			} catch (Throwable e) {
				Log.i("XML_LEXER", "Error" + e);
				break;
			}
			if (token == XMLToken.END)
				break;
			int type=Lexer.NORMAL;
			sa:switch (token) {
				case HEAD:
				case TAG_NAME:
					type = Lexer.KEYWORD;
					break sa;
				case EQ:
				case LPAREN:
				case RPAREN:
				case SLASH:
					type = Lexer.OPERATOR;
					break sa;
				case ATTR_NAME:
					type = Lexer.NAME;
					break;
				case ATTR_VALUE:
					type = Lexer.LITERAL;
					break;
				case COMMENT:
					type = Lexer.DOUBLE_SYMBOL_LINE;
					break;
			}
			int len = lexer.yylength();
			_tokens.add(new Pair(len, type));
			if(lexer.eof)
				break;
		}
	}

}
