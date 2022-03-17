package io.github.contractautomataproject.catlib.automaton.label;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CALabelTest {

	List<String> lab;
	CALabel match;
	CALabel offer;
	CALabel request;
	
	@Before
	public void setup() {
		lab = new ArrayList<>();
		match= new CALabel(List.of(CALabel.IDLE, CALabel.OFFER+"a", CALabel.REQUEST+"a"));
		offer= new CALabel(List.of(CALabel.IDLE, CALabel.OFFER+"a", CALabel.IDLE));
		request= new CALabel(List.of(CALabel.IDLE, CALabel.REQUEST+"a", CALabel.IDLE));
	}
	
	@After
	public void teardown() {
		lab = null;
		match = null;
		offer = null;
		request = null;
	}

	@Test
	public void testConstructor1OfferEquals() {
		assertEquals(new CALabel(3,1,CALabel.OFFER+"a"),offer);
	}

	@Test
	public void testConstructor1RequestEquals() {
		assertEquals(new CALabel(3,1,CALabel.REQUEST+"a"),request);
	}

	@Test
	public void testConstructor2MatchEquals() {
		assertEquals(new CALabel(3,1,2,CALabel.OFFER+"a", CALabel.REQUEST+"a"),match);
	}
	
	@Test
	public void testConstructor2MatchEquals2() {
		assertEquals(new CALabel(3,2,1,CALabel.REQUEST+"a", CALabel.OFFER+"a"),match);
	}
	
	@Test
	public void testConstructor3OfferEquals() {
		assertEquals(new CALabel(List.of(CALabel.IDLE, CALabel.OFFER+"a", CALabel.IDLE)),offer);
	}

	@Test
	public void testConstructor3RequestEquals() {
		assertEquals(new CALabel(List.of(CALabel.IDLE, CALabel.REQUEST+"a", CALabel.IDLE)),request);
	}

	@Test
	public void testConstructor3MatchEquals() {
		assertEquals(new CALabel(List.of(CALabel.IDLE, CALabel.OFFER+"a", CALabel.REQUEST+"a")),match);
	}

	@Test
	public void testConstructor4OfferEquals() {
		CALabel test = new CALabel(List.of(CALabel.IDLE, CALabel.IDLE, CALabel.OFFER+"a", CALabel.IDLE, CALabel.IDLE));
		assertEquals(test, new CALabel(offer,5,1));
	}
	
	@Test
	public void testConstructor4RequestEquals() {
		CALabel test = new CALabel(List.of(CALabel.IDLE, CALabel.IDLE, CALabel.REQUEST+"a", CALabel.IDLE, CALabel.IDLE));
		assertEquals(test, new CALabel(request,5,1));
	}
	
	@Test
	public void testConstructor4MatchEquals() {
		CALabel test = new CALabel(List.of(CALabel.IDLE, CALabel.IDLE, CALabel.OFFER+"a", CALabel.REQUEST+"a", CALabel.IDLE));
		assertEquals(test, new CALabel(match,5,1));
	}
	
	@Test
	public void testConstructor4MatchEquals2() {
		CALabel test = new CALabel(List.of(CALabel.IDLE, CALabel.IDLE, CALabel.OFFER+"a", CALabel.REQUEST+"a"));
		assertEquals(test, new CALabel(match,4,1));
	}
	
	@Test
	public void testGetActionOffer() {
		assertTrue(offer.getPrincipalAction().startsWith(CALabel.OFFER));
	}
	
	@Test
	public void testGetActionRequest() {
		assertTrue(request.getPrincipalAction().startsWith(CALabel.REQUEST));
	}
	
	@Test
	public void testGetActionMatch() {
		assertTrue(match.getPrincipalAction().startsWith(CALabel.OFFER));
	}
	
	@Test
	public void testGetRank() {
		assertEquals(3,match.getRank().intValue());
	}
	
	@Test
	public void testGetOfferer() {
		assertEquals(1,match.getOfferer().intValue());
	}
	
	@Test
	public void testGetRequester() {
		assertEquals(2,match.getRequester().intValue());
	}
	
	@Test
	public void testGetOffererOrRequestInOffer() {
		assertEquals(1,offer.getOffererOrRequester().intValue());
	}

	@Test
	public void testGetOffererOrRequestInRequest() {
		assertEquals(1,request.getOffererOrRequester().intValue());
	}
	
	@Test
	public void testGetCoActionOffer() {
		assertEquals(CALabel.REQUEST+"a",offer.getCoAction());
	}
	
	@Test
	public void testGetCoActionRequest() {
		assertEquals(CALabel.OFFER+"a",request.getCoAction());
	}
	
	@Test
	public void testGetCoActionMatch() {
		assertEquals(CALabel.REQUEST+"a",match.getCoAction());
	}

	@Test
	public void testIsMatchTrue() {
		assertTrue(match.isMatch());
	}
	
	@Test
	public void testIsMatchFalse() {
		assertFalse(offer.isMatch());
	}
	
	@Test
	public void testIsMatchFalse2() {
		assertFalse(request.isMatch());
	}
	
	@Test
	public void testIsOfferTrue() {
		assertTrue(offer.isOffer());
	}
	
	@Test
	public void testIsOfferFalse() {
		assertFalse(match.isOffer());
	}
	
	@Test
	public void testIsOfferFalse2() {
		assertFalse(request.isOffer());
	}

	@Test
	public void testIsRequestTrue() {
		assertTrue(request.isRequest());
	}
	
	@Test
	public void testIsRequestFalse() {
		assertFalse(match.isRequest());
	}
	
	@Test
	public void testIsRequestFalse2() {
		assertFalse(offer.isRequest());
	}
	
	@Test
	public void testMatchTrue() {
		assertTrue(offer.match(request));
	}
	
	@Test
	public void testMatchTrue2() {
		assertTrue(request.match(offer));
	}
	
	@Test
	public void testMatchFalse() {
		assertFalse(match.match(match));
	}
	
	@Test
	public void testMatchFalse2() {
		assertFalse(request.match(match));
	}
	
	@Test
	public void testMatchFalse3() {
		assertFalse(offer.match(offer));
	}
	
	@Test 
	public void testGetUnsignedAction() {
		assertEquals("a",match.getUnsignedAction());
	}
	
	@Test
	public void testHashCode() {
		assertEquals(new CALabel(3,1,CALabel.OFFER+"a").hashCode(),offer.hashCode());
	}
	
	@Test
	public void testHashCode2() {
		Assert.assertNotEquals(offer.hashCode(),request.hashCode());
	}
	
	@Test
	public void testNotEquals() {
		Assert.assertNotEquals(offer, request);
	}
	
	@Test
	public void testNotEquals2() {
		Assert.assertNotEquals(offer, match);
	}
	
	
	@Test
	public void testNotEqualsSuper() {
		Assert.assertNotEquals("test", offer);
	}
	
//	@Test
//	public void testToCSV() {
//		assertEquals("[rank=3, offerer=1, requester=2, actiontype=MATCH]",match.toCSV());
//	}
	
	
	//********************** testing exceptions *********************
	
	@Test
	public void testConstructor1_Exception_nullArgument() {
		assertThatThrownBy(() -> new CALabel(1,0,null))
	    .isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void testConstructor1Exception_rankZero() {
		assertThatThrownBy(() -> new CALabel(0,0,CALabel.OFFER+"a"))
	    .isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void testConstructor1Exception_actionLength() {
		assertThatThrownBy(() -> new CALabel(1,0,"a"))
	    .isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void testConstructor1Exception_principalGreaterOrEqualRank() {
		assertThatThrownBy(() -> new CALabel(1,1,CALabel.OFFER+"a"))
	    .isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void testConstructor1Exception_notRequestNorOffer() {
		assertThatThrownBy(() -> new CALabel(1,0,"aa"))
	    .isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void testConstructor2Exception_nullArgument1() {
		assertThatThrownBy(() -> new CALabel(2,0,1,null, CALabel.OFFER+"a"))
	    .isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void testConstructor2Exception_nullArgument2() {
		assertThatThrownBy(() -> new CALabel(2,0,1,CALabel.OFFER+"a",null))
	    .isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void testConstructor2Exception_rankZero() {
		assertThatThrownBy(() -> new CALabel(0,0,1,CALabel.OFFER+"a",CALabel.REQUEST+"a"))
	    .isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void testConstructor2Exception_actionLength1() {
		assertThatThrownBy(() -> new CALabel(2,0,1,"a",CALabel.REQUEST+"a"))
	    .isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testConstructor2Exception_actionLength2() {
		assertThatThrownBy(() -> new CALabel(2,0,1,CALabel.REQUEST+"a","a"))
	    .isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void testConstructor2Exception_twoOffers() {
		assertThatThrownBy(() -> new CALabel(2,0,1,CALabel.OFFER+"a",CALabel.OFFER+"a"))
	    .isInstanceOf(IllegalArgumentException.class)
	    .hasMessageContaining("The action must be an offer and a request");
	}
	
	@Test
	public void testConstructor2Exception_twoRequests() {
		assertThatThrownBy(() -> new CALabel(2,0,1,CALabel.REQUEST+"a",CALabel.REQUEST+"a"))
	    .isInstanceOf(IllegalArgumentException.class)
	    .hasMessageContaining("The action must be an offer and a request");
	}
	
	@Test
	public void testConstructor2Exception_principal1GreaterOrEqualRank() {
		assertThatThrownBy(() -> new CALabel(2,2,1,CALabel.OFFER+"a",CALabel.REQUEST+"a"))
	    .isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void testConstructor2Exception_principal2GreaterOrEqualRank() {
		assertThatThrownBy(() -> new CALabel(2,0,2,CALabel.OFFER+"a",CALabel.REQUEST+"a"))
	    .isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void testConstructor3Exception_Empty() {
		List<String> l = new ArrayList<String>();
		assertThatThrownBy(() -> new CALabel(l))
	    .isInstanceOf(IllegalArgumentException.class);
	}
	

	@Test
	public void testConstructor3Exception_emptyLabel() {
		List<String> arg = new ArrayList<String>();
		assertThatThrownBy(() -> new CALabel(arg))
	    .isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void testConstructor3Exception_nullReferencesLabel() {
		List<String> l = new ArrayList<String>();
		l.add(null);
		assertThatThrownBy(() -> new CALabel(l))
	    .isInstanceOf(IllegalArgumentException.class)
	    .hasMessageContaining("Label contains null references");
	}

	@Test
	public void testConstructor3Exception_notWellFormedLabel() {
		List<String> l = new ArrayList<String>();
		l.add("aaa");
		assertThatThrownBy(() -> new CALabel(l))
	    .isInstanceOf(IllegalArgumentException.class);
	}
		
	@Test
	public void testConstructor3Exception_notWellFormedIdleLabel() {
		List<String> l = new ArrayList<String>();
		l.add(CALabel.IDLE);
		assertThatThrownBy(() -> new CALabel(l))
	    .isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void testConstructor3Exception_notWellFormedOffersLabel() {
		List<String> l = new ArrayList<String>();
		l.add(CALabel.OFFER+"a");
		l.add(CALabel.OFFER+"a");
		assertThatThrownBy(() -> new CALabel(l))
	    .isInstanceOf(IllegalArgumentException.class)
	    .hasMessageContaining("The label is not well-formed");
	}
	
	@Test
	public void testConstructor3Exception_notWellFormedRequestsLabel() {
		List<String> l = List.of(CALabel.REQUEST+"a",CALabel.REQUEST+"a");
		assertThatThrownBy(() -> new CALabel(l))
	    .isInstanceOf(IllegalArgumentException.class)
	    .hasMessageContaining("The label is not well-formed");
	}
	
	@Test
	public void testConstructor4ExceptionNullShift() {
		CALabel ca = new CALabel(1,0,CALabel.OFFER+"a");
		assertThatThrownBy(() -> new CALabel(ca,2,null))
	    .isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void testConstructor4ExceptionNullRank() {
		CALabel ca = new CALabel(1,0,CALabel.OFFER+"a");
		assertThatThrownBy(() -> new CALabel(ca,null,1))
	    .isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void testConstructor4ExceptionNullLabel() {
		assertThatThrownBy(() -> new CALabel(null,2,1))
	    .isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void testConstructor4ExceptionNegativeRank() {
		CALabel ca = new CALabel(1,0,CALabel.OFFER+"a");
		assertThatThrownBy(() -> new CALabel(ca,-1,1))
	    .isInstanceOf(IllegalArgumentException.class);
	}
	

	@Test
	public void testConstructor4ExceptionNegativeShift() {
		CALabel ca = new CALabel(1,0,CALabel.OFFER+"a");
		assertThatThrownBy(() -> new CALabel(ca,2,-2))
	    .isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void testConstructor4ExceptionOffererShiftGreaterRank() {
		CALabel ca = new CALabel(2,1,CALabel.OFFER+"a");
		assertThatThrownBy(() -> new CALabel(ca,3,2))
	    .isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void testConstructor4ExceptionRequestShiftGreaterRank() {
		CALabel ca = new CALabel(2,1,CALabel.REQUEST+"a");
		assertThatThrownBy(() -> new CALabel(ca,3,2))
	    .isInstanceOf(IllegalArgumentException.class);
	}
	
	/// ----------------------------
	
	
	@Test
	public void testGetOffererException() {
		CALabel cl = new CALabel(1,0,CALabel.REQUEST+"a");
		assertThatThrownBy(() -> cl.getOfferer())
	    .isInstanceOf(UnsupportedOperationException.class);
	}
	
	@Test
	public void testGetRequesterException() {
		CALabel cl = new CALabel(1,0,CALabel.OFFER+"a");
		assertThatThrownBy(() -> cl.getRequester())
	    .isInstanceOf(UnsupportedOperationException.class);
	}
	
	@Test
	public void testGetOffererOrRequesterTestException() {
		assertThatThrownBy(() -> match.getOffererOrRequester())
	    .isInstanceOf(UnsupportedOperationException.class)
	    .hasMessageContaining("Action is not a request nor an offer");
	}
	
	@Test
	public void testMatchException() {
		Label<List<String>> l  = new Label<>(List.of("ei"));
		CALabel ca = new CALabel(1,0,CALabel.OFFER+"a");
		assertThatThrownBy(() -> ca.match(l))
	    .isInstanceOf(IllegalArgumentException.class);
	
	}
	
}
