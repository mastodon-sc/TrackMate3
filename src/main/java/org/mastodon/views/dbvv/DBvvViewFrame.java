package org.mastodon.views.dbvv;

import bdv.cache.CacheControl;
import bdv.viewer.SourceAndConverter;
import bdv.viewer.ViewerOptions;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.Box;
import javax.swing.WindowConstants;
import org.mastodon.app.ui.GroupLocksPanel;
import org.mastodon.app.ui.ViewFrame;
import org.mastodon.grouping.GroupHandle;
import org.mastodon.mamut.model.Link;
import org.mastodon.mamut.model.ModelGraph;
import org.mastodon.mamut.model.Spot;
import org.mastodon.model.HighlightModel;
import org.mastodon.model.SelectionModel;
import org.mastodon.views.bvv.BvvOptions;
import org.scijava.ui.behaviour.MouseAndKeyHandler;
import org.scijava.ui.behaviour.util.InputActionBindings;

public class DBvvViewFrame extends ViewFrame
{
	private final DBvvPanel bvvPanel;

	/**
	 * @param sources
	 * 		the {@link SourceAndConverter sources} to display.
	 * @param numTimepoints
	 * 		number of available timepoints.
	 * @param cacheControl
	 * 		handle to cache. This is used to control io timing.
	 * @param optional
	 * 		optional parameters. See {@link ViewerOptions}.
	 * @param bvvOptional
	 * 		optional parameters. See {@link BvvOptions}.
	 */
	public DBvvViewFrame(
			final String windowTitle,
			final ModelGraph viewGraph,
			final SelectionModel< Spot, Link > selection,
			final HighlightModel< Spot, Link > highlight,
			final List< SourceAndConverter< ? > > sources,
			final int numTimepoints,
			final CacheControl cacheControl,
			final GroupHandle groupHandle,
			final ViewerOptions optional,
			final BvvOptions bvvOptional )
	{
		super( windowTitle );

		bvvPanel = new DBvvPanel( viewGraph, selection, highlight, sources, numTimepoints, cacheControl, optional, bvvOptional );
		add( bvvPanel, BorderLayout.CENTER );

		final GroupLocksPanel navigationLocksPanel = new GroupLocksPanel( groupHandle );
		settingsPanel.add( navigationLocksPanel );
		settingsPanel.add( Box.createHorizontalGlue() );

		pack();
		setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
		addWindowListener( new WindowAdapter()
		{
			@Override
			public void windowClosing( final WindowEvent e )
			{
				bvvPanel.stop();
			}
		} );

		final Component display = bvvPanel.getDisplay();
		final MouseAndKeyHandler mouseAndKeyHandler = new MouseAndKeyHandler();
		mouseAndKeyHandler.setInputMap( triggerbindings.getConcatenatedInputTriggerMap() );
		mouseAndKeyHandler.setBehaviourMap( triggerbindings.getConcatenatedBehaviourMap() );
		mouseAndKeyHandler.setKeypressManager( optional.values.getKeyPressedManager(), display );
		display.addKeyListener( mouseAndKeyHandler );
		display.addMouseListener( mouseAndKeyHandler );
		display.addMouseWheelListener( mouseAndKeyHandler );
		display.addMouseMotionListener( mouseAndKeyHandler );
		display.addFocusListener( mouseAndKeyHandler );
	}

	public InputActionBindings getKeybindings()
	{
		return keybindings;
	}

	public DBvvPanel getBvvPanel()
	{
		return bvvPanel;
	}
}
