package ADT;

public class Syntactic2 {
    private String filein; //The full file path to input file
    private SymbolTable symbolList; //Symbol table storing ident/const
    private Lexical lex; //Lexical analyzer
    private Lexical.token token; //Next Token retrieved
    private boolean traceon; //Controls tracing mode
    private int level = 0; //Controls indent for trace mode
    private boolean anyErrors; //Set TRUE if an error happens
    private final int symbolSize = 250;

    public Syntactic2(String filename, boolean traceOn) {
        filein = filename;
        traceon = traceOn;
        symbolList = new SymbolTable(symbolSize);
        lex = new Lexical(filein, symbolList, true);
        lex.setPrintToken(traceOn);
        anyErrors = false;
    }

    //The interface to the syntax analyzer, initiates parsing
    // Uses variable RECUR to get return values throughout the non-terminal methods
    public void parse() {
        int recur = 0;
        // prime the pump to get the first token to process
        token = lex.GetNextToken();
        // call PROGRAM
        recur = Program();
    }

    private int Program() {

        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("Program",true);
        if (token.code == lex.codeFor("UNIT")) {
            token = lex.GetNextToken();
            recur = ProgIdentifier();
            if (token.code == lex.codeFor("SEMIC")) {
                token = lex.GetNextToken();
                recur = Block();
                if (token.code == lex.codeFor("DOTTT")) {
                    token = lex.GetNextToken();
                } else {
                    error("DOTTT", token.lexeme);
                }
            } else {
                error("SEMIC", token.lexeme);
            }
        } else {
            error("UNIT", token.lexeme);
        }
        trace("Program",false);
        return recur;
    }
    //Non-Terminal ProgIddentifier
    private int ProgIdentifier() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("ProgramIdentifier",true);
        if (token.code == lex.codeFor("IDENT")) {
            symbolList.UpdateSymbol(symbolList.LookupSymbol(token.lexeme), 'P', 0);
            token = lex.GetNextToken();
        }
        trace("ProgramIdentifier",false);
        return recur;
    }
    //Non-Terminal Block
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
        }
        trace("Block", false);
        return recur;
    }
    //Non-Terminal for Statement
    private int Statement() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("Statement", true);
        if (token.code == lex.codeFor("IDENT")) {
            token = lex.GetNextToken();
            if (token.code == lex.codeFor("AS")) {
                recur = handleAssignment();
            } else{
                error("AS",token.lexeme);
            }
            token=lex.GetNextToken();  
            recur=SimpleExpression();
        } else{
            error("Statement", token.lexeme);
        }
        trace("Statement", false);
        return recur;
    }

    //Non-Terminal for SimpleExpression
    private int SimpleExpression() {
    
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("SimpleExpression", true);
        //Need to check the logic flow here and also in the embedded functions here... 
        // Sign(), Term(), etc...
        //Signs, and also with the terms and shit.
        if (token.code == lex.codeFor("ADDIT") || token.code == lex.codeFor("SUBTR")
                || token.code == lex.codeFor("MULTI") || token.code == lex.codeFor("DIVID")) {
            recur = Sign();
        }
        if (token.code == lex.codeFor("FLOAT") || token.code == lex.codeFor("INTEG")) {
            recur = Term();
        }
        trace("SimpleExpression", false);
        return recur;
    }
    //Non-Terminal for handleAssignment
    private int handleAssignment() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("handleAssignment", true);
        // have ident already in order to get to here, handle as Variable
        // recur = Variable(); // Variable moves ahead, next token ready
        if (token.code == lex.codeFor("AS")) {
            token = lex.GetNextToken();
            recur = SimpleExpression();
        } else {
            error(lex.reserveFor("AS"), token.lexeme);
        }
        trace("handleAssignment", false);
        return recur;
    }

    private int Variable() {
        int recur = 0;
        if(anyErrors){
            return -1;
        }
        trace("variable", true);
        if(token.code == lex.codeFor("IDENT")){
            token = lex.GetNextToken();
        } else {
            error("IDENT", token.lexeme);
        }
        trace("variable", false);
        return recur;
    }

    private int Sign() {
        int recur = 0;
        if(anyErrors){
            return -1;
        }
        trace("Sign", true);
        if(token.code == lex.codeFor("ADDIT") || token.code == lex.codeFor("SUBTR")|| token.code == lex.codeFor("MULTI") || token.code == lex.codeFor("DIVID")){
            token = lex.GetNextToken();
        } else {
            error("ADDIT or SUBTR", token.lexeme);
        }
        trace("Sign", false);
        return recur;
    }
    
    private int Term() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("Term", true);
        if (token.code == lex.codeFor("FLOAT") || token.code == lex.codeFor("INTEG")) {
            recur = Factor();
            token = lex.GetNextToken();
            while ((token.code == lex.codeFor("ADDIT") || token.code == lex.codeFor("SUBTR")) && (!lex.EOF()) && (!anyErrors)) {
                token = lex.GetNextToken();
                recur = Factor();
            }
        } else{
            error("Term",token.lexeme);
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
            recur = Variable();
        } else if (token.code == lex.codeFor("PARAO")) {
            token = lex.GetNextToken();
            recur = SimpleExpression();
            if (token.code == lex.codeFor("PARAC")) {
                token = lex.GetNextToken();
            } else {
                error("PARAC", token.lexeme);
            }
        } else {
            error("FLOAT OR INTEGER", token.lexeme);
        }
        trace("FACTOR", false);
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
}