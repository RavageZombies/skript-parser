package io.github.syst3ms.skriptparser.pattern;

import io.github.syst3ms.skriptparser.parsing.MatchContext;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

/**
 * A group of multiple choices, represented by {@linkplain ChoiceElement}s
 */
public class ChoiceGroup implements PatternElement {
    private final List<ChoiceElement> choices;

    public ChoiceGroup(List<ChoiceElement> choices) {
        this.choices = choices;
    }

    /**
     * Only used in unit tests
     */
    public ChoiceGroup(ChoiceElement... choices) {
        this(Arrays.asList(choices));
    }

    public List<ChoiceElement> getChoices() {
        return choices;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof ChoiceGroup)) {
            return false;
        } else {
            List<ChoiceElement> choiceElements = ((ChoiceGroup) obj).choices;
            return choices.size() == choiceElements.size() && choices.equals(choiceElements);
        }
    }

    @Override
    public int match(String s, int index, MatchContext context) {
        for (ChoiceElement choice : choices) {
            MatchContext branch = context.branch(choice.getElement());
            int m = choice.getElement().match(s, index, branch);
            if (m != -1) {
                context.merge(branch);
                context.addMark(choice.getParseMark());
                return m;
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner("|", "(", ")");
        for (ChoiceElement choice : choices) {
            if (choice.getParseMark() != 0) {
                joiner.add(choice.getParseMark() + ":" + choice.getElement().toString());
            } else {
                joiner.add(choice.getElement().toString());
            }
        }
        return joiner.toString();
    }
}
