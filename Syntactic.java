package ADT;
import java.util.Arrays;


public class Syntactic {
    private String filein; // The full file path to input file
    private SymbolTable symbolList; // Symbol table storing ident/const
    private Lexical lex; // Lexical analyzer
    private Lexical.token token; // Next Token retrieved
    private boolean traceon; // Controls tracing mode
    private int level = 0; // Controls indent for trace mode
    private boolean anyErrors; // Set TRUE if an error happens
    private final int symbolSize = 250;
    private int priv = -1;
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

    // Non Terminal PROGRAM is fully implemented here.
    private int Program() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
//        symbolList.consoleout();
        trace("Program", true);
        lex.mnemonics.consoleOut();
        if (token.code == lex.codeFor("UNIT")) {
            token = lex.GetNextToken();
            recur = ProgIdentifier();
            if (token.code == lex.codeFor("SEMIC")) {
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
//            	System.out.println("Here");
                error(lex.reserveFor("SEMIC"), token.lexeme);
            }
        } else {
            error(lex.reserveFor("UNIT"), token.lexeme);
        }
        trace("Program", false);
        return recur;
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


    // Non Terminal BLOCK is fully implemented here.
    private int Block() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("Block", true);

        // Parse variable declaration section
        while (token.code == lex.codeFor("VAR")) {
        	token=lex.GetNextToken();
            VariableDecSec();
        	System.out.println("PARSED VAR");
//        	token=lex.GetNextToken();
        }

        // Parse block body
        if (token.code == lex.codeFor("BEGIN")) {
        	System.out.println("Am I here in Begin? ");
            token = lex.GetNextToken(); // Move past BEGIN
            recur = Statement();
            while (token.code == lex.codeFor("SEMIC") && !lex.EOF() && !anyErrors) {
                token = lex.GetNextToken(); // Move past SEMICOLON
                recur = Statement();
            }
            if (token.code == lex.codeFor("END")) {
                token = lex.GetNextToken(); // Move past END
            } else {
            	System.out.println("Am I here? ");
                error(lex.reserveFor("END"), token.lexeme);
            }
        } else {
            error(lex.reserveFor("BEGIN"), token.lexeme);
        }

        trace("Block", false);
        return recur;
    }
    
    //Variable Declaration Section
    private void VariableDecSec() {
        trace("VariableDecSec", true);

        // Parse variable declarations
        while (true) {
            // Parse identifiers
        	System.out.println(token.code + " "+ lex.codeFor("IDENT"));
            while (token.code == lex.codeFor("IDENT")) {
                // Process the identifier
            	System.out.println("I am here");
                token = lex.GetNextToken(); // Move past IDENT

                // Check for comma or colon
                if (token.code == lex.codeFor("COMMA")) {
                    token = lex.GetNextToken(); // Move past COMMA
                } else if (token.code == lex.codeFor("COLON")) {
                    token = lex.GetNextToken(); // Move past COLON
                    // Parse simple type
                    SimpleType();

                    // Check for semicolon
                    if (token.code == lex.codeFor("SEMIC")) {
                        token = lex.GetNextToken(); // Move past SEMICOLON
                    } else {
                        error(lex.reserveFor("SEMIC"), token.lexeme);
                        return;
                    }
                    break; // Exit the inner loop to handle the next variable declaration
                } else {
                    error("Expected comma or colon in variable declaration", token.lexeme);
                    return;
                }
            }

            // Check for the end of variable declaration section
            if (token.code != lex.codeFor("IDENT")) {
                break;
            }
        }

        trace("VariableDecSec", false);
    }
    private void SimpleType() {
        trace("SimpleType", true);

        // Check the current token against the possible simple type tokens
        if (token.code == lex.codeFor("INTEG") ||
            token.code == lex.codeFor("FLOAT") ||
            token.code == lex.codeFor("STRIN")) {
            
            // Move to the next token
            token = lex.GetNextToken();
        } else {
            // Report an error if the current token does not match any simple type
            error("Expected INTEGER, FLOAT, or STRING as simple type", token.lexeme);
        }

        trace("SimpleType", false);
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
//        System.out.println(token.code);
//    	System.out.println(lex.codeFor("AS"));
        
        if (token.code == lex.codeFor("AS")) {
            token = lex.GetNextToken();
            recur = SimpleExpression();
            
        }else if(token.code==lex.codeFor("IDENT")) {
        	recur = SimpleExpression();
        } else if (token.code == lex.codeFor("ADDIT") || token.code == lex.codeFor("SUBTR")) {
            token = lex.GetNextToken();
            if(token.code ==lex.codeFor("NCINT")){
            	token=lex.GetNextToken();
            	return recur;
            }// Move past the operator
        }
        else {
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
//    private int SimpleExpression() {
//        int recur = 0;
//        if (anyErrors) {
//            return -1;
//        }
//        trace("SimpleExpression", true);
//        if (token.code == lex.codeFor("IDENT")) {
//            token = lex.GetNextToken() ;
//            return recur;
//        }
//        System.out.println(token.code+" "+lex.codeFor("ADDIT"));
//        if (token.code == lex.codeFor("ADDIT") || token.code == lex.codeFor("SUBTR")) {
//        	System.out.println("Hello Hello Can you hear me voice insude mt head");
//            token = lex.GetNextToken();
//        }if(token.code ==lex.codeFor("END")) {
//        	return recur;
//        }
//
//        // Parse the term
//        recur = Term();
//
//        // Zero or more occurrences of addop and term
//        while (token.code == lex.codeFor("ADDIT") || token.code == lex.codeFor("SUBTR")) {
//            token = lex.GetNextToken(); // Move past the addop
//            recur = Term(); // Parse the following term
//        }
//        trace("SimpleExpression", false);
//        return recur;
//    }
    
//    private int SimpleExpression() {
//        int recur = 0;
//        if (anyErrors) {
//            return -1;
//        }
//        trace("SimpleExpression", true);
//        
//        // Check if the current token is an identifier
//        if (token.code == lex.codeFor("IDENT")) {
//            token = lex.GetNextToken(); // Move past the identifier
//        } else {
//            // Print an error message if an identifier is expected
//            error("Identifier", token.lexeme);
//            return -1;
//        }
//        
//        // Check for the addition or subtraction operators
//        if (token.code == lex.codeFor("ADDIT") || token.code == lex.codeFor("SUBTR")) {
//            token = lex.GetNextToken(); // Move past the operator
//        }
//        
//        // Parse the term
//        recur = Term();
//        
//        // Zero or more occurrences of addop and term
//        while (token.code == lex.codeFor("ADDIT") || token.code == lex.codeFor("SUBTR")) {
//            token = lex.GetNextToken(); // Move past the addop
//            recur = Term(); // Parse the following term
//        }
//        
//        trace("SimpleExpression", false);
//        return recur;
//    }
    private int SimpleExpression() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("SimpleExpression", true);
        
        // Check if the current token is an identifier or a constant value
        System.out.println(token.code);
        if (token.code == lex.codeFor("IDENT") || token.code == lex.codeFor("NCINT") || token.code == lex.codeFor("NCFLO") || token.code == lex.codeFor("STRIN")) {
            token = lex.GetNextToken();
            // Move past the identifier or constant value
        } else {
            // Print an error message if an identifier or constant value is expected
            error("Identifier or constant value", token.lexeme);
            return -1;
        }
        
        // Check for the addition or subtraction operators
        if (token.code == lex.codeFor("ADDIT") || token.code == lex.codeFor("SUBTR")) {
            token = lex.GetNextToken(); // Move past the operator
        }
         if(token.code==lex.codeFor("TO")) {
        	 token=lex.GetNextToken();
         }if (token.code == lex.codeFor("DO")) {
         	token=lex.GetNextToken();
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
//    private int Statement() {
//        int recur = 0;
//        if (anyErrors) {
//            return -1;
//        }
//        trace("Statement", true);
//        if (token.code == lex.codeFor("IDENT")) { // must be an ASSIGNMENT
//            recur = handleAssignment();
//        } else {
//            if (token.code == lex.codeFor("IF")) { // must be an IF
//                // this would handle the rest of the IF statement IN PART B
//            } else
//            // if/elses should look for the other possible statement starts...
//            // but not until PART B
//            {
//                error("Statement start", token.lexeme);
//            }
//        }
//        trace("Statement", false);
//        return recur;
//    }
    private int Statement() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
       
        trace("Statement", true);
        System.out.println(token.code + " "+lex.codeFor("READL"));
        while (token.code == lex.codeFor("PARAO") || token.code == lex.codeFor("IF") ||
               token.code == lex.codeFor("WHILE") || token.code == lex.codeFor("REPEA") ||
               token.code == lex.codeFor("FOR") || token.code == lex.codeFor("WRITE") ||
               token.code == lex.codeFor("READL") || token.code == lex.codeFor("IDENT")) {
        	System.out.println("In the while loop as expected");
//        	if(token.code==lex.codeFor("IDENT")) {
//        		token=lex.GetNextToken();
//        	}
            if (token.code == lex.codeFor("PARAO")) { // Block body
                recur = Block();
            } else if (token.code == lex.codeFor("IF")) { // IF statement
                // Implement parsing for IF statement
            } else if (token.code == lex.codeFor("WHILE")) { // WHILE statement
                // Implement parsing for WHILE statement
            } else if (token.code == lex.codeFor("REPEA")) { // REPEAT statement
                token=lex.GetNextToken();
                recur=Statement();
                if(token.code==lex.codeFor("UNTIL")) {
                	token=lex.GetNextToken();
                }recur=SimpleExpression();
            	// Implement parsing for REPEAT statement
            }else if(token.code==lex.codeFor("UNTIL")) {
            	token=lex.GetNextToken();
            } 
            else if (token.code == lex.codeFor("FOR")) { // FOR statement
                // Implement parsing for FOR statement
            	System.out.println("I am in FOR");
            	token = lex.GetNextToken(); // Move past FOR
                if (token.code == lex.codeFor("IDENT")) {
                    // Parse variable
                    token = lex.GetNextToken(); // Move past variable
                    if (token.code == lex.codeFor("AS")) {
                        token = lex.GetNextToken(); // Move past AS
                        // Parse initial expression
                        recur = SimpleExpression(); // Assuming SimpleExpression is correctly implemented
                        System.out.println(token.code+":"+lex.codeFor("TO"));
                        if (token.code == lex.codeFor("DO")) {
                            token = lex.GetNextToken(); // Move past DO
//                            System.out.println("Am I here? ");
                            // Parse statement
                            recur = Block();
                            return recur;// Assuming Statement is correctly implemented
                        }
                        if (token.code == lex.codeFor("TO")) {
                            token = lex.GetNextToken(); // Move past TO
                            // Parse final expression
                            recur = SimpleExpression();
                            if(token.code == lex.codeFor("IDENT")){
                            	token=lex.GetNextToken();
                            }// Assuming SimpleExpression is correctly implemented
                            if (token.code == lex.codeFor("DO")) {
                                token = lex.GetNextToken(); // Move past DO
//                                System.out.println("Am I here? ");
                                // Parse statement
                                recur = Block();
                                return recur;// Assuming Statement is correctly implemented
                            } else {
                                error(lex.reserveFor("DO"), token.lexeme);
                            }
                        } else {
                        	System.out.println("Is it here ?");
                            error(lex.reserveFor("TO"), token.lexeme);
                        }
                    } else {
                        error(lex.reserveFor("AS"), token.lexeme);
                    }
                } else {
                    error("Identifier", token.lexeme);
                }
            } else if (token.code == lex.codeFor("WRITELN")) { // WRITELN statement
                // Implement parsing for WRITELN statement
            } else if (token.code == lex.codeFor("READL")) { // READLN statement
                // Implement parsing for READLN statement
            	System.out.println("I ma in READLN");
            	token = lex.GetNextToken(); // Move past READLN
                if (token.code == lex.codeFor("PARAO")) {
                    token = lex.GetNextToken(); // Move past LPAR
                    System.out.println("I am in PARAO");
                    if (token.code == lex.codeFor("IDENT")) {
                        // Process identifier
                        token = lex.GetNextToken(); // Move past identifier
                        if (token.code == lex.codeFor("PARAC")) {
                            // Successfully parsed READLN statement
                            token = lex.GetNextToken(); // Move past RPAR
                        } else {
                            error(lex.reserveFor("PARC"), token.lexeme);
                            break;
                        }
                    } else {
                        error("Identifier", token.lexeme);
                        break;
                    }
                } else {
                    error(lex.reserveFor("PARAO"), token.lexeme);
                    break;
                }
            } 
            
            else if (token.code == lex.codeFor("IDENT")) {
            	System.out.println("Why am I here in the handle assigment");// Variable assignment
                recur = handleAssignment();
//                recur = SimpleExpression();
            }

            // Check for more statements
            if (token.code == lex.codeFor("SEMIC")) {
                token = lex.GetNextToken(); // Move past SEMICOLON
            } else {
                break; // Exit the loop if no SEMICOLON found
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
        if (token.code == lex.codeFor("FLOAT") || token.code == lex.codeFor("INTEG") || token.code ==lex.codeFor("NCFLO") ||token.code==lex.codeFor("NCINT") || token.code == lex.codeFor("MULTI") || token.code == lex.codeFor("DIVID")) {
        	token=lex.GetNextToken();
//            System.out.println("this is the code I am looking for" + token.lexeme + "But you have"+lex.codeFor("MULTI"));
        }else if (token.code == lex.codeFor("IDENT")) {
        	priv=token.code;
            token = lex.GetNextToken();
        } else if (token.code == lex.codeFor("PARAO")) {
        	priv=token.code;
            token = lex.GetNextToken();
            recur = SimpleExpression();
            if (token.code == lex.codeFor("PARAC")) {
            	priv=token.code;
                token = lex.GetNextToken();
            } else {
                error("Right parenthesis", token.lexeme);
            }
        } else if (token.code == lex.codeFor("SEMIC")) {
        	priv=token.code;
            token = lex.GetNextToken();
            recur = Statement();
        }else if (token.code == lex.codeFor("TO")) {
        	token=lex.GetNextToken();
        }else if (token.code == lex.codeFor("DO")) {
        	token=lex.GetNextToken();
        }else if (token.code ==lex.codeFor("AS")) {
        	token=lex.GetNextToken();
        	recur=handleAssignment();
        	return recur;
        }
        else {
            error("Factor", token.lexeme);
        }
        trace("Factor", false);
        return recur;
    }
}