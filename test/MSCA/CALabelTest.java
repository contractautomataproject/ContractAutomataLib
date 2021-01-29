package MSCA;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import contractAutomata.CALabel;

public class CALabelTest {

	@Test
	public void constructorTest() {
		List<String> lab = new ArrayList<>();
		lab.add(CALabel.idle);
		lab.add(CALabel.offer+"a");
		lab.add(CALabel.request+"a");
		CALabel calab= new CALabel(lab);
		
		assert(calab.getAction().startsWith(CALabel.offer));
	}

}
