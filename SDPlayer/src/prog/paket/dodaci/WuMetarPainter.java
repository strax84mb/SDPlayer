package prog.paket.dodaci;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.plaf.nimbus.AbstractRegionPainter;

public class WuMetarPainter extends AbstractRegionPainter {

	private Color redColor = Color.RED;
	private Color yellowColor = Color.YELLOW;
	private Color greenColor = Color.GREEN;
	private Color grayColor = Color.LIGHT_GRAY;

	private WuMetar wm;

	public WuMetarPainter(WuMetar wm){
		this.wm = wm;
	}

	@Override
	protected void doPaint(Graphics2D g, JComponent arg1, int width,
			int height, Object[] arg4) {
		int value = wm.getValue() + 1;
		if(value <= 15){
			// Bojenje delimicno sive
			g.setColor(grayColor);
			g.fillRect(width - value, 3, 100 - width + value, 10);
		}else if(value <= 75){
			// Bojenje sive i delimicno zelene
			g.setColor(greenColor);
			g.fillRect(width - value, 3, 100 - width + value, 10);
			g.setColor(grayColor);
			g.fillRect(86, 3, 15, 10);
		}else if(value <= 92){
			// Bojenje sive, zelene i delimicno zute
			g.setColor(yellowColor);
			g.fillRect(width - value, 3, 100 - width + value, 10);
			g.setColor(greenColor);
			g.fillRect(26, 3, 60, 10);
			g.setColor(grayColor);
			g.fillRect(86, 3, 15, 10);
		}else{
			// Bojenje sive, zelene, zute i delimicno crvene
			g.setColor(redColor);
			g.fillRect(width - value, 3, 100 - width + value, 10);
			g.setColor(yellowColor);
			g.fillRect(9, 3, 17, 10);
			g.setColor(greenColor);
			g.fillRect(26, 3, 60, 10);
			g.setColor(grayColor);
			g.fillRect(86, 3, 15, 10);
		}
	}

	@Override
	protected PaintContext getPaintContext() {
		return null;
	}
	
}