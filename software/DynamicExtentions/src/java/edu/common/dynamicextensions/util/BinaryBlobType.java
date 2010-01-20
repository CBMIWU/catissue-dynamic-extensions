/*
 *  Copyright 2004 Blandware (http://www.blandware.com)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package edu.common.dynamicextensions.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.type.Type;
import org.hibernate.usertype.CompositeUserType;

import edu.wustl.common.util.logger.Logger;

/**
 * <p>This is wrapper for both BinaryType and BlobType in order to give developer the ability to switch them via config</p>
 * <p>Returned class is <code>byte[]</code>, that's why we should make conversion of BLOB</p>
 * <p>User hibernate.binary_or_blob hibernate property in order to manage behaviour</p>
 * <p/>
 * <p><a href="BinaryBlobType.java.html"><i>View Source</i></a></p>
 *
 * @author Andrey Grebnev <a href="mailto:andrey.grebnev@blandware.com">&lt;andrey.grebnev@blandware.com&gt;</a>
 * @version $Revision$ $Date$
 */
public class BinaryBlobType implements CompositeUserType
{

	/**
	 * Default buffer size in order to copy InputStream to byte[]
	 */
	protected static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

	/**
	 * If it is </code>true</code> we use <code>BlobType</code>, otherwise <code>BinaryType</code>
	 */
	protected boolean isBlob = true;

	/**
	 * Get the "property names" that may be used in a
	 * query.
	 *
	 * @return an array of "property names"
	 */
	public String[] getPropertyNames()
	{
		return new String[]{"value"};
	}

	/**
	 * Get the corresponding "property types".
	 *
	 * @return an array of Hibernate types
	 */
	public Type[] getPropertyTypes()
	{
		Type[] propertyType;
		if (isBlob)
		{
			propertyType = new Type[]{Hibernate.BLOB};
		}
		else
		{
			propertyType = new Type[]{Hibernate.BINARY};
		}
		return propertyType;
	}

	/**
	 * Get the value of a property.
	 *
	 * @param component an instance of class mapped by this "type"
	 * @param property
	 * @return the property value
	 * @throws org.hibernate.HibernateException
	 *
	 */
	public Object getPropertyValue(Object component, int property) throws HibernateException
	{
		return component;
	}

	/**
	 * Set the value of a property.
	 *
	 * @param component an instance of class mapped by this "type"
	 * @param property
	 * @param value     the value to set
	 * @throws org.hibernate.HibernateException
	 *
	 */
	public void setPropertyValue(Object component, int property, Object value)
			throws HibernateException
	{
		// TODO This method will be provided if required.
	}

	/**
	 * The class returned by <tt>nullSafeGet()</tt>.
	 *
	 * @return Class
	 */
	public Class returnedClass()
	{
		return Hibernate.BINARY.getReturnedClass();
	}

	/**
	 * Compare two instances of the class mapped by this type for persistence "equality".
	 * Equality of the persistent state.
	 *
	 * @param object1
	 * @param object2
	 * @return boolean
	 * @throws org.hibernate.HibernateException
	 *
	 */
	public boolean equals(Object object1, Object object2) throws HibernateException
	{
		return Hibernate.BINARY.isEqual(object1, object2);
	}

	/**
	 * Get a hashcode for the instance, consistent with persistence "equality"
	 */
	public int hashCode(Object object) throws HibernateException
	{
		return Hibernate.BINARY.getHashCode(object, null);
	}

	/**
	 * Retrieve an instance of the mapped class from a JDBC resultset. Implementors
	 * should handle possibility of null values.
	 *
	 * @param resultSet      a JDBC result set
	 * @param names   the column names
	 * @param session
	 * @param owner   the containing entity
	 * @return Object
	 * @throws org.hibernate.HibernateException
	 *
	 * @throws java.sql.SQLException
	 */
	public Object nullSafeGet(ResultSet resultSet, String[] names, SessionImplementor session,
			Object owner) throws HibernateException, SQLException
	{
		Object objectToReturn;
		if (isBlob)
		{
			Blob blob = (Blob) Hibernate.BLOB.nullSafeGet(resultSet, names, session, owner);
			if (blob == null)
			{
				objectToReturn = null;
			}
			else
			{
				objectToReturn = copyData(blob.getBinaryStream());
			}
		}
		else
		{
			objectToReturn = Hibernate.BINARY.nullSafeGet(resultSet, names, session, owner);
		}
		return objectToReturn;
	}

	/**
	 * Write an instance of the mapped class to a prepared statement. Implementors
	 * should handle possibility of null values. A multi-column type should be written
	 * to parameters starting from <tt>index</tt>.
	 *
	 * @param prepStatement      a JDBC prepared statement
	 * @param value   the object to write
	 * @param index   statement parameter index
	 * @param session
	 * @throws org.hibernate.HibernateException
	 *
	 * @throws java.sql.SQLException
	 */
	public void nullSafeSet(PreparedStatement prepStatement, Object value, int index,
			SessionImplementor session) throws HibernateException, SQLException
	{
		if (isBlob)
		{
			if (value == null)
			{
				Hibernate.BLOB.nullSafeSet(prepStatement, value, index, session);
			}
			else
			{
				Blob blob = Hibernate.createBlob((byte[]) value);
				Hibernate.BLOB.nullSafeSet(prepStatement, blob, index, session);
			}
		}
		else
		{
			Hibernate.BINARY.nullSafeSet(prepStatement, value, index, session);
		}
	}

	/**
	 * Return a deep copy of the persistent state, stopping at entities and at collections.
	 *
	 * @param value generally a collection element or entity field
	 * @return Object a copy
	 * @throws org.hibernate.HibernateException
	 *
	 */
	public Object deepCopy(Object value) throws HibernateException
	{
		return Hibernate.BINARY.deepCopy(value, null, null);
	}

	/**
	 * Check if objects of this type mutable.
	 *
	 * @return boolean
	 */
	public boolean isMutable()
	{
		return Hibernate.BINARY.isMutable();
	}

	/**
	 * Transform the object into its cacheable representation. At the very least this
	 * method should perform a deep copy. That may not be enough for some implementations,
	 * however; for example, associations must be cached as identifier values. (optional
	 * operation)
	 *
	 * @param value   the object to be cached
	 * @param session
	 * @return a cachable representation of the object
	 * @throws org.hibernate.HibernateException
	 *
	 */
	public Serializable disassemble(Object value, SessionImplementor session)
			throws HibernateException
	{
		return Hibernate.BINARY.disassemble(value, session, null);
	}

	/**
	 * Reconstruct an object from the cacheable representation. At the very least this
	 * method should perform a deep copy. (optional operation)
	 *
	 * @param cached  the object to be cached
	 * @param session
	 * @param owner   the owner of the cached object
	 * @return a reconstructed object from the cachable representation
	 * @throws org.hibernate.HibernateException
	 *
	 */
	public Object assemble(Serializable cached, SessionImplementor session, Object owner)
			throws HibernateException
	{
		return Hibernate.BINARY.assemble(cached, session, owner);
	}

	/**
	 * During merge, replace the existing (target) value in the entity we are merging to
	 * with a new (original) value from the detached entity we are merging. For immutable
	 * objects, or null values, it is safe to simply return the first parameter. For
	 * mutable objects, it is safe to return a copy of the first parameter. However, since
	 * composite user types often define component values, it might make sense to recursively
	 * replace component values in the target object.
	 *
	 * @param original
	 * @param target
	 * @param session
	 * @param owner
	 * @return first parameter
	 * @throws org.hibernate.HibernateException
	 *
	 */
	public Object replace(Object original, Object target, SessionImplementor session, Object owner)
			throws HibernateException
	{
		return Hibernate.BINARY.replace(original, target, session, owner, null);
	}

	/**
	 * Copy data from InputStream into byte[]
	 *
	 * @param input source
	 * @return the resulted array
	 */
	protected byte[] copyData(InputStream input)
	{
		ByteArrayOutputStream output = null;
		try
		{
			output = new ByteArrayOutputStream();
			byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			int pointerPosition = input.read(buffer);
			while (-1 != pointerPosition)
			{
				output.write(buffer, 0, pointerPosition);
				pointerPosition = input.read(buffer);
			}
			return output.toByteArray();

		}
		catch (IOException ex)
		{
			throw new RuntimeException("Cannot copy data from InputStream into byte[]", ex);
		}
		finally
		{
			try
			{
				input.close();
			}
			catch (IOException ex2)
			{
				Logger.out.error("The cause of the exception is - " + ex2.getMessage());
				//do nothing
			}
			try
			{
				output.close();
			}
			catch (IOException ex2)
			{
				Logger.out.error("The cause of the exception is - " + ex2.getMessage());
				//do nothing
			}
		}
	}
}
