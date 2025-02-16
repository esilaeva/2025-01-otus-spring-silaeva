package ru.otus.hw01.dao;

import ru.otus.hw01.domain.Question;

import java.util.List;

public interface QuestionDao {
  List<Question> findAll();
}