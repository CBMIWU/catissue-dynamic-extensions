package edu.common.dynamicextensions.ui.util; 

import java.io.Serializable;

import edu.common.dynamicextensions.domain.DynamicExtensionBaseDomainObject;
import edu.common.dynamicextensions.domaininterface.FileAttributeRecordValueInterface;


/**
 * This Class represents the a single value for multi select attribute.
 * 
 * @author Rahul Ner 
 */
public class FileAttributeRecordValue extends DynamicExtensionBaseDomainObject implements Serializable,FileAttributeRecordValueInterface
{

	/**
	 * content of the file.
	 */
	byte[] fileContent;
	
	/**
	 * this is the name of the file 
	 */
	String fileName;
	
	/**
	 * file content e.g. MIME text etc.
	 */
	String contentType;
	
	
	/**
	 * This method returns the unique identifier of the AbstractMetadata.
	 * @return the identifier of the AbstractMetadata.
	 */
	public Long getId()
	{
		return id;
	}


	
	/**
	 * @return Returns the contentType.
	 */
	public String getContentType()
	{
		return contentType;
	}


	
	/**
	 * @param contentType The contentType to set.
	 */
	public void setContentType(String contentType)
	{
		this.contentType = contentType;
	}


	
	/**
	 * @return Returns the fileContent.
	 */
	public byte[] getFileContent()
	{
		return fileContent;
	}


	
	/**
	 * @param fileContent The fileContent to set.
	 */
	public void setFileContent(byte[] fileContent)
	{
		this.fileContent = fileContent;
	}


	
	/**
	 * @return Returns the fileName.
	 */
	public String getFileName()
	{
		return fileName;
	}


	
	/**
	 * @param fileName The fileName to set.
	 */
	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}
	
	/**
	 * This method copies the values from one file record to another
	 * @param fileRecordValue
	 */
	public void copyValues(FileAttributeRecordValue  fileRecordValue) {
		this.contentType = fileRecordValue.getContentType();
		this.fileName =   fileRecordValue.getFileName();
		this.fileContent = fileRecordValue.getFileContent();
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return this.fileName;
	}
}
