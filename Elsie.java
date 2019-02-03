package elsie.main;

import java.util.Scanner;
/* NOTES
 * 
 * 6x6 substitution board
 * key is 36 char long string in first line of input
 * board constructed by adding each character to the array in order
 * i.e. first char in input string is in tile (0,0)
 * first char has 3 attributes (orig. char, x val, y val)
 * vector x and y values created via the following - (N % 6, N / 6)
 * 
 * CONSTRUCT THE BOARD
 * 
 * step 1: map all 36 characters to their corresponding integer values
 * 
 * step 2: make tile object with attributes (orig char, orig val, x, y, hasMarker)
 * 		   (hasMarker defaulted to False)
 * 
 * step 3: place tile onto board via adding to the end of an array
 *         (should array be 1D or 2D???)
 *         
 * step 4: set hasMarker of tile in location (0,0) to True
 * 
 * DECRYPT THE INPUT
 * 
 * step 5: grab value associated with first char in second line of input
 * 
 * step 6: Search for value in array (brute force O(N^2)?)
 * 
 * step 7: Traverse array left by tile w/ marker x value (subtract)
 * 
 * step 8: Traverse array up by tile w/ marker y value (subtract)
 * 
 * step 9: shift row of plaintext character one position right
 * 
 * step 10: shift column of ciphertext character one position down
 * 
 * step 11: marker is moved to position in array corresponding to
 *          pos of (hasMark = True) ex. (0,0) -> (0 + CT.x, 0 + CT.y)
 *          
 * step 12: repeat steps 5 - 11 to decrypt input
 * 
 * 
 */

public class Elsie {
	
	public static void main(String[] args) {
		
		String key;
		String input;
		String output;
		char [] dict;
		Tile [][] sBox;
		Scanner scan = new Scanner(System.in);
		
		dict = createDict();
		System.out.println("Enter the encryption key");
		key = scan.nextLine();
		
		sBox = createSubBox(key, dict);
		
		System.out.println("Enter the message to be encrypt/decrypt");
		input = scan.nextLine();
		scan.close();
		
		if (input.charAt(0) == '%') {
			output = encrypt(input, dict, sBox);
		}
		else {
			output = decrypt(input, dict, sBox);
		}
		
		System.out.println(output);
		
	}
	
	public static String encrypt(String input, char [] dict, Tile [][] sBox) {
		char c;
		Pair plain = new Pair(-1, -1);
		Pair cp = new Pair(-1, -1);
		Pair m = new Pair(0, 0);
		String output = "";
		
		for (int i = 1; i < input.length(); i++) {
			c = input.charAt(i);
			plain = findCharIndex(c, sBox);
			
			cp.i = plain.i + sBox[m.i][m.j].y;
			if (cp.i > 5) {
				cp.i -= 6;
			}
			cp.j = plain.j + sBox[m.i][m.j].x;
			if (cp.j > 5) {
				cp.j -= 6;
			}
			output += sBox[cp.i][cp.j].c;
			shiftRow(plain, m, cp, sBox);
			shiftColumn(cp, m, sBox);
			m = moveMarker(m, cp, sBox);
		}
		return output;
	}
	
	public static String decrypt(String input, char [] dict, Tile [][] sBox) {
		char c;
		Pair cp = new Pair(-1, -1);
		Pair plain = new Pair(-1, -1);
		Pair m = new Pair(0, 0);
		String output = "";
		
		//MAIN DECRYPTION LOOP
		for (int i = 0; i < input.length(); i++) {
			//printSubBox(sBox);
			c = input.charAt(i);
			cp = findCharIndex(c, sBox);
			
			plain.i = cp.i - sBox[m.i][m.j].y;
			if (plain.i < 0){
				plain.i += 6;
			}
			plain.j = cp.j - sBox[m.i][m.j].x;
			if (plain.j < 0) {
				plain.j += 6;
			}
			//System.out.println("plaintext character location (" + plain.i + ", " + plain.j + ")");
			//System.out.println("plaintext character = " + sBox[plain.i][plain.j].c);
			output += sBox[plain.i][plain.j].c;
			
			shiftRow(plain, m, cp, sBox);
			shiftColumn(cp, m, sBox);
			m = moveMarker(m, cp, sBox);
		}
		return output;
	}
	
	private static void shiftColumn(Pair cp, Pair m, Tile [][] sBox) {
		Tile temp;
		// shift row that contains plaintext character one to the right
		// the last tile in the row wraps to the front
		// do this by swapping first element with current element
		for (int i = 0; i < 5; i++) {
			temp = sBox[i + 1][cp.j];
			sBox[i + 1][cp.j] = sBox[0][cp.j];
			sBox[0][cp.j] = temp;
		}
		cp.i += 1;
		if (cp.i > 5) {
			cp.i -= 6;
		}
	}
	
	private static void shiftRow(Pair plain, Pair m, Pair cp, Tile [][] sBox) {
		Tile temp;
		
		if (cp.i == plain.i) {
			cp.j += 1;
			if (cp.j > 5) {
				cp.j -= 6;
			}
		}
		
		for (int j = 0; j < 5; j++) {
			temp = sBox[plain.i][j + 1];
			sBox[plain.i][j + 1] = sBox[plain.i][0];
			sBox[plain.i][0] = temp;
		}
	}
	
	private static Pair moveMarker(Pair m, Pair cp, Tile [][] sBox) {
		m = findMarkedIndex(sBox);
		//System.out.println("marker before move (" + m.i + " , " + m.j + ")");
		//System.out.println("cp vector x before move: " + sBox[cp.i][cp.j].x);
		//System.out.println("cp vector y before move: " + sBox[cp.i][cp.j].y);

		// find new location for marker and update m
		// also update prior sBox at m location to false and new location to true
		sBox[m.i][m.j].hasMark = false;
		
		m.i += sBox[cp.i][cp.j].y;
		if (m.i > 5) {
			m.i -= 6;
		}
		m.j += sBox[cp.i][cp.j].x;
		if (m.j > 5) {
			m.j -= 6;
		}
		sBox[m.i][m.j].hasMark = true;
		//System.out.println("marker after move (" + m.i + " , " + m.j + ")");
		//System.out.println("marker is on character: " + sBox[m.i][m.j].c);
		return m;
	}
	
	private static Pair findCharIndex(char c, Tile [][] sBox) {
		Pair p = new Pair(-1, -1);
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				if (c == sBox[i][j].c) {
					p.i = i;
					p.j = j;
					break;
				}
			}
		}
		//System.out.println("CP Index = (" + p.i + " , " + p.j + ")");
		//System.out.println("char at cp index = " + sBox[p.i][p.j].c);
		return p;
	}
	
	private static Pair findMarkedIndex(Tile [][] sBox) {
		Pair m = new Pair(-1, -1);
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				if (sBox[i][j].hasMark == true) {
					m.i = i;
					m.j = j;
				}
			}
		}
		return m;
	}
	
	public static char [] createDict() {
		int size, ascii;
		char [] dict;
		
		size = 36;
		ascii = 97;
		dict = new char[size];
		
		dict[0] = '#';
		dict[1] = '_';
		for (int i = 2; i < size ; i ++) {
			if (i < 10) { 
				dict[i] = (char) (i + 48);
			}
			else {
				dict[i] = ((char)ascii);
				ascii += 1;
			}
		}
		return dict;
	}
	
	//take in entire encryption key line and array of all possible characters contained in sBox
	//return sBox 2-D array of tile objects
	public static Tile [][] createSubBox(String key, char [] dict){
		char c;
		int val;
		Tile [][] sBox = new Tile[6][6];
		
		for (int i = 0; i < key.length(); i++) {
			c = key.charAt(i);
			val = findVal(dict, c);
			Tile tile = new Tile(val, val % 6, val / 6, c, false);
			sBox[i / 6][i % 6] = tile;
		}
		sBox[0][0].hasMark = true;
		return sBox;
	}
	
	private static int findVal(char [] dict, char c){
		int i = 0;
		
		while (c != dict[i]) {
			i++;
		}
		return i;
	}
	
	private static void printSubBox(Tile [][] sBox) {
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				System.out.print(sBox[i][j].c + " ");
			}
			System.out.println();
		}
	}

}
