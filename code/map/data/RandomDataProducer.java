package map.data;

import java.util.Random;

/**
 * Created by computer on 2017/4/20.
 */

public class RandomDataProducer extends Random {
    public char nextUpper() {
        return (char) (nextInt(26) + 'A');
    }

    public char nextLower() {
        return (char) (nextInt(26) + 'a');
    }

    public int nextDigit() {
        return nextInt(10);
    }

    public char nextUpperOrLower() {
        return nextBoolean() ? nextUpper() : nextLower();
    }

    public StringBuilder nextWord() {
        return nextWord(new StringBuilder());
    }

    public StringBuilder nextWord(StringBuilder word) {
        int len = nextInt(3) + 3;
        word.append(nextUpper());
        for (int i = 0; i < len; i++) word.append(nextUpperOrLower());
        return word;
    }



    public StringBuilder nextSentence() {
        StringBuilder sentence = nextWord();
        int len = nextInt(12) + 6;
        for (int i = 0; i < len; i++) nextWord(sentence);
        return sentence;
    }
    public String nextName() {
        StringBuilder name = nextWord();
        int len = nextInt(3);
        for (int i = 0; i < len; i++) nextWord(name);
        return name.toString();
    }
    public String nextGender() {
        return nextBoolean() ? "man" : "woman";
    }

    public int nextAge() {
        return nextInt(5) + 18;
    }

    public int nextHeight() {
        return nextInt(60) + 140;
    }

    public int nextWeight() {
        return nextInt(126) + 74;
    }
    public int nextScore() {
        return nextInt(101);
    }
    public String nextIntroduce() {
        return nextSentence().toString();
    }
}
