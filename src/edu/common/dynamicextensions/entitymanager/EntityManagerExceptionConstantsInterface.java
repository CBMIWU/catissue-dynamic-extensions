/**
 *<p>Title: </p>
 *<p>Description:  </p>
 *<p>Copyright:TODO</p>
 *@author Vishvesh Mulay
 *@version 1.0
 */ 
package edu.common.dynamicextensions.entitymanager;



public interface EntityManagerExceptionConstantsInterface
{

    //Exception constant in case of hibernate system exception
    static final String DYEXTN_S_001 = "DYEXTN_S_001";

    //Exception constant in case of JDBC system exception
    static final String DYEXTN_S_002 = "DYEXTN_S_002";

    //Exception constant in case of user not authenticated
    static final String DYEXTN_A_001 = "DYEXTN_A_002";
    
    //Exception constant in case of fatal system exception
    static final String DYEXTN_S_000 = "DYEXTN_A_000";
    
    //Exception constant in case of data type factory not initialised
    static final String DYEXTN_A_002 = "DYEXTN_A_002";

    
    
}
