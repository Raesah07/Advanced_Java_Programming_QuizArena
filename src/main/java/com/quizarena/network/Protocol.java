package com.quizarena.network;
public final class Protocol { private Protocol(){} public static String join(String... p){return String.join("|",p).replace("|","/");} public static String[] split(String line){return line.split("\\|",-1);} }
