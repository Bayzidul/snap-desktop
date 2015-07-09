/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esa.snap.smart.configurator.ui;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.prefs.BackingStoreException;
import java.util.regex.Pattern;
import javax.swing.*;

import org.apache.commons.lang.StringUtils;
import org.esa.snap.configurator.BenchmarkSingleCalculus;
import org.esa.snap.configurator.JavaSystemInfos;
import org.esa.snap.configurator.PerformanceParameters;
import org.esa.snap.configurator.Benchmark;
import org.esa.snap.configurator.ConfigurationOptimizer;
import org.esa.snap.framework.gpf.GPF;
import org.esa.snap.framework.gpf.OperatorSpi;
import org.esa.snap.rcp.SnapApp;
import org.esa.snap.util.SystemUtils;
import org.esa.snap.util.io.FileUtils;

final class PerformancePanel extends javax.swing.JPanel {
    
    /**
     * Color for fields filed with values in place in the application
     */
    private static final Color CURRENT_VALUES_COLOR = Color.BLACK;

    /**
     * Color for modified fields
     */
    private static final Color MODIFIED_VALUES_COLOR = Color.BLUE;

    /**
     * Color for error fields
     *
     */
    private static final Color ERROR_VALUES_COLOR = Color.RED;


    /**
     * Separator between values to be tested for the bechmark
     */
    private static final String BENCHMARK_SEPARATOR=";";

    /**
     * Tool for optimizing and setting the performance parameters
     */
    private final ConfigurationOptimizer confOptimizer;

    private final PerformanceOptionsPanelController controller;



    private static Path getUserDirPathFromString(String userDirString) {
        Path userDirPath = null;
        try {
            File userDirAsFile = new File(userDirString);
            userDirPath = FileUtils.getPathFromURI(userDirAsFile.toURI());
        } catch (IOException e) {
            SystemUtils.LOG.log(Level.WARNING, "Cannot convert performance parameters to PATH: {0}", userDirString);
        }

        return userDirPath;
    }

    PerformancePanel(PerformanceOptionsPanelController controller) {

        this.controller = controller;

        confOptimizer = ConfigurationOptimizer.getInstance();

        initComponents();

        vmParametersTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (vmParametersTextField.getForeground() != MODIFIED_VALUES_COLOR) {
                    vmParametersTextField.setForeground(MODIFIED_VALUES_COLOR);
                    controller.changed();
                }
            }
        });

        userDirTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (userDirTextField.getForeground() != MODIFIED_VALUES_COLOR) {
                    userDirTextField.setForeground(MODIFIED_VALUES_COLOR);
                    controller.changed();
                }
            }
        });

        nbThreadsTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (nbThreadsTextField.getForeground() != MODIFIED_VALUES_COLOR) {
                    nbThreadsTextField.setForeground(MODIFIED_VALUES_COLOR);
                    controller.changed();
                }
            }
        });

        defaultTileSizeTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (defaultTileSizeTextField.getForeground() != MODIFIED_VALUES_COLOR) {
                    defaultTileSizeTextField.setForeground(MODIFIED_VALUES_COLOR);
                    controller.changed();
                }
            }
        });


        cacheSizeTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (cacheSizeTextField.getForeground() != MODIFIED_VALUES_COLOR) {
                    cacheSizeTextField.setForeground(MODIFIED_VALUES_COLOR);
                    controller.changed();
                }
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     */
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        systemParametersPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        vmParametersTextField = new javax.swing.JTextField();
        userDirTextField = new javax.swing.JTextField();
        browseUserDirButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        sysResetButton = new javax.swing.JButton();
        sysComputeButton = new javax.swing.JButton();
        largeCacheInfoLabel = new javax.swing.JLabel();
        vmParametersInfoLabel = new javax.swing.JLabel();
        processingParametersPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        tileWidthLabel = new javax.swing.JLabel();
        cacheSizeLabel = new javax.swing.JLabel();
        nbThreadsLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        defaultTileSizeTextField = new javax.swing.JTextField();
        cacheSizeTextField = new javax.swing.JTextField();
        nbThreadsTextField = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        benchmarkTileSizeTextField = new javax.swing.JTextField();
        benchmarkCacheSizeTextField = new javax.swing.JTextField();
        benchmarkNbThreadsTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();

        procGraphJComboBox = new javax.swing.JComboBox(getBenchmarkOperators());
        jPanel3 = new javax.swing.JPanel();
        processingParamsComputeButton = new javax.swing.JButton();
        processingParamsResetButton = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 3000), new java.awt.Dimension(0, 32767));

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.PAGE_AXIS));

        systemParametersPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(PerformancePanel.class, "PerformancePanel.systemParametersPanel.border.title"))); 
        systemParametersPanel.setMinimumSize(new java.awt.Dimension(283, 115));
        systemParametersPanel.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(PerformancePanel.class, "PerformancePanel.jLabel2.text")); 
        jLabel2.setMaximumSize(new java.awt.Dimension(100, 14));
        jLabel2.setPreferredSize(new java.awt.Dimension(80, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 5);
        systemParametersPanel.add(jLabel2, gridBagConstraints);

        vmParametersTextField.setText(org.openide.util.NbBundle.getMessage(PerformancePanel.class, "PerformancePanel.vmParametersTextField.text")); 
        vmParametersTextField.setToolTipText(org.openide.util.NbBundle.getMessage(PerformancePanel.class, "PerformancePanel.vmParametersTextField.toolTipText")); 
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 10);
        systemParametersPanel.add(vmParametersTextField, gridBagConstraints);

        userDirTextField.setText(org.openide.util.NbBundle.getMessage(PerformancePanel.class, "PerformancePanel.userDirTextField.text")); 
        userDirTextField.setToolTipText(org.openide.util.NbBundle.getMessage(PerformancePanel.class, "PerformancePanel.userDirTextField.toolTipText")); 
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        systemParametersPanel.add(userDirTextField, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(browseUserDirButton, org.openide.util.NbBundle.getMessage(PerformancePanel.class, "PerformancePanel.browseUserDirButton.text")); 
        browseUserDirButton.addActionListener(evt -> browseUserDirButtonActionPerformed());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 2, 0, 10);
        systemParametersPanel.add(browseUserDirButton, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(PerformancePanel.class, "PerformancePanel.jLabel3.text")); 
        jLabel3.setMaximumSize(new java.awt.Dimension(200, 14));
        jLabel3.setMinimumSize(new java.awt.Dimension(100, 14));
        jLabel3.setPreferredSize(new java.awt.Dimension(80, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 2, 5);
        systemParametersPanel.add(jLabel3, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(sysResetButton, org.openide.util.NbBundle.getMessage(PerformancePanel.class, "PerformancePanel.sysResetButton.text")); 
        sysResetButton.addActionListener(evt -> sysResetButtonActionPerformed());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(10, 3, 0, 10);
        systemParametersPanel.add(sysResetButton, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(sysComputeButton, org.openide.util.NbBundle.getMessage(PerformancePanel.class, "PerformancePanel.sysComputeButton.text")); 
        sysComputeButton.addActionListener(evt -> sysComputeButtonActionPerformed());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 3);
        systemParametersPanel.add(sysComputeButton, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(largeCacheInfoLabel, org.openide.util.NbBundle.getMessage(PerformancePanel.class, "PerformancePanel.largeCacheInfoLabel.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        systemParametersPanel.add(largeCacheInfoLabel, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(vmParametersInfoLabel, org.openide.util.NbBundle.getMessage(PerformancePanel.class, "PerformancePanel.vmParametersInfoLabel.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        systemParametersPanel.add(vmParametersInfoLabel, gridBagConstraints);

        add(systemParametersPanel);

        processingParametersPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(PerformancePanel.class, "PerformancePanel.border.title"))); 
        processingParametersPanel.setMinimumSize(new java.awt.Dimension(450, 185));
        processingParametersPanel.setName(""); 
        processingParametersPanel.setPreferredSize(new java.awt.Dimension(400, 350));
        processingParametersPanel.setLayout(new java.awt.GridBagLayout());

        jPanel2.setLayout(new java.awt.GridLayout(3, 0, 0, 15));

        org.openide.awt.Mnemonics.setLocalizedText(tileWidthLabel, org.openide.util.NbBundle.getMessage(PerformancePanel.class, "PerformancePanel.tileWidthLabel.text")); 
        tileWidthLabel.setMaximumSize(new java.awt.Dimension(120, 14));
        tileWidthLabel.setPreferredSize(new java.awt.Dimension(100, 14));
        jPanel2.add(tileWidthLabel);

        org.openide.awt.Mnemonics.setLocalizedText(cacheSizeLabel, org.openide.util.NbBundle.getMessage(PerformancePanel.class, "PerformancePanel.cacheSizeLabel.text")); 
        cacheSizeLabel.setMaximumSize(new java.awt.Dimension(100, 14));
        cacheSizeLabel.setPreferredSize(new java.awt.Dimension(80, 14));
        jPanel2.add(cacheSizeLabel);

        org.openide.awt.Mnemonics.setLocalizedText(nbThreadsLabel, org.openide.util.NbBundle.getMessage(PerformancePanel.class, "PerformancePanel.nbThreadsLabel.text")); 
        nbThreadsLabel.setMaximumSize(new java.awt.Dimension(100, 14));
        jPanel2.add(nbThreadsLabel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        processingParametersPanel.add(jPanel2, gridBagConstraints);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(PerformancePanel.class, "PerformancePanel.jPanel1.border.title"))); 
        jPanel1.setMinimumSize(new java.awt.Dimension(100, 100));
        jPanel1.setLayout(new java.awt.GridLayout(3, 1, 0, 10));

        defaultTileSizeTextField.setText(org.openide.util.NbBundle.getMessage(PerformancePanel.class, "PerformancePanel.defaultTileSizeTextField.text")); 
        defaultTileSizeTextField.setMinimumSize(new java.awt.Dimension(100, 20));
        defaultTileSizeTextField.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel1.add(defaultTileSizeTextField);

        cacheSizeTextField.setText(org.openide.util.NbBundle.getMessage(PerformancePanel.class, "PerformancePanel.cacheSizeTextField.text")); 
        cacheSizeTextField.setMinimumSize(new java.awt.Dimension(100, 20));
        cacheSizeTextField.setName(""); 
        cacheSizeTextField.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel1.add(cacheSizeTextField);

        nbThreadsTextField.setText(org.openide.util.NbBundle.getMessage(PerformancePanel.class, "PerformancePanel.nbThreadsTextField.text")); 
        nbThreadsTextField.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel1.add(nbThreadsTextField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        processingParametersPanel.add(jPanel1, gridBagConstraints);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(PerformancePanel.class, "PerformancePanel.jPanel4.border.title"))); 
        jPanel4.setMinimumSize(new java.awt.Dimension(190, 107));
        jPanel4.setLayout(new java.awt.GridLayout(3, 1, 0, 10));


        PerformanceParameters actualParameters = confOptimizer.getActualPerformanceParameters();
        benchmarkTileSizeTextField.setText(Integer.toString(actualParameters.getDefaultTileSize()) + BENCHMARK_SEPARATOR);
        benchmarkTileSizeTextField.setPreferredSize(new java.awt.Dimension(150, 20));
        jPanel4.add(benchmarkTileSizeTextField);

        benchmarkCacheSizeTextField.setText(Integer.toString(actualParameters.getCacheSize()) + BENCHMARK_SEPARATOR);
        benchmarkCacheSizeTextField.setMinimumSize(new java.awt.Dimension(100, 20));
        benchmarkCacheSizeTextField.setName(""); 
        benchmarkCacheSizeTextField.setPreferredSize(new java.awt.Dimension(150, 20));
        jPanel4.add(benchmarkCacheSizeTextField);

        benchmarkNbThreadsTextField.setText(Integer.toString(actualParameters.getNbThreads()) + BENCHMARK_SEPARATOR);
        benchmarkNbThreadsTextField.setPreferredSize(new java.awt.Dimension(150, 20));
        jPanel4.add(benchmarkNbThreadsTextField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        processingParametersPanel.add(jPanel4, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(PerformancePanel.class, "PerformancePanel.jLabel1.text")); 
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        processingParametersPanel.add(jLabel1, gridBagConstraints);

        procGraphJComboBox.setMinimumSize(new java.awt.Dimension(180, 22));
        nbThreadsTextField.setMinimumSize(new java.awt.Dimension(100, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 10);
        processingParametersPanel.add(procGraphJComboBox, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(processingParamsComputeButton, org.openide.util.NbBundle.getMessage(PerformancePanel.class, "PerformancePanel.text")); 
        processingParamsComputeButton.setName(""); 
        processingParamsComputeButton.addActionListener(this::processingParamsComputeButtonActionPerformed);
        jPanel3.add(processingParamsComputeButton);

        org.openide.awt.Mnemonics.setLocalizedText(processingParamsResetButton, org.openide.util.NbBundle.getMessage(PerformancePanel.class, "PerformancePanel.processingParamsResetButton.text")); 
        processingParamsResetButton.addActionListener(this::processingParamsResetButtonActionPerformed);
        jPanel3.add(processingParamsResetButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        processingParametersPanel.add(jPanel3, gridBagConstraints);

        add(processingParametersPanel);
        add(filler1);
    }

    private Object[] getBenchmarkOperators() {
        GPF gpf = GPF.getDefaultInstance();
        if (gpf.getOperatorSpiRegistry().getOperatorSpis().isEmpty()) {
            gpf.getOperatorSpiRegistry().loadOperatorSpis();
        }
        Set<OperatorSpi> allOperators = gpf.getOperatorSpiRegistry().getOperatorSpis();

        TreeSet<String> externalOperatorsAliases = new TreeSet<>();
        for(OperatorSpi operatorSpi : allOperators) {
            if(!operatorSpi.getOperatorDescriptor().isInternal()) {
                externalOperatorsAliases.add(operatorSpi.getOperatorAlias());
            }
        }

        return externalOperatorsAliases.toArray();
    }
                                                  

    private void sysResetButtonActionPerformed() {
        setSystemPerformanceParametersToActualValues();
    }

    private void sysComputeButtonActionPerformed() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        PerformanceParameters optimizedParameters = confOptimizer.computeOptimisedSystemParameters();

        if(!vmParametersTextField.getText().equals(optimizedParameters.getVMParameters())) {
            vmParametersTextField.setText(optimizedParameters.getVMParameters());
            vmParametersTextField.setForeground(MODIFIED_VALUES_COLOR);
            vmParametersTextField.setCaretPosition(0);
        }

        if(!userDirTextField.getText().equals(optimizedParameters.getUserDir().toString())) {
            userDirTextField.setText(optimizedParameters.getUserDir().toString());
            userDirTextField.setForeground(MODIFIED_VALUES_COLOR);
        }

        setCursor(Cursor.getDefaultCursor());

        controller.changed();
    }

    private void browseUserDirButtonActionPerformed() {
        JFileChooser fileChooser = new JFileChooser(userDirTextField.getText());
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedDir = fileChooser.getSelectedFile();
            userDirTextField.setText(selectedDir.getAbsolutePath());
            userDirTextField.setForeground(MODIFIED_VALUES_COLOR);
            controller.changed();
        }
    }

    private void processingParamsComputeButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if(validCompute()){
            //Create performance parameters benchmark lists
            java.util.List<Integer> tileSizesList = new ArrayList<>();
            java.util.List<Integer> cacheSizesList = new ArrayList<>();
            java.util.List<Integer> nbThreadsList = new ArrayList<>();
            for(String tileSize : StringUtils.split(benchmarkTileSizeTextField.getText(), ';')){
                tileSizesList.add(Integer.parseInt(tileSize));
            }
            for(String cacheSize : StringUtils.split(benchmarkCacheSizeTextField.getText(), ';')){
                cacheSizesList.add(Integer.parseInt(cacheSize));
            }
            for(String nbThread : StringUtils.split(benchmarkNbThreadsTextField.getText(), ';')){
                nbThreadsList.add(Integer.parseInt(nbThread));
            }
            Benchmark benchmarkModel = new Benchmark(tileSizesList, cacheSizesList, nbThreadsList);
            String opName = procGraphJComboBox.getSelectedItem().toString();
            SnapApp.SnapContext appContext = new SnapApp.SnapContext();
            //launch Benchmark dialog
            BenchmarkDialog productDialog = new BenchmarkDialog(this, opName, benchmarkModel, appContext);
            productDialog.show();
        }
    }

    private void processingParamsResetButtonActionPerformed(java.awt.event.ActionEvent evt) {
        setProcessingPerformanceParametersToActualValues();
    }



    void load() {
        setSystemPerformanceParametersToActualValues();
        setProcessingPerformanceParametersToActualValues();
    }

    void updatePerformanceParameters(BenchmarkSingleCalculus benchmarkSingleCalcul){
        defaultTileSizeTextField.setText(Integer.toString(benchmarkSingleCalcul.getTileSize()));
        defaultTileSizeTextField.setForeground(MODIFIED_VALUES_COLOR);

        cacheSizeTextField.setText(Integer.toString(benchmarkSingleCalcul.getCacheSize()));
        cacheSizeTextField.setForeground(MODIFIED_VALUES_COLOR);

        nbThreadsTextField.setText(Integer.toString(benchmarkSingleCalcul.getNbThreads()));
        nbThreadsTextField.setForeground(MODIFIED_VALUES_COLOR);

        this.controller.changed();
    }

    void store() {
        if(valid()) {
            PerformanceParameters updatedPerformanceParams = getPerformanceParameters();
            confOptimizer.updateCustomisedParameters(updatedPerformanceParams);
            try {
                confOptimizer.saveCustomisedParameters();
            } catch (IOException|BackingStoreException e) {
                SystemUtils.LOG.severe("Could not save performance parameters: " + e.getMessage());
                setSystemPerformanceParametersToActualValues();
            }
        }
    }


    private PerformanceParameters getPerformanceParameters() {
        PerformanceParameters parameters = new PerformanceParameters();
        parameters.setVMParameters(vmParametersTextField.getText());
        Path userDirPath = getUserDirPathFromString(userDirTextField.getText());
        parameters.setUserDir(userDirPath);
        parameters.setDefaultTileSize(Integer.parseInt(defaultTileSizeTextField.getText()));
        parameters.setCacheSize(Integer.parseInt(cacheSizeTextField.getText()));
        parameters.setNbThreads(Integer.parseInt(nbThreadsTextField.getText()));
        return parameters;
    }

    boolean valid() {
        boolean isValid = true;

        File userDir = new File(userDirTextField.getText());
        if(!userDir.exists() || !userDir.isDirectory()) {
            userDirTextField.setForeground(ERROR_VALUES_COLOR);
            isValid = false;
        }

        String defaultTileSize = this.defaultTileSizeTextField.getText();
        try{
            Integer.parseInt(defaultTileSize);
        } catch (NumberFormatException ex) {
            this.defaultTileSizeTextField.setForeground(ERROR_VALUES_COLOR);
            isValid = false;
        }
        
        String readerCacheSize = this.cacheSizeTextField.getText();
        try{
            Integer.parseInt(readerCacheSize);
        } catch (NumberFormatException ex) {
            this.cacheSizeTextField.setForeground(ERROR_VALUES_COLOR);
            isValid = false;
        }


        String nbThreadsString = nbThreadsTextField.getText();
        try{
            int nbThreads = Integer.parseUnsignedInt(nbThreadsString);
            int nbCores = JavaSystemInfos.getInstance().getNbCPUs();

            if(nbThreads > nbCores) {
                nbThreadsTextField.setForeground(ERROR_VALUES_COLOR);
                isValid = false;
            }
        } catch (NumberFormatException ex) {
            nbThreadsTextField.setForeground(ERROR_VALUES_COLOR);
            isValid = false;
        }

        return isValid;
    }

    private boolean validCompute() {
        boolean isValid = true;
        Pattern patternBenchmarkValues = Pattern.compile("([0-9]+[\\;]*)+");
        if (!patternBenchmarkValues.matcher(benchmarkTileSizeTextField.getText()).matches()) {
            benchmarkTileSizeTextField.setForeground(ERROR_VALUES_COLOR);
            isValid = false;
        } else {
            benchmarkTileSizeTextField.setForeground(CURRENT_VALUES_COLOR);
        }
        if (!patternBenchmarkValues.matcher(benchmarkCacheSizeTextField.getText()).matches()) {
            benchmarkCacheSizeTextField.setForeground(ERROR_VALUES_COLOR);
            isValid = false;
        } else {
            benchmarkCacheSizeTextField.setForeground(CURRENT_VALUES_COLOR);
        }
        if (!patternBenchmarkValues.matcher(benchmarkNbThreadsTextField.getText()).matches() || !validBenchmarkNbThreads()) {
            benchmarkNbThreadsTextField.setForeground(ERROR_VALUES_COLOR);
            isValid = false;
        } else {
            benchmarkNbThreadsTextField.setForeground(CURRENT_VALUES_COLOR);
        }
        return isValid;
    }

    private boolean validBenchmarkNbThreads(){
        boolean valid = true;
        for(String nbThread : StringUtils.split(benchmarkNbThreadsTextField.getText(), ';')){
            try {
                if(Integer.parseInt(nbThread) > JavaSystemInfos.getInstance().getNbCPUs()){
                    valid = false;
                    break;
                }
            } catch (NumberFormatException e){
                valid = false;
                break;
            }
        }
        return valid;
    }

    private void setSystemPerformanceParametersToActualValues() {
        PerformanceParameters actualPerformanceParameters = confOptimizer.getActualPerformanceParameters();

        vmParametersTextField.setText(actualPerformanceParameters.getVMParameters());
        vmParametersTextField.setForeground(CURRENT_VALUES_COLOR);
        vmParametersTextField.setCaretPosition(0);

        userDirTextField.setText(actualPerformanceParameters.getUserDir().toString());
        userDirTextField.setForeground(CURRENT_VALUES_COLOR);
    }

    private void setProcessingPerformanceParametersToActualValues() {
        PerformanceParameters actualPerformanceParameters = confOptimizer.getActualPerformanceParameters();

        defaultTileSizeTextField.setText(Integer.toString(actualPerformanceParameters.getDefaultTileSize()));
        defaultTileSizeTextField.setForeground(CURRENT_VALUES_COLOR);

        cacheSizeTextField.setText(Integer.toString(actualPerformanceParameters.getCacheSize()));
        cacheSizeTextField.setForeground(CURRENT_VALUES_COLOR);

        nbThreadsTextField.setText(Integer.toString(actualPerformanceParameters.getNbThreads()));
        nbThreadsTextField.setForeground(CURRENT_VALUES_COLOR);
    }

    private javax.swing.JTextField benchmarkCacheSizeTextField;
    private javax.swing.JTextField benchmarkNbThreadsTextField;
    private javax.swing.JTextField benchmarkTileSizeTextField;
    private javax.swing.JButton browseUserDirButton;
    private javax.swing.JLabel cacheSizeLabel;
    private javax.swing.JTextField cacheSizeTextField;
    private javax.swing.JTextField defaultTileSizeTextField;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JLabel largeCacheInfoLabel;
    private javax.swing.JLabel nbThreadsLabel;
    private javax.swing.JTextField nbThreadsTextField;
    private javax.swing.JComboBox procGraphJComboBox;
    private javax.swing.JPanel processingParametersPanel;
    private javax.swing.JButton processingParamsComputeButton;
    private javax.swing.JButton processingParamsResetButton;
    private javax.swing.JButton sysComputeButton;
    private javax.swing.JButton sysResetButton;
    private javax.swing.JPanel systemParametersPanel;
    private javax.swing.JLabel tileWidthLabel;
    private javax.swing.JTextField userDirTextField;
    private javax.swing.JLabel vmParametersInfoLabel;
    private javax.swing.JTextField vmParametersTextField;
}