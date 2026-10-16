package jolie.lang.parse.module;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public final class BrowserModules {
	private BrowserModules() {}

	public static String probe( String source ) {
		String stage = "start";
		try {
			stage = "config";
			ModuleParsingConfiguration cfg = new ModuleParsingConfiguration(
				"UTF-8", new String[ 0 ], new String[ 0 ],
				BrowserModules.class.getClassLoader(),
				new java.util.HashMap< String, jolie.lang.parse.Scanner.Token >(),
				false, true );

			stage = "source";
			ModuleSource ms = ModuleSource.create(
				URI.create( "file:///main.ol" ),
				new ByteArrayInputStream( source.getBytes( StandardCharsets.UTF_8 ) ),
				jolie.lang.parse.ast.ServiceNode.DEFAULT_MAIN_SERVICE_NAME );

			stage = "ModuleParser.parse";
			ModuleParser parser = new ModuleParser( cfg );
			ModuleRecord rec = parser.parse( ms );

			stage = "ModuleFinderImpl";
			ModuleFinder finder = new ModuleFinderImpl( cfg.packagePaths() );

			stage = "ModuleCrawler.crawl";
			ModuleCrawler.CrawlerResult cr = ModuleCrawler.crawl( rec, cfg, finder );

			stage = "SymbolReferenceResolver";
			SymbolReferenceResolver.resolve( cr );

			return "all stages ok";
		} catch( Throwable e ) {
			String msg;
			try {
				msg = String.valueOf( e );
			} catch( Throwable x ) {
				msg = e.getClass().getName();
			}
			return "FAIL at " + stage + ": " + msg;
		}
	}
}
