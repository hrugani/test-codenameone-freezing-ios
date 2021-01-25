/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iw.mobile;

import com.codename1.io.Util;
import com.codename1.ui.BrowserComponent;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.util.SuccessCallback;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author helio
 */
public class MyBrowserComponent extends BrowserComponent {

    private ArrayList<String> varNamesList;
    private HashMap<String,String> initialVariablesMap;
    private BrowserComponent.JSProxy jsVariablesProxy;

    public MyBrowserComponent() {
        super();
        setPage("<html><body><div><h1>TESTE ...</h1></div></body></html>", null);
        addWebEventListener(BrowserComponent.onLoad, e -> {
            onLoad();
        });
    }

    private void onLoad() {
        
        this.varNamesList = new ArrayList<String>();
        this.varNamesList.add("iwvar_patient_name");
        this.varNamesList.add("iwvar_e_f_peso");
        this.varNamesList.add("iwvar_e_f_altura");
        this.varNamesList.add("iwvar_e_f_imc");
        this.varNamesList.add("iwvar_e_f_clasimc");
        this.varNamesList.add("iwvar_e_f_sondas_colost");        
        
        this.jsVariablesProxy = createJSProxy("$scope.variables");  
        this.initialVariablesMap = getHtmlVariableValues();
        
        Dialog.show("test", "this is a test...", "OK", null);
        
        System.out.println("teste... teste... teste");
        System.out.println("teste... teste... teste");
        System.out.println("teste... teste... teste");
           
    }    
    
    public HashMap<String,String> getHtmlVariableValues() {
        boolean isIOS = isIOS();
        if (isIOS) {
            return getHtmlVariableValues_sync();        
        }
        return getHtmlVariableValues_sync();
    }
    
    private HashMap<String,String> getHtmlVariableValues_sync() {
        HashMap<String,String> resp = null;

        if (this.jsVariablesProxy != null && varNamesList != null) {
            
            resp = new HashMap<String,String>();
            String strValue = "";
            
            for (String varName : varNamesList) {
                JSRef ref =  jsVariablesProxy.getAndWait(varName);
                JSType type = ref.getJSType();
                if (type == JSType.STRING) {
                    strValue = ref.getValue();
                }
                else {
                    if (type == JSType.BOOLEAN) {
                        if (ref.getBoolean()) {
                            strValue = "1";
                        }
                        else {
                            strValue = "0";
                        }
                    }
                }
                resp.put(varName, strValue);
            }
        }
        
        return resp;        
    }
    
    private HashMap<String,String> getHtmlVariableValues_async() {

        final HashMap<String,String> resp = new HashMap<String,String>();

        if (this.jsVariablesProxy != null && varNamesList != null) {
            
            final String READY_FLAG = "READY_FLAG"; 
            
            final HashMap<String, Boolean> varReadyFlagMap = new HashMap<String, Boolean>();
            varReadyFlagMap.put(READY_FLAG, false);
            
            SuccessCallback<Map<String,JSRef>> callback = new SuccessCallback<Map<String, JSRef>>() {
                @Override
                public void onSucess(Map<String, JSRef> value) {
                    for (Map.Entry<String,JSRef> entry : value.entrySet()) {
                        resp.put(entry.getKey(), getValueFromJSRef(entry.getValue()));
                    }
                    varReadyFlagMap.put(READY_FLAG, true);
                }
                
                private String getValueFromJSRef(JSRef jsRef) {
                    String strValue = "";
                    JSType type = jsRef.getJSType();
                    if (type == JSType.STRING) {
                        strValue = jsRef.getValue();
                    }
                    else {
                        if (type == JSType.BOOLEAN) {
                            if (jsRef.getBoolean()) {
                                strValue = "1";
                            }
                            else {
                                strValue = "0";
                            }
                        }
                    }
                    return strValue;
                }
            };
            jsVariablesProxy.get(this.varNamesList, callback);                       
            //Util.sleep(1000 * 15); // 15 seconds
            while (!varReadyFlagMap.get(READY_FLAG)) {
                Util.sleep(1000 * 1);
                if (varReadyFlagMap.get(READY_FLAG)) {
                    return resp;
                }
            }
            Dialog.show("IOS issue", "getHtmlVaribles dont't finished in 15 seconds" , "OK", null);   
        }
            
        return resp;
    }

    public void setHtmlVariablesValues(Map<String,String> varMap) {
        if (varMap.isEmpty()) {
            return;
        }
        java.util.Set<String> varNamesSet = varMap.keySet();
        for (String varName : varNamesSet) {
            if (this.varNamesList.contains(varName)) {                
                String strValue = varMap.get(varName);
                // boolean conversion
                Boolean bValue = false;
                if (strValue == null || "1".equals(strValue)) {
                    bValue = true;
                } 
                execute("getAngularScope().variables['" + varName + "']= " + bValue + ";getAngularScope().$apply();");
            }
            else {
                String strValue = varMap.get(varName);
                execute("getAngularScope().variables['" + varName + "']= '" + strValue + "';getAngularScope().$apply();");
            }
        }
    }    

        
    public boolean isIOS() {
        String so = Display.getInstance().getPlatformName();
        return "ios".equals(so);
    }

    public boolean isAndroid() {
        String so = Display.getInstance().getPlatformName();
        return "and".equals(so);
    }
    
    public String getOSVersion() {
        return Display.getInstance().getProperty("OSVer", "");
    }
        
        
}


