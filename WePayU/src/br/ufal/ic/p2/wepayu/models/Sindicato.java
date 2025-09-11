package br.ufal.ic.p2.wepayu.models;

import java.util.ArrayList;
import java.util.List;

public class Sindicato {
    private static List<String> memberIds = new ArrayList<>();
    public static List<String> getIdsSindicato() {return memberIds;}
    public static void setIdsSindicato(List<String> idsSindicato) {Sindicato.memberIds = idsSindicato;}
    public static boolean inSindicato(String idSindicato) {return memberIds.contains(idSindicato);}
    public static void addIdSindicato(String idSindicato) {memberIds.add(idSindicato);}
    public static void removeIdSindicato(String idSindicato) {memberIds.remove(idSindicato);}
    public static void zerarSindicato() {memberIds.clear();}
}