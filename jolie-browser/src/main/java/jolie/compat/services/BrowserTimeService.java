package jolie.compat.services;

import jolie.net.CommMessage;
import jolie.runtime.JavaService;
import jolie.runtime.Value;

public class BrowserTimeService extends JavaService {
	@Override
	public CommMessage callOperation( CommMessage m ) {
		switch( m.operationName() ) {
		case "sleep":
			try {
				Thread.sleep( m.value().intValue() );
			} catch( InterruptedException e ) {
				// woken early, just return
			}
			return CommMessage.createEmptyResponse( m );
		case "getCurrentTimeMillis":
			return CommMessage.createResponse( m, Value.create( System.currentTimeMillis() ) );
		default:
			return CommMessage.createEmptyResponse( m );
		}
	}
}
