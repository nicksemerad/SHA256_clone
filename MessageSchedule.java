package hashFunction;

import java.util.ArrayList;

public class MessageSchedule {

	private ArrayList<Word> words;
	
	public MessageSchedule(String block) {
		words = new ArrayList<Word>();
		populate(block);
		extend();
	}

	public void populate(String block) {
		for(int i = 0; i < 16; i++) {
			words.add( new Word(block.substring((32 * i), ((1+i) * 32))));
		}
	}
	
	/*
	 * sig1(t-2) + t-7 + sig0(t-15) + t-16
	 */
	public void extend() {
		while(words.size() < 64) {
			int l = words.size();
			Word a = words.get(l - 2).sig1().add(words.get(l - 7));
			Word b = words.get(l - 15).sig0().add(words.get(l - 16));
			words.add(a.add(b));
		}
	}
	
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
