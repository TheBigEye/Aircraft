package minicraft.core.io;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;

public final class ClipboardHandler implements ClipboardOwner {

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) { /* unused */ }

	/**
	 * Give the system clipboard data.
	 */
	public void setClipboardContents(String string) {
	    StringSelection stringSelection = new StringSelection(string);
	    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	    clipboard.setContents(stringSelection, null);
	}

	/**
	 * Get the string from the system clipboard data.
	 * @return A string with the contents.
	 */
	public String getClipboardContents() {
	    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	    Transferable contents = clipboard.getContents(null);
	    if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
	        try {
	            return (String) contents.getTransferData(DataFlavor.stringFlavor);
	        } catch (UnsupportedFlavorException | IOException exception){
	        	exception.printStackTrace();
	        }
	    }
	    return "";
	}
}
