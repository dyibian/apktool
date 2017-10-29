package com.myopicmobile.textwarrior.common;

public enum JavaToken {
	EOF(),
	ERROR,//(),
	IDENTIFIER,//(Tag.NAMED),
	ABSTRACT,//("abstract"),
	ASSERT,//("assert", Tag.NAMED),
	BOOLEAN,//("boolean", Tag.NAMED),
	BREAK,//("break"),
	BYTE,//("byte", Tag.NAMED),
	CASE,//("case"),
	CATCH,//("catch"),
	CHAR,//("char", Tag.NAMED),
	CLASS,//("class"),
	CONST,//("const"),
	CONTINUE,//("continue"),
	DEFAULT,//("default"),
	DO,//("do"),
	DOUBLE,//("double", Tag.NAMED),
	ELSE,//("else"),
	ENUM,//("enum", Tag.NAMED),
	EXTENDS,//("extends"),
	FINAL,//("final"),
	FINALLY,//("finally"),
	FLOAT,//("float", Tag.NAMED),
	FOR,//("for"),
	GOTO,//("goto"),
	IF,//("if"),
	IMPLEMENTS,//("implements"),
	IMPORT,//("import"),
	INSTANCEOF,//("instanceof"),
	INT,//("int", Tag.NAMED),
	INTERFACE,//("interface"),
	LONG,//("long", Tag.NAMED),
	NATIVE,//("native"),
	NEW,//("new"),
	PACKAGE,//("package"),
	PRIVATE,//("private"),
	PROTECTED,//("protected"),
	PUBLIC,//("public"),
	RETURN,//("return"),
	SHORT,//("short", Tag.NAMED),
	STATIC,//("static"),
	STRICTFP,//("strictfp"),
	SUPER,//("super", Tag.NAMED),
	SWITCH,//("switch"),
	SYNCHRONIZED,//("synchronized"),
	THIS,//("this", Tag.NAMED),
	THROW,//("throw"),
	THROWS,//("throws"),
	TRANSIENT,//("transient"),
	TRY,//("try"),
	VOID,//("void", Tag.NAMED),
	VOLATILE,//("volatile"),
	WHILE,//("while"),
	INTLITERAL,//(Tag.NUMERIC),
	LONGLITERAL,//(Tag.NUMERIC),
	FLOATLITERAL,//(Tag.NUMERIC),
	DOUBLELITERAL,//(Tag.NUMERIC),
	CHARLITERAL,//(Tag.NUMERIC),
	STRINGLITERAL,//(Tag.STRING),
	TRUE,//("true", Tag.NAMED),
	FALSE,//("false", Tag.NAMED),
	NULL,//("null", Tag.NAMED),
	UNDERSCORE,//("_", Tag.NAMED),
	ARROW,//("->"),
	COLCOL,//("::"),
	LPAREN,//(",//("),
	RPAREN,//(")"),
	LBRACE,//("{"),
	RBRACE,//("}"),
	LBRACKET,//("["),
	RBRACKET,//("]"),
	SEMI,//(";"),
	COMMA,//(","),
	DOT,//("."),
	ELLIPSIS,//("..."),
	EQ,//("="),
	GT,//(">"),
	LT,//("<"),
	BANG,//("!"),
	TILDE,//("~"),
	QUES,//("?"),
	COLON,//(":"),
	EQEQ,//("=="),
	LTEQ,//("<="),
	GTEQ,//(">="),
	BANGEQ,//("!="),
	AMPAMP,//("&&"),
	BARBAR,//("||"),
	PLUSPLUS,//("++"),
	SUBSUB,//("--"),
	PLUS,//("+"),
	SUB,//("-"),
	STAR,//("*"),
	SLASH,//("/"),
	AMP,//("&"),
	BAR,//("|"),
	CARET,//("^"),
	PERCENT,//("%"),
	LTLT,//("<<"),
	GTGT,//(">>"),
	GTGTGT,//(">>>"),
	PLUSEQ,//("+="),
	SUBEQ,//("-="),
	STAREQ,//("*="),
	SLASHEQ,//("/="),
	AMPEQ,//("&="),
	BAREQ,//("|="),
	CARETEQ,//("^="),
	PERCENTEQ,//("%="),
	LTLTEQ,//("<<="),
	GTGTEQ,//(">>="),
	GTGTGTEQ,//(">>>="),
	MONKEYS_AT,//("@"),
	COMMENT,
	SPACE;
}
