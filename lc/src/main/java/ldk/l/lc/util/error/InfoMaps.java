package ldk.l.lc.util.error;

import java.util.HashMap;

public class InfoMaps {
    public static HashMap<Integer,String> zh_cn=new HashMap<>(){{
        put(0,"");
        put(1,"非法字符 '..'");
        put(2,"未结束的字符串字面量");
        put(3,"未结束的字符字面量");
        put(4,"非法转义符");
        put(5,"未结束的注释");
        put(6,"需要<标识符>");
        put(7,"在\"extends\"之后应为父类名");
        put(8,"需要'{'");
        put(9,"需要'}'");
    }};
    public static HashMap<Integer,String> en_us=new HashMap<>(){{
        put(0,"");
        put(1,"illegal '..'");
        put(2,"unclosed string literal");
        put(3,"unclosed character literal");
        put(4,"illegal escape character");
        put(5,"unclosed comment");
        put(6,"requires<identifier>");
        put(7,"expected super class name after 'extends'");
        put(8,"requires '{'");
        put(9,"requires '}'");
    }};
}