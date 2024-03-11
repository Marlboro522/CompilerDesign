package ADT;

import java.io.*;

public class Lexical {
    private File file;
    private FileReader fileReader;
    private BufferedReader bufferedReader;
    private String line;
    private int linePos;
    private SymbolTable saveSymbols;
    private boolean EOF;
    private boolean echo;
    private boolean printToken;
    private int lineCount;
    private boolean needLine;
    private final int sizeReserveTable = 50;
    private ReserveTable reserveWords = new ReserveTable(sizeReserveTable);
    private ReserveTable mnemonics = new ReserveTable(sizeReserveTable);
    private char currCh;

    public Lexical(String filename, SymbolTable symbols, boolean echoOn) {
        saveSymbols = symbols;
        echo = echoOn;
        lineCount = 0;
        line = "";
        needLine = true;
        printToken = false;
        linePos = -1;
        initReserveWords(reserveWords);
        initMnemonics(mnemonics);

        try {
            file = new File(filename);
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            EOF = false;
            currCh = GetNextChar();
        } catch (IOException e) {
            EOF = true;
            e.printStackTrace();
        }
    }

    public int codeFor(String mnemonic) {
        return mnemonics.LookupName(mnemonic);
    }

    public String reserveFor(String mnemonic) {
        return reserveWords.LookupCode(mnemonics.LookupName(mnemonic));
    }

    public boolean EOF() {
        return EOF;
    }

    public void setPrintToken(boolean on) {
        printToken = on;
    }

    private void initReserveWords(ReserveTable reserveWords) {
        // Initialize reserve words table
        // Example:
        // reserveWords.Add("BEGIN", 11);
    	String[] reserves = {"GOTO", "INTEGER", "TO", "DO", "IF", "THEN", "ELSE", "FOR", "OF", "WRITELN", "READLN", "BEGIN", "END", "VAR", "WHILE", "UNIT", "LABEL", "REPEAT", "UNTIL", "PROCEDURE", "DOWNTO", "FUNCTION", "RETURN", "REAL", "STRING", "ARRAY", "/", "*", "+", "-", "(", ")", ";", ":=", ">", "<", ">=", "<=", "=", "<>", ",", "[", "]", ":", "."};
    	for (int i=0;i<=reserves.length-1;i++) {
    		reserveWords.Add(reserves[i], i);
    	}reserveWords.Add("IDENTIFIER",50);
    	reserveWords.Add("NCINT", 51);
    	reserveWords.Add("NCFLO", 52);
    	reserveWords.Add("STRCO", 53);
    }

    private void initMnemonics(ReserveTable mnemonics) {
        // Initialize token mnemonics table
        // Example:
        // mnemonics.Add("ARRAY", 25);
    	String[] reserves = {"GOTO", "INTEGER", "TO", "DO", "IF", "THEN", "ELSE", "FOR", "OF", "WRITELN", "READLN", "BEGIN", "END", "VAR", "WHILE", "UNIT", "LABEL", "REPEAT", "UNTIL", "PROCEDURE", "DOWNTO", "FUNCTION", "RETURN", "REAL", "STRING", "ARRAY", "DIVIDE", "MULTIPLY", "ADDITION", "SUBTRACTION", "PARAO", "PARAC", "SEMIC", "AS", "GREAT", "LESST", "GREAE", "LESSE", "EQUAL", "NEQUA", "COMMA", "BRACO", "BRACC", "COLON", "DOT"};
        for (int i = 0; i < reserves.length; i++) {
            reserves[i] = reserves[i].substring(0, Math.min(reserves[i].length(), 5)); // Limiting to 5 characters
            mnemonics.Add(reserves[i],i);
        }mnemonics.Add("IDENT", 50);
        mnemonics.Add("NCINT", 51);
    	mnemonics.Add("NCFLO", 52);
    	mnemonics.Add("STRCO", 53);
    }

    private void consoleShowError(String message) {
        System.out.println("**** ERROR FOUND: " + message);
    }

    private boolean isLetter(char ch) {
        return (((ch >= 'A') && (ch <= 'Z')) || ((ch >= 'a') && (ch <= 'z')));
    }

    private boolean isDigit(char ch) {
        return ((ch >= '0') && (ch <= '9'));
    }

    private boolean isWhitespace(char ch) {
        return ((ch == ' ') || (ch == '\t') || (ch == '\n'));
    }

    private char PeekNextChar() {
        char result = ' ';
        if ((needLine) || (EOF)) {
            result = ' ';
        } else {
            if ((linePos + 1) < line.length()) {
                result = line.charAt(linePos + 1);
            }
        }
        return result;
    }

    private void GetNextLine() {
        try {
            line = bufferedReader.readLine();
            if ((line != null) && (echo)) {
                lineCount++;
                System.out.println(String.format("%04d", lineCount) + " " + line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (line == null) {
            EOF = true;
        }
        linePos = -1;
        needLine = false;
    }

    public char GetNextChar() {
        char result;
        if (needLine) {
            GetNextLine();
        }
        if (EOF) {
            result = '\n';
            needLine = false;
        } else {
            if ((linePos < line.length() - 1)) {
                linePos++;
                result = line.charAt(linePos);
            } else {
                result = '\n';
                needLine = true;
            }
        }
        return result;
    }

    public char skipComment(char curr) {
        // Implement skipping comments
    	final char commentStart_1 = '{';
    	final char commentEnd_1 = '}';
    	final char commentStart_2 = '(';
    	final char commentPairChar = '*';
    	final char commentEnd_2 = ')';
    	if (curr == commentStart_1) { // Check for the { style comment
            curr = GetNextChar();
            while ((curr != commentEnd_1) && (!EOF)) {
                if (curr == '\n') {
                    lineCount++;
                    if (echo) {
                        System.out.println(String.format("%04d", lineCount) + " " + line);
                    }
                }
                curr = GetNextChar();
            }
            if (EOF) {
                consoleShowError("Unterminated comment found.");
            } else {
                curr = GetNextChar(); // skip over the closing }
            }
        } else if (curr == commentStart_2 && PeekNextChar() == commentPairChar) { // Check for the (* style comment
            curr = GetNextChar(); // skip the second character of the comment start delimiter
            curr = GetNextChar(); // move to the first character inside the comment
            while (!((curr == commentPairChar) && (PeekNextChar() == commentEnd_2)) && !EOF) {
                if (curr == '\n') {
                    lineCount++;
                    if (echo) {
                        System.out.println(String.format("%04d", lineCount) + " " + line);
                    }
                }
                curr = GetNextChar();
            }
            if (EOF) {
                consoleShowError("Unterminated comment found.");
            } else {
                curr = GetNextChar();
                curr = GetNextChar(); 
            }
        }
        return (curr);
    }

    public char skipWhiteSpace() {
        do {
            while ((isWhitespace(currCh)) && (!EOF)) {
                currCh = GetNextChar();
            }
            currCh = skipComment(currCh);
        } while (isWhitespace(currCh) && (!EOF));
        return currCh;
    }

//    private boolean isPrefix(char ch) {
//        return ((ch == ':') || (ch == '<') || (ch == '>'));
//    }

    private boolean isStringStart(char ch) {
        return ch == '"';
    }

    private token getIdentifier() {
        // Implement getting identifier token
//    	token result = new token() {
//    		while
//    	}
//        return dummyGet();
    	StringBuilder identifier = new StringBuilder();
        while ((isLetter(currCh) || isDigit(currCh) || currCh == '_' || currCh == '$') && identifier.length() < 30) {
            identifier.append(currCh);
            currCh = GetNextChar();
        }
        // Check if the identifier exceeds 30 characters
        if (identifier.length() == 30 && currCh != '\n') {
            consoleShowError("Identifier exceeds 30 characters and will be truncated");
            // Consume remaining characters of the identifier to avoid breaking across line boundaries
            while (isLetter(currCh) || isDigit(currCh) || currCh == '_' || currCh == '$') {
                currCh = GetNextChar();
            }
        }
        // Truncate the identifier if it's longer than 30 characters
        if (identifier.length() > 30) {
            identifier.setLength(30);
        }
        // Create token object
        token result = new token();
        result.lexeme = identifier.toString();
        // Check if the identifier is a reserved word
        int tokenCode = mnemonics.LookupName(result.lexeme.toUpperCase());
        if (tokenCode != -1) {
            // It's a reserved word, so set the token code accordingly
            result.code = tokenCode;
            result.mnemonic = result.lexeme.toUpperCase();
        } else {
            // It's not a reserved word, so assign the IDENTIFIER token code
            result.code = mnemonics.LookupName("IDENT");
            result.mnemonic = "IDENT";
        }
        return result;
    }

    private token getNumber() {
//        // Implement getting number token
//        return dummyGet();
    	StringBuilder number = new StringBuilder();
        boolean isFloat = false;
        // Read the integer part
        while (isDigit(currCh) && number.length() < 30) {
            number.append(currCh);
            currCh = GetNextChar();
        }
        // Check if there is a decimal point
        if (currCh == '.') {
            isFloat = true;
            number.append(currCh);
            currCh = GetNextChar();
            // Read the fractional part
            while (isDigit(currCh) && number.length() < 30) {
                number.append(currCh);
                currCh = GetNextChar();
            }
        }
        // Check for exponential notation
        if (currCh == 'E' && isFloat) {
            number.append(currCh);
            currCh = GetNextChar();
            // Check for optional sign
            if (currCh == '+' || currCh == '-') {
                number.append(currCh);
                currCh = GetNextChar();
            }
            // Read the digits after 'E'
            while (isDigit(currCh) && number.length() < 30) {
                number.append(currCh);
                currCh = GetNextChar();
            }
        }
        // Create token object
        token result = new token();
        result.lexeme = number.toString();
        // Assign token code based on whether it's a float or an integer
        result.code = isFloat ? mnemonics.LookupName("NCFLO") : mnemonics.LookupName("NCINT");
        result.mnemonic = isFloat ? "NCFLO" : "NCINT";
        return result;
    }

    private token getString() {
//        // Implement getting string token
//        return dummyGet();
    	StringBuilder stringLiteral = new StringBuilder();
        // Skip the starting double-quote
        currCh = GetNextChar();
        // Read characters until the terminating double-quote or the end of line
        while (currCh != '"' && currCh != '\n' && !EOF) {
            stringLiteral.append(currCh);
            currCh = GetNextChar();
        }
        // Check if the string ended prematurely
        if (currCh != '"') {
            consoleShowError("Unterminated string found.");
        }
        // Create token object
        token result = new token();
        result.lexeme = stringLiteral.toString();
        result.code = reserveWords.LookupName("STRCO");
        result.mnemonic = "STRCO";
        return result;
    }

    private token getOtherToken() {
//        // Implement getting other token
//        return dummyGet();
    	char ch = currCh;
        token result = new token();
        switch (ch) {
            case '+':
                result.lexeme = "+";
                result.code = reserveWords.LookupName("ADDIT");
                result.mnemonic = "ADDIT";
                break;
            case '-':
                result.lexeme = "-";
                result.code = reserveWords.LookupName("SUBTR");
                result.mnemonic = "SUBTR";
                break;
            case '(':
                result.lexeme = "(";
                result.code = reserveWords.LookupName("PARAO");
                result.mnemonic = "PARAO";
                break;
            case ')':
                result.lexeme = ")";
                result.code = reserveWords.LookupName("PARAC");
                result.mnemonic = "PARAC";
                break;
            case ';':
                result.lexeme = ";";
                result.code = reserveWords.LookupName("SEMIC");
                result.mnemonic = "SEMIC";
                break;
            case ':':
                if (PeekNextChar() == '=') {
                    result.lexeme = ":=";
                    result.code = reserveWords.LookupName(":=");
                    result.mnemonic = ":=";
                    currCh = GetNextChar(); // Move past '='
                } else {
                    result.lexeme = ":";
                    result.code = reserveWords.LookupName("COLON");
                    result.mnemonic = "COLON";
                }
                break;
            case '>':
                if (PeekNextChar() == '=') {
                    result.lexeme = ">=";
                    result.code = reserveWords.LookupName(">=");
                    result.mnemonic = ">=";
                    currCh = GetNextChar(); // Move past '='
                } else {
                    result.lexeme = ">";
                    result.code = reserveWords.LookupName("GREAT");
                    result.mnemonic = "GREAT";
                }
                break;
            case '<':
                if (PeekNextChar() == '=') {
                    result.lexeme = "<=";
                    result.code = reserveWords.LookupName("<=");
                    result.mnemonic = "<=";
                    currCh = GetNextChar(); // Move past '='
                } else if (PeekNextChar() == '>') {
                    result.lexeme = "<>";
                    result.code = reserveWords.LookupName("<>");
                    result.mnemonic = "<>";
                    currCh = GetNextChar(); // Move past '>'
                } else {
                    result.lexeme = "<";
                    result.code = mnemonics.LookupName("LESST");
                    result.mnemonic = "LESST";
                }
                break;
            case '=':
                result.lexeme = "=";
                result.code = mnemonics.LookupName("EQUAL");
                result.mnemonic = "EQUAL";
                break;
            case ',':
                result.lexeme = ",";
                result.code = mnemonics.LookupName("COMMA");
                result.mnemonic = "COMMA";
                break;
            case '[':
                result.lexeme = "[";
                result.code = mnemonics.LookupName("BRACO");
                result.mnemonic = "BRACO";
                break;
            case ']':
                result.lexeme = "]";
                result.code = mnemonics.LookupName("BRACC");
                result.mnemonic = "BRACC";
                break;
            case '.':
                result.lexeme = ".";
                result.code = mnemonics.LookupName("DOT");
                result.mnemonic = "DOT";
                break;
            default:
                // For any other character not defined elsewhere
                result.lexeme = String.valueOf(ch);
                result.code = mnemonics.LookupName("OTHER");
                result.mnemonic = "OTHER";
                break;
        }
        currCh = GetNextChar(); // Move to the next character
        return result;
    }

    public token GetNextToken() {
        token result = new token();
        currCh = skipWhiteSpace();
        if (isLetter(currCh)) {
            result = getIdentifier();
        } else if (isDigit(currCh)) {
            result = getNumber();
        } else if (isStringStart(currCh)) {
            result = getString();
        } else {
            result = getOtherToken();
        }
        if ((result.lexeme.equals("")) || (EOF)) {
            result = null;
        }
        if (result != null) {
            if (printToken) {
                System.out.println("\t" + result.mnemonic + " | \t" + String.format("%04d", result.code) + " | \t" + result.lexeme);
            }
        }
        return result;
    }
//    private void consoleShowError(String message) {
//        System.out.println("**** ERROR FOUND: " + message);
//    }

//    private token dummyGet() {
//        token result = new token();
//        result.lexeme = "" + currCh;
//        currCh = GetNextChar();
//        result.code = 0;
//        result.mnemonic = "DUMY";
//        return result;
//    }

    public class token {
        public String lexeme;
        public int code;
        public String mnemonic;

        token() {
            lexeme = "";
            code = 0;
            mnemonic = "";
        }
    }
}
