/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lv.autentica.test.view;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;

import java.util.stream.IntStream;

import javax.swing.JOptionPane;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 *
 * @author aks513
 */
public class DBTableModel implements TableModel {
    private final ArrayList<ArrayList<Object>> rows = new ArrayList<>();
    private final ArrayList<String> cols = new ArrayList<>();
    private ResultSet dbResultSet;

    public DBTableModel(ResultSet dbResultSet) {
        this.dbResultSet = dbResultSet;

        try {
            IntStream.rangeClosed(1, this.dbResultSet.getMetaData().getColumnCount()).forEach(index -> {
                try {
                    cols.add(this.dbResultSet.getMetaData().getColumnName(index));
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, ex.getCause(), "Cannot get table column names", JOptionPane.ERROR_MESSAGE);
                }
            });
            
            while (this.dbResultSet.next()) {
                final ArrayList<Object> row = new ArrayList<>();
                IntStream.rangeClosed(1, this.dbResultSet.getMetaData().getColumnCount()).forEach(index -> {
                    try {
                        row.add(this.dbResultSet.getObject(index));
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, ex.getCause(), "Cannot get table data", JOptionPane.ERROR_MESSAGE);
                    }
                });
                rows.add(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getCause(), ex.getLocalizedMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return cols.size();
    }

    @Override
    public String getColumnName(int columnIndex) {
        return cols.get(columnIndex);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return Object.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return Boolean.FALSE;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return rows.get(rowIndex).get(columnIndex);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

    }

    @Override
    public void addTableModelListener(TableModelListener l) {

    }

    @Override
    public void removeTableModelListener(TableModelListener l) {

    }

}
