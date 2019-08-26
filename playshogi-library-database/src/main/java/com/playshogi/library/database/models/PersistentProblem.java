package com.playshogi.library.database.models;

public class PersistentProblem {

    public static enum ProblemType {
        TSUME(1, "Tsume"), OPENING(2, "Opening Problem");

        private final int dbInt;
        private final String description;

        private ProblemType(final int dbInt, final String description) {
            this.dbInt = dbInt;
            this.description = description;
        }

        public int getDbInt() {
            return dbInt;
        }

        public String getDescription() {
            return description;
        }

        public static ProblemType fromDbInt(final int dbInt) {
            switch (dbInt) {
                case 1:
                    return TSUME;
                case 2:
                    return OPENING;
                default:
                    throw new IllegalArgumentException("Unknown problem type: " + dbInt);
            }

        }
    }

    private final int id;
    private final int kifuId;
    private final Integer numMoves;
    private final int elo;
    private final ProblemType pbType;

    public PersistentProblem(int id, int kifuId, Integer numMoves, int elo, ProblemType pbType) {
        this.id = id;
        this.kifuId = kifuId;
        this.numMoves = numMoves;
        this.elo = elo;
        this.pbType = pbType;
    }

    public int getId() {
        return id;
    }

    public int getKifuId() {
        return kifuId;
    }

    public Integer getNumMoves() {
        return numMoves;
    }

    public int getElo() {
        return elo;
    }

    public ProblemType getPbType() {
        return pbType;
    }

    @Override
    public String toString() {
        return "PersistentProblem{" +
                "id=" + id +
                ", kifuId=" + kifuId +
                ", numMoves=" + numMoves +
                ", elo=" + elo +
                ", pbType=" + pbType +
                '}';
    }
}
