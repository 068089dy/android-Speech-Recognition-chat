package com.example.dy.group_demo6.util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by dy on 17-3-8.
 */

public class json_method {
    public static Map toMap(String jsonString) throws JSONException {

        JSONObject jsonObject = new JSONObject(jsonString);

        Map result = new HashMap();
        Iterator iterator = jsonObject.keys();
        String key = null;
        String value = null;

        while (iterator.hasNext()) {

            key = (String) iterator.next();
            value = jsonObject.getString(key);
            result.put(key, value);
            //System.out.println("key:"+key+" "+"value"+value);

        }
        return result;

    }
    public static String tojsonstr(Map map){
        //Map result = new HashMap();
        Iterator<Map.Entry<String,String>> iterator = map.entrySet().iterator();
        String key = null;
        String value = null;
        String jsonstr = null;
        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            key = entry.getKey().toString();
            value = map.get(key).toString();
            //result.put(key, value);
            System.out.println("key:"+key+" "+"value:"+value);
            String jsonstr1 = "\""+key+"\""+":"+"\""+value+"\"";
            if(jsonstr==null) {
                jsonstr = jsonstr1;
            }else{
                jsonstr = jsonstr + "," + jsonstr1;
            }
        }
        jsonstr = "{"+jsonstr+"}";
        System.out.println(jsonstr);
        return jsonstr;
    }
}
