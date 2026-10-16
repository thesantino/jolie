package jolie;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.net.MalformedURLException;
import java.net.URI;
import org.teavm.jso.JSExport;
import jolie.lang.NativeType;
import jolie.lang.parse.*;
import jolie.lang.parse.ast.Program;
import jolie.lang.parse.ast.types.TypeDefinitionUndefined;
import jolie.lang.parse.context.ParsingContext;
import jolie.lang.parse.context.URIParsingContext;
import jolie.lang.parse.util.ProgramBuilder;

public class JolieJS {
	public static void main( String[] args ) {}

	@JSExport
	public static String parse( String source ) {
		String stage = "start";
		try {
			stage = "config";
			jolie.lang.parse.module.ModuleParsingConfiguration cfg =
				new jolie.lang.parse.module.ModuleParsingConfiguration(
					"UTF-8",
					new String[ 0 ],
					new String[ 0 ],
					JolieJS.class.getClassLoader(),
					new java.util.HashMap< String, jolie.lang.parse.Scanner.Token >(),
					false,
					true );

			stage = "module-source";
			jolie.lang.parse.module.ModuleSource ms =
				jolie.lang.parse.module.ModuleSource.create(
					URI.create( "file:///main.ol" ),
					new ByteArrayInputStream( source.getBytes( StandardCharsets.UTF_8 ) ),
					jolie.lang.parse.ast.ServiceNode.DEFAULT_MAIN_SERVICE_NAME );

			stage = "parseModule";
			jolie.lang.parse.module.Modules.ModuleParsedResult r =
				jolie.lang.parse.module.Modules.parseModule( cfg, ms );

			stage = "verifier";
			jolie.lang.parse.SemanticVerifier.Configuration conf =
				new jolie.lang.parse.SemanticVerifier.Configuration(
					jolie.lang.parse.ast.ServiceNode.DEFAULT_MAIN_SERVICE_NAME );
			new jolie.lang.parse.SemanticVerifier(
				r.mainProgram(), new java.util.HashMap<>( r.symbolTables() ), conf ).validate();
			return "OK";
		} catch( Throwable e ) {
			String msg;
			try {
				msg = String.valueOf( e );
			} catch( Throwable x ) {
				msg = e.getClass().getName();
			}
			return "ERROR at " + stage + ": " + msg;
		}
	}

	@JSExport
	public static String probe() {
		StringBuilder sb = new StringBuilder();
		sb.append( t( "paths-get-string", () -> String.valueOf( java.nio.file.Paths.get( "console.iol" ) ) ) );
		sb.append( t( "path-toUri", () -> String.valueOf( java.nio.file.Paths.get( "console.iol" ).toUri() ) ) );
		sb.append( t( "uri-toURL", () -> {
			try {
				return String.valueOf( java.net.URI.create( "file:///console.iol" ).toURL() );
			} catch( MalformedURLException e ) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "FAIL";
			}
		} ) );
		sb.append( t( "normalizeWindowsPath",
			() -> String.valueOf( jolie.util.UriUtils.normalizeWindowsPath( "console.iol" ) ) ) );
		sb.append( t( "moduleSource-create", () -> {
			try {
				java.net.URI u = java.net.URI.create( "file:/console.iol" );
				jolie.lang.parse.module.ModuleSource s = jolie.lang.parse.module.ModuleSource.create(
					u, new java.io.ByteArrayInputStream( new byte[ 0 ] ), "Main" );
				return "ok";
			} catch( Exception e ) {
				return "FAIL " + e;
			}
		} ) );
		sb.append( t( "p1-get-uri",
			() -> String.valueOf( java.nio.file.Paths.get( java.net.URI.create( "file:/console.iol" ) ) ) ) );
		sb.append( t( "p2-getParent", () -> String
			.valueOf( java.nio.file.Paths.get( java.net.URI.create( "file:/console.iol" ) ).getParent() ) ) );
		sb.append( t( "p3-parent-isNull", () -> String
			.valueOf( java.nio.file.Paths.get( java.net.URI.create( "file:/console.iol" ) ).getParent() == null ) ) );
		sb.append( t( "p4-parent-toUri", () -> String
			.valueOf( java.nio.file.Paths.get( java.net.URI.create( "file:/console.iol" ) ).getParent().toUri() ) ) );
		sb.append( t( "p5-scheme", () -> String.valueOf( java.net.URI.create( "file:/console.iol" ).getScheme() ) ) );
		sb.append( t( "h1-helper-get", () -> String
			.valueOf( jolie.compat.PathsCompat.get( java.net.URI.create( "file:/console.iol" ) ) ) ) );
		sb.append( t( "h2-paths-get-root", () -> String.valueOf( java.nio.file.Paths.get( "/console.iol" ) ) ) );
		sb.append( t( "h3-paths-get-root-parent",
			() -> String.valueOf( java.nio.file.Paths.get( "/console.iol" ).getParent() ) ) );
		sb.append( t( "h4-helper-toUri",
			() -> String.valueOf( jolie.compat.PathsCompat.toUri( java.nio.file.Paths.get( "/" ) ) ) ) );
		sb.append(
			t( "uri-resolve", () -> String.valueOf( java.net.URI.create( "file:/console.iol" ).resolve( "." ) ) ) );
		sb.append( t( "scanner-from-source", () -> {
			try {
				jolie.lang.parse.module.ModuleSource s = jolie.lang.parse.module.ModuleSource.create(
					java.net.URI.create( "file:/console.iol" ),
					new java.io.ByteArrayInputStream( "type A: void".getBytes() ), "Main" );
				new Scanner( s, "US-ASCII", false ).close();
				return "ok";
			} catch( Exception e ) {
				return "FAIL " + e;
			}
		} ) );
		sb.append( t( "uri-ctor-parent", () -> {
			try {
				java.net.URI u = java.net.URI.create( "file:/console.iol" );
				String p = u.getPath();
				String parent = p.substring( 0, p.lastIndexOf( '/' ) + 1 );
				return String.valueOf( new java.net.URI( u.getScheme(), u.getAuthority(), parent, null, null ) );
			} catch( Exception e ) {
				return "FAIL " + e;
			}
		} ) );
		sb.append( t( "uri-ctor-parent-null-host", () -> {
			try {
				return String.valueOf( new java.net.URI( "file", null, "/", null ) );
			} catch( Exception e ) {
				return "FAIL " + e;
			}
		} ) );
		sb.append( t( "path-getParent", () -> {
			try {
				return String.valueOf( java.nio.file.Paths.get( "/console.iol" ).getParent() );
			} catch( Exception e ) {
				return "FAIL " + e;
			}
		} ) );
		sb.append( t( "sysprop-file.separator", () -> String.valueOf( System.getProperty( "file.separator" ) ) ) );
		sb.append( t( "sysprop-path.separator", () -> String.valueOf( System.getProperty( "path.separator" ) ) ) );
		sb.append( t( "Constants.FILE_SEPARATOR", () -> String.valueOf( jolie.lang.Constants.FILE_SEPARATOR ) ) );
		return sb.toString();
	}

	private static String t( String name, java.util.function.Supplier< String > c ) {
		try {
			return name + ": " + c.get() + "\n";
		} catch( Throwable e ) {
			String msg;
			try {
				msg = String.valueOf( e );
			} catch( Throwable x ) {
				msg = e.getClass().getName();
			}
			return name + ": FAIL " + e.getClass().getName() + " " + msg + "\n";
		}
	}

	@JSExport
	public static void addFile( String name, String content ) {
		jolie.compat.Resources.put( name, content );
	}

	@JSExport
	public static String probePort() {
		try {
			String code =
				"interface I {\n RequestResponse: op( void )( void )\n}\n"
					+ "inputPort P {\n location: \"local\"\n protocol: sodep\n interfaces: I\n}\n";
			Scanner sc = new Scanner(
				new ByteArrayInputStream( code.getBytes( StandardCharsets.UTF_8 ) ),
				URI.create( "file:///p.ol" ) );
			OLParser p = new OLParser( sc, new String[ 0 ], JolieJS.class.getClassLoader() );
			jolie.lang.parse.ast.Program prog = p.parse();
			StringBuilder sb = new StringBuilder();
			for( jolie.lang.parse.ast.OLSyntaxNode node : prog.children() ) {
				sb.append( "node: " ).append( node.getClass().getName() ).append( "\n" );
				if( node instanceof jolie.lang.parse.ast.InputPortInfo ) {
					jolie.lang.parse.ast.InputPortInfo ip = (jolie.lang.parse.ast.InputPortInfo) node;
					Object loc = ip.location();
					Object pro = ip.protocol();
					sb.append( "location: " ).append( loc == null ? "null" : loc.getClass().getName() ).append( "\n" );
					sb.append( "location is ConstantStringExpression: " )
						.append( loc instanceof jolie.lang.parse.ast.expression.ConstantStringExpression )
						.append( "\n" );
					sb.append( "protocol: " ).append( pro == null ? "null" : pro.getClass().getName() ).append( "\n" );
					sb.append( "protocol is ConstantStringExpression: " )
						.append( pro instanceof jolie.lang.parse.ast.expression.ConstantStringExpression )
						.append( "\n" );
				}
			}
			return sb.toString();
		} catch( Throwable e ) {
			return "FAIL " + e;
		}
	}

	@JSExport
	public static String probeConsole( String source ) {
		String stage = "start";
		try {
			if( source == null )
				return "source is null";
			stage = "scanner";
			Scanner sc = new Scanner(
				new ByteArrayInputStream( source.getBytes( StandardCharsets.UTF_8 ) ),
				URI.create( "file:///main.ol" ) );
			stage = "parser-ctor";
			OLParser p = new OLParser( sc, new String[ 0 ], JolieJS.class.getClassLoader() );
			stage = "parse";
			jolie.lang.parse.ast.Program prog = p.parse();
			StringBuilder sb = new StringBuilder( "BEFORE optimize:\n" );
			dump( prog, sb, "" );
			stage = "optimize";
			prog = jolie.lang.parse.OLParseTreeOptimizer.optimize( prog );
			sb.append( "AFTER optimize:\n" );
			dump( prog, sb, "" );
			return sb.toString();
		} catch( Throwable e ) {
			return "FAIL at " + stage + ": " + e;
		}
	}

	private static void dump( jolie.lang.parse.ast.Program prog, StringBuilder sb, String indent ) {
		for( jolie.lang.parse.ast.OLSyntaxNode n : prog.children() ) {
			sb.append( indent ).append( n.getClass().getSimpleName() );
			if( n instanceof jolie.lang.parse.ast.OutputPortInfo ) {
				jolie.lang.parse.ast.OutputPortInfo o = (jolie.lang.parse.ast.OutputPortInfo) n;
				sb.append( " " ).append( o.id() ).append( " operationsMap=" ).append( o.operationsMap().size() );
			} else if( n instanceof jolie.lang.parse.ast.InterfaceDefinition ) {
				jolie.lang.parse.ast.InterfaceDefinition i = (jolie.lang.parse.ast.InterfaceDefinition) n;
				sb.append( " " ).append( i.name() ).append( " operations=" ).append( i.operationsMap().size() );
			}
			sb.append( "\n" );
			if( n instanceof jolie.lang.parse.ast.ServiceNode ) {
				dump( ((jolie.lang.parse.ast.ServiceNode) n).program(), sb, indent + "  " );
			}
		}
	}

	@JSExport
	public static String probeModules( String source ) {
		return jolie.lang.parse.module.BrowserModules.probe( source );
	}

	@JSExport
	public static String probeInterpreter() {
		try {
			jolie.Interpreter i = new jolie.Interpreter(
				null,
				java.util.Optional.of( jolie.runtime.Value.create() ),
				java.util.Optional.empty() );
			i.run();
			return "created";
		} catch( Throwable e ) {
			return "FAIL " + e;
		}
	}
}
