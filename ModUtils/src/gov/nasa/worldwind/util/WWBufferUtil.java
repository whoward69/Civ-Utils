/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.util;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * A collection of useful {@link Buffer} methods, all static.
 *
 * @author dcollins
 * @version $Id: WWBufferUtil.java 1171 2013-02-11 21:45:02Z dcollins $
 */
public class WWBufferUtil
{
    /** The size of a short primitive type, in bytes. */
    public static final int SIZEOF_SHORT = 2;
    /** The size of a int primitive type, in bytes. */
    public static final int SIZEOF_INT = 4;
    /** The size of a float primitive type, in bytes. */
    public static final int SIZEOF_FLOAT = 4;
    /** The size of a double primitive type, in bytes. */
    public static final int SIZEOF_DOUBLE = 8;
    /** The size of a char primitive type, in bytes. */
    public static final int SIZEOF_CHAR = 2;

    /**
     * Allocates a new direct {@link java.nio.ByteBuffer} of the specified size, in chars.
     *
     * @param size           the new ByteBuffer's size.
     * @param allocateDirect true to allocate and return a direct buffer, false to allocate and return a non-direct
     *                       buffer.
     *
     * @return the new buffer.
     *
     * @throws IllegalArgumentException if size is negative.
     */
    public static ByteBuffer newByteBuffer(int size, boolean allocateDirect)
    {
        if (size < 0)
        {
            String message = /* Logging.getMessage */("generic.SizeOutOfRange"/*, size*/);
            // Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        return allocateDirect ? newDirectByteBuffer(size) : ByteBuffer.allocate(size);
    }

    protected static ByteBuffer newDirectByteBuffer(int size)
    {
        return ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
    }
}
