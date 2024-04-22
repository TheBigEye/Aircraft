package minicraft.core.io;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.imageio.ImageIO;

import org.tinylog.Logger;

import minicraft.core.Game;

public class Screenshot {
	
	public static void take(BufferedImage image) {
	    try {
	        AffineTransform transform = new AffineTransform();
	        transform.scale(2.0, 2.0); // We scale the image using the SCALE from Renderer
	        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
	        image = op.filter(image, null);
	    	
	        File dir = new File(Game.gameDir + "/screenshots/");
	        if (!dir.exists()) {
	            dir.mkdirs();
	        }
	        
	        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss");
	        LocalDateTime now = LocalDateTime.now();
	        String fileName = dtf.format(now) + ".png";
	        
	        File outputFile = new File(dir, fileName);
	        ImageIO.write(image, "png", outputFile);
	        
	        Logger.info("Screenshot taked and saved as /screenshots/" + fileName);
	    } catch (Exception ex) {
	        System.err.println(ex);
	    }
	}

}
