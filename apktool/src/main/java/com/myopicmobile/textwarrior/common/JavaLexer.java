package com.myopicmobile.textwarrior.common;

import com.sun.tools.javac.parser.Scanner;
import com.sun.tools.javac.parser.ScannerFactory;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.parser.Tokens;

public class JavaLexer
{
	private static final Context ctx;
	DocumentProvider doc;

	public JavaLexer (DocumentProvider doc) {
		this.doc = doc;
	}
	public void run(){
		ScannerFactory fact = ScannerFactory.instance(ctx);
		Scanner s=fact.newScanner(doc,false);
		while(true){
			s.nextToken();
			Tokens.Token token = s.token();
			Tokens.TokenKind kind = token.kind;
			if(kind==Tokens.TokenKind.EOF)
				break;
			switch(kind){
				
			}
		}
	}
	static{
		ctx = new Context();
	}
}
