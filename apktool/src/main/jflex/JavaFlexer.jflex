package com.myopicmobile.textwarrior.common;

%%

%public
%class JavaLexer
%type JavaToken
%line
%column
%char

%{
	
	private static final String chString = "'([^\\\\]|\\\\[bfrtn(\\\\)]|\\\\u[0-9a-fA-F]{4})'";
	private JavaToken endChar(){
		String ch = yytext();
		if(ch.matches(chString))
			return JavaToken.CHARLITERAL;
		else
			return JavaToken.ERROR;
	}
%}
HexPrefix = 0 [xX]

HexDigit = [0-9a-fA-F]
HexDigits = [0-9a-fA-F]{4}
FewerHexDigits = [0-9a-fA-F]{0,3}
/* main character classes */
LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
Integer1 = 0
Integer2 = [1-9] [0-9]*
Integer3 = 0 [0-7]+
Integer4 = {HexPrefix} {HexDigit}+
Integer5 = 0[bB] [01]+
Integer = {Integer1} | {Integer2} | {Integer3} | {Integer4} | {Integer5}

DecimalExponent = [eE] -? [0-9]+

BinaryExponent = [pP] -? [0-9]+

/*This can only be a float and not an identifier, due to the decimal point*/
Float1 = -? [0-9]+ "." [0-9]* {DecimalExponent}?
Float2 = -? "." [0-9]+ {DecimalExponent}?
Float3 = -? {HexPrefix} {HexDigit}+ "." {HexDigit}* {BinaryExponent}
Float4 = -? {HexPrefix} "." {HexDigit}+ {BinaryExponent}
Float =  {Float1} | {Float2} | {Float3} | {Float4}
/* comments */
Comment = {TraditionalComment} | {EndOfLineComment} | 
          {DocumentationComment}

TraditionalComment = "/*" [^*] ~"*/" | "/*" "*"+ "/"
EndOfLineComment = "//" {InputCharacter}* {LineTerminator}?
DocumentationComment = "/*" "*"+ [^/*] ~"*/"
%state STRING
%state CHAR
%%

<YYINITIAL>{
	"abstract"		{return JavaToken.ABSTRACT; }
	"abstract"		{return JavaToken.ABSTRACT;}
	"assert"		{return JavaToken.ASSERT;}
	"boolean"		{return JavaToken.BOOLEAN;}
	"break"			{return JavaToken.BREAK;}
	"byte"			{return JavaToken.BYTE;}
	"case"			{return JavaToken.CASE;}
	"catch"			{return JavaToken.CATCH;}
	"char"			{return JavaToken.CHAR;}
	"class"			{return JavaToken.CLASS;}
	"const"			{return JavaToken.CONST;}
	"continue"		{return JavaToken.CONTINUE;}
	"default"		{return JavaToken.DEFAULT;}
	"do"			{return JavaToken.DO;}
	"double"		{return JavaToken.DOUBLE;}
	"else"			{return JavaToken.ELSE;}
	"enum"			{return JavaToken.ENUM;}
	"extends"		{return JavaToken.EXTENDS;}
	"final"			{return JavaToken.FINAL;}
	"finally"		{return JavaToken.FINALLY;}
	"float"			{return JavaToken.FLOAT;}
	"for"			{return JavaToken.FOR;}
	"goto"			{return JavaToken.GOTO;}
 	"if"			{return JavaToken.IF;}
	"implements"	{return JavaToken.IMPLEMENTS;}
	"import"		{return JavaToken.IMPORT;}
	"instanceof"	{return JavaToken.INSTANCEOF;}
	"int"			{return JavaToken.INT;}
	"interface"		{return JavaToken.INTERFACE;}
	"long"			{return JavaToken.LONG;}
	"native"		{return JavaToken.NATIVE;}
	"new"			{return JavaToken.NEW;}
	"package"		{return JavaToken.PACKAGE;}
	"private"		{return JavaToken.PRIVATE;}
	"protected"		{return JavaToken.PROTECTED;}
	"public"		{return JavaToken.PUBLIC;}
	"return"		{return JavaToken.RETURN;}
	"short"			{return JavaToken.SHORT;}
	"static"		{return JavaToken.STATIC;}
	"strictfp"		{return JavaToken.STRICTFP;}
	"super"			{return JavaToken.SUPER;}
	"switch"		{return JavaToken.SWITCH;}
	"synchronized"	{return JavaToken.SYNCHRONIZED;}
	"this"			{return JavaToken.THIS;}
	"throw"			{return JavaToken.THROW;}
	"throws"		{return JavaToken.THROWS;}
	"transient"		{return JavaToken.TRANSIENT;}
	"try"			{return JavaToken.TRY;}
	"void"			{return JavaToken.VOID;}
	"volatile"		{return JavaToken.VOLATILE;}
	"while"			{return JavaToken.WHILE;}
	{Integer}		{return JavaToken.INTLITERAL;}
	{Integer} [lL]	{return JavaToken.LONGLITERAL;}
	{Float} [Ff]	{return JavaToken.FLOATLITERAL;}
	{Float} [dD]?	{return JavaToken.DOUBLELITERAL;}
	"true"			{return JavaToken.TRUE;}
	"false"			{return JavaToken.FALSE;}
	"null"			{return JavaToken.NULL;}
	"_"				{return JavaToken.UNDERSCORE;}
	"->"			{return JavaToken.ARROW;}
	"::"			{return JavaToken.COLCOL;}
	"("				{return JavaToken.LPAREN;}
	")"				{return JavaToken.RPAREN;}
	"{"				{return JavaToken.LBRACE;}
	"}"				{return JavaToken.RBRACE;}
	"["				{return JavaToken.LBRACKET;}
	"]"				{return JavaToken.RBRACKET;}
	";"				{return JavaToken.SEMI;}
 	","				{return JavaToken.COMMA;}
	"."				{return JavaToken.DOT;}
	"..."			{return JavaToken.ELLIPSIS;}
	"="				{return JavaToken.EQ;}
	">"				{return JavaToken.GT;}
	"<"				{return JavaToken.LT;}
 	"!"				{return JavaToken.BANG;}
	"~"				{return JavaToken.TILDE;}
	"?"				{return JavaToken.QUES;}
	":"				{return JavaToken.COLON;}
	"=="			{return JavaToken.EQEQ;}
	"<="			{return JavaToken.LTEQ;}
	">=" 			{return JavaToken.GTEQ;}
	"!="			{return JavaToken.BANGEQ;}
	"&&"			{return JavaToken.AMPAMP;}
	"||"			{return JavaToken.BARBAR;}
	"//"			{return JavaToken.PLUSPLUS;}
	"--"			{return JavaToken.SUBSUB;}
	"+"				{return JavaToken.PLUS;}
	"-"				{return JavaToken.SUB;}
	"*"				{return JavaToken.STAR;}
	"/"				{return JavaToken.SLASH;}
	"&"				{return JavaToken.AMP;}
	"|"				{return JavaToken.BAR;}
	"^"				{return JavaToken.CARET;}
	"%"				{return JavaToken.PERCENT;}
	"<<"			{return JavaToken.LTLT;}
	">>"			{return JavaToken.GTGT;}
	">>>"			{return JavaToken.GTGTGT;}
	"+="			{return JavaToken.PLUSEQ;}
	"-="			{return JavaToken.SUBEQ;}
	"*="			{return JavaToken.STAREQ;}
	"/="			{return JavaToken.SLASHEQ;}
	"&="			{return JavaToken.AMPEQ;}
	"|="			{return JavaToken.BAREQ;}
	"^="			{return JavaToken.CARETEQ;}
	"%="			{return JavaToken.PERCENTEQ;}
	"<<="			{return JavaToken.LTLTEQ;}
	">>="			{return JavaToken.GTGTEQ;}
	">>>="			{return JavaToken.GTGTGTEQ;}
	"@"				{return JavaToken.MONKEYS_AT;}
	{Comment}		{return JavaToken.COMMENT;}
	"\""			{yybegin(STRING);}
	"'"				{yybegin(CHAR);}
	[a-zA-Z_][A-Za-z0-9_]*				{return JavaToken.IDENTIFIER;}
	[\r\n\t ]+ { return JavaToken.SPACE; }
	<<EOF>>			{return JavaToken.EOF;}
}

<STRING>{
	"\""			{yybegin(YYINITIAL);zzStartRead--;return JavaToken.STRINGLITERAL;}
	
	[^\r\n]+		{return JavaToken.STRINGLITERAL;}
	"\\\""			{return JavaToken.STRINGLITERAL;}
	[\r\n]			{yybegin(YYINITIAL);return JavaToken.ERROR;}
	<<EOF>>			{return JavaToken.ERROR;}
}

<CHAR>{
	'				{return endChar();}
	[^\r\n]+		{}
	[\r\n]			{yybegin(YYINITIAL);return JavaToken.ERROR;}
	<<EOF>>			{return JavaToken.ERROR;}
}
