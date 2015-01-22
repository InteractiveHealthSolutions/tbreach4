/**
 * UI class to display form
 */

package org.irdresearch.tbr3;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import org.jdesktop.layout.LayoutStyle;
import org.jdesktop.layout.GroupLayout;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class GeneratorUI extends JPanel implements ActionListener
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 4652592648303169985L;
	private static final int	size				= 140;
	private static final int	rowLimit			= 4;
	private static final int	pageLimit			= 8;
	private static final int	defaultRange		= 19999;
	private static final String	fileType			= "png";
	private static String		directory			= "c:\\TBR4SA\\QRCodes";

	private JLabel				titleLabel;
	private JLabel				jLabel1;
	//private JLabel				jLabel2;
	private JLabel				jLabel3;
	private JLabel				jLabel5;
	private JPanel				mainPanel;

	//private JTextField			dateTextField;
	private JTextField			locationIdTextField;

	private JSpinner			serialFromSpinner;
	private JSpinner			serialToSpinner;

	private JButton				generateButton;

	public GeneratorUI ()
	{
		initComponents ();
		File file = new File (directory);
		if (!file.exists () || !file.isDirectory ())
		{
			file.mkdirs ();
		}
		directory += "\\";
		//dateTextField.setText (new SimpleDateFormat ("yyyy-MM-dd").format (new Date ()));
		locationIdTextField.setText ("01,02,03");
		serialFromSpinner.setValue (1);
		serialToSpinner.setValue (defaultRange);
	}

	private void initComponents ()
	{
		titleLabel = new javax.swing.JLabel ();
		jLabel1 = new javax.swing.JLabel ();
		locationIdTextField = new javax.swing.JTextField ();
		//jLabel2 = new javax.swing.JLabel ();
		//dateTextField = new javax.swing.JTextField ();
		serialFromSpinner = new javax.swing.JSpinner ();
		jLabel5 = new javax.swing.JLabel ();
		serialToSpinner = new javax.swing.JSpinner ();
		jLabel3 = new javax.swing.JLabel ();
		titleLabel.setFont (new java.awt.Font ("Dialog", 0, 24));
		titleLabel.setText ("QR Code Generator");
		jLabel1.setText ("District IDs (two digits comma separated):");
		locationIdTextField.setToolTipText ("Enter District IDs separated by comma, like 01,02,03");
		//jLabel2.setText ("Date:");
		jLabel5.setText ("to");
		jLabel3.setText ("Serial No. Range:");
		generateButton = new javax.swing.JButton ();
		generateButton.setText ("Generate");
		generateButton.addActionListener (this);
		setCursor (new java.awt.Cursor (java.awt.Cursor.DEFAULT_CURSOR));
		GroupLayout layout = new GroupLayout (this);
		this.setLayout (layout);
		layout.setHorizontalGroup (layout.createParallelGroup (GroupLayout.LEADING).add (
				layout.createSequentialGroup ()
						.addContainerGap ()
						.add (layout
								.createParallelGroup (GroupLayout.LEADING)
								.add (titleLabel)
								.add (layout
										.createSequentialGroup ()
										.add (layout.createParallelGroup (GroupLayout.LEADING).add (jLabel1)/*.add (jLabel2)*/.add (jLabel3))
										.addPreferredGap (LayoutStyle.UNRELATED)
										.add (layout
												.createParallelGroup (GroupLayout.LEADING)
												.add (layout.createSequentialGroup ()/*.add (dateTextField, GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)*/.add (115, 115, 115))
												.add (locationIdTextField)
												.add (layout
														.createSequentialGroup ()
														.add (layout
																.createParallelGroup (GroupLayout.LEADING)
																.add (generateButton, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE)
																.add (layout.createSequentialGroup ().add (serialFromSpinner, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap (LayoutStyle.RELATED).add (jLabel5).addPreferredGap (LayoutStyle.RELATED)
																		.add (serialToSpinner, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE))).add (0, 0, Short.MAX_VALUE)))))
						.add (14, 14, 14)));
		layout.setVerticalGroup (layout.createParallelGroup (GroupLayout.LEADING).add (
				layout.createSequentialGroup ()
						.addContainerGap ()
						.add (titleLabel, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap (LayoutStyle.UNRELATED)
						.add (layout.createParallelGroup (GroupLayout.BASELINE).add (jLabel1)
								.add (locationIdTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap (LayoutStyle.RELATED)
						/*.add (layout.createParallelGroup (GroupLayout.BASELINE).add (dateTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).add (jLabel2))
						.addPreferredGap (LayoutStyle.RELATED)*/
						.add (layout.createParallelGroup (GroupLayout.BASELINE).add (serialFromSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).add (jLabel5)
								.add (serialToSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).add (jLabel3)).addPreferredGap (LayoutStyle.RELATED)
						.add (generateButton).addContainerGap (GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
	}

	public void generateCodes ()
	{
		String locationText = locationIdTextField.getText ();
		//String dateFrom = dateTextField.getText ();
		/*SimpleDateFormat format = new SimpleDateFormat ("yyyy-MM-dd");
		Date date = new Date ();*/
		StringBuilder error = new StringBuilder ();
		// Validate data
		if (locationText.equals ("") || !locationText.matches ("([0-9]{2,2},)*[0-9]{2,2}"))
		{
			error.append ("Location IDs are empty or invalid." + "\n");
		}
		/*if (dateFrom.equals (""))
		{
			error.append ("Date is empty." + "\n");
		}
		try
		{
			date = format.parse (dateFrom);
		}
		catch (ParseException e)
		{
			error.append ("Date is invalid. Make sure the date is in the format: YYYY-MM-DD" + "\n");
		}*/
		int from = Integer.parseInt (serialFromSpinner.getValue ().toString ());
		int to = Integer.parseInt (serialToSpinner.getValue ().toString ());
		if (from > to)
		{
			error.append ("Serial number range is not in order. Start value is greater than End value.");
		}
		if (to > defaultRange)
		{
			error.append ("Serial number range cannot be greater than " + defaultRange);
		}
		if (error.length () > 0)
		{
			JOptionPane.showMessageDialog (mainPanel, error.toString ());
			return;
		}
		String[] locations = locationText.split (",");
		//String dateString = new SimpleDateFormat ("yyMM").format (date);
		for (String s : locations)
		{
			ArrayList<String> files = new ArrayList<String> ();
			for (int i = from; i <= to; i++)
			{
				try
				{
					String qrCodeText = s /*+ dateString*/ + String.format ("%06d", i);
					qrCodeText += "-" + ChecksumHandler.calculateLuhnDigit (qrCodeText);
					String filePath = directory + qrCodeText + ".png";
					files.add (filePath);
					QrCodeHandler.createQRImage (filePath, qrCodeText, size, fileType);
					System.out.print (qrCodeText + "\t");
				}
				catch (Exception e)
				{
					e.printStackTrace ();
				}
			}
			// Merge images one file
			BufferedImage page = new BufferedImage (size * rowLimit, size * pageLimit, BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics = page.createGraphics ();
			int cnt = 1, i = 0, j = 0, x = 0, y = 0;
			for (String file : files)
			{
				
				try
				{
					if (i == rowLimit)
					{
						i = 0;
						j++;
					}
					// Write the file if the page limit is reached
					if (j == pageLimit)
					{
						i = j = 0;
						graphics.dispose ();
						File qrFile = new File (directory + s + '-' + cnt++ + ".png");
						try
						{
							ImageIO.write (page, fileType, qrFile);
							page = new BufferedImage (size * rowLimit, size * pageLimit, BufferedImage.TYPE_INT_ARGB);
							graphics = page.createGraphics ();
						}
						catch (IOException e)
						{
							e.printStackTrace ();
						}
					}
					
					for(int k = 0; k < rowLimit; k ++)
					{
						x = (size * i++);
						y = (size * j);
						BufferedImage image = ImageIO.read (new File (file));
						graphics.drawImage (image, x, y, null);
					}
					
				}
				catch (IOException e)
				{
					e.printStackTrace ();
				}
			}
			System.out.println ();
		}
		JOptionPane.showMessageDialog (mainPanel, "Done!");
	}

	@Override
	public void actionPerformed (ActionEvent e)
	{
		if (e.getSource () == generateButton)
		{
			generateCodes ();
		}
	}
}
