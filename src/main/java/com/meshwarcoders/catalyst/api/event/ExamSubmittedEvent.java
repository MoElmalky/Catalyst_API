package com.meshwarcoders.catalyst.api.event;

import com.meshwarcoders.catalyst.api.model.StudentAnswerModel;
import java.util.List;

public record ExamSubmittedEvent(List<StudentAnswerModel> answers) {
}
