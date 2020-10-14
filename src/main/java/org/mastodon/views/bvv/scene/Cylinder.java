package org.mastodon.views.bvv.scene;

import org.mastodon.views.bvv.pool.attributes.Matrix3fAttributeValue;
import org.mastodon.views.bvv.pool.attributes.Vector3fAttributeValue;

// bundle Shape and Color
public class Cylinder implements ModifiableRef< Cylinder >
{
	private final CylinderPool pool;

	final EllipsoidShape shape;
	final Color color;

	public final Matrix3fAttributeValue e;
	public final Matrix3fAttributeValue inve;
	public final Vector3fAttributeValue t;
	public final Vector3fAttributeValue rgb;

	Cylinder( CylinderPool pool )
	{
		this.pool = pool;
		shape = pool.shapes.createRef();
		color = pool.colors.createRef();

		e = shape.e;
		inve = shape.inve;
		t = shape.t;
		rgb = color.color;
	}

	@Override
	public int getInternalPoolIndex()
	{
		return shape.getInternalPoolIndex();
	}

	@Override
	public Cylinder refTo( final Cylinder obj )
	{
		return pool.getObject( obj.getInternalPoolIndex(), this );
	}

	@Override
	public void set( final Cylinder obj )
	{
		shape.set( obj.shape );
		color.set( obj.color );
	}
}