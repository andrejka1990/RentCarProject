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
public enum ButtonValuesEnum {
    ADD("Add"),
    EDIT("Edit"),
    DELETE("Delete"),
    RENT_CAR("Show rent cars");
    
    private final String value;

    private ButtonValuesEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
