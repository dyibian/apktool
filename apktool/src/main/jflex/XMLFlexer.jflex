package com.myopicmobile.textwarrior.common;

import static com.myopicmobile.textwarrior.common.XMLToken.*;

%%

%public
%class XMLFlexer
%type XMLToken
%line
%column
%char

%{
	public boolean eof = false;
%}
Name = [A-Za-z0-9]

%state TAG
%state HEAD
%state ATTR
%state COMENT
%%

<YYINITIAL>{
	"<?" {yybegin(HEAD);return XMLToken.HEAD;}
	"<"|"</"  {yybegin(TAG);return LPAREN;}
	"<!--"    {yybegin(COMENT);return COMMENT;}
	[^\r\n\t< ]+  {return TEXT;}
	[\r\n\t ]+ {return SPACE;}
	<<EOF>> {return END;}
}

<HEAD>{
	">"   {yybegin(YYINITIAL);return XMLToken.HEAD;}
	[^\r\n\t> ]+ {return XMLToken.HEAD;}
	[\r\n\t ]+ {return SPACE;}
	<<EOF>> {return END;}
}

<TAG>{
	(({Name}+) "-" )*({Name}+) {yybegin(ATTR);return TAG_NAME;}
	[\r\n\t ]+ {return SPACE;}
	<<EOF>> {return END;}
}

<ATTR>{
	">" {yybegin(YYINITIAL);return RPAREN;}
	[=/] {return SLASH;}
	\" [^\"]* \" {return ATTR_VALUE;}
	[^>/=\"\n\r\t ]+  {return ATTR_NAME;}
	[\r\n\t ]+ {return SPACE;}
	<<EOF>> {return END;}
}

<COMENT>{
	--+>	{yybegin(YYINITIAL);return COMMENT;}
	[^->]+	{return COMMENT;}
	 -+ [^>]   {return COMMENT;}
	>+   {return COMMENT;}
	 <<EOF>>  {eof=true;return COMMENT;}
}
