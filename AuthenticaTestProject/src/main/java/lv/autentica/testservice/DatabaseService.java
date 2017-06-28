/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lv.autentica.testservice;

import java.lang.reflect.Array;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import lv.autentica.test.constant.OtherConstants;

/**
 *
 * @author aks513
 */
public class DatabaseService {
    public Connection getDatabaseConnection() {
        try {
            Class.forName(OtherConstants.DATABASE_DRIVER_NAME);
            return DriverManager.getConnection(OtherConstants.DATABASE_CONNECTION_STRING, OtherConstants.LOGIN, OtherConstants.PASSWORD);
        } catch (ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getCause(), ex.getLocalizedMessage(), JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
        
    public ArrayList<String> getAvailableCarsToRentFormCombobox() {
        final ArrayList<String> collection = new ArrayList<>();
        
        try (final Statement dbStatement = getDatabaseConnection().createStatement()) {
            final ResultSet resultSet = dbStatement.executeQuery(OtherConstants.GET_ALL_CARS);
            
            while(resultSet.next())
                collection.add(resultSet.getString("NUMBER"));
                
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getCause(), "Car data error", JOptionPane.ERROR_MESSAGE);
        }    
        
        return collection;
    }
    
    public ArrayList<String> getAvailablePersonsToRentFormCombobox() {
        final ArrayList<String> collection = new ArrayList<>();
        
        try (final Statement dbStatement = getDatabaseConnection().createStatement()) {
            final ResultSet resultSet = dbStatement.executeQuery(OtherConstants.GET_ALL_EMPS);
            
            while(resultSet.next())
                collection.add(resultSet.getString("NAME"));
                
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getCause(), "Employee data error", JOptionPane.ERROR_MESSAGE);
        }    
        
        return collection;
    }
    
    public int getEmployeeIdByName(final String employeeName) {
        try (final PreparedStatement stmt = getDatabaseConnection().prepareStatement(
                OtherConstants.GET_PERSON_ID, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY
            )){
            
            stmt.setString(1, employeeName);            
            final ResultSet rs = stmt.executeQuery();
            
            if(rs.first())
                return rs.getInt("EMP_ID");
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getCause(), "Employee data error", JOptionPane.ERROR_MESSAGE);
            return -1;
        }
        
        return -1;
    }
    
    public void updateCarRentByName(final int userId, final String carName) {
        try (final PreparedStatement stmt = getDatabaseConnection().prepareStatement(OtherConstants.UPDATE_CAR)){
            
            stmt.setInt(1, userId);
            stmt.setString(2, carName);
            stmt.executeUpdate();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getCause(), "Cannot update car value", JOptionPane.ERROR_MESSAGE);            
        }
    }
    
    public void insertRentCar(final String... values) {
        try (final PreparedStatement stmt = getDatabaseConnection().prepareStatement(OtherConstants.RENT_CAR_INSERT)){
            
            stmt.setString(1, String.format("%s", Array.get(values, 0)));
            stmt.setDate(2, Date.valueOf(String.format("%s", Array.get(values, 1))));
            stmt.setDate(3, Date.valueOf(String.format("%s", Array.get(values, 2))));
            stmt.executeUpdate();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getCause(), "Cannot insert rent car", JOptionPane.ERROR_MESSAGE);            
        }
    }
    
    public void invalidateRentedCars() {
        try (final PreparedStatement stmt = getDatabaseConnection().prepareStatement(OtherConstants.INVALIDATE_RENTED_CARS)){            
            stmt.executeUpdate();            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getCause(), "Cannot update set car unrent", JOptionPane.ERROR_MESSAGE);            
        }
    }
}
