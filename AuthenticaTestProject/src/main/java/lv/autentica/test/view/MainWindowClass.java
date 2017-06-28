/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lv.autentica.test.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import java.sql.SQLException;
import java.sql.Statement;

import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import lv.autentica.test.constant.ButtonValuesEnum;
import lv.autentica.test.constant.OtherConstants;
import lv.autentica.test.constant.PanelValuesEnum;

import lv.autentica.testservice.DatabaseService;

import lv.autentica.test.listener.ClickedButtonListener;
import lv.autentica.test.listener.MainWindowListener;

/**
 *
 * @author aks513
 */
public class MainWindowClass extends JFrame {
    private static final long serialVersionUID = 1L;    
    private final DatabaseService dbService = new DatabaseService();
    
    private static final JTextField DB_DRIVER_TXT =  new JTextField();
    private static final JTextField CONNECTION_STRING_TXT =  new JTextField();
    private static final JTextField LOGIN_TXT =  new JTextField();
    private static final JPasswordField PASSWORD_TXT =  new JPasswordField();
    
    private JButton actionButton(final String value) {
        final JButton button = new JButton(value);
        button.addActionListener(new ClickedButtonListener());
        return button;
    }

    private JPanel contentPanel(final String title) {
        final JPanel panel = new JPanel(new BorderLayout());
        final JPanel subPanel = new JPanel(new FlowLayout());
        final PanelValuesEnum titleValue = Stream.of(PanelValuesEnum.values()).filter(
            value -> value.getValue().equalsIgnoreCase(title)
        ).findFirst().get();
                
        try (final Statement dbStatement = dbService.getDatabaseConnection().createStatement()) {
            String queryTable;
            
            switch(titleValue) {
                case CARS:
                    queryTable = OtherConstants.GET_ALL_CARS;
                    subPanel.add(actionButton(ButtonValuesEnum.RENT_CAR.getValue()));
                    break;
                default:
                    queryTable = OtherConstants.GET_ALL_EMPS;
            }
            
            panel.setBorder(BorderFactory.createTitledBorder(title));
            final JTable table = new JTable(new DBTableModel(dbStatement.executeQuery(String.format("%s", queryTable))));
            table.getTableHeader().setReorderingAllowed(Boolean.FALSE);
            table.getSelectionModel().clearSelection();
            panel.add(new JScrollPane(table), BorderLayout.CENTER);
            panel.add(subPanel, BorderLayout.SOUTH);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getCause(), "Incorrect statement", JOptionPane.ERROR_MESSAGE);
        }

        return panel;
    }
    
    private Timer dailyEventTimer() {
        return new Timer(10000, event -> {
            dbService.invalidateRentedCars();
        });
    }

    public void initFrame() {
        final Timer timer = dailyEventTimer();
        timer.start();        
        
        addWindowListener(new MainWindowListener(timer));
        setLayout(new GridLayout(0, 2));        
        setResizable(Boolean.FALSE);
        setLocationByPlatform(Boolean.TRUE);
        Stream.of(PanelValuesEnum.values()).map(value -> value.getValue()).map(item -> contentPanel(item)).forEach(component -> add(component));
        pack();
        
        setVisible(Boolean.TRUE);
    }
    
    private static JPanel initianalPanel() {
        final JPanel panel = new JPanel(new GridLayout(0, 1));
        
        panel.removeAll();        
        panel.add(new JLabel("<html><b>Before run the application:</b>"
                + "<ul><li>Please startup your database!</li>"
                + "<li>Execute attached sqls! (<b>sql.txt</b> in <b>script</b> folder)</li>"
                + "<li>Set JDBC Settings!</li>"
                + "<li>Press OK when it is done!</li>"
            + "</ul></html>"
        ));
        
        final JPanel subPanel = new JPanel(new GridLayout(0, 2));
        subPanel.add(new JLabel("Database driver name:"));
        subPanel.add(DB_DRIVER_TXT);
        subPanel.add(new JLabel("Database connection string:"));
        subPanel.add(CONNECTION_STRING_TXT);
        subPanel.add(new JLabel("Database login:"));
        subPanel.add(LOGIN_TXT);
        subPanel.add(new JLabel("Database password:"));
        subPanel.add(PASSWORD_TXT);
        panel.add(subPanel);
        
        return panel;
    }

    public static void main(String[] args) throws UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(new NimbusLookAndFeel());
        final int clickedValue = JOptionPane.showConfirmDialog(null, initianalPanel(), "Greeting panel", JOptionPane.OK_CANCEL_OPTION);
        
        /* To test
        public static String DATABASE_DRIVER_NAME = "org.apache.derby.jdbc.ClientDriver";
        public static String DATABASE_CONNECTION_STRING = "jdbc:derby://localhost:1527/sample";
        public static String LOGIN = "app";
        public static String PASSWORD = "app";
        */
        
        if(clickedValue == JOptionPane.OK_OPTION) {
            OtherConstants.DATABASE_DRIVER_NAME = DB_DRIVER_TXT.getText();
            OtherConstants.DATABASE_CONNECTION_STRING = CONNECTION_STRING_TXT.getText();
            OtherConstants.LOGIN = LOGIN_TXT.getText();
            OtherConstants.PASSWORD = String.valueOf(PASSWORD_TXT.getPassword());
            
            if(CONNECTION_STRING_TXT.getText().contains("oracle"))
                OtherConstants.CURRENT_DATE = "sysdate";
            else
                OtherConstants.CURRENT_DATE = "current_date";
            
            new MainWindowClass().initFrame();
        }
    }
}
