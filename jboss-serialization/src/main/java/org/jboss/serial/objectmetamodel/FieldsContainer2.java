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

package org.jboss.serial.objectmetamodel;

import org.jboss.serial.finalcontainers.*;
import org.jboss.serial.persister.RegularObjectPersister;

import org.jboss.serial.classmetamodel.ClassMetaData;
import org.jboss.serial.classmetamodel.ClassMetaDataSlot;
import org.jboss.serial.classmetamodel.ClassMetadataField;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.io.*;

/**
 * $Id: FieldsContainer.java 431 2013-05-01 03:02:57Z clebert.suconic@jboss.com $
 *
 * @author <a href="mailto:clebert.suconic@jboss.com">Clebert Suconic</a>
 */
public class FieldsContainer2 extends FieldsContainer implements FieldsContainerStatic {

    ClassMetaDataSlot metaData;

    protected HashMap fields = new HashMap();
    
    protected FieldsContainer2() {
    	super((FieldsContainerStatic)null);
    }
    
    public Map getMap()
    {
    	return fields;
    }
    
    
    public ClassMetadataField getMetaField(String name)
    {
    	return metaData.getField(name);
    }

    /** both {@link org.jboss.serial.persister.RegularObjectPersister) and writeMyself need to produce the same binary compatible output while
     * it's not required by RegularObjectPersister to create an intermediate HashMap to read its fields. Becuase of that we
     * have opted in keep static methods on FieldsContainer that will expose low level persistent operations */
    /*public static void writeNumberOfFields(ObjectOutput out, int fields) throws IOException
    {
        out.writeInt(fields);
    } */

    /** both {@link org.jboss.serial.persister.RegularObjectPersister) and writeMyself need to produce the same binary compatible output while
     * it's not required by RegularObjectPersister to create an intermediate HashMap to read its fields. Becuase of that we
     * have opted in keep static methods on FieldsContainer that will expose low level persistent operations */
    public static void writeField(ObjectOutput out, Map.Entry entry) throws IOException
    {
        out.writeUTF((String)entry.getKey());

        final Object value = entry.getValue();
        if (value  instanceof FinalContainer)
        {
            if (value instanceof BooleanContainer)
            {
                out.writeByte(DataContainerConstants.BOOLEAN);
                out.writeBoolean(((BooleanContainer)value).getValue());
            }
            else
            if (value instanceof ByteContainer)
            {
                out.writeByte(DataContainerConstants.BYTE);
                out.writeByte(((ByteContainer)value).getValue());
            }
            else
            if (value instanceof CharacterContainer)
            {
                out.writeByte(DataContainerConstants.CHARACTER);
                out.writeChar(((CharacterContainer)value).getValue());
            }
            else
            if (value instanceof DoubleContainer)
            {
                out.writeByte(DataContainerConstants.DOUBLE);
                out.writeDouble(((DoubleContainer)value).getValue());
            }
            else
            if (value instanceof FloatContainer)
            {
                out.writeByte(DataContainerConstants.FLOAT);
                out.writeFloat(((FloatContainer)value).getValue());
            }
            else
            if (value instanceof IntegerContainer)
            {
                out.writeByte(DataContainerConstants.INTEGER);
                out.writeInt(((IntegerContainer)value).getValue());
            }
            else
            if (value instanceof LongContainer)
            {
                out.writeByte(DataContainerConstants.LONG);
                out.writeLong(((LongContainer)value).getValue());
            }
            else
            if (value instanceof ShortContainer)
            {
                out.writeByte(DataContainerConstants.SHORT);
                out.writeShort(((ShortContainer)value).getValue());
            }
            else
            {
                throw new RuntimeException ("Unexpected datatype " + value.getClass().getName());
            }

        } else
        {
            out.writeByte(DataContainerConstants.OBJECTREF);
            out.writeObject(value);
        }
    }

    /** both {@link org.jboss.serial.persister.RegularObjectPersister) and readMyself need to produce the same binary compatible output while
     * it's not required by RegularObjectPersister to create an intermediate HashMap to read its fields. Becuase of that we
     * have opted in keep static methods on FieldsContainer that will expose low level persistent operations */
    /*public static int readNumberOfFields(ObjectInput input) throws IOException
    {
        return input.readInt();
    }*/


    public static Map.Entry readField(ObjectInput input) throws IOException,ClassNotFoundException
    {
        String name = input.readUTF();
        byte datatype = (byte)input.readByte();

        Object value = null;
        switch (datatype)
        {
            case DataContainerConstants.BOOLEAN:
                value = BooleanContainer.valueOf(input.readBoolean()); break;

            case DataContainerConstants.BYTE:
                value = new ByteContainer(input.readByte()); break;

            case DataContainerConstants.CHARACTER:
                value = new CharacterContainer(input.readChar()); break;

            case DataContainerConstants.DOUBLE:
                value = new DoubleContainer(input.readDouble()); break;

            case DataContainerConstants.FLOAT:
                value = new FloatContainer(input.readFloat()); break;

            case DataContainerConstants.INTEGER:
                value = new IntegerContainer(input.readInt()); break;

            case DataContainerConstants.LONG:
                value = new LongContainer(input.readLong()); break;

            case DataContainerConstants.SHORT:
                value = new ShortContainer(input.readShort()); break;

            case DataContainerConstants.OBJECTREF:
                value = input.readObject(); break;

            default:
                throw new RuntimeException("Unexpected datatype " + datatype);

        }

        return new EntryImpl(name,value);
    }

    public static class EntryImpl implements Map.Entry
    {
        private Object key;
        private Object value;

        public EntryImpl(Object key, Object value)
        {
            this.key=key;
            this.value=value;
        }
        public Object getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }

        public Object setValue(Object value) {
            throw new RuntimeException("method not supported");
        }
    }

    public void setField(String name, Object value)
    {
		if (metaData.getField(name) == null)
		{
			throw new IllegalArgumentException("can't find field " + name + " at the current class slot " + metaData.getSlotClass().getName());
		}
        fields.put(name, value);
    }
    
    public FieldsContainer2(ClassMetaDataSlot metaData)
    {
        this.metaData=metaData;
    }

    public ObjectInputStream.GetField createGet()
    {
        return new GetFieldImpl();
    }

    public ObjectOutputStream.PutField createPut()
    {
        return new PutFieldImpl();
    }

    class GetFieldImpl extends ObjectInputStream.GetField
    {
        public GetFieldImpl() {
            super();
        }
        public ObjectStreamClass getObjectStreamClass()
        {
            return ObjectStreamClass.lookup(metaData.getSlotClass());
        }

        public boolean defaulted(String name) throws IOException
        {
            return fields.get(name)==null;
        }

       public boolean get(String name, boolean val)
           throws IOException
       {
           BooleanContainer ret = (BooleanContainer)fields.get(name);

           if (ret==null)
           {
               return val;
           } else
           {
               return ret.getValue();
           }
       }

       public byte get(String name, byte val) throws IOException
       {
           ByteContainer ret = (ByteContainer)fields.get(name);

           if (ret==null)
           {
               return val;
           }
           else
           {
               return ret.getValue();
           }

       }

       public char get(String name, char val) throws IOException
       {
           CharacterContainer ret = (CharacterContainer)fields.get(name);

           if (ret==null)
           {
               return val;
           }
           else
           {
               return ret.getValue();
           }
       }

       public short get(String name, short val) throws IOException
       {
           ShortContainer ret = (ShortContainer)fields.get(name);

           if (ret==null)
           {
               return val;
           }
           else
           {
               return ret.getValue();
           }
       }

       public int get(String name, int val) throws IOException
       {
           IntegerContainer ret = (IntegerContainer)fields.get(name);

           if (ret==null)
           {
               return val;
           }
           else
           {
               return ret.getValue();
           }
       }

       public long get(String name, long val) throws IOException
       {
    	   LongContainer ret = (LongContainer)fields.get(name);

           if (ret==null)
           {
               return val;
           }
           else
           {
               return ret.getValue();
           }
       }

       public float get(String name, float val) throws IOException
       {
           FloatContainer ret = (FloatContainer)fields.get(name);

           if (ret==null)
           {
               return val;
           }
           else
           {
               return ret.getValue();
           }
       }

       public double get(String name, double val) throws IOException
       {
           DoubleContainer ret = (DoubleContainer) fields.get(name);

           if (ret==null)
           {
               return val;
           }
           else
           {
               return ret.getValue();
           }
       }

       public Object get(String name, Object val) throws IOException
       {
           Object ret = fields.get(name);

           if (ret==null)
           {
               return val;
           }
           else
           {
               return ret;
           }
       }
    }

    class PutFieldImpl extends ObjectOutputStream.PutField
    {
        public void put(String name, boolean val)
        {
            setField(name, BooleanContainer.valueOf(val));
        }

        public void put(String name, byte val)
        {
            setField(name,new ByteContainer(val));
        }

        public void put(String name, char val)
        {
            setField(name,new CharacterContainer(val));
        }

        public void put(String name, short val)
        {
            setField(name,new ShortContainer(val));
        }

        public void put(String name, int val)
        {
            setField(name,new IntegerContainer(val));
        }

        public void put(String name, long val)
        {
            setField(name,new LongContainer(val));
        }

        public void put(String name, float val)
        {
            setField(name, new FloatContainer(val));
        }

        public void put(String name, double val)
        {
            setField(name,new DoubleContainer(val));
        }

        public void put(String name, Object val)
        {
            setField(name, val);
        }

        public void write(ObjectOutput out) throws IOException
        {        	
        	RegularObjectPersister.writeSlotWithFieldsContainer(metaData, out, FieldsContainer2.this, null);
        }
    }
    
    // Static methods for FieldsContainerStatic
	public void staticWriteField(ObjectOutput out, Entry entry)
			throws IOException {
		
		writeField(out, entry);		
	}

	public Entry staticReadField(ObjectInput input) throws IOException,
			ClassNotFoundException {

		return readField(input);
	}

}
