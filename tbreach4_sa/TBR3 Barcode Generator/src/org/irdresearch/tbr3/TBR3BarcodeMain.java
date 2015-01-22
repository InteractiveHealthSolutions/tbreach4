/**
 * This class generates QR codes for Patient IDs for TB REACH 3 
 */

package org.irdresearch.tbr3;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public final class TBR3BarcodeMain
{
	/**
	 * @param args
	 */
	public static void main (String[] args)
	{
		java.awt.EventQueue.invokeLater (new Runnable ()
		{
			public void run ()
			{
				javax.swing.JFrame frame = new JFrame ("Client Editor");
				frame.setDefaultCloseOperation (WindowConstants.EXIT_ON_CLOSE);
				frame.getContentPane ().add (new GeneratorUI ());
				frame.pack ();
				frame.setVisible (true);
			}
		});
	}
}
