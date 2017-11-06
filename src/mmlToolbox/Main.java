package mmlToolbox;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	public static void main (String[] args) {
		boolean shiftVol = true;
		ArrayList<String> outText = new ArrayList<String>();
		double volChanger = -999;
		boolean qVolMode = false;
		Scanner stdIn = new Scanner (System.in);
		String fName = "LolNothing.whattoheck";
		int i = 0;
		
		for (String s: args) {
			if (s.equals("-mv"))
				shiftVol = false;

			if (s.equals("-vC"))
				volChanger = Integer.parseInt(args[i+1]);
			
			if (s.equals("-f")) {
				fName = args[i+1];
			}
			

			if (s.equals("-qv"))
				qVolMode = true;
			
			i++;
		}
		
		
		//Debug
		/*
		fName = "t.txt";
		qVolMode = true;
		shiftVol = true;
		*/
		
		System.out.println("AddMusic MMLToolBox Beta by CrispyYoshi");
		System.out.println("Usage: java -jar mmlToolbox.jar <flags>"
				+ "\n-mv: Multiply volume (Instead of shifting it)"
				+ "\n-vC: Specify value to shift/multiply volume by (Int)"
				+ "\n-qv: Tweak qXY volume instead of vXXX volume"
				+ "\n-f <filename>.txt: Text document to open (It will ask otherwise)");
		
		if (volChanger < -300) {
			if (shiftVol) {
				System.out.print("Type volume level to shift by, then hit Enter: ");
				volChanger = stdIn.nextDouble();
			} else {
				System.out.print("Type volume level to multiply by, then hit Enter: ");
				volChanger = stdIn.nextDouble();
			}
		}
		
		if (fName.equals("LolNothing.whattoheck")) {
			System.out.print("Please enter text document name followed by \".txt\", then hit Enter: ");
			fName = stdIn.next();
		}
		
		
		// This will reference one line at a time
        String line = null;
        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = 
                new FileReader(fName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = 
                new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
            	String tweakedLine = "";
            	boolean commentOn = false;
            	
            	for (int j = 0; j < line.length(); j++) {
            		char thisChar = line.charAt(j);
            		String newVol = "";
            		
            		if (thisChar == ';')
            			commentOn = true;
            		
            		if (!commentOn) {
	            		if (!qVolMode) {
		            		if (thisChar == 'v') {
		            			j++;
		            			try {
			            			while (line.charAt(j) >= '0' && line.charAt(j) <= '9' && j < line.length() && !line.isEmpty()) {
			            				newVol += line.charAt(j);
			                			j++;
			            			}
			            			System.out.println(newVol);
		            			} catch (StringIndexOutOfBoundsException e) {
		            			}
		            			j--;
		            		}
	            		} else if (qVolMode) {
	            			if (thisChar == 'q') {
	            				tweakedLine += thisChar; // Add the q itself
	            				j++;
	            				tweakedLine += line.charAt(j); // Add the length following the q, unaltered
	            				j++;
	            				String inHex = line.charAt(j) + "";
	            				
	            				int qVol = Integer.parseInt(inHex, 16); // f by default
	            				
	            				qVol += volChanger;
	            				if (qVol > 15) {
	            					System.out.println("q volume is too high! Capping at f...");
	            					qVol = 15;
	            				}
	            				if (qVol < 0) {
	            					System.out.println("q volume is too low! Flooring at 0...");
	            					qVol = 0;
	            				}
	            				thisChar = Integer.toHexString(qVol).toCharArray()[0];
	            			}
            				            				
	            		}
            		}
            		
            		tweakedLine += thisChar; // Amend the character we're currently looking at to the final output for this line
            		
            		if (!commentOn) {
	            		if (!newVol.isEmpty() && !qVolMode) {
	            			int finalNewVol = (Integer.valueOf(newVol));
	            			if (shiftVol)
	            				finalNewVol += volChanger;
	            			else {
	            				Math.floor(finalNewVol *= volChanger);
	            			}
	            			
	            			System.out.println("Volume change: " + finalNewVol);
	            			
	            			if (finalNewVol > 255) {
	            				System.out.println("Overflow detected! Capping volume at v255...");
	            				finalNewVol = 255;
	            			}
	            			
	            			if (finalNewVol < 0) {
	            				System.out.println("Underflow detected! Flooring volume at v0...");
	            				finalNewVol = 0;
	            			}
	            			
	            			tweakedLine += finalNewVol;
	            		}
            		}
            	}
        	outText.add(tweakedLine);
        	}
            // Always close files.
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                "Couldn't open file '" + fName + "'");                
        }
        catch(IOException ex) {
            System.out.println(
                "Error reading file '" + fName + "'");
        }
		
		FileWriter fWriter = null;
		try {
			String fOut = fName.substring(0, fName.length()-4) + "_out.txt";
			
			fWriter = new FileWriter(fOut);
	        BufferedWriter bufferedWriter = new BufferedWriter(fWriter);
	        
	        for (String s : outText) {
	        	bufferedWriter.write(s + "\n");
	        }
	        bufferedWriter.close();

            System.out.println("Done! Exported to \'" + fOut + "\'!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Close scanner to prevent memory leaks
		stdIn.close();
	}
}
