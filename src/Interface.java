import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

public class Interface {
    public static void main(String[] args) {
        JFrame f = new JFrame("TxtSplitter");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//fecha o programa ao clicar no X
        f.setSize(700, 500);//tamanho da janela
        f.setLocationRelativeTo(null);//centraliza a janela
        
        
        JPanel root = new JPanel(new BorderLayout(10,10));//painel usado para organizar os componentes
        root.setBorder(BorderFactory.createEmptyBorder(
            10,10,10,10));

        JPanel top = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);//margem do componente
        gbc.anchor = GridBagConstraints.WEST;//alinhamento do componente
        gbc.fill = GridBagConstraints.HORIZONTAL;//preenche o componente horizontalmente
        
        JButton btnEscolher = new JButton("Selecionar Arquivo");
        JTextField txtCaminho = new JTextField();
        txtCaminho.setEditable(false);

        JLabel lblBloco = new JLabel("Tamanho do bloco:");
        JSpinner spnBloco = new JSpinner(new SpinnerNumberModel(
            1,1, Integer.MAX_VALUE,1));

        JButton btnSplit = new JButton("Dividir");
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        top.add(btnSplit, gbc);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        top.add(btnEscolher, gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        top.add(txtCaminho, gbc);


        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        top.add(lblBloco, gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        top.add(spnBloco, gbc);

        JTextArea preview = new JTextArea();
        preview.setEditable(false);
        preview.setLineWrap(true);
        preview.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(
            preview,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        btnEscolher.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter(
                "Arquivos de texto (*.txt)", "txt"));
            int result = chooser.showOpenDialog(f);
            if(result == JFileChooser.APPROVE_OPTION){
                File file = chooser.getSelectedFile();
                txtCaminho.setText(file.getAbsolutePath());
            try {
                    byte[] data = Files.readAllBytes(file.toPath());
                    String content = new String(data, StandardCharsets.UTF_8);
                    preview.setText(content);
                    preview.setCaretPosition(0);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(f, "Erro ao ler o arquivo:\n" + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        root.add(top, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);



        f.setContentPane(root);
        f.setVisible(true);
    }
}

