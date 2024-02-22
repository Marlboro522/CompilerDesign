package ADT;

import java.io.*;

public class Interpreter {
    private ReserveTable reserveTable;
    public static final int MAX_VALUE = 16;

    public Interpreter() {
        // Initialize the ReserveTable and add opcode mappings
        this.reserveTable = new ReserveTable(MAX_VALUE); // Assuming a maximum of 100 opcode mappings
        InitializeReserveTable(reserveTable);
    }

    public boolean initializeFactorialTest(SymbolTable stable, QuadTable qtable) {
        // Add necessary variable and opcode data for Factorial function to SymbolTable and QuadTable
        // Implement according to assignment requirements
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
        //... put the rest of the Quad table entries below...

    }

    public boolean initializeSummationTest(SymbolTable stable, QuadTable qtable) {
        InitSTS(stable);
        InitQTS(qtable);
        return true;
    }

    public static void InitSTS(SymbolTable st) {
        st.AddSymbol("n", 'V', 10);
        st.AddSymbol("i", 'V', 0);
        st.AddSymbol("sum", 'V', 0);
        st.AddSymbol("1", 'C', 1);
        st.AddSymbol("$temp", 'V', 0);
    }

    public static void InitQTS(QuadTable qt) {
        qt.AddQuad(5, 3, 0, 2); // MOV
        qt.AddQuad(5, 3, 0, 1); // MOV
        qt.AddQuad(3, 1, 0, 4); // SUB
        qt.AddQuad(10, 4, 0, 7); // jp
        qt.AddQuad(4, 2, 1, 2); // ADD
        qt.AddQuad(4, 1, 3, 1); // ADD
        qt.AddQuad(8, 0, 0, 2); // JUMP
        qt.AddQuad(6, 2, 0, 0); // PRINT
    }

    public void InterpretQuads(QuadTable Q, SymbolTable S, boolean traceOn, String filename) {
    	try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            int pc = 0; // Program counter
            while (pc < Q.NextQuad()) {
                int[] quad = Q.GetQuad(pc); // Get the current quad
                if ("JP".equals(reserveTable.LookupCode(quad[0])) || "JMP".equals(reserveTable.LookupCode(quad[0]))) {
                    int jumpDestination = quad[3]; // Get the jump destination
                    if (jumpDestination >= 0 && jumpDestination < Q.NextQuad()) {
                        pc = jumpDestination; // Jump to the specified line
                    } else {
                        // Handle invalid jump destination (e.g., print an error message)
                        System.err.println("Invalid jump destination: " + jumpDestination);
                        // Optionally, terminate the interpreter or take appropriate action
                        break;
                    }
                } else {
                    String traceMessage = makeTraceString(pc, quad[0], quad[1], quad[2], quad[3], S); // Generate trace message
                    if (traceOn) {
                        System.out.println(traceMessage); // Print to console
                        writer.write(traceMessage + "\n"); // Write to file
                    }
                    pc++; // Move to the next quad
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String makeTraceString(int pc, int opcode, int op1, int op2, int op3, SymbolTable S) {
        String mnemonic = reserveTable.LookupCode(opcode);
        String operand1 = S.GetSymbol(op1);
        String operand2 = S.GetSymbol(op2);
        String operand3 = S.GetSymbol(op3);
        return String.format("PC = %04d: %s %d <%s>, %d <%s>, %d <%s>", pc, mnemonic, opcode, operand1, op1, operand2, op2, operand3, op3);
    }

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
        optable.Add("JMP", 8);
        optable.Add("JZ", 9);
        optable.Add("JP", 10);
        optable.Add("JN", 11);
        optable.Add("JNZ", 12);
        optable.Add("JNP", 13);
        optable.Add("JNN", 14);
        optable.Add("JINDR", 15);
        // Implement according to assignment requirements
    }
}
