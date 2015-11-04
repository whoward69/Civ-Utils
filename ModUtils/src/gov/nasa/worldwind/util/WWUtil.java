/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.util;

import java.awt.List;

/**
 * @author tag
 * @version $Id: WWUtil.java 1171 2013-02-11 21:45:02Z dcollins $
 */
public class WWUtil
{
    /**
     * Determine whether an object reference is null or a reference to an empty string.
     *
     * @param s the reference to examine.
     *
     * @return true if the reference is null or is a zero-length {@link String}.
     */
    public static boolean isEmpty(Object s)
    {
        return s == null || (s instanceof String && ((String) s).length() == 0);
    }

    /**
     * Determine whether an {@link List} is null or empty.
     *
     * @param list the list to examine.
     *
     * @return true if the list is null or zero-length.
     */
    public static boolean isEmpty(java.util.List<?> list)
    {
        return list == null || list.size() == 0;
    }
}
