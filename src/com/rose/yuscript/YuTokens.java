/*
 * Copyright 2020 Rose2073
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.rose.yuscript;

/**
 * @author Rose
 * Tokens of yu language
 */
public enum YuTokens {
	DOT,//.
	COMMA,//,
	LPAREN,//(
	RPAREN,//)
	LBRACE,//{
	RBRACE,//}
	
	ANDAND,//&&
	OROR,//||
	
	STARTS_WITH,//?*
	ENDS_WITH,//*?
	CONTAINS,//?
	
	EQEQ,//==
	NOTEQ,//!=
	LTEQ,//<=
	GTEQ,//>=
	EQ,//=
	NOT,//!
	LT,//<
	GT,//>
	SEMI,//;
	
	PLUS,// +
	MINUS,// -
	MULTIPLY,// *
	DIVIDE,// /
	
	//Constant expression
	STRING,
	NUMBER,
	
	ENDCODE,//end code
	BREAK,//break
	VARIABLE_PREFIX,//s,ss,sss
	
	COMMENT,
	WHITESPACE,
	NEWLINE,
	EOF,
	UNKNOWN,
	IDENTIFIER,
	IF,
	ELSE,
	WHILE,
	FOR,
	TRUE,FALSE,NULL
}
