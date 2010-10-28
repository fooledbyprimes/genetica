/**
 *   chromosome representation class
 *
 *
 **/



import java.util.*;        //if (b>0) {fitness = 1;}

public class chromosome {
    BitSet bitstream;
    BitSet badVerticies;
    int    len;
    int    badNum;
    int    goodNum;
    double fitness;
    int    numGenesOn;
    boolean selected = false;

    public chromosome(int size) {
        bitstream = new BitSet(size);
        badVerticies = new BitSet(size);
        len = size;
        fitness = -1;
    }

    public chromosome(chromosome chr) {
        len = chr.length();
        bitstream = new BitSet(len);
        badVerticies = new BitSet(len);
        for (int i = 1; i <= len; i++) {
            if (chr.getGene(i)) { bitstream.set(i); }
            if (chr.getBadVerticies().get(i)) { badVerticies.set(i); }
        }
        badNum = chr.badNum;
        goodNum = chr.goodNum;
        fitness = chr.fitness;
        numGenesOn = chr.numGenesOn;
    }
    
//MOVED TO GApopulation
//      public void mutate(Random p) {
//          int index = (int) (p.nextFloat() * len);
//          if (bitstream.get(index)) {
//              bitstream.clear(index);
//          }
//          else {
//              bitstream.set(index);
//          }
//      }

    public boolean getGene(int index) {
        return bitstream.get(index);
    }
    
    public boolean getGeneQuality(int index) {
        return badVerticies.get(index);
    }


    public void setGene(int index) {
        bitstream.set(index);
    }


    public void storeBadVerticies(BitSet B) {
        badVerticies.andNot(badVerticies);
        badVerticies.or(B);
    }


    public int getNumGenesOn() {
        int a = 0;
        for (int i = 1; i <=len; i++) {
            if (bitstream.get(i)) {a++;}
        }
        return a;
    }

    public void clearGene(int index) {
        bitstream.clear(index);
    }


    public void setNumBad(int bad) {
        
    }
    
    public BitSet getBits() {return bitstream; }
    public BitSet getBadVerticies() { return badVerticies; }
    public String dumpBadVerticies() {
        String s = new String("");
        for (int i = 1; i<= len; i++) {
            if (badVerticies.get(i)) { 
                s = s.concat("1");
            }
            else {
                s = s.concat("0");
            }
        }
        return s;
    }
        
    public void setFitness(int s, int b) {
	goodNum = s-b;
	badNum = b;
	double l = len;

	double a = s-badNum;
	double c = a/l;
	
	double inf = badNum/l;
	double d = 1.0 - inf;


	double test = 100.0*c*(1.0-inf);
	//double test = 100.0*c;

        fitness = test ;
	if (fitness < 0.1) {fitness = 0.1; }
    }


    public double getFitness() {return fitness;}
    public int length() {return len;}
    
    public String dump() {
        String S = new String("");
        for (int i = 1; i <= len; i++) {
            if (bitstream.get(i)) {
                S = S.concat("1");
            }
            else {
                S = S.concat(".");
            }
        }
        S = S.concat(" f: "+fitness + " g:"+goodNum+" b:"+badNum + " on:"+
                     getNumGenesOn());
        return S;
    }

    public void setSelect(boolean b) {
	selected = b;
    }


}
