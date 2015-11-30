package wuerfelspiel;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.text.NumberFormatter;

/**
 * Dies ist ein klassisches Wuerfelspiel, bei welchem ein Spieler gegen eine KI antritt und als erster die Punktegrenze erreichen muss.<br>
 * Man kann, wenn man am Zug ist so oft wuerfeln wie man moechte und keine Sechs kommt. Wenn eine Sechs kommt,
 * verfallen alle Punkte, wenn man nicht den Zug abgegeben hat und sie sicherte.
 * 
 * @author Lukas Schramm
 * @version 1.0
 */
public class Wuerfelspiel {

	private int grenze = 50;
	private JFrame frame1 = new JFrame("Würfeln bis "+grenze);
	private NumberFormat format = NumberFormat.getInstance(); 
	private NumberFormatter formatter = new NumberFormatter(format);
	private JButton ButtonWuerfeln = new JButton("Würfeln");
	private JButton ButtonSichern = new JButton("Sichern");
	private JButton ButtonNeustart = new JButton("Neustart"); 
	private JLabel LabelZahlenSp = new JLabel();
	private JLabel LabelZahlenKi = new JLabel();
	private JLabel LabelPunktzahlSp = new JLabel();
	private JLabel LabelPunktzahlKi = new JLabel();
	  
	private ArrayList<Integer> zahlensp = new ArrayList<Integer>();
	private ArrayList<Integer> zahlenki = new ArrayList<Integer>();
	private int sumsp=0, sumki=0;
	private int pktsp=0, pktki=0;
	private int sichernBei;
	private int compSum = 0;
	private boolean letzterZug = false;
	
	public Wuerfelspiel() {
		frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame1.setSize(485,125);
		frame1.setResizable(false);
		
		Container cp = frame1.getContentPane();
		cp.setLayout(null);
		
		ButtonWuerfeln.setBounds(380, 10, 90, 25);
		ButtonWuerfeln.setVisible(true);
		ButtonWuerfeln.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				neuerWurf();
			}
		});
		cp.add(ButtonWuerfeln);
		
		ButtonSichern.setBounds(380, 40, 90, 25);
		ButtonSichern.setVisible(true);
		ButtonSichern.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				sichern();
			}
		});
		cp.add(ButtonSichern);
		
		ButtonNeustart.setBounds(380, 70, 90, 25);
		ButtonNeustart.setVisible(true);
		ButtonNeustart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				neustart();
			}
		});
		cp.add(ButtonNeustart);
		
		LabelZahlenKi.setBounds(15, 15, 300, 20);
		LabelZahlenKi.setText("Computer:");
		LabelZahlenKi.setVisible(true);
		cp.add(LabelZahlenKi);
		
		LabelZahlenSp.setBounds(15, 55, 300, 20);
		LabelZahlenSp.setText("Spieler:");
		LabelZahlenSp.setVisible(true);
		cp.add(LabelZahlenSp);
		
		LabelPunktzahlKi.setBounds(315, 15, 50, 20);
		LabelPunktzahlKi.setText("Σ: 0");
		LabelPunktzahlKi.setVisible(true);
		cp.add(LabelPunktzahlKi);
		
		LabelPunktzahlSp.setBounds(315, 55, 50, 20);
		LabelPunktzahlSp.setText("Σ: 0");
		LabelPunktzahlSp.setVisible(true);
		cp.add(LabelPunktzahlSp);
		
		frame1.setLocationRelativeTo(null);
		format.setGroupingUsed(false); 
		formatter.setAllowsInvalid(false);
		
		zahleneingabe();
		frame1.setTitle("Würfeln bis "+grenze);
		frame1.setVisible(true);
		computerzug(true);
		buttonsAktivieren(false);
	}
	
	/**
	 * Diese Methode fragt den Spieler zu Anfang, bis zu welcher Punktzahl er spielen möchte.
	 * Wenn er keine gueltige, natuerliche Zahl zwischen 25 und 1.000 eingibt, dann wird die Frage nochmals formuliert.
	 */
	private void zahleneingabe() {
		JFormattedTextField nummernfeld = new JFormattedTextField(formatter);
		Object[] zahlenfrage = {"Bis zu welcher Zahl möchtest Du würfeln?", nummernfeld};
		JOptionPane pane = new JOptionPane(zahlenfrage, JOptionPane.QUESTION_MESSAGE, JOptionPane.DEFAULT_OPTION);
		pane.createDialog(null, "Spiellänge").setVisible(true);
		
		String zahlStr = nummernfeld.getText();
		if(zahlStr.equals("")) {
			JOptionPane.showMessageDialog(null, "Bitte gib eine Zahl ein!", "Ungültige Eingabe", JOptionPane.ERROR_MESSAGE);
			zahleneingabe();
		} else {
			int zahl = Integer.parseInt(zahlStr);
			if(zahl > 1000 || zahl < 25) {
				JOptionPane.showMessageDialog(null, "Bitte wähle eine Grenze zwischen 25 und 1.000.", "Ungültige Eingabe", JOptionPane.QUESTION_MESSAGE);
				zahleneingabe();
			} else {
				grenze = zahl;
			}
		}
		
	}
	
	/**
	 * Dies ist die Methode fuer einen neuen Wurf des Spielers. Es wird eine Zufallszahl von 1 bis 6 geworfen.
	 * Wenn eine Sechs faellt, ist der Spielerzug vorbei und die KI darf spielen.
	 * Sollte dies der letzte Spielzug als Nachzug des Spielers gewesen sein, dann endet das Spiel hier auch.
	 * Wenn keine Sechs gefallen ist, dann wird die Zahl zwischengespeichert.
	 */
	private void neuerWurf() {
		Random wuerfel = new Random();
		int wurf = wuerfel.nextInt(6)+1;
		if(wurf==6) {
			if(!letzterZug) {
				JOptionPane.showMessageDialog(null, "Du hast eine 6 gewürfelt. Alle nicht gesicherten Punkte gehen verloren.\nDer Computer ist nun dran.","Sechs geworfen", JOptionPane.PLAIN_MESSAGE);
				computerzug(true);
			} else {
				letztZugAuswertung();
			}
			zahlensp.clear();
			LabelZahlenSp.setText("Spieler:");
			buttonsAktivieren(false);
			
		} else {
			zahlensp.add(wurf);
			String anzeige = "";
			for(Integer k:zahlensp) {
				anzeige += " "+k;
			}
			LabelZahlenSp.setText("Spieler:"+anzeige);
		}
	}
	
	/**
	 * Diese Methode sichert die Punkte des Spielers und gibt die Runde weiter an den Computergegner.
	 */
	private void sichern() {
		for(Integer k:zahlensp) {
			sumsp += k;
		}
		LabelPunktzahlSp.setText("Σ: "+sumsp);
		zahlensp.clear();
		LabelZahlenSp.setText("Spieler:");
		buttonsAktivieren(false);
		letztZugAuswertung();
	}
	
	/**
	 * Dies ist die Methode fuer einen neuen Wurf der KI. Es wird eine Zufallszahl von 1 bis 6 geworfen.
	 * Wenn eine Sechs faellt, ist der Spielerzug vorbei und der Spieler darf spielen.
	 * Bei erstmaligem Zug wird die Zahl compSum, ein Computerzwischenspeicher auf 0 gesetzt und zufaellig ausgelost,
	 * bis zu welchem Wert von 8 bis 14 der Computer diese Runde versucht zu kommen.
	 * Es werden, um nicht zu schnell fuer den Spieler zu sein im Abstand von 1,5 Sekunden Wuerfe generiert.
	 * Sollte der Computer bei der sich selbst erdachten Summe fuer diese Partie angelangt sein oder sollte die Punktzahl zum Sieg reichen, sichert er seine Punkte.
	 * @param erstZug Boolean, ob das der erstmalige Wurf des Computers seit der letzten Sicherung ist
	 */
	private void computerzug(boolean erstZug) {
		Random wuerfel = new Random();
		if(erstZug) {
			sichernBei = wuerfel.nextInt(6)+8;
			compSum = 0;
		}
		int wurf = wuerfel.nextInt(6)+1;
		if(wurf==6) {
			zahlenki.add(6);
			String anzeige = "";
        	for(Integer k:zahlenki) {
				anzeige += " "+k;
			}
        	LabelZahlenKi.setText("Computer:"+anzeige);
        	Thread thread = new Thread(new Runnable() {
		        public void run() {
		            try {
		            	Thread.sleep(1500);
		            	zahlenki.clear();
		    			LabelZahlenKi.setText("Computer:");
		    			buttonsAktivieren(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
		        }
		    });
			thread.setDaemon(true);
			thread.start();
			
		} else if (compSum < sichernBei && sumki+compSum < grenze) {
			compSum += wurf;
			zahlenki.add(wurf);
        	String anzeige = "";
        	for(Integer k:zahlenki) {
				anzeige += " "+k;
			}
        	LabelZahlenKi.setText("Computer:"+anzeige);
			Thread thread = new Thread(new Runnable() {
		        public void run() {
		            try {
		            	Thread.sleep(1500);
		            	computerzug(false);
					} catch (Exception e) {
						e.printStackTrace();
					}
		        }
		    });
			thread.setDaemon(true);
			thread.start();
		} else {
			sumki += compSum;
			LabelPunktzahlKi.setText("Σ: "+sumki);
			Thread thread = new Thread(new Runnable() {
		        public void run() {
		            try {
		            	Thread.sleep(1500);
		            	zahlenki.clear();
		    			LabelZahlenKi.setText("Computer:");
		    			buttonsAktivieren(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
		        }
		    });
			thread.setDaemon(true);
			thread.start();
		}
		if(sumki>grenze-1) {
			nachzug();
		}
	}
	
	/**
	 * Diese Methode wird aufgerufen, wenn der Computer die Punktzahl als Erster erreicht und der Spieler den Nachzug hat.
	 * Der boolean letzterZug wird auf true gesetzt.
	 */
	private void nachzug() {
		JOptionPane.showMessageDialog(null,"Der Computer hat es vor Dir über "+grenze+" Punkte geschafft.\nDa Du einen Zug später angefangen hast,\nkannst Du nun noch einmal Würfeln und gleichziehen.","Letzte Chance", JOptionPane.PLAIN_MESSAGE);
		letzterZug = true;
	}
	
	/**
	 * Diese Methode wertet aus, ob der Spieler gewonnen oder verloren hat oder ob er durch den Nachzug noch ein Unentschieden erreichte.
	 */
	private void letztZugAuswertung() {
		if(letzterZug) {
			if(sumsp>grenze-1) {
				auswertung(2);
			} else {
				auswertung(0);
			}
		} else {
			if(sumsp>grenze-1) {
				auswertung(1);
			} else {
				computerzug(true);
			}
		}
	}
	
	/**
	 * Diese Methode aktiviert oder deaktiviert die Buttons fuer den Spieler, um beim Computerzug nichts beeinflussen zu koennen.
	 * @param akt boolean zum Aktivieren oder Deaktivieren
	 */
	private void buttonsAktivieren(boolean akt) {
		ButtonWuerfeln.setEnabled(akt);
		ButtonSichern.setEnabled(akt);
		ButtonNeustart.setEnabled(akt);
	}
	
	/**
	 * Diese Methode kuert den Sieger der aktuellen Runde und gibt ihm einen Punkt.
	 * @param sieger int der anzeigt, wer gewann oder ob das Spiel unentschieden endet
	 */
	private void auswertung(int sieger) {
		if(sieger == 0) {
			pktki += 1;
			auswertungsdialog("Der Computer hat als Erster die "+grenze+"-Punkte-Grenze erreicht.\nDamit hast Du verloren.", "Verloren");
		} else if(sieger == 1) {
			pktsp += 1;
			auswertungsdialog("Du hast als Erster die "+grenze+"-Punkte-Grenze erreicht.\nDamit hast Du gewonnen.", "Gewonnen");
		} else{
			auswertungsdialog("Ihr habt es beide über "+grenze+" Punkte geschafft.\nDas Spiel endet unentschieden.", "Unentschieden");
		}
		neuepartie();
	}
	
	/**
	 * Dialog der anzeigt, welcher Spieler gerade in Fuerung liegt.
	 * @param str Uebernimmt den Auswertungsstring aus der Methode auswertung()
	 * @param title Uebernimmt den Titel aus der Methode auswertung()
	 */
	private void auswertungsdialog(String str, String title) {
		if(pktki > pktsp) {
			JOptionPane.showMessageDialog(null, str+"\nDer Computer führt mit "+pktki+" zu "+pktsp+".\nEine neue Runde wird gestartet.", title, JOptionPane.PLAIN_MESSAGE);
		} else if (pktki == pktsp) {
			JOptionPane.showMessageDialog(null, str+"\nEs herrscht Gleichstand mit "+pktki+" zu "+pktsp+".\nEine neue Runde wird gestartet.", title, JOptionPane.PLAIN_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(null, str+"\nDu liegst mit "+pktsp+" zu "+pktki+" vorn.\nEine neue Runde wird gestartet.", title, JOptionPane.PLAIN_MESSAGE);
		}
	}
	
	/**
	 * Die Zuruecksetzung aller relevanter Variablen startet ein neues Spiel und den Beginn eines Computerzuges.
	 */
	private void neuepartie() {
		zahlensp.clear();
		zahlenki.clear();
		sumsp = 0;
		sumki = 0;
		compSum = 0;
		LabelPunktzahlSp.setText("Σ: 0");
		LabelPunktzahlKi.setText("Σ: 0");
		buttonsAktivieren(false);
		letzterZug = false;
		computerzug(true);
	}

	/**
	 * Diese Methode startet ein komplett neues Duell und setzt alle Punktzahlen zurueck.
	 */
	private void neustart() {
		JOptionPane.showMessageDialog(null, "Ein neues Spiel wird gestartet und die Punkte zurückgesetzt.", "Neustart", JOptionPane.PLAIN_MESSAGE);
		pktsp = 0;
		pktki = 0;
		zahleneingabe();
		frame1.setTitle("Würfeln bis "+grenze);
		neuepartie();
	}
	
	public static void main(String[] args) {
		new Wuerfelspiel();
	}

}