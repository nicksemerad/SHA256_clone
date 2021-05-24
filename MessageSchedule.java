package hashFunction;

import java.util.ArrayList;

/**
 * This class is the schedule for a single block. This class takes
 * a block as a parameter in its construction, and will populate,
 * extend, and compress the schedule into the final hash. 
 * 
 * f1e036d3876c0cf2368d18c7102a74d85acd994e19d1c4c8eeb9f95cad496233.
 * 
 * The above hash is a message that I will use as a signature, as 
 * nobody will be able to create this hash without knowing my message.
 * Mostly for fun :)
 * 
 * @author Nick Semerad - 2021
 *
 */

public class MessageSchedule {

	private ArrayList<Word> words;
	
	/**
	 * This is the constructor for a message schedule, for a single
	 * block. By calling this constructor, the block parameter
	 * will be converted to an array of Strings, called words. Then,
	 * it will fill the schedule with these words, and extend this schedule
	 * from 16 words to 64 words. 
	 * 
	 * @param block - the 512 bit block to be converted to a schedule
	 */
	public MessageSchedule(String block) {
		words = new ArrayList<Word>();
		populate(block);
		extend();
	}

	/**
	 * This method will populate the schedule with 16 32bit words,
	 * by cutting the block into smaller pieces. 
	 * 
	 * @param block - the block to be divided, and put in schedule
	 */
	public void populate(String block) {
		for(int i = 0; i < 16; i++) {
			words.add( new Word(block.substring((32 * i), ((1+i) * 32))));
		}
	}
	
	/**
	 * This method will use the already populated words in the schedule
	 * to extend the schedule to 64 words. It does this by adding a series
	 * of words together, some of which are operated on by a function. It
	 * then adds the resulting word to the end of the schedule, until it
	 * is full.
	 * 
	 * Formula, where t=last word in schedule:
	 * sig1(t-2) + (t-7) + sig0(t-15) + (t-16)
	 * These operations can be found in the Word class
	 */
	public void extend() {
		while(words.size() < 64) {
			int l = words.size();
			Word a = words.get(l - 2).sig1().add(words.get(l - 7));
			Word b = words.get(l - 15).sig0().add(words.get(l - 16));
			words.add(a.add(b));
		}
	}
	
	/**
	 * This method will take the 64 words in the message schedule, 
	 * and compress them into a hash. The initial hash is the square
	 * roots of the first 64 primes' cube root. It does this by 
	 * combining various words in the initial hash, and the schedule,
	 * with some words being operated on. These operations can be 
	 * found in the Word class. After the first block has been compressed,
	 * the next block will be compressed with the previous blocks resulting
	 * hash, as the new initial hash. 
	 * 
	 * @param initHash - initial values for the schedule to be compressed into
	 * @return k - the array list of words, containing the compressed schedule
	 */
	public ArrayList<Word> compress(ArrayList<Word> initHash) {
		ArrayList<Word> k = new ArrayList<Word>(); 
		ArrayList<Word> consts = Word.constants(1, 64);
		k = initHash;
		// compress the hash values into the initHash
		for(int i = 0; i < 64; i++) {
			Word t1 = k.get(4).SIG1().add(Word.ch(k.get(4), k.get(5), k.get(6)));
			t1 = t1.add(k.get(7).add(consts.get(i).add(words.get(i))));
			Word t2 = k.get(0).SIG0().add(Word.mj(k.get(0), k.get(1), k.get(2)));
			k.add(0, t1.add(t2));
			k.set(4, k.get(4).add(t1)); 
			k.remove(8);
		}
		// add back the initial hash
		for(int i = 0; i < 8; i++) 
			k.set(i, k.get(i).add(Word.constants(0, 8).get(i)));
		return k;
	}
}
