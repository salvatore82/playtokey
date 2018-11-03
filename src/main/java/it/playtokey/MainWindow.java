package it.playtokey;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

public class MainWindow {

    private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
    private JFrame frmPlaykey;
    private JTextField path;
    private JTextField pathToSave;
    private JPanel mainPanel;
    private File[] files;
    private JList colorJList; // list to hold the color names
    private JList copyJList; // list to copy the color names into
    private FileReader fr;
    private JFormattedTextField formattedTextField;
    private JScrollPane scrollPane_1;
    private JScrollPane scrollPane;
    private JProgressBar progressBar;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MainWindow window = new MainWindow();
                    window.frmPlaykey.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public MainWindow() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        try {
            UIManager.setLookAndFeel(PREFERRED_LOOK_AND_FEEL);
        } catch (Exception e) {
        }
        frmPlaykey = new JFrame();
        frmPlaykey.setTitle("Play2Key");
        frmPlaykey.setBounds(100, 100, 487, 500);
        frmPlaykey.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmPlaykey.getContentPane().setLayout(new BorderLayout(0, 0));

        mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        frmPlaykey.getContentPane().add(mainPanel, BorderLayout.CENTER);
        mainPanel.setLayout(null);

        JLabel lblPlaylists = new JLabel("Playlists");
        lblPlaylists.setBounds(10, 45, 57, 14);
        mainPanel.add(lblPlaylists);

        path = new JTextField();
        path.setBounds(79, 42, 290, 28);
        path.setColumns(30);
        path.setEditable(Boolean.FALSE);
        mainPanel.add(path);

        JButton btnSfoglia = new JButton("Sfoglia");
        btnSfoglia.setBounds(381, 41, 80, 28);
        btnSfoglia.addActionListener(new SfogliaListener());
        mainPanel.add(btnSfoglia);
        
        colorJList = new JList();
        colorJList.setVisibleRowCount(6);

        copyJList = new JList();
        copyJList.setVisibleRowCount(6);
        
        scrollPane = new JScrollPane(colorJList);
        scrollPane.setBounds(10, 87, 166, 131);
        mainPanel.add(scrollPane);
        
        scrollPane_1 = new JScrollPane(copyJList);
        scrollPane_1.setBounds(296, 84, 166, 131);
        mainPanel.add(scrollPane_1);

        JLabel lblPathDiPutput = new JLabel("Output");
        lblPathDiPutput.setBounds(10, 242, 68, 14);
        mainPanel.add(lblPathDiPutput);

        pathToSave = new JTextField();
        pathToSave.setBounds(79, 238, 290, 28);
        pathToSave.setColumns(30);
        pathToSave.setEditable(Boolean.FALSE);
        mainPanel.add(pathToSave);

        JButton btnCrea = new JButton("Crea!");
        btnCrea.setBounds(142, 285, 90, 28);
        btnCrea.addActionListener(new CreaListener());
        mainPanel.add(btnCrea);

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setBounds(115, 333, 255, 20);
        mainPanel.add(progressBar);

        JButton btnSposta = new JButton("Sposta");
        btnSposta.setBounds(197, 130, 80, 28);
        btnSposta.addActionListener(new SelectPlaylistListener());
        mainPanel.add(btnSposta);

        formattedTextField = new JFormattedTextField();
        formattedTextField.setEditable(false);
        formattedTextField.setBounds(52, 377, 366, 64);
        mainPanel.add(formattedTextField);

        JButton btnNewButton = new JButton("Sfoglia");
        btnNewButton.setBounds(381, 238, 80, 28);
        btnNewButton.addActionListener(new FolderOutListener());
        mainPanel.add(btnNewButton);
        
        JButton btnAnnulla = new JButton("Annulla");
        btnAnnulla.setBounds(254, 285, 90, 28);
        mainPanel.add(btnAnnulla);

    }

    class SfogliaListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.setMultiSelectionEnabled(true);
            chooser.addChoosableFileFilter(new PlaylistFilter());
            if (chooser.showOpenDialog(mainPanel) == JFileChooser.APPROVE_OPTION) {
                formattedTextField.setText("");
                path.setText(chooser.getSelectedFile().getAbsolutePath());

                File[] playlists = chooser.getSelectedFiles();
                files = new File[playlists.length];
                for (int i = 0; i < playlists.length; i++) {
                    File file = playlists[i];
                    files[i] = file;
                }

                if (files != null) {
                    colorJList.setListData(files);
                    colorJList.setVisibleRowCount(6);
                    colorJList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                    copyJList.setVisibleRowCount(6);
                }
            } else {
                formattedTextField.setText("Nessuna playlist selezionata");
            }
            SwingUtilities.updateComponentTreeUI(mainPanel);
        }
    }

    class PlaylistFilter extends FileFilter {
        public String getDescription() {
            return "m3u Playlists (*.m3u)";
        }

        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            } else {
                return f.getName().toLowerCase().endsWith(".m3u");
            }
        }
    }

    class SelectPlaylistListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            if(colorJList!=null && colorJList.getSelectedValues()!=null && colorJList.getSelectedValues().length > 0) {
                formattedTextField.setText("");
                copyJList.setListData(colorJList.getSelectedValues());
                copyJList.setVisibleRowCount(6);
            } else {
                formattedTextField.setText("Nessuna playlist selezionata");
            }
        }
    }

    class FolderOutListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File("."));
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);
            if (chooser.showOpenDialog(mainPanel) == JFileChooser.APPROVE_OPTION) {
                formattedTextField.setText("");
                pathToSave.setText(chooser.getSelectedFile().getAbsolutePath());
                SwingUtilities.updateComponentTreeUI(mainPanel);
            } else {
                formattedTextField.setText("Nessuna playlist selezionata");
            }
        }
    }

    class CreaListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if(copyJList!=null && copyJList.getModel().getSize() > 0) {
                if(pathToSave.getText()!=null && !"".equals(pathToSave.getText().trim())) {
                    formattedTextField.setText("");
                    Creator task = new Creator();
                    task.execute();
                } else {
                    formattedTextField.setText("Seleziona un percorso in cui salvare i files.");
                }
            } else {
                formattedTextField.setText("Nessuna playlist da convertire!");
            }
        }
    }

    class Creator extends SwingWorker {

        @Override
        public Void doInBackground() throws InterruptedException {
            Object[] playlistsSelezionate = (Object[]) colorJList.getSelectedValues();
            int barIncrement = 100 / playlistsSelezionate.length;
            int progress = 0;
            for (int i = 0; i < playlistsSelezionate.length; i++) {

                File playlist = (File) playlistsSelezionate[i];
                File folderOut = new File(pathToSave.getText() + File.separator + playlist.getName().split("\\.")[0] + "\\");
                if (!folderOut.exists()) {
                    folderOut.mkdir();
                }

                try {
                    fr = new FileReader(playlist);
                    BufferedReader br = new BufferedReader(fr);
                    String s;
                    while ((s = br.readLine()) != null) {
                        if (s != null && !"".equals(s.trim()) && !s.contains("#")) {
                            File songIn = new File(playlist.getParent() + File.separator + s);
                            File songOut = new File(folderOut + File.separator + songIn.getName());

                            if (!songOut.exists()) {
                                songOut.createNewFile();
                            }

                            FileChannel source = null;
                            FileChannel destination = null;
                            try {
                                source = new FileInputStream(songIn).getChannel();
                                destination = new FileOutputStream(songOut).getChannel();

                                long count = 0;
                                long size = source.size();
                                while ((count += destination.transferFrom(source, count, size - count)) < size)
                                    ;
                            } catch (Exception e) {
                                System.out.println("Fallita la copia di " + songIn.getAbsolutePath());
                                e.printStackTrace();
                            } finally {
                                if (source != null) {
                                    source.close();
                                }
                                if (destination != null) {
                                    destination.close();
                                }
                            }
                        }
                    }
                    fr.close();
                } catch (FileNotFoundException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e2) {
                    // TODO Auto-generated catch block
                    e2.printStackTrace();
                }

                progress = (i != playlistsSelezionate.length - 1) ? (barIncrement * (i + 1)) : 100;

                // Call the process method to update the GUI
                publish(progress);
            }

            return null;
        }

        protected void process(List chunks) {
            for (Object chunk : chunks) {
                progressBar.setValue((Integer) chunk);
                progressBar.setStringPainted(true);
            }
        }

        // when the 'task' has finished re-enable the go button
        @Override
        public void done() {
            formattedTextField.setText("Fine!");
        }
    }
}
