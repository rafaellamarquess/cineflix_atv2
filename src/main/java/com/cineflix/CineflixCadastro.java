package com.cineflix;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CineflixCadastro extends JFrame {

    private JTextField nomeField;
    private JTextField dataLancamentoField;
    private JTextField categoriaField;
    private JButton cadastrarButton;
    private JButton limparButton;
    private JTable filmesTable;
    private DefaultTableModel tableModel;

    public CineflixCadastro() {
        setTitle("Cineflix - Cadastro de Filmes");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());


        JPanel cadastroPanel = new JPanel(new GridLayout(5, 2));
        JLabel nomeLabel = new JLabel("Nome:");
        nomeField = new JTextField();
        JLabel dataLancamentoLabel = new JLabel("Data de Lançamento:");
        dataLancamentoField = new JTextField();
        JLabel categoriaLabel = new JLabel("Categoria:");
        categoriaField = new JTextField();

        cadastrarButton = new JButton("Cadastrar");
        limparButton = new JButton("Limpar");

        cadastroPanel.add(nomeLabel);
        cadastroPanel.add(nomeField);
        cadastroPanel.add(dataLancamentoLabel);
        cadastroPanel.add(dataLancamentoField);
        cadastroPanel.add(categoriaLabel);
        cadastroPanel.add(categoriaField);
        cadastroPanel.add(cadastrarButton);
        cadastroPanel.add(limparButton);

        add(cadastroPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Nome", "Data de Lançamento", "Categoria"}, 0);
        filmesTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(filmesTable);

        add(tableScrollPane, BorderLayout.CENTER);

        carregarDadosTabela();

        cadastrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cadastrarFilme();
            }
        });

        limparButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limparCampos();
            }
        });
    }
    private void limparCampos() {
        nomeField.setText("");
        dataLancamentoField.setText("");
        categoriaField.setText("");
    }

    private void cadastrarFilme() {
        String nome = nomeField.getText();
        String dataLancamentoStr = dataLancamentoField.getText();
        String categoria = categoriaField.getText();

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date parsedDate = dateFormat.parse(dataLancamentoStr);
            long millis = parsedDate.getTime();
            java.sql.Date dataLancamento = new java.sql.Date(millis);

            try (Connection connection = DatabaseConnection.getConnection()) {
                String sql = "INSERT INTO filmes (nome, datalancamento, categoria) VALUES (?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, nome);
                preparedStatement.setDate(2, dataLancamento);
                preparedStatement.setString(3, categoria);
                int rowsInserted = preparedStatement.executeUpdate();

                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(this, "Filme cadastrado com sucesso!");
                    carregarDadosTabela();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao cadastrar filme: " + ex.getMessage());
            }
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao converter data: " + ex.getMessage());
        }
    }

    private void carregarDadosTabela() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT nome, datalancamento, categoria FROM filmes";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            tableModel.setRowCount(0);

            while (resultSet.next()) {
                String nome = resultSet.getString("nome");
                Date dataLancamento = resultSet.getDate("datalancamento");
                String categoria = resultSet.getString("categoria");

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String dataLancamentoStr = dateFormat.format(dataLancamento);

                tableModel.addRow(new Object[]{nome, dataLancamentoStr, categoria});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados da tabela: " + ex.getMessage());
        }
    }

}

