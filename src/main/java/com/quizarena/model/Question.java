package com.quizarena.model;
import java.io.Serializable;
public record Question(long id,String text,String a,String b,String c,String d,char correctOption) implements Serializable { public String option(char x){return switch(Character.toUpperCase(x)){case 'A'->a;case 'B'->b;case 'C'->c;case 'D'->d;default->"";};} }
