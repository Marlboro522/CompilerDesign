//Raja Kantheti
// Spring-2024
package ADT;

import java.io.*;
import java.util.*;

public class Interpreter {
    private ReserveTable reserveTable;
    public static final int MAX_VALUE = 16;

    public Interpreter() {
        // Initialize the ReserveTable and add opcode mappings
        this.reserveTable = new ReserveTable(MAX_VALUE); // Assuming a maximum of 100 opcode mappings
        InitializeReserveTable(reserveTable);
    }

    public boolean initializeFactorialTest(SymbolTable stable, QuadTable qtable) {
        //Initializes the symbol table and the quad table using the below funcitons, 
        InitSTF(stable);
        InitQTF(qtable);
        return true; // Return true if initialization is successful, false otherwise
    }

    // factorial Symbols
    public static void InitSTF(SymbolTable st) {
        st.AddSymbol("n", 'V', 10);
        st.AddSymbol("i", 'V', 0);
        st.AddSymbol("product", 'V', 0);
        st.AddSymbol("1", 'C', 1);
        st.AddSymbol("$temp", 'V', 0);
        //... put the rest of the Symbol table entries below...
    }

    // factorial Quads
    public void InitQTF(QuadTable qt) {
        qt.AddQuad(5, 3, 0, 2); // MOV
        qt.AddQuad(5, 3, 0, 1); // MOV
        qt.AddQuad(3, 1, 0, 4); // SUB
        qt.AddQuad(10, 4, 0, 7); // jp
        qt.AddQuad(2, 2, 1, 2); // MUL
        qt.AddQuad(4, 1, 3, 1); // ADD
        qt.AddQuad(8, 0, 0, 2); // JUMP
        qt.AddQuad(6, 2, 0, 0); // PRINT
        qt.AddQuad(0, 0, 0, 0); //stap
        //... put the rest of the Quad table entries below...

    }
    
    public boolean initializeSummationTest(SymbolTable stable, QuadTable qtable) {
    	//Initializes the symbol table and the quad table using the below funcitons,
        InitSTS(stable);
        InitQTS(qtable);
        return true;
    }
    //factorial symabols
    public static void InitSTS(SymbolTable st) {
        st.AddSymbol("n", 'V', 10);
        st.AddSymbol("i", 'V', 0);
        st.AddSymbol("sum", 'V', 0);
        st.AddSymbol("1", 'C', 1);
        st.AddSymbol("$temp", 'V', 0);
    }
 // summation Quads
    public static void InitQTS(QuadTable qt) {
        qt.AddQuad(5, 3, 0, 2); // MOV
        qt.AddQuad(5, 3, 0, 1); // MOV
        qt.AddQuad(3, 1, 0, 4); // SUB
        qt.AddQuad(10, 4, 0, 7); // jp
        qt.AddQuad(4, 2, 1, 2); // ADD
        qt.AddQuad(4, 1, 3, 1); // ADD
        qt.AddQuad(8, 0, 0, 2); // JUMP
        qt.AddQuad(6, 2, 0, 0); // PRINT
        qt.AddQuad(0, 0, 0, 0); //stap
    }

    public void InterpretQuads(QuadTable Q, SymbolTable S, boolean TraceOn, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            int PC = 0; // Program counter

            while (PC < Q.sharenextAvailable()) {
                int[] quad = Q.GetQuad(PC);
                int opcode = quad[0];
                int op1 = quad[1];
                int op2 = quad[2];
                int op3 = quad[3];

                // Echo trace mode printout if TraceOn is true
                if (TraceOn) {
                    String traceString = makeTraceString(PC, opcode, op1, op2, op3, S);
                    System.out.println(traceString);
                    writer.write(traceString);
                    writer.newLine();
                }

                // Execute the instruction based on the opcode
                //individual implementation of each opcode in the 16 instructions sgiven
                switch (opcode) {
                    case STOP_OPCODE: // STOP
                        System.out.println("Execution terminated by program stop.");
                        return;
                    case DIV_OPCODE: // DIV
                        S.UpdateSymbol(op3, 'V', S.GetInteger(op1) / S.GetInteger(op2));
                        break;
                    case MUL_OPCODE: // MUL
                        S.UpdateSymbol(op3, 'V', S.GetInteger(op1) * S.GetInteger(op2));
                        break;
                    case SUB_OPCODE: // SUB
                        S.UpdateSymbol(op3, 'V', S.GetInteger(op1) - S.GetInteger(op2));
                        break;
                    case ADD_OPCODE: // ADD
                        S.UpdateSymbol(op3, 'V', S.GetInteger(op1) + S.GetInteger(op2));
                        break;
                    case MOV_OPCODE: // MOV
                        S.UpdateSymbol(op3, 'V', S.GetInteger(op1));
                        break;
                    case PRINT_OPCODE: // PRINT
                        writer.write(S.GetSymbol(op1)); // op3 contains the symbol index
                        writer.newLine();
                        System.out.println(S.GetInteger(op1));
                        break;
                    case READ_OPCODE: // READ
                    	System.out.print("Enter value for " + S.GetSymbol(op3) + ": ");
                        Scanner scanner = new Scanner(System.in);
                        int readValue = scanner.nextInt();
                        S.UpdateSymbol(op3, 'V', readValue);
                        break;
                    case JMP_OPCODE: // JMP
                        PC = op3; // Jump to the specified quad
                        continue; // Skip the PC increment
                    case JZ_OPCODE: // JZ
                        if (S.GetInteger(op1) == 0) {
                            PC = op3; // Jump to the specified quad
                            continue; // Skip the PC increment
                        }
                        break;
                    case JP_OPCODE: // JP
                        if (S.GetInteger(op1) > 0) {
                            PC = op3; // Jump to the specified quad
                            continue; // Skip the PC increment
                        }
                        break;
                    case JN_OPCODE: // JN
                        if (S.GetInteger(op1) < 0) {
                            PC = op3; // Jump to the specified quad
                            continue; // Skip the PC increment
                        }
                        break;
                    case JNZ_OPCODE: // JNZ
                        if (S.GetInteger(op1) != 0) {
                            PC = op3; // Jump to the specified quad
                            continue; // Skip the PC increment
                        }
                        break;
                    case JNP_OPCODE: // JNP
                        if (S.GetInteger(op1) >= 0) {
                            PC = op3; // Jump to the specified quad
                            continue; // Skip the PC increment
                        }
                        break;
                    case JNN_OPCODE: // JNN
                        if (S.GetInteger(op1) <= 0) {
                            PC = op3; // Jump to the specified quad
                            continue; // Skip the PC increment
                        }
                        break;
                    case JINDR_OPCODE: // JINDR
                        // Implementation for JINDR opcode
                        break;
                    default:
                        System.err.println("Invalid opcode: " + opcode);
                        break;
                }

                PC++; // Move to the next quad
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //method to make the trace, impleemtned seperately to incerawse readability
    private String makeTraceString(int pc, int opcode, int op1, int op2, int op3, SymbolTable S) {
        String mnemonic = getMnemonic(opcode);
        String operand1 = S.GetSymbol(op1);
        String operand2 = S.GetSymbol(op2);
        String operand3 = (opcode >= 8 && opcode <= 14) ? Integer.toString(op3) : S.GetSymbol(op3);
        return String.format("PC = %04d: %s %d <%s>, %d <%s>, %s", pc, mnemonic, opcode, operand1, op1, operand2, operand3);
    }
    //this functions is used in the makeTraceString method to get the strind part of the opcode easily with respect to the number
    private String getMnemonic(int opcode) {
        switch (opcode) {
            case 0:
                return "STOP";
            case 1:
                return "DIV";
            case 2:
                return "MUL";
            case 3:
                return "SUB";
            case 4:
                return "ADD";
            case 5:
                return "MOV";
            case 6:
                return "PRINT";
            case 7:
                return "READ";
            case 8:
                return "JMP";
            case 9:
                return "JZ";
            case 10:
                return "JP";
            case 11:
                return "JN";
            case 12:
                return "JNZ";
            case 13:
                return "JNP";
            case 14:
                return "JNN";
            case 15:
                return "JINDR";
            default:
                return "UNKNOWN";
        }
    }
    public static final int STOP_OPCODE=0;
    public static final int DIV_OPCODE=1;
    public static final int MUL_OPCODE=2;
    public static final int SUB_OPCODE=3;
    public static final int ADD_OPCODE=4;
    public static final int MOV_OPCODE=5;
    public static final int PRINT_OPCODE=6;
    public static final int READ_OPCODE=7;
    public static final int JMP_OPCODE=8;
    public static final int JZ_OPCODE=9;
    public static final int JP_OPCODE=10;
    public static final int JN_OPCODE=11;
    public static final int JNZ_OPCODE=12;
    public static final int JNP_OPCODE=13;
    public static final int JNN_OPCODE=14;
    public static final int JINDR_OPCODE=15;
    
    
    private void InitializeReserveTable(ReserveTable optable) {
        // Add opcode mappings to ReserveTable
        optable.Add("STOP", 0);
        optable.Add("DIV", 1);
        optable.Add("MUL", 2);
        optable.Add("SUB", 3);
        optable.Add("ADD", 4);
        optable.Add("MOV", 5);
        optable.Add("PRINT", 6);
        optable.Add("READ", 7);
        optable.Add("JUMP", 8);
        optable.Add("JZ", 9);
        optable.Add("JP", 10);
        optable.Add("JN", 11);
        optable.Add("JNZ", 12);
        optable.Add("JNP", 13);
        optable.Add("JNN", 14);
        optable.Add("JINDR", 15);
        // Add other opcode mappings as needed
    }
    
}
