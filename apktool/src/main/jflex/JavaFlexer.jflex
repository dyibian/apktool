package com.myopicmobile.textwarrior.common;


%%

%public
%class JavaLexer
%type JavaToken
%line
%column
%char

%{
	private StringBuilder string = new StringBuilder();
	//private static final String chString = "'([^\\\\]|\\\\[bfrtn(\\\\)]|\\\\u[0-9a-fA-F]{4})'";
	private JavaToken endStringOrChar(JavaToken token){
		token.setLen(string.length());
		return token;
	}
	private JavaToken end(JavaToken token){
		token.setLen(yylength());
		return token;
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
	"abstract"		{return end(JavaToken.ABSTRACT); }
	"abstract"		{return end(JavaToken.ABSTRACT);}
	"assert"		{return end(JavaToken.ASSERT);}
	"boolean"		{return end(JavaToken.BOOLEAN);}
	"break"			{return end(JavaToken.BREAK);}
	"byte"			{return end(JavaToken.BYTE);}
	"case"			{return end(JavaToken.CASE);}
	"catch"			{return end(JavaToken.CATCH);}
	"char"			{return end(JavaToken.CHAR);}
	"class"			{return end(JavaToken.CLASS);}
	"const"			{return end(JavaToken.CONST);}
	"continue"		{return end(JavaToken.CONTINUE);}
	"default"		{return end(JavaToken.DEFAULT);}
	"do"			{return end(JavaToken.DO);}
	"double"		{return end(JavaToken.DOUBLE);}
	"else"			{return end(JavaToken.ELSE);}
	"enum"			{return end(JavaToken.ENUM);}
	"extends"		{return end(JavaToken.EXTENDS);}
	"final"			{return end(JavaToken.FINAL);}
	"finally"		{return end(JavaToken.FINALLY);}
	"float"			{return end(JavaToken.FLOAT);}
	"for"			{return end(JavaToken.FOR);}
	"goto"			{return end(JavaToken.GOTO);}
 	"if"			{return end(JavaToken.IF);}
	"implements"	{return end(JavaToken.IMPLEMENTS);}
	"import"		{return end(JavaToken.IMPORT);}
	"instanceof"	{return end(JavaToken.INSTANCEOF);}
	"int"			{return end(JavaToken.INT);}
	"interface"		{return end(JavaToken.INTERFACE);}
	"long"			{return end(JavaToken.LONG);}
	"native"		{return end(JavaToken.NATIVE);}
	"new"			{return end(JavaToken.NEW);}
	"package"		{return end(JavaToken.PACKAGE);}
	"private"		{return end(JavaToken.PRIVATE);}
	"protected"		{return end(JavaToken.PROTECTED);}
	"public"		{return end(JavaToken.PUBLIC);}
	"return"		{return end(JavaToken.RETURN);}
	"short"			{return end(JavaToken.SHORT);}
	"static"		{return end(JavaToken.STATIC);}
	"strictfp"		{return end(JavaToken.STRICTFP);}
	"super"			{return end(JavaToken.SUPER);}
	"switch"		{return end(JavaToken.SWITCH);}
	"synchronized"	{return end(JavaToken.SYNCHRONIZED);}
	"this"			{return end(JavaToken.THIS);}
	"throw"			{return end(JavaToken.THROW);}
	"throws"		{return end(JavaToken.THROWS);}
	"transient"		{return end(JavaToken.TRANSIENT);}
	"try"			{return end(JavaToken.TRY);}
	"void"			{return end(JavaToken.VOID);}
	"volatile"		{return end(JavaToken.VOLATILE);}
	"while"			{return end(JavaToken.WHILE);}
	{Integer}		{return end(JavaToken.INTLITERAL);}
	{Integer} [lL]	{return end(JavaToken.LONGLITERAL);}
	{Float} [Ff]	{return end(JavaToken.FLOATLITERAL);}
	{Float} [dD]?	{return end(JavaToken.DOUBLELITERAL);}
	"true"			{return end(JavaToken.TRUE);}
	"false"			{return end(JavaToken.FALSE);}
	"null"			{return end(JavaToken.NULL);}
	"_"				{return end(JavaToken.UNDERSCORE);}
	"->"			{return end(JavaToken.ARROW);}
	"::"			{return end(JavaToken.COLCOL);}
	"("				{return end(JavaToken.LPAREN);}
	")"				{return end(JavaToken.RPAREN);}
	"{"				{return end(JavaToken.LBRACE);}
	"}"				{return end(JavaToken.RBRACE);}
	"["				{return end(JavaToken.LBRACKET);}
	"]"				{return end(JavaToken.RBRACKET);}
	";"				{return end(JavaToken.SEMI);}
 	","				{return end(JavaToken.COMMA);}
	"."				{return end(JavaToken.DOT);}
	"..."			{return end(JavaToken.ELLIPSIS);}
	"="				{return end(JavaToken.EQ);}
	">"				{return end(JavaToken.GT);}
	"<"				{return end(JavaToken.LT);}
 	"!"				{return end(JavaToken.BANG);}
	"~"				{return end(JavaToken.TILDE);}
	"?"				{return end(JavaToken.QUES);}
	":"				{return end(JavaToken.COLON);}
	"=="			{return end(JavaToken.EQEQ);}
	"<="			{return end(JavaToken.LTEQ);}
	">=" 			{return end(JavaToken.GTEQ);}
	"!="			{return end(JavaToken.BANGEQ);}
	"&&"			{return end(JavaToken.AMPAMP);}
	"||"			{return end(JavaToken.BARBAR);}
	"//"			{return end(JavaToken.PLUSPLUS);}
	"--"			{return end(JavaToken.SUBSUB);}
	"+"				{return end(JavaToken.PLUS);}
	"-"				{return end(JavaToken.SUB);}
	"*"				{return end(JavaToken.STAR);}
	"/"				{return end(JavaToken.SLASH);}
	"&"				{return end(JavaToken.AMP);}
	"|"				{return end(JavaToken.BAR);}
	"^"				{return end(JavaToken.CARET);}
	"%"				{return end(JavaToken.PERCENT);}
	"<<"			{return end(JavaToken.LTLT);}
	">>"			{return end(JavaToken.GTGT);}
	">>>"			{return end(JavaToken.GTGTGT);}
	"+="			{return end(JavaToken.PLUSEQ);}
	"-="			{return end(JavaToken.SUBEQ);}
	"*="			{return end(JavaToken.STAREQ);}
	"/="			{return end(JavaToken.SLASHEQ);}
	"&="			{return end(JavaToken.AMPEQ);}
	"|="			{return end(JavaToken.BAREQ);}
	"^="			{return end(JavaToken.CARETEQ);}
	"%="			{return end(JavaToken.PERCENTEQ);}
	"<<="			{return end(JavaToken.LTLTEQ);}
	">>="			{return end(JavaToken.GTGTEQ);}
	">>>="			{return end(JavaToken.GTGTGTEQ);}
	"@"				{return end(JavaToken.MONKEYS_AT);}
	{Comment}		{return end(JavaToken.COMMENT);}
	\"				{yybegin(STRING);string.delete(0,string.length());;string.append('"');}
	'				{yybegin(CHAR);string.delete(0,string.length());;string.append("'");}
	[a-zA-Z_][A-Za-z0-9_]*				{return end(JavaToken.IDENTIFIER);}
	[\r\n\t ]+ { return end(JavaToken.SPACE); }
	<<EOF>>			{return end(JavaToken.EOF);}
}

<STRING>{
	\"		{yybegin(YYINITIAL);string.append('"');return endStringOrChar(JavaToken.STRINGLITERAL);}
	
	[^\r\n\"\\]+  { string.append(yytext()); }
	\\[bftrn\"\\]   {string.append(yytext());}
	{LineTerminator}			{yybegin(YYINITIAL);string.append(yytext());return endStringOrChar(JavaToken.ERROR);}
	<<EOF>>			{return endStringOrChar(JavaToken.ERROR);}
}

<CHAR>{
	'   {yybegin(YYINITIAL);string.append("'");return endStringOrChar(JavaToken.CHARLITERAL);}
	[^\r\n\'\\]  |"\\b" | "\\\""  |
	"\\f" | "\\n" | "\\'"  |
	"\\t" | "\\r" {string.append(yytext());}
	{LineTerminator}			{yybegin(YYINITIAL);return endStringOrChar(JavaToken.ERROR);}
	<<EOF>>			{return endStringOrChar(JavaToken.ERROR);}
}
