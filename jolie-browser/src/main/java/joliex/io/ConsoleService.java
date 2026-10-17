package joliex.io;

import jolie.net.CommMessage;
import jolie.runtime.JavaService;

public class ConsoleService extends JavaService {
	@Override
	public CommMessage callOperation( CommMessage m ) {
		switch( m.operationName() ) {
		case "println":
			jolie.compat.Output.write( m.value().strValue() + "\n" );
			break;
		case "print":
			jolie.compat.Output.write( m.value().strValue() );
			break;
		default:
			break;
		}
		return CommMessage.createEmptyResponse( m );
	}
}
