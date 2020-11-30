package FMCA;

import MSCA.MSCA;

/**
 * Featured Modal Contract Automata
 * 
 * decorate MSCA with a new orchestration synthesis for a product configuration
 * 
 * @author Davide Basile
 *
 */
public class FMCA {
	private MSCA aut;
	public FMCA(MSCA aut)
	{
		this.aut=aut;
	}
	

	/**
	 * @return the synthesised orchestration/mpc of product p in agreement
	 * 
	 */
	public MSCA orchestration(Product p)
	{
		aut.synthesis( (x,t,bad) -> 
		x.getLabel().isRequest()||p.isForbidden(x)||bad.contains(x.getTarget()), 
		(x,t,bad) -> bad.contains(x.getTarget())&&x.isUncontrollableOrchestration(t, bad));

		if (aut==null||!p.checkRequired(aut.getTransition()))
			aut=null;
		
		return aut;
	}


	public MSCA getAut() {
		return aut;
	}
}
