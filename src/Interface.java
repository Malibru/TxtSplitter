import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import split.TxtSplitter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import javax.swing.SwingWorker;

import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

public class Interface {
    public static void main(String[] args) {
        JFrame f = new JFrame("TxtSplitter");
        //carrega o ícone a partir dos recursos do classpath para funcionar no JAR/EXE
        java.net.URL iconUrl = Interface.class.getResource("/Logo_Dicoco.png");
        Image baseIcon;
        if (iconUrl != null) {
            baseIcon = new ImageIcon(iconUrl).getImage();
        } else {
            //fallback para execuções locais se o recurso não estiver no classpath
            baseIcon = new ImageIcon("src/Logo_Dicoco.png").getImage();
        }
        f.setIconImage(baseIcon);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//fecha o programa ao clicar no X
        f.setSize(700, 500);//tamanho da janela
        f.setLocationRelativeTo(null);//centraliza a janela
        java.util.List<Image> icons = new java.util.ArrayList<>();
        int[] sizes = new int[]{16, 32, 48, 64, 128, 256};
        for (int s : sizes) {
            icons.add(baseIcon.getScaledInstance(s, s, Image.SCALE_SMOOTH));
        }
        f.setIconImages(icons);
        
        
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
        btnSplit.setEnabled(false);
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
                    btnSplit.setEnabled(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(f, "Erro ao ler o arquivo:\n" + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnSplit.addActionListener(e -> {
            String caminho = txtCaminho.getText().trim();
            if (caminho.isEmpty()){
                JOptionPane.showMessageDialog(f, "Selecione o arquivo .txt primeiro");
                return;
            }
            int partes = (int) spnBloco.getValue();
            if (partes < 1){
                JOptionPane.showMessageDialog(f, "Informe a quantidade de partes (>= 1)");
                return;
            }

            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Salvar em...");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);

            int res = chooser.showSaveDialog(f);
            if(res != JFileChooser.APPROVE_OPTION){
                return;
            }

            File pastaSaida = chooser.getSelectedFile();
            btnSplit.setEnabled(false);
            btnSplit.setText("Dividindo...");
            new SwingWorker<Void, Void>(){
                @Override
                protected Void doInBackground() throws Exception{
                    Path arquivo = Paths.get(caminho);
                    List<Path> gerados = TxtSplitter.splitIntoParts(arquivo, pastaSaida.toPath(), partes);
                    return null;
                }
                @Override
                protected void done() {
                    btnSplit.setEnabled(true);
                    btnSplit.setText("Dividir");
                    try {
                        get();
                        JOptionPane.showMessageDialog(f, "Divisão concluída com sucesso", "OK", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(f, "Erro ao dividir: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        });

        root.add(top, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);



        f.setContentPane(root);
        f.setVisible(true);
    }
}

