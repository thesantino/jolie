package jolie.compat;

import org.teavm.model.*;
import org.teavm.model.instructions.InvocationType;
import org.teavm.model.instructions.InvokeInstruction;
import org.teavm.vm.spi.TeaVMHost;
import org.teavm.vm.spi.TeaVMPlugin;

public class JolieCompatPlugin implements TeaVMPlugin {
	@Override
	public void install( TeaVMHost host ) {
		host.add( new Rewriter() );
	}

	static class Rewriter implements ClassHolderTransformer {
		private static final MethodReference GET_RESOURCE = new MethodReference(
			"jolie.compat.Resources", "getResource",
			ValueType.object( "java.lang.ClassLoader" ),
			ValueType.object( "java.lang.String" ),
			ValueType.object( "java.net.URL" ) );

		private static final MethodReference GET_RESOURCE_AS_STREAM = new MethodReference(
			"jolie.compat.Resources", "getResourceAsStream",
			ValueType.object( "java.lang.ClassLoader" ),
			ValueType.object( "java.lang.String" ),
			ValueType.object( "java.io.InputStream" ) );

		private static final MethodReference PATHS_GET = new MethodReference(
			"jolie.compat.PathsCompat", "get",
			ValueType.object( "java.net.URI" ),
			ValueType.object( "java.nio.file.Path" ) );

		private static final MethodReference FILES_NEW_INPUT_STREAM = new MethodReference(
			"jolie.compat.FilesCompat", "newInputStream",
			ValueType.object( "java.nio.file.Path" ),
			ValueType.arrayOf( ValueType.object( "java.nio.file.OpenOption" ) ),
			ValueType.object( "java.io.InputStream" ) );

		private static final MethodReference PATH_TO_URI = new MethodReference(
			"jolie.compat.PathsCompat", "toUri",
			ValueType.object( "java.nio.file.Path" ),
			ValueType.object( "java.net.URI" ) );

		private static final MethodReference SYSTEM_GET_PROPERTY = new MethodReference(
			"jolie.compat.SystemCompat", "getProperty",
			ValueType.object( "java.lang.String" ),
			ValueType.object( "java.lang.String" ) );

		private static final MethodReference LOGGER_SET_LEVEL = new MethodReference(
			"jolie.compat.LoggerCompat", "setLevel",
			ValueType.object( "java.util.logging.Logger" ),
			ValueType.object( "java.util.logging.Level" ),
			ValueType.VOID );
		private static final MethodReference THREAD_INIT_NAME = new MethodReference(
			"java.lang.Thread", "<init>", ValueType.object( "java.lang.String" ), ValueType.VOID );
		private static final MethodReference THREAD_SET_CL = new MethodReference(
			"jolie.compat.ThreadCompat", "setContextClassLoader",
			ValueType.object( "java.lang.Thread" ), ValueType.object( "java.lang.ClassLoader" ), ValueType.VOID );
		private static final MethodReference CL_FIND_LOADED = new MethodReference(
			"jolie.compat.ClassLoaderCompat", "findLoadedClass",
			ValueType.object( "java.lang.ClassLoader" ), ValueType.object( "java.lang.String" ),
			ValueType.object( "java.lang.Class" ) );
		private static final MethodReference CL_GET_PARENT = new MethodReference(
			"jolie.compat.ClassLoaderCompat", "getParent",
			ValueType.object( "java.lang.ClassLoader" ), ValueType.object( "java.lang.ClassLoader" ) );
		private static final MethodReference CL_FIND_RESOURCE = new MethodReference(
			"jolie.compat.ClassLoaderCompat", "findResource",
			ValueType.object( "java.lang.ClassLoader" ), ValueType.object( "java.lang.String" ),
			ValueType.object( "java.net.URL" ) );
		private static final MethodReference CL_ADD_URL = new MethodReference(
			"jolie.compat.ClassLoaderCompat", "addURL",
			ValueType.object( "java.lang.ClassLoader" ), ValueType.object( "java.net.URL" ), ValueType.VOID );
		private static final MethodReference CL_LOAD_CLASS = new MethodReference(
			"jolie.compat.ClassLoaderCompat", "loadClass",
			ValueType.object( "java.lang.ClassLoader" ), ValueType.object( "java.lang.String" ),
			ValueType.object( "java.lang.Class" ) );
		private static final java.util.Map< String, String > NEW_TYPE = new java.util.HashMap<>();
		static {
			NEW_TYPE.put( "jolie.net.SocketListenerFactory", "jolie.net.BrowserListenerFactory" );
			NEW_TYPE.put( "jolie.net.SocketCommChannelFactory", "jolie.net.BrowserChannelFactory" );
		}
		private static final MethodReference RUNTIME_PROCESSORS = new MethodReference(
			"jolie.compat.RuntimeCompat", "availableProcessors",
			ValueType.object( "java.lang.Runtime" ), ValueType.INTEGER );
		private static final MethodReference JAVA_SERVICE_CREATE = new MethodReference(
			"jolie.compat.JavaServices", "create",
			ValueType.object( "java.lang.Object" ), ValueType.object( "java.lang.String" ),
			ValueType.object( "jolie.runtime.JavaService" ) );
		private static final MethodReference CLASS_GET_METHOD = new MethodReference(
			"jolie.compat.ReflectCompat", "getMethod",
			ValueType.object( "java.lang.Class" ), ValueType.object( "java.lang.String" ),
			ValueType.arrayOf( ValueType.object( "java.lang.Class" ) ),
			ValueType.object( "java.lang.reflect.Method" ) );

		private String current = "";
		private static final MethodReference EMBEDDING_FACTORY = new MethodReference(
			"jolie.compat.Embedding", "factory",
			ValueType.object( "jolie.Interpreter" ), ValueType.object( "java.lang.String" ),
			ValueType.object( "jolie.runtime.embedding.EmbeddedServiceLoaderFactory" ) );

		private static final MethodReference SCHED_SHUTDOWN_NOW = new MethodReference(
			"jolie.compat.SchedulerCompat", "shutdownNow",
			ValueType.object( "java.lang.Object" ), ValueType.object( "java.util.List" ) );
		private static final MethodReference SCHED_SHUTDOWN = new MethodReference(
			"jolie.compat.SchedulerCompat", "shutdown",
			ValueType.object( "java.lang.Object" ), ValueType.VOID );
		private static final MethodReference SCHED_AWAIT = new MethodReference(
			"jolie.compat.SchedulerCompat", "awaitTermination",
			ValueType.object( "java.lang.Object" ), ValueType.LONG,
			ValueType.object( "java.util.concurrent.TimeUnit" ), ValueType.BOOLEAN );
		private static final MethodReference PROTOCOL_FACTORY = new MethodReference(
			"jolie.compat.Protocols", "create",
			ValueType.object( "java.lang.Object" ), ValueType.object( "java.lang.String" ),
			ValueType.object( "jolie.net.CommCore" ),
			ValueType.object( "jolie.net.ext.CommProtocolFactory" ) );
		private static final MethodReference SCHED_SCHEDULE = new MethodReference(
			"jolie.compat.SchedulerCompat", "schedule",
			ValueType.object( "java.lang.Object" ), ValueType.object( "java.lang.Runnable" ),
			ValueType.LONG, ValueType.object( "java.util.concurrent.TimeUnit" ),
			ValueType.object( "java.util.concurrent.ScheduledFuture" ) );

		@Override
		public void transformClass( ClassHolder holder, ClassHolderTransformerContext ctx ) {
			current = holder.getName();
			for( MethodHolder method : holder.getMethods() ) {
				Program program = method.getProgram();
				if( program == null ) {
					continue;
				}
				for( int i = 0; i < program.basicBlockCount(); i++ ) {
					java.util.List< Instruction > list = new java.util.ArrayList<>();
					for( Instruction x : program.basicBlockAt( i ) ) {
						list.add( x );
					}
					for( Instruction ins : list ) {
						if( ins instanceof InvokeInstruction ) {
							rewrite( (InvokeInstruction) ins );
						} else if( ins instanceof org.teavm.model.instructions.ConstructInstruction ) {
							rewriteConstruct( (org.teavm.model.instructions.ConstructInstruction) ins );
						}
					}
				}
			}
		}

		private void rewriteConstruct( org.teavm.model.instructions.ConstructInstruction c ) {
			String repl = NEW_TYPE.get( c.getType() );
			if( repl != null ) {
				c.setType( repl );
			} else if( c.getType().equals( "jolie.net.CommCore$SelectorThread" ) ) {
				org.teavm.model.instructions.NullConstantInstruction n =
					new org.teavm.model.instructions.NullConstantInstruction();
				n.setReceiver( c.getReceiver() );
				c.replace( n );
			}
		}

		private void rewrite( InvokeInstruction inv ) {
			MethodReference m = inv.getMethod();
			String owner = m.getClassName();

			if( owner.equals( "java.nio.file.Paths" )
				&& m.getName().equals( "get" )
				&& inv.getArguments().size() == 1
				&& m.parameterType( 0 ).equals( ValueType.object( "java.net.URI" ) ) ) {
				inv.setMethod( PATHS_GET );
				return;
			}
			if( owner.equals( "java.nio.file.Files" )
				&& m.getName().equals( "newInputStream" )
				&& inv.getArguments().size() == 2 ) {
				inv.setMethod( FILES_NEW_INPUT_STREAM );
				return;
			}
			if( owner.equals( "java.nio.file.Path" )
				&& m.getName().equals( "toUri" )
				&& inv.getInstance() != null
				&& inv.getArguments().isEmpty() ) {
				Variable self = inv.getInstance();
				inv.setType( InvocationType.SPECIAL );
				inv.setMethod( PATH_TO_URI );
				inv.setInstance( null );
				inv.setArguments( self );
				return;
			}
			if( owner.equals( "java.lang.System" )
				&& m.getName().equals( "getProperty" )
				&& inv.getArguments().size() == 1 ) {
				inv.setMethod( SYSTEM_GET_PROPERTY );
				return;
			}
			if( owner.equals( "java.util.logging.Logger" )
				&& m.getName().equals( "setLevel" )
				&& inv.getInstance() != null
				&& inv.getArguments().size() == 1 ) {
				Variable self = inv.getInstance();
				Variable level = inv.getArguments().get( 0 );
				inv.setType( InvocationType.SPECIAL );
				inv.setMethod( LOGGER_SET_LEVEL );
				inv.setInstance( null );
				inv.setArguments( self, level );
				return;
			}
			// Thread(ThreadGroup, String) -> Thread(String); the instance stays, so this works for super(...)
			// calls
			if( owner.equals( "java.lang.Thread" ) && m.getName().equals( "<init>" )
				&& inv.getArguments().size() == 2
				&& m.parameterType( 0 ).equals( ValueType.object( "java.lang.ThreadGroup" ) ) ) {
				Variable name = inv.getArguments().get( 1 );
				inv.setMethod( THREAD_INIT_NAME );
				inv.setArguments( name );
				return;
			}
			// any receiver type: Thread, JolieExecutorThread, StarterThread, ...
			if( m.getName().equals( "setContextClassLoader" ) && inv.getInstance() != null
				&& inv.getArguments().size() == 1 ) {
				Variable self = inv.getInstance();
				Variable l = inv.getArguments().get( 0 );
				inv.setType( InvocationType.SPECIAL );
				inv.setMethod( THREAD_SET_CL );
				inv.setInstance( null );
				inv.setArguments( self, l );
				return;
			}
			if( owner.equals( "jolie.JolieClassLoader" ) && inv.getInstance() != null ) {
				MethodReference t = null;
				switch( m.getName() ) {
				case "findLoadedClass":
					t = CL_FIND_LOADED;
					break;
				case "getParent":
					t = CL_GET_PARENT;
					break;
				case "findResource":
					t = CL_FIND_RESOURCE;
					break;
				case "addURL":
					t = CL_ADD_URL;
					break;
				}
				if( t != null ) {
					Variable[] args = new Variable[ inv.getArguments().size() + 1 ];
					args[ 0 ] = inv.getInstance();
					for( int i = 0; i < inv.getArguments().size(); i++ ) {
						args[ i + 1 ] = inv.getArguments().get( i );
					}
					inv.setType( InvocationType.SPECIAL );
					inv.setMethod( t );
					inv.setInstance( null );
					inv.setArguments( args );
					return;
				}
			}
			if( m.getName().equals( "<init>" ) && NEW_TYPE.containsKey( owner ) ) {
				inv.setMethod( new MethodReference( NEW_TYPE.get( owner ), m.getDescriptor() ) );
				return;
			}
			// SelectorThread is never constructed any more, so drop its constructor call
			if( owner.equals( "jolie.net.CommCore$SelectorThread" ) && m.getName().equals( "<init>" ) ) {
				inv.delete();
				return;
			}
			// CommCore.shutdown calls selector.wakeup(); no selectors exist
			if( owner.equals( "java.nio.channels.Selector" ) && m.getName().equals( "wakeup" ) ) {
				inv.delete();
				return;
			}
			// socket factory constructors
			if( m.getName().equals( "<init>" ) && NEW_TYPE.containsKey( owner ) ) {
				inv.setMethod( new MethodReference( NEW_TYPE.get( owner ), m.getDescriptor() ) );
				return;
			}
			if( owner.equals( "java.lang.Runtime" ) && m.getName().equals( "availableProcessors" )
				&& inv.getInstance() != null ) {
				Variable self = inv.getInstance();
				inv.setType( InvocationType.SPECIAL );
				inv.setMethod( RUNTIME_PROCESSORS );
				inv.setInstance( null );
				inv.setArguments( self );
				return;
			}
			if( owner.equals( "jolie.runtime.embedding.JavaServiceNodeLoader" )
				&& m.getName().equals( "loadJavaClass" )
				&& inv.getInstance() != null && inv.getArguments().size() == 1 ) {
				Variable self = inv.getInstance();
				Variable name = inv.getArguments().get( 0 );
				inv.setType( InvocationType.SPECIAL );
				inv.setMethod( JAVA_SERVICE_CREATE );
				inv.setInstance( null );
				inv.setArguments( self, name );
				return;
			}
			if( current.equals( "jolie.runtime.JavaService" )
				&& owner.equals( "java.lang.Class" ) && m.getName().equals( "getMethod" )
				&& inv.getInstance() != null && inv.getArguments().size() == 2 ) {
				Variable self = inv.getInstance();
				Variable name = inv.getArguments().get( 0 );
				Variable params = inv.getArguments().get( 1 );
				inv.setType( InvocationType.SPECIAL );
				inv.setMethod( CLASS_GET_METHOD );
				inv.setInstance( null );
				inv.setArguments( self, name, params );
				return;
			}
			if( owner.equals( "jolie.Interpreter" ) && m.getName().equals( "getEmbeddedServiceLoaderFactory" )
				&& inv.getInstance() != null && inv.getArguments().size() == 1 ) {
				Variable self = inv.getInstance();
				Variable name = inv.getArguments().get( 0 );
				inv.setType( InvocationType.SPECIAL );
				inv.setMethod( EMBEDDING_FACTORY );
				inv.setInstance( null );
				inv.setArguments( self, name );
				return;
			}
			if( owner.equals( "java.util.concurrent.ScheduledExecutorService" ) && inv.getInstance() != null ) {
				MethodReference t = null;
				switch( m.getName() ) {
				case "shutdownNow":
					if( inv.getArguments().size() == 0 )
						t = SCHED_SHUTDOWN_NOW;
					break;
				case "shutdown":
					if( inv.getArguments().size() == 0 )
						t = SCHED_SHUTDOWN;
					break;
				case "awaitTermination":
					if( inv.getArguments().size() == 2 )
						t = SCHED_AWAIT;
					break;
				case "schedule":
					if( inv.getArguments().size() == 3 )
						t = SCHED_SCHEDULE;
					break;
				}
				if( t != null ) {
					Variable[] args = new Variable[ inv.getArguments().size() + 1 ];
					args[ 0 ] = inv.getInstance();
					for( int k = 0; k < inv.getArguments().size(); k++ ) {
						args[ k + 1 ] = inv.getArguments().get( k );
					}
					inv.setType( InvocationType.SPECIAL );
					inv.setMethod( t );
					inv.setInstance( null );
					inv.setArguments( args );
					return;
				}
			}
			if( owner.equals( "jolie.JolieClassLoader" ) && m.getName().equals( "createCommProtocolFactory" )
				&& inv.getInstance() != null && inv.getArguments().size() == 2 ) {
				Variable self = inv.getInstance();
				Variable name = inv.getArguments().get( 0 );
				Variable core = inv.getArguments().get( 1 );
				inv.setType( InvocationType.SPECIAL );
				inv.setMethod( PROTOCOL_FACTORY );
				inv.setInstance( null );
				inv.setArguments( self, name, core );
				return;
			}
			if( owner.equals( "java.lang.ClassLoader" ) && m.getName().equals( "loadClass" )
				&& inv.getInstance() != null && inv.getArguments().size() == 1 ) {
				Variable self = inv.getInstance();
				Variable name = inv.getArguments().get( 0 );
				inv.setType( InvocationType.SPECIAL );
				inv.setMethod( CL_LOAD_CLASS );
				inv.setInstance( null );
				inv.setArguments( self, name );
				return;
			}
			if( owner.equals( "java.lang.ClassLoader" )
				&& inv.getInstance() != null
				&& inv.getArguments().size() == 1 ) {
				MethodReference target = null;
				if( m.getName().equals( "getResource" ) ) {
					target = GET_RESOURCE;
				} else if( m.getName().equals( "getResourceAsStream" ) ) {
					target = GET_RESOURCE_AS_STREAM;
				}
				if( target != null ) {
					Variable loader = inv.getInstance();
					Variable name = inv.getArguments().get( 0 );
					inv.setType( InvocationType.SPECIAL );
					inv.setMethod( target );
					inv.setInstance( null );
					inv.setArguments( loader, name );
				}
			}
		}
	}
}
