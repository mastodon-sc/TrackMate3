package org.mastodon.revised.mamut;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.mastodon.revised.bdv.SharedBigDataViewerData;
import org.mastodon.revised.bdv.ViewerFrameMamut;
import org.mastodon.views.context.ContextChooser;
import org.mastodon.views.context.ContextProvider;
import org.mastodon.revised.model.mamut.Model;
import org.mastodon.revised.model.mamut.Spot;
import org.mastodon.revised.trackscheme.display.TrackSchemeFrame;
import org.scijava.ui.behaviour.KeyPressedManager;
import org.scijava.ui.behaviour.io.InputTriggerConfig;
import org.scijava.ui.behaviour.io.InputTriggerDescription;
import org.scijava.ui.behaviour.io.InputTriggerDescriptionsBuilder;
import org.scijava.ui.behaviour.io.yaml.YamlConfigIO;

import bdv.spimdata.SpimDataMinimal;
import bdv.viewer.RequestRepaint;
import bdv.viewer.ViewerFrame;
import bdv.viewer.ViewerOptions;
import mpicbg.spim.data.generic.AbstractSpimData;

public class WindowManager
{
	/**
	 * Information for one BigDataViewer window.
	 */
	public static class BdvWindow
	{
		private final ViewerFrameMamut viewerFrame;

		private final ContextProvider< Spot > contextProvider;

		public BdvWindow(
				final ViewerFrameMamut viewerFrame,
				final ContextProvider< Spot > contextProvider )
		{
			this.viewerFrame = viewerFrame;
			this.contextProvider = contextProvider;
		}

		public ViewerFrameMamut getViewerFrame()
		{
			return viewerFrame;
		}

		public ContextProvider< Spot > getContextProvider()
		{
			return contextProvider;
		}
	}

	/**
	 * Information for one TrackScheme window.
	 */
	public static class TsWindow
	{
		private final TrackSchemeFrame trackSchemeFrame;

		private final ContextChooser< Spot > contextChooser;

		public TsWindow(
				final TrackSchemeFrame trackSchemeFrame,
				final ContextChooser< Spot > contextChooser )
		{
			this.trackSchemeFrame = trackSchemeFrame;
			this.contextChooser = contextChooser;
		}

		public TrackSchemeFrame getTrackSchemeFrame()
		{
			return trackSchemeFrame;
		}

		public ContextChooser< Spot > getContextChooser()
		{
			return contextChooser;
		}
	}

	private final KeyPressedManager keyPressedManager;

	private final MamutAppModel appModel;

	/**
	 * All currently open BigDataViewer windows.
	 */
	private final List< BdvWindow > bdvWindows = new ArrayList<>();

	/**
	 * The {@link ContextProvider}s of all currently open BigDataViewer windows.
	 */
	private final List< ContextProvider< Spot > > contextProviders = new ArrayList<>();

	/**
	 * All currently open TrackScheme windows.
	 */
	private final List< TsWindow > tsWindows = new ArrayList<>();

	public WindowManager(
			final String spimDataXmlFilename,
			final SpimDataMinimal spimData,
			final Model model,
			final InputTriggerConfig keyconf )
	{
		keyPressedManager = new KeyPressedManager();
		final RequestRepaint requestRepaint = () -> {
			for ( final BdvWindow w : bdvWindows )
				w.getViewerFrame().getViewerPanel().requestRepaint();
		};

		final ViewerOptions options = ViewerOptions.options()
				.inputTriggerConfig( keyconf )
				.shareKeyPressedEvents( keyPressedManager );
		final SharedBigDataViewerData sharedBdvData = new SharedBigDataViewerData( spimDataXmlFilename, spimData, options, requestRepaint );

		appModel = new MamutAppModel( model, sharedBdvData, keyconf );
	}

	private synchronized void addBdvWindow( final BdvWindow w )
	{
		w.getViewerFrame().addWindowListener( new WindowAdapter()
		{
			@Override
			public void windowClosing( final WindowEvent e )
			{
				removeBdvWindow( w );
			}
		} );
		bdvWindows.add( w );
		contextProviders.add( w.getContextProvider() );
		for ( final TsWindow tsw : tsWindows )
			tsw.getContextChooser().updateContextProviders( contextProviders );
	}

	private synchronized void removeBdvWindow( final BdvWindow w )
	{
		bdvWindows.remove( w );
		contextProviders.remove( w.getContextProvider() );
		for ( final TsWindow tsw : tsWindows )
			tsw.getContextChooser().updateContextProviders( contextProviders );
	}

	private synchronized void addTsWindow( final TsWindow w )
	{
		w.getTrackSchemeFrame().addWindowListener( new WindowAdapter()
		{
			@Override
			public void windowClosing( final WindowEvent e )
			{
				removeTsWindow( w );
			}
		} );
		tsWindows.add( w );
		w.getContextChooser().updateContextProviders( contextProviders );
	}

	private synchronized void removeTsWindow( final TsWindow w )
	{
		tsWindows.remove( w );
		w.getContextChooser().updateContextProviders( new ArrayList<>() );
	}

	public void createBigDataViewer()
	{
		final MamutViewBdv view = new MamutViewBdv( appModel );
		addBdvWindow( view.getBdvWindow() );
	}

	public void createTrackScheme()
	{
		final MamutViewTrackScheme view = new MamutViewTrackScheme( appModel );
		addTsWindow( view.getTsWindow() );
	}

	public void closeAllWindows()
	{
		final ArrayList< JFrame > frames = new ArrayList<>();
		for ( final BdvWindow w : bdvWindows )
			frames.add( w.getViewerFrame() );
		for ( final TsWindow w : tsWindows )
			frames.add( w.getTrackSchemeFrame() );
		SwingUtilities.invokeLater( new Runnable()
		{
			@Override
			public void run()
			{
				for ( final JFrame f : frames )
					f.dispatchEvent( new WindowEvent( f, WindowEvent.WINDOW_CLOSING ) );
			}
		} );
	}

	public Model getModel()
	{
		return appModel.getModel();
	}

	public AbstractSpimData< ? > getSpimData()
	{
		return appModel.getSharedBdvData().getSpimData();
	}

	// TODO: move somewhere else. make bdvWindows, tsWindows accessible.
	public static class DumpInputConfig
	{
		private static List< InputTriggerDescription > buildDescriptions( final WindowManager wm ) throws IOException
		{
			final InputTriggerDescriptionsBuilder builder = new InputTriggerDescriptionsBuilder();

			final ViewerFrameMamut viewerFrame = wm.bdvWindows.get( 0 ).viewerFrame;
			builder.addMap( viewerFrame.getKeybindings().getConcatenatedInputMap(), "bdv" );
			builder.addMap( viewerFrame.getTriggerbindings().getConcatenatedInputTriggerMap(), "bdv" );

			final TrackSchemeFrame trackschemeFrame = wm.tsWindows.get( 0 ).trackSchemeFrame;
			builder.addMap( trackschemeFrame.getKeybindings().getConcatenatedInputMap(), "ts" );
			builder.addMap( trackschemeFrame.getTriggerbindings().getConcatenatedInputTriggerMap(), "ts" );

			return builder.getDescriptions();
		}

		public static boolean mkdirs( final String fileName )
		{
			final File dir = new File( fileName ).getParentFile();
			return dir == null ? false : dir.mkdirs();
		}

		public static void writeToYaml( final String fileName, final WindowManager wm ) throws IOException
		{
			mkdirs( fileName );
			YamlConfigIO.write( buildDescriptions( wm ), fileName );
		}
	}
}
