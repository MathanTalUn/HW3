/***************************/
/* FILE NAME: LEX_FILE.lex */
/***************************/

import java_cup.runtime.*;

%%

%class Lexer
%line
%column
%cupsym TokenNames
%cup

%{
	private Symbol symbol(int type)               { return new Symbol(type, yyline + 1, yycolumn + 1); }
	private Symbol symbol(int type, Object value) { return new Symbol(type, yyline + 1, yycolumn + 1, value); }
	private Symbol symbol(int type, int line, int column, Object value) { return new Symbol(type, line, column, value); }
	
	public int getLine() { return yyline + 1; }
	public int getTokenStartPosition() { return yycolumn + 1; }
    
    private int stringLine;
	private int stringColumn;
	private String stringBuffer = "";
%}

LineTerminator	= \r|\n|\r\n
WhiteSpace		= {LineTerminator} | [ \t\f]
INTEGER			= 0 | [1-9][0-9]*
ID				= [a-zA-Z][a-zA-Z0-9]*

STRING_OPEN 	= \"
STRING_CLOSE 	= \"
CMNT_1_OPEN		= \/\/
CMNT_1_CLOSE	= \n|\r|\r\n
COMMENT_1		= [^\r\n]*
CMNT_2_OPEN		= \/\*
CMNT_2_CLOSE	= \*\/
COMMENT_2		= ([^*] | \*+[^/*])*

%state STRING
%state CMNT_1
%state CMNT_2

%%

<YYINITIAL> {

/* Keywords */
"class"             { return symbol(TokenNames.CLASS); }
"extends"           { return symbol(TokenNames.EXTENDS); }
"return"            { return symbol(TokenNames.RETURN); }
"new"               { return symbol(TokenNames.NEW); }
"if"                { return symbol(TokenNames.IF); }
"else"              { return symbol(TokenNames.ELSE); }
"while"             { return symbol(TokenNames.WHILE); }
"array"             { return symbol(TokenNames.ARRAY); }
"int"               { return symbol(TokenNames.TYPE_INT); }
"string"            { return symbol(TokenNames.TYPE_STRING); }
"void"              { return symbol(TokenNames.TYPE_VOID); }
"nil"               { return symbol(TokenNames.NIL); }

/* Operators & Separators */
"+"                 { return symbol(TokenNames.PLUS); }
"-"                 { return symbol(TokenNames.MINUS); }
"*"                 { return symbol(TokenNames.TIMES); }
"/"                 { return symbol(TokenNames.DIVIDE); }
"("                 { return symbol(TokenNames.LPAREN); }
")"                 { return symbol(TokenNames.RPAREN); }
"["                 { return symbol(TokenNames.LBRACK); }
"]"                 { return symbol(TokenNames.RBRACK); }
"{"                 { return symbol(TokenNames.LBRACE); }
"}"                 { return symbol(TokenNames.RBRACE); }
":="                { return symbol(TokenNames.ASSIGN); }
"="                 { return symbol(TokenNames.EQ); }
"<"                 { return symbol(TokenNames.LT); }
">"                 { return symbol(TokenNames.GT); }
"."                 { return symbol(TokenNames.DOT); }
","                 { return symbol(TokenNames.COMMA); }
";"                 { return symbol(TokenNames.SEMICOLON); }

/* Identifiers & Integers */
{ID}                { return symbol(TokenNames.ID, yytext()); }
{INTEGER}           { return symbol(TokenNames.INT, Integer.valueOf(yytext())); }

/* String handling */
{STRING_OPEN}	    {
                        stringLine = this.getLine();
                        stringColumn = this.getTokenStartPosition();
                        yybegin(STRING);
                    }

/* Comment handling */
{CMNT_1_OPEN}	    { yybegin(CMNT_1); }
{CMNT_2_OPEN}	    { yybegin(CMNT_2); }

{WhiteSpace}        { /* ignore */ }
<<EOF>>             { return symbol(TokenNames.EOF); }
.                   { throw new RuntimeException("ERROR(" + (yyline+1) + ")"); }
}

<STRING> {
    [^\n\r\"]+		{ stringBuffer += yytext(); }
    {STRING_CLOSE}	{ 
                        String tokenValue = stringBuffer;
                        stringBuffer = "";
                        yybegin(YYINITIAL);
                        return symbol(TokenNames.STRING, stringLine, stringColumn, tokenValue);
                    }
    <<EOF>>			{ throw new RuntimeException("ERROR(" + (yyline+1) + ")"); }
}

<CMNT_1> {
    {CMNT_1_CLOSE} 	{ yybegin(YYINITIAL); }
    {COMMENT_1}		{ /* Ignore content */ }
    <<EOF>>			{ return symbol(TokenNames.EOF); }
}

<CMNT_2> {
    {CMNT_2_CLOSE}	{ yybegin(YYINITIAL); }
    {COMMENT_2}		{ /* Ignore content */ }
    <<EOF>>			{ throw new RuntimeException("ERROR(" + (yyline+1) + ")"); }
    .               { /* catch-all for anything not matched by COMMENT_2 */ }
}