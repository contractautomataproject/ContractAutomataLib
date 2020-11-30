package CA;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 
 * @author Davide Basile
 *
 */
public class CALabel {

	private final Integer rank;
	private Integer offerer;
	private Integer requester;
	private String action; //convention: in case of match, the action is always the offer

	final public static String idle="-";
	final public static String offer="!";
	final public static String request="?";

	public CALabel(Integer rank, Integer principal, String action) {
		super();
		if (rank==null||principal==null||action==null)
			throw new IllegalArgumentException("Null argument");
		
		if (action.startsWith(offer))
		{
			this.offerer=principal;
		}
		else if (action.startsWith(request))
		{
			this.requester=principal;
		}
		else
			throw new IllegalArgumentException("The action is not a request nor an offer");

		this.rank = rank;
		this.action = action;
	}

	public CALabel(Integer rank, Integer offerer, Integer requester, String offeraction) {
		super();
		if (rank==null||offerer==null||requester==null||offeraction==null)
			throw new IllegalArgumentException("Null argument");
		
		if (!offeraction.startsWith(CALabel.offer))
			throw new IllegalArgumentException("Bug: this constructor is only for matches and by convention action is the offer");

		this.rank = rank;
		this.action = offeraction;
		this.offerer = offerer;
		this.requester = requester;
	}

	public CALabel(List<String> label)
	{
		this.rank = label.size();
		Map<Integer, String> map = IntStream.range(0, rank)
				.filter(i->!label.get(i).equals(idle))
				.mapToObj(i->i)
				.collect(Collectors.toMap(Function.identity(), label::get));

		if (map.size()!=1 && map.size()!=2)
			throw new IllegalArgumentException("the action is not a request nor an offer nor a match \n"+label+"\n"+map.toString());

		map.entrySet().forEach(e->{
			if (e.getValue().startsWith(offer))
			{
				this.offerer=e.getKey();
				this.action=e.getValue();//convention on match
			}
			else if (e.getValue().startsWith(request))
			{
				this.requester=e.getKey();
				if (map.size()==1) //not a match
					this.action=e.getValue();
			}
			else
				throw new IllegalArgumentException("the action is not well-formed");
		});

		if ((map.size()==2)&&(offerer==null||requester==null||!action.startsWith(offer)))
				throw new IllegalArgumentException("the action is not well-formed");
	}
	
	public Integer getRank() {
		return rank;
	}


	public Integer getOfferer() {
		if (!this.isRequest())
			return offerer;
		else 
			throw new UnsupportedOperationException("No offerer in a request action");
	}

	public Integer getRequester() {
		if (!this.isOffer())
			return requester;
		else
			throw new UnsupportedOperationException("No requester in an offer action");
	}
	
	public Integer getOffererOrRequester() {
		if (this.isOffer()) 
			return this.getOfferer();
		else if (this.isRequest())
			return this.getRequester();
		else
			throw new UnsupportedOperationException("Action is not a request nor an offer");
	}
	
	public String getAction() {
		return action;
	}

	public String getCoAction()
	{
		if (action.startsWith(offer))
			return request+action.substring(1);
		else if (action.startsWith(request))
			return offer+action.substring(1);
		else
			throw new IllegalArgumentException("The label is not an action");
	}

	public List<String> getLabelAsList(){
		if (!this.isMatch())
		{
			return IntStream.range(0, rank)
					.mapToObj(i->((this.isOffer()&&i==offerer)
								||(this.isRequest()&&i==requester))?action:idle)
					.collect(Collectors.toList());
		}
		else
		{
			return IntStream.range(0, rank)
					.mapToObj(i->(i==offerer)?action:(i==requester)?this.getCoAction():idle)
					.collect(Collectors.toList());
		}		
	}


	public boolean isMatch()
	{
		return this.offerer!=null && this.requester!=null && this.action.startsWith(offer);
	}

	public boolean isOffer()
	{
		return this.offerer!=null && this.requester==null && this.action.startsWith(offer);
	}

	public boolean isRequest()
	{
		return this.offerer==null && this.requester!=null && this.action.startsWith(request);
	}
	
	public static boolean match(CALabel l1,CALabel l2)
	{
		if (l1.isMatch()||l2.isMatch())	//both transitions must not be match (non-associative)
			return false;
		if (l1.isOffer()&&l2.isOffer())
			return false;
		if (l1.isRequest()&&l2.isRequest())
			return false;

		return l1.getAction().substring(1).equals(l2.getAction().substring(1));
	}

	public String getUnsignedAction()
	{
		String action=this.getAction();
		if (action.startsWith(offer)||action.startsWith(request))
			return action.substring(1);
		else
			throw new RuntimeException("Bug: irregular label action"); //label;
	}

	public static String getUnsignedAction(String action)
	{
		if (action.startsWith(offer)||action.startsWith(request))
			return action.substring(1);
		else
			return action;
	}


	@Override
	public CALabel clone() {
		if (!this.isMatch())
			return new CALabel(rank,(this.isOffer())?offerer:requester,action);
		else 
			return new CALabel(rank,offerer,requester,action);
	}
	
	@Override
	public String toString() {
		return this.getLabelAsList().toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + ((offerer == null) ? 0 : offerer.hashCode());
		result = prime * result + ((rank == null) ? 0 : rank.hashCode());
		result = prime * result + ((requester == null) ? 0 : requester.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CALabel other = (CALabel) obj;
		if (action == null) {
			if (other.action != null)
				return false;
		} else if (!action.equals(other.action))
			return false;
		if (offerer == null) {
			if (other.offerer != null)
				return false;
		} else if (!offerer.equals(other.offerer))
			return false;
		if (rank == null) {
			if (other.rank != null)
				return false;
		} else if (!rank.equals(other.rank))
			return false;
		if (requester == null) {
			if (other.requester != null)
				return false;
		} else if (!requester.equals(other.requester))
			return false;
		return true;
	}
	
	
}
