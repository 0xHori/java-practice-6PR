package ru.edu.pr03;


public class SelfCheck {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        testTypical();
        testLowerBound();
        testUpperBound();
        testAboveBound();
        testUrgentBelowLimit();
        testUrgentAtLimit();
        testPersonalDataOverridesAll();
        testHighRiskWithoutPersonalData();
        testHighRiskWithHighAmount();
        testNegativeAmount();
        testNullRisk();
        testNegativeFinanceLimit();
        testCustomFinanceLimit();
        testDecisionTable();

        System.out.println();
        System.out.println("Пройдено: " + passed + ", провалено: " + failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void testTypical() {
        check("Типовое значение 100_000", route(100_000, false, RiskLevel.LOW, false) == ApprovalRoute.STANDARD);
    }

    private static void testLowerBound() {
        check("Нижняя граница 549_999", route(549_999, false, RiskLevel.LOW, false) == ApprovalRoute.STANDARD);
    }

    private static void testUpperBound() {
        check("Верхняя граница 550_000", route(550_000, false, RiskLevel.LOW, false) == ApprovalRoute.FINANCE_REVIEW);
    }

    private static void testAboveBound() {
        check("Выше границы 550_001", route(550_001, false, RiskLevel.LOW, false) == ApprovalRoute.FINANCE_REVIEW);
    }

    private static void testUrgentBelowLimit() {
        check("Срочная ниже лимита 549_999", route(549_999, true, RiskLevel.LOW, false) == ApprovalRoute.FAST_TRACK);
    }

    private static void testUrgentAtLimit() {
        check("Срочная на границе 550_000", route(550_000, true, RiskLevel.LOW, false) == ApprovalRoute.FINANCE_REVIEW);
    }

    private static void testPersonalDataOverridesAll() {
        check("ПДн перекрывает HIGH риск, срочность и сумму",
                route(1_000_000, true, RiskLevel.HIGH, true) == ApprovalRoute.SECURITY_REVIEW);
    }

    private static void testHighRiskWithoutPersonalData() {
        check("HIGH риск без ПДн", route(100_000, false, RiskLevel.HIGH, false) == ApprovalRoute.DIRECTOR_REVIEW);
    }

    private static void testHighRiskWithHighAmount() {
        check("HIGH риск с суммой выше лимита", route(700_000, false, RiskLevel.HIGH, false) == ApprovalRoute.DIRECTOR_REVIEW);
    }

    private static void testNegativeAmount() {
        checkThrows("Отрицательная сумма", () -> route(-100, false, RiskLevel.LOW, false));
    }

    private static void testNullRisk() {
        checkThrows("null risk", () -> route(100_000, false, null, false));
    }

    private static void testNegativeFinanceLimit() {
        checkThrows("Отрицательный финансовый лимит", () -> new ApprovalRouter(-1));
    }

    private static void testCustomFinanceLimit() {
        ApprovalRouter router = new ApprovalRouter(500_000L);
        check("Кастомный лимит 500_000", router.route(500_000, false, RiskLevel.LOW, false) == ApprovalRoute.FINANCE_REVIEW);
    }

    private static void testDecisionTable() {
        check("Т1: 10_000, urgent, HIGH, ПДн -> SECURITY_REVIEW",
                route(10_000, true, RiskLevel.HIGH, true) == ApprovalRoute.SECURITY_REVIEW);
        check("Т2: 800_000, не urgent, LOW, ПДн -> SECURITY_REVIEW",
                route(800_000, false, RiskLevel.LOW, true) == ApprovalRoute.SECURITY_REVIEW);
        check("Т3: 100_000, urgent, HIGH, без ПДн -> DIRECTOR_REVIEW",
                route(100_000, true, RiskLevel.HIGH, false) == ApprovalRoute.DIRECTOR_REVIEW);
        check("Т4: 700_000, не urgent, HIGH, без ПДн -> DIRECTOR_REVIEW",
                route(700_000, false, RiskLevel.HIGH, false) == ApprovalRoute.DIRECTOR_REVIEW);
        check("Т5: 550_000, не urgent, LOW, без ПДн -> FINANCE_REVIEW",
                route(550_000, false, RiskLevel.LOW, false) == ApprovalRoute.FINANCE_REVIEW);
        check("Т6: 600_000, urgent, LOW, без ПДн -> FINANCE_REVIEW",
                route(600_000, true, RiskLevel.LOW, false) == ApprovalRoute.FINANCE_REVIEW);
        check("Т7: 549_999, urgent, LOW, без ПДн -> FAST_TRACK",
                route(549_999, true, RiskLevel.LOW, false) == ApprovalRoute.FAST_TRACK);
        check("Т8: 549_999, не urgent, LOW, без ПДн -> STANDARD",
                route(549_999, false, RiskLevel.LOW, false) == ApprovalRoute.STANDARD);
        check("Т9: 150_000, не urgent, MEDIUM, без ПДн -> STANDARD",
                route(150_000, false, RiskLevel.MEDIUM, false) == ApprovalRoute.STANDARD);
        check("Т10: 500_000, urgent, LOW, без ПДн -> FAST_TRACK",
                route(500_000, true, RiskLevel.LOW, false) == ApprovalRoute.FAST_TRACK);
    }

    private static ApprovalRoute route(long amount, boolean urgent, RiskLevel risk, boolean personal) {
        return new ApprovalRouter().route(amount, urgent, risk, personal);
    }

    private static void check(String name, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("[OK]   " + name);
        } else {
            failed++;
            System.out.println("[FAIL] " + name);
        }
    }

    private static void checkThrows(String name, Runnable action) {
        try {
            action.run();
            failed++;
            System.out.println("[FAIL] " + name + " (исключение не брошено)");
        } catch (IllegalArgumentException e) {
            passed++;
            System.out.println("[OK]   " + name + " -> " + e.getMessage());
        }
    }
}
