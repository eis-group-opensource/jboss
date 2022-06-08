/*
* JBoss, Home of Professional Open Source
* Copyright 2005, JBoss Inc., and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/

package org.jboss.serial.persister;

import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;

import org.jboss.serial.Compatability;
import org.jboss.serial.classmetamodel.ClassMetaDataSlot;
import org.jboss.serial.objectmetamodel.ObjectSubstitutionInterface;

/**
 * $Id: ObjectOutputStreamProxy.java 442 2013-05-14 14:26:08Z bmaxwell $
 * @author Clebert Suconic
 * @author bmaxwell
 */
public class ObjectOutputStreamProxy extends ObjectOutputStream
{

    private ObjectOutputStreamProxy delegate;

    protected ObjectOutputStreamProxy() throws IOException {
        // only called by ClassMetadataField1 / ClassMetadataField2
        // cannot create delegates here else it will cause a loop	
    }
	
    public ObjectOutputStreamProxy(ObjectOutput output, Object currentObj, ClassMetaDataSlot currentMetaClass, ObjectSubstitutionInterface currentSubstitution) throws IOException
    {
        super();
        
        if(Compatability.SYNC_BINARY_FORMATS2) {
        	delegate = new ObjectOutputStreamProxy2(output, currentObj, currentMetaClass, currentSubstitution);
        } else {
        	delegate = new ObjectOutputStreamProxy1(output, currentObj, currentMetaClass, currentSubstitution);
        }
        
    }

    protected void writeObjectOverride(Object obj) throws IOException {
    	delegate.writeObjectOverride(obj);
    }

    public void writeUnshared(Object obj) throws IOException {
        delegate.writeUnshared(obj);
    }

    public void defaultWriteObject() throws IOException {
        delegate.defaultWriteObject();
    }

    public void writeFields() throws IOException {
        delegate.writeFields();
    }

    public void reset() throws IOException {
    	delegate.reset();
    }

    protected void writeStreamHeader() throws IOException {
    	delegate.writeStreamHeader();
    }

    protected void writeClassDescriptor(ObjectStreamClass desc)
    throws IOException
    {
    	delegate.writeClassDescriptor(desc);
    }

    /**
     * Writes a byte. This method will block until the byte is actually
     * written.
     *
     * @param   val the byte to be written to the stream
     * @throws  IOException If an I/O error has occurred.
     */
    public void write(int val) throws IOException {
           delegate.write(val);
    }

    /**
     * Writes an array of bytes. This method will block until the bytes are
     * actually written.
     *
     * @param   buf the data to be written
     * @throws  IOException If an I/O error has occurred.
     */
    public void write(byte[] buf) throws IOException {
        delegate.write(buf);
    }

    public void write(byte[] buf, int off, int len) throws IOException {
        delegate.write(buf, off, len);
    }

    /**
     * Flushes the stream. This will write any buffered output bytes and flush
     * through to the underlying stream.
     *
     * @throws  IOException If an I/O error has occurred.
     */
    public void flush() throws IOException {
        delegate.flush();
    }

    protected void drain() throws IOException {
    	delegate.drain();
    }

    public void close() throws IOException {
        delegate.close();
    }

    public void writeBoolean(boolean val) throws IOException {
        delegate.writeBoolean(val);
    }

    public void writeByte(int val) throws IOException  {
        delegate.writeByte(val);
    }

    public void writeShort(int val)  throws IOException {
        delegate.writeShort(val);
    }

    public void writeChar(int val)  throws IOException {
        delegate.writeChar(val);
    }

    public void writeInt(int val)  throws IOException {
        delegate.writeInt(val);
    }

    public void writeLong(long val)  throws IOException {
        delegate.writeLong(val);
    }

    public void writeFloat(float val) throws IOException {
        delegate.writeFloat(val);
    }

    public void writeDouble(double val) throws IOException {
        delegate.writeDouble(val);
    }

    public void writeBytes(String str) throws IOException {
        delegate.writeBytes(str);
    }

    public void writeChars(String str) throws IOException {
        delegate.writeChars(str);
    }

    public void writeUTF(String str) throws IOException {
        delegate.writeUTF(str);
    }

    public ObjectOutputStream.PutField putFields() throws IOException {
        return delegate.putFields();
    }

}
