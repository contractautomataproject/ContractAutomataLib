package FMCA;


import CA.CAState;
import MSCA.MSCATransition;



/**
 * Transition of a featured modal contract automaton
 * 
 * @author Davide Basile
 *
 */
public class FMCATransition extends MSCATransition { 
	
	public FMCATransition(CAState source, Integer p1, String action, Integer p2, CAState target, action type) {
		super(source, p1, action, p2, target, type);
	}

	public FMCATransition(CAState caState, String[] lab, CAState caState2, action permitted) {
		super(caState, lab, caState2, permitted);
	}

	boolean isForbidden(Product p)
	{
		return (FMCAUtils.getIndex(p.getForbidden(),this.getUnsignedAction())>=0);
	}

	boolean isRequired(Product p)
	{
		return (FMCAUtils.getIndex(p.getRequired(),this.getUnsignedAction())>=0);		
	}

	
}