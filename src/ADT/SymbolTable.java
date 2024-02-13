//Raja Kantheti
//Compiler Design Spring-2024
package ADT;
import java.io.*;
import java.util.*;


public class SymbolTable {
    private int maxSize; 
    //maximum size of the table
    private List<SymbolEntry> table;
    ///Did this to segregate the methods usage

    public SymbolTable(int maxSize) {
        this.maxSize = maxSize;
        this.table = new ArrayList<>(maxSize);
    } //Constructot for the Symbol TAble class
    
    //Specified methods according to the document
    public int AddSymbol(String symbol, char usage, int value) {
        return AddSymbol(symbol, usage, value, 0.0, null);
    }

    public int AddSymbol(String symbol, char usage, double value) {
        return AddSymbol(symbol, usage, 0, value, null);
    }

    public int AddSymbol(String symbol, char usage, String value) {
        return AddSymbol(symbol, usage, 0, 0.0, value);
    }
    //overloaded methods for all the above methods, with the logic accordingly.

    private int AddSymbol(String symbol, char usage, int intValue, double floatValue, String stringValue) {
        if (table.size() < maxSize) {
            SymbolEntry entry = new SymbolEntry(symbol, usage, intValue, floatValue, stringValue);
            int index = LookupSymbol(symbol);
            if (index == -1) {
                table.add(entry);
                return table.size() - 1;
            } else {
                return index;
            }
        } else {
            System.err.println("SymbolTable is full. Cannot add more symbols.");
            return -1;
        }
    }
//Looks for a string in the table that can match with the symbol irrespective of the case
    public int LookupSymbol(String symbol) {
        for (int i = 0; i < table.size(); i++) {
            if (table.get(i).GetSymbol().equalsIgnoreCase(symbol)) {
                return i;
            }
        }
        return -1;
    }
    
    //<ethods of the class declared as Symbol entry that specifies the format and also the characteristics.
    

    public String GetSymbol(int index) {
        return table.get(index).GetSymbol();
    }

    public char GetUsage(int index) {
        return table.get(index).GetUsage();
    }

    public char GetDataType(int index) {
        return table.get(index).GetDataType();
    }

    public String GetString(int index) {
        return table.get(index).GetStringValue();
    }

    public int GetInteger(int index) {
        return table.get(index).GetIntValue();
    }

    public double GetFloat(int index) {
        return table.get(index).GetFloatValue();
    }

    public void UpdateSymbol(int index, char usage, int value) {
        table.get(index).SetUsage(usage);
        table.get(index).SetIntValue(value);
    }

    public void UpdateSymbol(int index, char usage, double value) {
        table.get(index).SetUsage(usage);
        table.get(index).SetFloatValue(value);
    }

    public void UpdateSymbol(int index, char usage, String value) {
        table.get(index).SetUsage(usage);
        table.get(index).SetStringValue(value);
    }

    public void PrintSymbolTable(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("Index Name                Use Typ Value\n");
            for (int i = 0; i < table.size(); i++) {
                SymbolEntry e = table.get(i);
                writer.write(String.format("%-6d%-21s%-4c%-4c%s\n", i, e.GetSymbol(), e.GetUsage(), e.GetDataType(), e.GetValueAsString()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class SymbolEntry {
        private String symbol;
        private char usage;
        private int intValue;
        private double floatValue;
        private String stringValue;
//Constructor that defines a mental map of the inputs
        public SymbolEntry(String symbol, char usage, int intValue, double floatValue, String stringValue) {
            this.symbol = symbol;
            this.usage = usage;
            this.intValue = intValue;
            this.floatValue = floatValue;
            this.stringValue = stringValue;
        }
        
        //M<ethods used here are used in the class Symboltable class declared abovve
        //to segregate the values purely because of ease of access. 

        public String GetSymbol() {
            return symbol;
        }

        public char GetUsage() {
            return usage;
        }

        public void SetUsage(char usage) {
            this.usage = usage;
        }

        public char GetDataType() {
            if (intValue != 0) {
                return 'I';
            } else if (floatValue != 0.0) {
                return 'F';
            } else {
                return 'S';
            }
        }

        public int GetIntValue() {
            return intValue;
        }

        public void SetIntValue(int intValue) {
            this.intValue = intValue;
        } 

        public double GetFloatValue() {
            return floatValue;
        }

        public void SetFloatValue(double floatValue) {
            this.floatValue = floatValue;
        }

        public String GetStringValue() {
            return stringValue;
        }

        public void SetStringValue(String stringValue) {
            this.stringValue = stringValue;
        }

        @Override
        public String toString() {
            return String.format("%s\t%c\t%c\t%s", symbol, usage, GetDataType(), GetValueAsString());
        }

        private String GetValueAsString() {
            if (GetDataType() == 'I') {
                return String.valueOf(intValue);
            } else if (GetDataType() == 'F') {
                return String.valueOf(floatValue);
            } else {
                return stringValue;
            }
        }
    }
}                          
