/*
HERE is code that goes in the Interpreter class
so that all the trace outputs match the intended result 

    private String makeTraceString(int pc, int opcode,int op1,int op2,int op3 ){
        String result = "";
        result = "PC = "+String.format("%04d", pc)+": "+(optable.LookupCode(opcode)+"     ").substring(0,6)+String.format("%02d",op1)+
                                ", "+String.format("%02d",op2)+", "+String.format("%02d",op3);
        return result;
    }

HERE is a free opcode table initialization for a created ReserveTable
  private void initReserve(ReserveTable optable){
      optable.Add("STOP", 0);
      optable.Add("DIV", 1);
      optable.Add("MUL", 2);
      optable.Add("SUB", 3);
      optable.Add("ADD", 4);
      optable.Add("MOV", 5);
      optable.Add("PRINT", 6);
      optable.Add("READ", 7);
      optable.Add("JMP", 8);
      optable.Add("JZ", 9);
      optable.Add("JP", 10);
      optable.Add("JN", 11);
      optable.Add("JNZ", 12);
      optable.Add("JNP", 13);
      optable.Add("JNN", 14);
      optable.Add("JINDR", 15);
 }

HERE IS THE FACTORIAL INITIALIZATION STUFF...
    public boolean initializeFactorialTest(SymbolTable stable, QuadTable qtable) {
        InitSTforFactorial(stable);
        InitQTforFactorial(qtable);
        return true;
    }

//factorial Symbols  
    public static void InitSTF(SymbolTable st) {
        st.AddSymbol("n", 'V', 10);
        st.AddSymbol("i", 'V', 0);
        //... put the rest of the Symbol table entries below...    
    }

//factorial Quads 
    public void InitQTF(QuadTable qt) {
        qt.AddQuad(5, 3, 0, 2); //MOV
        qt.AddQuad(5, 3, 0, 1); //MOV
        qt.AddQuad(3, 1, 0, 4); //SUB
        //... put the rest of the Quad table entries below...    

    }

*/
package mainHW1;
import ADT.*;

/**
 *
 * @author abrouill
 */
public class mainHW1 {

    public static void main(String[] args) {
		// Expects 6 command-line parameters for filenames, 
		//     see arg[0] through arg[5] below 
        Interpreter interp = new Interpreter();
        SymbolTable st;
        QuadTable qt;
        
        // interpretation FACTORIAL
        st = new SymbolTable(20);     //Create an empty SymbolTable
        qt = new QuadTable(20);       //Create an empty QuadTable
        System.out.println("This program expects command-line parameters for filenames in this order:");
        System.out.println("traceFactorial SymbolFactorial QuadFactorial traceSum SymbolSum QuadSum");
        interp.initializeFactorialTest(st,qt);  //Set up for FACTORIAL
        interp.InterpretQuads(qt, st, true, args[0]);
        st.PrintSymbolTable(args[1]);
        qt.PrintQuadTable(args[2]);

        // interpretation SUMMATION
        st = new SymbolTable(20);     //Create an empty SymbolTable
        qt = new QuadTable(20);       //Create an empty QuadTable
        interp.initializeSummationTest(st,qt);  //Set up for SUMMATION
        interp.InterpretQuads(qt, st, true, args[3]);
        st.PrintSymbolTable(args[4]);
        qt.PrintQuadTable(args[5]);
        }
    
}
