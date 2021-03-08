
package rs.ac.bg.etf.pp1;

import java_cup.runtime.Symbol;

%%

%{

	// ukljucivanje informacije o poziciji tokena
	private Symbol new_symbol(int type) {
		return new Symbol(type, yyline+1, yycolumn);
	}
	
	// ukljucivanje informacije o poziciji tokena
	private Symbol new_symbol(int type, Object value) {
		return new Symbol(type, yyline+1, yycolumn, value);
	}

%}

%cup
%line
%column

%xstate COMMENT

%eofval{
	return new_symbol(sym.EOF);
%eofval}

%%

" " 	{ }
"\b" 	{ }
"\t" 	{ }
"\r\n" 	{ }
"\f" 	{ }


"break" 	{ return new_symbol(sym.BREAK, yytext());}
"class" 	{ return new_symbol(sym.CLASS, yytext());}
"enum" 		{ return new_symbol(sym.ENUM, yytext());}
"else" 		{ return new_symbol(sym.ELSE, yytext());}
"const" 	{ return new_symbol(sym.CONST, yytext());}
"if" 		{ return new_symbol(sym.IF, yytext());}
"switch" 	{ return new_symbol(sym.SWITCH, yytext());}
"do" 		{ return new_symbol(sym.DO, yytext());}
"while" 	{ return new_symbol(sym.WHILE, yytext());}
"new"       { return new_symbol(sym.NEW, yytext());}
"read"		{ return new_symbol(sym.READ, yytext());}
"extends"	{ return new_symbol(sym.EXTENDS, yytext());}
"continue"	{ return new_symbol(sym.CONTINUE, yytext());}
"case"		{ return new_symbol(sym.CASE, yytext());}
"program"   { return new_symbol(sym.PROGRAM, yytext());}
"print" 	{ return new_symbol(sym.PRINT, yytext()); }
"return" 	{ return new_symbol(sym.RETURN, yytext()); }
"void" 		{ return new_symbol(sym.VOID, yytext()); }

"+" 		{ return new_symbol(sym.ADD, yytext()); }
"-" 		{ return new_symbol(sym.SUB, yytext()); }
"*" 		{ return new_symbol(sym.MUL, yytext()); }
"/" 		{ return new_symbol(sym.DIV, yytext()); }
"==" 		{ return new_symbol(sym.EQ, yytext()); }
"!=" 		{ return new_symbol(sym.NEQ, yytext()); }
">" 		{ return new_symbol(sym.G, yytext()); }
">=" 		{ return new_symbol(sym.GE, yytext()); }
"<" 		{ return new_symbol(sym.L, yytext()); }
"<=" 		{ return new_symbol(sym.LE, yytext()); }
"&&" 		{ return new_symbol(sym.ANDAND, yytext()); }
"||" 		{ return new_symbol(sym.OROR, yytext()); }
"%" 		{ return new_symbol(sym.MOD, yytext()); }
"=" 		{ return new_symbol(sym.DODELA, yytext()); }
"++" 		{ return new_symbol(sym.INC, yytext()); }
"--" 		{ return new_symbol(sym.DEC, yytext()); }
";" 		{ return new_symbol(sym.TZ, yytext()); }
"," 		{ return new_symbol(sym.COMMA, yytext()); }
"." 		{ return new_symbol(sym.DOT, yytext()); }

"(" 		{ return new_symbol(sym.LZAGRADA, yytext()); }
")" 		{ return new_symbol(sym.DZAGRADA, yytext()); }
"[" 		{ return new_symbol(sym.LUGLASTA, yytext()); }
"]" 		{ return new_symbol(sym.DUGLASTA, yytext()); }
"{" 		{ return new_symbol(sym.LVITICASTA, yytext()); }
"}"			{ return new_symbol(sym.DVITICASTA, yytext()); }
"?" 		{ return new_symbol(sym.UPITNIK, yytext()); }
":"			{ return new_symbol(sym.DVOTACKA, yytext()); }


"//" {yybegin(COMMENT);}
<COMMENT> . {yybegin(COMMENT);}
<COMMENT> "\r\n" { yybegin(YYINITIAL); }

[0-9]+  { return new_symbol(sym.NUMBER, new Integer (yytext())); }
\'[^']\' { return new_symbol(sym.CHAR,yytext()); }
("true" | "false") { return new_symbol(sym.BOOLEAN, new String (yytext())); }
([a-z]|[A-Z])[a-z|A-Z|0-9|_]* 	{return new_symbol (sym.IDENT, yytext()); }

. { System.err.println("Leksicka greska ("+yytext()+") u liniji "+(yyline+1)+" u koloni " + yycolumn); }
