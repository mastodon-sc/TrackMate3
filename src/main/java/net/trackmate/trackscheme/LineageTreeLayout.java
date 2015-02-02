package net.trackmate.trackscheme;

import java.util.Iterator;

public class LineageTreeLayout
{
	private double rightmost;

	private final TrackSchemeGraph graph;

	public LineageTreeLayout( final TrackSchemeGraph graph )
	{
		this.graph = graph;
		rightmost = 0;
	}

	public void reset()
	{
		rightmost = 0;
	}

	public void layoutX()
	{
		reset();
		final TrackSchemeVertexList roots = VertexOrder.getOrderedRoots( graph );

		for ( final TrackSchemeVertex root : roots )
			layoutX( root );
	}

	public void layoutX( final TrackSchemeVertex v )
	{
		if ( v.outgoingEdges().isEmpty() )
		{
			v.setLayoutX( rightmost );
			rightmost += 1;
		}
		else
		{
			final TrackSchemeVertex child = graph.vertexRef();
			final TrackSchemeEdge edge = graph.edgeRef();
			final Iterator< TrackSchemeEdge > iterator = v.outgoingEdges().iterator();
			int numLaidOutChildren = layoutNextChild( iterator, child, edge );
			final double firstChildX = child.getLayoutX();
			if ( iterator.hasNext() )
			{
				while ( iterator.hasNext() )
					numLaidOutChildren += layoutNextChild( iterator, child, edge );
				final double lastChildX = child.getLayoutX();
				if ( numLaidOutChildren > 0 )
					v.setLayoutX( ( firstChildX + lastChildX ) / 2 );
				else
				{
					v.setLayoutX( rightmost );
					rightmost += 1;
				}
			}
			else
				if ( numLaidOutChildren > 0 )
					v.setLayoutX( firstChildX );
				else
				{
					v.setLayoutX( rightmost );
					rightmost += 1;
				}
			graph.releaseRef( edge );
			graph.releaseRef( child );
		}
	}

	private int layoutNextChild( final Iterator< TrackSchemeEdge > iterator, final TrackSchemeVertex child, final TrackSchemeEdge edge )
	{
		final TrackSchemeEdge next = iterator.next();
		next.getTarget( child );
		if ( child.incomingEdges().get( 0, edge ).equals( next ) )
		{
			layoutX( child );
			return 1;
		}
		else
			return 0;
	}
}
