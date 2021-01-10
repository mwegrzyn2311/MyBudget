package dao;

import com.google.inject.Inject;
import com.google.inject.Provider;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

public class StatisticDao {
    protected final Provider<EntityManager> entityManager;

    @Inject
    public StatisticDao(Provider<EntityManager> entityManager) {
        this.entityManager = entityManager;
    }

    private Map<String, IncomeOutcome> processIncomeOutcome(List<Object[]> rows) {
        Map<String, IncomeOutcome> results = new HashMap<>();
        for(Object[] row: rows) {
            final String key = (String) row[0];
            if(!results.containsKey(key)) {
                IncomeOutcome temp = new IncomeOutcome();
                results.put(key, temp);
            }
            if((int)row[1] == 0) {
                results.get(key).income = BigDecimal.valueOf((double) row[2]);
            } else {
                results.get(key).outcome = BigDecimal.valueOf((double) row[2]);
            }
        }

        return results;
    }

    public Map<String, IncomeOutcome> getYearlyIncomeOutcome() {
        EntityManager em = entityManager.get();
        List<Object[]> res = em.createNativeQuery("" +
                "select cast(strftime('%Y', Operation.date/1000, 'unixepoch') as varchar), cast(TopCategory.operationType as int), cast(coalesce(sum(Operation.amount), 0) as double) " +
                "from Operation " +
                "join Category on Operation.category_fk = Category.id " +
                "join TopCategory on Category.topCategory_id = TopCategory.id " +
                "group by strftime('%Y', Operation.date/1000, 'unixepoch'), TopCategory.operationType")
                .getResultList();

        return processIncomeOutcome(res);
    }

    public Map<String, IncomeOutcome> getMonthlyIncomeOutcome(String year) {
        EntityManager em = entityManager.get();
        List<Object[]> res = em.createNativeQuery("" +
                "select cast(strftime('%m', Operation.date/1000, 'unixepoch') as varchar), cast(TopCategory.operationType as int), cast(coalesce(sum(Operation.amount), 0) as double) " +
                "from Operation " +
                "join Category on Operation.category_fk = Category.id " +
                "join TopCategory on Category.topCategory_id = TopCategory.id " +
                "where strftime('%Y', Operation.date/1000, 'unixepoch') = :year " +
                "group by strftime('%m', Operation.date/1000, 'unixepoch'), TopCategory.operationType")
                .setParameter("year", year)
                .getResultList();

        return processIncomeOutcome(res);
    }

    public static String mapMonthToName(String month) {
        return switch (month) {
            case "01" -> "Jan";
            case "02" -> "Feb";
            case "03" -> "Mar";
            case "04" -> "Apr";
            case "05" -> "May";
            case "06" -> "Jun";
            case "07" -> "Jul";
            case "08" -> "Aug";
            case "09" -> "Sep";
            case "10" -> "Oct";
            case "11" -> "Nov";
            case "12" -> "Dec";
            default -> "";
        };
    }

    private Map<String, BigDecimal> processPerTopCategoryOutcome(List<Object[]> rows) {
        return rows.stream().collect(Collectors.toMap(
                row -> (String) row[0],
                row -> BigDecimal.valueOf((double) row[1])
        ));
    }

    public Map<String, BigDecimal> getPerTopCategoryOutcomeInYear(String year) {
        EntityManager em = entityManager.get();
        List<Object[]> res = em.createNativeQuery("" +
                "select cast(TopCategory.name as varchar), cast(coalesce(sum(Operation.amount), 0) as double) " +
                "from Operation " +
                "join Category on Operation.category_fk = Category.id " +
                "join TopCategory on Category.topCategory_id = TopCategory.id " +
                "where " +
                "   strftime('%Y', Operation.date/1000, 'unixepoch') = :year " +
                "   and TopCategory.operationType = 1 " +
                "group by TopCategory.name ")
                .setParameter("year", year)
                .getResultList();

        return processPerTopCategoryOutcome(res);
    }

    public Map<String, BigDecimal> getTopCategoryOutcomeInMonth(String year, String month) {
        EntityManager em = entityManager.get();
        List<Object[]> res = em.createNativeQuery("" +
                "select cast(TopCategory.name as varchar), cast(coalesce(sum(Operation.amount), 0) as double) " +
                "from Operation " +
                "join Category on Operation.category_fk = Category.id " +
                "join TopCategory on Category.topCategory_id = TopCategory.id " +
                "where " +
                "   strftime('%Y', Operation.date/1000, 'unixepoch') = :year " +
                "   and strftime('%m', Operation.date/1000, 'unixepoch') = :month " +
                "   and TopCategory.operationType = 1 " +
                "group by TopCategory.name ")
                .setParameter("year", year)
                .setParameter("month", month)
                .getResultList();

        return processPerTopCategoryOutcome(res);
    }

    private List<String> processAvailableHistoryPoints(List<Object> rows) {
        return rows.stream().map(objects -> (String) objects)
                .sorted(
                        Comparator.comparingInt((ToIntFunction<String>) Integer::parseInt).reversed()
                )
                .collect(Collectors.toList());
    }

    public List<String> getAvailableHistoryYears() {
        EntityManager em = entityManager.get();
        List<Object> res = em.createNativeQuery("" +
                "select cast(strftime('%Y', Operation.date/1000, 'unixepoch') as varchar)" +
                "from Operation " +
                "group by strftime('%Y', Operation.date/1000, 'unixepoch')")
                .getResultList();

        return processAvailableHistoryPoints(res);
    }

    public List<String> getAvailableOutcomeHistoryYears() {
        EntityManager em = entityManager.get();
        List<Object> res = em.createNativeQuery("" +
                "select cast(strftime('%Y', Operation.date/1000, 'unixepoch') as varchar)" +
                "from Operation " +
                "join Category on Operation.category_fk = Category.id " +
                "join TopCategory on Category.topCategory_id = TopCategory.id " +
                "where TopCategory.operationType=1 " +
                "group by strftime('%Y', Operation.date/1000, 'unixepoch')")
                .getResultList();

        return processAvailableHistoryPoints(res);
    }

    public List<String> getAvailableOutcomeHistoryMonthsInYear(String year) {
        EntityManager em = entityManager.get();
        List<Object> res = em.createNativeQuery("" +
                "select cast(strftime('%m', Operation.date/1000, 'unixepoch') as varchar)" +
                "from Operation " +
                "join Category on Operation.category_fk = Category.id " +
                "join TopCategory on Category.topCategory_id = TopCategory.id " +
                "where TopCategory.operationType=1 " +
                "and strftime('%Y', Operation.date/1000, 'unixepoch') = :year " +
                "group by strftime('%m', Operation.date/1000, 'unixepoch')")
                .setParameter("year", year)
                .getResultList();

        return processAvailableHistoryPoints(res);
    }

    public static class IncomeOutcome {
        public BigDecimal income = BigDecimal.ZERO;
        public BigDecimal outcome = BigDecimal.ZERO;
    }
}
