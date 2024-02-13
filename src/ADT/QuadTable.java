package ADT;
import java.io.*;
public class QuadTable {
	//required class members
	int maxSize;
	int [][] table;
	int nextAvailable;
	//construtor begin
	public QuadTable(int maxSize) {
		// TODO Auto-generated constructor stub
		this.maxSize=maxSize;
		this.table=new int[maxSize][4];
		this.nextAvailable=0;
	}
	//required class methods
	public int NextQuad() {
		return nextAvailable;
	}
	
	public void AddQuad(int opcode,int op1, int op2, int op3) {
		if(nextAvailable < maxSize) {
			table[nextAvailable][0]=opcode;
			table[nextAvailable][1]=op1;
			table[nextAvailable][2]=op2;
			table[nextAvailable][3]=op3;
			nextAvailable++;
		}else {
			System.err.println("The table is full");
		}
	}

	public int[] GetQuad(int index) {
		// checking if the index is bound between 0 and nextAvailable
		if (index >=0 && index < nextAvailable) {
			return table[index];
		}else {
			System.err.println("Index doesn't exist");
			return null;
		}
		
	}
	
	public void UpdateJump(int index,int op3) {
		if (index>=0 && index <nextAvailable) {
			table[index][3]=op3;
		}else {
			System.err.println("Index doesn't exist");
		}
	}
	
	public void PrintQuadTable(String filename) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
			writer.write("Index\sOpCode\s\s\sOp1\s\s\s\s\sOp2\s\s\s\s\sOp3\n");
            for (int i = 0; i < nextAvailable; i++) {
                int[] quad = table[i];
                writer.write(String.format("%d\t|\t%d\t|\t%d\t|\t%d\t|\t%d\s\s|%n",i, quad[0], quad[1], quad[2], quad[3]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

}
