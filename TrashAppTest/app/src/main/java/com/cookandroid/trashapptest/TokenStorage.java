package com.cookandroid.trashapptest;

import android.content.Context;
import android.security.keystore.KeyGenParameterSpec;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class TokenStorage {
    static String storage_token = "";
    static String storage_rtoken = "";
    TokenStorage(){

    }
    public void setToken(String token){
        storage_token = token;
    }
    public void setrToken(String rtoken){
        storage_rtoken = rtoken;
    }
    public String getToken(){
        return storage_token;
    }
    public String getrToken(){
        return storage_rtoken;
    }
}
