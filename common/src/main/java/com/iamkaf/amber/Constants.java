package com.iamkaf.amber;

//? if <=1.16.5 {
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//?} else {
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//?}

public class Constants {
    /**
     * Identifier of the mod. Update these fields when reusing the template.
     */
    public static final String MOD_ID = "amber";
    public static final String MOD_NAME = "Amber";
    //? if <=1.16.5 {
    public static final Logger LOG = LogManager.getLogger(MOD_NAME);
    //?} else {
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);
    //?}
}
