import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

 class AffineCipher implements ActionListener {

   private int sWidth,sHeight;
   private JFrame frame;

   private JTextArea inText,outText;
   private JButton enCrypt,deCrypt;

   private String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ_";
   private Map<String, Integer> countMap = new HashMap<String, Integer>();

   public Map.Entry<String, Integer> max1,max2,max3,max4;

   private BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

   AffineCipher(int sWidth,int sHeight) {

	this.sWidth = sWidth;
	this.sHeight = sHeight;

	frame = new JFrame("Affine Cipher Breaker");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setSize(this.sWidth, this.sHeight);
	frame.setLocation(100,100);
	frame.setResizable(false);
//	frame.setLayout(new FlowLayout());

	JPanel pane = new JPanel(new FlowLayout());
	pane.setBackground(new Color(0,0,0));

	inText = new JTextArea(8,53);
	inText.setLineWrap(true);
	inText.setBorder(BorderFactory.createEtchedBorder(Color.GREEN,Color.BLACK));
	inText.setBackground(new Color(0,0,0));
	inText.setForeground(new Color(0,255,0));
	JScrollPane inTextPane = new JScrollPane(inText);
	pane.add(inTextPane);

	deCrypt = new JButton("Decrypt");
	deCrypt.addActionListener(this);
	pane.add(deCrypt);

	enCrypt = new JButton("Encrypt");
	enCrypt.addActionListener(this);
	pane.add(enCrypt);

	outText = new JTextArea(8,53);
	outText.setLineWrap(true);
	outText.setBorder(BorderFactory.createEtchedBorder(Color.GREEN,Color.BLACK));
	outText.setBackground(new Color(0,0,0));
	outText.setForeground(new Color(0,255,0));
	outText.setEditable(false);
	JScrollPane outTextPane = new JScrollPane(outText);
	pane.add(outTextPane);

	frame.add(pane);

	frame.setVisible(true);
   }

   public void actionPerformed(ActionEvent event) {

	Object source = event.getSource();

	if(source == deCrypt) {

	   String _e = null,_blank = null;

		ChCount();
		System.out.print("Welcher Buchstabe soll als <E> vermutet werden?: ");
		try {
		 //_e = input.readLine();
		 _e = JOptionPane.showInputDialog(frame,"Meiste Buchstaben: "+max1.getKey()+"<"+max1.getValue()+">, "+max2.getKey()+"<"+max2.getValue()+">, "+max3.getKey()+"<"+max3.getValue()+">, "+max4.getKey()+"<"+max4.getValue()+"> Welcher kann E sein?").toUpperCase();
		 System.out.println(_e+"<"+chars.indexOf(_e)+"> OK.\n");

		} catch(Exception e) { System.out.println("INPUT ERROR ### ABORT."); }

		System.out.print("Welcher Buchstabe soll als <_> (blank) vermutet werden?: ");
		try {
		 //_blank = input.readLine();
		 _blank = JOptionPane.showInputDialog(frame,"Meiste Buchstaben: "+max1.getKey()+"<"+max1.getValue()+">, "+max2.getKey()+"<"+max2.getValue()+">, "+max3.getKey()+"<"+max3.getValue()+">, "+max4.getKey()+"<"+max4.getValue()+"> Welcher kann _ (blank) sein?").toUpperCase();
		 System.out.println(_blank+"<"+chars.indexOf(_blank)+"> OK.\n");

		} catch(Exception e) { System.out.println("INPUT ERROR ### ABORT."); }

		if(_e != null && _blank != null) { GaussDecrypt(chars.indexOf(_e),chars.indexOf(_blank)); }
	}

	if(source == enCrypt) {

	   String _e = null,_blank = null;

		String text = this.inText.getText().replace(" ","_");
		text = text.replace("\n","").toUpperCase();
		String[] split = text.split("");

		boolean mulOK = false,sumOK = false;
		String myMult = "0",mySum = "0";

		try {

		 while(!mulOK) {
		   myMult = JOptionPane.showInputDialog(frame,"Bitte den Multiplikator eingeben:");
		   if(GCD(Integer.parseInt(myMult),this.chars.length()) == 1) { mulOK = true; System.out.println("GCD("+myMult+","+this.chars.length()+") = 1, OK."); }
		   else JOptionPane.showMessageDialog(frame,"GCD-Fehler","Der eingegebene Multiplikator ist leider nicht teilerfremd. Bitte einen neuen wählen.",JOptionPane.WARNING_MESSAGE);
		 }
		 while(!sumOK) {
		   mySum = JOptionPane.showInputDialog(frame,"Bitte den Summanden eingeben:");
		   if(mySum != null) { Integer.parseInt(mySum); sumOK = true; System.out.println("SUM = "+mySum+", OK."); }
		   else throw new Exception();
		 }
		} catch(Exception e) { System.out.println("INPUT ERROR ### ABORT."); }

		if(mulOK && sumOK) {

			startENC(Integer.parseInt(myMult),Integer.parseInt(mySum),split);
		}
	}
   }

   public void ChCount() {

	String[] chArr = chars.split("");
	String text = this.inText.getText().toUpperCase().replace(" ","_");
	String replaced;
	this.max1 = new AbstractMap.SimpleEntry<String,Integer>("nil",0);
	this.max2 = new AbstractMap.SimpleEntry<String,Integer>("nil",0);
	this.max3 = new AbstractMap.SimpleEntry<String,Integer>("nil",0);
	this.max4 = new AbstractMap.SimpleEntry<String,Integer>("nil",0);

	for(String val: chArr) {

	   replaced = text.replace(val,"");
	   countMap.put(val,(text.length() - replaced.length()));
	}

	for(Map.Entry<String, Integer> entry: countMap.entrySet()) {

		if(entry.getValue() >= max1.getValue()) { max4 = max3; max3 = max2; max2 = max1; max1 = entry; }
		if(entry.getValue() >= max2.getValue() && entry.getValue() < max1.getValue()) { max4 = max3; max3 = max2; max2 = entry; }
		if(entry.getValue() >= max3.getValue() && entry.getValue() < max2.getValue()) { max4 = max3; max3 = entry; }
		if(entry.getValue() >= max4.getValue() && entry.getValue() < max3.getValue()) { max4 = entry; }
	}

	System.out.println("=== LIST ===");

	for(Map.Entry<String, Integer> entry: countMap.entrySet()) {

		System.out.println(entry.getKey()+" kommt "+entry.getValue()+" Mal vor.");
	}

	System.out.println("=== MOST ===");

	System.out.println("Most; <"+max1.getKey()+"> "+max1.getValue()+", <"+max2.getKey()+"> "+max2.getValue()+", <"+max3.getKey()+"> "+max3.getValue()+", <"+max4.getKey()+"> "+max4.getValue());

	
   }

   public static void main(String[] args) {

	new AffineCipher(600,345);
   }

   private void GaussDecrypt(int _e, int _blank) {

	// __a=_e-_blank/5
	// System.out.println("Versuch 2: __a = "+(((_blank-_e)%27)/5));

	int hR = 22;
	int lB = _blank+(-_e+27);

	int __a = (((_blank-_e)/22)%27); //int __a = ((_e-_blank)/5);
	if(__a < 0) __a = (__a+27)%27;

	while(hR != 1) {

	   hR = (hR*2)%27;
	   lB = (lB*2)%27;
	 System.out.println("HR: "+hR+"; LB: "+lB);
	}
	__a = lB;

	System.out.println("Versuch 1: __a = "+__a);

	int __b = (_e-4*__a)%27;
	if(__b < 0) __b = (__b+27)%27;
	System.out.println("Versuch #: __b = "+__b);

	System.out.println("Key ("+__a+","+__b+") gefunden. OK?");

	int __x = 0;
	while((__x*__a)%27 != 1) { __x++; }
	System.out.println("X gefunden: "+__x);

	String[] itArr = inText.getText().split("");

	for(String val: itArr) {

		int index = chars.indexOf(val);
//		System.out.println("<"+index+"> berechnet __x*index+__b mod27: "+(__x*index+__b)+"mod27 = "+((((__x*index+__b)%27)+27)%27));
		System.out.println("<"+index+"> berechnet "+__x+"*("+index+" - "+__b+") mod27: "+((__x*((index-__b))%27)+27)%27);
	}

	System.out.println("== PLAINTEXT ==");

	String outText = inText.getText();
	outText = outText.replace("\n","");

	for(String val: itArr) {

		int index = chars.indexOf(val);
		outText = outText.replace(val,Character.toString(chars.charAt(((__x*((index-__b))%27)+27)%27)).toLowerCase());
	}

	System.out.println(outText.replace("_"," ").toUpperCase());
	this.outText.setText(null);
	this.outText.append(outText.replace("_"," ").toUpperCase());
   }

   private void startENC(int mul, int sum, String[] text) {

	this.outText.setText(null);

	for(String val: text) {

	   outText.append(Character.toString(chars.charAt((this.chars.indexOf(val)*mul+sum)%27)).replace("_"," "));
	}
   }

public int GCD(int a, int b) {
   if (b==0) return a;
   return GCD(b,a%b);
}
 }