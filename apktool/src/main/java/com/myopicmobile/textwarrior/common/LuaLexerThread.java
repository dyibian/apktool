package com.myopicmobile.textwarrior.common;

import java.io.IOException;
import java.util.ArrayList;
import static com.myopicmobile.textwarrior.common.Lexer.*;

public class LuaLexerThread extends LexerThread {
	public LuaLexerThread(CharSequence ch){
		super(ch);
	}
	protected void tokenize () { 
		ArrayList<Pair> tokens = new ArrayList<Pair>(8196);
		LuaLexer lexer = new LuaLexer(doc);
		lan.clearUserWord();
		try {
			int idx = 0;

			LuaTokenTypes lastType = null;
			LuaTokenTypes lastType2 = null;

			String lastName="";
			Pair lastPair = null;
			StringBuilder bul=new StringBuilder();			
			boolean isModule=false;
			while (!_abort.isSet()) {
				Pair pair = null;
				LuaTokenTypes type = lexer.advance();
				if (type == null)
					break;
				int len = lexer.yylength();
				if (isModule && lastType == LuaTokenTypes.STRING && type != LuaTokenTypes.STRING) {
					String mod=bul.toString();
					if (bul.length() > 2)
						lan.addUserWord(mod.substring(1, mod.length() - 1));
					bul = new StringBuilder();
					isModule = false;
				}

				if (lastType2 == type && lastPair != null) {
					lastPair.setFirst(lastPair.getFirst() + len);
				} else if (isKeyword(type)) {
					//关键字
					tokens.add(new Pair(len, Lexer. KEYWORD));
				} else if (type == LuaTokenTypes.LPAREN || type == LuaTokenTypes.RPAREN
						   || type == LuaTokenTypes.LBRACK || type == LuaTokenTypes.RBRACK
						   || type == LuaTokenTypes.LCURLY || type == LuaTokenTypes.RCURLY
						   || type == LuaTokenTypes.COMMA || type == LuaTokenTypes.DOT) {
					//括号
					tokens.add(pair = new Pair(len, OPERATOR));
				} else if (type == LuaTokenTypes.STRING || type == LuaTokenTypes.LONGSTRING) {
					//字符串
					if (lastType != type) {
						tokens.add(pair = new Pair(len, SINGLE_SYMBOL_DELIMITED_A));
						if (lastName.equals("require"))
							isModule = true;
					} else {
						lastPair.setFirst(lastPair.getFirst() + len);
					}
					if (isModule)
						bul.append(lexer.yytext());
				} else if (type == LuaTokenTypes.NAME) {
					String name=lexer.yytext();
					if (lastType == LuaTokenTypes.FUNCTION) {
						//函数名
						tokens.add(new Pair(len, LITERAL));
						lan.addUserWord(name);
					} else if (lan.isUserWord(name)) {
						tokens.add(new Pair(len, LITERAL));
					} else if (lan.isBasePackage(name)) {
						tokens.add(new Pair(len, NAME));
					} else if (lastType == LuaTokenTypes.DOT && lan.isBasePackage(lastName) && lan.isBaseWord(lastName, name)) {
						//标准库函数
						tokens.add(new Pair(len, NAME));
					} else if (lan.isName(name)) {
						tokens.add(new Pair(len, NAME));
					} else {
						tokens.add(new Pair(len, NORMAL));
					}
					if (lastType == LuaTokenTypes.ASSIGN && name.equals("require")) {
						lan.addUserWord(lastName);
						if (tokens.size() >= 3) {
							Pair p=tokens.get(tokens.size() - 3);
							p.setSecond(NAME);
						}
					}

					lastName = name;
				} else if (type == LuaTokenTypes.SHORTCOMMENT || type == LuaTokenTypes.LONGCOMMENT) {
					//注释					
					if (lastType != type)
						tokens.add(pair = new Pair(len, DOUBLE_SYMBOL_LINE));
					else
						lastPair.setFirst(lastPair.getFirst() + len);
				} else if (type == LuaTokenTypes.NUMBER) {
					tokens.add(new Pair(len, LITERAL));
				} else {
					tokens.add(pair = new Pair(len, NORMAL));
				}

				if (type != LuaTokenTypes.WS
				//&& type != LuaTokenTypes.NEWLINE
					&& type != LuaTokenTypes.NL_BEFORE_LONGSTRING) {
					lastType = type;
				}
				lastType2 = type;
				if (pair != null)
					lastPair = pair;
				idx += len;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (tokens.isEmpty()) {
			// return value cannot be empty
			tokens.add(new Pair(0, NORMAL));
		}
		lan.updateUserWord();
		_tokens = tokens;
	}

	private static boolean isKeyword (LuaTokenTypes t) {
        switch (t) {
			case TRUE:
			case FALSE:
            case DO:
            case FUNCTION:
            case NOT:
            case AND:
            case OR:
            case WITH:
            case IF:
            case THEN:
            case ELSEIF:
            case ELSE:
            case WHILE:
            case FOR:
            case IN:
            case RETURN:
            case BREAK:
            case CONTINUE:
            case LOCAL:
            case REPEAT:
            case UNTIL:
            case END:
            case NIL:
				//   return true;
            default:
                return false;
        }
    }
}

