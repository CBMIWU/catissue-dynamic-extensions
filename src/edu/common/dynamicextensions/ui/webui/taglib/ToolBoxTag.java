package edu.common.dynamicextensions.ui.webui.taglib;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import edu.wustl.common.util.logger.Logger;


/**
 * Custom Tag to display ToolBox Menu
 *       <p> This Tag accepts list of Id-Value pairs among which Values will be displyed in
 * Tools menu. Whenever user clicks on tool Javascript function specified by user is
 * called and ID of the selected tool wil be passed to it as parameter.
 *
 * @author deepti_shelar
 
 */
public class ToolBoxTag extends TagSupport {
    
    
    
    /**
     * Unique Tag Id
     */
    protected String id = null;
    
    /**
     * List of Id-Value pairs.
     * <li> ID - Id of the selector which will be passed Javascript function , when it is selected
     * <li> Value - Displayed in the selector Menu.
     * @see java.util.List
     */
    protected List toolsList = null;
    
    /**
     * JavaScript Function Name which has to be called whenwver
     * user clicks on any selection item.
     */
    protected String onClick = null;
    
    /**
     * selector Id which has to be shown selected initially.
     */
    protected Object selectedToolID = null;
    /**
     * height
     */
    protected String height = null;
    /**
     * width
     */
    protected String width = null;
    /**
     * This will indicate whether the selector has to be shown selected.
     * By default,(if it is not specified) tag will display  "selectedSelectorID" selector as selected.
     * or the first selector as selected.
     * Otherwise when user has set this attribute to false, no selector will be shown selected.
     */
    protected Boolean showSelected = null;
    /**
     * Display Mode - View, Edit ,etc.
     */
    protected String displayMode = "";
    /**
     * keysOrStrings Specifies whether option list contains message keys or
     * string values.
     */
    protected Boolean messageKeys = null;
    /**
     * bundleName Specifies the resource bundle name
     */
    protected String bundleName = null;
    /**
     * styleClass Specifies the styleClass
     */
    protected String styleClass = null;
    /**
     * This list contains disable options list
     */
    protected List disableList = new ArrayList();
    
    /**
     * selectorTooltipList
     */
    protected List selectorTooltipList = null;
  
    /**
     * getter method for selectorsList
     * @return Returns the selectorsList.
     * @since TODO
     */
    public List getToolsList() {
        return toolsList;
    }
    /**
     * setter method for selectorsList
     * @param selectorsList The selectorsList to set.
     * @see java.util.HashMap
     * @since TODO
     */
    public void setToolsList(List toolsList) {
        this.toolsList = toolsList;
    }
    /**
     * @return Returns the id.
     * @since TODO
     */
    public String getId() {
        return id;
    }
    /**
     * @param id The id to set.
     * @since TODO
     */
    public void setId(String id) {
        this.id = id;
    }
    /**
     * @return Returns the onClick.
     * @since TODO
     */
    public String getOnClick() {
        return onClick;
    }
    /**
     * @param onClick The onClick to set.
     * @since TODO
     */
    public void setOnClick(String onClick) {
        this.onClick = onClick;
    }
    
    /**
     * @return Returns the selectedSelectorID.
     * @since TODO
     */
    public Object getSelectedToolID() {
        return selectedToolID;
    }
    /**
     * @param selectedSelectorID The selectedSelectorID to set.
     * @since TODO
     */
    public void setSelectedToolID(Object selectedToolID) {
        this.selectedToolID = selectedToolID;
    }
    
    /**
     * @return Returns the displayMode.
     * @since TODO
     */
    public String getDisplayMode() {
        return displayMode;
    }
    /**
     * @param displayMode The displayMode to set.
     * @since TODO
     */
    public void setDisplayMode(String displayMode) {
        this.displayMode = displayMode;
    }
    
    /**
     * Getter method for bundleName.
     * @return Returns the bundleName.
     */
    public String getBundleName() {
        return bundleName;
    }
    /**
     * Setter method for bundleName
     * @param bundleName The bundleName to set.
     */
    public void setBundleName(String bundleName) {
        this.bundleName = bundleName;
    }
    
    /**
     * Getter method for messageKeys.
     * @return Returns the messageKeys.
     */
    public Boolean getMessageKeys() {
        return messageKeys;
    }
    /**
     * Setter method for messageKeys
     * @param messageKeys The messageKeys to set.
     */
    public void setMessageKeys(Boolean messageKeys) {
        this.messageKeys = messageKeys;
    }
    
    /**
     * Getter method for showSelected.
     * @return Returns the showSelected.
     */
    public Boolean getShowSelected() {
        return showSelected;
    }
    /**
     * Setter method for showSelected
     * @param showSelected The showSelected to set.
     */
    public void setShowSelected(Boolean showSelected) {
        this.showSelected = showSelected;
    }
    /**
     * @return Returns the height.
     */
    public String getHeight() {
        return height;
    }
    /**
     * @param height The height to set.
     */
    public void setHeight(String height) {
        this.height = height;
    }
    /**
     * @return Returns the width.
     */
    public String getWidth() {
        return width;
    }
    /**
     * @param width The width to set.
     */
    public void setWidth(String width) {
        this.width = width;
    }
    /**
     * @return Returns the styleClass.
     */
    public String getStyleClass() {
        return styleClass;
    }
    /**
     * @param styleClass The styleClass to set.
     */
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }
    
    /**
     * @return Returns the selectorTooltipList.
     */
    public List getSelectorTooltipList() {
        return selectorTooltipList;
    }
    /**
     * @param selectorTooltipList The selectorTooltipList to set.
     */
    public void setSelectorTooltipList(List selectorTooltipList) {
        this.selectorTooltipList = selectorTooltipList;
    }
    /**
     * Validates all the attributes passed to the tag
     * @return boolean - true if all the attributes passed to the tag are valid
     * @since TODO
     */
    private boolean isDataValid() {
        if (id == null) {
        	Logger.out.debug("Selector Tag  Id is must");
            return false;
        }
        if (toolsList == null) {
        	Logger.out.debug(" toolsList is must");
            return false;
        }
        if (onClick == null) {
        	Logger.out.debug(" onClick Function Name is must");
            return false;
        }
       /* if (selectedToolID == null) {
        	selectedToolID = ((NameValueBean) toolsList.get(0));
        }*/
        if (messageKeys == null) {
            messageKeys = new Boolean(false);
        }
        if (messageKeys.booleanValue() && bundleName == null) {
        	Logger.out.debug(" For using message keys , Resource Bundle Name is must.");
            return false;
        }
        if (showSelected == null) {
            showSelected = new Boolean(true);
        }
        if (height == null) {
            height = "261px";
        }
        if (width == null) {
            width = "143px";
        }
        if (styleClass == null) {
            styleClass = "level4_blue_label";
        }
        return true;
    }
    
    /**
     * This method contains no operations.
     * @return int SKIP_BODY
     * @since TODO
     */
    public int doStartTag() {
        
    	Logger.out.debug(" Entering Selector List Tag ...");
        
        return SKIP_BODY;
    }
    
    /**
     * Process the end of this tag.
     * Generates HTML to be rendered.
     * @return int EVAL_PAGE
     * @since TODO
     */
    public int doEndTag() {
        if (!isDataValid()) {
            return EVAL_PAGE;
        }
        Logger.out.debug(" Entering Selectors List Tag : doEndTag method");
        StringBuffer sb = new StringBuffer();
        int toolsListSize=toolsList.size();
        sb.append("\n<div id=\"" + id + "\"  class=\"formField\"  style=\"height: " + height + "; width:" + width + "; overflow-y: auto;\">");
        sb.append("\n       <table border=\"3\" cellspacing =\"0\" cellpadding =\"0\" id=\"" + id + "tableContainingTools\" class=\"tableBorder_styles_selectormenu\">");
        for (int i=0; i < toolsListSize; i++) {
            String selectedTool =  toolsList.get(i).toString();
            Logger.out.debug("Selector List : " + selectedTool);
            if (selectedTool != null) {
            	sb.append("\n<tr onclick=\""+onClick+"('"+ selectedTool+"','"+ id +"')\">"+selectedTool+"</tr>");
            }
        }
        sb.append("\n        </table> ");
        sb.append("\n</div> ");
        sb.append("<html:hidden property=\""+id+"_tool\" value=\"\">");
        try {
            JspWriter out = pageContext.getOut();
            out.println(sb.toString());
        } catch (Exception e) {
            Logger.out.debug("IO Exception occured. No response generated.");
        }
        Logger.out.debug(" Leaving Selector List Tag");
        return EVAL_PAGE;
    }
    
    /**
     * Releases acquired resources.
     * @since TODO
     * */
    public void release() {
        super.release();
        this.toolsList= null;
        this.id = null;
        this.onClick = null;
        this.selectedToolID = null;
    }
    
    
    /**
     * 
     * @param id Object
     * @return boolean
     */
    public boolean isOptionEnabled(Object id){
        Iterator disableListIterator = disableList.iterator();
        String optionId = null;
        while (disableListIterator.hasNext()) {
            optionId = (String) disableListIterator.next();
            if (optionId.equals(id)) {
                return false;
            }
        }
        return true;
    }
    /**
     * @return Returns the disableList.
     */
    public List getDisableList() {
        return disableList;
    }
    /**
     * @param disableList The disableList to set.
     */
    public void setDisableList(List disableList) {
        this.disableList = disableList;
    }
}
