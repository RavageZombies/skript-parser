package io.github.syst3ms.skriptparser.parsing;

import io.github.syst3ms.skriptparser.TestRegistration;
import io.github.syst3ms.skriptparser.log.SkriptLogger;
import io.github.syst3ms.skriptparser.pattern.*;
import io.github.syst3ms.skriptparser.types.TypeManager;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PatternParserTest {

    static {
        TestRegistration.register();
    }

    @Test
    public void testParsePattern() {
        SkriptLogger logger = new SkriptLogger();
        PatternParser parser = new PatternParser();
        assertEquals(new TextElement("syntax"), parser.parsePattern("syntax", logger));
        assertEquals(new OptionalGroup(new TextElement("optional")), parser.parsePattern("[optional]", logger));
        assertEquals(
                new OptionalGroup(
                    new CompoundElement(
                            new TextElement("nested "),
                            new OptionalGroup(new TextElement("optional"))
                    )
                ),
                parser.parsePattern("[nested [optional]]", logger)
        );
        assertEquals(
                new ChoiceGroup(
                        new ChoiceElement(new TextElement("single choice"), 0)
                ),
                parser.parsePattern("(single choice)", logger)
        );
        assertEquals(
                new ChoiceGroup(
                        new ChoiceElement(new TextElement("parse mark"), 1)
                ),
                parser.parsePattern("(1:parse mark)", logger)
        );
        assertEquals(
                new ChoiceGroup(
                        new ChoiceElement(new TextElement("first choice"), 0),
                        new ChoiceElement(new TextElement("second choice"), 0)
                ),
                parser.parsePattern("(first choice|second choice)", logger)
        );
        assertEquals(
                new ChoiceGroup(
                        new ChoiceElement(new TextElement("first mark"), 0),
                        new ChoiceElement(new TextElement("second mark"), 1)
                ),
                parser.parsePattern("(first mark|1:second mark)", logger)
        );
        assertEquals(
                new OptionalGroup(
                        new CompoundElement(
                                new TextElement("optional "),
                                new ChoiceGroup(
                                        new ChoiceElement(new TextElement("first choice"), 0),
                                        new ChoiceElement(new TextElement("second choice"), 1)
                                )
                        )
                ),
                parser.parsePattern("[optional (first choice|1:second choice)]", logger)
        );
        assertEquals(
                new RegexGroup(Pattern.compile(".+")),
                parser.parsePattern("<.+>", logger)
        );
        assertEquals(
                new ExpressionElement(
                        Collections.singletonList(TypeManager.getPatternType("number")),
                        ExpressionElement.Acceptance.ALL,
                        false,
                        false
                ),
                parser.parsePattern("%number%", logger)
        );
        assertEquals(
                new ExpressionElement(
                        Arrays.asList(
                                TypeManager.getPatternType("number"),
                                TypeManager.getPatternType("strings")
                        ),
                        ExpressionElement.Acceptance.LITERALS_ONLY,
                        true,
                        false
                ),
                parser.parsePattern("%*number/strings%", logger)
        );
        assertNull(parser.parsePattern("(unclosed", logger));
        assertNull(parser.parsePattern("%unfinished type", logger));
    }

}