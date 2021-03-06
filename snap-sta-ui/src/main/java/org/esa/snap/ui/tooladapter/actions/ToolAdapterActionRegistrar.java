/*
 *
 *  * Copyright (C) 2015 CS SI
 *  *
 *  * This program is free software; you can redistribute it and/or modify it
 *  * under the terms of the GNU General Public License as published by the Free
 *  * Software Foundation; either version 3 of the License, or (at your option)
 *  * any later version.
 *  * This program is distributed in the hope that it will be useful, but WITHOUT
 *  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 *  * more details.
 *  *
 *  * You should have received a copy of the GNU General Public License along
 *  * with this program; if not, see http://www.gnu.org/licenses/
 *
 */
package org.esa.snap.ui.tooladapter.actions;

import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.OperatorSpiRegistry;
import org.esa.snap.core.gpf.descriptor.ToolAdapterOperatorDescriptor;
import org.esa.snap.core.gpf.operators.tooladapter.ToolAdapterIO;
import org.esa.snap.core.gpf.operators.tooladapter.ToolAdapterListener;
import org.esa.snap.core.gpf.operators.tooladapter.ToolAdapterOpSpi;
import org.esa.snap.core.gpf.operators.tooladapter.ToolAdapterRegistry;
import org.esa.snap.rcp.util.Dialogs;
import org.esa.snap.utils.AdapterWatcher;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.modules.OnStart;
import org.openide.modules.OnStop;
import org.openide.modules.Places;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Helper class for creating menu entries for tool adapter operators.
 * The inner runnable class should be invoked when the IDE starts, and will
 * register the available adapters as menu actions.
 * TODO: Delegate registration of SPIs to snap-sta. Register actions only for what was already registered
 * @author Cosmin Cara
 */

public class ToolAdapterActionRegistrar {

    private static final String DEFAULT_MENU_PATH = "Menu/Tools/External Tools";

    private static final Map<String, ToolAdapterOperatorDescriptor> actionMap = new HashMap<>();

    private static final ToolAdapterListener listener = new ToolAdapterListener() {
        @Override
        public void adapterAdded(ToolAdapterOperatorDescriptor operatorDescriptor) {
            registerOperatorMenu(operatorDescriptor);
        }

        @Override
        public void adapterRemoved(ToolAdapterOperatorDescriptor operatorDescriptor) {
            removeOperatorMenu(operatorDescriptor);
        }
    };

    /**
     * Returns the map of menu items (actions) and operator descriptors.
     *
     * @return
     */
    public static Map<String, ToolAdapterOperatorDescriptor> getActionMap() {
        return actionMap;
    }

    public static String getDefaultMenuLocation() {
        return DEFAULT_MENU_PATH;
    }

    /**
     * Creates a menu entry in the default menu location (Tools > External Tools) for the given adapter operator.
     *
     * @param operator  The operator descriptor
     */
    public static void registerOperatorMenu(ToolAdapterOperatorDescriptor operator) {
        String menuGroup = operator.getMenuLocation();
        if (menuGroup == null) {
            operator.setMenuLocation(DEFAULT_MENU_PATH);
        }
         registerOperatorMenu(operator, true);
    }

    /**
     * Creates a menu entry in the given menu location for the given adapter operator.
     *
     * @param operator  The operator descriptor
     * @param hasChanged      Flag that indicates if the descriptor has changed (true) or is new (false)
     */
    public static void registerOperatorMenu(ToolAdapterOperatorDescriptor operator, boolean hasChanged) {
        String menuLocation = operator.getMenuLocation();
        try {
            getDefaultLocation();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (menuLocation == null) {
            menuLocation = getDefaultMenuLocation();
            operator.setMenuLocation(menuLocation);
        }
        FileObject menuFolder = FileUtil.getConfigFile(menuLocation);
        try {
            if (menuFolder == null) {
                menuFolder = FileUtil.getConfigFile("Menu");
                String[] menuTokens = menuLocation.split("/");
                for (int i = 1; i < menuTokens.length; i++) {
                    FileObject subMenu = menuFolder.getFileObject(menuTokens[i]);
                    if (subMenu == null) {
                        menuFolder = menuFolder.createFolder(menuTokens[i]);
                    } else {
                        menuFolder = subMenu;
                    }
                }
                menuFolder.setAttribute("position", 9999);
            }
            String menuKey = operator.getAlias();
            FileObject newItem = menuFolder.getFileObject(menuKey, "instance");
            if (newItem == null) {
                newItem = menuFolder.createData(menuKey, "instance");
            }
            ExecuteToolAdapterAction action = new ExecuteToolAdapterAction(menuKey);
            newItem.setAttribute("instanceCreate", action);
            newItem.setAttribute("instanceClass", action.getClass().getName());
            if (actionMap.containsKey(menuKey)) {
                actionMap.remove(menuKey);
            }
            actionMap.put(menuKey, operator);
        } catch (IOException e) {
            Dialogs.showError("Error:" + e.getMessage());
        }
    }

    public static void removeOperatorMenu(ToolAdapterOperatorDescriptor operator) {
        //if (!operator.isFromPackage()) {
        FileObject menuFolder = FileUtil.getConfigFile(operator.getMenuLocation());
        try {
            if (menuFolder != null) {
                String operatorAlias = operator.getAlias();
                FileObject newItem = menuFolder.getFileObject(operatorAlias, "instance");
                if (newItem != null) {
                    newItem.delete();
                }
                if (actionMap.containsKey(operatorAlias)) {
                    actionMap.remove(operatorAlias);
                }
            }
            FileObject defaultLocation = getDefaultLocation();
            FileObject[] children = defaultLocation.getChildren();
            if (children == null || children.length == 0) {
                defaultLocation.delete();
            }
        } catch (IOException e) {
            Dialogs.showError("Error:" + e.getMessage());
        }
        //}
    }

    private static FileObject getDefaultLocation() throws IOException {
        FileObject defaultMenu = FileUtil.getConfigFile(DEFAULT_MENU_PATH);
        if (defaultMenu == null) {
            defaultMenu = FileUtil.getConfigFile("Menu").getFileObject("Tools");
            defaultMenu = defaultMenu.createFolder(DEFAULT_MENU_PATH.replace("Menu/Tools/", ""));
            FileObject[] objects = defaultMenu.getParent().getChildren();
            int position = 9999;
            Object value = objects[objects.length - 1].getAttribute("position");
            if (value != null) {
                position = ((Integer) value) + 1;
            }
            defaultMenu.setAttribute("position", position);
        }
        return defaultMenu;
    }

    /**
     * Startup class that performs menu initialization to be invoked by NetBeans.
     */
    @OnStart
    public static class StartOp implements Runnable {
        @Override
        public void run() {
            OperatorSpiRegistry spiRegistry = GPF.getDefaultInstance().getOperatorSpiRegistry();
            Path jarPaths = Paths.get(Places.getUserDirectory().getAbsolutePath(), "modules");
            Map<String, File> jarAdapters = Files.exists(jarPaths) ? getJarAdapters(jarPaths.toFile()) : null;
            if (spiRegistry != null) {
                Collection<OperatorSpi> operatorSpis = spiRegistry.getOperatorSpis();
                if (operatorSpis != null) {
                    if (operatorSpis.size() == 0) {
                        operatorSpis.addAll(ToolAdapterIO.searchAndRegisterAdapters());
                    }
                    /*final List<OperatorSpi> orphaned = operatorSpis.stream()
                            .filter(spi -> spi instanceof ToolAdapterOpSpi &&
                                    ((ToolAdapterOperatorDescriptor) spi.getOperatorDescriptor()).isFromPackage() &&
                                    jarAdapters != null && !jarAdapters.containsKey(spi.getOperatorDescriptor().getAlias()))
                            .collect(Collectors.toList());
                    orphaned.forEach(spi -> {
                        ToolAdapterOperatorDescriptor operatorDescriptor = (ToolAdapterOperatorDescriptor) spi.getOperatorDescriptor();
                        operatorSpis.remove(spi);
                        //ToolAdapterActionRegistrar.removeOperatorMenu(operatorDescriptor);
                        ToolAdapterIO.removeOperator(operatorDescriptor);
                    });*/
                    operatorSpis.stream().filter(spi -> spi instanceof ToolAdapterOpSpi).forEach(spi -> {
                        ToolAdapterOperatorDescriptor operatorDescriptor = (ToolAdapterOperatorDescriptor) spi.getOperatorDescriptor();
                        registerOperatorMenu(operatorDescriptor, false);
                    });
                }
                ToolAdapterRegistry.INSTANCE.addListener(listener);
                AdapterWatcher.INSTANCE.startMonitor();
            }
        }

        private Map<String, File> getJarAdapters(File fromPath) {
            Map<String, File> output = new HashMap<>();
            if (fromPath != null && fromPath.exists()) {
                String descriptionKeyName = "OpenIDE-Module-Short-Description";
                Attributes.Name typeKey = new Attributes.Name("OpenIDE-Module-Type");
                File[] files = fromPath.listFiles((dir, name) -> name.endsWith("jar"));
                if (files != null) {
                    try {
                        for (File file : files) {
                            JarFile jarFile = new JarFile(file);
                            Manifest manifest = jarFile.getManifest();
                            if (manifest != null) {
                                Attributes manifestEntries = manifest.getMainAttributes();
                                if (manifestEntries.containsKey(typeKey) &&
                                        "STA".equals(manifestEntries.getValue(typeKey.toString()))) {
                                    output.put(manifestEntries.getValue(descriptionKeyName), file);
                                }
                            }
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
            return output;
        }
    }

    @OnStop
    public static class StopOp implements Runnable {
        @Override
        public void run() {
            AdapterWatcher.INSTANCE.stopMonitor();
        }
    }
}
