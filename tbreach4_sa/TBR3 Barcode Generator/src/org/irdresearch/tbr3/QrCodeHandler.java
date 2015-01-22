/**
 * 
 */

package org.irdresearch.tbr3;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import javax.imageio.ImageIO;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class QrCodeHandler
{
	public static void createQRImage (String filePath, String qrCodeText, int size, String fileType)
			throws WriterException, IOException
	{
		// Create the ByteMatrix for the QR-Code that encodes the given String
		Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel> ();
		hintMap.put (EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

		QRCodeWriter qrCodeWriter = new QRCodeWriter ();
		BitMatrix byteMatrix = qrCodeWriter.encode (qrCodeText, BarcodeFormat.QR_CODE, size, size, hintMap);

		// Make the BufferedImage that are to hold the QRCode
		int matrixWidth = byteMatrix.getWidth ();
		int matrixHeight = byteMatrix.getHeight ();
		BufferedImage image = new BufferedImage (matrixWidth, matrixHeight, BufferedImage.TYPE_INT_RGB);
		image.createGraphics ();
		Graphics2D graphics = (Graphics2D) image.getGraphics ();
		graphics.setColor (Color.WHITE);
		graphics.fillRect (0, 0, matrixWidth + 5, matrixHeight + 5);
		graphics.setFont (graphics.getFont ().deriveFont (18f));
		graphics.setColor (Color.BLACK);
		graphics.drawString (qrCodeText, 20, size - 5);
		
		// Paint and save the image using the ByteMatrix
		for (int i = 0; i < matrixHeight; i++)
		{
			for (int j = 0; j < matrixHeight; j++)
			{
				if (byteMatrix.get (i, j))
				{
					graphics.fillRect (i, j, 1, 1);
				}
			}
		}
		// Write the image on file
		File qrFile = new File (filePath);
		ImageIO.write (image, fileType, qrFile);
	}
}
