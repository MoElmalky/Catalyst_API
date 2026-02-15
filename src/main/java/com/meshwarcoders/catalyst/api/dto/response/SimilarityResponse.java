package com.meshwarcoders.catalyst.api.dto.response;

public class SimilarityResponse {

    private Long studentId;
    private Long questionId;

    private double similarity;
    private double score;
    private boolean passed;

    public SimilarityResponse() {
        // Default constructor
    }

    public SimilarityResponse(
            Long studentId,
            Long questionId,
            double similarity,
            double score,
            boolean passed
    ) {
        this.studentId = studentId;
        this.questionId = questionId;
        this.similarity = similarity;
        this.score = score;
        this.passed = passed;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }
}
