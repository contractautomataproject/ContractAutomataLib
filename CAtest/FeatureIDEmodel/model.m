Composition : Hotel EconomyClient BusinessClient :: _Composition ;

Hotel : payment notification :: _Hotel ;

payment : card
	| cash ;

notification : [invoice] [receipt] :: _notification ;

EconomyClient : EconomyRequests :: _EconomyClient ;

EconomyRequests : EconomyBathroom EconomyRoom EconomyCancellation :: _EconomyRequests ;

EconomyBathroom : [sharedBathroom] :: _EconomyBathroom ;

EconomyRoom : [sharedRoom] [singleRoom2] :: _EconomyRoom ;

EconomyCancellation : [noFreeCancellation] :: _EconomyCancellation ;

BusinessClient : BusinessRequests :: _BusinessClient ;

BusinessRequests : BusinessBathroom BusinessRoom BusinessCancellation :: _BusinessRequests ;

BusinessBathroom : [privateBathroom] :: _BusinessBathroom ;

BusinessRoom : [singleRoom] :: _BusinessRoom ;

BusinessCancellation : [freeCancellation] :: _BusinessCancellation ;

%%

cash implies invoice ;
sharedRoom implies sharedBathroom ;
freeCancellation implies cash ;
singleRoom iff singleRoom2 ;

