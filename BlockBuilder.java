package hashFunction;

import java.util.ArrayList;

public class BlockBuilder {
	
	private ArrayList<String> mBlocks;
	
	/**
	 * This is the constructor for all the blocks that
	 * the message is converted into. How this works, 
	 * is it loops through the message 'n' number of times. 
	 * 'n' is the number of blocks this message has.
	 * Each time it does this, it will take a substring
	 * of the message, with a size of 512 bites. 
	 * 
	 * Assume we are taking the first block, of a one 
	 * block sized message. n = 1 therefore i = 0
	 * The substring will be from index (512*i) = 0 
	 * up to but not including (1+i * 512) = 512
	 * So the substring is from index 0-511, which is
	 * the entire message and block
	 * 
	 * @param input - the message to be converted to blocks
	 */
	public BlockBuilder(String input) {
		mBlocks = new ArrayList<String>();
		String result = toBinary(input);
		int n = result.length() / 512;
		for(int i = 0; i < n; i++) {
			mBlocks.add(result.substring((512 * i), ((1+i) * 512)));
		}
	}
	
	/**
	 * This method is to get the blocks from a 
	 * given BlockBuilder object.
	 * 
	 * @return mBlocks - the message blocks
	 */
	public ArrayList<String> getBlocks() {
		return mBlocks;
	}
	
	/**
	 * This method converts a message string to binary, with each char having
	 * a leading zero on its binary conversion
	 * 
	 * @param message - the string converted to binary
	 * @return binary - the resulting binary, fully padded
	 */
	public String toBinary(String message) {
		StringBuilder binary = new StringBuilder();
		for(int i = 0; i < message.length(); i++) 
			binary.append(0 + Integer.toBinaryString((int)message.charAt(i)));
		return pad(binary);
	}
	
	/**
	 * This method will pad the binary message with a one, to signal the start
	 * of the padding, then many zeros, until the total length is a multiple of 512
	 * bits. This leaves space for an ending 64 bits of data, storing the 
	 * length of the original message at the end. Then it will call the 
	 * below addLength() method, and append that to the padded binary message, 
	 * bringing the total length to a multiple of 512 bits. 
	 * 
	 * @param binary - string builder storing the binary encoding of the message
	 * @return binary - the input string padded and complete for block slicing
	 */
	public static String pad(StringBuilder binary) {
		Integer l = binary.length();
		binary.append(1); 
		Double filled = 65.0 + l;
		Double padCount = Math.ceil(1.0 + l / 512) * 512;
		
		for(int i = 0; i < padCount - filled; i++) 
			binary.append(0);
		
		binary.append(addLengthData(l));
		return binary.toString();
	}
	
	/**
	 * This method, called by the above pad() method, will convert an 
	 * integer length, to that integer in binary. Then it will pad the 
	 * front of the binary length with zeros, until the total length 
	 * is 64 bits. This all goes at the end of the block.
	 * 
	 * @param initLength - the original length of the message
	 * @return output - the input length converted to binary and padded
	 */
	public static String addLengthData(Integer initLength) {
		String binaryLen = Integer.toBinaryString(initLength);
		StringBuilder output = new StringBuilder();
		Integer bitCount = binaryLen.length();
		for(int i = 0; i < 64 - bitCount; i++) 
			output.append(0);
		output.append(binaryLen);
		return output.toString();
	}
}
