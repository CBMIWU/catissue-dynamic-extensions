package edu.wustl.metadata.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.TaggedValueInterface;
import edu.common.dynamicextensions.entitymanager.EntityGroupManager;
import edu.common.dynamicextensions.entitymanager.EntityGroupManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.cab2b.common.exception.RuntimeException;

public class PackageName {

    private static EntityGroupManagerInterface entityGroupManager = EntityGroupManager.getInstance();

    public static void main(String[] args) {
        getPackageName(args[0], args[1]);
    }

    private static void getPackageName(String directoryPath, String xmiName) {

        String packageName = null;
        try {
            File file = new File(xmiName);
            int indexOfExtension = file.getName().lastIndexOf('.');
            if (indexOfExtension != -1) {
                xmiName = file.getName().substring(0, indexOfExtension);
            }

            EntityGroupInterface entityGroups = entityGroupManager.getEntityGroupByName(xmiName);
            EntityInterface entity = entityGroups.getEntityCollection().iterator().next();
            Set<TaggedValueInterface> taggedValues = (Set<TaggedValueInterface>) entity.getEntityGroup().getTaggedValueCollection();

            Iterator<TaggedValueInterface> taggedValuesIter = taggedValues.iterator();
            while (taggedValuesIter.hasNext()) {
                TaggedValueInterface taggedValue = taggedValuesIter.next();
                if (taggedValue.getKey().equals("PackageName")) {
                    packageName = taggedValue.getValue();
                    break;
                }
            }
            int start = packageName.lastIndexOf('.');
            String entityName = xmiName;
            String packageEntityName = xmiName;
            if (start != -1) {
                packageEntityName = packageName.substring(packageName.lastIndexOf('.') + 1, packageName.length());

                StringBuffer tempPackageName = new StringBuffer(
                        packageName.substring(0, packageName.indexOf('.') + 1));
                packageName = packageName.substring(packageName.indexOf('.') + 1, packageName.length());
                if (packageName.indexOf('.') != -1) {
                    tempPackageName.append(packageName.substring(0, packageName.indexOf('.')));
                }
                packageName = tempPackageName.toString().replace('.', '/');
            }

            writeToFile(directoryPath, packageName, entityName, packageEntityName);
        } catch (DynamicExtensionsSystemException e) {
            throw new RuntimeException("Error while retriving Entity Group", e);
        } catch (DynamicExtensionsApplicationException e) {
            throw new RuntimeException("Error while retriving Entity Group", e);
        }
    }

    private static void writeToFile(String directoryPath, String packageName, String entityName,
                                    String packageEntityName) {
        File newFile = new File(directoryPath + File.separator + "Package.txt");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));
            writer.write("entity.name=" + entityName);
            writer.newLine();
            writer.write("de.package.name=" + packageName);
            writer.newLine();
            writer.write("cacore.package.name=" + packageEntityName);
            writer.close();
            newFile.renameTo(new File(directoryPath + File.separator + "Package.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Error while writing Entity Groups Name to file", e);
        }
    }
}
