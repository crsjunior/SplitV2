package br.com.split.utilidades;

import java.text.NumberFormat;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class MonetaryMask implements TextWatcher {  
    
    private boolean isUpdating;  
    private EditText mEditText;  
    private NumberFormat mNF = NumberFormat.getCurrencyInstance(); 
    private String originalText;
    
    public MonetaryMask(EditText et) {
    	  mEditText = et;
    	  }
    
    
    @Override  
    public void onTextChanged(CharSequence cs, int start, int before, int after) {  
       if (isUpdating) {  
          isUpdating = false;  
          return;  
       }  
   
       isUpdating = true;  
       String str = cs.toString();  
       boolean hasMask = (str.indexOf("R$") >= 0 && str.indexOf(".") >= 0 && str.indexOf(",") >= 0) || (str.indexOf("R$") >= 0 && str.indexOf(",") >= 0);  
   
       if (hasMask) {  
          str = str.replaceAll("[R$]", "").replaceAll("[.]", "").replaceAll("[,]", "");  
       }  
   
       try {  
          double value = (Double.parseDouble(str) / 100);
          originalText = String.valueOf(value);
          str = mNF.format(value);  
          mEditText.setText(str);  
          mEditText.setSelection(str.length());  
       } catch (Exception e) {  
          e.printStackTrace();  
       }  
    }  
    
    public String getOriginalText(){
    	return originalText;
    }
    
    
   
    @Override  
    public void beforeTextChanged(CharSequence cs, int start, int count, int after) {  
       // Não iremos utilizar...  
    }  
   
    @Override  
    public void afterTextChanged(Editable e) {  
         
    }  
   
 }  