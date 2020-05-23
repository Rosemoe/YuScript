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
package io.github.rose2073.yuscript.tree;

import io.github.rose2073.yuscript.YuTokens;
import io.github.rose2073.yuscript.util.MyCharacter;
import io.github.rose2073.yuscript.util.TrieTree;

/**
 * @author Rose
 *
 */
public class YuTokenizer {

    private static TrieTree<YuTokens> keywords;

    static {
        doStaticInit();
    }

    private String source;
    protected int bufferLen;
    private int line;
    private int column;
    private int index;
    protected int offset;
    protected int length;
    private YuTokens currToken;
    private boolean lcCal;
    private boolean skipWS;
    private boolean skipComment;

    public YuTokenizer(String src) {
        if(src == null) {
            throw new IllegalArgumentException("src can not be null");
        }
        this.source = src;
        init();
    }

    private void init() {
        line = 0;
        column = 0;
        length = 0;
        index = 0;
        currToken = YuTokens.WHITESPACE;
        lcCal = false;
        skipWS = false;
        skipComment = false;
        this.bufferLen = source.length();
    }

    public void setCalculateLineColumn(boolean cal) {
        this.lcCal = cal;
    }

    public void setSkipWhitespace(boolean skip) {
        this.skipWS = skip;
    }

    public void setSkipComment(boolean skip) {
        this.skipComment = skip;
    }

    public void pushBack(int length) {
        if (length > getTokenLength()) {
            throw new IllegalArgumentException("pushBack length too large");
        }
        this.length -= length;
    }

    private boolean isIdentifierPart(char ch) {
        return MyCharacter.isJavaIdentifierPart(ch);
    }

    private boolean isIdentifierStart(char ch) {
        return MyCharacter.isJavaIdentifierStart(ch);
    }

    public String getTokenString() {
        return source.substring(offset, offset + length);
    }

    public int getTokenLength() {
        return length;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public int getIndex() {
        return index;
    }

    public YuTokens getToken() {
        return currToken;
    }

    public String yyDesc() {
        return " line " + line + " column " + column;
    }

    private char charAt(int i) {
        return source.charAt(i);
    }

    private char charAt() {
        return source.charAt(offset + length);
    }

    public YuTokens nextToken() {
        YuTokens token;
        do {
            token = directNextToken();
        } while ((skipWS && (token == YuTokens.WHITESPACE || token == YuTokens.NEWLINE)) || (skipComment && token == YuTokens.COMMENT));
        currToken = token;
        return token;
    }

    public YuTokens directNextToken() {
        if (lcCal) {
            boolean r = false;
            for (int i = offset; i < offset + length; i++) {
                char ch = charAt(i);
                if (ch == '\r') {
                    r = true;
                    line++;
                    column = 0;
                } else if (ch == '\n') {
                    if (r) {
                        r = false;
                        continue;
                    }
                    line++;
                    column = 0;
                } else {
                    r = false;
                    column++;
                }
            }
        }
        index = index + length;
        offset = offset + length;
        if (offset == bufferLen) {
            length = 0;
            return YuTokens.EOF;
        }
        char ch = source.charAt(offset);
        length = 1;
        if (ch == '\n') {
            return YuTokens.NEWLINE;
        } else if (ch == '\r') {
            scanNewline();
            return YuTokens.NEWLINE;
        } else if (isWhitespace(ch)) {
            char chLocal;
            while (offset + length < bufferLen && isWhitespace(chLocal = charAt(offset + length)) ) {
                if (chLocal == '\r' || chLocal == '\n') {
                    break;
                }
                length++;
            }
            return YuTokens.WHITESPACE;
        } else {
            if (isIdentifierStart(ch)) {
                return scanIdentifier(ch);
            }
            if (isPrimeDigit(ch)) {
                scanNumber();
                return YuTokens.NUMBER;
            }
            if(ch == '(') {
                return YuTokens.LPAREN;
            }else if(ch == ')') {
                return YuTokens.RPAREN;
            }else if(ch == '<') {
                return scanOperatorTwo('=', YuTokens.LT, YuTokens.LTEQ);
            }else if(ch == '>') {
                return scanOperatorTwo('=', YuTokens.GT, YuTokens.GTEQ);
            }
            switch (ch) {
                case '=':
                    return scanOperatorTwo('=', YuTokens.EQ, YuTokens.EQEQ);
                case '.':
                	if(column == 0) {
                		while(offset + length < bufferLen && charAt() != '\r' && charAt() != '\n') {
                			length++;
                		}
                		return YuTokens.COMMENT;
                	}
                    return YuTokens.DOT;
                case '{':
                    return YuTokens.LBRACE;
                case '}':
                    return YuTokens.RBRACE;
                case '/':
                    return scanDIV();
                case '*':
                    return scanOperatorTwo('?', YuTokens.MULTIPLY, YuTokens.ENDS_WITH);
                case '-':
                    return YuTokens.MINUS;
                case '+':
                    return YuTokens.PLUS;
                case ',':
                    return YuTokens.COMMA;
                case '!':
                    return scanOperatorTwo('=', YuTokens.NOT, YuTokens.NOTEQ);
                case '?':
                    return scanOperatorTwo('*', YuTokens.CONTAINS, YuTokens.STARTS_WITH);
                case '\"':
                    scanString();
                    return YuTokens.STRING;
                case ';':
                	return YuTokens.SEMI;
                case '|':
                	if(scanOperatorTwo('|', YuTokens.UNKNOWN, YuTokens.OROR) == YuTokens.OROR){
                		return YuTokens.OROR;
                	}
                	bad();
                	return YuTokens.UNKNOWN;
                case '&':
                	if(scanOperatorTwo('&', YuTokens.UNKNOWN, YuTokens.ANDAND) == YuTokens.ANDAND){
                		return YuTokens.ANDAND;
                	}
                	bad();
                	return YuTokens.UNKNOWN;
                default:
                    bad();
                    return YuTokens.UNKNOWN;
            }
        }
    }

    protected final void throwIfNeeded() {
        if(offset + length == bufferLen) {
            bad();
        }
    }

    protected void scanNewline() {
        if (offset + length < bufferLen && charAt(offset + length) == '\n') {
            length++;
        }
    }

    protected YuTokens scanIdentifier(char ch) {
        TrieTree.Node<YuTokens> n = keywords.root.map.get(ch);
        while (offset + length < bufferLen && isIdentifierPart(ch = charAt(offset + length))) {
            length++;
            n = n == null ? null : n.map.get(ch);
        }
        YuTokens rt = (n == null ? YuTokens.IDENTIFIER : (n.token == null ? YuTokens.IDENTIFIER : n.token));
        //Predicate
        if(rt == YuTokens.VARIABLE_PREFIX && offset + length < bufferLen) {
        	//Skip White spaces to get next valid character
        	int extraOffset = 0;
        	char predicate_ch = '\n';
        	while(offset + length + extraOffset < bufferLen && isWhitespace(predicate_ch = charAt(offset + length + extraOffset))) {
        		extraOffset++;
        	}
        	if(!isWhitespace(predicate_ch)) {
        		//For iyu language ugly function name
        		if(length == 1 && extraOffset == 0 && (predicate_ch == '2' || predicate_ch == '+' || predicate_ch == '-' || predicate_ch == '*' || predicate_ch == '/')) {
        			extraOffset++;
        			while(offset + length + extraOffset < bufferLen && isWhitespace(predicate_ch = charAt(offset + length + extraOffset))) {
                		extraOffset++;
                	}
        			if(predicate_ch == '(') {
        				length++;
        				return YuTokens.IDENTIFIER;
        			}
        		}
        		//
        		if(predicate_ch != '.' && !isIdentifierStart(predicate_ch)) {
        			return YuTokens.IDENTIFIER;
        		}
        	}
        }
        if(rt == YuTokens.WHILE || rt == YuTokens.FOR || rt == YuTokens.IF) {
        	int extraOffset = 0;
        	char predicate_ch = '\n';
        	while(offset + length + extraOffset < bufferLen && isWhitespace(predicate_ch = charAt(offset + length + extraOffset))) {
        		extraOffset++;
        	}
        	if(predicate_ch != '(') {
        		return YuTokens.IDENTIFIER;
        	}
        }
        return rt;
    }
    
    private void bad() {
    	throw new IllegalArgumentException("syntax error:token start at " + yyDesc());
    }

    protected void scanTrans() {
        throwIfNeeded();
        char ch = charAt(offset + length);
        if (ch == '\\' || ch == 't' || ch == 'f' || ch == 'n' || ch == 'r' || ch == '0' || ch == '\"' || ch == '\''
                || ch == 'b') {
            length++;
        } else if (ch == 'u') {
            length++;
            for (int i = 0; i < 4; i++) {
                throwIfNeeded();
                if (!isDigit(charAt(offset + length))) {
                	bad();
                    return;
                }
                length++;
            }
        } else {
            bad();
        }
    }

    protected void scanString() {
        throwIfNeeded();
        char ch;
        while (offset + length < bufferLen && (ch = charAt(offset + length)) != '\"') {
            if (ch == '\\') {
                length++;
                scanTrans();
            } else {
                if (ch == '\n') {
                	bad();
                    return;
                }
                length++;
                throwIfNeeded();
            }
        }
        if (offset + length == bufferLen) {
           bad();
        } else {
            length++;
        }
    }

    protected void scanNumber() {
        while(offset + length < bufferLen && isPrimeDigit(charAt())) {
        	length++;
        }
    }

    protected YuTokens scanDIV() {
        if (offset + 1 == bufferLen) {
            return YuTokens.DIVIDE;
        }
        char ch = charAt();
        if (ch == '/') {
            length++;
            while (offset + length < bufferLen && charAt() != '\n') {
                length++;
            }
            return YuTokens.COMMENT;
        } else if (ch == '.') {
            length++;
            char pre, curr = '?';
            boolean breakFromLoop = false;
            while (offset + length < bufferLen) {
                pre = curr;
                curr = charAt();
                if (curr == '/' && pre == '.') {
                    length++;
                    breakFromLoop = true;
                    break;
                }
                length++;
            }
            if (!breakFromLoop) {
               bad();
            }
            return YuTokens.COMMENT;
        } else {
            return YuTokens.DIVIDE;
        }
    }

    protected YuTokens scanOperatorTwo(char expected, YuTokens ifWrong, YuTokens ifRight) {
        if(offset + length == bufferLen) {
        	return ifWrong;
        }
        if(charAt() == expected) {
        	length++;
        	return ifRight;
        }
    	return ifWrong;
    }

    public void reset(String src) {
        if(src == null) {
            throw new IllegalArgumentException();
        }
        this.source = src;
        line = 0;
        column = 0;
        length = 0;
        index = 0;
        offset = 0;
        currToken = YuTokens.WHITESPACE;
        bufferLen = src.length();
    }

    protected static void doStaticInit() {
        keywords = new TrieTree<>();
        keywords.put("s", YuTokens.VARIABLE_PREFIX);
        keywords.put("ss", YuTokens.VARIABLE_PREFIX);
        keywords.put("sss", YuTokens.VARIABLE_PREFIX);
        keywords.put("endcode", YuTokens.ENDCODE);
        keywords.put("break", YuTokens.BREAK);
        keywords.put("w", YuTokens.WHILE);
        keywords.put("f", YuTokens.IF);
        keywords.put("else", YuTokens.ELSE);
        keywords.put("for", YuTokens.FOR);
        keywords.put("null", YuTokens.NULL);
        keywords.put("true", YuTokens.TRUE);
        keywords.put("false", YuTokens.FALSE);
        keywords.put("end", YuTokens.END);
        keywords.put("fn", YuTokens.FUNCTION);
        MyCharacter.initMap();
    }

    protected static boolean isDigit(char c) {
        return ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f'));
    }

    protected static boolean isPrimeDigit(char c) {
        return (c >= '0' && c <= '9');
    }

    protected static boolean isWhitespace(char c) {
        return (c == '\t' || c == ' ' || c == '\f' || c == '\n' || c == '\r');
    }
}
