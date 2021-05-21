package hashFunction;

import java.util.ArrayList;

public class Word {
	
	private String bits; // stored as a string to keep leading zero(s)
	
	/**
	 * Constructor to create a new word with the bits
	 * as a parameter. 
	 * 
	 * @param input - the bits that make up the 32bit word
	 */
	public Word(String input) {
		bits = input;
	}
	
	/**
	 * A get method for a specific bit.
	 * 
	 * @param index - the index of the bit you want to get
	 * @return - the bit in Integer format
	 */
	public int getBit(int index) {
		return Integer.parseInt(String.valueOf(bits.charAt(index)));
	}
	
	/**
	 * A get method for all bits.
	 * 
	 * @return - the bits in String format
	 */
	public String getBits() {
		return bits;
	}
	

	/**
	 * Shift Right 
	 * This method shifts the bits to the right, count 
	 * number of times. 
	 * 
	 * @param num - how many shifts you want
	 */
	public Word shr(int count) {
		String output = bits;
		for(int i = 0; i < count; i++) 
			output = "0" + output.substring(0, output.length() - 1);
		return new Word(output);
	}
	
	/**
	 * Rotation Right
	 * This method will move the bits over count number of times, 
	 * but unlike the shift right method, the bits will wrap around
	 * to the front when pushed off of the end. 
	 * 
	 * @param count
	 */
	public Word rotr(int count) {
		String output = bits;
		for(int i = 0; i < count; i++) 
			output = output.charAt(output.length() - 1) + output.substring(0, output.length() - 1);
		return new Word(output);
	}
	
	/**
	 * Exclusive Or
	 * This method compares two 32bit words. This compares 
	 * this word and the input word, creating a new output word. 
	 * This output word will have a '1' in index N if the two
	 * compared words have only one '1' at index N. 
	 * i.e. comparing('110010', '101001') results in '011011'
	 * 
	 * @param toCompareWith - the word to compare against 'this' word
	 * @return a new word build based on the exclusive or logic
	 */
	public Word xor(Word toCompareWith) {
		StringBuilder output = new StringBuilder();
		for(int i = 0; i < 32; i++) {
			if(this.getBit(i) == toCompareWith.getBit(i)) {
				output.append(0);
			}
			else {
				output.append(1);
			}
		}
		return new Word(output.toString());
	}
	
	/**
	 * This method adds two words together.
	 * 
	 * It first computes the sum of the two word's values in 
	 * long format. Then set sum equal to the mod of sum in 2^32 
	 * to ensure a max length of 32 bits. Add leading zero(s) as
	 * necessary. Return the sum Word. 
	 * 
	 * @param input - the word you are adding
	 * @return a new word with the sum of this word and the parameter
	 */
	public Word add(Word input) {
		Long sum = Long.parseLong(this.getBits(), 2) + Long.parseLong(input.getBits(), 2);
		sum =  (long) (sum % Math.pow(2, 32));
		StringBuilder padSum = new StringBuilder();
		String output = Long.toBinaryString(sum);
		for(int i = 0; i < 32 - output.length(); i++) 
			padSum.append(0);
		padSum.append(output);
		return new Word(padSum.toString());
	}
	
	/**
	 * Performs a series of operations on the bits.
	 * 
	 * @return - a new word of the resulting bits
	 */
	public Word sig0() {
		return shr(3).xor(rotr(7).xor(rotr(18)));
	}
	
	
	/**
	 * Performs a series of operations on the bits.
	 * 
	 * @return - a new word of the resulting bits
	 */
	public Word sig1() {
		return shr(10).xor(rotr(17).xor(rotr(19)));
	}
	
	/**
	 * Performs a series of operations on the bits.
	 * 
	 * @return - a new word of the resulting bits
	 */
	public Word SIG0() {
		return rotr(22).xor(rotr(2).xor(rotr(13)));
	}
	
	/**
	 * Performs a series of operations on the bits.
	 * 
	 * @return - a new word of the resulting bits
	 */
	public Word SIG1() {
		return rotr(25).xor(rotr(6).xor(rotr(11)));
	}
	
	/*
	 * Choice (x, y, z)
	 * x(N) determines output(N), being
	 * y(N) if x(N) = 1 or 
	 * z(N) if x(N) = 0
	 */
	public static Word ch(Word x, Word y, Word z) {
		StringBuilder output = new StringBuilder();
		for(int i = 0; i < 32; i++) {
			if(x.getBit(i) == 1) {
				output.append(y.getBit(i));
			}
			if(x.getBit(i) == 0) {
				output.append(z.getBit(i));
			}
		}
		return new Word(output.toString());
	}
	
	
	/*
	 * Majority (x, y, z)
	 * the mode of x(N), y(N), z(N) will be output(N)
	 */
	public static Word mj(Word x, Word y, Word z) {
		StringBuilder output = new StringBuilder();
		for(int i = 0; i < 32; i++) {
			if((x.getBit(i) + y.getBit(i) + z.getBit(i)) >= 2) {
				output.append(1);
			}
			else {
				output.append(0);
			}
		}
		return new Word(output.toString());
	}
	
	/**
	 * Static pad function, pads a string of bits
	 * until it is 32 bits total. 
	 * 
	 * This differs from the block builder pad method,
	 * as that one pads to 512 bits.
	 * 
	 * @param toPad - the bits you want to pad to 32 bits total
	 * @return padded - the padded bits
	 */
	public static String pad32(String toPad) {
		int zeros = 32 - toPad.length();
		StringBuilder padded = new StringBuilder();
		for(int i = 0; i < zeros; i++) 
			padded.append(0);
		padded.append(toPad);
		return padded.toString();
	}
	
	/**
	 * constants
	 * 
	 * This method takes the cube or square root of the first 64 prime 
	 * numbers. This is used in both hashing and compression. 
	 * The results have been formatted to fit into a word, 32 bits. 
	 * 
	 * @param type - 0 if you want sqrt, 1 if you want cbrt
	 * @return consts - a word array of the resulting constants
	 */
	public static ArrayList<Word> constants(int type, int length) {
		long[] primes = new long[] {2,3,5,7,11,13,17,19,23,29,31,37,41,43,47,53,
						59,61,67,71,73,79,83,89,97,101,103,107,109,113,127,131,
						137,139,149,151,157,163,167,173,179,181,191,193,197,199,211,223,
						227,229,233,239,241,251,257,263,269,271,277,281,283,293,307,311};
		ArrayList<Word> consts = new ArrayList<Word>();
		
		if(type == 0) {
			for(int i = 0; i < length; i++) {
				Double result = (Math.sqrt(primes[i]) % 1) * Math.pow(2, 32);
				String data = String.format("%.0f", Math.floor(result));
				String bits = Long.toBinaryString(Long.parseLong(data));
				consts.add(new Word(pad32(bits)));
			}
		}
		if(type == 1) {
			for(int i = 0; i < length; i++) {
				Double result = (Math.cbrt(primes[i]) % 1) * Math.pow(2, 32);
				String data = String.format("%.0f", Math.floor(result));
				String bits = Long.toBinaryString(Long.parseLong(data));
				consts.add(new Word(pad32(bits)));
			}
		}
		return consts;
	}
}