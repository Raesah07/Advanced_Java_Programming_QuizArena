package com.quizarena.model;
public record PlayerAnswer(long questionId,String selectedOption,boolean correct,long responseMs) {}
