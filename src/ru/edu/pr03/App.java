package ru.edu.pr03;

public final class App {
    /** Сумма заявки в рублях (мой вариант 1, модификатор 10). */
    private static final long AMOUNT = 550_000L;

    /** Срочность заявки. */
    private static final boolean URGENT = false;

    /** Уровень риска: LOW / MEDIUM / HIGH. */
    private static final RiskLevel RISK = RiskLevel.LOW;

    /** Есть ли в заявке персональные данные. */
    private static final boolean CONTAINS_PERSONAL_DATA = false;

    private App() {
    }

    public static void main(String[] args) {
        long amount = AMOUNT;
        boolean urgent = URGENT;
        RiskLevel risk = RISK;
        boolean personal = CONTAINS_PERSONAL_DATA;

        if (args.length == 4) {
            try {
                amount = Long.parseLong(args[0]);
                urgent = Boolean.parseBoolean(args[1]);
                risk = RiskLevel.valueOf(args[2].toUpperCase());
                personal = Boolean.parseBoolean(args[3]);
            } catch (IllegalArgumentException e) {
                System.err.println("Не удалось разобрать аргументы: " + e.getMessage());
                System.err.println("Формат: <amount> <urgent> <LOW|MEDIUM|HIGH> <personal>");
                System.exit(1);
            }
        } else if (args.length != 0) {
            System.err.println("Ожидается 4 аргумента: <amount> <urgent> <LOW|MEDIUM|HIGH> <personal>");
            System.exit(1);
        }

        ApprovalRouter router = new ApprovalRouter();

        System.out.println("Входные параметры:");
        System.out.println("\tamount = " + amount);
        System.out.println("\turgent = " + urgent);
        System.out.println("\trisk = " + risk);
        System.out.println("\tcontainsPersonalData = " + personal);
        System.out.println("\tfinanceLimit = " + router.getFinanceLimit());
        System.out.println();

        String rule;
        ApprovalRoute route;
        try {
            route = router.route(amount, urgent, risk, personal);
            rule = explain(amount, urgent, risk, personal, router.getFinanceLimit());
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка входных данных: " + e.getMessage());
            System.out.println("Маршрут не назначен (объект не изменён).");
            return;
        }

        System.out.println("Сработало правило: " + rule);
        System.out.println("Итоговый маршрут: " + route);
    }

    private static String explain(long amount, boolean urgent, RiskLevel risk,
                                  boolean personal, long financeLimit) {
        if (personal) {
            return "ПДн (наивысший приоритет, перекрывает всё)";
        }
        if (risk == RiskLevel.HIGH) {
            return "risk == HIGH (перекрывает сумму и срочность)";
        }
        if (amount >= financeLimit) {
            return "amount >= " + financeLimit + " (перекрывает срочность)";
        }
        if (urgent) {
            return "urgent == true (сумма ниже лимита)";
        }
        return "ни одно специальное правило не сработало (STANDARD)";
    }
}
