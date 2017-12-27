package org.mastodon.revised.io.mamut;

import static org.mastodon.revised.model.mamut.trackmate.TrackMateXMLKeys.EDGE_FEATURE_DECLARATION_TAG;
import static org.mastodon.revised.model.mamut.trackmate.TrackMateXMLKeys.EDGE_SOURCE_ATTRIBUTE_NAME;
import static org.mastodon.revised.model.mamut.trackmate.TrackMateXMLKeys.EDGE_TAG;
import static org.mastodon.revised.model.mamut.trackmate.TrackMateXMLKeys.EDGE_TARGET_ATTRIBUTE_NAME;
import static org.mastodon.revised.model.mamut.trackmate.TrackMateXMLKeys.FEATURE_ATTRIBUTE_NAME;
import static org.mastodon.revised.model.mamut.trackmate.TrackMateXMLKeys.FEATURE_DECLARATION_TAG;
import static org.mastodon.revised.model.mamut.trackmate.TrackMateXMLKeys.FEATURE_DIMENSION_ATTRIBUTE_NAME;
import static org.mastodon.revised.model.mamut.trackmate.TrackMateXMLKeys.FEATURE_ISINT_ATTRIBUTE_NAME;
import static org.mastodon.revised.model.mamut.trackmate.TrackMateXMLKeys.FEATURE_NAME_ATTRIBUTE_NAME;
import static org.mastodon.revised.model.mamut.trackmate.TrackMateXMLKeys.FEATURE_SHORT_NAME_ATTRIBUTE_NAME;
import static org.mastodon.revised.model.mamut.trackmate.TrackMateXMLKeys.FEATURE_TAG;
import static org.mastodon.revised.model.mamut.trackmate.TrackMateXMLKeys.FILENAME_ATTRIBUTE;
import static org.mastodon.revised.model.mamut.trackmate.TrackMateXMLKeys.FILTERED_TRACKS_TAG;
import static org.mastodon.revised.model.mamut.trackmate.TrackMateXMLKeys.FOLDER_ATTRIBUTE;
import static org.mastodon.revised.model.mamut.trackmate.TrackMateXMLKeys.FRAME_ATTRIBUTE_NAME;
import static org.mastodon.revised.model.mamut.trackmate.TrackMateXMLKeys.ID_ATTRIBUTE_NAME;
import static org.mastodon.revised.model.mamut.trackmate.TrackMateXMLKeys.IMAGE_DATA_TAG;
import static org.mastodon.revised.model.mamut.trackmate.TrackMateXMLKeys.LABEL_ATTRIBUTE_NAME;
import static org.mastodon.revised.model.mamut.trackmate.TrackMateXMLKeys.MODEL_TAG;
import static org.mastodon.revised.model.mamut.trackmate.TrackMateXMLKeys.QUALITY_ATTRIBUTE_NAME;
import static org.mastodon.revised.model.mamut.trackmate.TrackMateXMLKeys.RADIUS_ATTRIBUTE_NAME;
import static org.mastodon.revised.model.mamut.trackmate.TrackMateXMLKeys.SETTINGS_TAG;
import static org.mastodon.revised.model.mamut.trackmate.TrackMateXMLKeys.SPATIAL_UNITS_ATTRIBUTE_NAME;
import static org.mastodon.revised.model.mamut.trackmate.TrackMateXMLKeys.SPOT_COLLECTION_NSPOTS_ATTRIBUTE_NAME;
import static org.mastodon.revised.model.mamut.trackmate.TrackMateXMLKeys.SPOT_COLLECTION_TAG;
import static org.mastodon.revised.model.mamut.trackmate.TrackMateXMLKeys.SPOT_ELEMENT_TAG;
import static org.mastodon.revised.model.mamut.trackmate.TrackMateXMLKeys.SPOT_FEATURE_DECLARATION_TAG;
import static org.mastodon.revised.model.mamut.trackmate.TrackMateXMLKeys.SPOT_FRAME_COLLECTION_TAG;
import static org.mastodon.revised.model.mamut.trackmate.TrackMateXMLKeys.TIME_UNITS_ATTRIBUTE_NAME;
import static org.mastodon.revised.model.mamut.trackmate.TrackMateXMLKeys.TRACKMATE_TAG;
import static org.mastodon.revised.model.mamut.trackmate.TrackMateXMLKeys.TRACK_COLLECTION_TAG;
import static org.mastodon.revised.model.mamut.trackmate.TrackMateXMLKeys.TRACK_FEATURE_DECLARATION_TAG;
import static org.mastodon.revised.model.mamut.trackmate.TrackMateXMLKeys.TRACK_ID_ATTRIBUTE_NAME;
import static org.mastodon.revised.model.mamut.trackmate.TrackMateXMLKeys.TRACK_ID_TAG;
import static org.mastodon.revised.model.mamut.trackmate.TrackMateXMLKeys.TRACK_NAME_ATTRIBUTE_NAME;
import static org.mastodon.revised.model.mamut.trackmate.TrackMateXMLKeys.TRACK_TAG;
import static org.mastodon.revised.model.mamut.trackmate.TrackMateXMLKeys.T_ATTRIBUTE_NAME;
import static org.mastodon.revised.model.mamut.trackmate.TrackMateXMLKeys.X_ATTRIBUTE_NAME;
import static org.mastodon.revised.model.mamut.trackmate.TrackMateXMLKeys.Y_ATTRIBUTE_NAME;
import static org.mastodon.revised.model.mamut.trackmate.TrackMateXMLKeys.Z_ATTRIBUTE_NAME;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.mastodon.collection.RefCollections;
import org.mastodon.collection.RefList;
import org.mastodon.collection.RefSet;
import org.mastodon.graph.algorithm.RootFinder;
import org.mastodon.graph.algorithm.traversal.DepthFirstSearch;
import org.mastodon.graph.algorithm.traversal.GraphSearch.SearchDirection;
import org.mastodon.graph.algorithm.traversal.SearchListener;
import org.mastodon.properties.PropertyMap;
import org.mastodon.revised.mamut.MamutProject;
import org.mastodon.revised.model.feature.Feature;
import org.mastodon.revised.model.feature.FeatureModel;
import org.mastodon.revised.model.feature.FeatureProjection;
import org.mastodon.revised.model.mamut.Link;
import org.mastodon.revised.model.mamut.Model;
import org.mastodon.revised.model.mamut.Spot;
import org.mastodon.spatial.SpatioTemporalIndex;

import mpicbg.spim.data.SpimDataException;
import mpicbg.spim.data.XmlKeys;
import mpicbg.spim.data.sequence.TimePoint;
import mpicbg.spim.data.sequence.TimePoints;
import mpicbg.spim.data.sequence.XmlIoTimePoints;

/**
 * Class to export a Mastodon project to MaMuT Fiji plugin.
 *
 * @author Jean-Yves Tinevez
 *
 */
public class MamutExporter
{

	private final Model model;

	private final MamutProject project;

	private final Element root;

	private MamutExporter( final Model model, final MamutProject project )
	{
		this.model = model;
		this.project = project;
		this.root = new Element( TRACKMATE_TAG );
	}

	private void write( final File file )
	{
		try (FileOutputStream fos = new FileOutputStream( file ))
		{
			final Document document = new Document( root );
			final XMLOutputter outputter = new XMLOutputter( Format.getPrettyFormat() );
			outputter.output( document, fos );
		}
		catch ( final FileNotFoundException e )
		{
			e.printStackTrace();
		}
		catch ( final IOException e )
		{
			e.printStackTrace();
		}
	}

	private void appendModel()
	{
		// Read space units from dataset xml if we can.
		String spaceUnits = "pixel";
		final Document document = getSAXParsedDocument( project.getDatasetXmlFile().getAbsolutePath() );
		final List< Element > viewSetupsElements = document
				.getRootElement()
				.getChild( XmlKeys.SEQUENCEDESCRIPTION_TAG )
				.getChild( XmlKeys.VIEWSETUPS_TAG )
				.getChildren( XmlKeys.VIEWSETUP_TAG );
		for ( final Element vsEl : viewSetupsElements )
		{
			final Element vs, uel;
			if ( null != ( vs = vsEl.getChild( XmlKeys.VIEWSETUP_VOXELSIZE_TAG ) ) && null != ( uel = vs.getChild( XmlKeys.VOXELDIMENSIONS_UNIT_TAG ) ) )
			{
				spaceUnits = uel.getValue();
				break;
			}
		}

		// BDV does not let you save frame interval in physical units.
		final String timeUnits = "frames";

		final Element modelElement = new Element( MODEL_TAG );
		modelElement.setAttribute( SPATIAL_UNITS_ATTRIBUTE_NAME, spaceUnits );
		modelElement.setAttribute( TIME_UNITS_ATTRIBUTE_NAME, timeUnits );

		final Element featureDeclarationElement = featuresDeclarationToXml();
		modelElement.addContent( featureDeclarationElement );

		final Element spotElement = spotCollectionToXml();
		modelElement.addContent( spotElement );

		final Element[] tracksElements = trackCollectionToXml();
		for ( final Element element : tracksElements )
			modelElement.addContent( element );

		root.addContent( modelElement );
	}

	private void appendSettings()
	{
		final Element settingsElement = new Element(SETTINGS_TAG);

		final Element imageDataElement = imageDataToXml();
		settingsElement.addContent( imageDataElement );

		root.addContent( settingsElement );
	}

	private Element imageDataToXml()
	{
		final Collection< Attribute > attributes = new ArrayList<>();

		// File path.
		final File datasetXmlFile = project.getDatasetXmlFile();
		attributes.add( new Attribute( FILENAME_ATTRIBUTE, datasetXmlFile.getName() ) );
		attributes.add( new Attribute( FOLDER_ATTRIBUTE, datasetXmlFile.getParent() ) );

		// Image attributes.
		final Document document = getSAXParsedDocument( project.getDatasetXmlFile().getAbsolutePath() );

		final List< Element > viewSetupsElements = document
				.getRootElement()
				.getChild( XmlKeys.SEQUENCEDESCRIPTION_TAG )
				.getChild( XmlKeys.VIEWSETUPS_TAG )
				.getChildren( XmlKeys.VIEWSETUP_TAG );
		double pixelWidth = 1.;
		double pixelHeight = 1.0;
		double voxelDepth = 1.0;
		for ( final Element vsEl : viewSetupsElements )
		{
			final Element vs, uel;
			if ( null != ( vs = vsEl.getChild( XmlKeys.VIEWSETUP_VOXELSIZE_TAG ) ) && null != ( uel = vs.getChild( XmlKeys.VOXELDIMENSIONS_SIZE_TAG ) ) )
			{
				final String val = uel.getContent( 0 ).getValue();
				final double[] calibration = Arrays.stream( val.split( " " ) )
						.mapToDouble( Double::parseDouble )
						.toArray();
				pixelWidth = calibration[ 0 ];
				pixelHeight = calibration[ 1 ];
				voxelDepth = calibration[ 2 ];
			}
		}

		final Element timePointsElement = document
				.getRootElement()
				.getChild( XmlKeys.SEQUENCEDESCRIPTION_TAG )
				.getChild( XmlKeys.TIMEPOINTS_TAG );
		int nframes = 1;
		final XmlIoTimePoints xmlIoTimePoints = new XmlIoTimePoints();
		try
		{
			final TimePoints timePoints = xmlIoTimePoints.fromXml( timePointsElement );
			nframes = timePoints.size();
		}
		catch ( final SpimDataException e )
		{
			e.printStackTrace();
		}

		final Element imageDataElement = new Element( IMAGE_DATA_TAG );
		return imageDataElement;
	}

	private Element[] trackCollectionToXml()
	{
		/*
		 * Track collection element.
		 */
		final Element allTracksElement = new Element( TRACK_COLLECTION_TAG );

		// Collect roots, as candidates for single tracks.
		final RefList< Spot > roots = RefCollections.createRefList( model.getGraph().vertices() );
		roots.addAll( RootFinder.getRoots( model.getGraph() ) );

		// Sort by ID (not needed but hey).
		final Comparator< Spot > labelComparator = ( o1, o2 ) -> Integer.compare( o1.getInternalPoolIndex(), o2.getInternalPoolIndex() );
		roots.sort( labelComparator );

		/*
		 * We will iterate the graph, cross component by cross component, to
		 * serialize the tracks.
		 */
		final DepthFirstSearch< Spot, Link > search = new DepthFirstSearch<>( model.getGraph(), SearchDirection.UNDIRECTED );
		final RefSet< Spot > toSkip = RefCollections.createRefSet( model.getGraph().vertices() );
		final RefList< Spot > iteratedRoots = RefCollections.createRefList( model.getGraph().vertices() );

		for ( final Spot root : roots )
		{
			// Skip over the roots that were path of a track already dealt with.
			if ( toSkip.contains( root ) )
				continue;

			// Create the track element.
			final Element trackElement = trackToXml( root );
			allTracksElement.addContent( trackElement );

			final SearchListener< Spot, Link, DepthFirstSearch< Spot, Link > > searchListener = new SearchListener< Spot, Link, DepthFirstSearch< Spot, Link > >()
			{

				@Override
				public void processVertexLate( final Spot vertex, final DepthFirstSearch< Spot, Link > search )
				{
					/*
					 * 1 root = 1 track, unless a track has several roots. Add
					 * the iterated vertex to the list of root to skip if
					 * needed.
					 */
					if ( vertex.incomingEdges().isEmpty() )
						toSkip.add( vertex );
				}

				@Override
				public void processVertexEarly( final Spot vertex, final DepthFirstSearch< Spot, Link > search )
				{}

				@Override
				public void processEdge( final Link edge, final Spot from, final Spot to, final DepthFirstSearch< Spot, Link > search )
				{
					// Add iterated edge to the track element.
					final Element edgeElement = edgeToXml( edge, from.getInternalPoolIndex(), to.getInternalPoolIndex() );
					trackElement.addContent( edgeElement );
				}

				@Override
				public void crossComponent( final Spot from, final Spot to, final DepthFirstSearch< Spot, Link > search )
				{}
			};
			search.setTraversalListener( searchListener );
			search.start( root );
			iteratedRoots.add( root );
		}

		/*
		 * Filtered track collection element.
		 */

		final Element filteredTracksElement = new Element( FILTERED_TRACKS_TAG );
		for ( final Spot spot : iteratedRoots )
		{
			final Element filteredTrackID = new Element( TRACK_ID_TAG );
			filteredTrackID.setAttribute( TRACK_ID_ATTRIBUTE_NAME, Integer.toString( spot.getInternalPoolIndex() ) );
			filteredTracksElement.addContent( filteredTrackID );
		}

		return new Element[] { allTracksElement, filteredTracksElement };
	}

	private Element spotCollectionToXml()
	{
		final Element spotCollectionElement = new Element( SPOT_COLLECTION_TAG );
		spotCollectionElement.setAttribute( SPOT_COLLECTION_NSPOTS_ATTRIBUTE_NAME, "" + model.getGraph().vertices().size() );

		// Read time points from dataset xml.
		List< TimePoint > tps = null;
		try
		{
			final Document document = getSAXParsedDocument( project.getDatasetXmlFile().getAbsolutePath() );
			final Element timePointsElement = document
					.getRootElement()
					.getChild( XmlKeys.SEQUENCEDESCRIPTION_TAG )
					.getChild( XmlKeys.TIMEPOINTS_TAG );
			final XmlIoTimePoints xmlIoTimePoints = new XmlIoTimePoints();
			final TimePoints timePoints = xmlIoTimePoints.fromXml( timePointsElement );
			tps = timePoints.getTimePointsOrdered();
		}
		catch ( final SpimDataException e )
		{
			e.printStackTrace();
		}

		final SpatioTemporalIndex< Spot > spots = model.getSpatioTemporalIndex();
		for ( final TimePoint tp : tps )
		{

			final Element frameSpotsElement = new Element( SPOT_FRAME_COLLECTION_TAG );
			frameSpotsElement.setAttribute( FRAME_ATTRIBUTE_NAME, tp.getName() );

			for ( final Spot spot : spots.getSpatialIndex( tp.getId() ) )
			{
				final Element spotElement = spotToXml( spot );
				frameSpotsElement.addContent( spotElement );
			}
			spotCollectionElement.addContent( frameSpotsElement );
		}

		return spotCollectionElement;
	}

	private Element edgeToXml( final Link edge, final int sourceSpotID, final int targetSpotID )
	{
		final Collection< Attribute > attributes = new ArrayList<>();

		// Source and target ID.
		attributes.add( new Attribute( EDGE_SOURCE_ATTRIBUTE_NAME, Integer.toString( sourceSpotID ) ) );
		attributes.add( new Attribute( EDGE_TARGET_ATTRIBUTE_NAME, Integer.toString( targetSpotID ) ) );

		// Link features.
		final FeatureModel fm = model.getFeatureModel();
		Set< Feature< ?, ? > > features = fm.getFeatureSet( Link.class );
		if ( null == features )
			features = Collections.emptySet();

		for ( final Feature< ?, ? > feature : features )
		{
			@SuppressWarnings( "unchecked" )
			final Feature< Link, ? > f = ( Feature< Link, PropertyMap< Link, ? > > ) feature;
			final Map< String, FeatureProjection< Link > > projections = f.getProjections();
			for ( final String projectionKey : projections.keySet() )
				attributes.add( new Attribute( projectionKey, "" + projections.get( projectionKey ).value( edge ) ) );
		}

		final Element edgeElement = new Element( EDGE_TAG );
		edgeElement.setAttributes( attributes );
		return edgeElement;
	}

	private Element trackToXml( final Spot root )
	{
		final Collection< Attribute > attributes = new ArrayList<>();

		// Track name.
		attributes.add( new Attribute( TRACK_NAME_ATTRIBUTE_NAME, root.getLabel() ) );

		// Track ID.
		attributes.add( new Attribute( TRACK_ID_ATTRIBUTE_NAME, Integer.toString( root.getInternalPoolIndex() ) ) );

		// Other track features.
		// TODO: when we compute and store track features, modify this.
//		final FeatureModel fm = model.getFeatureModel();
		Set< Feature< ?, ? > > features = null;
		if ( null == features )
			features = Collections.emptySet();

		for ( final Feature< ?, ? > feature : features )
		{
			@SuppressWarnings( "unchecked" )
			final Feature< Spot, ? > f = ( Feature< Spot, PropertyMap< Spot, ? > > ) feature;
			final Map< String, FeatureProjection< Spot > > projections = f.getProjections();
			for ( final String projectionKey : projections.keySet() )
				attributes.add( new Attribute( projectionKey, "" + projections.get( projectionKey ).value( root ) ) );
		}

		final Element trackElement = new Element( TRACK_TAG );
		trackElement.setAttributes( attributes );
		return trackElement;
	}

	private Element spotToXml( final Spot spot )
	{
		final Collection< Attribute > attributes = new ArrayList<>();

		// Id.
		attributes.add( new Attribute( ID_ATTRIBUTE_NAME, "" + spot.getInternalPoolIndex() ) );
		// Name.
		attributes.add( new Attribute( LABEL_ATTRIBUTE_NAME, spot.getLabel() ) );
		// Position.
		attributes.add( new Attribute( X_ATTRIBUTE_NAME, "" + spot.getDoublePosition( 0 ) ) );
		attributes.add( new Attribute( Y_ATTRIBUTE_NAME, "" + spot.getDoublePosition( 1 ) ) );
		attributes.add( new Attribute( Z_ATTRIBUTE_NAME, "" + spot.getDoublePosition( 2 ) ) );
		// Radius.
		attributes.add( new Attribute( RADIUS_ATTRIBUTE_NAME, "" + Math.sqrt( spot.getBoundingSphereRadiusSquared() ) ) );
		// Frame and time.
		attributes.add( new Attribute( FRAME_ATTRIBUTE_NAME, "" + spot.getTimepoint() ) );
		attributes.add( new Attribute( T_ATTRIBUTE_NAME, "" + spot.getTimepoint() ) );
		// Quality.
		attributes.add( new Attribute( QUALITY_ATTRIBUTE_NAME, "-1" ) );

		// Spot features.
		final FeatureModel fm = model.getFeatureModel();
		Set< Feature< ?, ? > > features = fm.getFeatureSet( Spot.class );
		if ( null == features )
			features = Collections.emptySet();

		for ( final Feature< ?, ? > feature : features )
		{
			@SuppressWarnings( "unchecked" )
			final Feature< Spot, ? > f = ( Feature< Spot, PropertyMap< Spot, ? > > ) feature;
			final Map< String, FeatureProjection< Spot > > projections = f.getProjections();
			for ( final String projectionKey : projections.keySet() )
				attributes.add( new Attribute( projectionKey, "" + projections.get( projectionKey ).value( spot ) ) );
		}

		final Element spotElement = new Element( SPOT_ELEMENT_TAG );
		spotElement.setAttributes( attributes );
		return spotElement;
	}

	private Element featuresDeclarationToXml()
	{
		final Element featuresElement = new Element( FEATURE_DECLARATION_TAG );
		appendFeaturesDeclarationOfClass( Spot.class, featuresElement, SPOT_FEATURE_DECLARATION_TAG );
		appendFeaturesDeclarationOfClass( Link.class, featuresElement, EDGE_FEATURE_DECLARATION_TAG );
		appendFeaturesDeclarationOfClass( Link.class, featuresElement, TRACK_FEATURE_DECLARATION_TAG );
		return featuresElement;
	}

	private void appendFeaturesDeclarationOfClass( final Class< ? > clazz, final Element featuresElement, final String classFeatureDeclarationTag )
	{
		final FeatureModel fm = model.getFeatureModel();
		Set< Feature< ?, ? > > features = fm.getFeatureSet( clazz );
		if ( null == features )
			features = Collections.emptySet();

		final Element classFeaturesElement = new Element( classFeatureDeclarationTag );
		for ( final Feature< ?, ? > feature : features )
		{
			// We actually export feature projections.
			final Map< String, ? > projections = feature.getProjections();
			for ( final String projectionKey : projections.keySet() )
			{
				final Element fel = new Element( FEATURE_TAG );
				fel.setAttribute( FEATURE_ATTRIBUTE_NAME, projectionKey );
				// Mastodon does not support feature name yet.
				fel.setAttribute( FEATURE_NAME_ATTRIBUTE_NAME, projectionKey );
				fel.setAttribute( FEATURE_SHORT_NAME_ATTRIBUTE_NAME, projectionKey );
				// Mastodon does not support feature dimension yet.
				fel.setAttribute( FEATURE_DIMENSION_ATTRIBUTE_NAME, "NONE" );
				fel.setAttribute( FEATURE_ISINT_ATTRIBUTE_NAME, "false" );
				classFeaturesElement.addContent( fel );
			}
		}
		featuresElement.addContent( classFeaturesElement );
	}

	private static Document getSAXParsedDocument( final String fileName )
	{
		final SAXBuilder builder = new SAXBuilder();
		Document document = null;
		try
		{
			document = builder.build( fileName );
		}
		catch ( JDOMException | IOException e )
		{
			e.printStackTrace();
		}
		return document;
	}

	public static final void export( final File target, final Model model, final MamutProject project )
	{
		final MamutExporter exporter = new MamutExporter( model, project );
		exporter.appendModel();
		exporter.appendSettings();

//		System.out.println( new XMLOutputter( Format.getPrettyFormat() ).outputString( new Document( exporter.root ) ) ); // DEBUG
	}

	public static void main( final String[] args ) throws IOException
	{
//		final String bdvFile = "samples/datasethdf5.xml";
		final String bdvFile = "/Users/tinevez/Desktop/FakeTracks.xml";
		final String modelFile = "samples/model_revised.raw";
		final MamutProject project = new MamutProject( new File( "." ), new File( bdvFile ), new File( modelFile ) );
		final Model model = new Model();
		model.loadRaw( project.getRawModelFile() );
		final File target = new File( "samples/mamutExport.xml" );
		export( target, model, project );

	}

}