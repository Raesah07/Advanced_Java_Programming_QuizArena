package com.quizarena.model;
public record PlayerScore(long userId,int score,int correct,long responseMs,int rank) {}
