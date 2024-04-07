package ADT;

public class Syntactic {
    private String filein; // The full file path to input file
    private SymbolTable symbolList; // Symbol table storing ident/const
    private Lexical lex; // Lexical analyzer
    private Lexical.token token; // Next Token retrieved
    private boolean traceon; // Controls tracing mode
    private int level = 0; // Controls indent for trace mode
    private boolean anyErrors; // Set TRUE if an error happens
    private final int symbolSize = 250;

    public Syntactic(String filename, boolean traceOn) {
        filein = filename;
        traceon = traceOn;
        symbolList = new SymbolTable(symbolSize);
        lex = new Lexical(filein, symbolList, true);
        lex.setPrintToken(traceOn);
        anyErrors = false;
    }

    // The interface to the syntax analyzer, initiates parsing
    // Uses variable RECUR to get return values throughout the non-terminal methods
    public void parse() {
        int recur = 0;
        // prime the pump to get the first token to process
        token = lex.GetNextToken();
        // call PROGRAM
        recur = Program();
    }

    // Non Terminal PROGIDENTIFIER is fully implemented here, leave it as-is.
    private int ProgIdentifier() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        // This non-term is used to uniquely mark the program identifier
        if (token.code == lex.codeFor("IDENT")) {
            // Because this is the progIdentifier, it will get a 'P' type to
            // prevent re-use as a var
            symbolList.UpdateSymbol(symbolList.LookupSymbol(token.lexeme), 'P', 0);
            // move on
            token = lex.GetNextToken();
        }
        return recur;
    }

    // Non Terminal PROGRAM is fully implemented here.
    private int Program() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("Program", true);
        if (token.code == lex.codeFor("UNIT")) {
            token = lex.GetNextToken();
            recur = ProgIdentifier();
            if (token.code == lex.codeFor("SEMI")) {
                token = lex.GetNextToken();
                recur = Block();
                if (token.code == lex.codeFor("DOTTT")) {
                    if (!anyErrors) {
                        System.out.println("Success.");
                    } else {
                        System.out.println("Compilation failed.");
                    }
                } else {
                    error(lex.reserveFor("DOTTT"), token.lexeme);
                }
            } else {
                error(lex.reserveFor("SEMIC"), token.lexeme);
            }
        } else {
            error(lex.reserveFor("UNIT"), token.lexeme);
        }
        trace("Program", false);
        return recur;
    }

    // Non Terminal BLOCK is fully implemented here.
    private int Block() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("Block", true);
        if (token.code == lex.codeFor("BEGIN")) {
            token = lex.GetNextToken();
            recur = Statement();
            while ((token.code == lex.codeFor("SEMIC")) && (!lex.EOF()) && (!anyErrors)) {
                token = lex.GetNextToken();
                recur = Statement();
            }
            if (token.code == lex.codeFor("END")) {
                token = lex.GetNextToken();
            } else {
                error(lex.reserveFor("END"), token.lexeme);
            }
        } else {
            error(lex.reserveFor("BEGIN"), token.lexeme);
        }
        trace("Block", false);
        return recur;
    }

    // Not a Non Terminal, but used to shorten Statement code body for readability.
    // <variable> $COLON-EQUALS <simple expression>
    private int handleAssignment() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("handleAssignment", true);
        // have ident already in order to get to here, handle as Variable
        recur = Variable(); // Variable moves ahead, next token ready
        if (token.code == lex.codeFor("AS")) {
            token = lex.GetNextToken();
            recur = SimpleExpression();
        } else {
            error(lex.reserveFor("AS"), token.lexeme);
        }
        trace("handleAssignment", false);
        return recur;
    }

    // Non Terminal This is dummied in to only work for an identifier.
    // It will work with the SyntaxAMiniTest file having ASSIGNMENT statements
    // containing only IDENTIFIERS. TERM and FACTOR and numbers will be
    // needed to complete Part A.
    // SimpleExpression MUST BE
    // COMPLETED TO IMPLEMENT CFG for <simple expression>
    private int SimpleExpression() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("SimpleExpression", true);
        if (token.code == lex.codeFor("IDENT")) {
            token = lex.GetNextToken();
        }
        if (token.code == lex.codeFor("ADDIT") || token.code == lex.codeFor("SUBTR")) {
            token = lex.GetNextToken();
        }

        // Parse the term
        recur = Term();

        // Zero or more occurrences of addop and term
        while (token.code == lex.codeFor("ADDIT") || token.code == lex.codeFor("SUBTR")) {
            token = lex.GetNextToken(); // Move past the addop
            recur = Term(); // Parse the following term
        }
        trace("SimpleExpression", false);
        return recur;
    }

    // Eventually this will handle all possible statement starts in
    // a nested if/else or switch structure. Only ASSIGNMENT is implemented now.
    private int Statement() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("Statement", true);
        if (token.code == lex.codeFor("IDENT")) { // must be an ASSIGNMENT
            recur = handleAssignment();
        } else {
            if (token.code == lex.codeFor("IF")) { // must be an IF
                // this would handle the rest of the IF statement IN PART B
            } else
            // if/elses should look for the other possible statement starts...
            // but not until PART B
            {
                error("Statement start", token.lexeme);
            }
        }
        trace("Statement", false);
        return recur;
    }

    // Non-terminal VARIABLE just looks for an IDENTIFIER. Later, a
    // type-check can verify compatible math ops, or if casting is required.
    private int Variable() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("Variable", true);
        if ((token.code == lex.codeFor("IDENT"))) {
            // bookkeeping and move on
            token = lex.GetNextToken();
        } else
            error("Variable", token.lexeme);
        trace("Variable", false);
        return recur;
    }

    /**
     * *************************************************
     */
    /* UTILITY FUNCTIONS USED THROUGHOUT THIS CLASS */
    // error provides a simple way to print an error statement to standard output
    // and avoid redundancy
    private void error(String wanted, String got) {
        anyErrors = true;
        System.out.println("ERROR: Expected " + wanted + " but found " + got);
    }

    // trace simply RETURNs if traceon is false; otherwise, it prints an
    // ENTERING or EXITING message using the proc string
    private void trace(String proc, boolean enter) {
        String tabs = "";
        if (!traceon) {
            return;
        }
        if (enter) {
            tabs = repeatChar(" ", level);
            System.out.print(tabs);
            System.out.println("--> Entering " + proc);
            level++;
        } else {
            if (level > 0) {
                level--;
            }
            tabs = repeatChar(" ", level);
            System.out.print(tabs);
            System.out.println("<-- Exiting " + proc);
        }
    }

    // repeatChar returns a string containing x repetitions of string s;
    // nice for making a varying indent format
    private String repeatChar(String s, int x) {
        int i;
        String result = "";
        for (i = 1; i <= x; i++) {
            result = result + s;
        }
        return result;
    }

    private int Term() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("Term", true);
        recur = Factor();
        while (token.code == lex.codeFor("MULTI") || token.code == lex.codeFor("DIVID")) {
            token = lex.GetNextToken();
            recur = Factor();
        }
        trace("Term", false);
        return recur;
    }

    private int Factor() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("Factor", true);
        if (token.code == lex.codeFor("FLOAT") || token.code == lex.codeFor("INTEG")) {
            token = lex.GetNextToken();
        } else if (token.code == lex.codeFor("IDENT")) {
            token = lex.GetNextToken();
        } else if (token.code == lex.codeFor("PARAO")) {
            token = lex.GetNextToken();
            recur = SimpleExpression();
            if (token.code == lex.codeFor("PARAC")) {
                token = lex.GetNextToken();
            } else {
                error("Right parenthesis", token.lexeme);
            }
        } else {
            error("Factor", token.lexeme);
        }
        trace("Factor", false);
        return recur;
    }
}
