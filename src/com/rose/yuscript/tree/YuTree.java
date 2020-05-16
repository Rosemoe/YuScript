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
package com.rose.yuscript.tree;

import java.util.Objects;

import com.rose.yuscript.*;
import static com.rose.yuscript.YuTokens.*;

/**
 * @author Rose
 *
 */
public final class YuTree {
	
	private final YuTokenizer tokenizer;
	private final YuScope root;

	public YuTree(YuTokenizer tokenizer) throws YuSyntaxError {
		this.tokenizer = Objects.requireNonNull(tokenizer);
		tokenizer.setCalculateLineColumn(true);
		tokenizer.setSkipComment(true);
		tokenizer.setSkipWhitespace(true);
		root = (YuScope)parseCodeBlock(true, false);
	}
	
	public YuScope getRoot() {
		return root;
	}
	
	private YuCodeBlock parseCodeBlock(boolean outside, boolean exitOnEnd) throws YuSyntaxError {
		YuCodeBlock block = outside ? new YuScope() : new YuCodeBlock();
		while(tokenizer.nextToken() != EOF) {
			switch(tokenizer.getToken()) {
			case LBRACE:{
				block.addChild(parseCodeBlock(false, false));
				break;
			}
			case RBRACE:{
				if(!outside) {
					return block;
				}else {
					throw new YuSyntaxError("unexpected '}'");
				}
			}
			case VARIABLE_PREFIX:{
				tokenizer.pushBack(tokenizer.getTokenLength());
				block.addChild(parseAssignment());
				break;
			}
			case IDENTIFIER:{
				tokenizer.pushBack(tokenizer.getTokenLength());
				block.addChild(parseFunctionCall());
				break;
			}
			case IF:{
				tokenizer.pushBack(tokenizer.getTokenLength());
				block.addChild(parseIfTree());
				break;
			}
			case WHILE:{
				tokenizer.pushBack(tokenizer.getTokenLength());
				block.addChild(parseWhileTree());
				break;
			}
			case FOR:{
				tokenizer.pushBack(tokenizer.getTokenLength());
				block.addChild(parseForTree());
				break;
			}
			case ENDCODE:{
				block.addChild(new YuEndcode());
				break;
			}
			case BREAK:{
				block.addChild(new YuBreak());
				break;
			}
			case FUNCTION:{
				tokenizer.pushBack(tokenizer.getTokenLength());
				block.addFunction(parseFunction());
				break;
			}
			case EOF:{
				if(outside) {
					return block;
				}else {
					throw new YuSyntaxError();
				}
			}
			case END:
				if(exitOnEnd) {
					tokenizer.pushBack(tokenizer.getTokenLength());
					return block;
				}
			default:{
				throw new YuSyntaxError();
			}
			}
		}
		return block;
	}

	private YuFunction parseFunction() throws YuSyntaxError {
		if(tokenizer.nextToken() != FUNCTION) {
			throw new YuSyntaxError("'fn' expected");
		}
		if(tokenizer.nextToken() != IDENTIFIER) {
			throw new YuSyntaxError("Identifier expected");
		}
		YuFunction function = new YuFunction();
		function.setName(tokenizer.getTokenString());
		if(tokenizer.nextToken() != LPAREN) {
			throw new YuSyntaxError("'(' expected");
		}
		YuTokens next = tokenizer.nextToken();
		while(next == IDENTIFIER) {
			function.addParameter(tokenizer.getTokenString());
			next = tokenizer.nextToken();
			if(next == COMMA) {
				next = tokenizer.nextToken();
				if(next != IDENTIFIER) {
					throw new YuSyntaxError("Identifier expected");
				}
			} else if(next == RPAREN) {
				break;
			} else{
				throw new YuSyntaxError("unexpected '" + tokenizer.getTokenString() + "' here");
			}
		}
		function.setFunctionBody(parseCodeBlock(true, true));
		if(tokenizer.nextToken() != END) {
			throw new YuSyntaxError("'end' expected");
		}
		if(tokenizer.nextToken() != FUNCTION) {
			throw new YuSyntaxError("'fn' expected");
		}
		return function;
	}

	private YuForTree parseForTree() throws YuSyntaxError {
		YuForTree tree = new YuForTree();
		if(tokenizer.nextToken() != FOR) {
			throw new YuSyntaxError("'for' expected");
		}
		if(tokenizer.nextToken() != LPAREN) {
			throw new YuSyntaxError("'(' expected");
		}
		tokenizer.nextToken();
		tree.setDest(parseValue());
		if(tokenizer.nextToken() != SEMI) {
			throw new YuSyntaxError("';' expected");
		}
		tokenizer.nextToken();
		tree.setSrc(parseValue());
		if(tokenizer.nextToken() != RPAREN) {
			throw new YuSyntaxError("')' expected");
		}
		if(tokenizer.nextToken() != LBRACE) {
			throw new YuSyntaxError("'{' expected");
		}
		tree.setCodeBlock(parseCodeBlock(false, false));
		return tree;
	}
	
	private YuWhileTree parseWhileTree() throws YuSyntaxError {
		YuWhileTree tree = new YuWhileTree();
		if(tokenizer.nextToken() != WHILE) {
			throw new YuSyntaxError("'w' expected");
		}
		if(tokenizer.nextToken() != LPAREN) {
			throw new YuSyntaxError("'(' expected");
		}
		tree.setCondition(parseConditionalExpression());
		if(tokenizer.nextToken() != RPAREN) {
			throw new YuSyntaxError("')' expected");
		}
		if(tokenizer.nextToken() != LBRACE) {
			throw new YuSyntaxError("'{' expected");
		}
		tree.setCodeBlock(parseCodeBlock(false, false));
		return tree;
	}
	
	private YuIfTree parseIfTree() throws YuSyntaxError {
		YuIfTree tree = new YuIfTree();
		if(tokenizer.nextToken() != IF) {
			throw new YuSyntaxError("'f' expected");
		}
		if(tokenizer.nextToken() != LPAREN) {
			throw new YuSyntaxError("'(' expected");
		}
		tree.setCondition(parseConditionalExpression());
		if(tokenizer.nextToken() != RPAREN) {
			throw new YuSyntaxError("')' expected");
		}
		if(tokenizer.nextToken() != LBRACE) {
			throw new YuSyntaxError("'{' expected");
		}
		tree.setCodeBlock(parseCodeBlock(false, false));
		YuTokens next = tokenizer.nextToken();
		if(next != ELSE) {
			tokenizer.pushBack(tokenizer.getTokenLength());
			return tree;
		}
		next = tokenizer.nextToken();
		if(next == IF) {
			YuCodeBlock block = new YuCodeBlock();
			tokenizer.pushBack(tokenizer.getTokenLength());
			block.addChild(parseIfTree());
			tree.setFallbackCodeBlock(block);
		}else if(next == LBRACE) {
			tree.setFallbackCodeBlock(parseCodeBlock(false, false));
		}else {
			throw new YuSyntaxError();
		}
		return tree;
	}
	
	private YuConditionalExpression parseConditionalExpression() throws YuSyntaxError {
		YuConditionalExpression expr = new YuConditionalExpression();
		YuCondition condition = parseCondition();
		expr.addChild(condition);
		while(true) {
			YuTokens next = tokenizer.nextToken();
			if(next == ANDAND || next == OROR) {
				expr.addExpression(next, parseCondition());
			}else {
				tokenizer.pushBack(tokenizer.getTokenLength());
				break;
			}
		}
		return expr;
	}
	
	private YuCondition parseCondition() throws YuSyntaxError {
		YuCondition condition = new YuCondition();
		condition.setLeft(parseExpression());
		YuTokens token = tokenizer.nextToken();
		switch(token) {
		case EQEQ:
		case LT:
		case LTEQ:
		case GT:
		case GTEQ:
		case STARTS_WITH:
		case ENDS_WITH:
		case CONTAINS:
		case NOTEQ:
			condition.setOperator(token);
			break;
		default:
			throw new YuSyntaxError("unexpected '" + tokenizer.getTokenString() + "' here");
		}
		condition.setRight(parseExpression());
		return condition;
	}
	
	private YuAssignment parseAssignment() throws YuSyntaxError {
		YuAssignment assignment = new YuAssignment();
		if(tokenizer.nextToken() != VARIABLE_PREFIX) {
			throw new YuSyntaxError("'s','ss','sss' expected");
		}
		assignment.setVariableType(tokenizer.getTokenString());
		if(tokenizer.nextToken() == IDENTIFIER) {
			assignment.setVariableName(tokenizer.getTokenString());
		}else {
			throw new YuSyntaxError("Identifier expected");
		}
		if(tokenizer.nextToken() != EQ) {
			throw new YuSyntaxError("'=' expected");
		}
		assignment.setValue(parseExpression());
		return assignment;
	}
	
	private YuFunctionCall parseFunctionCall() throws YuSyntaxError {
		YuFunctionCall call = new YuFunctionCall();
		if(tokenizer.nextToken() != IDENTIFIER) {
			throw new YuSyntaxError("Identifier expected");
		}
		call.setFunctionName(tokenizer.getTokenString());
		if(tokenizer.nextToken() != LPAREN) {
			throw new YuSyntaxError("'(' expected");
		}
		if(tokenizer.nextToken() == RPAREN) {
			return call;
		}
		tokenizer.pushBack(tokenizer.getTokenLength());
		while(true) {
			YuExpression expr = parseExpression();
			call.addArgument(expr);
			tokenizer.nextToken();
			if(tokenizer.getToken() == RPAREN) {
				break;
			}else if(tokenizer.getToken() != COMMA){
				throw new YuSyntaxError("',' or ')' expected");
			}
		}
		return call;
	}

	private YuExpression parseExpression() throws YuSyntaxError {
		YuExpression expression = new YuExpression();
		YuTokens token = tokenizer.nextToken();
		boolean invert = token == NOT;
		if(invert) {
			token = tokenizer.nextToken();
		}
		switch(token) {
		case LPAREN:{
			YuExpression expr = parseExpression();
			expr.setInvert(invert);
			expression.addChild(expr);
			if(tokenizer.nextToken() != RPAREN) {
				throw new YuSyntaxError("')' expected");
			}
			break;
		}
		case NUMBER:
		case STRING:
		case VARIABLE_PREFIX:
		case IDENTIFIER:
		case TRUE:
		case FALSE:
		case NULL:
		{
			YuValue value = parseValue();
			value.setInvert(invert);
			expression.addChild(value);
			break;
		}
		case MINUS:
		{
			token = tokenizer.nextToken();
			if(token != NUMBER && token != VARIABLE_PREFIX && token != IDENTIFIER) {
				throw new YuSyntaxError();
			}
			YuValue val = new YuValue();
			val.setInvert(invert);
			val.setNumber("-" + tokenizer.getTokenString());
			expression.addChild(val);
			break;
		}
		default:
			throw new YuSyntaxError();
		}
		token = tokenizer.nextToken();
		while(true) {
			invert = false;
			if(token == PLUS || token == MINUS || token == MULTIPLY || token == DIVIDE || token == NOT) {
				if(token == NOT) {
					invert = true;
					token = tokenizer.nextToken();
					if(!(token == PLUS || token == MINUS || token == MULTIPLY || token == DIVIDE)) {
						throw new YuSyntaxError("operator expected");
					}
				}
				YuTokens op = tokenizer.getToken();
				token = tokenizer.nextToken();
				switch(token) {
				case LPAREN:{
					YuExpression expr = parseExpression();
					expr.setInvert(invert);
					expression.addExpression(op,expr);
					if(tokenizer.getToken() != RPAREN) {
						throw new YuSyntaxError("')' expected");
					}
					break;
				}
				case NUMBER:
				case STRING:
				case VARIABLE_PREFIX:
				case IDENTIFIER:
				case TRUE:
				case FALSE:
				case NULL:
				{
					YuValue val = parseValue();
					val.setInvert(invert);
					expression.addExpression(op,val);
					break;
				}
				case MINUS:
				{
					token = tokenizer.nextToken();
					if(token != NUMBER) {
						throw new YuSyntaxError("number literal expected");
					}
					YuValue val = new YuValue();
					val.setInvert(invert);
					val.setNumber("-" + tokenizer.getTokenString());
					expression.addExpression(op,val);
					break;
				}
				default:
					throw new YuSyntaxError("unexpected '" + tokenizer.getTokenString() + "' here");
				}
				token = tokenizer.nextToken();
			}else{
				tokenizer.pushBack(tokenizer.getTokenLength());
				break;
			}
		}
		return expression;
	}
	
	private YuValue parseValue() throws YuSyntaxError {
		YuValue value = new YuValue();
		switch(tokenizer.getToken()) {
		case NUMBER:
			value.setNumber(tokenizer.getTokenString());
			break;
		case STRING:
			value.setString(tokenizer.getTokenString());
			break;
		case VARIABLE_PREFIX:
			String prefix = tokenizer.getTokenString();
			if(tokenizer.nextToken() != DOT) {
				throw new YuSyntaxError("'.' expected");
			}
			if(tokenizer.nextToken() != IDENTIFIER) {
				throw new YuSyntaxError("Identifier expected");
			}
			value.setVariableName(prefix + "." + tokenizer.getTokenString());
			break;
		case IDENTIFIER:
			value.setVariableName(tokenizer.getTokenString());
			break;
		case TRUE:
			value.setBool(true);
			break;
		case FALSE:
			value.setBool(false);
			break;
		case NULL:
			value.setNull();
			break;
		default:
			throw new YuSyntaxError();
		}
		return value;
	}
	
}
