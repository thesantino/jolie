package org.teavm.classlib.java.security;

import org.teavm.classlib.java.lang.TIllegalArgumentException;

public class TInvalidParameterException extends TIllegalArgumentException {
	public TInvalidParameterException() {
		super();
	}

	public TInvalidParameterException( String message ) {
		super( message );
	}
}
