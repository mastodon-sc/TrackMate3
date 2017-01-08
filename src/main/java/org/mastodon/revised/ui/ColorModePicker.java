package org.mastodon.revised.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.mastodon.revised.model.feature.FeatureKeys;
import org.mastodon.revised.model.feature.FeatureRangeCalculator;
import org.mastodon.revised.model.feature.FeatureTarget;
import org.mastodon.revised.ui.ColorMode.EdgeColorMode;
import org.mastodon.revised.ui.ColorMode.VertexColorMode;
import org.mastodon.revised.ui.util.CategoryJComboBox;
import org.mastodon.revised.ui.util.ColorMap;

public class ColorModePicker extends JPanel
{

	private static final long serialVersionUID = 1L;

	private static final Dimension PREFERRED_SIZE = new Dimension( 400, 200 );

	private final CategoryJComboBox< VertexColorMode, FeatureKeyWrapper > colorVertexChoices;

	private final JComboBox< String > cmapVertex;

	private final ColorMapPainter colorMapPainterVertex;

	private final JFormattedTextField minVertex;

	private final JFormattedTextField maxVertex;

	private final JButton autoscaleVertex;

	private final CategoryJComboBox< EdgeColorMode, FeatureKeyWrapper > colorEdgeChoices;

	private final JComboBox< String > cmapEdge;

	private final ColorMapPainter colorMapPainterEdge;

	private final JFormattedTextField minEdge;

	private final JFormattedTextField maxEdge;

	private final JButton autoscaleEdge;

	private final FeatureRangeCalculator featureRangeCalculator;

	private final FeatureRangeCalculator branchGraphFeatureRangeCalculator;

	private final ColorMode current;

	public ColorModePicker( final ColorMode current,
			final FeatureKeys featureKeys,
			final FeatureRangeCalculator featureRangeCalculator,
			final FeatureKeys branchGraphFeatureKeys,
			final FeatureRangeCalculator branchGraphFeatureRangeCalculator )
	{
		super( new GridBagLayout() );
		this.current = current;
		this.featureRangeCalculator = featureRangeCalculator;
		this.branchGraphFeatureRangeCalculator = branchGraphFeatureRangeCalculator;

		/*
		 * Current settings.
		 */

		final VertexColorMode vertexColorMode = current.getVertexColorMode();
		final String vertexFeatureKey = current.getVertexFeatureKey();
		final ColorMap currentCMap1 = current.getVertexColorMap();
		final double minVertexColorRange = current.getMinVertexColorRange();
		final double maxVertexColorRange = current.getMaxVertexColorRange();
		final EdgeColorMode edgeColorMode = current.getEdgeColorMode();
		final String edgeFeatureKey = current.getEdgeFeatureKey();
		final ColorMap currentCMap2 = current.getEdgeColorMap();
		final double minEdgeColorRange = current.getMinEdgeColorRange();
		final double maxEdgeColorRange = current.getMaxEdgeColorRange();

		/*
		 * Layout panel.
		 */

		final GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets( 2, 5, 2, 5 );
		c.ipadx = 0;
		c.ipady = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.RELATIVE;

		/*
		 * Listeners.
		 */
		final ActionListener action = ( ActionListener ) ( e ) -> fireSettingsChanged();
		final FocusListener focus = new FocusListener()
		{
			@Override
			public void focusLost( final FocusEvent e )
			{
				fireSettingsChanged();
			}

			@Override
			public void focusGained( final FocusEvent e )
			{}
		};
		final ActionListener autoscaleVertexAction = new ActionListener()
		{
			@Override
			public void actionPerformed( final ActionEvent e )
			{
				new Thread( "Vertex feature range calculation thread." )
				{
					@Override
					public void run()
					{
						autoScaleVertexFeature();
					}
				}.start();
			}
		};
		final ActionListener autoscaleEdgeAction = new ActionListener()
		{
			@Override
			public void actionPerformed( final ActionEvent e )
			{
				new Thread( "Edge feature range calculation thread." )
				{
					@Override
					public void run()
					{
						autoScaleEdgeFeature();
					}
				}.start();
			}
		};

		/*
		 * Vertices.
		 */

		c.gridx = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.;
		c.gridwidth = 1;
		final JLabel lbl1 = new JLabel( "Color vertices by" );
		lbl1.setFont( getFont().deriveFont( Font.BOLD ) );
		lbl1.setHorizontalAlignment( SwingConstants.RIGHT );
		add( lbl1, c );

		c.gridx++;
		c.weightx = 1.;
		c.gridwidth = 3;
		colorVertexChoices = vertexColorBy( featureKeys, branchGraphFeatureKeys, vertexColorMode, vertexFeatureKey );
		add( colorVertexChoices, c );

		// Colormap and ranges.
		c.gridx = 0;
		c.weightx = 0.;
		c.gridwidth = 1;
		c.gridy++;
		final JLabel lbl3 = new JLabel( "ColorMap" );
		lbl3.setHorizontalAlignment( SwingConstants.RIGHT );
		add( lbl3, c );

		c.gridx = 1;
		c.gridwidth = 1;
		cmapVertex = new JComboBox<>(
				ColorMap.getColorMapNames().toArray( new String[] {} ) );
		cmapVertex.setSelectedItem( currentCMap1.getName() );
		add( cmapVertex, c );

		c.gridx = 2;
		c.gridwidth = 2;
		colorMapPainterVertex = new ColorMapPainter( cmapVertex );
		add( colorMapPainterVertex, c );

		c.gridx = 0;
		c.weightx = 0.;
		c.gridwidth = 1;
		c.gridy++;
		final JLabel lbl5 = new JLabel( "Min/Max" );
		lbl5.setHorizontalAlignment( SwingConstants.RIGHT );
		add( lbl5, c );

		c.gridx = 1;
		c.weightx = 1.;
		c.gridwidth = 3;
		final JPanel scalePanel1 = new JPanel();
		final BoxLayout boxLayout1 = new BoxLayout( scalePanel1, BoxLayout.LINE_AXIS );
		scalePanel1.setLayout( boxLayout1 );

		minVertex = new JFormattedTextField( Double.valueOf( minVertexColorRange ) );
		minVertex.setHorizontalAlignment( SwingConstants.CENTER );
		scalePanel1.add( minVertex );
		maxVertex = new JFormattedTextField( Double.valueOf( maxVertexColorRange ) );
		maxVertex.setHorizontalAlignment( SwingConstants.CENTER );
		scalePanel1.add( maxVertex );
		autoscaleVertex = new JButton( "Autoscale" );
		scalePanel1.add( autoscaleVertex );
		add( scalePanel1, c );

		/*
		 * Wire up listeners.
		 */
		cmapVertex.addActionListener( action );
		minVertex.addActionListener( action );
		minVertex.addFocusListener( focus );
		maxVertex.addActionListener( action );
		maxVertex.addFocusListener( focus );
		autoscaleVertex.addActionListener( autoscaleVertexAction );

		final Collection< JComponent > toMute1 = Arrays.asList( new JComponent[] { cmapVertex, minVertex, maxVertex, autoscaleVertex, colorMapPainterVertex } );
		final ComponentMuter muter1 = new ComponentMuter( toMute1 );
		colorVertexChoices.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( final ActionEvent e )
			{
				switch ( colorVertexChoices.getSelectedCategory() )
				{
				case FIXED:
					muter1.enable( false );
					break;
				default:
					muter1.enable( true );
					break;
				}
				fireSettingsChanged();
			}
		} );
		muter1.enable( colorVertexChoices.getSelectedCategory() != VertexColorMode.FIXED );

		/*
		 * Edges.
		 */

		c.gridy++;
		add( Box.createVerticalStrut( 5 ), c );

		c.gridy++;
		c.gridx = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.;
		c.gridwidth = 1;
		final JLabel lbl2 = new JLabel( "Color edges by" );
		lbl2.setFont( getFont().deriveFont( Font.BOLD ) );
		lbl2.setHorizontalAlignment( SwingConstants.RIGHT );
		add( lbl2, c );

		c.gridx++;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.;
		c.gridwidth = 3;
		colorEdgeChoices = edgeColorBy( featureKeys, branchGraphFeatureKeys, edgeColorMode, edgeFeatureKey );
		add( colorEdgeChoices, c );

		// Colormap and ranges.
		c.gridx = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.;
		c.gridwidth = 1;
		c.gridy++;
		final JLabel lbl4 = new JLabel( "ColorMap" );
		lbl4.setHorizontalAlignment( SwingConstants.RIGHT );
		add( lbl4, c );

		c.gridx = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.;
		cmapEdge = new JComboBox<>( ColorMap.getColorMapNames().toArray( new String[] {} ) );

		cmapEdge.setSelectedItem( currentCMap2.getName() );
		add( cmapEdge, c );

		c.gridx = 2;
		c.gridwidth = 2;
		colorMapPainterEdge = new ColorMapPainter( cmapEdge );
		add( colorMapPainterEdge, c );

		c.gridx = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.;
		c.gridwidth = 1;
		c.gridy++;
		final JLabel lbl6 = new JLabel( "Min/Max" );
		lbl6.setHorizontalAlignment( SwingConstants.RIGHT );
		add( lbl6, c );

		c.gridx = 1;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.;
		final JPanel scalePanel2 = new JPanel();
		final BoxLayout boxLayout2 = new BoxLayout( scalePanel2, BoxLayout.LINE_AXIS );
		scalePanel2.setLayout( boxLayout2 );

		minEdge = new JFormattedTextField( Double.valueOf( minEdgeColorRange ) );
		minEdge.setHorizontalAlignment( SwingConstants.CENTER );
		scalePanel2.add( minEdge );
		maxEdge = new JFormattedTextField( Double.valueOf( maxEdgeColorRange ) );
		maxEdge.setHorizontalAlignment( SwingConstants.CENTER );
		scalePanel2.add( maxEdge );
		autoscaleEdge = new JButton( "Autoscale" );
		scalePanel2.add( autoscaleEdge );
		add( scalePanel2, c );

		/*
		 * Wire up edge listeners.
		 */

		cmapEdge.addActionListener( action );
		minEdge.addActionListener( action );
		minEdge.addFocusListener( focus );
		maxEdge.addActionListener( action );
		maxEdge.addFocusListener( focus );
		autoscaleEdge.addActionListener( autoscaleEdgeAction );
		final Collection< JComponent > toMute2 = Arrays.asList( new JComponent[] { cmapEdge, minEdge, maxEdge, autoscaleEdge, colorMapPainterEdge } );
		final ComponentMuter muter2 = new ComponentMuter( toMute2 );
		colorEdgeChoices.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( final ActionEvent e )
			{
				switch ( colorEdgeChoices.getSelectedCategory() )
				{
				case FIXED:
					muter2.enable( false );
					break;
				default:
					muter2.enable( true );
					break;
				}
				fireSettingsChanged();
			}
		} );
		muter2.enable( colorEdgeChoices.getSelectedCategory() != EdgeColorMode.FIXED );
	}

	private void fireSettingsChanged()
	{
		current
				.vertexColorMode( colorVertexChoices.getSelectedCategory() )
				.vertexColorFeatureKey( colorVertexChoices.getSelectedItem().featureKey )
				.vertexColorMap( ColorMap.getColorMap( ( String ) cmapVertex.getSelectedItem() ) )
				.minVertexColorRange( ( double ) minVertex.getValue() )
				.maxVertexColorRange( ( double ) maxVertex.getValue() )
				.edgeColorMode( colorEdgeChoices.getSelectedCategory() )
				.edgeColorFeatureKey( colorEdgeChoices.getSelectedItem().featureKey )
				.edgeColorMap( ColorMap.getColorMap( ( String ) cmapEdge.getSelectedItem() ) )
				.minEdgeColorRange( ( double ) minEdge.getValue() )
				.maxEdgeColorRange( ( double ) maxEdge.getValue() )
				.notifyListeners();
	}

	private void autoScaleVertexFeature()
	{
		colorVertexChoices.setEditable( false );
		minVertex.setEnabled( false );
		maxVertex.setEnabled( false );
		try
		{
			final VertexColorMode category = colorVertexChoices.getSelectedCategory();
			final String featureKey = colorVertexChoices.getSelectedItem().featureKey;
			final double[] range;
			switch ( category )
			{
			case BRANCH_EDGE:
			case BRANCH_VERTEX:
				range = branchGraphFeatureRangeCalculator.getRange( featureKey );
				break;
			default:
				range = featureRangeCalculator.getRange( featureKey );
				break;
			}
			minVertex.setValue( Double.valueOf( range[ 0 ] ) );
			maxVertex.setValue( Double.valueOf( range[ 1 ] ) );
		}
		finally
		{
			fireSettingsChanged();
			colorVertexChoices.setEditable( true );
			minVertex.setEnabled( true );
			maxVertex.setEnabled( true );
		}
	}

	private void autoScaleEdgeFeature()
	{
		colorEdgeChoices.setEditable( false );
		minEdge.setEnabled( false );
		maxEdge.setEnabled( false );
		try
		{
			final EdgeColorMode category = colorEdgeChoices.getSelectedCategory();
			final String featureKey = colorEdgeChoices.getSelectedItem().featureKey;
			final double[] range;
			switch ( category )
			{
			case BRANCH_EDGE:
			case BRANCH_VERTEX:
				range = branchGraphFeatureRangeCalculator.getRange( featureKey );
				break;
			default:
				range = featureRangeCalculator.getRange( featureKey );
				break;
			}
			minEdge.setValue( Double.valueOf( range[ 0 ] ) );
			maxEdge.setValue( Double.valueOf( range[ 1 ] ) );
		}
		finally
		{
			fireSettingsChanged();
			colorEdgeChoices.setEditable( true );
			minEdge.setEnabled( true );
			maxEdge.setEnabled( true );
		}
	}

	private static CategoryJComboBox< VertexColorMode, FeatureKeyWrapper > vertexColorBy(
			final FeatureKeys featureKeys,
			final FeatureKeys branchGraphFeatureKeys,
			final VertexColorMode currentMode,
			final String currentFeatureKey )
	{
		/*
		 * Harvest possible choices.
		 */

		final Map< VertexColorMode, Collection< FeatureKeyWrapper > > items = new LinkedHashMap<>();
		final Map< VertexColorMode, String > categoryNames = new HashMap<>();

		// Fixed.
		final FeatureKeyWrapper fixedColor = new FeatureKeyWrapper( "Fixed color" );
		items.put( VertexColorMode.FIXED, Collections.singleton( fixedColor ) );
		categoryNames.put( VertexColorMode.FIXED, "Fixed" );

		// This vertex.
		final Collection< FeatureKeyWrapper > vertexProjections = new ArrayList<>();
		for ( final String projectionKey : featureKeys.getProjectionKeys( FeatureTarget.VERTEX ) )
			vertexProjections.add( new FeatureKeyWrapper( projectionKey ) );
		if ( !vertexProjections.isEmpty() )
		{
			items.put( VertexColorMode.VERTEX, vertexProjections );
			categoryNames.put( VertexColorMode.VERTEX, "Vertex feature" );
		}

		// Incoming and outgoing edges.
		final Collection< FeatureKeyWrapper > incomingEdgeProjections = new ArrayList<>();
		final Collection< FeatureKeyWrapper > outgoingEdgeProjections = new ArrayList<>();
		for ( final String projectionKey : featureKeys.getProjectionKeys( FeatureTarget.EDGE ) )
		{
			incomingEdgeProjections.add( new FeatureKeyWrapper( projectionKey ) );
			outgoingEdgeProjections.add( new FeatureKeyWrapper( projectionKey ) );
		}
		if ( !incomingEdgeProjections.isEmpty() )
		{
			items.put( VertexColorMode.INCOMING_EDGE, incomingEdgeProjections );
			items.put( VertexColorMode.OUTGOING_EDGE, outgoingEdgeProjections );
			categoryNames.put( VertexColorMode.INCOMING_EDGE, "Incoming edge feature" );
			categoryNames.put( VertexColorMode.OUTGOING_EDGE, "Outgoing edge feature" );
		}

		// Branch vertex.
		final Collection< FeatureKeyWrapper > branchVertexProjections = new ArrayList<>();
		for ( final String projectionKey : branchGraphFeatureKeys.getProjectionKeys( FeatureTarget.VERTEX ) )
			branchVertexProjections.add( new FeatureKeyWrapper( projectionKey ) );
		if ( !branchVertexProjections.isEmpty() )
		{
			items.put( VertexColorMode.BRANCH_VERTEX, branchVertexProjections );
			categoryNames.put( VertexColorMode.BRANCH_VERTEX, "Branch vertex" );
		}

		// Branch edge.
		final Collection< FeatureKeyWrapper > branchEdgeProjections = new ArrayList<>();
		for ( final String projectionKey : branchGraphFeatureKeys.getProjectionKeys( FeatureTarget.EDGE ) )
			branchEdgeProjections.add( new FeatureKeyWrapper( projectionKey ) );
		if ( !branchEdgeProjections.isEmpty() )
		{
			items.put( VertexColorMode.BRANCH_EDGE, branchEdgeProjections );
			categoryNames.put( VertexColorMode.BRANCH_EDGE, "Branch edge" );
		}

		final Map< FeatureKeyWrapper, String > itemNames = null;
		final CategoryJComboBox< VertexColorMode, FeatureKeyWrapper > comboBox =
				new CategoryJComboBox<>( items, itemNames, categoryNames );

		// "Find" current setting.
		FeatureKeyWrapper fkw = fixedColor;
		for ( final FeatureKeyWrapper featureKeyWrapper : items.get( currentMode ) )
		{
			if ( featureKeyWrapper.featureKey.equals( currentFeatureKey ) )
			{
				fkw = featureKeyWrapper;
				break;
			}
		}
		comboBox.setSelectedItem( fkw );

		return comboBox;
	}

	private static CategoryJComboBox< EdgeColorMode, FeatureKeyWrapper > edgeColorBy(
			final FeatureKeys featureKeys,
			final FeatureKeys branchGraphFeatureKeys,
			final EdgeColorMode currentMode,
			final String currentFeatureKey )
	{
		/*
		 * Harvest possible choices.
		 */
		final Map< EdgeColorMode, Collection< FeatureKeyWrapper > > items = new LinkedHashMap<>();
		final Map< EdgeColorMode, String > categoryNames = new HashMap<>();

		// Fixed color.
		final FeatureKeyWrapper fixedColor = new FeatureKeyWrapper( "Fixed color" );
		items.put( EdgeColorMode.FIXED, Collections.singleton( fixedColor ) );
		categoryNames.put( EdgeColorMode.FIXED, "Fixed" );

		// This edge.
		final Collection< FeatureKeyWrapper > edgeProjections = new ArrayList<>();
		for ( final String projectionKey : featureKeys.getProjectionKeys( FeatureTarget.EDGE ) )
			edgeProjections.add( new FeatureKeyWrapper( projectionKey ) );
		if ( !edgeProjections.isEmpty() )
		{
			items.put( EdgeColorMode.EDGE, edgeProjections );
			categoryNames.put( EdgeColorMode.EDGE, "Edge feature" );
		}

		// Source and target vertex.
		final Collection< FeatureKeyWrapper > sourceVertexProjections = new ArrayList<>();
		final Collection< FeatureKeyWrapper > targetVertexProjections = new ArrayList<>();
		for ( final String projectionKey : featureKeys.getProjectionKeys( FeatureTarget.VERTEX ) )
		{
			sourceVertexProjections.add( new FeatureKeyWrapper( projectionKey ) );
			targetVertexProjections.add( new FeatureKeyWrapper( projectionKey ) );
		}
		if ( !sourceVertexProjections.isEmpty() )
		{
			items.put( EdgeColorMode.SOURCE_VERTEX, sourceVertexProjections );
			items.put( EdgeColorMode.TARGET_VERTEX, targetVertexProjections );
			categoryNames.put( EdgeColorMode.SOURCE_VERTEX, "Source vertex feature" );
			categoryNames.put( EdgeColorMode.TARGET_VERTEX, "Target vertex feature" );
		}

		// Branch edge.
		final Collection< FeatureKeyWrapper > branchEdgeProjections = new ArrayList<>();
		for ( final String projectionKey : branchGraphFeatureKeys.getProjectionKeys( FeatureTarget.EDGE ) )
			branchEdgeProjections.add( new FeatureKeyWrapper( projectionKey ) );
		if ( !branchEdgeProjections.isEmpty() )
		{
			items.put( EdgeColorMode.BRANCH_EDGE, branchEdgeProjections );
			categoryNames.put( EdgeColorMode.BRANCH_EDGE, "Branch edge" );
		}

		// Branch vertex.
		final Collection< FeatureKeyWrapper > branchVertexProjections = new ArrayList<>();
		for ( final String projectionKey : featureKeys.getProjectionKeys( FeatureTarget.VERTEX ) )
			branchVertexProjections.add( new FeatureKeyWrapper( projectionKey ) );
		if ( !branchVertexProjections.isEmpty() )
		{
			items.put( EdgeColorMode.BRANCH_VERTEX, branchVertexProjections );
			categoryNames.put( EdgeColorMode.BRANCH_VERTEX, "Branch vertex" );
		}

		final Map< FeatureKeyWrapper, String > itemNames = null;
		final CategoryJComboBox< EdgeColorMode, FeatureKeyWrapper > comboBox =
				new CategoryJComboBox<>( items, itemNames, categoryNames );

		// "Find" current setting.
		FeatureKeyWrapper fkw = fixedColor;
		for ( final FeatureKeyWrapper featureKeyWrapper : items.get( currentMode ) )
		{
			if ( featureKeyWrapper.featureKey.equals( currentFeatureKey ) )
			{
				fkw = featureKeyWrapper;
				break;
			}
		}
		comboBox.setSelectedItem( fkw );

		return comboBox;
	}

	private static final class FeatureKeyWrapper
	{
		private final String featureKey;

		public FeatureKeyWrapper( final String featureKey )
		{
			this.featureKey = featureKey;
		}

		@Override
		public String toString()
		{
			return featureKey;
		}
	}

	private static final class ColorMapPainter extends JComponent
	{

		private static final long serialVersionUID = 1L;

		private final JComboBox< String > choices;

		public ColorMapPainter( final JComboBox< String > choices )
		{
			this.choices = choices;
		}

		@Override
		protected void paintComponent( final Graphics g )
		{
			super.paintComponent( g );
			if ( !isEnabled() )
				return;

			final String cname = ( String ) choices.getSelectedItem();
			final ColorMap cmap = ColorMap.getColorMap( cname );
			final int w = getWidth();
			final int h = getHeight();
			final int lw = ( int ) ( 0.8 * w );
			for ( int i = 0; i < lw; i++ )
			{
				g.setColor( cmap.get( ( double ) i / lw ) );
				g.drawLine( i, 0, i, h );
			}

			// NaN.
			g.setColor( cmap.get( Double.NaN ) );
			g.fillRect( ( int ) ( 0.83 * w ), 0, ( int ) ( 0.07 * w ), h );

			// Missing color.
			g.setColor( cmap.getMissingColor() );
			g.fillRect( ( int ) ( 0.93 * w ), 0, ( int ) ( 0.07 * w ), h );
		}

		@Override
		public Dimension getPreferredSize()
		{
			final Dimension dimension = super.getPreferredSize();
			dimension.height = 20;
			return dimension;
		}

		@Override
		public Dimension getMinimumSize()
		{
			return getPreferredSize();
		}
	}

	@Override
	public Dimension getPreferredSize()
	{
		return PREFERRED_SIZE;
	}

	private static final class ComponentMuter
	{
		private final Collection< JComponent > toMute;

		public ComponentMuter( final Collection< JComponent > toMute )
		{
			this.toMute = toMute;
		}

		public void enable( final boolean enable )
		{
			for ( final JComponent c : toMute )
				c.setEnabled( enable );
		}

	}
}
