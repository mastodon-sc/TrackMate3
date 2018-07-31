package org.mastodon.revised.bvv.pool.attributes;

import org.joml.Matrix3fc;

public interface Matrix3fAttributeValue extends Matrix3fAttributeReadOnlyValue
{
	void set( final Matrix3fc value );

	void set( final float[] value );

	void identity();
}