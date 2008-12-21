package jpen.test;

import javax.swing.JComponent;
import javax.swing.JPanel;
import jpen.PenManager;
import jpen.PenProvider;
import junit.framework.TestCase;

public class SmokeTest extends TestCase {

	public void testPenManager() throws Exception {
		JComponent component = new JPanel();
		PenManager penManager = new PenManager(component);
		System.out.println("Providers:");
		int count=0;
		for(PenProvider.Constructor constructor: penManager.getConstructors()){
			System.out.println("Constructor: "+constructor.getName());
			count++;
			PenProvider.ConstructionException ex=constructor.getConstructionException();
			if(ex!=null){
				ex.printStackTrace();
				fail(ex.getMessage());
			}
		}
		assertTrue(count>=2);
	}
	
}