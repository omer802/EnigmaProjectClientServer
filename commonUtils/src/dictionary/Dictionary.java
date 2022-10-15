package dictionary;




import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
public class Dictionary {

    // TODO: 9/14/2022 convert from static to public and put every task with this dicitonary 
    // TODO: 10/11/2022 think if its ok to put dictionary and trie in util like this
     public Set<String> getWordDictionary() {
        return wordDictionary;
    }
    private Trie trie;
     public boolean isWordsInDictionary(List<String> words){
        for (String word:words) {
            if(!wordDictionary.contains(word))
                return false;
        }
        return true;
    }


    private Set<String> wordDictionary;
    private String excludeChars;
    private String toCheckWhatHappenddHeree;

    public Dictionary(String words, String excludeChars) {
        this.excludeChars = excludeChars;
        createWordDictionary(words);
        toCheckWhatHappenddHeree ="*************************************************************************************************";
    }

    private void createWordDictionary(String words) {
        String wordToClean = cleanStringFromExcludeChars(words);
        List<String> wordList = Arrays.asList(wordToClean.split(" "));
        this.wordDictionary = wordList.stream().collect(Collectors.toSet());
        this.trie = new Trie(wordDictionary);

    }
    public String cleanStringFromExcludeChars(String words) {
        String wordToClean = new String(words);
        wordToClean = wordToClean.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");
        wordToClean = wordToClean.replaceAll("\\p{C}", "");
        wordToClean = wordToClean.trim();
        wordToClean = wordToClean.toUpperCase();
        //delete all exclude chars from string
        for (int i = 0; i < excludeChars.length(); i++) {
            wordToClean = wordToClean.replace(String.valueOf(excludeChars.charAt(i)), "");
        }
        return wordToClean;
    }

    public boolean isDictionaryContainString(String str){
        String[] wordList = str.split(" ");
        for (String word: wordList) {
            if(!getWordDictionary().contains(word)){
                return false;
            }
        }
         return true;
    }
    public Trie getTrie(){
        return trie;
    }
}
