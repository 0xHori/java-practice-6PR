package ru.edu.pr03;


public class ApprovalRouter {

    public static final long FINANCE_LIMIT = 550_000L;

    private final long financeLimit;

    public ApprovalRouter() {
        this(FINANCE_LIMIT);
    }

    public ApprovalRouter(long financeLimit) {
        if (financeLimit < 0) {
            throw new IllegalArgumentException("Финансовый лимит не может быть отрицательным: " + financeLimit);
        }
        this.financeLimit = financeLimit;
    }

    public ApprovalRoute route(long amount, boolean urgent, RiskLevel risk, boolean containsPersonalData) {
        validateParameters(amount, risk);

        if (containsPersonalData) {
            return ApprovalRoute.SECURITY_REVIEW;
        }

        if (risk == RiskLevel.HIGH) {
            return ApprovalRoute.DIRECTOR_REVIEW;
        }

        if (isFinanceReviewRequired(amount)) {
            return ApprovalRoute.FINANCE_REVIEW;
        }

        if (urgent) {
            return ApprovalRoute.FAST_TRACK;
        }

        return ApprovalRoute.STANDARD;
    }

    private void validateParameters(long amount, RiskLevel risk) {
        if (amount < 0) {
            throw new IllegalArgumentException("Сумма заявки не может быть отрицательной: " + amount);
        }
        if (risk == null) {
            throw new IllegalArgumentException("Уровень риска обязателен для заполнения (не может быть null)");
        }
    }

    private boolean isFinanceReviewRequired(long amount) {
        return amount >= this.financeLimit;
    }

    public long getFinanceLimit() {
        return this.financeLimit;
    }
}
