package net.trackmate.graph.collection;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.trackmate.graph.Edge;
import net.trackmate.graph.ReadOnlyGraph;
import net.trackmate.graph.SafePoolObjectIteratorWrapper;
import net.trackmate.graph.Vertex;

public class CollectionUtils
{
	public static < V extends Vertex< ? > > RefSet< V > createVertexSet( final ReadOnlyGraph< V, ? > graph )
	{
		if ( graph instanceof SetCreator )
			return ( ( SetCreator< V, ? > ) graph ).createVertexSet();
		else
			return wrap( new HashSet< V >() );
	}

	public static < V extends Vertex< ? > > RefSet< V > createVertexSet( final ReadOnlyGraph< V, ? > graph, final int initialCapacity )
	{
		if ( graph instanceof SetCreator )
			return ( ( SetCreator< V, ? > ) graph ).createVertexSet( initialCapacity );
		else
			return wrap( new HashSet< V >( initialCapacity ) );
	}

	public static < E extends Edge< ? > > RefSet< E > createEdgeSet( final ReadOnlyGraph< ?, E > graph )
	{
		if ( graph instanceof SetCreator )
			return ( ( SetCreator< ?, E > ) graph ).createEdgeSet();
		else
			return wrap( new HashSet< E >() );
	}

	public static < E extends Edge< ? > > RefSet< E > createEdgeSet( final ReadOnlyGraph< ?, E > graph, final int initialCapacity )
	{
		if ( graph instanceof SetCreator )
			return ( ( SetCreator< ?, E > ) graph ).createEdgeSet( initialCapacity );
		else
			return wrap( new HashSet< E >( initialCapacity ) );
	}

	public static < V extends Vertex< ? > > RefList< V > createVertexList( final ReadOnlyGraph< V, ? > graph )
	{
		if ( graph instanceof ListCreator )
			return ( ( ListCreator< V, ? > ) graph ).createVertexList();
		else
			return wrap( new ArrayList< V >() );
	}

	public static < V extends Vertex< ? > > RefList< V > createVertexList( final ReadOnlyGraph< V, ? > graph, final int initialCapacity )
	{
		if ( graph instanceof ListCreator )
			return ( ( ListCreator< V, ? > ) graph ).createVertexList( initialCapacity );
		else
			return wrap( new ArrayList< V >( initialCapacity ) );
	}

	public static < E extends Edge< ? > > RefList< E > createEdgeList( final ReadOnlyGraph< ?, E > graph )
	{
		if ( graph instanceof ListCreator )
			return ( ( ListCreator< ?, E > ) graph ).createEdgeList();
		else
			return wrap( new ArrayList< E >() );
	}

	public static < E extends Edge< ? > > RefList< E > createEdgeList( final ReadOnlyGraph< ?, E > graph, final int initialCapacity )
	{
		if ( graph instanceof ListCreator )
			return ( ( ListCreator< ?, E > ) graph ).createEdgeList( initialCapacity );
		else
			return wrap( new ArrayList< E >( initialCapacity ) );
	}

	public static < V extends Vertex< ? > > RefDeque< V > createVertexDeque( final ReadOnlyGraph< V, ? > graph )
	{
		if ( graph instanceof DequeCreator )
			return ( ( DequeCreator< V, ? > ) graph ).createVertexDeque();
		else
			return wrap( new ArrayDeque< V >() );
	}

	public static < V extends Vertex< ? > > RefDeque< V > createVertexDeque( final ReadOnlyGraph< V, ? > graph, final int initialCapacity )
	{
		if ( graph instanceof DequeCreator )
			return ( ( DequeCreator< V, ? > ) graph ).createVertexDeque( initialCapacity );
		else
			return wrap( new ArrayDeque< V >( initialCapacity ) );
	}

	public static < E extends Edge< ? > > RefDeque< E > createEdgeDeque( final ReadOnlyGraph< ?, E > graph )
	{
		if ( graph instanceof DequeCreator )
			return ( ( DequeCreator< ?, E > ) graph ).createEdgeDeque();
		else
			return wrap( new ArrayDeque< E >() );
	}

	public static < E extends Edge< ? > > RefDeque< E > createEdgeDeque( final ReadOnlyGraph< ?, E > graph, final int initialCapacity )
	{
		if ( graph instanceof DequeCreator )
			return ( ( DequeCreator< ?, E > ) graph ).createEdgeDeque( initialCapacity );
		else
			return wrap( new ArrayDeque< E >( initialCapacity ) );
	}

	public static < V extends Vertex< ? > > RefStack< V > createVertexStack( final ReadOnlyGraph< V, ? > graph )
	{
		if ( graph instanceof StackCreator )
			return ( ( StackCreator< V, ? > ) graph ).createVertexStack();
		else
			return wrapAsStack( new ArrayDeque< V >() );
	}

	public static < V extends Vertex< ? > > RefStack< V > createVertexStack( final ReadOnlyGraph< V, ? > graph, final int initialCapacity )
	{
		if ( graph instanceof StackCreator )
			return ( ( StackCreator< V, ? > ) graph ).createVertexStack( initialCapacity );
		else
			return wrapAsStack( new ArrayDeque< V >( initialCapacity ) );
	}

	public static < E extends Edge< ? > > RefStack< E > createEdgeStack( final ReadOnlyGraph< ?, E > graph )
	{
		if ( graph instanceof StackCreator )
			return ( ( StackCreator< ?, E > ) graph ).createEdgeStack();
		else
			return wrapAsStack( new ArrayDeque< E >() );
	}

	public static < E extends Edge< ? > > RefStack< E > createEdgeStack( final ReadOnlyGraph< ?, E > graph, final int initialCapacity )
	{
		if ( graph instanceof StackCreator )
			return ( ( StackCreator< ?, E > ) graph ).createEdgeStack( initialCapacity );
		else
			return wrapAsStack( new ArrayDeque< E >( initialCapacity ) );
	}

	public static < E extends Edge< ? >, O > RefObjectMap< E, O > createEdgeObjectMap( final ReadOnlyGraph< ?, E > graph )
	{
		if ( graph instanceof MapCreator )
			return ( ( MapCreator< ?, E > ) graph ).createEdgeObjectMap();
		else
			return wrap( new HashMap< E, O >() );
	}

	public static < V extends Vertex< ? >, O > RefObjectMap< V, O > createVertexObjectMap( final ReadOnlyGraph< V, ? > graph )
	{
		if ( graph instanceof MapCreator )
			return ( ( MapCreator< V, ? > ) graph ).createVertexObjectMap();
		else
			return wrap( new HashMap< V, O >() );
	}

	public static < V extends Vertex< E >, E extends Edge< V > > RefRefMap< V, E > createVertexEdgeMap( final ReadOnlyGraph< V, E > graph )
	{
		if ( graph instanceof MapCreator )
			return ( ( MapCreator< V, E > ) graph ).createVertexEdgeMap();
		else
			return wrap( new HashMap< V, E >() );
	};

	public static < V extends Vertex< E >, E extends Edge< V > > RefRefMap< V, E > createVertexEdgeMap( final ReadOnlyGraph< V, E > graph, final int initialCapacity )
	{
		if ( graph instanceof MapCreator )
			return ( ( MapCreator< V, E > ) graph ).createVertexEdgeMap( initialCapacity );
		else
			return wrap( new HashMap< V, E >( initialCapacity ) );
	};

	public static < V extends Vertex< E >, E extends Edge< V > > RefRefMap< E, V > createEdgeVertexMap( final ReadOnlyGraph< V, E > graph )
	{
		if ( graph instanceof MapCreator )
			return ( ( MapCreator< V, E > ) graph ).createEdgeVertexMap();
		else
			return wrap( new HashMap< E, V >() );
	}

	public static < V extends Vertex< E >, E extends Edge< V > > RefRefMap< E, V > createEdgeVertexMap( final ReadOnlyGraph< V, E > graph, final int initialCapacity )
	{
		if ( graph instanceof MapCreator )
			return ( ( MapCreator< V, E > ) graph ).createEdgeVertexMap();
		else
			return wrap( new HashMap< E, V >() );
	}

	public static < V extends Vertex< ? > > RefRefMap< V, V > createVertexVertexMap( final ReadOnlyGraph< V, ? > graph )
	{
		if ( graph instanceof MapCreator )
			return ( ( MapCreator< V, ? > ) graph ).createVertexVertexMap();
		else
			return wrap( new HashMap< V, V >() );
	}

	public static < V extends Vertex< ? > > RefRefMap< V, V > createVertexVertexMap( final ReadOnlyGraph< V, ? > graph, final int initialCapacity )
	{
		if ( graph instanceof MapCreator )
			return ( ( MapCreator< V, ? > ) graph ).createVertexVertexMap( initialCapacity );
		else
			return wrap( new HashMap< V, V >( initialCapacity ) );
	}

	public static < E extends Edge< ? > > RefRefMap< E, E > createEdgeEdgeMap( final ReadOnlyGraph< ?, E > graph )
	{
		if ( graph instanceof MapCreator )
			return ( ( MapCreator< ?, E > ) graph ).createEdgeEdgeMap();
		else
			return wrap( new HashMap< E, E >() );
	}

	public static < E extends Edge< ? > > RefRefMap< E, E > createEdgeEdgeMap( final ReadOnlyGraph< ?, E > graph, final int initialCapacity )
	{
		if ( graph instanceof MapCreator )
			return ( ( MapCreator< ?, E > ) graph ).createEdgeEdgeMap( initialCapacity );
		else
			return wrap( new HashMap< E, E >( initialCapacity ) );
	}

	public static < V extends Vertex< ? > > RefIntMap< V > createVertexIntMap( final ReadOnlyGraph< V, ? > graph, final int noEntryValue )
	{
		if ( graph instanceof MapCreator )
			return ( ( MapCreator< V, ? > ) graph ).createVertexIntMap( noEntryValue );
		else
			return new RefIntMapWrapper< V >( noEntryValue );
	}

	public static < V extends Vertex< ? > > RefIntMap< V > createVertexIntMap( final ReadOnlyGraph< V, ? > graph, final int noEntryValue, final int initialCapacity )
	{
		if ( graph instanceof MapCreator )
			return ( ( MapCreator< V, ? > ) graph ).createVertexIntMap( noEntryValue, initialCapacity );
		else
			return new RefIntMapWrapper< V >( noEntryValue, initialCapacity );
	}

	public static < V extends Vertex< ? > > IntRefMap< V > createIntVertexMap( final ReadOnlyGraph< V, ? > graph, final int noEntryKey )
	{
		if ( graph instanceof MapCreator )
			return ( ( MapCreator< V, ? > ) graph ).createIntVertexMap( noEntryKey );
		else
			return new IntRefMapWrapper< V >( noEntryKey );
	}

	public static < V extends Vertex< ? > > IntRefMap< V > createIntVertexMap( final ReadOnlyGraph< V, ? > graph, final int noEntryKey, final int initialCapacity )
	{
		if ( graph instanceof MapCreator )
			return ( ( MapCreator< V, ? > ) graph ).createIntVertexMap( noEntryKey, initialCapacity );
		else
			return new IntRefMapWrapper< V >( noEntryKey, initialCapacity );
	}

	public static < E extends Edge< ? > > RefIntMap< E > createEdgeIntMap( final ReadOnlyGraph< ?, E > graph, final int noEntryValue )
	{
		if ( graph instanceof MapCreator )
			return ( ( MapCreator< ?, E > ) graph ).createEdgeIntMap( noEntryValue );
		else
			return new RefIntMapWrapper< E >( noEntryValue );
	}

	public static < E extends Edge< ? > > RefIntMap< E > createEdgeIntMap( final ReadOnlyGraph< ?, E > graph, final int noEntryValue, final int initialCapacity )
	{
		if ( graph instanceof MapCreator )
			return ( ( MapCreator< ?, E > ) graph ).createEdgeIntMap( noEntryValue, initialCapacity );
		else
			return new RefIntMapWrapper< E >( noEntryValue, initialCapacity );
	}

	@SuppressWarnings( { "unchecked", "rawtypes" } )
	public static < O > Iterator< O > safeIterator( final Iterator< O > iterator )
	{
		if ( iterator instanceof MaybeRefIterator )
			if ( ( ( MaybeRefIterator ) iterator ).isRefIterator() )
				return new SafePoolObjectIteratorWrapper( iterator );
		return iterator;
	}

	public static interface SetCreator< V extends Vertex< E >, E extends Edge< V > > extends ReadOnlyGraph< V, E >
	{
		public RefSet< V > createVertexSet();

		public RefSet< V > createVertexSet( final int initialCapacity );

		public RefSet< E > createEdgeSet();

		public RefSet< E > createEdgeSet( final int initialCapacity );

	}

	public static interface ListCreator< V extends Vertex< E >, E extends Edge< V > > extends ReadOnlyGraph< V, E >
	{
		public RefList< V > createVertexList();

		public RefList< V > createVertexList( final int initialCapacity );

		public RefList< E > createEdgeList();

		public RefList< E > createEdgeList( final int initialCapacity );
	}

	public static interface DequeCreator< V extends Vertex< E >, E extends Edge< V > > extends ReadOnlyGraph< V, E >
	{
		public RefDeque< V > createVertexDeque();

		public RefDeque< V > createVertexDeque( final int initialCapacity );

		public RefDeque< E > createEdgeDeque();

		public RefDeque< E > createEdgeDeque( final int initialCapacity );
	}

	public static interface StackCreator< V extends Vertex< E >, E extends Edge< V > > extends ReadOnlyGraph< V, E >
	{
		public RefStack< V > createVertexStack();

		public RefStack< V > createVertexStack( final int initialCapacity );

		public RefStack< E > createEdgeStack();

		public RefStack< E > createEdgeStack( final int initialCapacity );
	}

	public static interface MapCreator< V extends Vertex< E >, E extends Edge< V > > extends ReadOnlyGraph< V, E >
	{
		public < O > RefObjectMap< V, O > createVertexObjectMap();

		public < O > RefObjectMap< E, O > createEdgeObjectMap();

		public RefRefMap< V, E > createVertexEdgeMap();

		public RefRefMap< V, E > createVertexEdgeMap( int initialCapacity );

		public RefRefMap< E, V > createEdgeVertexMap();

		public RefRefMap< E, V > createEdgeVertexMap( int initialCapacity );

		public RefRefMap< V, V > createVertexVertexMap();

		public RefRefMap< V, V > createVertexVertexMap( int initialCapacity );

		public RefRefMap< E, E > createEdgeEdgeMap();

		public RefRefMap< E, E > createEdgeEdgeMap( int initialCapacity );

		public RefIntMap< V > createVertexIntMap( int noEntryValue );

		public RefIntMap< V > createVertexIntMap( int noEntryValue, int initialCapacity );

		public RefIntMap< E > createEdgeIntMap( int noEntryValue );

		public RefIntMap< E > createEdgeIntMap( int noEntryValue, int initialCapacity );

		public IntRefMap< V > createIntVertexMap( int noEntryKey );

		public IntRefMap< V > createIntVertexMap( int noEntryKey, int initialCapacity );
	}

	public static interface CollectionCreator< V extends Vertex< E >, E extends Edge< V > > extends
			SetCreator< V, E >,
			ListCreator< V, E >,
			DequeCreator< V, E >,
			StackCreator< V, E >,
			MapCreator< V, E >
	{}

	private static < O > RefSet< O > wrap( final Set< O > set )
	{
		return new RefSetWrapper< O >( set );
	}

	private static < O > RefList< O > wrap( final List< O > set )
	{
		return new RefListWrapper< O >( set );
	}

	private static < O > RefDeque< O > wrap( final Deque< O > set )
	{
		return new RefDequeWrapper< O >( set );
	}

	private static < O > RefStack< O > wrapAsStack( final Deque< O > set )
	{
		return new RefStackWrapper< O >( set );
	}

	private static < K, O > RefRefMap< K, O > wrap( final Map< K, O > map )
	{
		return new RefMapWrapper< K, O >( map );
	}
}
