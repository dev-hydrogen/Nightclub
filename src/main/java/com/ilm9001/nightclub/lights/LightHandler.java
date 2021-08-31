package com.ilm9001.nightclub.lights;

import com.ilm9001.nightclub.lights.DownTop.DownTopCircle;
import com.ilm9001.nightclub.lights.Side.FrontFacerCircle;
import com.ilm9001.nightclub.lights.TopDown.TopDownCircle;
import com.ilm9001.nightclub.lights.TopDown.TopDownDoubleCircle;
import com.ilm9001.nightclub.lights.TopDown.TopDownLineCircle;
import com.ilm9001.nightclub.parse.ConfigParser;

public class LightHandler {
    
    public static class BackLasers {
        
        public static void on(boolean isBlue) {
            for(DownTopCircle dtc : ConfigParser.getDownTopCircleList()) {
                dtc.on();
            }
        }
    
        public static void off() {
            for(DownTopCircle dtc : ConfigParser.getDownTopCircleList()) {
                dtc.off();
            }
        }
    
        public static void flash(boolean isBlue) {
            for(DownTopCircle dtc : ConfigParser.getDownTopCircleList()) {
                dtc.flash();
            }
        }
    
        public static void flashOff(boolean isBlue) {
            for(DownTopCircle dtc : ConfigParser.getDownTopCircleList()) {
                dtc.flashOff();
            }
        }
        public static void setSpeed(int multiplier) {
            for(DownTopCircle dtc : ConfigParser.getDownTopCircleList()) {
                dtc.setSpeed(multiplier);
            }
        }
    }
    public static class RingLights {
    
        public static void on(boolean isBlue) {
            for(TopDownLineCircle tdlc : ConfigParser.getTopDownLineCircleList()) {
                tdlc.on();
            }
            //ConfigParser.getCube().on();
        }
        
        public static void off() {
            for(TopDownLineCircle tdlc : ConfigParser.getTopDownLineCircleList()) {
                tdlc.off();
            }
            //ConfigParser.getCube().off();
        }
        
        public static void flash(boolean isBlue) {
            for(TopDownLineCircle tdlc : ConfigParser.getTopDownLineCircleList()) {
                tdlc.flash();
            }
            //ConfigParser.getCube().flash();
        }
        
        public static void flashOff(boolean isBlue) {
            for(TopDownLineCircle tdlc : ConfigParser.getTopDownLineCircleList()) {
                tdlc.flashOff();
            }
            //ConfigParser.getCube().flashOff();
        }
        public static void setSpeed(int multiplier) {
            for(TopDownLineCircle tdlc : ConfigParser.getTopDownLineCircleList()) {
                tdlc.setSpeed(multiplier);
            }
            //ConfigParser.getCube().setSpeed(multiplier);
        }
    }
    public static class LeftLasers {
        
        public static void on(boolean isBlue) {
            for(TopDownCircle tdc : ConfigParser.getTopDownCircleList()) {
                tdc.on();
            }
        }
        
        public static void off() {
            for(TopDownCircle tdc : ConfigParser.getTopDownCircleList()) {
                tdc.off();
            }
        }
        
        public static void flash(boolean isBlue) {
            for(TopDownCircle tdc : ConfigParser.getTopDownCircleList()) {
                tdc.flash();
            }
        }
        
        public static void flashOff(boolean isBlue) {
            for(TopDownCircle tdc : ConfigParser.getTopDownCircleList()) {
                tdc.flashOff();
            }
        }
        
        public static void setSpeed(int multiplier) {
            for(TopDownCircle tdc : ConfigParser.getTopDownCircleList()) {
                tdc.setSpeed(multiplier);
            }
        }
    }
    public static class RightLasers {
        
        public static void on(boolean isBlue) {
            for(TopDownDoubleCircle tddc : ConfigParser.getTopDownDoubleCircleList()) {
                tddc.on();
            }
        }
        
        public static void off() {
            for(TopDownDoubleCircle tddc : ConfigParser.getTopDownDoubleCircleList()) {
                tddc.off();
            }
        }
        
        public static void flash(boolean isBlue) {
            for(TopDownDoubleCircle tddc : ConfigParser.getTopDownDoubleCircleList()) {
                tddc.flash();
            }
        }
        
        public static void flashOff(boolean isBlue) {
            for(TopDownDoubleCircle tddc : ConfigParser.getTopDownDoubleCircleList()) {
                tddc.flashOff();
            }
        }
        public static void setSpeed(int multiplier) {
            for(TopDownDoubleCircle tddc : ConfigParser.getTopDownDoubleCircleList()) {
                tddc.setSpeed(multiplier);
            }
        }
    }
    public static class CenterLights {
        
        public static void on(boolean isBlue) {
            for (FrontFacerCircle ff : ConfigParser.getFrontFacerList()) {
                ff.on();
            }
        }
        
        public static void off() {
            for (FrontFacerCircle ff : ConfigParser.getFrontFacerList()) {
                ff.off();
            }
        }
        
        public static void flash(boolean isBlue) {
            for (FrontFacerCircle ff : ConfigParser.getFrontFacerList()) {
                ff.flash();
            }
        }
        
        public static void flashOff(boolean isBlue) {
            for (FrontFacerCircle ff : ConfigParser.getFrontFacerList()) {
                ff.flashOff();
            }
        }
        public static void setSpeed(int multiplier) {
            for (FrontFacerCircle ff : ConfigParser.getFrontFacerList()) {
                ff.setSpeed(multiplier);
            }
        }
    }
}
