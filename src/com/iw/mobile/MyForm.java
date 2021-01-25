/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iw.mobile;

import com.codename1.ui.Form;
import com.codename1.ui.layouts.GridLayout;

/**
 *
 * @author helio
 */
public class MyForm extends Form {
    public MyForm() {
        super("Test MyBrowserComponent");
        setLayout(new GridLayout(1,1));
        add(new MyBrowserComponent());
    }
}
