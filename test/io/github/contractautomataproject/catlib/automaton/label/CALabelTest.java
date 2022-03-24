package io.github.contractautomataproject.catlib.automaton.label;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.github.contractautomataproject.catlib.automaton.label.action.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.Strict.class)
public class CALabelTest {


	@Mock IdleAction ia;
	@Mock OfferAction oa;
	@Mock RequestAction ra;
	@Mock Action act;
	@Mock AddressedOfferAction aoa;
	@Mock AddressedRequestAction ara;
	@Mock Address adr;

	CALabel match;
	CALabel offer;
	CALabel request;
	CALabel addressedOffer;
	CALabel addressedRequest;

	@Before
	public void setup() {
		ia = mock(IdleAction.class);
		oa = mock(OfferAction.class);
		ra = mock(RequestAction.class);

		when(ia.toString()).thenReturn("-");
		when(ra.toString()).thenReturn("?test");
		when(oa.toString()).thenReturn("!test");

		when(ra.getLabel()).thenReturn("test");
		when(oa.getLabel()).thenReturn("test");

		when(ara.getLabel()).thenReturn("test");
		when(aoa.getLabel()).thenReturn("test");

		when(oa.match(ra)).thenReturn(true);
		when(ra.match(oa)).thenReturn(true);

		when(ara.getAddress()).thenReturn(adr);
		when(aoa.getAddress()).thenReturn(adr);


		match= new CALabel(Arrays.asList(ia, oa, ra));
		offer= new CALabel(Arrays.asList(ia, oa, ia));
		request= new CALabel(Arrays.asList(ia, ra, ia));

		addressedOffer= new CALabel(Arrays.asList(ia, aoa, ia));
		addressedRequest= new CALabel(Arrays.asList(ia, ara, ia));
	}
	
	@After
	public void teardown() {
		match = null;
		offer = null;
		request = null;
	}

	@Test
	public void testConstructor1OfferEquals() {
		assertEquals(new CALabel(3,1,oa).toString(),offer.toString());
	}
	
	@Test
	public void testConstructor1EqualsActionLength() {
		assertEquals(new CALabel(3,1,oa),new CALabel(3,1,oa));
	}

	@Test
	public void testConstructor1RequestEquals() {
		assertEquals(new CALabel(3,1,ra).toString(),request.toString());
	}
	
	@Test
	public void testConstructor3EqualsActionLength() {
		assertEquals(new CALabel(List.of(ia, oa, ia)),
				new CALabel(List.of(ia, oa, ia)));
	}
	
	
	@Test
	public void testConstructor3OfferEquals() {
		assertEquals(new CALabel(List.of(ia, oa, ia)),offer);
	}

	@Test
	public void testConstructor3RequestEquals() {
		assertEquals(new CALabel(List.of(ia, ra, ia)),request);
	}

	@Test
	public void testConstructor3MatchEquals() {
		assertEquals(new CALabel(List.of(ia, oa, ra)),match);
	}
	
	@Test
	public void testGetActionOffer() {
		assertTrue(offer.getAction() instanceof OfferAction);
	}
	
	@Test
	public void testGetActionRequest() {
		assertTrue(request.getAction() instanceof RequestAction);
	}
	
	@Test
	public void testGetActionMatch() {
		assertTrue(match.getAction() instanceof OfferAction);
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
		assertEquals(ra.toString(),offer.getCoAction().toString());
	}
	
	@Test
	public void testGetCoActionAddressedOfferLabel() {
		assertEquals(ara.getLabel(),addressedOffer.getCoAction().getLabel());
	}

	@Test
	public void testGetCoActionAddressedOfferAddress() {
		assertEquals(ara.getAddress(),((AddressedAction)addressedOffer.getCoAction()).getAddress());
	}

	@Test
	public void testGetCoActionAddressedOfferClass() {
		assertEquals(AddressedRequestAction.class,addressedOffer.getCoAction().getClass());
	}

	@Test
	public void testGetCoActionAddressedRequestLabel() {
		assertEquals(aoa.getLabel(),addressedRequest.getCoAction().getLabel());
	}

	@Test
	public void testGetCoActionAddressedRequestAddress() {
		assertEquals(aoa.getAddress(),((AddressedAction)addressedRequest.getCoAction()).getAddress());
	}

	@Test
	public void testGetCoActionAddressedRequestClass() {
		assertEquals(AddressedOfferAction.class,addressedRequest.getCoAction().getClass());
	}

	@Test
	public void testGetCoActionRequest() {
		assertEquals(oa.toString(),request.getCoAction().toString());
	}
	
	@Test
	public void testGetCoActionMatch() {
		assertEquals(ra.toString(),match.getCoAction().toString());
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
	public void testMatchTrueOfferRequest() {
		assertTrue(offer.match(request));
	}
	
	@Test
	public void testMatchTrueRequestOffer() {
		assertTrue(request.match(offer));
	}
	
	@Test
	public void testMatchFalseMatch() {
		assertFalse(match.match(offer));
	}

	@Test
	public void testMatchFalseMatchArg() {
		assertFalse(offer.match(match));
	}

	@Test
	public void testMatchFalseActionTypeOffer() {
		assertFalse(offer.match(offer));
	}

	@Test
	public void testMatchFalseActionTypeRequest() {
		assertFalse(request.match(request));
	}



	@Test
	public void testMatchFalseRequestOffer() {
		when(ra.match(oa)).thenReturn(false);
		assertFalse(request.match(offer));
	}


	@Test
	public void testMatchFalseOfferRequest() {
		when(oa.match(ra)).thenReturn(false);
		assertFalse(offer.match(request));
	}


	//********************** testing exceptions *********************

	
	@Test
	public void testConstructor1Exception_rankZero() {
		Assert.assertThrows(IllegalArgumentException.class, ()->new CALabel(0,0, oa));
	}
	
	@Test
	public void testConstructor1Exception_principalGreaterOrEqualRank() {
		Assert.assertThrows(IllegalArgumentException.class, ()->new CALabel(1,1,oa));
	}
	
	@Test
	public void testConstructor1Exception_notRequestNorOffer() {
		Assert.assertThrows(IllegalArgumentException.class, ()->new CALabel(1,0,act));
	}

	@Test
	public void testConstructor1Exception_notRequestNorOffer2() {
		Assert.assertThrows("The action is not a request nor an offer",IllegalArgumentException.class, ()->new CALabel(1,0, ia));
	}

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
		l.add(act);
		Assert.assertThrows(IllegalArgumentException.class, ()->new CALabel(l));
	}
		
	@Test
	public void testConstructor3Exception_notWellFormedIdleLabel() {
		List<Action> l = new ArrayList<>();
		l.add(ia);
		Assert.assertThrows(IllegalArgumentException.class, ()->new CALabel(l));
	}
	
	@Test
	public void testConstructor3Exception_notWellFormedOffersLabel() {
		List<Action> l = new ArrayList<>();
		l.add(oa);
		l.add(oa);
		Assert.assertThrows("The label is not well-formed", IllegalArgumentException.class, ()->new CALabel(l));
	}
	
	@Test
	public void testConstructor3Exception_notWellFormedRequestsLabel() {
		List<Action> l = List.of(ra,ra);
		Assert.assertThrows("The label is not well-formed", IllegalArgumentException.class, ()->new CALabel(l));
	}
	

	/// ----------------------------
	
	
	@Test
	public void testGetOffererException() {
		CALabel cl = new CALabel(1,0,ra);		
		Assert.assertThrows(UnsupportedOperationException.class, cl::getOfferer);
	}
	
	@Test
	public void testGetRequesterException() {
		CALabel cl = new CALabel(1,0,oa);
		Assert.assertThrows(UnsupportedOperationException.class, cl::getRequester);
	}
	
	@Test
	public void testGetOffererOrRequesterTestException() {		
		Assert.assertThrows("Action is not a request nor an offer",UnsupportedOperationException.class, ()->match.getOffererOrRequester());
	}
	
	@Test
	public void testMatchException() {
		Label<Action> l  = new Label<>(Collections.singletonList(act));
		CALabel ca = new CALabel(1,0,oa);
		Assert.assertThrows(IllegalArgumentException.class, ()->ca.match(l));
	}
	
}
