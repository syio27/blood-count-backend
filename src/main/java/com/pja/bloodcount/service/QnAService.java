package com.pja.bloodcount.service;

import com.pja.bloodcount.dto.request.AnswerRequest;
import com.pja.bloodcount.model.*;
import com.pja.bloodcount.repository.*;
import com.pja.bloodcount.validation.PatientValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@AllArgsConstructor
public class QnAService {

    private final BCAssessmentQuestionRepository bcaQuestionRepository;
    private final AnswerRepository answerRepository;
    private final MSQuestionRepository msQuestionRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final GameRepository gameRepository;
    private final QuestionRepository questionRepository;
    private final ErythrocyteQBRepository erythrocyteQBRepository;
    private final LeukocyteQBRepository leukocyteQBRepository;
    private final VariousQBRepository variousQBRepository;
    private final PatientValidator patientValidator;

    public List<BCAssessmentQuestion>  createQnAForBCAssessment(Long gameId){
        Optional<Game> optionalGame = gameRepository.findById(gameId);
        if(optionalGame.isEmpty()){
            // TODO: change to Game related Exception class
            throw new RuntimeException("game is not found");
        }
        Game game = optionalGame.get();
        Patient patient = patientValidator.validateIfExistsAndGet(game.getPatient().getId());
        List<BloodCount> bloodCountList = patient.getBloodCounts();
        List<BCAssessmentQuestion> questionList = new ArrayList<>();
        bloodCountList.forEach(bloodCount -> {
            if(isForAssessment(bloodCount.getParameter(), bloodCount.getUnit())){
               Answer answer1 = Answer
                       .builder()
                       .text("INCREASED")
                       .build();
               Answer answer2 = Answer
                        .builder()
                        .text("NORMAL")
                        .build();
               Answer answer3 = Answer
                        .builder()
                        .text("DECREASED")
                        .build();

               //answerRepository.saveAll(List.of(answer1, answer2, answer3));

               BCAssessmentQuestion question = BCAssessmentQuestion
                       .builder()
                       .parameter(bloodCount.getParameter())
                       .unit(bloodCount.getUnit())
                       .value(bloodCount.getValue())
                       .build();
               question.addAnswer(answer1);
               question.addAnswer(answer2);
               question.addAnswer(answer3);
               bcaQuestionRepository.save(question);
               List<Answer> answers = question.getAnswers();
               answers.forEach(answer -> {
                   if(bloodCount.getLevelType().name().equals(answer.getText())){
                       question.setCorrectAnswerId(answer.getId());
                   }
               });
               questionList.add(question);
            }
        });
        bcaQuestionRepository.saveAll(questionList);
        return questionList;
    }

    public List<MSQuestion> createMSQuestions(Long gameId){
        Optional<Game> optionalGame = gameRepository.findById(gameId);
        if(optionalGame.isEmpty()){
            // TODO: change to Game related Exception class
            throw new RuntimeException("game is not found");
        }
        Game game = optionalGame.get();
        Answer answer1Q1 = Answer
                .builder()
                .text("Mikrocytowa")
                .build();
        Answer answer2Q1 = Answer
                .builder()
                .text("Normocytowa")
                .build();
        Answer answer3Q1 = Answer
                .builder()
                .text("Makrocytowa")
                .build();
        Answer answer4Q1 = Answer
                .builder()
                .text("Pacjent nie ma niedokrwistości")
                .build();

        MSQuestion msQuestion1 = MSQuestion
                .builder()
                .text("Jak określisz niedokrwistość:")
                .build();
        msQuestion1.addAnswer(answer1Q1);
        msQuestion1.addAnswer(answer2Q1);
        msQuestion1.addAnswer(answer3Q1);
        msQuestion1.addAnswer(answer4Q1);

        Answer answer1Q2 = Answer
                .builder()
                .text("Stopień 0 (norma) – stężenie Hb >= 11,0 g/dl")
                .build();
        Answer answer2Q2 = Answer
                .builder()
                .text("Stopień I (łagodna) – stężenie Hb 9,5 – 10,9 g/dl")
                .build();
        Answer answer3Q2 = Answer
                .builder()
                .text("Stopień II (umiarkowana) – stężenie Hb 8,0 – 9,4 g/dl")
                .build();
        Answer answer4Q2 = Answer
                .builder()
                .text("Stopień III (ciężka) - stężenie Hb 6,5 – 7,9 g/dl")
                .build();

        Answer answer5Q2 = Answer
                .builder()
                .text("Stopień IV (bardzo ciężka) – stężenie Hb < 6,5 g/dl")
                .build();

        MSQuestion msQuestion2 = MSQuestion
                .builder()
                .text("Stosując klasyfikację WHO, rozpoznasz:")
                .build();
        msQuestion2.addAnswer(answer1Q2);
        msQuestion2.addAnswer(answer2Q2);
        msQuestion2.addAnswer(answer3Q2);
        msQuestion2.addAnswer(answer4Q2);
        msQuestion2.addAnswer(answer5Q2);

        msQuestionRepository.saveAll(List.of(msQuestion1, msQuestion2));
        List<Answer> answersMSQ1 = msQuestion1.getAnswers();
        String anemiaType = game.getCaseDetails().getAnemiaType();
        String formattedAnemia = formatString(anemiaType);
        answersMSQ1.forEach(answer -> {
            if(answer.getText().equals(formattedAnemia)){
                msQuestion1.setCorrectAnswerId(answer.getId());
            }
        });
        List<Answer> answersMSQ2 = msQuestion2.getAnswers();
        double hgbValue = game.getPatient().getBloodCounts().stream().filter(bloodCount -> "HGB".equals(bloodCount.getParameter())).findFirst().get().getValue();
        log.info("HGB value is: {}", hgbValue);
        answersMSQ2.forEach(answer -> {
            String range = getRangeInString(answer.getText());
            log.info("Range of answer's text: {}", range);
            if(isInRange(range, hgbValue)){
                msQuestion2.setCorrectAnswerId(answer.getId());
            }
        });

        msQuestionRepository.saveAll(List.of(msQuestion1, msQuestion2));
        return new ArrayList<>(List.of(msQuestion1, msQuestion2));
    }

    public List<MSQuestion> createTrueFalseMSQuestions(Long gameId){
        Optional<Game> optionalGame = gameRepository.findById(gameId);
        if(optionalGame.isEmpty()){
            // TODO: change to Game related Exception class
            throw new RuntimeException("game is not found");
        }
        Game game = optionalGame.get();
        List<MSQuestion> msQuestions = new ArrayList<>();
        List<ErythrocyteQuestion> allErythrocyteQuestions = erythrocyteQBRepository.findAll();
        List<ErythrocyteQuestion> randomErythrocyteQuestions = randomlyPickQuestions(allErythrocyteQuestions, 3);
        randomErythrocyteQuestions.forEach(erythrocyteQuestion -> {
            Answer answerTrue = Answer
                    .builder()
                    .text("Prawda")
                    .build();
            Answer answerFalse = Answer
                    .builder()
                    .text("Fałsz")
                    .build();
            MSQuestion msQuestion = MSQuestion
                    .builder()
                    .text(erythrocyteQuestion.getText())
                    .build();
            msQuestion.addAnswer(answerTrue);
            msQuestion.addAnswer(answerFalse);
            msQuestionRepository.save(msQuestion);
            List<Answer> answers = msQuestion.getAnswers();
            answers.forEach(answer -> {
                if(answer.getText().equals(erythrocyteQuestion.getAnswer())){
                    msQuestion.setCorrectAnswerId(answer.getId());
                }
            });
            msQuestions.add(msQuestion);
            msQuestionRepository.save(msQuestion);
        });

        List<LeukocyteQuestion> allLeukocyteQuestions = leukocyteQBRepository.findAll();
        List<LeukocyteQuestion> randomLeukocyteQuestions = randomlyPickQuestions(allLeukocyteQuestions, 3);

        randomLeukocyteQuestions.forEach(leukocyteQuestion -> {
            Answer answerTrue = Answer
                    .builder()
                    .text("Prawda")
                    .build();
            Answer answerFalse = Answer
                    .builder()
                    .text("Fałsz")
                    .build();
            MSQuestion msQuestion = MSQuestion
                    .builder()
                    .text(leukocyteQuestion.getText())
                    .build();
            msQuestion.addAnswer(answerTrue);
            msQuestion.addAnswer(answerFalse);
            msQuestionRepository.save(msQuestion);
            List<Answer> answers = msQuestion.getAnswers();
            answers.forEach(answer -> {
                if(answer.getText().equals(leukocyteQuestion.getAnswer())){
                    msQuestion.setCorrectAnswerId(answer.getId());
                }
            });
            msQuestions.add(msQuestion);
            msQuestionRepository.save(msQuestion);
        });

        List<VariousQuestion> allVariousQuestions = variousQBRepository.findAll();
        List<VariousQuestion> randomVariousQuestions = randomlyPickQuestions(allVariousQuestions, 4);

        randomVariousQuestions.forEach(variousQuestion -> {
            Answer answerTrue = Answer
                    .builder()
                    .text("Prawda")
                    .build();
            Answer answerFalse = Answer
                    .builder()
                    .text("Fałsz")
                    .build();
            MSQuestion msQuestion = MSQuestion
                    .builder()
                    .text(variousQuestion.getText())
                    .build();
            msQuestion.addAnswer(answerTrue);
            msQuestion.addAnswer(answerFalse);
            msQuestionRepository.save(msQuestion);
            List<Answer> answers = msQuestion.getAnswers();
            answers.forEach(answer -> {
                if(answer.getText().equals(variousQuestion.getAnswer())){
                    msQuestion.setCorrectAnswerId(answer.getId());
                }
            });
            msQuestions.add(msQuestion);
            msQuestionRepository.save(msQuestion);
        });

        return msQuestions;
    }

    public int score(List<AnswerRequest> answerRequestList, Long gameId){
        AtomicInteger score = new AtomicInteger(0);
        List<UserAnswer> userAnswers = new ArrayList<>();

        answerRequestList.forEach(answerRequest -> {
            Optional<Question> optionalQuestion = questionRepository.findById(answerRequest.getQuestionId());
            if(optionalQuestion.isEmpty()){
                throw new RuntimeException("Question is not found");
            }
            Question question = optionalQuestion.get();
            if(!Objects.equals(question.getGame().getId(), gameId)){
                throw new RuntimeException("Question is not part game: " + gameId);
            }
            Optional<Answer> optionalAnswer = answerRepository.findById(answerRequest.getAnswerId());
            if(optionalAnswer.isEmpty()){
                throw new RuntimeException("Answer is not found");
            }
            Answer answer = optionalAnswer.get();
            log.info("Answer's question id: {}", answer.getQuestion().getId());
            log.info("question id from request: {}",answerRequest.getQuestionId());
            if(!Objects.equals(answer.getQuestion().getId(), answerRequest.getQuestionId())){
                throw new RuntimeException("Answer is not part of answers set of question: " + answerRequest.getQuestionId());
            }
            if(Objects.equals(question.getCorrectAnswerId(), answerRequest.getAnswerId())){
                score.getAndIncrement();
            }

            UserAnswer userAnswer = UserAnswer
                    .builder()
                    .game(question.getGame())
                    .user(question.getGame().getUser())
                    .answer(answer)
                    .question(question)
                    .build();

            userAnswers.add(userAnswer);
        });
        userAnswerRepository.saveAll(userAnswers);
        return score.get();
    }

    private boolean isForAssessment(String parameter, String unit) {
        HashMap<String, String> bloodCountMap = new HashMap<>();
        bloodCountMap.put("WBC", "10^9/L");
        bloodCountMap.put("RBC", "10^12/L");
        bloodCountMap.put("HGB", "g/dl");
        bloodCountMap.put("HCT", "%");
        bloodCountMap.put("MCV", "fl");
        bloodCountMap.put("MCH", "pg");
        bloodCountMap.put("MCHC", "g/dl");
        bloodCountMap.put("PLT", "10^9/L");

        return bloodCountMap.containsKey(parameter) && bloodCountMap.containsValue(unit);
    }

    private String getRangeInString(String text){
        Pattern pattern = Pattern.compile("(?<=Hb\\s).+?(?=\\sg/dl)");
        Matcher matcher = pattern.matcher(text);
        if(matcher.find()){
            return matcher.group();
        }
        return null;
    }

    private static String formatString(String input) {
        input = input.substring(3);
        input = input.substring(0, 1).toUpperCase() + input.substring(1);
        return input;
    }

    private boolean isInRange(String range, double value){
        double max, min;
        List<String> ranges = new ArrayList<>(List.of(range.split(" ")));
        if(ranges.contains(">=")){
            min = Double.parseDouble(ranges.get(1).replace(',', '.'));
            log.info("min: {}", min);
            return value >= min;
        }
        if(ranges.contains("<")){
            max = Double.parseDouble(ranges.get(1).replace(',', '.'));
            log.info("min: {}", max);
            return value < max;
        }
        if(ranges.contains("–")){
            min = Double.parseDouble(ranges.get(0).replace(',', '.'));
            max = Double.parseDouble(ranges.get(2).replace(',', '.'));
            log.info("min: {}, and max: {}", min, max);
            return value > min && value < max;
        }
        return false;
    }

    private <T> List<T> randomlyPickQuestions(List<T> allQuestions, int number){
        List<T> questions = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            T randomEntity = allQuestions.remove(ThreadLocalRandom.current().nextInt(allQuestions.size()));
            questions.add(randomEntity);
        }
        return questions;
    }
}