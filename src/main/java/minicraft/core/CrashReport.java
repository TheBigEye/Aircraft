package minicraft.core;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class CrashReport extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private JTextArea crashTextArea;
    private JScrollPane crashScrollPane;
    private JLabel crashIconLabel;
    
    // TODO: Add a crash log save, save all into a txt file

    public CrashReport(String crash) {
    	// Crash log Structure
    	crashTextArea = new JTextArea(5, 5);
    	crashIconLabel = new JLabel();
        
        Image icon = null;
		try (
			InputStream is = Game.class.getResourceAsStream("/resources/title.png")) {
			BufferedImage titleIcon = ImageIO.read(is);
			icon = titleIcon.getScaledInstance(258 + 16, 60 + 8, Image.SCALE_REPLICATE);
			crashIconLabel.setIcon(new ImageIcon(icon));
		} catch (IOException exception) {
			exception.printStackTrace();
		}

		crashTextArea.setForeground(Color.BLACK);
		crashTextArea.setBackground(Color.WHITE);
		
		// Crash message
		crashTextArea.setText(crash);
		crashTextArea.setEditable(false);
		crashTextArea.setFont(new Font("Consolas", Font.PLAIN, 12));
		
		// All white, is better
		UIManager.put("OptionPane.background", Color.white);
		UIManager.put("Panel.background", Color.white);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException exception) {
            exception.printStackTrace();
        }

        // Adjust size and set layout
        setPreferredSize(new Dimension(Renderer.getWindowSize()));
        setLayout(null);
        
        // Add components to parent widget
        crashScrollPane = new JScrollPane(crashTextArea);
        crashTextArea.select(0, 0); // Reset the scroll bars position
        add(crashScrollPane);
        add(crashIconLabel);
          
        // Set component bounds (only needed by absolute positioning)
        crashScrollPane.setBounds(69, 100, 732, 380);
        crashIconLabel.setBounds((Renderer.getWindowSize().width / 2) - (icon.getWidth(Initializer.frame) / 2), 25, 258 + 16, 60 + 8);
        setBackground(new Color(46, 53, 69));
    }
    
    /**
     * Manually launched crash!
     */
    public static void crashMePlease() {
    	throw new NullPointerException("Manually initiated crash! :D");
    }
	
}
