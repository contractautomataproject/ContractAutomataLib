package io.github.contractautomataproject.catlib.automaton.label;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import io.github.contractautomataproject.catlib.automaton.label.action.Action;
import io.github.contractautomataproject.catlib.automaton.label.action.IdleAction;
import io.github.contractautomataproject.catlib.automaton.label.action.OfferAction;
import io.github.contractautomataproject.catlib.automaton.label.action.RequestAction;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CALabelTest {

	CALabel match;
	CALabel offer;
	CALabel request;
	
	@Before
	public void setup() {
		match= new CALabel(List.of(new IdleAction(), new OfferAction("a"), new RequestAction("a")));
		offer= new CALabel(List.of(new IdleAction(), new OfferAction("a"), new IdleAction()));
		request= new CALabel(List.of(new IdleAction(), new RequestAction("a"), new IdleAction()));
	}
	
	@After
	public void teardown() {
		match = null;
		offer = null;
		request = null;
	}

	@Test
	public void testConstructor1OfferEquals() {
		assertEquals(new CALabel(3,1,new OfferAction("a")),offer);
	}
	
	@Test
	public void testConstructor1EqualsActionLength() {
		assertEquals(new CALabel(3,1,new OfferAction("test")),new CALabel(3,1,new OfferAction("test")));
	}

	@Test
	public void testConstructor1RequestEquals() {
		assertEquals(new CALabel(3,1,new RequestAction("a")),request);
	}
//
//	@Test
//	public void testConstructor2EqualsActionLength() {
//		assertEquals(new CALabel(3,1,2,new OfferAction("test"), new RequestAction("test")),new CALabel(3,1,2,new OfferAction("test"), new RequestAction("test")));
//	}
//
//	@Test
//	public void testConstructor2MatchEquals() {
//		assertEquals(new CALabel(3,1,2,new OfferAction("a"), new RequestAction("a")),match);
//	}
//
//	@Test
//	public void testConstructor2MatchEquals2() {
//		assertEquals(new CALabel(3,2,1,new RequestAction("a"), new OfferAction("a")),match);
//	}
	
	@Test
	public void testConstructor3EqualsActionLength() {
		assertEquals(new CALabel(List.of(new IdleAction(), new OfferAction("test"), new IdleAction())),
				new CALabel(List.of(new IdleAction(), new OfferAction("test"), new IdleAction())));
	}
	
	
	@Test
	public void testConstructor3OfferEquals() {
		assertEquals(new CALabel(List.of(new IdleAction(), new OfferAction("a"), new IdleAction())),offer);
	}

	@Test
	public void testConstructor3RequestEquals() {
		assertEquals(new CALabel(List.of(new IdleAction(), new RequestAction("a"), new IdleAction())),request);
	}

	@Test
	public void testConstructor3MatchEquals() {
		assertEquals(new CALabel(List.of(new IdleAction(), new OfferAction("a"), new RequestAction("a"))),match);
	}

//	@Test
//	public void testConstructor4EqualsActionLength() {
//		CALabel test = new CALabel(List.of(new IdleAction(), new IdleAction(), new OfferAction("test"), new RequestAction("test")));
//		CALabel test2 =  new CALabel(List.of(new IdleAction(), new OfferAction("test"), new RequestAction("test")));
//		assertEquals(test, new CALabel(test2,4,1));
//
//	}
//
//	@Test
//	public void testConstructor4OfferEquals() {
//		CALabel test = new CALabel(List.of(new IdleAction(), new IdleAction(), new OfferAction("a"), new IdleAction(), new IdleAction()));
//		assertEquals(test, new CALabel(offer,5,1));
//	}
//
//	@Test
//	public void testConstructor4RequestEquals() {
//		CALabel test = new CALabel(List.of(new IdleAction(), new IdleAction(), new RequestAction("a"), new IdleAction(), new IdleAction()));
//		assertEquals(test, new CALabel(request,5,1));
//	}
//
//	@Test
//	public void testConstructor4MatchEquals() {
//		CALabel test = new CALabel(List.of(new IdleAction(), new IdleAction(), new OfferAction("a"), new RequestAction("a"), new IdleAction()));
//		assertEquals(test, new CALabel(match,5,1));
//	}
//
//	@Test
//	public void testConstructor4MatchEquals2() {
//		CALabel test = new CALabel(List.of(new IdleAction(), new IdleAction(), new OfferAction("a"), new RequestAction("a")));
//		assertEquals(test, new CALabel(match,4,1));
//	}
	
	@Test
	public void testGetActionOffer() {
		assertTrue(offer.getPrincipalAction() instanceof OfferAction);
	}
	
	@Test
	public void testGetActionRequest() {
		assertTrue(request.getPrincipalAction() instanceof RequestAction);
	}
	
	@Test
	public void testGetActionMatch() {
		assertTrue(match.getPrincipalAction() instanceof OfferAction);
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
		assertEquals(new RequestAction("a"),offer.getCoAction());
	}
	
	@Test
	public void testGetCoActionRequest() {
		assertEquals(new OfferAction("a"),request.getCoAction());
	}
	
	@Test
	public void testGetCoActionMatch() {
		assertEquals(new RequestAction("a"),match.getCoAction());
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
	public void testHashCode() {
		assertEquals(new CALabel(3,1,new OfferAction("a")).hashCode(),offer.hashCode());
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
	
	//********************** testing exceptions *********************
	
	@Test
	public void testConstructor1_Exception_nullArgument() {
		Assert.assertThrows(NullPointerException.class, ()->new CALabel(1,0,new OfferAction(null)));
	}
	
	@Test
	public void testConstructor1Exception_rankZero() {
		Assert.assertThrows(IllegalArgumentException.class, ()->new CALabel(0,0, new OfferAction("a")));
	}
	
	@Test
	public void testConstructor1Exception_actionLength() {
		Assert.assertThrows(IllegalArgumentException.class, ()->new CALabel(1,0,new Action("a")));
	}
	
	@Test
	public void testConstructor1Exception_principalGreaterOrEqualRank() {
		Assert.assertThrows(IllegalArgumentException.class, ()->new CALabel(1,1,new OfferAction("a")));
	}
	
	@Test
	public void testConstructor1Exception_notRequestNorOffer() {
		Assert.assertThrows(IllegalArgumentException.class, ()->new CALabel(1,0,new Action("aa")));
	}

	@Test
	public void testConstructor1Exception_notRequestNorOffer2() {
		Assert.assertThrows("The action is not a request nor an offer",IllegalArgumentException.class, ()->new CALabel(1,0, new IdleAction()));
	}

//	@Test
//	public void testConstructor2Exception_nullArgument1() {
//		Assert.assertThrows(NullPointerException.class, ()->new CALabel(2,0,1,null, new OfferAction("a")));
//	}
//
//	@Test
//	public void testConstructor2Exception_nullArgument2() {
//		Assert.assertThrows(NullPointerException.class, ()->new CALabel(2,0,1,new OfferAction("a"),null));
//	}
//
//	@Test
//	public void testConstructor2Exception_rankZero() {
//		Assert.assertThrows(IllegalArgumentException.class, ()->new CALabel(0,0,1,new OfferAction("a"),new RequestAction("a")));
//	}
//
//	@Test
//	public void testConstructor2Exception_actionLength1() {
//		Assert.assertThrows(IllegalArgumentException.class, ()->new CALabel(2,0,1,new Action("a"),new RequestAction("a")));
//	}
//
//	@Test
//	public void testConstructor2Exception_actionLength2() {
//		Assert.assertThrows(IllegalArgumentException.class, ()->new CALabel(2,0,1,new RequestAction("a"),new Action("a")));
//	}
//
//	@Test
//	public void testConstructor2Exception_twoOffers() {
//		Assert.assertThrows("The action must be an offer and a request", IllegalArgumentException.class, ()->new CALabel(2,0,1,new OfferAction("a"),new OfferAction("a")));
//	}
//
//	@Test
//	public void testConstructor2Exception_twoRequests() {
//		Assert.assertThrows("The action must be an offer and a request", IllegalArgumentException.class, ()->new CALabel(2,0,1,new RequestAction("a"),new RequestAction("a")));
//	}
//
//	@Test
//	public void testConstructor2Exception_principal1GreaterOrEqualRank() {
//		Assert.assertThrows(IllegalArgumentException.class, ()->new CALabel(2,2,1,new OfferAction("a"),new RequestAction("a")));
//	}
//
//	@Test
//	public void testConstructor2Exception_principal2GreaterOrEqualRank() {
//		Assert.assertThrows(IllegalArgumentException.class, ()->new CALabel(2,0,2,new OfferAction("a"),new RequestAction("a")));
//	}
	
	@Test
	public void testConstructor3Exception_Empty() {
		List<Action> l = new ArrayList<>();
		Assert.assertThrows(IllegalArgumentException.class, ()->new CALabel(l));
	}
	

	@Test
	public void testConstructor3Exception_emptyLabel() {
		List<Action> arg = new ArrayList<>();
		Assert.assertThrows(IllegalArgumentException.class, ()->new CALabel(arg));
	}
	
	@Test
	public void testConstructor3Exception_nullReferencesLabel() {
		List<Action> l = new ArrayList<>();
		l.add(null);
		Assert.assertThrows("The label is not well-formed", IllegalArgumentException.class, ()->new CALabel(l));
	}

	@Test
	public void testConstructor3Exception_notWellFormedLabel() {
		List<Action> l = new ArrayList<>();
		l.add(new Action("aaa"));
		Assert.assertThrows(IllegalArgumentException.class, ()->new CALabel(l));
	}
		
	@Test
	public void testConstructor3Exception_notWellFormedIdleLabel() {
		List<Action> l = new ArrayList<>();
		l.add(new IdleAction());
		Assert.assertThrows(IllegalArgumentException.class, ()->new CALabel(l));
	}
	
	@Test
	public void testConstructor3Exception_notWellFormedOffersLabel() {
		List<Action> l = new ArrayList<>();
		l.add(new OfferAction("a"));
		l.add(new OfferAction("a"));
		Assert.assertThrows("The label is not well-formed", IllegalArgumentException.class, ()->new CALabel(l));
	}
	
	@Test
	public void testConstructor3Exception_notWellFormedRequestsLabel() {
		List<Action> l = List.of(new RequestAction("a"),new RequestAction("a"));
		Assert.assertThrows("The label is not well-formed", IllegalArgumentException.class, ()->new CALabel(l));
	}
	
//	@Test
//	public void testConstructor4ExceptionNullShift() {
//		CALabel ca = new CALabel(1,0,new OfferAction("a"));
//		Assert.assertThrows(IllegalArgumentException.class, ()->new CALabel(ca,2,null));
//	}
//
//
//	@Test
//	public void testConstructor4ExceptionNullRank() {
//		CALabel ca = new CALabel(1,0,new OfferAction("a"));
//		Assert.assertThrows(IllegalArgumentException.class, ()->new CALabel(ca,null,1));
//	}
//
//	@Test
//	public void testConstructor4ExceptionNullLabel() {
//		Assert.assertThrows(IllegalArgumentException.class, ()->new CALabel(null,2,1));
//	}
//
//	@Test
//	public void testConstructor4ExceptionNegativeRank() {
//		CALabel ca = new CALabel(1,0,new OfferAction("a"));
//		Assert.assertThrows(IllegalArgumentException.class, ()->new CALabel(ca,-1,1));
//	}
//
//
//	@Test
//	public void testConstructor4ExceptionNegativeShift() {
//		CALabel ca = new CALabel(1,0,new OfferAction("a"));
//		Assert.assertThrows(IllegalArgumentException.class, ()->new CALabel(ca,2,-2));
//
//	}
//
//	@Test
//	public void testConstructor4ExceptionOffererShiftGreaterRank() {
//		CALabel ca = new CALabel(2,1,new OfferAction("a"));
//		Assert.assertThrows(IllegalArgumentException.class, ()->new CALabel(ca,3,2));
//	}
//
//	@Test
//	public void testConstructor4ExceptionRequestShiftGreaterRank() {
//		CALabel ca = new CALabel(2,1,new RequestAction("a"));
//		Assert.assertThrows(IllegalArgumentException.class, ()->new CALabel(ca,3,2));
//	}
	
	/// ----------------------------
	
	
	@Test
	public void testGetOffererException() {
		CALabel cl = new CALabel(1,0,new RequestAction("a"));		
		Assert.assertThrows(UnsupportedOperationException.class, cl::getOfferer);
	}
	
	@Test
	public void testGetRequesterException() {
		CALabel cl = new CALabel(1,0,new OfferAction("a"));
		Assert.assertThrows(UnsupportedOperationException.class, cl::getRequester);
	}
	
	@Test
	public void testGetOffererOrRequesterTestException() {		
		Assert.assertThrows("Action is not a request nor an offer",UnsupportedOperationException.class, ()->match.getOffererOrRequester());
	}
	
	@Test
	public void testMatchException() {
		Label<Action> l  = new Label<>(List.of(new Action("ei")));
		CALabel ca = new CALabel(1,0,new OfferAction("a"));
		Assert.assertThrows(IllegalArgumentException.class, ()->ca.match(l));
	}
	
}
