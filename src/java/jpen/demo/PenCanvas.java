/* [{
Copyright 2008 Nicolas Carranza <nicarran at gmail.com>

This file is part of jpen.

jpen is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License,
or (at your option) any later version.

jpen is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with jpen.  If not, see <http://www.gnu.org/licenses/>.
}] */
package jpen.demo;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import jpen.event.PenAdapter;
import jpen.PButton;
import jpen.PButtonEvent;
import jpen.PenManager;
import jpen.PKind;
import jpen.PLevel;
import jpen.PLevelEvent;

class PenCanvas
	extends JComponent {
	public static final long serialVersionUID=1l;
	private static final Logger L=Logger.getLogger(PenCanvas.class.getName());
	{
		//L.setLevel(Level.ALL);
	}

	private static final Dimension SIZE=new Dimension(1000,1000);
	private static final Color BACKGROUND_COLOR=new Color(247, 217, 186); // yellish
	private static final float CURSOR_DIAM=2;
	private static final float STROKE_RAD=30f;
	private static final Dimension PREF_SCROLLPANE_SIZE=new Dimension(230,230);

	final PenManager penManager;
	final JScrollPane scrollPane;

	private final BufferedImage image=GraphicsEnvironment.getLocalGraphicsEnvironment().
	    getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(SIZE.width, SIZE.height);
	private final Graphics2D g=(Graphics2D)image.getGraphics();
	private final Point2D.Float cursorCenter=new Point2D.Float();
	private final Ellipse2D.Float stroke=new Ellipse2D.Float();
	private final Rectangle dirtyArea=new Rectangle();
	private boolean isDirty;

	private final Rectangle rectangle=new Rectangle();

	PenCanvas() {
		this.penManager=new PenManager(this);
		penManager.pen.setFirePenTockOnSwing(true);
		penManager.pen.setFrequency(40);
		Utils.freezeSize(this, SIZE);
		setDoubleBuffered(false);
		setOpaque(false);
		scrollPane=new JScrollPane(this);
		scrollPane.setPreferredSize(PREF_SCROLLPANE_SIZE);

		image.setAccelerationPriority(1);

		g.setColor(BACKGROUND_COLOR);
		rectangle.setFrame(0, 0, SIZE.width, SIZE.height);
		g.fill(rectangle);
		setupRenderingHints(g);

		penManager.pen.addListener(new PenAdapter() {
			    @Override
			    public void penLevelEvent(PLevelEvent ev) {
				    for(PLevel level:ev.levels) {
					    PLevel.Type levelType=level.getType();
					    switch(levelType) {
					    case X:
						    cursorCenter.x=level.value;
						    break;
					    case Y:
						    cursorCenter.y=level.value;
						    break;
					    }
				    }
				    paintStroke();
			    }
			    @Override
			    public void penButtonEvent(PButtonEvent ev){
				    if(ev.button.getType()==PButton.Type.LEFT)
					    paintStroke();
			    }
			    @Override
			    public void penTock(long availableTime) {
				    if(availableTime<0)
					    L.warning("no available time to repaint... but this is a test, so I continue.");
				    if(isDirty)
					    repaint(dirtyArea.x, dirtyArea.y, dirtyArea.width, dirtyArea.height);
				    isDirty=false;
			    }
		    });
	}

	private static void setupRenderingHints(Graphics2D g){
		g.setRenderingHint(RenderingHints.KEY_RENDERING,
		    RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
		    RenderingHints.VALUE_STROKE_PURE);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		    RenderingHints.VALUE_ANTIALIAS_ON);
	}

	private synchronized void paintStroke() {
		if(!penManager.pen.getButtonValue(PButton.Type.LEFT)){
			L.fine("left button is not pressed");
			return ;
		}

		PKind.Type kindType=penManager.pen.getKind().getType();
		float r=kindType.equals(PKind.Type.CURSOR)? 0.5f: penManager.pen.getLevelValue(PLevel.Type.PRESSURE);
		r*=r*STROKE_RAD;
		if(r==0){
			L.fine("no pressure");
			return ;
		}
		switch(kindType) {
		case STYLUS:
			g.setColor(Color.BLACK);
			break;
		case ERASER:
			g.setColor(BACKGROUND_COLOR);
			break;
		case CURSOR:
			g.setColor(Color.BLUE);
		}
		stroke.x=cursorCenter.x-r;
		stroke.y=cursorCenter.y-r;
		stroke.width=stroke.height=2*r;
		g.fill(stroke);
		L.fine("stoke painted");
		addCursorAreaToDirtyArea();
	}

	private void addCursorAreaToDirtyArea() {
		evalCursorArea(rectangle);
		if(isDirty)
			dirtyArea.add(rectangle);
		else
			dirtyArea.setRect(rectangle);
		isDirty=true;
	}

	private void evalCursorArea(Rectangle r) {
		float max=2*STROKE_RAD+2;
		r.x=(int)(cursorCenter.x-max);
		r.y=(int)(cursorCenter.y-max);
		r.width=r.height=(int)(2*max);
	}

	@Override
	protected synchronized void paintComponent(Graphics g) {
		Graphics2D g2=(Graphics2D)g;
		g2.drawImage(image, null, null);
	}

}
