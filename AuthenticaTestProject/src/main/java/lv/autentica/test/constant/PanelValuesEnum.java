/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lv.autentica.test.constant;

/**
 *
 * @author aks513
 */
public enum PanelValuesEnum {
    CARS("Cars"),
    EMPLOYEES("Employees");
    
    private final String value;

    private PanelValuesEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    
}
