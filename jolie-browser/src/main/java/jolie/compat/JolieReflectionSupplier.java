package jolie.compat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.teavm.classlib.ReflectionContext;
import org.teavm.classlib.ReflectionSupplier;
import org.teavm.model.ClassReader;
import org.teavm.model.MethodDescriptor;
import org.teavm.model.MethodReader;

public class JolieReflectionSupplier implements ReflectionSupplier {
	@Override
	public Collection< MethodDescriptor > getAccessibleMethods( ReflectionContext ctx, String className ) {
		if( !className.equals( "jolie.runtime.embedding.JavaServiceHelpers" )
			&& !className.equals( "jolie.runtime.Value" ) ) {
			return java.util.Collections.emptyList();
		}
		ClassReader cls = ctx.getClassSource().get( className );
		List< MethodDescriptor > result = new ArrayList<>();
		if( cls != null ) {
			for( MethodReader m : cls.getMethods() ) {
				if( !m.getName().startsWith( "<" ) ) {
					result.add( m.getDescriptor() );
				}
			}
		}
		return result;
	}
}
