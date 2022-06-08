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

import org.apache.log4j.Logger;
import org.jboss.serial.classmetamodel.*;
import org.jboss.serial.exception.SerializationException;
import org.jboss.serial.finalcontainers.*;
import org.jboss.serial.objectmetamodel.DataContainerConstants;
import org.jboss.serial.objectmetamodel.FieldsContainer;
import org.jboss.serial.objectmetamodel.ObjectSubstitutionInterface;
import org.jboss.serial.objectmetamodel.ObjectsCache;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * This is the persister of a regular object.
 * @author clebert suconic
 */
public class RegularObjectPersister2 extends RegularObjectPersister implements Persister, RegularObjectPersisterStatic
{
	private static final Logger log = Logger.getLogger(RegularObjectPersister.class);
   	private static final boolean isDebug = log.isDebugEnabled();
   	
    byte id;
    
    public RegularObjectPersister2() {
    	super((RegularObjectPersisterStatic)null);
    }
    
    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public void writeData(ClassMetaData metaData, ObjectOutput out, Object obj, ObjectSubstitutionInterface substitution) throws IOException
    {
        defaultWrite(out, obj, metaData, substitution);
    }

    public static void defaultWrite(ObjectOutput output, Object obj, ClassMetaData metaClass, ObjectSubstitutionInterface substitution) throws IOException
    {
        //data.getCache().putClassMetaData(metaClass);

    	ClassMetaDataSlot slots[] = metaClass.getSlots();
    	
    	if (isDebug)
    	{
    		log.debug("defaultWrite::" + metaClass.getClassName() + " contains " + slots.length + " slots");
    	}
    	
		//System.out.println("defaultWrite::" + metaClass.getClassName() + " contains " + slots.length + " slots");
    	//output.writeInt(slots.length);
    	
    	for (int slotNr=0;slotNr<slots.length;slotNr++)
    	{
    		//System.out.println("defaultWrite:: slot " + slotNr + " NR=" + slots[slotNr].getSlotClass().getName() + " from parentClass=" + metaClass.getClassName());
        	if (isDebug)
        	{
        		log.debug("defaultWrite:: slot " + slotNr + " NR=" + slots[slotNr].getSlotClass().getName() + " from parentClass=" + metaClass.getClassName());
        	}
    		if (slots[slotNr].getPrivateMethodWrite()!=null)
    		{
    			writeSlotWithMethod(slots[slotNr], output, obj, substitution);
    		}
    		else
    		{
    			writeSlotWithFields(slots[slotNr],output, obj, substitution);
    		}
    	}
    	
    }

    private static void readSlotWithMethod(ClassMetaDataSlot slot, short[] fieldsKey, ObjectInput input, Object obj, ObjectSubstitutionInterface substitution) throws IOException
    {
    	if (isDebug)
    	{
    		log.debug("readSlotWithMethod slot=" + slot.getSlotClass().getName());
    	}
    	try {
			slot.getPrivateMethodRead().invoke(obj,new Object[]{new ObjectInputStreamProxy(input,fieldsKey,obj,slot,substitution)});
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			IOException io = new IOException (e.getMessage());
			io.initCause(e);
			throw io;
		}
    }
    
    private static void writeSlotWithMethod(ClassMetaDataSlot slot, ObjectOutput out, Object obj, ObjectSubstitutionInterface substitution) throws IOException
    {
    	if (isDebug)
    	{
    		log.debug("writeSlotWithMethod slot=" + slot.getSlotClass().getName());
    	}
    	try {
			slot.getPrivateMethodWrite().invoke(obj,new Object[]{new ObjectOutputStreamProxy(out,obj,slot,substitution)});
		}
    	catch (IllegalArgumentException e)
    	{
    		throw e;
    	}
    	catch (IOException e)
    	{
    		throw e;
    	}
    	catch (Exception e) {
    		log.fatal("error",e);
    		e.printStackTrace();
			IOException io = new IOException (e.getMessage());
			io.initCause(e);
			throw io;
		}
    }
    
	static void writeSlotWithFields(ClassMetaDataSlot slot,ObjectOutput output, Object obj, ObjectSubstitutionInterface substitution) throws IOException
	{
		ClassMetadataField[] fields = slot.getFields();
		
		output.writeInt(fields.length);
		
    	if (isDebug)
    	{
    		log.debug("writeSlotWithFields slot=" + slot.getSlotClass().getName() + " and " + fields.length + " fields");
    	}

    	for (int fieldNR=0;fieldNR<fields.length;fieldNR++)
		{
			ClassMetadataField field = fields[fieldNR];
			if (isDebug)
			{
				log.debug("writeSlotWithFields FieldNr=" + fieldNR);
			}
			
		    if (field.getField().getType().isPrimitive() && !field.getField().getType().isArray())
		    {
		    	if (isDebug)
		    	{
		    		log.debug("writeSlotWithFields slot=" + slot.getSlotClass().getName() + " primitiveField " + fields[fieldNR].getFieldName() + " with object=NULL");
		    	}
		        writeOnPrimitive(output,obj,field);
		    }
		    else
		    {
		        Object value = null;
		        value = FieldsManager.getFieldsManager().getObject(obj,field);

		        if (isDebug)
		    	{
		        	if (value==null)
		        	{
			    		log.debug("writeSlotWithFields slot=" + slot.getSlotClass().getName() + " objectField " + fields[fieldNR].getFieldName() + " with object=NULL");
		        	}
		        	else
		        	{
			    		log.debug("writeSlotWithFields slot=" + slot.getSlotClass().getName() + " objectField " + fields[fieldNR].getFieldName() + " with object=" + value.getClass().getName());
		        	}
		    	}

		        output.writeUTF(field.getFieldName());
		        output.writeByte(DataContainerConstants.OBJECTREF);
		        
		        output.writeObject(value);
		    }
		}
	}
		
	public static void writeSlotWithFieldsContainer(ClassMetaDataSlot slot,ObjectOutput output, FieldsContainer fields , ObjectSubstitutionInterface substitution) throws IOException
	{
	    output.writeInt(fields.getMap().size());
		
    	if (isDebug)
    	{
    		log.debug("writeSlotWithFields slot=" + slot.getSlotClass().getName() + " and " + fields.getMap().size() + " fields");
    	}

    	Set setFields = fields.getMap().entrySet();
    	
    	Iterator iter = setFields.iterator();
    	while (iter.hasNext())
    	{
    		Map.Entry entry = (Map.Entry)iter.next();
    		Object value = entry.getValue();
    		ClassMetadataField field = fields.getMetaField((String)entry.getKey());
    		
		    if (!field.isObject())
		    {
		    	if (isDebug)
		    	{
		    		log.debug("writeSlotWithFields slot=" + slot.getSlotClass().getName() + " primitiveField " + field.getFieldName() + " with object=NULL");
		    	}
		    	writeOnPrimitiveWithValue(output,entry.getValue(),field);
		    }
		    else
		    {
		        if (isDebug)
		    	{
		        	if (value==null)
		        	{
			    		log.debug("writeSlotWithFields slot=" + slot.getSlotClass().getName() + " objectField " + field.getFieldName() + " with object=NULL");
		        	}
		        	else
		        	{
			    		log.debug("writeSlotWithFields slot=" + slot.getSlotClass().getName() + " objectField " + field.getFieldName() + " with object=" + value.getClass().getName());
		        	}
		    	}

		        output.writeUTF(field.getFieldName());
		        output.writeByte(DataContainerConstants.OBJECTREF);
		        
		        output.writeObject(value);
		    }
		}
	}
		
	private static void writeDatatype ( final ObjectOutput out, byte datatype ) throws IOException
	{
		out.writeByte(datatype);
	}

    private static void writeOnPrimitive(final ObjectOutput out, final Object obj, final ClassMetadataField metaField) throws IOException
    {

    	try
    	{
			out.writeUTF(metaField.getFieldName());
    		
	        final Field field = metaField.getField();
	        final Class clazz = field.getType();
	        if (clazz == Integer.TYPE) {
	            writeDatatype(out, DataContainerConstants.INTEGER);
	            out.writeInt(FieldsManager.getFieldsManager().getInt(obj,metaField));
	        } else if (clazz == Byte.TYPE) {
	        	writeDatatype(out, DataContainerConstants.BYTE);
	            out.writeByte(FieldsManager.getFieldsManager().getByte(obj,metaField));
	        } else if (clazz == Long.TYPE) {
	        	writeDatatype(out, DataContainerConstants.LONG);
	            out.writeLong(FieldsManager.getFieldsManager().getLong(obj,metaField));
	        } else if (clazz == Float.TYPE) {
	        	writeDatatype(out, DataContainerConstants.FLOAT);
	            out.writeFloat(FieldsManager.getFieldsManager().getFloat(obj,metaField));
	        } else if (clazz == Double.TYPE) {
	        	writeDatatype(out, DataContainerConstants.DOUBLE);
	            out.writeDouble(FieldsManager.getFieldsManager().getDouble(obj,metaField));
	        } else if (clazz == Short.TYPE) {
	        	writeDatatype(out, DataContainerConstants.SHORT);
	            out.writeShort(FieldsManager.getFieldsManager().getShort(obj,metaField));
	        } else if (clazz == Character.TYPE) {
	        	writeDatatype(out, DataContainerConstants.CHARACTER);
	            out.writeChar(field.getChar(obj));
	        } else if (clazz == Boolean.TYPE) {
	        	writeDatatype(out, DataContainerConstants.BOOLEAN);
	            out.writeBoolean(field.getBoolean(obj));
	        } else {
	            throw new RuntimeException("Unexpected datatype " + clazz.getName());
	        }
    	}
    	catch (IllegalAccessException access)
    	{
    		IOException io = new IOException (access.getMessage());
    		io.initCause(access);
    		throw io;
    	}
    }

    private static void writeOnPrimitiveWithValue(final ObjectOutput out, final Object value, final ClassMetadataField metaField) throws IOException
    {

		out.writeUTF(metaField.getFieldName());
		
        final Class clazz = metaField.getType();
        if (clazz == Integer.TYPE) {
            writeDatatype(out, DataContainerConstants.INTEGER);
            out.writeInt(((IntegerContainer)value).getValue());
        } else if (clazz == Byte.TYPE) {
        	writeDatatype(out, DataContainerConstants.BYTE);
            out.writeByte(((ByteContainer)value).getValue());
        } else if (clazz == Long.TYPE) {
        	writeDatatype(out, DataContainerConstants.LONG);
            out.writeLong(((LongContainer)value).getValue());
        } else if (clazz == Float.TYPE) {
        	writeDatatype(out, DataContainerConstants.FLOAT);
            out.writeFloat(((FloatContainer)value).getValue());
        } else if (clazz == Double.TYPE) {
        	writeDatatype(out, DataContainerConstants.DOUBLE);
            out.writeDouble(((DoubleContainer)value).getValue());
        } else if (clazz == Short.TYPE) {
        	writeDatatype(out, DataContainerConstants.SHORT);
            out.writeShort(((ShortContainer)value).getValue());
        } else if (clazz == Character.TYPE) {
        	writeDatatype(out, DataContainerConstants.CHARACTER);
            out.writeChar(((CharacterContainer)value).getValue());
        } else if (clazz == Boolean.TYPE) {
        	writeDatatype(out, DataContainerConstants.BOOLEAN);
            out.writeBoolean(((BooleanContainer)value).getValue());
        } else {
            throw new RuntimeException("Unexpected datatype " + clazz.getName());
        }
    }

    public Object readData (ClassLoader loader, StreamingClass streaming, ClassMetaData metaData, int referenceId, ObjectsCache cache, ObjectInput input, ObjectSubstitutionInterface substitution) throws IOException
    {
        Object obj = metaData.newInstance();
        cache.putObjectInCacheRead(referenceId,obj);
        return defaultRead(input, obj,streaming,metaData, substitution);

    }

    public static Object defaultRead(ObjectInput input, Object obj, StreamingClass streaming, ClassMetaData metaData, ObjectSubstitutionInterface substitution) throws IOException {

        try
        {

        	//final int numberOfSlots = input.readInt();
        	
        	ClassMetaDataSlot[] slots = metaData.getSlots();
        	if (isDebug)
        	{
        		log.debug("defaultRead::class " + metaData.getClassName() + " contains " + slots.length + " slots");
        	}

        	for (int slotNR=0;slotNR<slots.length;slotNR++)
        	{
        		ClassMetaDataSlot slot = metaData.getSlots()[slotNR];

        		if (isDebug)
            	{
            		log.debug("defaultRead::slot[" + slotNR+"]=" + slot.getSlotClass().getName());
            	}
                
        		if (slot.getPrivateMethodRead()!=null)
        		{
        			readSlotWithMethod(slot, streaming.getKeyFields()[slotNR], input, obj, substitution);
        		}
        		else
        		{
        			readSlotWithFields(streaming.getKeyFields()[slotNR],slot, input, obj);
        		}
        	}
            

            return obj;
        }
        catch (ClassNotFoundException e)
        {
            throw new SerializationException("Error reading " + obj.getClass().getName(),e);
        }
        /*catch (IllegalAccessException e)
        {
            throw new SerializationException("Error reading " + field.getField().getDeclaringClass().getName() + " field=" + field.getFieldName(),e);
        } */
    }

	static void readSlotWithFields(short fieldsKey[],ClassMetaDataSlot slot, ObjectInput input, Object obj) throws IOException, ClassNotFoundException {
    	if (isDebug)
    	{
    		log.debug("readSlotWithFields slot=" + slot.getSlotClass().getName());
    	}
    	final int numberOfFields = input.readInt();

		for (int i=0;i<numberOfFields;i++)
		{
			final String fieldName;
		    final byte dataType;

			fieldName = input.readUTF();
		    dataType = input.readByte();

		    final ClassMetadataField field;
		    
		    if (fieldName != null)
		    {
		    	field = slot.getField(fieldName);
		    }
		    else
		    {
			    field = slot.getFields()[fieldsKey[i]];
			    
		    	
		    }
		    if (isDebug)
		    {
		    	log.debug("FieldName on Read=" + field.getFieldName());
		    }
	
		    if (field.getField().getType()==Integer.TYPE)
		    {
	            FieldsManager.getFieldsManager().setInt(obj,field,input.readInt());
		    }
		    else
		    if (field.getField().getType()==Byte.TYPE)
		    {
	            FieldsManager.getFieldsManager().setByte(obj,field,input.readByte());
		    }
		    else
		    if (field.getField().getType()==Long.TYPE)
		    {
	            FieldsManager.getFieldsManager().setLong(obj,field,input.readLong());
		    }
		    else
		    if (field.getField().getType()==Float.TYPE)
		    {
	            FieldsManager.getFieldsManager().setFloat(obj,field,input.readFloat());
		    }
		    else
		    if (field.getField().getType()==Double.TYPE)
		    {
	            FieldsManager.getFieldsManager().setDouble(obj,field,input.readDouble());
		    }
		    else
		    if (field.getField().getType()==Short.TYPE)
		    {
	            FieldsManager.getFieldsManager().setShort(obj,field,input.readShort());
		    }
		    else
		    if (field.getField().getType()==Character.TYPE)
		    {
	            FieldsManager.getFieldsManager().setCharacter(obj,field,input.readChar());
		    }
		    else
		    if (field.getField().getType()==Boolean.TYPE)
		    {
	            FieldsManager.getFieldsManager().setBoolean(obj,field,input.readBoolean());
		    }
		    else
		    {
	        	if (isDebug)
	        	{
	        		log.debug("readSlotWithFields slot=" + slot.getSlotClass().getName() + " field=" + field.getFieldName() + "<<-reading Object");
	        	}
	            Object objTmp = input.readObject();
	            FieldsManager.getFieldsManager().setObject(obj,field,objTmp);
		    }
		}
	}

	static FieldsContainer readSlotWithFields(ClassMetaDataSlot slot, ObjectInput input) throws IOException, ClassNotFoundException {
    	if (isDebug)
    	{
    		log.debug("readSlotWithFields slot=" + slot.getSlotClass().getName());
    	}
    	final int numberOfFields = input.readInt();
    	
    	FieldsContainer container = new FieldsContainer(slot);
		
		for (int i=0;i<numberOfFields;i++)
		{
			Map.Entry entry = container.readField(input);
			container.setField((String)entry.getKey(), entry.getValue());
			
		}
		
		return container;
	}

    public boolean canPersist(Object obj)
	{
		// not implemented
		return false;
	}

	// static methods for RegularObjectPersisterInterface
	
	public void staticDefaultWrite(ObjectOutput output, Object obj,
			ClassMetaData metaClass, ObjectSubstitutionInterface substitution)
			throws IOException {
		
		defaultWrite(output, obj, metaClass, substitution);
	}

	public void staticWriteSlotWithFields(ClassMetaDataSlot slot,
			ObjectOutput output, Object obj,
			ObjectSubstitutionInterface substitution) throws IOException {
		
		writeSlotWithFields(slot, output, obj, substitution);
		
	}

	public Object staticDefaultRead(ObjectInput input, Object obj,
			StreamingClass streaming, ClassMetaData metaData,
			ObjectSubstitutionInterface substitution) throws IOException {
		
		return defaultRead(input,  obj, streaming,  metaData, substitution);
	}

	public void staticReadSlotWithFields(short[] fieldsKey,
			ClassMetaDataSlot slot, ObjectInput input, Object obj)
			throws IOException, ClassNotFoundException {

		readSlotWithFields(fieldsKey, slot, input, obj);
	}

	public FieldsContainer staticReadSlotWithFields(ClassMetaDataSlot slot,
			ObjectInput input) throws IOException, ClassNotFoundException {

		return readSlotWithFields(slot, input);
	}

	public void staticWriteSlotWithFieldsContainer(ClassMetaDataSlot slot,
			ObjectOutput output, FieldsContainer fields,
			ObjectSubstitutionInterface substitution) throws IOException {

		writeSlotWithFieldsContainer(slot, output, fields, substitution);
	}

    
}
