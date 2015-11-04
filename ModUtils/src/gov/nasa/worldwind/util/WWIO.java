/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;


/**
 * @author Tom Gaskins
 * @version $Id: WWIO.java 1171 2013-02-11 21:45:02Z dcollins $
 */
public class WWIO
{
    /**
     * Open an {@link java.io.InputStream} from a general source. The source type may be one of the following: <ul>
     * <li>{@link java.io.InputStream}</li> <li>{@link java.net.URL}</li> <li>absolute {@link java.net.URI}</li>
     * <li>{@link java.io.File}</li> <li>{@link String} containing a valid URL description or a file or resource name
     * available on the classpath.</li> </ul>
     *
     * @param src the input source of one of the above types.
     *
     * @return an InputStream for the input source.
     *
     * @throws IllegalArgumentException if the source is null, an empty string, or is not one of the above types.
     * @throws Exception                if the source cannot be opened for any reason.
     */
    public static InputStream openStream(Object src) throws Exception
    {
        if (src == null || WWUtil.isEmpty(src))
        {
            String message = /* Logging.getMessage */("nullValue.SourceIsNull");
            // Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (src instanceof InputStream)
        {
            return (InputStream) src;
        }
        else if (src instanceof URL)
        {
            return ((URL) src).openStream();
        }
        else if (src instanceof URI)
        {
            return ((URI) src).toURL().openStream();
        }
        else if (src instanceof File)
        {
            Object streamOrException = getFileOrResourceAsStream(((File) src).getPath(), null);
            if (streamOrException instanceof Exception)
            {
                throw (Exception) streamOrException;
            }

            return (InputStream) streamOrException;
        }
        else if (!(src instanceof String))
        {
            String message = /* Logging.getMessage */("generic.UnrecognizedSourceType"/*, src.toString()*/);
            // Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        String sourceName = (String) src;

        URL url = WWIO.makeURL(sourceName);
        if (url != null)
            return url.openStream();

        Object streamOrException = getFileOrResourceAsStream(sourceName, null);
        if (streamOrException instanceof Exception)
        {
            throw (Exception) streamOrException;
        }

        return (InputStream) streamOrException;
    }

    public static Object getFileOrResourceAsStream(String path, Class<?> c)
    {
        if (path == null)
        {
            String message = /* Logging.getMessage */("nullValue.FilePathIsNull");
            throw new IllegalStateException(message);
        }

        File file = new File(path);
        if (file.exists())
        {
            try
            {
                return new FileInputStream(file);
            }
            catch (Exception e)
            {
                return e;
            }
        }

        if (c == null)
            c = WWIO.class;

        try
        {
            return c.getResourceAsStream("/" + path);
        }
        catch (Exception e)
        {
            return e;
        }
    }

    /**
     * Creates an {@link InputStream} for the contents of a {@link ByteBuffer}. The method creates a copy of the
     * buffer's contents and passes a steam reference to that copy.
     *
     * @param buffer the buffer to create a stream for.
     *
     * @return an {@link InputStream} for the buffer's contents.
     *
     * @throws IllegalArgumentException if <code>buffer</code> is null.
     */
    public static InputStream getInputStreamFromByteBuffer(ByteBuffer buffer)
    {
        if (buffer == null)
        {
            String message = /* Logging.getMessage */("nullValue.ByteBufferIsNull");
            // Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (buffer.hasArray() && buffer.limit() == buffer.capacity()) // otherwise bytes beyond the limit are included
            return new ByteArrayInputStream(buffer.array());

        byte[] byteArray = new byte[buffer.limit()];
        buffer.get(byteArray);
        return new ByteArrayInputStream(byteArray);
    }

    /**
     * Returns a new {@link java.io.BufferedInputStream} which wraps the specified InputStream. If the specified
     * InputStream is already a BufferedInputStream, this returns the original InputStream cast to a
     * BufferedInputStream.
     *
     * @param is the InputStream to wrap with a new BufferedInputStream.
     *
     * @return a new BufferedInputStream which wraps the specified InputStream.
     */
    public static BufferedInputStream getBufferedInputStream(InputStream is)
    {
        if (is == null)
        {
            String message = /* Logging.getMessage */("nullValue.InputStreamIsNull");
            // Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        return (is instanceof BufferedInputStream && BufferedInputStream.class.equals(is.getClass()))
            ? (BufferedInputStream) is : new BufferedInputStream(is);
    }

    /**
     * Reads all the available bytes from the specified {@link java.nio.channels.ReadableByteChannel}, returning the
     * bytes as a {@link ByteBuffer} with the current JVM byte order. This returns a direct ByteBuffer if allocateDirect
     * is true, and returns a non-direct ByteBuffer otherwise. Direct buffers are backed by native memory, and may
     * reside outside of the normal garbage-collected heap. Non-direct buffers are backed by JVM heap memory.
     *
     * @param channel        the channel to read.
     * @param allocateDirect true to allocate and return a direct buffer, false to allocate and return a non-direct
     *                       buffer.
     *
     * @return the bytes from the specified channel, with the current JVM byte order.
     *
     * @throws IllegalArgumentException if the channel is null.
     * @throws IOException              if an I/O error occurs.
     */
    public static ByteBuffer readChannelToBuffer(ReadableByteChannel channel, boolean allocateDirect) throws IOException
    {
        if (channel == null)
        {
            String message = /* Logging.getMessage */("nullValue.ChannelIsNull");
            // Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        final int PAGE_SIZE = (int) Math.round(Math.pow(2, 16));
        ByteBuffer buffer = WWBufferUtil.newByteBuffer(PAGE_SIZE, allocateDirect);

        int count = 0;
        while (count >= 0)
        {
            count = channel.read(buffer);
            if (count > 0 && !buffer.hasRemaining())
            {
                ByteBuffer biggerBuffer = allocateDirect ? ByteBuffer.allocateDirect(buffer.limit() + PAGE_SIZE)
                    : ByteBuffer.allocate(buffer.limit() + PAGE_SIZE);
                biggerBuffer.put((ByteBuffer) buffer.rewind());
                buffer = biggerBuffer;
            }
        }

        if (buffer != null)
            buffer.flip();

        return buffer;
    }

    /**
     * Reads the available bytes from the specified {@link java.nio.channels.ReadableByteChannel} up to the number of
     * bytes remaining in the buffer. Bytes read from the specified channel are copied to the specified {@link
     * ByteBuffer}. Upon returning the specified buffer's limit is set to the number of bytes read, and its position is
     * set to zero.
     *
     * @param channel the channel to read bytes from.
     * @param buffer  the buffer to receive the bytes.
     *
     * @return the specified buffer.
     *
     * @throws IllegalArgumentException if the channel or the buffer is null.
     * @throws IOException              if an I/O error occurs.
     */
    public static ByteBuffer readChannelToBuffer(ReadableByteChannel channel, ByteBuffer buffer) throws IOException
    {
        if (channel == null)
        {
            String message = /* Logging.getMessage */("nullValue.ChannelIsNull");
            // Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (buffer == null)
        {
            String message = /* Logging.getMessage */("nullValue.BufferIsNull");
            // Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        int count = 0;
        while (count >= 0 && buffer.hasRemaining())
        {
            count = channel.read(buffer);
        }

        buffer.flip();

        return buffer;
    }

    /**
     * Close a stream and catch any {@link IOException} generated in the process. This supports any object that
     * implements the {@link java.io.Closeable} interface.
     *
     * @param stream the stream to close. If null, this method does nothing.
     * @param name   the name of the stream to place in the log message if an exception is encountered.
     */
    public static void closeStream(Object stream, String name)
    {
        if (stream == null)
            return;

        try
        {
            if (stream instanceof Closeable)
            {
                ((Closeable) stream).close();
            }
            else
            {
                // String message = /* Logging.getMessage */("WWIO.StreamTypeNotSupported"/*, name != null ? name : "Unknown"*/);
                // Logging.logger().warning(message);
            }
        }
        catch (IOException e)
        {
            // String message = /* Logging.getMessage */("generic.ExceptionClosingStream"/*, e, name != null ? name : "Unknown"*/);
            // Logging.logger().severe(message);
        }
    }

    /**
     * Converts a string to a URL.
     *
     * @param path the string to convert to a URL.
     *
     * @return a URL for the specified object, or null if a URL could not be created.
     *
     * @see #makeURL(Object)
     * @see #makeURL(Object, String)
     */
    public static URL makeURL(String path)
    {
        try
        {
            return new URL(path);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * Creates a URL from an object.
     *
     * @param path the object from which to create a URL, typically a string.
     *
     * @return a URL for the specified object, or null if a URL could not be created.
     *
     * @see #makeURL(String)
     * @see #makeURL(Object, String)
     */
    public static URL makeURL(Object path)
    {
        try
        {
            URI uri = makeURI(path);

            return uri != null ? uri.toURL() : null;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * Creates a URL from an object. If the object does not already convert directly to a URL, a URL with a specified
     * protocol is created.
     *
     * @param path            the object from which to create a URL, typically a string.
     * @param defaultProtocol if non-null, a protocol to use if the specified path does not yet include a protocol.
     *
     * @return a URL for the specified object, or null if a URL could not be created.
     *
     * @see #makeURL(String)
     * @see #makeURL(Object)
     */
    public static URL makeURL(Object path, String defaultProtocol)
    {
        try
        {
            URL url = makeURL(path);

            if (url == null && !WWUtil.isEmpty(path.toString()) && !WWUtil.isEmpty(defaultProtocol))
                url = new URL(defaultProtocol, null, path.toString());

            return url;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * Creates a URI from an object.
     *
     * @param path the object from which to create a URI, typically a string.
     *
     * @return a URI for the specified object, or null if a URI could not be created.
     *
     * @see #makeURL(String)
     * @see #makeURL(Object)
     * @see #makeURL(Object, String)
     */
    public static URI makeURI(Object path)
    {
        try
        {
            if (path instanceof String)
                return new URI((String) path);
            else if (path instanceof File)
                return ((File) path).toURI();
            else if (path instanceof URL)
                return ((URL) path).toURI();
            else
                return null;
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
