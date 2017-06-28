/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lv.autentica.test.listener;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.sql.SQLException;
import java.sql.Statement;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import lv.autentica.test.constant.ButtonValuesEnum;
import lv.autentica.test.constant.OtherConstants;
import lv.autentica.test.view.DBTableModel;
import lv.autentica.testservice.DatabaseService;

/**
 *
 * @author aks513
 */
public class ClickedButtonListener implements ActionListener {
    private final DatabaseService dbService = new DatabaseService();
    private final JComboBox<?> carCbn = new JComboBox<>(dbService.getAvailableCarsToRentFormCombobox().toArray());
    private final JComboBox<?> personCbn = new JComboBox<>(dbService.getAvailablePersonsToRentFormCombobox().toArray());
    private final SimpleDateFormat dateFormat = new SimpleDateFormat(OtherConstants.DATE_FORMAT);
    private final JFormattedTextField dateFromTxt = new JFormattedTextField(dateFormat);
    private final JFormattedTextField dateto = new JFormattedTextField(dateFormat);
    
    private JPanel showRentCarForm() {   
        final JPanel panel = new JPanel(new GridLayout(0, 4));
        panel.removeAll();
        panel.add(new JLabel("Car number"));
        panel.add(carCbn);
        panel.add(new JLabel("Person"));
        panel.add(personCbn);
        panel.add(new JLabel(String.format("Date from (%s)", OtherConstants.DATE_FORMAT)));
        panel.add(dateFromTxt);
        panel.add(new JLabel(String.format("Date to (%s)", OtherConstants.DATE_FORMAT)));
        panel.add(dateto);
        
        return panel;
    }
    
    private JPanel showRentCarPanel() {
        final JPanel resultPanel = new JPanel(new BorderLayout());
        
        try (final Statement dbStatement = dbService.getDatabaseConnection().createStatement()) {
            dbStatement.executeUpdate(OtherConstants.INVALIDATE_RENTED_CARS);
            
            final JTable table = new JTable(new DBTableModel(dbStatement.executeQuery(String.format("%s", OtherConstants.RENT_CARS))));
            table.getTableHeader().setReorderingAllowed(Boolean.FALSE);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            resultPanel.add(new JScrollPane(table), BorderLayout.CENTER);
            resultPanel.add(showRentCarForm(), BorderLayout.SOUTH);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getCause(), "Incorrect statement", JOptionPane.ERROR_MESSAGE);
        }
        
        return resultPanel;
    }
    
    private String dateConversation(final String stringDate) throws ParseException {        
        final Date currentDate = new SimpleDateFormat(OtherConstants.DATE_FORMAT).parse(stringDate);
        final String sqlDate = new SimpleDateFormat(OtherConstants.DB_DATE_FORMAT).format(currentDate);
        return sqlDate;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        final JButton clickedButton = (JButton) e.getSource();
        
        final ButtonValuesEnum btn = Stream.of(ButtonValuesEnum.values()).filter(
            text -> text.getValue().equalsIgnoreCase(clickedButton.getText())
        ).findAny().get();
        
        switch(btn) {
            case RENT_CAR:
                final int optionPaneValue = JOptionPane.showOptionDialog(
                    null, showRentCarPanel(), "Rent car form",
                    JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, new Object[]{"Rent car", "Close window"}, null
                );
              
                if(optionPaneValue == JOptionPane.YES_OPTION) {                    
                    try {                    
                        final String carNumber = String.format("%s", carCbn.getSelectedItem());
                        
                        dbService.insertRentCar(
                            carNumber,dateConversation(dateFromTxt.getText()),dateConversation(dateto.getText())
                        );
                        
                        dbService.updateCarRentByName(
                            dbService.getEmployeeIdByName(String.format("%s", personCbn.getSelectedItem())),carNumber
                        );

                    } catch (ParseException pe) {
                        JOptionPane.showMessageDialog(null, "Illegal date value", "Incorrect statement", JOptionPane.ERROR_MESSAGE);
                    }                    
                }                
                break;
        }
    }    
}