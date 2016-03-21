package net.trackmate.revised.trackscheme;

import java.util.ArrayList;

import net.trackmate.revised.ui.selection.NavigationListener;

public class TrackSchemeNavigation implements
		ModelNavigationListener
{
	private final ArrayList< NavigationListener< TrackSchemeVertex, TrackSchemeEdge > > listeners;

	private final ModelNavigationProperties props;

	private final TrackSchemeGraph< ?, ? > graph;

	public TrackSchemeNavigation(
			final ModelNavigationProperties props,
			final TrackSchemeGraph< ?, ? > graph )
	{
		this.props = props;
		this.graph = graph;
		this.listeners = new ArrayList<>();

		props.forwardNavigationEventsTo( this );
	}

	/**
	 * Registers the specified listener to this handler.
	 *
	 * @param listener
	 *            the {@link NavigationListener} to register.
	 * @return {@code true} if the specified listener was added to the
	 *         listeners of this handler. {@code false} if the specified
	 *         listener was already registered.
	 */
	public synchronized boolean addNavigationListener( final NavigationListener< TrackSchemeVertex, TrackSchemeEdge > listener )
	{
		if ( !listeners.contains( listener ) )
		{
			listeners.add( listener );
			return true;
		}
		return false;
	}

	public synchronized boolean removeNavigationListener( final NavigationListener< TrackSchemeVertex, TrackSchemeEdge > l )
	{
		return listeners.remove( l );
	}

	// forward to ModelNavigationProperties
	public void notifyNavigateToVertex( final TrackSchemeVertex v )
	{
		props.notifyNavigateToVertex( v.getModelVertexId() );
	}

	/*
	 * ModelNavigationListener implementation
	 * converts forwarded model id events to TrackSchemeVertex/Edge
	 */

	@Override
	public void navigateToVertex( final int mid )
	{
		final TrackSchemeVertex ref = graph.vertexRef();
		final TrackSchemeVertex v = graph.getTrackSchemeVertexForModelId( mid, ref );
		for ( final NavigationListener< TrackSchemeVertex, TrackSchemeEdge > l : listeners )
			l.navigateToVertex( v );
		graph.releaseRef( ref );
	}

	@Override
	public void navigateToEdge( final int mid )
	{
		final TrackSchemeEdge ref = graph.edgeRef();
		final TrackSchemeEdge e = graph.getTrackSchemeEdgeForModelId( mid, ref );
		for ( final NavigationListener< TrackSchemeVertex, TrackSchemeEdge > l : listeners )
			l.navigateToEdge( e );
		graph.releaseRef( ref );
	}
}
