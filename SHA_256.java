package hashFunction;

import java.util.ArrayList;
import hashFunction.BlockBuilder;
import hashFunction.MessageSchedule;
import hashFunction.Word;

/**
 * This class is the driver class for the various other classes 
 * and methods, that are required to make a hash with SHA256.
 * 
 * f1e036d3876c0cf2368d18c7102a74d85acd994e19d1c4c8eeb9f95cad496233.
 * 
 * The above hash is a message that I will use as a signature, as 
 * nobody will be able to create this hash without knowing my message
 * Mostly for fun :)
 * 
 * @author Nick Semerad - 2021
 *
 */
public class SHA_256 {

	private ArrayList<Word> currentHash;
	
	/**
	 * This is the constructor of a new hash. Although it is called
	 * the hash function, this class does not do any hashing. Instead,
	 * it acts as a driver for making, scheduling, and compressing
	 * the input message. The current hash starts as the constants
	 * created in the word class. Once a block has been compressed 
	 * into those values, the resulting hash will be the new current 
	 * hash. 
	 * 
	 * @param message - the message to be hashed
	 */
	public SHA_256(String message) {
		currentHash = Word.constants(0, 8);
		for(String block : new BlockBuilder(message).getBlocks()) {
			MessageSchedule g = new MessageSchedule(block);
			currentHash = g.compress(currentHash);			
		}
		System.out.println(result());
	}
	
	/**
	 * This function will append the current hash words together,
	 * to make the continuous and final hash. This message is called
	 * with the completion constructor, and is only used for the output
	 * of the hash to console (currently)
	 * 
	 * @return result - the resulting hex string of the final message hash
	 */
	public String result(){
		StringBuilder hex = new StringBuilder();
		for(Word w : currentHash) {
			StringBuilder currHex = new StringBuilder();
			Long decimal = Long.parseLong(w.getBits(), 2);
			String hexStr = Long.toString(decimal, 16);
			for(int i = 0; i < 8 - hexStr.length(); i++) 
				currHex.append(0);
			currHex.append(hexStr);
			hex.append(currHex);
		}
		
		return hex.toString();
	}
	
	/**
	 * This main method is currently being used for testing. 
	 */
	public static void main(String[] args) {
		SHA_256 h = new SHA_256(args[0]);
	}
}
