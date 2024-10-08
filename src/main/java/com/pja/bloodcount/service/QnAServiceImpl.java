package com.pja.bloodcount.service;

import com.pja.bloodcount.exceptions.*;
import com.pja.bloodcount.model.*;
import com.pja.bloodcount.model.enums.Language;
import com.pja.bloodcount.model.enums.LevelType;
import com.pja.bloodcount.model.enums.Parameter;
import com.pja.bloodcount.model.enums.Unit;
import com.pja.bloodcount.repository.*;
import com.pja.bloodcount.service.contract.QnAService;
import com.pja.bloodcount.validation.PatientValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@AllArgsConstructor
public class QnAServiceImpl implements QnAService {

    private final BCAssessmentQuestionRepository bcaQuestionRepository;
    private final MSQuestionRepository msQuestionRepository;
    private final GameRepository gameRepository;
    private final ErythrocyteQBRepository erythrocyteQBRepository;
    private final LeukocyteQBRepository leukocyteQBRepository;
    private final VariousQBRepository variousQBRepository;

    @Override
    public List<BCAssessmentQuestion> createQnAForBCAssessment(Game game) {
        Patient patient = game.getPatient();
        List<BloodCount> bloodCountList = patient.getBloodCounts();
        List<BCAssessmentQuestion> questionList = new ArrayList<>();
        bloodCountList.forEach(bloodCount -> {
            if (isForAssessment(bloodCount.getParameter(), bloodCount.getUnit())) {
                Answer answer1 = Answer
                        .builder()
                        .text(LevelType.INCREASED.name())
                        .build();
                Answer answer2 = Answer
                        .builder()
                        .text(LevelType.NORMAL.name())
                        .build();
                Answer answer3 = Answer
                        .builder()
                        .text(LevelType.DECREASED.name())
                        .build();

                BCAssessmentQuestion question = BCAssessmentQuestion
                        .builder()
                        .parameter(bloodCount.getParameter())
                        .unit(bloodCount.getUnit())
                        .value(bloodCount.getValue())
                        .build();
                question.addAllAnswers(List.of(answer1, answer2, answer3));
                bcaQuestionRepository.save(question);
                List<Answer> answers = question.getAnswers();
                answers.forEach(answer -> {
                    if (bloodCount.getLevelType().name().equals(answer.getText())) {
                        question.setCorrectAnswerId(answer.getId());
                    }
                });
                questionList.add(question);
            }
        });
        return bcaQuestionRepository.saveAll(questionList);
    }

    @Override
    public List<MSQuestion> createMSQuestions(Game game, Language language) {
        MSQuestion msQuestion1 = MSQuestion
                .builder()
                .build();

        MSQuestion msQuestion2 = MSQuestion
                .builder()
                .build();

        makeMSAnswersAndQuestion(msQuestion1, msQuestion2, language);

        msQuestionRepository.saveAll(List.of(msQuestion1, msQuestion2));
        List<Answer> answersMSQ1 = msQuestion1.getAnswers();
        String anemiaType = game.getCaseDetails().getAnemiaType();
        answersMSQ1.forEach(answer -> {
            if (answer.getText().equals(anemiaType)) {
                msQuestion1.setCorrectAnswerId(answer.getId());
            } else {
                log.warn("Q. Answer doesnt match with anemia type, correct answer is being set as null");
            }
        });
        List<Answer> answersMSQ2 = msQuestion2.getAnswers();
        double hgbValue = game.getPatient().getBloodCounts().stream()
                .filter(bloodCount -> "HGB".equals(bloodCount.getParameter()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No HGB parameter found"))
                .getValue();

        answersMSQ2.forEach(answer -> {
            String range = getRangeInString(answer.getText());
            assert range != null;
            if (isInRange(range, hgbValue)) {
                msQuestion2.setCorrectAnswerId(answer.getId());
            }
        });

        return msQuestionRepository.saveAll(List.of(msQuestion1, msQuestion2));
    }

    @Override
    public List<MSQuestion> createTrueFalseMSQuestions(Language language) {
        List<MSQuestion> msQuestions = new ArrayList<>();

        msQuestions.addAll(createMSQuestions(erythrocyteQBRepository, language, 3));
        msQuestions.addAll(createMSQuestions(leukocyteQBRepository, language, 3));
        msQuestions.addAll(createMSQuestions(variousQBRepository, language, 4));
        return msQuestions;
    }

    private <Q extends QuestionBase<T>, T extends QuestionTranslationBase> List<MSQuestion> createMSQuestions(JpaRepository<Q, Long> repository,
                                                                                                              Language language,
                                                                                                              int numberOfQuestions) {
        List<MSQuestion> msQuestions = new ArrayList<>();

        Q questionStorage = repository.findAll()
                .stream()
                .filter(q -> q.getLanguage().equals(language))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Question storage for language " + language + " doesn't exist"));
        List<T> randomQuestions = randomlyPickQuestions(questionStorage.getTranslations(), numberOfQuestions);

        randomQuestions.forEach(question -> {
            MSQuestion msQuestion = MSQuestion
                    .builder()
                    .text(question.getText())
                    .build();
            makeTrueFalseAnswers(msQuestion, language);
            msQuestionRepository.save(msQuestion);
            List<Answer> answers = msQuestion.getAnswers();
            answers.forEach(answer -> {
                if (answer.getText().equals(question.getAnswer())) {
                    msQuestion.setCorrectAnswerId(answer.getId());
                }
            });
            msQuestions.add(msQuestion);
        });
        msQuestionRepository.saveAll(msQuestions);
        return msQuestions;
    }

    private void makeTrueFalseAnswers(MSQuestion msQuestion, Language language) {
        Answer answerTrue = Answer.builder().build();
        Answer answerFalse = Answer.builder().build();
        switch (language) {
            case EN -> {
                answerTrue.setText("True");
                answerFalse.setText("False");
            }
            case PL -> {
                answerTrue.setText("Prawda");
                answerFalse.setText("Fałsz");
            }
            default -> throw new LanguageNotSupportedException("Language: %s currently not supported".formatted(language));
        }
        msQuestion.addAnswer(answerTrue);
        msQuestion.addAnswer(answerFalse);
    }

    private boolean isForAssessment(String parameter, String unit) {
        HashMap<String, String> bloodCountMap = new HashMap<>();
        bloodCountMap.put(Parameter.WBC.name(), Unit.GIGALITER.symbol());
        bloodCountMap.put(Parameter.RBC.name(), Unit.TERALITER.symbol());
        bloodCountMap.put(Parameter.HGB.name(), Unit.GRAMS_PER_DECILITER.symbol());
        bloodCountMap.put(Parameter.HCT.name(), Unit.PERCENTAGE.symbol());
        bloodCountMap.put(Parameter.MCV.name(), Unit.FEMTOLITERS.symbol());
        bloodCountMap.put(Parameter.MCH.name(), Unit.PICOGRAMS.symbol());
        bloodCountMap.put(Parameter.MCHC.name(), Unit.GRAMS_PER_DECILITER.symbol());
        bloodCountMap.put(Parameter.PLT.name(), Unit.GIGALITER.symbol());

        return bloodCountMap.containsKey(parameter) && bloodCountMap.containsValue(unit);
    }

    private String getRangeInString(String text) {
        Pattern pattern = Pattern.compile("(?<=HGB\\s).+?(?=\\sg/dl)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    private boolean isInRange(String range, double value) {
        double max, min;
        List<String> ranges = new ArrayList<>(List.of(range.split(" ")));
        if (ranges.contains(">=")) {
            min = Double.parseDouble(ranges.get(1).replace(',', '.'));
            return value >= min;
        }
        if (ranges.contains("<")) {
            max = Double.parseDouble(ranges.get(1).replace(',', '.'));
            return value < max;
        }
        if (ranges.contains("–")) {
            min = Double.parseDouble(ranges.get(0).replace(',', '.'));
            max = Double.parseDouble(ranges.get(2).replace(',', '.'));
            return value > min && value < max;
        }
        return false;
    }

    private <T> List<T> randomlyPickQuestions(List<T> allQuestions, int number) {
        List<T> questions = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            T randomEntity = allQuestions.remove(ThreadLocalRandom.current().nextInt(allQuestions.size()));
            questions.add(randomEntity);
        }
        return questions;
    }

    private void makeMSAnswersAndQuestion(MSQuestion msQuestion1, MSQuestion msQuestion2, Language language) {
        Answer answer1Q1 = Answer
                .builder()
                .build();
        Answer answer2Q1 = Answer
                .builder()
                .build();
        Answer answer3Q1 = Answer
                .builder()
                .build();
        Answer answer4Q1 = Answer
                .builder()
                .build();
        Answer answer5Q1 = Answer
                .builder()
                .build();
        Answer answer6Q1 = Answer
                .builder()
                .build();
        Answer answer7Q1 = Answer
                .builder()
                .build();
        Answer answer8Q1 = Answer
                .builder()
                .build();

        Answer answer1Q2 = Answer
                .builder()
                .build();
        Answer answer2Q2 = Answer
                .builder()
                .build();
        Answer answer3Q2 = Answer
                .builder()
                .build();
        Answer answer4Q2 = Answer
                .builder()
                .build();
        Answer answer5Q2 = Answer
                .builder()
                .build();

        if (language.equals(Language.PL)) {
            msQuestion1.setText("Jak określisz niedokrwistość:");
            answer1Q1.setText("Pacjent nie ma niedokrwistości");
            answer2Q1.setText("Normochromiczna, normocytarna");
            answer3Q1.setText("Normochromiczna, makrocytarna");
            answer4Q1.setText("Normochromiczna,mikrocytarna");
            answer5Q1.setText("Hipochromiczna, mikrocytarna");
            answer6Q1.setText("Hipochromiczna, normocytarna");
            answer7Q1.setText("Hipochromiczna, makrocytarna");
            answer8Q1.setText("Hiperchroniczna, makrocytarna");

            msQuestion2.setText("Stosując klasyfikację WHO, rozpoznasz:");
            answer1Q2.setText("Stopień 0 (norma) – HGB >= 11,0 g/dl");
            answer2Q2.setText("Stopień I (łagodna) – HGB 9,5 – 10,9 g/dl");
            answer3Q2.setText("Stopień II (umiarkowana) – HGB 8,0 – 9,4 g/dl");
            answer4Q2.setText("Stopień III (ciężka) - HGB 6,5 – 7,9 g/dl");
            answer5Q2.setText("Stopień IV (bardzo ciężka) – HGB 4,8 – 6,5 g/dl");
        }
        if (language.equals(Language.EN)) {
            msQuestion1.setText("How you define anaemia:");
            answer1Q1.setText("The patient does not have anaemia");
            answer2Q1.setText("Normochromic, normocytic");
            answer3Q1.setText("Normochromic, macrocytic");
            answer4Q1.setText("Normochromic, microcytic");
            answer5Q1.setText("Hypochromic, microcytic");
            answer6Q1.setText("Hypochromic, normocytic");
            answer7Q1.setText("Hypochromic, macrocytic");
            answer8Q1.setText("Hyperchromic, macrocytic");

            msQuestion2.setText("Using the WHO classification, you recognise:");
            answer1Q2.setText("Grade 0 (normal) – HGB >= 11,0 g/dl");
            answer2Q2.setText("Grade I (mild) – HGB 9,5 – 10,9 g/dl");
            answer3Q2.setText("Grade II (moderate) – HGB 8,0 – 9,4 g/dl");
            answer4Q2.setText("Grade III (severe) - HGB 6,5 – 7,9 g/dl");
            answer5Q2.setText("Grade IV (life-threatening) – HGB 4,8 – 6,5 g/dl");
        }
        msQuestion1.addAnswer(answer1Q1);
        msQuestion1.addAnswer(answer2Q1);
        msQuestion1.addAnswer(answer3Q1);
        msQuestion1.addAnswer(answer4Q1);
        msQuestion1.addAnswer(answer5Q1);
        msQuestion1.addAnswer(answer6Q1);
        msQuestion1.addAnswer(answer7Q1);
        msQuestion1.addAnswer(answer8Q1);

        msQuestion2.addAnswer(answer1Q2);
        msQuestion2.addAnswer(answer2Q2);
        msQuestion2.addAnswer(answer3Q2);
        msQuestion2.addAnswer(answer4Q2);
        msQuestion2.addAnswer(answer5Q2);
    }
}