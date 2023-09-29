package com.pja.bloodcount.model;

import com.pja.bloodcount.model.enums.Language;

import java.util.List;

public interface QuestionBase<T extends QuestionTranslationBase> {
    List<T> getTranslations();
    Language getLanguage();
}