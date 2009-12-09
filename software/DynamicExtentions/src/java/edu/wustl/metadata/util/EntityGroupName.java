package edu.wustl.metadata.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.common.dynamicextensions.entitymanager.EntityGroupManager;
import edu.common.dynamicextensions.entitymanager.EntityGroupManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.cab2b.common.exception.RuntimeException;
import edu.wustl.common.beans.NameValueBean;

public class EntityGroupName {

    private static EntityGroupManagerInterface entityGroupManager = EntityGroupManager.getInstance();

    public void getAllEntityGroupBean(String directoryPath) {
        List<String> allEntityGroupNames = new ArrayList<String>();
        try {
            Collection<NameValueBean> allEntityGroupBeansNames = entityGroupManager.getAllEntityGroupBeans();
            for (NameValueBean nameValueBean : allEntityGroupBeansNames) {
                allEntityGroupNames.add(nameValueBean.getName());
            }
            writeToFile(allEntityGroupNames, directoryPath);
        } catch (DynamicExtensionsSystemException e) {
            throw new RuntimeException("Error while retriving Entity Group Names", e);
        }
    }

    private void writeToFile(List<String> allEntityGroupNames, String directoryPath) {
        File newFile = new File(directoryPath + File.separator + "EntityGroups.txt");
        String entityGroupTag = "entity.group.name=";
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));
            writer.write(entityGroupTag);
            for (String entity : allEntityGroupNames) {
                writer.write(entity);
                writer.write(',');
            }
            writer.close();
            newFile.renameTo(new File(directoryPath + File.separator + "EntityGroup.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Error while writing Entity Groups Name to file", e);
        }
    }

    public static void main(String[] args) {
        EntityGroupName entityGroupName = new EntityGroupName();
        entityGroupName.getAllEntityGroupBean(args[0]);
    }

}
