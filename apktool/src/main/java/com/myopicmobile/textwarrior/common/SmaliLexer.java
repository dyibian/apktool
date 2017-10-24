package com.myopicmobile.textwarrior.common;

import android.util.Log;
import java.util.ArrayList;
import org.antlr.runtime.Token;
import org.jf.smali.smaliFlexLexer;
import org.jf.smali.smaliParser;

public class SmaliLexer extends LexerThread {
	private static final String cond="(goto|cond|sswitch)(_.*)?";
	public SmaliLexer(CharSequence ch){
		super(ch);
	}
	protected void tokenize() {
		_tokens = new ArrayList<Pair>();
		CharSeqReader reader = new CharSeqReader(doc);
		smaliFlexLexer lexer = new smaliFlexLexer(reader);
		int lastType = 0;
		lan.clearUserWord();
		int lastIndex = 0;
		while (true) {
			Token token = lexer.nextToken();
			int type=token.getType();
			if (type == Token.EOF)
				break;
			if (lastType == smaliParser.COLON
				&& token.getText().matches(cond))
				type = smaliParser.COLON;
			lastType = type;
			int len = token.getText().length();
			if (type == smaliParser.STRING_LITERAL ||
				type == smaliParser.INVALID_TOKEN)
				len = getRealLen(len, lastIndex);
			lastIndex += len;
			type = getType(token, type);
			lan.updateUserWord();
			Pair pair = new Pair(len, type);
			_tokens.add(pair);
		}
	}
	private int getType(Token token, int type) {
		switch (type) {
			case smaliParser.ANNOTATION_DIRECTIVE:
			case smaliParser.END_ANNOTATION_DIRECTIVE:
			case smaliParser.ARRAY_DATA_DIRECTIVE:
			case smaliParser.END_ARRAY_DATA_DIRECTIVE:
			case smaliParser.CLASS_DIRECTIVE:
			case smaliParser.FIELD_DIRECTIVE:
			case smaliParser.END_FIELD_DIRECTIVE:
			case smaliParser.SOURCE_DIRECTIVE:
			case smaliParser.SUPER_DIRECTIVE:
			case smaliParser.IMPLEMENTS_DIRECTIVE:
			case smaliParser.METHOD_DIRECTIVE:
			case smaliParser.LINE_DIRECTIVE:
			case smaliParser.LOCAL_DIRECTIVE:
			case smaliParser.END_LOCAL_DIRECTIVE:
			case smaliParser.LOCALS_DIRECTIVE:
			case smaliParser.REGISTERS_DIRECTIVE:
			case smaliParser.END_METHOD_DIRECTIVE:
			case smaliParser.END_PACKED_SWITCH_DIRECTIVE:
			case smaliParser.CATCHALL_DIRECTIVE:
			case smaliParser.CATCH_DIRECTIVE:
			case smaliParser.END_PARAMETER_DIRECTIVE:
			case smaliParser.END_SPARSE_SWITCH_DIRECTIVE:
			case smaliParser.END_SUBANNOTATION_DIRECTIVE:
			case smaliParser.ENUM_DIRECTIVE:
			case smaliParser.EPILOGUE_DIRECTIVE:
			case smaliParser.PACKED_SWITCH_DIRECTIVE:
			case smaliParser.PARAMETER_DIRECTIVE:
			case smaliParser.SPARSE_SWITCH_DIRECTIVE:
			case smaliParser.SUBANNOTATION_DIRECTIVE:
			case smaliParser.PROLOGUE_DIRECTIVE:
			case smaliParser.RESTART_LOCAL_DIRECTIVE:
				return Lexer.NAME;
			case smaliParser.ANNOTATION_VISIBILITY:
			case smaliParser.ACCESS_SPEC:
				return  Lexer.KEYWORD;
			case smaliParser.LINE_COMMENT:
				return Lexer.DOUBLE_SYMBOL_LINE;
			case smaliParser.CLASS_DESCRIPTOR:
			case smaliParser.PRIMITIVE_TYPE:
			case smaliParser.ARRAY_TYPE_PREFIX:
			case smaliParser.VOID_TYPE:
				lan.addUserWord(token.getText().trim());
				return Lexer.SINGLE_SYMBOL_DELIMITED_B;
			case smaliParser.INTEGER_LITERAL:
			case smaliParser.CHAR_LITERAL:
			case smaliParser.BOOL_LITERAL:
			case smaliParser.BYTE_LITERAL:
			case smaliParser.DOUBLE_LITERAL:
			case smaliParser.DOUBLE_LITERAL_OR_ID:
			case smaliParser.FLOAT_LITERAL:
			case smaliParser.FLOAT_LITERAL_OR_ID:
			case smaliParser.LONG_LITERAL:
			case smaliParser.NULL_LITERAL:
			case smaliParser.SHORT_LITERAL:
			case smaliParser.STRING_LITERAL:
			case smaliParser.DOTDOT:
			case smaliParser.ARROW:
			case smaliParser.EQUAL:
			case smaliParser.COLON:
			case smaliParser.COMMA:
			case smaliParser.OPEN_BRACE:
			case smaliParser.OPEN_PAREN:
			case smaliParser.CLOSE_BRACE:
			case smaliParser.CLOSE_PAREN:
			case smaliParser.POSITIVE_INTEGER_LITERAL:
			case smaliParser.NEGATIVE_INTEGER_LITERAL:
				return Lexer.LITERAL;
			case smaliParser.REGISTER:
				return Lexer.OPERATOR;
			case smaliParser.INVALID_TOKEN:
				return Lexer.UNKNOWN;
			default:
				if (type >= 42 && type <= 87)
					return Lexer.OPERATOR;
				return Lexer.NORMAL;
		}
	}
	private int getRealLen(int len, int lastIndex) {
		for (int i=0;i < len;i++) {
			char c = doc.charAt(lastIndex + i);
			if (c == '\\') {
				char c1=doc.charAt(i + 1 + lastIndex);
				if (c1 == 'u') {
					len += 5;
					i += 5;
				} else if (c == 'b' ||
						   c == 'f' ||
						   c == 'r' ||
						   c == 't' ||
						   c == 'n' ||
						   c == '"' ||
						   c == '\'' ||
						   c == '\\') {
					len++;
					i++;
				}
			}
		}
		return len;
	}
}
