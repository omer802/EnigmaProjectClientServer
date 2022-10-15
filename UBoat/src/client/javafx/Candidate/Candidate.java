package client.javafx.Candidate;

public class Candidate {
    public String getCandidateString() {
        return candidateString;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getCode() {
        return code;
    }

    private String candidateString;
    private String teamName;
    private String code;

    public Candidate(String candidateString, String teamName, String code) {
        this.candidateString = candidateString;
        this.teamName = teamName;
        this.code = code;
    }
}
