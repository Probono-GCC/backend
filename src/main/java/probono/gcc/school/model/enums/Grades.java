package probono.gcc.school.model.enums;

public enum Grades {
    PLAYGROUP,
    NURSERY,
    LOWER_KG,
    UPPER_KG,
    CLASS1,
    CLASS2,
    CLASS3,
    CLASS4,
    CLASS5,
    CLASS6,
    CLASS7,
    CLASS8,
    CLASS9,
    CLASS10,
    GRADUATED;

    public Grades getNextGrade() {
        // 마지막 학년인 경우 그대로 유지
        if (this.ordinal() == Grades.values().length - 1) {
            return this;
        }
        return Grades.values()[this.ordinal() + 1];
    }
}
