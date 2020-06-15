/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notesfx.service;

import java.util.prefs.Preferences;

/**
 *
 * @author RIZAL
 */
public class DataSession {
    private Preferences p;

    public DataSession() {
    }
    
    public String getColorBg(){
        p = Preferences.userRoot().node("notefx");
        return p.get("bg", "-fx-background-color:#ffffff");
    }
    public void setColorBg(String bg){
        p = Preferences.userRoot().node("notefx");
        p.put("bg", bg);
    }
    public String getColorTxt(){
        p = Preferences.userRoot().node("notefx");
        return p.get("txt", "-fx-text-fill:#000000");
    }
    public void setColorTxt(String txt){
        p = Preferences.userRoot().node("notefx");
        p.put("txt", txt);
    }
}
