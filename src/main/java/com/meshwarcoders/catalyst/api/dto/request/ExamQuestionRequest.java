package com.meshwarcoders.catalyst.api.dto.request;

import com.meshwarcoders.catalyst.api.model.common.QuestionType;

import java.util.List;

public record ExamQuestionRequest(String text,
                                  QuestionType type,
                                  List<String> options,
                                  List <Integer> correctOptionIndex,
                                  Integer maxPoints,
                                  String answer) {

}