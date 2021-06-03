package contractAutomata;

import family.Product;

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
		if (aut==null)
			throw new IllegalArgumentException();
		this.aut=aut;
	}

	/**
	 * @return the synthesised orchestration/mpc of product p in agreement
	 * 
	 */
	public MSCA orchestration(Product p)
	{
		aut=aut.synthesis( (x,t,bad) -> 
		x.getLabel().isRequest()||bad.contains(x.getTarget())||p.isForbidden(x), 
		(x,t,bad) -> (!x.isUrgent()&&x.isUncontrollableOrchestration(t, bad))||(x.isUrgent()&&!t.contains(x)));

		if (aut!=null&&!p.checkRequired(aut.getTransition()))
			aut=null;
		
		return aut;
	}


	public MSCA getAut() {
		return aut;
	}
}
