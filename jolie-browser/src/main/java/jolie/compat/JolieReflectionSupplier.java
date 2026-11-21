package jolie.compat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.teavm.classlib.ReflectionContext;
import org.teavm.classlib.ReflectionSupplier;
import org.teavm.model.ClassReader;
import org.teavm.model.ClassReaderSource;
import org.teavm.model.MethodDescriptor;
import org.teavm.model.MethodReader;

public class JolieReflectionSupplier implements ReflectionSupplier {
	@Override
	public Collection< MethodDescriptor > getAccessibleMethods( ReflectionContext ctx, String className ) {
		ClassReaderSource src = ctx.getClassSource();
		if( !reflectable( src, className ) ) {
			return Collections.emptyList();
		}
		ClassReader cls = src.get( className );
		List< MethodDescriptor > result = new ArrayList<>();
		if( cls != null ) {
			for( MethodReader m : cls.getMethods() ) {
				if( !m.getName().startsWith( "<" ) && m.getLevel() == org.teavm.model.AccessLevel.PUBLIC ) {
					result.add( m.getDescriptor() );
				}
			}
		}
		return result;
	}

	private static boolean reflectable( ClassReaderSource src, String name ) {
		if( name.equals( "jolie.runtime.embedding.JavaServiceHelpers" ) || name.equals( "jolie.runtime.Value" ) ) {
			return true;
		}
		if( name.equals( "jolie.runtime.JavaService" ) ) {
			return false;
		}
		return extendsJavaService( src, name ) || implementsConverter( src, name );
	}

	private static boolean extendsJavaService( ClassReaderSource src, String name ) {
		String c = name;
		while( c != null ) {
			ClassReader r = src.get( c );
			if( r == null ) {
				return false;
			}
			c = r.getParent();
			if( "jolie.runtime.JavaService".equals( c ) ) {
				return true;
			}
		}
		return false;
	}

	private static boolean implementsConverter( ClassReaderSource src, String name ) {
		String c = name;
		while( c != null ) {
			ClassReader r = src.get( c );
			if( r == null ) {
				return false;
			}
			if( r.getInterfaces().contains( "jolie.runtime.JavaService$ValueConverter" ) ) {
				return true;
			}
			c = r.getParent();
		}
		return false;
	}
}
