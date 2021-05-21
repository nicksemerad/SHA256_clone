package hashFunction;

import java.util.ArrayList;

public class HashFunction {

	private ArrayList<Word> currentHash;
	
	public HashFunction(String message) {
		currentHash = Word.constants(0, 8);
		for(String block : new BlockBuilder(message).getBlocks()) {
			MessageSchedule g = new MessageSchedule(block);
			currentHash = g.compress(currentHash);			
		}
	}
	
	public String result(){
		StringBuilder result = new StringBuilder();
		for(Word w : currentHash) {
			Long decimal = Long.parseLong(w.getBits(), 2);
			String hexStr = Long.toString(decimal, 16);
			result.append(hexStr);
		}
		return result.toString();
	}
	
	public static void main(String[] args) {
		HashFunction h = new HashFunction("");
		System.out.println(h.result());
	}
}
